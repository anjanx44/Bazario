'use client';

import React from 'react';
import AdminSidebar from '@/components/admin/layout/AdminSidebar';
import AdminHeader from '@/components/admin/layout/AdminHeader';
import { usePathname } from 'next/navigation';

interface AdminLayoutProps {
  children: React.ReactNode;
}

const getTitle = (pathname: string) => {
  if (pathname.includes('/dashboard')) return 'Dashboard Overview';
  if (pathname.includes('/products')) return 'Product Catalog';
  if (pathname.includes('/inventory')) return 'Inventory Management';
  if (pathname.includes('/orders')) return 'Order Management';
  if (pathname.includes('/customers')) return 'Customer Management';
  if (pathname.includes('/settings')) return 'System Settings';
  return 'Admin Portal';
};

export default function AdminLayout({ children }: AdminLayoutProps) {
  const pathname = usePathname();
  const title = getTitle(pathname);

  return (
    <div className="flex min-h-screen bg-gray-50">
      <AdminSidebar />
      <div className="flex-1 flex flex-col min-w-0 h-screen overflow-hidden">
        <AdminHeader title={title} />
        <main className="flex-1 overflow-y-auto p-8">
          <div className="max-w-7xl mx-auto">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}
