package com.signal.inference.provider;
import com.signal.inference.contract.*;
import com.signal.inference.engine.InferenceProvider;
public class MockProvider implements InferenceProvider {
 private final ProviderDescriptor descriptor;
 public MockProvider(ProviderDescriptor descriptor){this.descriptor=descriptor;}
 public ProviderDescriptor descriptor(){return descriptor;}
 public String infer(InferenceRequest request){return "["+descriptor.model()+"] Mock answer to: "+request.prompt();}
 public InferenceResult.Usage estimateUsage(String prompt,String output){
  int in=Math.max(1,(prompt.length()+3)/4),out=Math.max(1,(output.length()+3)/4);
  long cost=(in*descriptor.inputMicroUsdPerMillion()+out*descriptor.outputMicroUsdPerMillion())/1_000_000;
  return new InferenceResult.Usage(in,out,cost);
 }
}
