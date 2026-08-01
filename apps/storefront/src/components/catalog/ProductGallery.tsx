'use client';

import React, { useState } from 'react';
import Image from 'next/image';
import { Heart } from 'lucide-react';

interface ProductGalleryProps {
  images: string[];
}

export const ProductGallery = ({ images }: ProductGalleryProps) => {
  const [activeImage, setActiveImage] = useState(0);

  return (
    <div className="flex flex-col gap-4">
      <div className="aspect-[4/5] bg-surface-container-low rounded-xl overflow-hidden border border-outline-variant relative group">
        <Image
          src={images[activeImage]}
          alt="Product main image"
          fill
          className="object-cover group-hover:scale-105 transition-transform duration-500"
          priority
        />
        <button className="absolute top-4 right-4 bg-white/80 backdrop-blur-md p-3 rounded-full border border-outline-variant text-on-surface hover:bg-primary hover:text-white transition-all duration-300">
          <Heart className="w-6 h-6" />
        </button>
      </div>
      
      <div className="grid grid-cols-4 gap-4">
        {images.map((image, index) => (
          <button
            key={index}
            onClick={() => setActiveImage(index)}
            className={`aspect-square bg-surface-container-low rounded-lg overflow-hidden border-2 transition-all ${
              activeImage === index ? 'border-primary ring-2 ring-primary/20' : 'border-outline-variant hover:border-primary'
            }`}
          >
            <div className="relative w-full h-full">
              <Image
                src={image}
                alt={`Product thumbnail ${index + 1}`}
                fill
                className="object-cover"
              />
            </div>
          </button>
        ))}
      </div>
    </div>
  );
};
