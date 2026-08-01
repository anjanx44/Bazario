# 🛍️ Bazario Storefront

The Bazario public-facing customer storefront — a **Next.js 15 App Router** application with a premium Cosmic UI design system. It connects to the [Bazario Spring Boot backend](../backend/README.md) for live data and falls back to static mock data when the API is unavailable.

---

## ✨ Features

- **Product Catalog** — Paginated, filterable product grid with category navigation
- **Product Detail Pages** — Dynamic `[slug]` routes with full product information
- **Customer Authentication** — Email/password login with password visibility toggle; Google OAuth integration point (NextAuth.js ready)
- **Zero-Crash UI** — Every API call is wrapped in a `try/catch` with `STATIC_*_DATA` fallbacks; the UI never breaks when the backend is offline
- **Premium Cosmic UI** — Deep midnight base (`#090d16`), glassmorphic cards, animated ambient glow spheres, Tailwind CSS

---

## 🗂️ Project Structure

```
src/
├── app/
│   ├── page.tsx                  → Homepage / Product catalog grid
│   ├── products/
│   │   └── [slug]/page.tsx       → Product detail page
│   └── auth/
│       └── login/page.tsx        → Customer login page
├── services/
│   ├── catalogService.ts         → fetchProducts(), fetchProductBySlug(), fetchCategories()
│   └── authService.ts            → loginWithCredentials(), registerCustomer(), refreshAccessToken(), etc.
└── app/globals.css               → Global Tailwind base styles
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Next.js 15 (App Router) |
| Language | TypeScript (strict) |
| Styling | Tailwind CSS v4 |
| Icons | lucide-react |
| HTTP | Native `fetch` (with fallback pattern) |
| Auth | NextAuth.js (integration-ready) |

---

## 📡 Backend Integration

All data-fetching lives in `src/services/`. Each function follows the **Defensive Hydration Pattern**:

```ts
// Example: catalogService.ts
export async function fetchProducts(params): Promise<PagedProductResponse> {
  try {
    const res = await fetch(`${API_BASE}/api/v1/products?...`);
    if (!res.ok || data.content.length === 0) return STATIC_CATALOG_DATA;
    return data;
  } catch {
    return STATIC_CATALOG_DATA; // backend offline → static fallback
  }
}
```

The backend base URL defaults to `http://localhost:8080` and is configured via:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

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

Create `apps/storefront/.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=your-nextauth-secret
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### 3. Start the Dev Server

```bash
# From monorepo root (recommended — uses Turborepo)
pnpm dev

# Or directly from this directory
pnpm --filter storefront dev
```

The storefront will be available at **`http://localhost:3000`**.

---

## 📄 Pages

| Route | Description |
|---|---|
| `/` | Homepage with product catalog grid |
| `/products/[slug]` | Product detail page |
| `/auth/login` | Customer login (email/password + Google OAuth) |

---

## 🔗 Related

- [Backend README](../backend/README.md)
- [Admin README](../admin/README.md)
- [Architecture Overview](../../docs/architecture-overview.md)
