import React from 'react';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { HeroSection } from '@/components/ui/HeroSection';
import { FlashSaleStrip } from '@/components/ui/FlashSaleStrip';
import { ProductGrid } from '@/components/catalog/ProductGrid';
import { TrustBadges } from '@/components/ui/TrustBadges';
import { Product } from '@/types';

// Mock data based on the HTML provided
const featuredProducts: Product[] = [
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
    imageUrl: '/images/products/titan-mesh-router.svg',
    discountPercentage: 15
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
    imageUrl: '/images/products/chrono-minimalist-watch.svg',
    discountPercentage: 20
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
    imageUrl: '/images/products/aerosound-elite-5.svg',
    discountPercentage: 15
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
    imageUrl: '/images/products/pro-book-x1-case.svg'
  }
];

export default function LandingPage() {
  return (
    <div className="flex flex-col min-h-screen bg-white text-gray-900 antialiased">
      <Header />
      
      <main className="flex-grow">
        <HeroSection />
        
        <FlashSaleStrip />
        
        <ProductGrid 
          title="Featured Collections" 
          products={featuredProducts} 
          seeMoreHref="#" 
        />
        
        <TrustBadges />
      </main>
      
      <Footer />
    </div>
  );
}
