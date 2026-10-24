package com.signal.inference.contract;
import java.util.Set;
public record ProviderDescriptor(String id,String model,Set<String> capabilities,int priority,long inputMicroUsdPerMillion,long outputMicroUsdPerMillion){}
