package com.signal.inference.provider;

import com.signal.inference.contract.ProviderDescriptor;
import com.signal.inference.engine.InferenceProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@EnableConfigurationProperties(InferenceProperties.class)
public class ProviderConfiguration {
    @Bean
    List<InferenceProvider> configuredProviders(InferenceProperties settings) {
        return settings.providers().entrySet().stream()
            .filter(entry -> entry.getValue().isEnabled())
            .map(entry -> {
                var p = entry.getValue();
                if (p.model() == null || p.model().isBlank() || p.capabilities() == null || p.capabilities().isEmpty()
                    || p.priority() == null || p.inputMicroUsdPerMillion() == null || p.outputMicroUsdPerMillion() == null)
                    throw new IllegalArgumentException("Incomplete inference provider: " + entry.getKey());
                return (InferenceProvider) new MockProvider(new ProviderDescriptor(entry.getKey(), p.model(),
                    p.capabilities(), p.priority(), p.inputMicroUsdPerMillion(), p.outputMicroUsdPerMillion()));
            }).toList();
    }
}
