'use client';

import React from 'react';
import Link from 'next/link';
import { ShoppingCart, Heart, Search, MapPin, User } from 'lucide-react';

export const Header = () => {
  return (
    <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="flex justify-between items-center w-full px-4 md:px-12 py-4 max-w-7xl mx-auto">
        <div className="flex flex-col">
          <Link href="/" className="text-2xl font-bold tracking-tight text-blue-700">
            Bazario Store
          </Link>
          <span className="text-[10px] uppercase tracking-widest font-bold text-gray-500 opacity-60">
            Your Premium Hub
          </span>
        </div>
        
        <div className="hidden lg:flex flex-1 max-w-xl mx-6">
          <div className="relative w-full">
            <input 
              className="w-full bg-gray-50 border border-gray-200 rounded-lg py-2 pl-10 pr-4 focus:ring-2 focus:ring-blue-600 focus:ring-opacity-20 focus:border-blue-600 transition-all outline-none" 
              placeholder="Search premium gear..." 
              type="text"
            />
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
          </div>
        </div>
        
        <nav className="hidden md:flex items-center gap-6">
          <Link className="text-sm font-semibold text-blue-700 border-b-2 border-blue-700 pb-1" href="#">
            New Arrivals
          </Link>
          <Link className="text-sm font-semibold text-gray-600 hover:text-blue-700 transition-colors" href="#">
            Best Sellers
          </Link>
          <Link className="text-sm font-semibold text-gray-600 hover:text-blue-700 transition-colors" href="#">
            Categories
          </Link>
          <Link className="text-sm font-semibold text-gray-600 hover:text-blue-700 transition-colors" href="#">
            Sale
          </Link>
        </nav>
        
        <div className="flex items-center gap-4 ml-6">
          <div className="flex items-center text-gray-600 hover:bg-gray-50 px-2 py-1 rounded transition-all cursor-pointer">
            <MapPin className="mr-1 w-5 h-5" />
            <span className="text-sm font-semibold hidden sm:inline">Dhaka</span>
          </div>
          
          <div className="relative p-2 hover:bg-gray-50 rounded-full transition-all cursor-pointer">
            <Heart className="w-6 h-6 text-gray-600" />
            <span className="absolute top-0 right-0 bg-blue-600 text-white text-[10px] w-4 h-4 flex items-center justify-center rounded-full font-bold">
              5
            </span>
          </div>
          
          <div className="flex items-center gap-2 p-2 hover:bg-gray-50 rounded-lg transition-all cursor-pointer">
            <div className="relative">
              <ShoppingCart className="w-6 h-6 text-gray-600" />
              <span className="absolute -top-1 -right-1 bg-emerald-600 text-white text-[10px] w-4 h-4 flex items-center justify-center rounded-full font-bold">
                2
              </span>
            </div>
            <span className="text-sm font-semibold hidden lg:inline">$420.00</span>
          </div>
          
          <div className="p-2 hover:bg-gray-50 rounded-full transition-all cursor-pointer">
            <User className="w-6 h-6 text-gray-600" />
          </div>
        </div>
      </div>
    </header>
  );
};
