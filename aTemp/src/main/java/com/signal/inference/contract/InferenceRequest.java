package com.signal.inference.contract;
import jakarta.validation.constraints.NotBlank;
public record InferenceRequest(@NotBlank String prompt, String capability, Integer maxTokens, String preferredProvider) {
 public String requestedCapability(){return capability == null || capability.isBlank()?"chat":capability;}
 public int tokenLimit(){return maxTokens == null?256:Math.max(1,Math.min(maxTokens,4096));}
}
