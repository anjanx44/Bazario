'use client';

import { useState } from 'react';
import Image from 'next/image';
import { Plus } from 'lucide-react';

interface BundleItem {
  id: string;
  name: string;
  price: number;
  imageUrl: string;
}

interface FrequentlyBoughtTogetherProps {
  currentProduct: BundleItem;
  suggestedItems: BundleItem[];
}

export default function FrequentlyBoughtTogether({
  currentProduct,
  suggestedItems
}: FrequentlyBoughtTogetherProps) {
  const [selectedIds, setSelectedIds] = useState<string[]>([
    currentProduct.id,
    ...suggestedItems.map(item => item.id)
  ]);

  const toggleItem = (id: string) => {
    if (id === currentProduct.id) return; // Cannot deselect main product
    setSelectedIds(prev => 
      prev.includes(id) 
        ? prev.filter(i => i !== id) 
        : [...prev, id]
    );
  };

  const allItems = [currentProduct, ...suggestedItems];
  const selectedItems = allItems.filter(item => selectedIds.includes(item.id));
  const totalPrice = selectedItems.reduce((sum, item) => sum + item.price, 0);

  return (
    <section className="mt-stack-lg bg-surface-container-low p-8 rounded-3xl border border-outline-variant">
      <h2 className="text-headline-md font-headline-md mb-6">Frequently Bought Together</h2>
      
      <div className="flex flex-col lg:flex-row items-center gap-8">
        <div className="flex items-center gap-4">
          {allItems.map((item, index) => (
            <div key={item.id} className="flex items-center gap-4">
              <div className={`w-24 h-24 bg-surface rounded-xl border border-outline-variant p-2 relative ${!selectedIds.includes(item.id) && 'opacity-40 grayscale'}`}>
                <Image
                  src={item.imageUrl}
                  alt={item.name}
                  width={80}
                  height={80}
                  className="w-full h-full object-contain"
                />
              </div>
              {index < allItems.length - 1 && (
                <Plus className="w-6 h-6 text-outline" />
              )}
            </div>
          ))}
        </div>

        <div className="flex flex-col gap-3 flex-1">
          {allItems.map(item => (
            <label key={item.id} className="flex items-center gap-3 cursor-pointer group">
              <input
                type="checkbox"
                checked={selectedIds.includes(item.id)}
                onChange={() => toggleItem(item.id)}
                disabled={item.id === currentProduct.id}
                className="w-5 h-5 text-primary rounded border-outline-variant focus:ring-primary disabled:opacity-50"
              />
              <span className={`text-body-md ${!selectedIds.includes(item.id) && 'text-on-surface-variant'}`}>
                {item.id === currentProduct.id ? 'This item: ' : ''}
                {item.name} 
                <span className="font-bold ml-2">${item.price.toFixed(2)}</span>
              </span>
            </label>
          ))}
        </div>

        <div className="lg:border-l lg:pl-8 flex flex-col items-center lg:items-start gap-4">
          <div>
            <p className="text-on-surface-variant text-label-md uppercase">Total Bundle Price</p>
            <p className="text-headline-lg font-headline-lg text-primary">${totalPrice.toFixed(2)}</p>
          </div>
          <button className="bg-primary text-on-primary px-8 py-3 rounded-full font-bold hover:opacity-90 transition-all">
            Add Bundle to Cart
          </button>
        </div>
      </div>
    </section>
  );
}
