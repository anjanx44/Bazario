'use client';

import React, { useState } from 'react';
import { Star, StarHalf, ShoppingCart, Truck, ShieldCheck, Minus, Plus } from 'lucide-react';

interface ProductInfoProps {
  name: string;
  price: number;
  originalPrice?: number;
  rating: number;
  reviewsCount: number;
  colors: { name: string; value: string }[];
}

export const ProductInfo = ({
  name,
  price,
  originalPrice,
  rating,
  reviewsCount,
  colors
}: ProductInfoProps) => {
  const [selectedColor, setSelectedColor] = useState(0);
  const [quantity, setQuantity] = useState(1);

  const discount = originalPrice ? Math.round(((originalPrice - price) / originalPrice) * 100) : 0;

  return (
    <div className="flex flex-col gap-6 lg:pl-12">
      <div>
        <h1 className="text-3xl font-bold text-on-surface mb-2">{name}</h1>
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-1 text-orange-500">
            {[...Array(Math.floor(rating))].map((_, i) => (
              <Star key={i} className="w-5 h-5 fill-current" />
            ))}
            {rating % 1 !== 0 && <StarHalf className="w-5 h-5 fill-current" />}
            <span className="text-sm font-semibold text-gray-500 ml-1">({reviewsCount} reviews)</span>
          </div>
          <span className="px-2 py-0.5 bg-green-100 text-green-700 text-xs font-bold rounded-full">In Stock</span>
        </div>
      </div>

      <div className="flex items-center gap-4 py-4 border-y border-gray-200">
        <span className="text-3xl font-bold text-on-surface">${price.toFixed(2)}</span>
        {originalPrice && (
          <>
            <span className="text-lg text-gray-400 line-through">${originalPrice.toFixed(2)}</span>
            <span className="px-2 py-1 bg-red-100 text-red-600 text-xs font-bold rounded-lg">-{discount}% OFF</span>
          </>
        )}
      </div>

      <div className="flex flex-col gap-4">
        <div>
          <span className="text-sm font-bold text-gray-500 block mb-3 uppercase tracking-wider">
            Color: {colors[selectedColor].name}
          </span>
          <div className="flex gap-3">
            {colors.map((color, index) => (
              <button
                key={index}
                onClick={() => setSelectedColor(index)}
                className={`w-10 h-10 rounded-full transition-all ${
                  selectedColor === index ? 'ring-2 ring-primary ring-offset-2' : 'border border-gray-200 hover:ring-2 hover:ring-gray-300'
                }`}
                style={{ backgroundColor: color.value }}
                title={color.name}
              />
            ))}
          </div>
        </div>

        <div>
          <span className="text-sm font-bold text-gray-500 block mb-3 uppercase tracking-wider">Quantity</span>
          <div className="inline-flex items-center border border-gray-200 rounded-xl p-1 bg-gray-50">
            <button 
              onClick={() => setQuantity(Math.max(1, quantity - 1))}
              className="w-10 h-10 flex items-center justify-center hover:bg-gray-200 rounded-lg transition-colors"
            >
              <Minus className="w-4 h-4" />
            </button>
            <span className="w-12 text-center font-bold text-base">{quantity}</span>
            <button 
              onClick={() => setQuantity(quantity + 1)}
              className="w-10 h-10 flex items-center justify-center hover:bg-gray-200 rounded-lg transition-colors"
            >
              <Plus className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      <div className="flex flex-col sm:flex-row gap-4 mt-4">
        <button className="flex-1 bg-primary text-white py-4 px-8 rounded-full font-bold text-lg flex items-center justify-center gap-2 hover:opacity-90 active:scale-[0.98] transition-all">
          <ShoppingCart className="w-5 h-5" />
          Add to Cart
        </button>
        <button className="flex-1 border-2 border-primary text-primary py-4 px-8 rounded-full font-bold text-lg hover:bg-blue-50 transition-all">
          Buy Now
        </button>
      </div>

      <div className="flex flex-col gap-3 mt-4 text-sm text-gray-600">
        <div className="flex items-center gap-3">
          <Truck className="w-5 h-5 text-primary" />
          <span>Free express shipping on orders over $200</span>
        </div>
        <div className="flex items-center gap-3">
          <ShieldCheck className="w-5 h-5 text-primary" />
          <span>2-Year Global Warranty and support</span>
        </div>
      </div>
    </div>
  );
};
