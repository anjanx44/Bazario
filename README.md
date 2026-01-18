# 🛒 Bazario - Modern E-Commerce Ecosystem

**Bazario** is a high-performance, scalable e-commerce backend built with **Spring Boot 3**, focusing on clean architecture, data integrity, and developer experience. This project serves as a collaborative learning platform for building industry-standard enterprise applications.

---

## 🚀 Tech Stack

- **Backend:** Java 21, Spring Boot 3.3+
- **Database:** PostgreSQL
- **Migration:** Flyway (Database Versioning)
- **Documentation:** Swagger UI / OpenAPI 3
- **Build Tool:** Maven

---

## 🏛 Architecture Overview

Bazario follows a **Domain-Driven Design (DDD)** approach with a classic 3-layer architecture to ensure concerns are separated and the codebase remains maintainable.

- **API Layer:** REST Controllers handling HTTP requests and DTO mapping.
- **Service Layer:** Core business logic, transactional boundaries, and domain rules.
- **Persistence Layer:** Spring Data JPA with Flyway for schema migrations.

---

## 🛠 Features (Upcoming)

- [ ] **Database Infrastructure:** Versioned migrations using Flyway.
- [ ] **Schema Design:** UUID-based primary keys and high-precision monetary handling (Decimal).
- [ ] **Product Management:** Full CRUD for Categories and Products.
- [ ] **Inventory System:** Real-time stock tracking and low-stock alerts.
- [ ] **Auth & Security:** Role-based access control (Admin/Customer).

---

## 🚦 Getting Started

### Prerequisites
- JDK 21
- PostgreSQL 15+
- Maven 3.9+

### Installation

1. **Fork the repository** to your own GitHub account.
2. **Clone your fork:**
   ```bash
   git clone [https://github.com/your-username/Bazario.git](https://github.com/your-username/Bazario.git)
   cd Bazario
