# Bazario — GitHub Issue Drafts (10)

> Repo: anjanx44/Bazario · Format: professional (Context → Current state → Acceptance criteria → Skill payoff)
> Created via `gh issue create` after `gh auth login`. Numbering follows Apply-List syllabus order.

---

## ISSUE 1.1 — JWT CORE

**Title:** `feat(auth): JWT access + refresh tokens (core)`

**Body:**
## Context
`AuthController` currently returns hardcoded `"TODO_GENERATE_JWT_ACCESS_TOKEN"` strings. Login appears to work but tokens are fake, and `SecurityConfig` has no JWT filter.

## Current state
- `auth/adapters/in/web/AuthController.java:84-85,145-146` — stub tokens
- `config/SecurityConfig.java:29,95` — JWT filter TODO
- `auth/domain/service/CustomerUserDetailsService.java` — exists, usable

## Acceptance criteria
- [ ] Add `jjwt` dependency (pom.xml)
- [ ] `JwtService`: generate access (15m) + refresh (7d) tokens, validate, extract subject
- [ ] `JwtAuthenticationFilter` wired before `UsernamePasswordAuthenticationFilter`
- [ ] `/login` returns real tokens; `/refresh` rotates; `/me` requires valid token
- [ ] Happy-path + invalid-token tests green

**Interview skill:** Security (OAuth2/JWT) — follow-up: #1.2

---

## ISSUE 1.2 — PASSWORD RESET + GUARDS

**Title:** `feat(auth): password reset flow + enforce role guards`

**Body:**
## Context
Follows #1.1 (real JWT exists). Forgot/reset endpoints are TODO stubs and `@PreAuthorize` on admin routes is unenforceable without real tokens.

## Acceptance criteria
- [ ] Forgot-password: secure random token + expiry persisted; reset validates + updates password
- [ ] `@PreAuthorize("hasRole('ADMIN')")` enforced + tested (401/403 cases)
- [ ] Negative tests: expired token, reused token, missing role

**Interview skill:** Security (OAuth2/JWT)

---

## ISSUE 2 — ACTUATOR

**Title:** `feat(ops): add Spring Actuator + Micrometer Prometheus metrics`

**Body:**
## Context
No observability: no health endpoint, no metrics. Production-readiness and interview questions on monitoring can't be demonstrated.

## Acceptance criteria
- [ ] `spring-boot-starter-actuator` + `micrometer-registry-prometheus` deps
- [ ] `/actuator/health`, `/actuator/prometheus` exposed (dev profile)
- [ ] Custom counter: `orders.created.total`; timer: product lookup latency
- [ ] `docker-compose.yaml`: Prometheus service scraping the app

**Interview skill:** Observability 🟡→✅

---

## ISSUE 3 — TESTS

**Title:** `test: add Testcontainers-based @DataJpaTest coverage (catalog, inventory)`

**Body:**
## Context
Single test file (`BazarioApplicationTests` context-load only). Zero repository/service coverage — the biggest portfolio red flag.

## Acceptance criteria
- [ ] `testcontainers` + `spring-boot-testcontainers` deps (Postgres module)
- [ ] `@DataJpaTest` for catalog + inventory repositories (CRUD + custom queries)
- [ ] 1 service-level test (InventoryService stock decrement incl. low-stock alert)
- [ ] `mvn verify` green in CI

**Interview skill:** Testing 🟡→✅

---

## ISSUE 4 — N+1

**Title:** `perf(jpa): eliminate N+1 queries in catalog/inventory reads`

**Body:**
## Context
Likely N+1 on product→category / inventory→product joins (to be confirmed with Hibernate `show-sql` + query count). Classic senior-interview topic — fix with evidence.

## Acceptance criteria
- [ ] Reproduce: log query count for `GET /products` with N rows (before number recorded here)
- [ ] Fix via `@EntityGraph` / fetch join where appropriate; document lazy-vs-eager choice per relation
- [ ] After number recorded; add regression test asserting query count
- [ ] Short note in PR description explaining the tradeoff

**Interview skill:** JPA depth ❌→🟡

---

## ISSUE 5 — IDEMPOTENCY

**Title:** `feat(orders): idempotent order placement + RFC-7807 error responses`

**Body:**
## Context
Double-submit creates duplicate orders. Payment-adjacent APIs are expected to accept `Idempotency-Key`. Error responses are ad-hoc, not standardized.

## Acceptance criteria
- [ ] `Idempotency-Key` header on `POST /orders`; same key + same payload → same order (no duplicate)
- [ ] Same key + different payload → `422 Unprocessable Entity`
- [ ] Global `@ControllerAdvice` returns RFC-7807 `problem+json` for all errors
- [ ] Integration test: double-POST proof

**Interview skill:** REST API design ❌→🟡

---

## ISSUE 6 — REDIS

**Title:** `feat(catalog): Redis cache-aside for product details + category tree`

**Body:**
## Context
Every product view hits Postgres. Catalog reads are the highest-traffic path — textbook cache-aside case (already envisioned in `6.tech that can we use next.md`).

## Acceptance criteria
- [ ] `spring-data-redis` dep + Redis service in `docker-compose.yaml`
- [ ] `@Cacheable` product-details + category tree (TTL 10m); `@CacheEvict` on PUT/DELETE/PATCH-stock
- [ ] Stampede guard (singleflight/lock) documented
- [ ] Before/after p95 latency noted in PR (even local numbers)

**Interview skill:** Redis/Caching ❌→🟡

---

## ISSUE 7.1 — KAFKA SETUP

**Title:** `chore(kafka): local Kafka (KRaft) + hello producer/consumer`

**Body:**
## Context
No broker exists. Before touching order flow, prove local Kafka works.

## Acceptance criteria
- [ ] `spring-kafka` dep + Kafka (KRaft, no Zookeeper) service in compose
- [ ] Smoke test: send + receive on `bazario.smoke` topic (committed)
- [ ] README: how to start/stop broker locally

**Interview skill:** Kafka — follow-up: #7.2

---

## ISSUE 7.2 — OUTBOX RELAY

**Title:** `feat(orders): outbox table + relay publishing OrderPlaced`

**Body:**
## Context
`OrderService` uses in-memory Spring events — lost on crash, single-instance only. Outbox table + relay gives durable publishing without distributed transactions.

## Acceptance criteria
- [ ] `outbox` table (Flyway migration) written in the same order-creation transaction
- [ ] Relay (scheduler) publishes to `orders.placed` topic, marks sent
- [ ] Document choice in PR: outbox-relay vs transactional send (why)
- [ ] Test: kill mid-flow → event still delivered on restart (or documented manual proof)

**Interview skill:** Kafka + outbox — follow-up: #7.3

---

## ISSUE 7.3 — CONSUMERS

**Title:** `feat(orders): notification + inventory consumers (groups, offsets)`

**Body:**
## Context
Follows #7.2 (events flow durably). Now consume: notification side-effect + inventory decrement, with correct group/offset semantics.

## Acceptance criteria
- [ ] Notification consumer (log/email stub) on its own consumer group
- [ ] Inventory consumer decrements stock (idempotent handler — duplicate delivery safe)
- [ ] Document in PR: consumer-group + offset-commit behavior, rebalance effect
- [ ] Test: restart consumer → resumes from committed offset, no double-decrement

**Interview skill:** Kafka + event-driven — follow-up: #10 (write-up)

---

## ISSUE 8 — K8S

**Title:** `chore(infra): Kubernetes manifests + kind verification`

**Body:**
## Context
Backend `Dockerfile` already exists and is solid (multi-stage, non-root `bazario` user, G1GC — verified 2026-09-22). What's missing is the K8s path — required for the DevOps half of the syllabus. No `k8s/` manifests exist.

## Acceptance criteria
- [ ] `k8s/` manifests: Deployment, Service, Ingress, ConfigMap/Secret (no secrets in git)
- [ ] Verified on `kind`/`k3d`: `kubectl get pods` all Ready + smoke test of 3 endpoints
- [ ] README section: how to run locally (compose) vs kind

**Interview skill:** Docker/K8s ❌→🟡

---

## ISSUE 9 — LLD

**Title:** `refactor: apply Strategy (pricing/discount) + Factory (order creation) patterns`

**Body:**
## Context
Hexagonal structure exists but domain logic is procedural. Applying named patterns gives LLD interview stories with real diffs.

## Acceptance criteria
- [ ] `PricingStrategy` (standard vs discount vs coupon) with 2+ implementations + test
- [ ] `OrderFactory` encapsulating snapshot + validation rules for order creation
- [ ] No behavior change: full test suite green
- [ ] PR explains why these patterns (not just what)

**Interview skill:** LLD/Design Patterns ❌→🟡

---

## ISSUE 10 — OUTBOX DOC

**Title:** `docs: replace in-memory domain events with outbox pattern write-up`

**Body:**
## Context
After Issue #7, document the before/after: Spring `ApplicationEventPublisher` vs outbox-relay-to-Kafka — reliability, ordering, failure modes. This write-up doubles as HLD interview prep material.

## Acceptance criteria
- [ ] `docs/outbox-pattern.md`: diagram (ASCII), failure table, ordering guarantees
- [ ] Link from backend README
- [ ] Consistent with actual Issue #7 implementation (no fiction)

**Interview skill:** Microservices patterns ❌→🟡 + HLD material

---

## ISSUE 11 — DUPLICATE ROUTES

**Title:** `fix(api): remove duplicated product/inventory routes between Admin and public controllers`

**Body:**
## Context
`AdminController` re-implements public routes: `GET/POST /products`, `PUT/DELETE /products/{id}`, `GET /products/{productId}`, `GET /categories`, `GET+PATCH /inventory/{productId}/stock` — all overlapping `ProductController` and `InventoryController`. Two owners for the same resource = behavior drift (already visible: admin product queries differ from public ones).

## Acceptance criteria
- [ ] Decide ownership: public controllers own resources; admin reuses use-case ports (no duplicate logic)
- [ ] Remove duplicated handler methods; admin endpoints delegate to the same use cases
- [ ] `./mvnw test` green; smoke-test both `/api/v1/products` and `/api/v1/admin/products` paths

**Interview skill:** REST API design ❌→🟡

---

## ISSUE 12 — DUAL REGISTRATION

**Title:** `fix(api): unify registration — /auth/register vs /customers/register`

**Body:**
## Context
Two registration paths exist: `POST /api/v1/auth/register` and `POST /api/v1/customers/register`. Unclear which creates the user + customer record, risk of duplicate accounts.

## Acceptance criteria
- [ ] One path: `POST /api/v1/auth/register` creates user AND customer (single transaction)
- [ ] `/customers/register` removed or documented as alias (prefer removal)
- [ ] Integration test: register → login → `/me` end-to-end

**Interview skill:** REST API design ❌→🟡

---

## ISSUE 13 — PAGINATION

**Title:** `feat(api): paginate all list endpoints (orders, addresses, admin lists)`

**Body:**
## Context
`GET /api/v1/orders`, `GET /customer/{customerId}`, `GET /{id}/addresses`, admin `GET /orders` return unbounded lists. Catalog already has the `Pageable` pattern (`SpringDataProductRepository.findActiveProducts`) — follow it everywhere.

## Acceptance criteria
- [ ] `page`/`size`/`sort` params on all list endpoints above (default size 20, max 100)
- [ ] Consistent envelope: `{content, page, size, totalElements, totalPages}`
- [ ] Admin `GET /orders` paged (currently unbounded — biggest risk)

**Interview skill:** REST API design ❌→🟡

---

## ISSUE 14 — VALIDATION

**Title:** `fix(api): add bean validation to customers/orders/inventory DTOs`

**Body:**
## Context
Verified 2026-09-22: `CustomerDtos`, `OrderDtos`, `InventoryDtos` have **0 constraints** and their controllers never use `@Valid`. Catalog (12 constraints) and auth are validated — extend the same standard. Unvalidated order/customer input reaches the domain layer unchecked.

## Acceptance criteria
- [ ] `@NotBlank/@NotNull/@Email/@Positive/@Size` on all three DTO modules (mirror catalog standard)
- [ ] `@Valid` on every `@RequestBody` in Customer/Order/Inventory controllers
- [ ] `GlobalExceptionHandler` returns 400 `problem+json` on `MethodArgumentNotValidException` (extends Issue #5)
- [ ] Negative-case tests per module

**Interview skill:** REST API design ❌→🟡
