import React from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { ShoppingCart, Star } from 'lucide-react';
import { Product } from '@/types';

interface ProductCardProps {
  product: Product;
}

export const ProductCard = ({ product }: ProductCardProps) => {
  return (
    <div className="group bg-white border border-gray-200 rounded-xl overflow-hidden hover:shadow-xl transition-all duration-300">
      <div className="aspect-[4/5] relative bg-gray-100 overflow-hidden">
        <Image 
          src={product.imageUrl} 
          alt={product.name}
          fill
          className="object-cover group-hover:scale-110 transition-transform duration-500"
          sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 25vw"
        />
        {product.discountPercentage && (
          <div className="absolute top-4 left-4 bg-orange-600 text-white px-3 py-1 rounded text-sm font-bold">
            {product.discountPercentage}% OFF
          </div>
        )}
        <button className="absolute bottom-4 right-4 bg-blue-700 text-white w-10 h-10 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 translate-y-4 group-hover:translate-y-0 transition-all duration-300 shadow-lg z-10">
          <ShoppingCart className="w-5 h-5" />
        </button>
      </div>
      
      <div className="p-4 space-y-2">
        <div className="flex items-center gap-1">
          <Star className="w-4 h-4 text-orange-500 fill-orange-500" />
          <span className="text-sm font-semibold text-gray-700">
            {product.rating} ({product.reviewsCount} reviews)
          </span>
        </div>
        
        <Link href={`/product/${product.slug}`}>
          <h4 className="text-base font-bold text-gray-900 hover:text-blue-700 transition-colors line-clamp-1">
            {product.name}
          </h4>
        </Link>
        
        <div className="flex items-baseline gap-2">
          <span className="text-xl font-bold text-emerald-700">
            ${product.discountPrice || product.basePrice}
          </span>
          {product.discountPrice && (
            <span className="text-sm text-gray-400 line-through">
              ${product.basePrice}
            </span>
          )}
        </div>
      </div>
    </div>
  );
};
