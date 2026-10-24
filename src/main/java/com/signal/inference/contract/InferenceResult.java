package com.signal.inference.contract;
import java.util.UUID;
public record InferenceResult(UUID requestId, String tenantId, String providerId, String model, String output, Usage usage, long latencyMs) {
 public record Usage(int inputTokens,int outputTokens,long estimatedMicroUsd){}
}
