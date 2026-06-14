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
    imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBBS3ZgijprHGamll8xYB8gGBp6knSbfgCstM0hwmdayw89PMpFVGVTRqIw68mhyKJED_rFjwp7LUdwAA7POlTpptIIu_LQBci9r_K3mEM4nTS2d-Vx1-3eCG3NWw-IacMfP60z6H2tiRInLlRoTTC5GY6d35SwRhODuqZBO0iLHcW7OsgjvrUc1EUFVdDVyfnTPPB5I63WWvkXmxU3zHtHX5KW0O11OHH6DXFV0fwfR5M1On1NJog1s_PfuuCtQ5WGnoAGbV5sub4',
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
    imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCXgyKB3FGb1l_drwImZYe-fWae-tzTftc6WRckjsDVjfQvbCXRu252685h2GSjA_neQem_2ADa3KWsXmNEzSET4gdKVXT-l69cbCIY_3ObyNbXmhLm8lFUWhWWdDjzpu-d7UxtZFq3ERdLDolgHx6WyjSS8-PhFFpZdjJ963RXGbtQQ9Igps4htT0fiZpzsu7XIvcbiaNPcOyWWPQc4bjTfNiKQX25epcCTdksEDeqLY8x6uNOYiSpwIpv2m2u45sYF80vm23hd5o',
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
    imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM',
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
    imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDr9C7zh1VL59wfqocKE7prp1bZKL_lWw1GBcpTFAG1aAWWLY2hWeE4ABUbdQq2_DNtENuGdU48_rT3NeM-yO9wDj71_8SHJddiX3fC88a3alWzLMISvV3H6Ws7PG1Jy3f1Bp8IysUbO_6aRvWEeILSwL_Hhk5dpfZItjFNrbsiC5u6szNZPIAV3FvLcK5AW8ZkciTj_XO0Xk-jR8JPJAuUOn89JqDXUTBRMfp53ddItJ1M5uBg7GbvnJC1I0YO_7S0FtJimH_B2YI'
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
