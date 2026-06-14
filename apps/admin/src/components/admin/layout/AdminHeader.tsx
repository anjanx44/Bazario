import React from 'react';
import { Search, Bell, User } from 'lucide-react';

interface AdminHeaderProps {
  title: string;
}

export default function AdminHeader({ title }: AdminHeaderProps) {
  return (
    <header className="h-20 bg-white border-b border-gray-200 px-8 flex items-center justify-between sticky top-0 z-10 backdrop-blur-md bg-white/80">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">{title}</h1>
        <p className="text-sm text-gray-500">Welcome back, Admin</p>
      </div>

      <div className="flex items-center gap-6">
        <div className="relative group hidden md:block">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400 group-focus-within:text-blue-600 transition-colors" />
          <input 
            type="text" 
            placeholder="Search anything..." 
            className="pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-full text-sm focus:outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 w-64 transition-all"
          />
        </div>

        <div className="flex items-center gap-2">
          <button className="p-2.5 text-gray-500 hover:bg-gray-100 rounded-full transition-colors relative">
            <Bell className="w-6 h-6" />
            <span className="absolute top-2.5 right-2.5 w-2 h-2 bg-red-600 rounded-full border-2 border-white"></span>
          </button>
          
          <div className="h-8 w-[1px] bg-gray-200 mx-2"></div>

          <button className="flex items-center gap-3 pl-2 pr-4 py-1.5 hover:bg-gray-100 rounded-full transition-colors">
            <div className="w-9 h-9 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center">
              <User className="w-5 h-5" />
            </div>
            <div className="text-left hidden lg:block">
              <p className="text-sm font-bold text-gray-900 leading-tight">Admin User</p>
              <p className="text-[10px] text-gray-500 uppercase tracking-wider">Super Admin</p>
            </div>
          </button>
        </div>
      </div>
    </header>
  );
}
