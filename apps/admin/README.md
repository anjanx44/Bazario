# 🛡️ Bazario Admin Portal

The Bazario internal control center — a **Next.js 15 App Router** application with an Enterprise Cyberpunk UI design system. It provides authorized administrators with full visibility and control over products, orders, inventory, and business metrics. All API calls are secured with `Authorization: Bearer` JWT headers and fall back to static mock data when the backend is unavailable.

---

## ✨ Features

- **Dashboard** — Live KPI metrics (revenue, orders, customers, active products) with period-over-period change indicators
- **Order Management** — Paginated, filterable order list with inline status updates
- **Product & Inventory Management** — Full CRUD for products with stock tracking and low-stock alerts
- **Secure API Client** — Central `adminFetch<T>()` wrapper auto-injects JWT, handles `401` redirects, and returns typed `AdminFetchResult<T>` discriminated unions
- **Zero-Crash UI** — Every service call has a `STATIC_*_DATA` fallback; the dashboard never renders broken layouts
- **Enterprise Cyberpunk UI** — Deep midnight base (`#070a13`), 24px radial micro-grid, glassmorphic cards, cyan/indigo ambient glow spheres, Tailwind CSS

---

## 🗂️ Project Structure

```
src/
├── app/
│   ├── admin/
│   │   ├── login/page.tsx         → Admin login (no social auth — credentials only)
│   │   ├── dashboard/page.tsx     → KPI metrics + recent orders table
│   │   └── products/page.tsx      → Product catalog & inventory management
│   └── layout.tsx / globals.css
├── services/
│   ├── apiClient.ts               → adminFetch<T>(): central JWT-aware fetch wrapper
│   ├── dashboardService.ts        → fetchDashboardMetrics(), fetchRecentOrders(), updateOrderStatus()
│   └── adminProductService.ts     → Full product CRUD + fetchAdminCategories(), inventory adjust
└── types/
    └── index.ts                   → Shared TypeScript interfaces mirroring all Java Admin DTOs
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Next.js 15 (App Router) |
| Language | TypeScript (strict) |
| Styling | Tailwind CSS v4 |
| Icons | lucide-react |
| HTTP | Native `fetch` via `adminFetch<T>()` wrapper |
| Auth | JWT Bearer token (NextAuth.js integration-ready) |

---

## 📡 Backend Integration

All data-fetching lives in `src/services/`. The central `apiClient.ts` handles authentication and error normalization:

```ts
// apiClient.ts
export async function adminFetch<T>(
  endpoint: string,
  options?: RequestInit
): Promise<AdminFetchResult<T>> {
  const token = localStorage.getItem('admin_token');
  const res = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      ...options?.headers,
    },
  });
  if (res.status === 401) { /* redirect to /admin/login */ }
  // ...
}
```

Each service function wraps `adminFetch` with a typed static fallback:

```ts
// dashboardService.ts
export async function fetchDashboardMetrics(): Promise<DashboardMetrics> {
  try {
    const result = await adminFetch<DashboardMetrics>('/api/v1/admin/dashboard/metrics');
    if (!result.ok) return STATIC_DASHBOARD_METRICS;
    return result.data;
  } catch {
    return STATIC_DASHBOARD_METRICS;
  }
}
```

The backend base URL is configured via:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

---

## 🔐 Authentication

The admin login page (`/admin/login`) accepts **email + password only** — no social sign-in. On successful authentication the backend returns a JWT which is stored and automatically injected into every subsequent admin API request by `adminFetch`.

---

## 🚀 Running Locally

### Prerequisites

- Node.js 20+
- pnpm 9+
- [Backend running](../backend/README.md) on `http://localhost:8080` *(optional — static fallbacks work without it)*

### 1. Install Dependencies

From the **monorepo root**:

```bash
pnpm install
```

### 2. Configure Environment

Create `apps/admin/.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXTAUTH_URL=http://localhost:3001
NEXTAUTH_SECRET=your-nextauth-secret
```

### 3. Start the Dev Server

```bash
# From monorepo root (recommended — uses Turborepo)
pnpm dev

# Or directly from this directory
pnpm --filter admin dev
```

The admin portal will be available at **`http://localhost:3001`**.

---

## 📄 Pages

| Route | Description |
|---|---|
| `/admin/login` | Admin login — credentials only, no social auth |
| `/admin/dashboard` | KPI metrics cards + recent orders table |
| `/admin/products` | Product catalog with create / edit / deactivate + inventory adjust |

---

## 🔗 Related

- [Backend README](../backend/README.md)
- [Storefront README](../storefront/README.md)
- [Architecture Overview](../../docs/architecture-overview.md)
