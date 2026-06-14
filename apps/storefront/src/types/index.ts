import { ReactNode } from 'react';

export interface Product {
  id: string;
  name: string;
  slug: string;
  description?: string;
  basePrice: number;
  discountPrice?: number;
  sku: string;
  rating: number;
  reviewsCount: number;
  active: boolean;
  imageUrl: string;
  images?: string[];
  discountPercentage?: number;
  categoryName?: string;
  colors?: { name: string; hex: string }[];
  specifications?: Record<string, string>;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
  description?: string;
}
