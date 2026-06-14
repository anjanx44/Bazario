# ⚙️ Bazario Backend

The Bazario backend is a **Spring Boot 4** modular monolith built on **Hexagonal Architecture (Ports & Adapters)** and **Domain-Driven Design (DDD)** principles. It exposes a RESTful JSON API consumed by both the Storefront and Admin Portal frontends.

---

## 🏗️ Architecture

The codebase is organized as a set of **bounded contexts**, each following a strict layered structure:

```
com.bazario/
├── auth/           → Customer & admin authentication (JWT)
├── catalog/        → Product & category management
├── customers/      → Customer profiles & addresses
├── inventory/      → Stock tracking & low-stock alerts
├── orders/         → Order lifecycle & dashboard metrics
├── admin/          → Admin Portal API surface (ACL layer)
└── config/         → CORS, Security, OpenAPI global config
```

Each bounded context follows this internal layout:

```
{context}/
├── domain/
│   ├── model/          → Pure domain entities (no framework annotations)
│   └── service/        → Domain service (implements Use Case port)
├── application/
│   ├── model/          → Application-layer value objects / read models
│   └── ports/
│       ├── in/         → Inbound ports (Use Case interfaces)
│       └── out/        → Outbound ports (Repository SPI interfaces)
└── adapters/
    ├── in/web/         → REST Controllers + flat contextual DTOs
    └── out/persistence/→ JPA Entities, Spring Data Repos, Mappers
```

### Dependency Rule

> **Domain → Application → Adapters** (one direction only)

- Domain layer has **zero** Spring/JPA annotations and **zero** adapter imports.
- Ports (Use Case interfaces) reference only domain models and application-layer value objects.
- The `admin` module acts as an **Anti-Corruption Layer (ACL)**: it translates between bounded contexts inside `AdminController` using private `toDto()` helpers — never by importing another module's adapter DTOs.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Mapping | MapStruct 1.6 + Lombok |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Security | Spring Security (Stateless JWT) |

---

## 📡 API Endpoints

### Public Storefront (`/api/v1/`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/auth/login` | Customer login → JWT |
| `POST` | `/api/v1/auth/register` | Customer registration |
| `POST` | `/api/v1/auth/refresh` | Refresh access token |
| `GET` | `/api/v1/auth/me` | Fetch current customer |
| `POST` | `/api/v1/auth/forgot-password` | Request password reset |
| `POST` | `/api/v1/auth/reset-password` | Confirm password reset |
| `GET` | `/api/v1/products` | Paginated product catalog (filterable) |
| `GET` | `/api/v1/products/{slug}` | Single product by slug |
| `GET` | `/api/v1/categories` | All categories |
| `POST` | `/api/v1/orders` | Place a new order |
| `GET` | `/api/v1/orders/{id}` | Get order by ID |

### Admin Portal (`/api/v1/admin/`) — requires `ROLE_ADMIN`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/admin/dashboard/metrics` | KPI metrics |
| `GET` | `/api/v1/admin/orders` | Paginated order list |
| `PATCH` | `/api/v1/admin/orders/{id}/status` | Update order status |
| `GET` | `/api/v1/admin/products` | Paginated product list |
| `POST` | `/api/v1/admin/products` | Create product |
| `GET` | `/api/v1/admin/products/{id}` | Get product by ID |
| `PUT` | `/api/v1/admin/products/{id}` | Update product |
| `DELETE` | `/api/v1/admin/products/{id}` | Deactivate product |
| `GET` | `/api/v1/admin/categories` | All categories (dropdown) |
| `GET` | `/api/v1/admin/inventory/{productId}` | Get inventory snapshot |
| `PATCH` | `/api/v1/admin/inventory/{productId}/stock` | Adjust stock |

---

## 🚀 Running Locally

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose (for PostgreSQL)

### 1. Start Infrastructure

From the **monorepo root**:

```bash
docker-compose up -d
```

This starts PostgreSQL on port `5432`.

### 2. Configure Environment

The app reads from `src/main/resources/application-dev.yml`. Create a `.env` file or set environment variables:

```env
DB_URL=jdbc:postgresql://localhost:5432/bazario
DB_USERNAME=bazario
DB_PASSWORD=bazario
JWT_SECRET=your-256-bit-secret
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The API will be available at **`http://localhost:8080`**.

### 4. Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 📦 Build for Production

```bash
./mvnw clean package -DskipTests
java -jar target/Bazario-0.0.1-SNAPSHOT.jar
```

---

## 🔗 Related

- [Storefront README](../storefront/README.md)
- [Admin README](../admin/README.md)
- [Architecture Overview](../../docs/architecture-overview.md)
- [Database Design](../../docs/database-design.md)
