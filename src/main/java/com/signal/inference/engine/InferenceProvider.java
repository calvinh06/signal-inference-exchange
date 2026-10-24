package com.signal.inference.engine;
import com.signal.inference.contract.*;
public interface InferenceProvider {
 ProviderDescriptor descriptor();
 InferenceResult.Usage estimateUsage(String prompt,String output);
 String infer(InferenceRequest request);
}
