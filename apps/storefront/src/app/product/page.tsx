import React from 'react';
import Link from 'next/link';
import { ChevronRight } from 'lucide-react';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { ProductGallery } from '@/components/catalog/ProductGallery';
import { ProductInfo } from '@/components/catalog/ProductInfo';
import { ProductTabs } from '@/components/catalog/ProductTabs';
import FrequentlyBoughtTogether from '@/components/catalog/FrequentlyBoughtTogether';

export default function ProductPage() {
  const productData = {
    name: "AeroSound Elite 5 - Premium Noise-Cancelling Headphones",
    price: 299.00,
    originalPrice: 350.00,
    rating: 4.5,
    reviewsCount: 120,
    images: [
      "/images/products/aerosound-elite-5.svg",
      "/images/products/aerosound-elite-5-side.svg",
      "/images/products/aerosound-elite-5-detail.svg",
      "/images/products/aerosound-elite-5-case.svg"
    ],
    colors: [
      { name: "Matte Black", value: "#1A1A1A" },
      { name: "Navy Blue", value: "#1B2A4E" },
      { name: "Silver Grey", value: "#D1D5DB" }
    ],
    bundle: {
      suggestedItems: [
        {
          id: "2",
          name: "Premium Aluminum Headphone Stand",
          price: 45.00,
          imageUrl: "/images/products/headphone-stand.svg"
        },
        {
          id: "3",
          name: "Hardshell Travel Protection Case",
          price: 29.00,
          imageUrl: "/images/products/travel-case.svg"
        }
      ]
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-surface">
      <Header />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-12 py-8 flex-grow w-full">
        {/* Breadcrumbs */}
        <nav className="mb-8">
          <ol className="flex items-center gap-2 text-sm font-semibold text-gray-500">
            <li><Link href="/" className="hover:text-primary">Home</Link></li>
            <li className="flex items-center gap-2">
              <ChevronRight className="w-4 h-4" />
              <Link href="/electronics" className="hover:text-primary">Electronics</Link>
            </li>
            <li className="flex items-center gap-2">
              <ChevronRight className="w-4 h-4" />
              <Link href="/audio" className="hover:text-primary">Audio</Link>
            </li>
            <li className="flex items-center gap-2 text-on-surface">
              <ChevronRight className="w-4 h-4" />
              <span>AeroSound Elite 5</span>
            </li>
          </ol>
        </nav>

        {/* Product Core */}
        <section className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-start">
          <ProductGallery images={productData.images} />
          <ProductInfo 
            name={productData.name}
            price={productData.price}
            originalPrice={productData.originalPrice}
            rating={productData.rating}
            reviewsCount={productData.reviewsCount}
            colors={productData.colors}
          />
        </section>

        {/* Tabs Section */}
        <ProductTabs />

        {/* Frequently Bought Together */}
        <FrequentlyBoughtTogether 
          currentProduct={{
            id: "1",
            name: productData.name,
            price: productData.price,
            imageUrl: productData.images[0]
          }}
          suggestedItems={productData.bundle.suggestedItems}
        />
      </main>

      <Footer />
    </div>
  );
}
