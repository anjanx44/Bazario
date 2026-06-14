import React from 'react';
import Image from 'next/image';

export const HeroSection = () => {
  return (
    <section className="max-w-7xl mx-auto px-4 md:px-12 py-12 md:py-24">
      <div className="grid lg:grid-cols-2 gap-12 items-center">
        <div className="space-y-8">
          <h1 className="text-5xl md:text-6xl font-extrabold text-gray-900 leading-tight">
            Upgrade Your Lifestyle. <br/>
            <span className="text-blue-700">Premium Tech & Gear</span> Delivered Fast.
          </h1>
          <p className="text-lg md:text-xl text-gray-600 max-w-lg leading-relaxed">
            Curation meets performance. Discover the latest elite gadgets designed for the modern professional. Fast shipping globally.
          </p>
          <div className="pt-4">
            <button className="bg-blue-700 text-white font-bold px-10 py-4 rounded-lg hover:opacity-90 active:scale-95 transition-all shadow-lg shadow-blue-700/20">
              Shop Latest Drops
            </button>
          </div>
        </div>
        
        <div className="relative group">
          <div className="aspect-[4/5] bg-gray-200 rounded-xl overflow-hidden shadow-2xl relative">
            <Image 
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuCaTxtT9_grkc81oHlgbbXDwxI9wPbeMh6UZw02iSs0zdMfsMN_1gljcfqBR6iWPVQ2kKFfhpfC_7s8Gp_Lv72Zn0OFe88xPmZrv_bXHp4-fDptdrK2HfUXieaWZBUc4hp4pQBKZqzeUAFT7hk1aVCn99Q9BskR2h3Ly9W3_1x4B-E7i9IQqVwMnURDf5LRF5UWt7hBxtAIUdUbm2rs7QtifjclQKfvBeh3voxUH1YQBwtgNs94CDIOHVocuSCcnrUdiAVLu5fbKJs"
              alt="AeroSound Elite Headphones"
              fill
              priority
              className="object-cover group-hover:scale-105 transition-transform duration-700"
              sizes="(max-width: 768px) 100vw, 50vw"
            />
            <div className="absolute bottom-0 left-0 right-0 p-8 bg-gradient-to-t from-black/70 to-transparent text-white">
              <span className="text-sm font-bold uppercase tracking-widest text-blue-200">Premium Choice</span>
              <h3 className="text-3xl font-bold mt-2">AeroSound Elite</h3>
              <p className="text-base opacity-90 mt-1">Experience pure acoustic perfection.</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
