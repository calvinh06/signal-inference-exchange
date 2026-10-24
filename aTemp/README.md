# Signal Inference Exchange — runnable reference

Java 21 / Spring Boot 3.5.6 / Maven. Uses deterministic mock providers; no external model API keys.

## Run

```bash
mvn test
mvn spring-boot:run
```

## End-to-end

```bash
curl -s http://localhost:8080/api/v1/inference/providers
curl -s -X POST http://localhost:8080/api/v1/inference \
  -H 'Content-Type: application/json' -H 'X-Demo-Tenant: signal' \
  -d '{"prompt":"Summarize our exchange architecture","capability":"chat"}'
curl -s -X POST http://localhost:8080/api/v1/inference \
  -H 'Content-Type: application/json' -H 'X-Demo-Tenant: signal' \
  -d '{"prompt":"Evaluate routing risks","capability":"reasoning"}'
```

Expected routing: `chat` → `mock-economy`; `reasoning` → `mock-advanced`. Responses contain request ID, tenant, provider, model, mock output, estimated usage, and latency.

## Boundaries

- `contract`: portable request/result/provider metadata; records currently packaged together for easy execution.
- `engine`: provider SPI, eligibility, selection, execution.
- `provider`: two mock provider implementations, injected as Spring beans.
- `api`: REST adapter.

## Explicit limitations / next steps

This is a **reference prototype**, not a production financial or inference service. `X-Demo-Tenant` is untrusted and **must not be used for real tenant isolation**. Integrate with the existing Signal Spring Boot starter to derive tenant from verified principal/token and enforce tenant-specific policy and provider access. Move `contract` and provider SPI into a standalone Java library without Spring dependencies. Add an immutable selection-decision record, pricing/version snapshots, OpenTelemetry spans, usage events and durable metering, streaming, timeouts, circuit breakers, provider health, async JetStream orchestration, persistence, and explicit retry/idempotency semantics. Actual provider API adapters should replace the mocks. Token estimates are intentionally crude and not billable measurements. `maxTokens` is parsed but not enforced by the mocks.

Routing is deterministic by `priority` (lower is preferred); it does not yet optimize costs, latency, or quality. Do not claim automatic failover or billing correctness based on this prototype.
