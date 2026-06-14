# API Contracts — Bazario E-Commerce Platform

> **Base URL:** `http://localhost:8080` (development) / `https://api.bazario.com` (production)  
> **API Version prefix:** `/api/v1`  
> **Content-Type:** `application/json` on all requests and responses  
> **Authentication:** Bearer JWT token in `Authorization` header for protected routes

---

## Table of Contents

1. [HTTP Status Code Reference](#http-status-code-reference)
2. [Storefront API](#storefront-api)
   - [POST /api/v1/auth/login — Customer Login](#post-apiv1authlogin--customer-login)
   - [GET /api/v1/products/{slug} — Product Detail](#get-apiv1productsslug--product-detail)
3. [Admin Portal API](#admin-portal-api)
   - [GET /api/v1/admin/dashboard/metrics — Dashboard KPIs](#get-apiv1admindashboardmetrics--dashboard-kpis)
   - [GET /api/v1/admin/products — Paged Product Management](#get-apiv1adminproducts--paged-product-management)
4. [Shared Error Response Schema](#shared-error-response-schema)

---

## HTTP Status Code Reference

| Code | Meaning | When Used |
|---|---|---|
| `200 OK` | Success | Successful GET, PATCH, PUT |
| `201 Created` | Resource created | Successful POST that creates a new resource |
| `204 No Content` | Success, no body | Successful DELETE |
| `400 Bad Request` | Validation failure | Missing required fields, constraint violations, malformed JSON |
| `401 Unauthorized` | Authentication required | Missing or expired JWT token |
| `403 Forbidden` | Authorization failure | Valid JWT but insufficient role (e.g., non-admin accessing `/api/v1/admin/**`) |
| `404 Not Found` | Resource not found | Product slug/ID, order ID, customer ID does not exist |
| `409 Conflict` | Duplicate resource | SKU or slug already exists on product creation |
| `500 Internal Server Error` | Unexpected server error | Unhandled exception; database connectivity failure |

---

## Storefront API

### POST /api/v1/auth/login — Customer Login

Authenticates a registered customer using email and password. Returns a JWT access token and refresh token on success.

**Authentication required:** No

---

#### Request

```
POST /api/v1/auth/login
Content-Type: application/json
```

**Request Body:**

```json
{
  "email": "jane.doe@example.com",
  "password": "MySecureP@ssw0rd"
}
```

| Field | Type | Required | Constraints | Description |
|---|---|---|---|---|
| `email` | `string` | ✅ Yes | Valid email format, max 320 chars | Customer's registered email address |
| `password` | `string` | ✅ Yes | Min 8 characters | Customer's plaintext password (transmitted over HTTPS only) |

---

#### Responses

**200 OK — Authentication successful**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1NWU4ZjQ3Yy0xMjM0LTQ1NjctODkwYS1hYmNkZWYxMjM0NTYiLCJlbWFpbCI6ImphbmUuZG9lQGV4YW1wbGUuY29tIiwicm9sZSI6IlJPTEVfQ1VTVE9NRVIiLCJpYXQiOjE3MTg5ODAwMDAsImV4cCI6MTcxODk4MzYwMH0.signature",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4gc2FtcGxl",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "customer": {
    "id": "55e8f47c-1234-4567-890a-abcdef123456",
    "email": "jane.doe@example.com",
    "firstName": "Jane",
    "lastName": "Doe"
  }
}
```

| Field | Type | Description |
|---|---|---|
| `accessToken` | `string` | Signed JWT; include as `Authorization: Bearer <token>` on subsequent requests |
| `refreshToken` | `string` | Opaque token used to obtain a new access token via `POST /api/v1/auth/refresh` |
| `tokenType` | `string` | Always `"Bearer"` |
| `expiresIn` | `integer` | Access token lifetime in seconds (default: 3600) |
| `customer.id` | `string (UUID)` | Customer's unique identifier |
| `customer.email` | `string` | Authenticated email address |
| `customer.firstName` | `string` | Customer's given name |
| `customer.lastName` | `string` | Customer's family name |

---

**400 Bad Request — Validation failure**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    { "field": "email", "message": "must be a well-formed email address" },
    { "field": "password", "message": "must not be blank" }
  ],
  "timestamp": "2026-06-14T17:12:00Z",
  "path": "/api/v1/auth/login"
}
```

**401 Unauthorized — Invalid credentials**

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "timestamp": "2026-06-14T17:12:00Z",
  "path": "/api/v1/auth/login"
}
```

**500 Internal Server Error**

```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later.",
  "timestamp": "2026-06-14T17:12:00Z",
  "path": "/api/v1/auth/login"
}
```

---

### GET /api/v1/products/{slug} — Product Detail

Fetches the full details of a single active product by its URL slug. Used by the Storefront product detail page (`/products/[slug]`).

**Authentication required:** No

---

#### Request

```
GET /api/v1/products/{slug}
```

| Path Parameter | Type | Required | Description |
|---|---|---|---|
| `slug` | `string` | ✅ Yes | URL-safe product identifier (e.g., `wireless-headphones-pro`) |

**Example:**

```
GET /api/v1/products/wireless-headphones-pro
```

---

#### Responses

**200 OK — Product found**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Wireless Headphones Pro",
  "slug": "wireless-headphones-pro",
  "description": "Premium noise-cancelling wireless headphones with 40-hour battery life and spatial audio support.",
  "basePrice": 299.99,
  "discountPrice": 249.99,
  "sku": "WHP-PRO-001",
  "active": true,
  "category": {
    "id": "c1d2e3f4-a5b6-7890-cdef-123456789abc",
    "name": "Audio",
    "slug": "audio"
  },
  "createdAt": "2026-01-15T10:30:00Z"
}
```

| Field | Type | Nullable | Description |
|---|---|---|---|
| `id` | `string (UUID)` | No | Product unique identifier |
| `name` | `string` | No | Display name |
| `slug` | `string` | No | URL-safe identifier |
| `description` | `string` | Yes | Long-form product description |
| `basePrice` | `number` | No | Original retail price |
| `discountPrice` | `number` | Yes | Active sale price; `null` if no discount |
| `sku` | `string` | No | Stock Keeping Unit |
| `active` | `boolean` | No | Always `true` for storefront-visible products |
| `category.id` | `string (UUID)` | Yes | Category identifier; `null` if uncategorized |
| `category.name` | `string` | Yes | Category display name |
| `category.slug` | `string` | Yes | Category URL slug |
| `createdAt` | `string (ISO-8601)` | No | Product creation timestamp (UTC) |

---

**404 Not Found — Slug does not exist or product is inactive**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found: wireless-headphones-pro",
  "timestamp": "2026-06-14T17:12:00Z",
  "path": "/api/v1/products/wireless-headphones-pro"
}
```

**500 Internal Server Error** — See [Shared Error Response Schema](#shared-error-response-schema).

---

## Admin Portal API

> All Admin Portal endpoints require a valid JWT with `ROLE_ADMIN` claim.  
> Include the token as: `Authorization: Bearer <accessToken>`

---

### GET /api/v1/admin/dashboard/metrics — Dashboard KPIs

Returns aggregated Key Performance Indicator metrics for the Admin Dashboard header cards. Includes total revenue, order count, customer count, and active product count — each with a period-over-period percentage change.

**Authentication required:** Yes — `ROLE_ADMIN`

---

#### Request

```
GET /api/v1/admin/dashboard/metrics
Authorization: Bearer eyJhbGci...
```

No query parameters or request body.

---

#### Responses

**200 OK — Metrics returned**

```json
{
  "totalRevenue": 128450.75,
  "revenueChange": 12.5,
  "totalOrders": 3842,
  "ordersChange": 8.2,
  "totalCustomers": 1205,
  "customersChange": 5.1,
  "activeProducts": 94,
  "productsChange": -2.3
}
```

| Field | Type | Description |
|---|---|---|
| `totalRevenue` | `number` | Cumulative sum of all completed order totals (USD) |
| `revenueChange` | `number` | Percentage change vs. previous period (positive = growth) |
| `totalOrders` | `integer` | Total number of orders placed across all statuses |
| `ordersChange` | `number` | Percentage change in order count vs. previous period |
| `totalCustomers` | `integer` | Total registered customer accounts |
| `customersChange` | `number` | Percentage change in customer registrations vs. previous period |
| `activeProducts` | `integer` | Number of products with `is_active = true` |
| `productsChange` | `number` | Percentage change in active product count vs. previous period |

---

**401 Unauthorized — Missing or expired token**

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or has expired",
  "timestamp": "2026-06-14T17:12:00Z",
  "path": "/api/v1/admin/dashboard/metrics"
}
```

**403 Forbidden — Valid token but insufficient role**

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: ROLE_ADMIN required",
  "timestamp": "2026-06-14T17:12:00Z",
  "path": "/api/v1/admin/dashboard/metrics"
}
```

**500 Internal Server Error** — See [Shared Error Response Schema](#shared-error-response-schema).

---

### GET /api/v1/admin/products — Paged Product Management

Returns a paginated list of **all** products (active and inactive) for the Admin Product Catalog page. Supports filtering by search term, category, active status, and low-stock flag.

**Authentication required:** Yes — `ROLE_ADMIN`

---

#### Request

```
GET /api/v1/admin/products
Authorization: Bearer eyJhbGci...
```

**Query Parameters:**

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `page` | `integer` | No | `0` | Zero-based page index |
| `size` | `integer` | No | `20` | Number of items per page (max recommended: 100) |
| `sort` | `string` | No | `createdAt,desc` | Sort field and direction, e.g. `name,asc` or `basePrice,desc` |
| `search` | `string` | No | — | Partial match against product `name` or `sku` |
| `categoryId` | `string (UUID)` | No | — | Filter by exact category UUID |
| `active` | `boolean` | No | — | `true` = active only; `false` = inactive only; omit = all |
| `lowStock` | `boolean` | No | — | `true` = only products where `stock_quantity ≤ low_stock_threshold` |

**Example request:**

```
GET /api/v1/admin/products?page=0&size=20&sort=createdAt,desc&search=headphones&active=true
Authorization: Bearer eyJhbGci...
```

---

#### Responses

**200 OK — Products returned**

```json
{
  "content": [
    {
      "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "name": "Wireless Headphones Pro",
      "slug": "wireless-headphones-pro",
      "description": "Premium noise-cancelling wireless headphones.",
      "basePrice": 299.99,
      "discountPrice": 249.99,
      "sku": "WHP-PRO-001",
      "active": true,
      "imageUrl": null,
      "stockQuantity": 42,
      "lowStockThreshold": 10,
      "category": {
        "id": "c1d2e3f4-a5b6-7890-cdef-123456789abc",
        "name": "Audio",
        "slug": "audio"
      },
      "createdAt": "2026-01-15T10:30:00Z",
      "updatedAt": "2026-06-10T08:15:00Z"
    }
  ],
  "totalElements": 94,
  "totalPages": 5,
  "size": 20,
  "number": 0
}
```

**Response Body — Top-level fields:**

| Field | Type | Description |
|---|---|---|
| `content` | `array` | Array of `AdminProductResponse` objects for the current page |
| `totalElements` | `integer` | Total number of products matching the applied filters |
| `totalPages` | `integer` | Total number of pages at the requested `size` |
| `size` | `integer` | Number of items per page (echoes the request parameter) |
| `number` | `integer` | Current zero-based page index (echoes the request parameter) |

**`AdminProductResponse` object fields:**

| Field | Type | Nullable | Description |
|---|---|---|---|
| `id` | `string (UUID)` | No | Product unique identifier |
| `name` | `string` | No | Display name |
| `slug` | `string` | No | URL-safe identifier |
| `description` | `string` | Yes | Long-form description |
| `basePrice` | `number` | No | Original retail price |
| `discountPrice` | `number` | Yes | Active sale price; `null` if no discount |
| `sku` | `string` | No | Stock Keeping Unit |
| `active` | `boolean` | No | `true` = visible on storefront |
| `imageUrl` | `string` | Yes | Product image URL; `null` until media service is wired |
| `stockQuantity` | `integer` | Yes | Current units in stock; `null` if inventory not initialized |
| `lowStockThreshold` | `integer` | Yes | Alert threshold; `null` if not configured |
| `category.id` | `string (UUID)` | Yes | Category identifier |
| `category.name` | `string` | Yes | Category display name |
| `category.slug` | `string` | Yes | Category URL slug |
| `createdAt` | `string (ISO-8601)` | No | Product creation timestamp (UTC) |
| `updatedAt` | `string (ISO-8601)` | No | Last modification timestamp (UTC) |

---

**400 Bad Request — Invalid query parameter**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    { "field": "categoryId", "message": "must be a valid UUID" }
  ],
  "timestamp": "2026-06-14T17:12:00Z",
  "path": "/api/v1/admin/products"
}
```

**401 Unauthorized** — See [Dashboard Metrics 401 example](#401-unauthorized--missing-or-expired-token-1).

**403 Forbidden** — See [Dashboard Metrics 403 example](#403-forbidden--valid-token-but-insufficient-role-1).

**500 Internal Server Error** — See [Shared Error Response Schema](#shared-error-response-schema).

---

## Shared Error Response Schema

All error responses across the Bazario API conform to the following structure:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Human-readable description of the error",
  "details": [
    {
      "field": "fieldName",
      "message": "Field-level validation message (only present on 400 responses)"
    }
  ],
  "timestamp": "2026-06-14T17:12:00Z",
  "path": "/api/v1/resource/identifier"
}
```

| Field | Type | Nullable | Description |
|---|---|---|---|
| `status` | `integer` | No | HTTP status code mirrored in the body |
| `error` | `string` | No | Standard HTTP reason phrase for the status code |
| `message` | `string` | No | Human-readable summary of the error cause |
| `details` | `array` | Yes | Present only on `400` responses; contains per-field validation errors |
| `details[].field` | `string` | No | Name of the request field that failed validation |
| `details[].message` | `string` | No | Specific validation constraint message |
| `timestamp` | `string (ISO-8601)` | No | Server-side timestamp when the error occurred (UTC) |
| `path` | `string` | No | The request URI that triggered the error |
