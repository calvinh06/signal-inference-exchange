package com.signal.inference;

import com.signal.inference.contract.InferenceRequest;
import com.signal.inference.engine.ExchangeService;
import com.signal.inference.provider.InferenceProperties;
import com.signal.inference.provider.ProviderConfiguration;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.Set;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class ConfigurationRoutingTest {
    private final ProviderConfiguration configuration = new ProviderConfiguration();
    private InferenceProperties properties(boolean cheapEnabled, String preferred) {
        return new InferenceProperties(Map.of(
            "cheap", new InferenceProperties.Provider(cheapEnabled, "small", Set.of("chat"), 1, 1L, 1L),
            "advanced", new InferenceProperties.Provider(true, "large", Set.of("chat", "reasoning"), 2, 2L, 2L)
        ), preferred == null ? Map.of() : Map.of("chat", preferred));
    }
    @Test void disabledProviderIsNotRegistered() {
        var props = properties(false, null);
        var service = new ExchangeService(configuration.configuredProviders(props), props);
        assertEquals("advanced", service.execute("tenant-a", new InferenceRequest("hi", "chat", null, null)).providerId());
        assertThrows(NoSuchElementException.class, () -> service.execute("tenant-a", new InferenceRequest("hi", "chat", null, "cheap")));
    }
    @Test void configuredPreferenceOverridesPriority() {
        var props = properties(true, "advanced");
        var service = new ExchangeService(configuration.configuredProviders(props), props);
        assertEquals("advanced", service.execute("tenant-a", new InferenceRequest("hi", "chat", null, null)).providerId());
        assertEquals("cheap", service.execute("tenant-a", new InferenceRequest("hi", "chat", null, "cheap")).providerId());
    }
    @Test void missingPreferredProviderFallsBackToPriority() {
        var props = properties(true, "not-registered");
        var service = new ExchangeService(configuration.configuredProviders(props), props);
        assertEquals("cheap", service.execute("tenant-a", new InferenceRequest("hi", "chat", null, null)).providerId());
    }
}
