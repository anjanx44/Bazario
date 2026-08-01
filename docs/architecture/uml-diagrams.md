# UML Diagrams — Bazario E-Commerce Platform

> **Notation:** All diagrams use [Mermaid.js](https://mermaid.js.org/) syntax and render natively in GitHub, GitLab, and JetBrains IDEs.

---

## Table of Contents

1. [Entity Relationship Diagram (ERD)](#entity-relationship-diagram-erd)
2. [Hexagonal Architecture — Class Diagram (Admin/Orders Module)](#hexagonal-architecture--class-diagram-adminorders-module)
3. [Sequence Diagram — Admin Authentication & Order Status Update](#sequence-diagram--admin-authentication--order-status-update)

---

## Entity Relationship Diagram (ERD)

Shows all PostgreSQL tables, their columns, data types, and relationships across the four bounded contexts.

```mermaid
erDiagram
    categories {
        UUID   id           PK
        string name         "NOT NULL"
        string slug         "NOT NULL, UNIQUE"
        text   description
        timestamptz created_at
        timestamptz updated_at
    }

    products {
        UUID        id             PK
        UUID        category_id    FK
        string      name           "NOT NULL"
        string      slug           "NOT NULL, UNIQUE"
        text        description
        numeric     base_price     "NOT NULL, >0"
        numeric     discount_price
        string      sku            "NOT NULL, UNIQUE"
        boolean     is_active      "DEFAULT TRUE"
        timestamptz created_at
        timestamptz updated_at
    }

    inventory {
        UUID    id                  PK
        UUID    product_id          "NOT NULL, UNIQUE"
        integer stock_quantity      "NOT NULL, >=0"
        integer low_stock_threshold
        timestamptz created_at
        timestamptz updated_at
    }

    customers {
        UUID    id            PK
        string  email         "NOT NULL, UNIQUE"
        string  password_hash "NOT NULL"
        string  first_name    "NOT NULL"
        string  last_name
        string  phone
        boolean is_enabled    "DEFAULT TRUE"
        timestamptz created_at
        timestamptz updated_at
    }

    customer_addresses {
        UUID    id            PK
        UUID    customer_id   FK
        string  address_line1 "NOT NULL"
        string  address_line2
        string  city          "NOT NULL"
        string  postal_code   "NOT NULL"
        string  country       "NOT NULL"
        boolean is_default    "DEFAULT FALSE"
        timestamptz created_at
    }

    orders {
        UUID        id                    PK
        string      order_number          "NOT NULL, UNIQUE"
        UUID        customer_id           "logical ref"
        numeric     total_amount          "NOT NULL, >=0"
        string      status                "PENDING|PAID|SHIPPED|..."
        jsonb       shipping_address_json "NOT NULL"
        timestamptz created_at
        timestamptz updated_at
    }

    order_items {
        UUID        id           PK
        UUID        order_id     FK
        UUID        product_id   "logical ref"
        string      product_name "NOT NULL"
        numeric     price        "NOT NULL, >=0"
        integer     quantity     "NOT NULL, >0"
        timestamptz created_at
    }

    categories    ||--o{ products          : "has many"
    products      ||--o| inventory         : "tracked by"
    customers     ||--o{ customer_addresses : "has many"
    orders        ||--o{ order_items       : "contains"
    customers     ||--o{ orders            : "places (logical)"
    products      ||--o{ order_items       : "referenced by (logical)"
```

---

## Hexagonal Architecture — Class Diagram (Admin/Orders Module)

Illustrates the strict dependency flow from the Web Adapter inward through the Inbound Port, Domain Service, Outbound Port, and finally to the Persistence Adapter. No arrows point outward from the domain.

```mermaid
classDiagram
    direction LR

    %% ── Web Adapter (Infrastructure) ──────────────────────────────────────
    class AdminController {
        -OrderUseCase orderUseCase
        -ProductUseCase productUseCase
        -InventoryUseCase inventoryUseCase
        +getDashboardMetrics() ResponseEntity
        +listOrders(...) ResponseEntity
        +updateOrderStatus(id, request) ResponseEntity
        +listAdminProducts(...) ResponseEntity
        +createAdminProduct(request) ResponseEntity
        +updateAdminProduct(id, request) ResponseEntity
        +deleteAdminProduct(id) ResponseEntity
        -toDto(Order) AdminOrderResponse
        -toDto(DashboardMetrics) DashboardMetricsResponse
        -toDto(Product) AdminProductResponse
    }

    %% ── Inbound Ports (Application) ────────────────────────────────────────
    class OrderUseCase {
        <<interface>>
        +createOrder(Order) Order
        +getOrderById(UUID) Optional~Order~
        +getOrdersByCustomerId(UUID) List~Order~
        +listOrders(Pageable, OrderStatus, String, String, String) Page~Order~
        +updateOrderStatus(UUID, OrderStatus) Optional~Order~
        +getDashboardMetrics() DashboardMetrics
    }

    class ProductUseCase {
        <<interface>>
        +createProduct(Product) Product
        +getProductById(UUID) Optional~Product~
        +findActiveProducts(...) Page~Product~
        +findAllProducts(...) Page~Product~
        +updateProduct(UUID, AdminUpdateProductCommand) Optional~Product~
        +deactivateProduct(UUID) void
        +listAllCategories() List~CategoryView~
    }

    %% ── Domain Services (Domain) ────────────────────────────────────────────
    class OrderService {
        -OrderRepositoryPort orderRepositoryPort
        -CustomerUseCase customerUseCase
        -ProductUseCase productUseCase
        -InventoryUseCase inventoryUseCase
        +createOrder(Order) Order
        +listOrders(...) Page~Order~
        +updateOrderStatus(UUID, OrderStatus) Optional~Order~
        +getDashboardMetrics() DashboardMetrics
    }

    class ProductService {
        -ProductRepositoryPort productRepositoryPort
        -CategoryRepositoryPort categoryRepositoryPort
        +createProduct(Product) Product
        +findAllProducts(...) Page~Product~
        +updateProduct(UUID, AdminUpdateProductCommand) Optional~Product~
        +deactivateProduct(UUID) void
        +listAllCategories() List~CategoryView~
    }

    %% ── Outbound Ports (Application) ────────────────────────────────────────
    class OrderRepositoryPort {
        <<interface>>
        +save(Order) Order
        +findById(UUID) Optional~Order~
        +findByCustomerId(UUID) List~Order~
        +findAllOrders(...) Page~Order~
        +sumTotalRevenue() double
        +countAll() long
    }

    class ProductRepositoryPort {
        <<interface>>
        +save(Product) Product
        +findBySlug(String) Optional~Product~
        +findById(UUID) Optional~Product~
        +findActiveProducts(...) Page~Product~
        +findAllProducts(...) Page~Product~
        +deactivate(UUID) void
    }

    %% ── Persistence Adapters (Infrastructure) ──────────────────────────────
    class PostgresOrderAdapter {
        -SpringDataOrderRepository repo
        -OrderPersistenceMapper mapper
        +save(Order) Order
        +findById(UUID) Optional~Order~
        +findAllOrders(...) Page~Order~
        +sumTotalRevenue() double
        +countAll() long
    }

    class PostgresProductAdapter {
        -SpringDataProductRepository repo
        -ProductPersistenceMapper mapper
        +save(Product) Product
        +findBySlug(String) Optional~Product~
        +findAllProducts(...) Page~Product~
        +deactivate(UUID) void
    }

    %% ── Application-Layer Value Objects ─────────────────────────────────────
    class DashboardMetrics {
        <<record>>
        +double totalRevenue
        +double revenueChange
        +long totalOrders
        +double ordersChange
        +long totalCustomers
        +double customersChange
        +long activeProducts
        +double productsChange
    }

    class AdminUpdateProductCommand {
        <<record>>
        +String name
        +String slug
        +BigDecimal basePrice
        +UUID categoryId
        +Boolean active
        +Integer lowStockThreshold
    }

    %% ── Relationships ────────────────────────────────────────────────────────
    AdminController      --> OrderUseCase         : uses
    AdminController      --> ProductUseCase        : uses
    OrderService         ..|> OrderUseCase         : implements
    ProductService       ..|> ProductUseCase       : implements
    OrderService         --> OrderRepositoryPort   : calls
    ProductService       --> ProductRepositoryPort : calls
    PostgresOrderAdapter ..|> OrderRepositoryPort  : implements
    PostgresProductAdapter ..|> ProductRepositoryPort : implements
    OrderService         ..> DashboardMetrics      : returns
    ProductService       ..> AdminUpdateProductCommand : accepts
```

---

## Sequence Diagram — Admin Authentication & Order Status Update

Shows the complete end-to-end request-response flow: an administrator logs in via the Admin Portal, receives a JWT, and then uses that token to update an order's status through the backend.

```mermaid
sequenceDiagram
    autonumber

    actor Admin as Admin User (Browser)
    participant NextAdmin as Next.js Admin Portal<br/>(apps/admin)
    participant AuthCtrl as AuthController<br/>(POST /api/v1/auth/login)
    participant AuthSvc as AuthService<br/>(Domain Service)
    participant CustomerRepo as CustomerRepositoryPort<br/>(PostgresCustomerAdapter)
    participant DB as PostgreSQL
    participant AdminCtrl as AdminController<br/>(PATCH /api/v1/admin/orders/{id}/status)
    participant SecFilter as Spring Security<br/>(JWT Filter)
    participant OrderSvc as OrderService<br/>(Domain Service)
    participant OrderRepo as OrderRepositoryPort<br/>(PostgresOrderAdapter)

    %% ── Phase 1: Authentication ──────────────────────────────────────────────
    rect rgb(15, 25, 50)
        Note over Admin, DB: Phase 1 — Admin Authentication
        Admin->>NextAdmin: Enter email + password → click "Authenticate Access"
        NextAdmin->>AuthCtrl: POST /api/v1/auth/login<br/>{ "email": "admin@bazario.com", "password": "••••••" }
        AuthCtrl->>AuthSvc: authenticate(email, password)
        AuthSvc->>CustomerRepo: findByEmail(email)
        CustomerRepo->>DB: SELECT * FROM customers WHERE email = ?
        DB-->>CustomerRepo: CustomerJpaEntity row
        CustomerRepo-->>AuthSvc: Customer domain object
        AuthSvc->>AuthSvc: BCrypt.verify(password, passwordHash)
        AuthSvc->>AuthSvc: assertRole(customer, ROLE_ADMIN)
        AuthSvc->>AuthSvc: JwtService.generateToken(customer)
        AuthSvc-->>AuthCtrl: AuthResult { accessToken, refreshToken, expiresIn }
        AuthCtrl-->>NextAdmin: 200 OK<br/>{ "accessToken": "eyJ...", "expiresIn": 3600 }
        NextAdmin->>NextAdmin: localStorage.setItem("admin_token", accessToken)
        NextAdmin-->>Admin: Dashboard rendered ✓
    end

    %% ── Phase 2: Order Status Update ─────────────────────────────────────────
    rect rgb(10, 30, 20)
        Note over Admin, DB: Phase 2 — Update Order Status
        Admin->>NextAdmin: Select order → change status to "SHIPPED" → confirm
        NextAdmin->>AdminCtrl: PATCH /api/v1/admin/orders/{orderId}/status<br/>Authorization: Bearer eyJ...<br/>{ "status": "SHIPPED" }
        AdminCtrl->>SecFilter: Intercept request
        SecFilter->>SecFilter: JwtService.validateToken(token)
        SecFilter->>SecFilter: Extract claims → ROLE_ADMIN confirmed
        SecFilter-->>AdminCtrl: SecurityContext populated ✓
        AdminCtrl->>AdminCtrl: @PreAuthorize("hasRole('ADMIN')") passes
        AdminCtrl->>AdminCtrl: OrderStatus.valueOf("SHIPPED")
        AdminCtrl->>OrderSvc: updateOrderStatus(orderId, SHIPPED)
        OrderSvc->>OrderRepo: findById(orderId)
        OrderRepo->>DB: SELECT * FROM orders WHERE id = ?
        DB-->>OrderRepo: OrderJpaEntity row
        OrderRepo-->>OrderSvc: Order domain object
        OrderSvc->>OrderSvc: order.setStatus(SHIPPED)
        OrderSvc->>OrderSvc: order.setUpdatedAt(now())
        OrderSvc->>OrderRepo: save(order)
        OrderRepo->>DB: UPDATE orders SET status = 'SHIPPED', updated_at = now() WHERE id = ?
        DB-->>OrderRepo: Updated row
        OrderRepo-->>OrderSvc: Updated Order domain object
        OrderSvc-->>AdminCtrl: Optional<Order> (present)
        AdminCtrl->>AdminCtrl: toDto(order) → AdminOrderResponse
        AdminCtrl-->>NextAdmin: 200 OK<br/>{ "id": "...", "status": "SHIPPED", "updatedAt": "..." }
        NextAdmin-->>Admin: Order row updated in UI ✓
    end
```
