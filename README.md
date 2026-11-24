# 🛒 Bazario - Enterprise Multi-Module Monorepo

Welcome to **Bazario**, a modern, high-performance e-commerce ecosystem. This repository is organized as a monorepo containing the backend service, admin panel, and customer storefront.

## 🏗 Project Structure

- **[apps/backend](apps/backend)**: Spring Boot 3.x Modular Monolith Application.
- **[apps/admin](apps/admin)**: Next.js 15 Admin Panel for internal management.
- **[apps/storefront](apps/storefront)**: Next.js 15 Public Customer-Facing Application.
- **[docker](docker)**: Local infrastructure setup using Docker Compose (PostgreSQL, Redis).
- **[docs](docs)**: Technical documentation hub.

## 📄 Documentation Hub

Explore our detailed documentation to get started:

- 🏛 **[Architecture Overview](docs/architecture-overview.md)**
- 🗄 **[Database Design](docs/database-design.md)**
- 🚀 **[Onboarding Guide](docs/onboarding-guide.md)**

## 🛠 Tech Stack

- **Backend**: Java 21, Spring Boot 3.x, Hibernate, Flyway, PostgreSQL.
- **Frontend**: Next.js 15, React, Tailwind CSS.
- **Orchestration**: PNPM Workspaces, Turborepo.
- **Infrastructure**: Docker, GitHub Actions.

## 🚦 Getting Started

Refer to the **[Onboarding Guide](docs/onboarding-guide.md)** for detailed instructions on setting up your local environment.

### Kafka locally (KRaft, no Zookeeper)

Kafka is part of the local stack so the outbox pipeline (`orders.placed`) can be exercised before any domain code publishes events.

```bash
# 1. start postgres + kafka
docker compose up -d

# 2. create the topic once (replication factor 1, dev defaults)
docker exec bazario-kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --if-not-exists \
  --topic orders.placed \
  --partitions 1 --replication-factor 1

# 3. it exists
docker exec bazario-kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 --list

# 4. smoke roundtrip (one terminal produces, another consumes)
docker exec -it bazario-kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server localhost:9092 --topic orders.placed
# type a line, e.g. {"orderId":"1a2b","status":"PLACED"}

docker exec -it bazario-kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 --topic orders.placed --from-beginning
```

Bootstrap servers used by the app: `localhost:9092` (override with `KAFKA_BOOTSTRAP_SERVERS`). Containers on the compose network use `kafka:29092`. Topic auto-creation is disabled on purpose, so `orders.placed` must be created before a producer or consumer touches it.

> **Profiles and metrics.** `application.yml` defaults to the `dev` profile so local runs work without extra flags.
> A real deployment must set the profile explicitly, for example `SPRING_PROFILES_ACTIVE=prod`.
> Non-dev profiles expose only `health,info`; add Prometheus with `MANAGEMENT_EXPOSURE=health,info,prometheus`
> (the `dev` profile adds it automatically). Scraping Prometheus in a non-dev profile requires
> authentication or a separate management port.

## Contributing

Want to contribute? Read **[CONTRIBUTING.md](CONTRIBUTING.md)** — it covers the branching strategy (`main` is always green, short-lived `feature/fix/docs/…` branches merged via PR), branch naming, commit message conventions, and the pull-request requirements.


---

## 📐 System Architecture & Documentation

Enterprise-grade technical documentation for engineering onboarding, architectural review, and API integration. All documents are located under the `docs/` directory.

### Architecture Documents

| # | Document | Path | Description | Primary Audience |
|---|---|---|---|---|
| 1 | **Database Design** | [`docs/architecture/database-design.md`](docs/architecture/database-design.md) | Full PostgreSQL DDL scripts for all 7 tables across 4 bounded contexts (Catalog, Orders, Inventory, Customers). Includes data dictionary, index rationale, and constraint definitions. | Backend Engineers, DBAs |
| 2 | **UML Diagrams** | [`docs/architecture/uml-diagrams.md`](docs/architecture/uml-diagrams.md) | Three Mermaid.js diagrams: Entity Relationship Diagram (ERD), Hexagonal Architecture Class Diagram (Admin/Orders module), and a full Sequence Diagram for Admin Authentication + Order Status Update flow. | All Engineers, Tech Leads |
| 3 | **API Contracts** | [`docs/development/api-contracts.md`](docs/development/api-contracts.md) | Strict REST API specifications for 4 core endpoints: Customer Login, Product Detail (Storefront), Dashboard Metrics, and Paged Product Management (Admin). Includes exact JSON payloads, all HTTP status codes, and a shared error response schema. | Frontend Engineers, QA, Integrators |

### Quick Navigation

| I want to… | Go to… |
|---|---|
| Understand the database schema and table relationships | [`docs/architecture/database-design.md`](docs/architecture/database-design.md) |
| See how Hexagonal Architecture layers connect end-to-end | [`docs/architecture/uml-diagrams.md`](docs/architecture/uml-diagrams.md) |
| Integrate the Next.js frontend with the Java REST API | [`docs/development/api-contracts.md`](docs/development/api-contracts.md) |
| Onboard to the full monorepo and run it locally | [`docs/onboarding-guide.md`](docs/onboarding-guide.md) |
| Read the high-level system architecture overview | [`docs/architecture-overview.md`](docs/architecture-overview.md) |
