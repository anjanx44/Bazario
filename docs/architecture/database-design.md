# Database Design — Bazario E-Commerce Platform

> **Database:** PostgreSQL 16  
> **Schema version:** 1.0  
> **Bounded Contexts covered:** Catalog, Orders, Inventory, Customers (Admin/Users)

---

## Table of Contents

1. [Schema Overview](#schema-overview)
2. [DDL Scripts](#ddl-scripts)
   - [Catalog Context](#catalog-context)
   - [Customers Context](#customers-context)
   - [Orders Context](#orders-context)
   - [Inventory Context](#inventory-context)
3. [Data Dictionary](#data-dictionary)
4. [Indexes & Performance Notes](#indexes--performance-notes)
5. [Constraints & Referential Integrity](#constraints--referential-integrity)

---

## Schema Overview

The Bazario PostgreSQL schema is organized around four bounded contexts. Each context owns its tables exclusively — no cross-context foreign keys exist at the database level, preserving the Hexagonal Architecture boundary. Cross-context references (e.g., `orders.customer_id → customers.id`) are **logical references** enforced at the application layer, not via DB-level `FOREIGN KEY` constraints across contexts.

```
┌─────────────────────────────────────────────────────────────────┐
│  CATALOG CONTEXT          │  CUSTOMERS CONTEXT                  │
│  ┌──────────┐             │  ┌───────────┐  ┌──────────────┐   │
│  │categories│◄──┐         │  │ customers │──►│cust_addresses│   │
│  └──────────┘   │         │  └───────────┘  └──────────────┘   │
│  ┌──────────┐   │         ├─────────────────────────────────────┤
│  │ products │───┘         │  ORDERS CONTEXT                     │
│  └──────────┘             │  ┌────────┐  ┌───────────┐         │
│                           │  │ orders │──►│order_items│         │
│  INVENTORY CONTEXT        │  └────────┘  └───────────┘         │
│  ┌──────────┐             │                                     │
│  │inventory │             │  (orders.customer_id → logical ref  │
│  └──────────┘             │   to customers.id)                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## DDL Scripts

### Catalog Context

#### `categories`

```sql
CREATE TABLE categories (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_categories_slug UNIQUE (slug)
);

CREATE INDEX idx_categories_slug ON categories (slug);
```

#### `products`

```sql
CREATE TABLE products (
    id              UUID           NOT NULL DEFAULT gen_random_uuid(),
    category_id     UUID,
    name            VARCHAR(255)   NOT NULL,
    slug            VARCHAR(255)   NOT NULL,
    description     TEXT,
    base_price      NUMERIC(19, 4) NOT NULL,
    discount_price  NUMERIC(19, 4),
    sku             VARCHAR(100)   NOT NULL,
    is_active       BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),

    CONSTRAINT pk_products          PRIMARY KEY (id),
    CONSTRAINT uq_products_slug     UNIQUE (slug),
    CONSTRAINT uq_products_sku      UNIQUE (sku),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE SET NULL,
    CONSTRAINT chk_products_base_price     CHECK (base_price > 0),
    CONSTRAINT chk_products_discount_price CHECK (discount_price IS NULL OR discount_price > 0)
);

CREATE INDEX idx_products_slug        ON products (slug);
CREATE INDEX idx_products_sku         ON products (sku);
CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_products_is_active   ON products (is_active);
```

---

### Customers Context

#### `customers`

```sql
CREATE TABLE customers (
    id            UUID         NOT NULL DEFAULT gen_random_uuid(),
    email         VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100),
    phone         VARCHAR(30),
    is_enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_customers       PRIMARY KEY (id),
    CONSTRAINT uq_customers_email UNIQUE (email)
);

CREATE INDEX idx_customers_email ON customers (email);
```

#### `customer_addresses`

```sql
CREATE TABLE customer_addresses (
    id            UUID         NOT NULL DEFAULT gen_random_uuid(),
    customer_id   UUID         NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city          VARCHAR(100) NOT NULL,
    postal_code   VARCHAR(20)  NOT NULL,
    country       VARCHAR(100) NOT NULL,
    is_default    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_customer_addresses         PRIMARY KEY (id),
    CONSTRAINT fk_customer_addresses_customer FOREIGN KEY (customer_id)
        REFERENCES customers (id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_addresses_customer_id ON customer_addresses (customer_id);
```

---

### Orders Context

#### `orders`

```sql
CREATE TABLE orders (
    id                      UUID           NOT NULL DEFAULT gen_random_uuid(),
    order_number            VARCHAR(50)    NOT NULL,
    customer_id             UUID           NOT NULL,
    total_amount            NUMERIC(19, 4) NOT NULL,
    status                  VARCHAR(30)    NOT NULL,
    shipping_address_json   JSONB          NOT NULL,
    created_at              TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ    NOT NULL DEFAULT now(),

    CONSTRAINT pk_orders             PRIMARY KEY (id),
    CONSTRAINT uq_orders_order_number UNIQUE (order_number),
    CONSTRAINT chk_orders_status     CHECK (status IN (
        'PENDING', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED'
    )),
    CONSTRAINT chk_orders_total_amount CHECK (total_amount >= 0)
);

CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_orders_status      ON orders (status);
CREATE INDEX idx_orders_created_at  ON orders (created_at DESC);
```

#### `order_items`

```sql
CREATE TABLE order_items (
    id           UUID           NOT NULL DEFAULT gen_random_uuid(),
    order_id     UUID           NOT NULL,
    product_id   UUID           NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    price        NUMERIC(19, 4) NOT NULL,
    quantity     INTEGER        NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),

    CONSTRAINT pk_order_items          PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order    FOREIGN KEY (order_id)
        REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT chk_order_items_qty     CHECK (quantity > 0),
    CONSTRAINT chk_order_items_price   CHECK (price >= 0)
);

CREATE INDEX idx_order_items_order_id   ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
```

---

### Inventory Context

#### `inventory`

```sql
CREATE TABLE inventory (
    id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
    product_id          UUID        NOT NULL,
    stock_quantity      INTEGER     NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_inventory              PRIMARY KEY (id),
    CONSTRAINT uq_inventory_product_id   UNIQUE (product_id),
    CONSTRAINT chk_inventory_stock       CHECK (stock_quantity >= 0),
    CONSTRAINT chk_inventory_threshold   CHECK (low_stock_threshold IS NULL OR low_stock_threshold >= 0)
);

CREATE INDEX idx_inventory_product_id ON inventory (product_id);
```

---

## Data Dictionary

### `products`

| Column | Type | Nullable | Description |
|---|---|---|---|
| `id` | `UUID` | NOT NULL | Primary key, auto-generated via `gen_random_uuid()` |
| `category_id` | `UUID` | NULL | FK → `categories.id`; `NULL` if category is deleted |
| `name` | `VARCHAR(255)` | NOT NULL | Display name of the product |
| `slug` | `VARCHAR(255)` | NOT NULL | URL-safe unique identifier (e.g., `wireless-headphones-pro`) |
| `description` | `TEXT` | NULL | Long-form product description; supports Markdown |
| `base_price` | `NUMERIC(19,4)` | NOT NULL | Original retail price; must be > 0 |
| `discount_price` | `NUMERIC(19,4)` | NULL | Sale price; `NULL` means no active discount |
| `sku` | `VARCHAR(100)` | NOT NULL | Stock Keeping Unit; globally unique across catalog |
| `is_active` | `BOOLEAN` | NOT NULL | Soft-delete flag; `FALSE` hides product from storefront |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | Record creation timestamp (UTC) |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | Last modification timestamp (UTC) |

### `orders`

| Column | Type | Nullable | Description |
|---|---|---|---|
| `id` | `UUID` | NOT NULL | Primary key |
| `order_number` | `VARCHAR(50)` | NOT NULL | Human-readable order reference (e.g., `ORD-A1B2C3D4`) |
| `customer_id` | `UUID` | NOT NULL | Logical reference to `customers.id` (no DB FK) |
| `total_amount` | `NUMERIC(19,4)` | NOT NULL | Sum of all `order_items.price × quantity` |
| `status` | `VARCHAR(30)` | NOT NULL | Order lifecycle state; see `OrderStatus` enum |
| `shipping_address_json` | `JSONB` | NOT NULL | Snapshot of shipping address at time of order placement |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | Order placement timestamp (UTC) |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | Last status change timestamp (UTC) |

### `inventory`

| Column | Type | Nullable | Description |
|---|---|---|---|
| `id` | `UUID` | NOT NULL | Primary key |
| `product_id` | `UUID` | NOT NULL | Logical reference to `products.id`; unique (1:1 with product) |
| `stock_quantity` | `INTEGER` | NOT NULL | Current units available; must be ≥ 0 |
| `low_stock_threshold` | `INTEGER` | NULL | Alert threshold; admin is notified when `stock_quantity ≤ threshold` |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | Record creation timestamp (UTC) |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | Last stock adjustment timestamp (UTC) |

### `customers`

| Column | Type | Nullable | Description |
|---|---|---|---|
| `id` | `UUID` | NOT NULL | Primary key |
| `email` | `VARCHAR(320)` | NOT NULL | Unique login identifier; RFC 5321 max length |
| `password_hash` | `VARCHAR(255)` | NOT NULL | BCrypt hash of the customer's password |
| `first_name` | `VARCHAR(100)` | NOT NULL | Customer given name |
| `last_name` | `VARCHAR(100)` | NULL | Customer family name |
| `phone` | `VARCHAR(30)` | NULL | Optional contact phone number |
| `is_enabled` | `BOOLEAN` | NOT NULL | Account active flag; `FALSE` blocks login |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | Account registration timestamp (UTC) |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | Last profile update timestamp (UTC) |

---

## Indexes & Performance Notes

| Index Name | Table | Column(s) | Rationale |
|---|---|---|---|
| `idx_products_slug` | `products` | `slug` | Product detail page lookup by URL slug — high-frequency read |
| `idx_products_sku` | `products` | `sku` | Admin inventory search and duplicate SKU validation |
| `idx_products_is_active` | `products` | `is_active` | Storefront catalog filter (`WHERE is_active = TRUE`) |
| `idx_products_category_id` | `products` | `category_id` | Category-filtered product grid queries |
| `idx_categories_slug` | `categories` | `slug` | Category navigation lookup by slug |
| `idx_orders_status` | `orders` | `status` | Admin order list filtered by status (PENDING, SHIPPED, etc.) |
| `idx_orders_created_at` | `orders` | `created_at DESC` | Default sort for admin order list and dashboard metrics |
| `idx_orders_customer_id` | `orders` | `customer_id` | Customer order history lookup |
| `idx_order_items_order_id` | `order_items` | `order_id` | Eager loading of items for a given order |
| `idx_order_items_product_id` | `order_items` | `product_id` | Revenue analytics per product |
| `idx_inventory_product_id` | `inventory` | `product_id` | Stock lookup during order placement and admin inventory view |
| `idx_customers_email` | `customers` | `email` | Login authentication lookup — critical hot path |
| `idx_customer_addresses_customer_id` | `customer_addresses` | `customer_id` | Shipping address resolution during order creation |

---

## Constraints & Referential Integrity

| Constraint | Type | Rule |
|---|---|---|
| `fk_products_category` | FOREIGN KEY | `products.category_id → categories.id ON DELETE SET NULL` |
| `fk_order_items_order` | FOREIGN KEY | `order_items.order_id → orders.id ON DELETE CASCADE` |
| `fk_customer_addresses_customer` | FOREIGN KEY | `customer_addresses.customer_id → customers.id ON DELETE CASCADE` |
| `uq_products_slug` | UNIQUE | One slug per product across the entire catalog |
| `uq_products_sku` | UNIQUE | One SKU per product; prevents duplicate inventory records |
| `uq_inventory_product_id` | UNIQUE | One inventory record per product (1:1 relationship) |
| `uq_orders_order_number` | UNIQUE | Human-readable order reference must be globally unique |
| `chk_orders_status` | CHECK | Status must be a valid `OrderStatus` enum value |
| `chk_products_base_price` | CHECK | Base price must be strictly positive |
| `chk_inventory_stock` | CHECK | Stock quantity cannot go negative |

> **Cross-context references** (`orders.customer_id`, `order_items.product_id`, `inventory.product_id`) are **not** enforced by DB-level foreign keys. Referential integrity for these is enforced at the application layer inside the respective domain services (`OrderService`, `InventoryUseCase`), preserving bounded context isolation.
