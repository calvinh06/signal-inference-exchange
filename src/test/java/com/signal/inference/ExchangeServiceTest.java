package com.signal.inference;
import com.signal.inference.contract.*;
import com.signal.inference.engine.*;
import com.signal.inference.provider.MockProvider;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class ExchangeServiceTest {
 private final ExchangeService service=new ExchangeService(List.of(
 new MockProvider(new ProviderDescriptor("cheap","small",Set.of("chat"),1,100_000,200_000)),
 new MockProvider(new ProviderDescriptor("deep","large",Set.of("chat","reasoning"),2,500_000,800_000))));
 @Test void routesToLowestPriorityEligible(){assertEquals("cheap",service.execute("tenant-a",new InferenceRequest("hello","chat",null,null)).providerId());}
 @Test void capabilityFilters(){assertEquals("deep",service.execute("tenant-a",new InferenceRequest("think","reasoning",null,null)).providerId());}
 @Test void providerOverride(){assertEquals("deep",service.execute("tenant-a",new InferenceRequest("hello","chat",null,"deep")).providerId());}
 @Test void rejectsUnknownProvider(){assertThrows(NoSuchElementException.class,()->service.execute("tenant-a",new InferenceRequest("hello","chat",null,"missing")));}
 @Test void rejectsMissingTenant(){assertThrows(IllegalArgumentException.class,()->service.execute("",new InferenceRequest("hello","chat",null,null)));}
}
