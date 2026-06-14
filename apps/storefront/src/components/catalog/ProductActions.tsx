'use client';

import { useState } from 'react';
import { ShoppingCart, Plus, Minus, Truck, ShieldCheck } from 'lucide-react';

interface Color {
  name: string;
  hex: string;
}

interface ProductActionsProps {
  colors?: Color[];
}

export default function ProductActions({ colors = [] }: ProductActionsProps) {
  const [selectedColor, setSelectedColor] = useState(colors[0]?.name || '');
  const [quantity, setQuantity] = useState(1);

  return (
    <div className="flex flex-col gap-6">
      {/* Variants */}
      <div className="flex flex-col gap-4">
        {colors.length > 0 && (
          <div>
            <span className="text-label-md font-label-md text-on-surface-variant block mb-3 uppercase tracking-wider">
              Color: {selectedColor}
            </span>
            <div className="flex gap-3">
              {colors.map((color) => (
                <button
                  key={color.name}
                  onClick={() => setSelectedColor(color.name)}
                  className={`w-10 h-10 rounded-full transition-all ${
                    selectedColor === color.name
                      ? 'ring-2 ring-primary ring-offset-2'
                      : 'border border-outline-variant hover:ring-2 hover:ring-outline'
                  }`}
                  style={{ backgroundColor: color.hex }}
                  title={color.name}
                />
              ))}
            </div>
          </div>
        )}

        <div>
          <span className="text-label-md font-label-md text-on-surface-variant block mb-3 uppercase tracking-wider">
            Quantity
          </span>
          <div className="inline-flex items-center border border-outline-variant rounded-xl p-1 bg-surface-container-low">
            <button 
              onClick={() => setQuantity(Math.max(1, quantity - 1))}
              className="w-10 h-10 flex items-center justify-center hover:bg-surface-container-highest rounded-lg transition-colors"
            >
              <Minus className="w-5 h-5" />
            </button>
            <span className="w-12 text-center font-bold text-body-md">{quantity}</span>
            <button 
              onClick={() => setQuantity(quantity + 1)}
              className="w-10 h-10 flex items-center justify-center hover:bg-surface-container-highest rounded-lg transition-colors"
            >
              <Plus className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>

      {/* CTAs */}
      <div className="flex flex-col sm:flex-row gap-4 mt-4">
        <button className="flex-1 bg-primary text-on-primary py-4 px-8 rounded-full font-bold text-body-lg flex items-center justify-center gap-2 hover:opacity-90 active:scale-[0.98] transition-all">
          <ShoppingCart className="w-6 h-6" />
          Add to Cart
        </button>
        <button className="flex-1 border-2 border-primary text-primary py-4 px-8 rounded-full font-bold text-body-lg hover:bg-primary-container hover:text-on-primary-container transition-all">
          Buy Now
        </button>
      </div>

      {/* Trust Badges */}
      <div className="flex flex-col gap-3 mt-4 text-body-md text-on-surface-variant">
        <div className="flex items-center gap-3">
          <Truck className="w-6 h-6 text-primary" />
          <span>Free express shipping on orders over $200</span>
        </div>
        <div className="flex items-center gap-3">
          <ShieldCheck className="w-6 h-6 text-primary" />
          <span>2-Year Global Warranty and support</span>
        </div>
      </div>
    </div>
  );
}
