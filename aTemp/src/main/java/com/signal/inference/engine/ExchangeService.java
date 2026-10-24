package com.signal.inference.engine;

import com.signal.inference.contract.*;
import com.signal.inference.provider.InferenceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ExchangeService {
    private final List<InferenceProvider> providers;
    private final Map<String, String> preferredProviders;

    public ExchangeService(List<InferenceProvider> providers) { this(providers, Map.of()); }

    @Autowired
    public ExchangeService(@Qualifier("configuredProviders") List<InferenceProvider> providers, InferenceProperties settings) {
        this(providers, settings.preferredProviders());
    }

    private ExchangeService(List<InferenceProvider> providers, Map<String, String> preferences) {
        this.providers = List.copyOf(providers);
        this.preferredProviders = Map.copyOf(preferences);
    }

    public List<ProviderDescriptor> catalog() {
        return providers.stream().map(InferenceProvider::descriptor).toList();
    }

    public InferenceResult execute(String tenantId, InferenceRequest request) {
        if (tenantId == null || tenantId.isBlank())
            throw new IllegalArgumentException("Verified tenant identity is required");
        String capability = request.requestedCapability();
        String explicit = request.preferredProvider();
        String configured = preferredProviders.get(capability);
        List<InferenceProvider> eligible = providers.stream()
            .filter(p -> p.descriptor().capabilities().contains(capability))
            .filter(p -> explicit == null || explicit.isBlank() || p.descriptor().id().equals(explicit))
            .sorted(Comparator.comparingInt((InferenceProvider p) ->
                (explicit == null || explicit.isBlank()) && p.descriptor().id().equals(configured) ? 0 : 1)
                .thenComparingInt(p -> p.descriptor().priority()))
            .toList();
        if (eligible.isEmpty()) throw new NoSuchElementException("No eligible inference provider");
        InferenceProvider selected = eligible.getFirst();
        UUID id = UUID.randomUUID();
        long start = System.nanoTime();
        String output = selected.infer(request);
        var usage = selected.estimateUsage(request.prompt(), output);
        return new InferenceResult(id, tenantId, selected.descriptor().id(),
            selected.descriptor().model(), output, usage, (System.nanoTime() - start) / 1_000_000);
    }
}
