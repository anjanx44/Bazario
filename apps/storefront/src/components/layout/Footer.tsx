import React from 'react';
import Link from 'next/link';
import { Facebook, Instagram, Globe, CreditCard, Wallet, Wifi } from 'lucide-react';

export const Footer = () => {
  return (
    <footer className="bg-gray-50 border-t border-gray-200 w-full">
      <div className="max-w-7xl mx-auto px-4 md:px-12 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-12">
          <div className="space-y-4">
            <span className="text-xl font-bold text-gray-900">Bazario Store</span>
            <p className="text-sm text-gray-600 leading-relaxed">
              Premium tech and gear for the modern enthusiast. High-quality standards in every delivery.
            </p>
            <div className="flex gap-4">
              <Link className="text-gray-400 hover:text-blue-700 transition-colors" href="#">
                <Facebook className="w-5 h-5" />
              </Link>
              <Link className="text-gray-400 hover:text-blue-700 transition-colors" href="#">
                <Instagram className="w-5 h-5" />
              </Link>
              <Link className="text-gray-400 hover:text-blue-700 transition-colors" href="#">
                <Globe className="w-5 h-5" />
              </Link>
            </div>
          </div>
          
          <div>
            <h4 className="font-bold text-gray-900 mb-4">Shop</h4>
            <ul className="space-y-2 text-sm font-semibold text-gray-600">
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">New Arrivals</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Best Sellers</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Tech Gadgets</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Accessories</Link></li>
            </ul>
          </div>
          
          <div>
            <h4 className="font-bold text-gray-900 mb-4">Company</h4>
            <ul className="space-y-2 text-sm font-semibold text-gray-600">
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">About Us</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Careers</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Partnerships</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Sustainability</Link></li>
            </ul>
          </div>
          
          <div>
            <h4 className="font-bold text-gray-900 mb-4">Legal & Support</h4>
            <ul className="space-y-2 text-sm font-semibold text-gray-600">
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Shipping & Returns</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Security Policy</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Customer Support</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Privacy Policy</Link></li>
              <li><Link className="hover:underline hover:text-gray-900 transition-all" href="#">Terms of Service</Link></li>
            </ul>
          </div>
        </div>
        
        <div className="border-t border-gray-200 pt-6 flex flex-col md:flex-row justify-between items-center gap-4">
          <p className="text-sm font-semibold text-gray-500">
            © {new Date().getFullYear()} Bazario Store. All rights reserved.
          </p>
          <div className="flex gap-4 items-center text-gray-400">
            <CreditCard className="w-6 h-6" />
            <Wallet className="w-6 h-6" />
            <Wifi className="w-6 h-6" />
          </div>
        </div>
      </div>
    </footer>
  );
};
