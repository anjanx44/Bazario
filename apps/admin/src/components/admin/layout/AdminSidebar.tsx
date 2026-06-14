'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { 
  LayoutDashboard, 
  Package, 
  TableProperties, 
  ShoppingBag, 
  Users, 
  Settings,
  LogOut,
  ChevronRight
} from 'lucide-react';

const menuItems = [
  { name: 'Dashboard', icon: LayoutDashboard, href: '/admin/dashboard' },
  { name: 'Product Catalog', icon: Package, href: '/admin/products' },
  { name: 'Inventory', icon: TableProperties, href: '/admin/inventory' },
  { name: 'Orders', icon: ShoppingBag, href: '/admin/orders' },
  { name: 'Customers', icon: Users, href: '/admin/customers' },
  { name: 'Settings', icon: Settings, href: '/admin/settings' },
];

export default function AdminSidebar() {
  const pathname = usePathname();

  return (
    <aside className="w-64 bg-white border-r border-gray-200 flex flex-col h-screen sticky top-0 z-20">
      <div className="p-6">
        <Link href="/admin/dashboard" className="flex items-center gap-3">
          <div className="w-10 h-10 bg-blue-600 rounded-xl flex items-center justify-center text-white">
            <Package className="w-6 h-6" />
          </div>
          <span className="text-xl font-bold text-gray-900 tracking-tight">Bazario <span className="text-blue-600">Admin</span></span>
        </Link>
      </div>

      <nav className="flex-1 px-4 py-4 space-y-1 overflow-y-auto">
        {menuItems.map((item) => {
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.name}
              href={item.href}
              className={`flex items-center justify-between px-4 py-3 rounded-xl transition-all group ${
                isActive 
                  ? 'bg-blue-50 text-blue-600 font-semibold' 
                  : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
              }`}
            >
              <div className="flex items-center gap-3">
                <item.icon className={`w-5 h-5 transition-transform ${isActive ? 'text-blue-600' : 'text-gray-400 group-hover:text-gray-600 group-hover:scale-110'}`} />
                <span className="text-sm">{item.name}</span>
              </div>
              <ChevronRight className={`w-4 h-4 transition-all ${isActive ? 'opacity-100' : 'opacity-0 group-hover:opacity-100'}`} />
            </Link>
          );
        })}
      </nav>

      <div className="p-4 border-t border-gray-100">
        <button className="w-full flex items-center gap-3 px-4 py-3 text-red-600 rounded-xl hover:bg-red-50 transition-colors group">
          <LogOut className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
          <span className="text-sm font-medium">Logout</span>
        </button>
      </div>
    </aside>
  );
}
