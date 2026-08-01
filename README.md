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
