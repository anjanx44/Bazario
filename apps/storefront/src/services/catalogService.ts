/**
 * Bazario Storefront — Catalog Service
 *
 * Fetches product data from the Spring Boot backend.
 * Falls back to STATIC_CATALOG_DATA on any network/API failure
 * so the UI never crashes or renders a broken layout.
 *
 * Backend base URL is read from NEXT_PUBLIC_API_URL (set in .env.local).
 * Example: NEXT_PUBLIC_API_URL=http://localhost:8080
 */

import { Product, Category } from '@/types';

// ─── Environment ────────────────────────────────────────────────────────────

const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

// ─── TypeScript Interfaces (mirror Java DTOs) ────────────────────────────────

export interface ProductApiResponse {
  id: string;
  name: string;
  slug: string;
  description: string;
  basePrice: number;
  discountPrice: number | null;
  sku: string;
  active: boolean;
  imageUrl?: string;
  images?: string[];
  rating?: number;
  reviewsCount?: number;
  discountPercentage?: number;
  categoryName?: string;
  colors?: { name: string; hex: string }[];
  specifications?: Record<string, string>;
  category: {
    id: string;
    name: string;
    slug: string;
  };
}

export interface PagedProductResponse {
  content: ProductApiResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface ProductListParams {
  page?: number;
  size?: number;
  sort?: string;
  categorySlug?: string;
  search?: string;
  minPrice?: number;
  maxPrice?: number;
}

// ─── Static Fallback Data ────────────────────────────────────────────────────

export const STATIC_CATALOG_DATA: Product[] = [
  {
    id: '1',
    name: 'Titan Mesh Router',
    slug: 'titan-mesh-router',
    basePrice: 250.00,
    discountPrice: 199.00,
    sku: 'TITAN-MESH-001',
    rating: 4.9,
    reviewsCount: 85,
    active: true,
    imageUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuBBS3ZgijprHGamll8xYB8gGBp6knSbfgCstM0hwmdayw89PMpFVGVTRqIw68mhyKJED_rFjwp7LUdwAA7POlTpptIIu_LQBci9r_K3mEM4nTS2d-Vx1-3eCG3NWw-IacMfP60z6H2tiRInLlRoTTC5GY6d35SwRhODuqZBO0iLHcW7OsgjvrUc1EUFVdDVyfnTPPB5I63WWvkXmxU3zHtHX5KW0O11OHH6DXFV0fwfR5M1On1NJog1s_PfuuCtQ5WGnoAGbV5sub4',
    discountPercentage: 15,
  },
  {
    id: '2',
    name: 'Chrono Minimalist Watch',
    slug: 'chrono-minimalist-watch',
    basePrice: 150.00,
    discountPrice: 120.00,
    sku: 'CHRONO-WAT-002',
    rating: 4.7,
    reviewsCount: 42,
    active: true,
    imageUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuCXgyKB3FGb1l_drwImZYe-fWae-tzTftc6WRckjsDVjfQvbCXRu252685h2GSjA_neQem_2ADa3KWsXmNEzSET4gdKVXT-l69cbCIY_3ObyNbXmhLm8lFUWhWWdDjzpu-d7UxtZFq3ERdLDolgHx6WyjSS8-PhFFpZdjJ963RXGbtQQ9Igps4htT0fiZpzsu7XIvcbiaNPcOyWWPQc4bjTfNiKQX25epcCTdksEDeqLY8x6uNOYiSpwIpv2m2u45sYF80vm23hd5o',
    discountPercentage: 20,
  },
  {
    id: '3',
    name: 'AeroSound Elite 5',
    slug: 'aerosound-elite-5',
    basePrice: 350.00,
    discountPrice: 299.00,
    sku: 'AERO-AIR-003',
    rating: 4.8,
    reviewsCount: 120,
    active: true,
    imageUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM',
    discountPercentage: 15,
  },
  {
    id: '4',
    name: 'Pro-Book X1 Case',
    slug: 'pro-book-x1-case',
    basePrice: 45.00,
    sku: 'PRO-CASE-004',
    rating: 4.5,
    reviewsCount: 210,
    active: true,
    imageUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuDr9C7zh1VL59wfqocKE7prp1bZKL_lWw1GBcpTFAG1aAWWLY2hWeE4ABUbdQq2_DNtENuGdU48_rT3NeM-yO9wDj71_8SHJddiX3fC88a3alWzLMISvV3H6Ws7PG1Jy3f1Bp8IysUbO_6aRvWEeILSwL_Hhk5dpfZItjFNrbsiC5u6szNZPIAV3FvLcK5AW8ZkciTj_XO0Xk-jR8JPJAuUOn89JqDXUTBRMfp53ddItJ1M5uBg7GbvnJC1I0YO_7S0FtJimH_B2YI',
  },
];

export const STATIC_PRODUCT_DETAIL_DATA: Record<string, Product> = {
  'aerosound-elite-5': {
    id: '3',
    name: 'AeroSound Elite 5 - Premium Noise-Cancelling Headphones',
    slug: 'aerosound-elite-5',
    description:
      'The AeroSound Elite 5 redefines acoustic excellence. Equipped with our proprietary ANC 4.0 chip, these headphones analyze ambient noise 50,000 times per second to create an immersive bubble of silence, no matter where you are.',
    basePrice: 350.00,
    discountPrice: 299.00,
    discountPercentage: 15,
    sku: 'AS-ELITE5-BLK',
    rating: 4.5,
    reviewsCount: 120,
    active: true,
    imageUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM',
    images: [
      'https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM',
    ],
    colors: [
      { name: 'Matte Black', hex: '#1A1A1A' },
      { name: 'Deep Sea', hex: '#1B2A4E' },
      { name: 'Silver Mist', hex: '#D1D5DB' },
    ],
    categoryName: 'Audio',
    specifications: {
      'Driver Size': '40mm',
      'Frequency Response': '20Hz – 20kHz',
      'Battery Life': '30 hours',
      'Connectivity': 'Bluetooth 5.3',
      'Weight': '250g',
    },
  },
};

// ─── Mapper: API → Frontend Product type ────────────────────────────────────

function mapApiProductToProduct(api: ProductApiResponse): Product {
  return {
    id: api.id,
    name: api.name,
    slug: api.slug,
    description: api.description,
    basePrice: api.basePrice,
    discountPrice: api.discountPrice ?? undefined,
    sku: api.sku,
    active: api.active,
    imageUrl: api.imageUrl,
    images: api.images,
    rating: api.rating,
    reviewsCount: api.reviewsCount,
    discountPercentage: api.discountPercentage,
    categoryName: api.category?.name ?? api.categoryName,
    colors: api.colors,
    specifications: api.specifications,
  };
}

// ─── Service Functions ───────────────────────────────────────────────────────

/**
 * Fetch a paginated list of active products for the storefront catalog.
 * GET /api/v1/products?page=0&size=12&sort=createdAt,desc&categorySlug=...&search=...
 */
export async function fetchProducts(params: ProductListParams = {}): Promise<Product[]> {
  try {
    const {
      page = 0,
      size = 12,
      sort = 'createdAt,desc',
      categorySlug,
      search,
      minPrice,
      maxPrice,
    } = params;

    const query = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    });
    if (categorySlug) query.set('categorySlug', categorySlug);
    if (search) query.set('search', search);
    if (minPrice !== undefined) query.set('minPrice', String(minPrice));
    if (maxPrice !== undefined) query.set('maxPrice', String(maxPrice));

    const res = await fetch(`${API_BASE}/api/v1/products?${query.toString()}`, {
      next: { revalidate: 60 }, // ISR: revalidate every 60 seconds
    });

    if (!res.ok) {
      console.warn(`[catalogService] fetchProducts failed with status ${res.status}. Using static fallback.`);
      return STATIC_CATALOG_DATA;
    }

    const data: PagedProductResponse = await res.json();

    if (!data.content || data.content.length === 0) {
      console.warn('[catalogService] fetchProducts returned empty content. Using static fallback.');
      return STATIC_CATALOG_DATA;
    }

    return data.content.map(mapApiProductToProduct);
  } catch (error) {
    console.error('[catalogService] fetchProducts error:', error);
    return STATIC_CATALOG_DATA;
  }
}

/**
 * Fetch a single product by its URL slug.
 * GET /api/v1/products/{slug}
 */
export async function fetchProductBySlug(slug: string): Promise<Product | null> {
  try {
    const res = await fetch(`${API_BASE}/api/v1/products/${encodeURIComponent(slug)}`, {
      next: { revalidate: 120 },
    });

    if (res.status === 404) {
      return STATIC_PRODUCT_DETAIL_DATA[slug] ?? null;
    }

    if (!res.ok) {
      console.warn(`[catalogService] fetchProductBySlug(${slug}) failed with status ${res.status}. Using static fallback.`);
      return STATIC_PRODUCT_DETAIL_DATA[slug] ?? null;
    }

    const data: ProductApiResponse = await res.json();
    return mapApiProductToProduct(data);
  } catch (error) {
    console.error(`[catalogService] fetchProductBySlug(${slug}) error:`, error);
    return STATIC_PRODUCT_DETAIL_DATA[slug] ?? null;
  }
}

/**
 * Fetch all product categories for navigation/filtering.
 * GET /api/v1/categories
 */
export async function fetchCategories(): Promise<Category[]> {
  const STATIC_CATEGORIES: Category[] = [
    { id: '1', name: 'Audio', slug: 'audio' },
    { id: '2', name: 'Networking', slug: 'networking' },
    { id: '3', name: 'Wearables', slug: 'wearables' },
    { id: '4', name: 'Accessories', slug: 'accessories' },
  ];

  try {
    const res = await fetch(`${API_BASE}/api/v1/categories`, {
      next: { revalidate: 300 },
    });

    if (!res.ok) {
      console.warn(`[catalogService] fetchCategories failed with status ${res.status}. Using static fallback.`);
      return STATIC_CATEGORIES;
    }

    const data: Category[] = await res.json();

    if (!Array.isArray(data) || data.length === 0) {
      return STATIC_CATEGORIES;
    }

    return data;
  } catch (error) {
    console.error('[catalogService] fetchCategories error:', error);
    return STATIC_CATEGORIES;
  }
}
