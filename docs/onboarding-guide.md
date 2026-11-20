# Bazario Onboarding Guide

Welcome! This guide gets a fresh checkout running locally: **PostgreSQL**, **Kafka**, the **Spring Boot backend**, and the two **Next.js frontends**.

> ⏱️ Budget ~30 minutes on the first run (dependency downloads dominate).
> Verified on Windows 11 with Docker Desktop 29.9, JDK 21, Node 24, npm 11.

---

## 1. Prerequisites

| Tool | Version | Verify |
|---|---|---|
| Git | any recent | `git --version` |
| Docker Desktop | with Compose v2 | `docker compose version` |
| **JDK 21** | required by the backend (`<java.version>21</java.version>`) | `java -version` |
| Node.js | **20.9+** (Next.js 16 requirement) | `node -v` |
| npm | 10+ (see note below) | `npm -v` |

> 💡 **Windows / IntelliJ users:** if `java -version` reports 17, point `JAVA_HOME` at IntelliJ's bundled JDK 21:
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\jbr"
> ```
> The Maven wrapper picks up `JAVA_HOME`. It must be set in every new terminal (or permanently in your user environment variables).

---

## 2. Clone

```bash
git clone git@github.com:anjanx44/Bazario.git
cd Bazario
```

---

## 3. Local configuration

The backend loads `.env` through `DotEnvConfig` (an `ApplicationContextInitializer`). It reads from the **current working directory**, so the file belongs in `apps/backend/`:

```bash
cp .env.example apps/backend/.env
```

This matters because `application-dev.yml` defaults to `postgres / postgres`, while the compose stack creates `dev_user / dev_password`. Without the `.env` file, the app starts but fails on the first database call.

---

## 4. Start infrastructure

From the repository root:

```bash
docker compose up -d postgres kafka
docker compose up kafka-init          # creates orders.placed; exits when done
docker compose ps                     # postgres + kafka should both be (healthy)
```

| Service | Container | Host port | Notes |
|---|---|---|---|
| `postgres` | `bazario-postgres` | `5432` | DB `bazario_dev_db` |
| `kafka` | `bazario-kafka` | `9092` | Single-node **KRaft** (no ZooKeeper) |
| `kafka-init` | `bazario-kafka-init` | – | One-shot: `orders.placed`, 3 partitions, RF 1 |

Stop without losing data: `docker compose down`. Wipe local data: `docker compose down -v`.

> ⚠️ If another PostgreSQL container already publishes `5432`, `docker compose up` fails with a port-binding error. Stop that container first or change the published port in `docker-compose.yaml` (and `DB_PORT` in `.env`).

---

## 5. Backend

```bash
cd apps/backend
./mvnw test                 # must be green before you start changing things
./mvnw spring-boot:run      # Windows: .\mvnw.cmd spring-boot:run
```

| URL | Purpose |
|---|---|
| <http://localhost:8080/swagger-ui.html> | Swagger UI — interact with every endpoint |
| <http://localhost:8080/api-docs> | OpenAPI JSON |
| <http://localhost:8080/actuator/health> | Only if Actuator is enabled in your branch |

Flyway applies the versioned migrations from `db/migration/{catalog,customers,inventory,orders,auth}` on startup. JPA `ddl-auto` is `none` — never create tables by hand.

---

## 6. Frontends

The apps have their own `package-lock.json` files. `pnpm-workspace.yaml` and `turbo.json` are currently **empty placeholders**, so use npm per app (the README's "pnpm workspace + Turborepo" description is aspirational, not wired up yet).

```bash
# terminal 1 — customer storefront
cd apps/storefront
npm ci
npm run dev -- -p 3000

# terminal 2 — admin portal
cd apps/admin
npm ci
npm run dev -- -p 3001
```

The backend's CORS config allows exactly `http://localhost:3000` and `http://localhost:3001`. Both apps read the API base URL from `NEXT_PUBLIC_API_URL`, defaulting to `http://localhost:8080`.

---

## 7. What's where

```
Bazario/
├── docker-compose.yaml            # PostgreSQL + Kafka + topic init
├── .env.example                   # copy to apps/backend/.env
├── apps/
│   ├── backend/                   # Spring Boot 4 modular monolith
│   │   └── src/main/java/com/bazario/
│   │       ├── auth/ catalog/ customers/ inventory/ orders/   # bounded contexts
│   │       ├── admin/             # anti-corruption layer for the admin UI
│   │       └── config/            # security, CORS, OpenAPI, dotenv
│   ├── storefront/                # Next.js public app  (port 3000)
│   └── admin/                     # Next.js admin app    (port 3001)
└── docs/
    ├── architecture/database-design.md
    ├── architecture/uml-diagrams.md
    └── development/api-contracts.md
```

Each bounded context follows **domain → application → adapters** (Hexagonal Architecture). Domain classes carry no Spring/JPA annotations. See [`docs/architecture/uml-diagrams.md`](architecture/uml-diagrams.md) and the [backend README](../apps/backend/README.md).

---

## 8. Definition of "my setup works"

- [ ] `docker compose ps` → `bazario-postgres` and `bazario-kafka` are `(healthy)`
- [ ] `kafka-topics.sh --bootstrap-server localhost:9092 --list` → `orders.placed`
- [ ] `cd apps/backend && ./mvnw test` → all tests pass
- [ ] Swagger UI loads and `GET /api/v1/products` returns a response
- [ ] Both frontends render against the running API (no CORS errors in devtools)

---

## 9. Troubleshooting

| Symptom | Fix |
|---|---|
| `release version 21 not supported` | Set `JAVA_HOME` to a JDK 21 (see §1) |
| App starts, DB calls fail with auth error | `apps/backend/.env` missing or stale — recreate from `.env.example` |
| `bind: address already in use` on 5432 / 9092 | Another local service owns the port; stop it or remap in `docker-compose.yaml` |
| `Topic orders.placed not found` | `docker compose up kafka-init`; never rely on auto-creation (it is disabled) |
| Kafka container unhealthy | `docker compose logs kafka` — check `CLUSTER_ID` and that no ZooKeeper services are configured |
| Frontend shows CORS error | Use ports 3000/3001, or add your port to `cors.allowed-origins` |
| A page under `docs/` looks empty | Some root-level docs are 0-byte placeholders; use the real files under `docs/architecture/` and `docs/development/` |

---

## 10. Before your first PR

Read [`CONTRIBUTING.md`](../CONTRIBUTING.md): `main` is always green, work on short-lived `feature/`, `fix/` or `docs/` branches, use Conventional Commits, and open a PR for review. GitHub issues are the single source of truth for work items.

## Related docs

- [Backend README](../apps/backend/README.md)
- [Database design](architecture/database-design.md)
- [UML diagrams](architecture/uml-diagrams.md)
- [API contracts](development/api-contracts.md)
