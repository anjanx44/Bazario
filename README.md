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
