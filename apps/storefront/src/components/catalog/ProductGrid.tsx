import React from 'react';
import Link from 'next/link';
import { ProductCard } from '@/components/catalog/ProductCard';
import { Product } from '@/types';

interface ProductGridProps {
  title: string;
  products: Product[];
  seeMoreHref?: string;
}

export const ProductGrid = ({ title, products, seeMoreHref }: ProductGridProps) => {
  return (
    <section className="max-w-7xl mx-auto px-4 md:px-12 py-12 md:py-24">
      <div className="flex justify-between items-end mb-8">
        <h2 className="text-3xl font-bold text-gray-900">{title}</h2>
        {seeMoreHref && (
          <Link href={seeMoreHref} className="text-blue-700 font-bold hover:underline">
            See More Collections
          </Link>
        )}
      </div>
      
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </section>
  );
};
