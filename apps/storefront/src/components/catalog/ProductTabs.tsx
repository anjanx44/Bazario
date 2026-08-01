'use client';

import React, { useState } from 'react';
import Image from 'next/image';

interface Tab {
  id: string;
  label: string;
  content: React.ReactNode;
}

export const ProductTabs = () => {
  const [activeTab, setActiveTab] = useState('description');

  const tabs: Tab[] = [
    {
      id: 'description',
      label: 'Description',
      content: (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          <div className="flex flex-col gap-4">
            <h3 className="text-2xl font-bold">Experience Pure Silence.</h3>
            <p className="text-lg text-gray-600 leading-relaxed">
              The AeroSound Elite 5 redefines acoustic excellence. Equipped with our proprietary ANC 4.0 chip, these headphones analyze ambient noise 50,000 times per second to create an immersive bubble of silence, no matter where you are.
            </p>
            <p className="text-base text-gray-600">
              From the bustling city streets to the hum of a jet engine, focus only on what matters: your sound. With up to 60 hours of battery life and studio-grade high-fidelity drivers, the Elite 5 isn&apos;t just a gadget—it&apos;s an investment in your auditory lifestyle.
            </p>
          </div>
          <div className="relative aspect-video rounded-2xl overflow-hidden shadow-xl border border-gray-200">
            <Image
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuCaFPVcjmQk6wwVbzCppn59KRIgJhhnYqIt3iY0gtRc4bTjj2n__mZo6iRZq6lfBys-nG7ECkpuSasymdiWTGq53owVFSyTi721Ar3cdZUDwMUHrHjXzyzA721k08RQvijPKnJc6cvCQqKx-sCfAQc6bTFBpFqSo5jT7VDRuPGp6a18nY4wydYeGo3-SqN4-ZsFVy52AErHJ9BMdi67hI1tMkAp88MCvTjfL3pa2Z_7H1AtQCzvgFj2nkSwP7Gqs4xa0ImfYmnAi9Y"
              alt="Lifestyle image"
              fill
              className="object-cover"
            />
          </div>
        </div>
      )
    },
    {
      id: 'specifications',
      label: 'Specifications',
      content: (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div className="space-y-4">
            <div className="flex justify-between border-b pb-2">
              <span className="font-semibold">Driver Size</span>
              <span>40mm High-Resolution</span>
            </div>
            <div className="flex justify-between border-b pb-2">
              <span className="font-semibold">Frequency Response</span>
              <span>20Hz - 40kHz</span>
            </div>
            <div className="flex justify-between border-b pb-2">
              <span className="font-semibold">Battery Life</span>
              <span>Up to 60 Hours</span>
            </div>
          </div>
          <div className="space-y-4">
            <div className="flex justify-between border-b pb-2">
              <span className="font-semibold">Connectivity</span>
              <span>Bluetooth 5.3, USB-C</span>
            </div>
            <div className="flex justify-between border-b pb-2">
              <span className="font-semibold">Noise Cancellation</span>
              <span>Hybrid Active (4-mic)</span>
            </div>
            <div className="flex justify-between border-b pb-2">
              <span className="font-semibold">Weight</span>
              <span>250g</span>
            </div>
          </div>
        </div>
      )
    },
    {
      id: 'reviews',
      label: 'Reviews (120)',
      content: (
        <div className="space-y-6">
          <p className="text-gray-600 italic">User reviews will be loaded here...</p>
        </div>
      )
    }
  ];

  return (
    <section className="mt-12 border-t border-gray-200">
      <div className="flex border-b border-gray-200 overflow-x-auto">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-8 py-5 text-sm font-bold uppercase tracking-widest transition-all whitespace-nowrap ${
              activeTab === tab.id
                ? 'border-b-2 border-primary text-primary'
                : 'text-gray-500 hover:text-primary'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>
      <div className="py-8 animate-in fade-in slide-in-from-bottom-2 duration-500">
        {tabs.find((tab) => tab.id === activeTab)?.content}
      </div>
    </section>
  );
};
