package com.signal.inference.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "signal.inference")
public record InferenceProperties(Map<String, Provider> providers, Map<String, String> preferredProviders) {
    public InferenceProperties {
        providers = providers == null ? Map.of() : Map.copyOf(providers);
        preferredProviders = preferredProviders == null ? Map.of() : Map.copyOf(preferredProviders);
    }
    public record Provider(Boolean enabled, String model, Set<String> capabilities,
                           Integer priority, Long inputMicroUsdPerMillion,
                           Long outputMicroUsdPerMillion) {
        public boolean isEnabled() { return Boolean.TRUE.equals(enabled); }
    }
}
