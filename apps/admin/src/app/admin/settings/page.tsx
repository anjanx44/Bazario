'use client';

import React, { useState } from 'react';
import { 
  Settings, 
  Globe, 
  Lock, 
  Bell, 
  Database, 
  ShieldCheck, 
  Save, 
  RotateCcw,
  Monitor,
  ToggleLeft as Toggle,
  Mail,
  Smartphone
} from 'lucide-react';

export default function SettingsPage() {
  const [activeTab, setActiveTab] = useState('general');

  const tabs = [
    { id: 'general', name: 'General', icon: Settings },
    { id: 'security', name: 'Security', icon: Lock },
    { id: 'notifications', name: 'Notifications', icon: Bell },
    { id: 'database', name: 'Database', icon: Database },
  ];

  return (
    <>
      <div className="flex flex-col lg:flex-row gap-8">
        {/* Settings Navigation */}
        <aside className="lg:w-64 flex flex-col gap-2">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-3 px-6 py-3.5 rounded-xl text-sm font-bold transition-all ${
                activeTab === tab.id 
                  ? 'bg-blue-600 text-white shadow-lg shadow-blue-600/20' 
                  : 'text-gray-500 hover:bg-gray-100 hover:text-gray-900'
              }`}
            >
              <tab.icon className="w-5 h-5" />
              {tab.name}
            </button>
          ))}
        </aside>

        {/* Settings Content */}
        <div className="flex-1 space-y-8">
          {/* Form Card */}
          <div className="bg-white rounded-3xl border border-gray-200 shadow-sm overflow-hidden">
            <div className="px-8 py-6 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Monitor className="w-6 h-6 text-blue-600" />
                <h2 className="text-xl font-bold text-gray-900">Store Configuration</h2>
              </div>
              <div className="flex items-center gap-3">
                <button className="px-4 py-2 text-sm font-bold text-gray-500 hover:bg-gray-100 rounded-lg transition-all flex items-center gap-2">
                  <RotateCcw className="w-4 h-4" /> Reset
                </button>
                <button className="px-6 py-2 bg-blue-600 text-white rounded-lg text-sm font-bold flex items-center gap-2 hover:opacity-90 transition-all">
                  <Save className="w-4 h-4" /> Save Changes
                </button>
              </div>
            </div>

            <div className="p-8 grid grid-cols-1 md:grid-cols-2 gap-8">
              <div className="space-y-2">
                <label className="text-sm font-bold text-gray-900">Store Name</label>
                <input 
                  type="text" 
                  defaultValue="Bazario Enterprise"
                  className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 transition-all"
                />
              </div>
              <div className="space-y-2">
                <label className="text-sm font-bold text-gray-900">Support Email</label>
                <input 
                  type="email" 
                  defaultValue="support@bazario.com"
                  className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 transition-all"
                />
              </div>
              <div className="space-y-2">
                <label className="text-sm font-bold text-gray-900">Default Currency</label>
                <select className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 transition-all text-sm">
                  <option>USD ($)</option>
                  <option>EUR (€)</option>
                  <option>GBP (£)</option>
                </select>
              </div>
              <div className="space-y-2">
                <label className="text-sm font-bold text-gray-900">Timezone</label>
                <select className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 transition-all text-sm">
                  <option>UTC (GMT+0)</option>
                  <option>EST (GMT-5)</option>
                  <option>PST (GMT-8)</option>
                </select>
              </div>
            </div>
          </div>

          {/* System Controls */}
          <div className="bg-white rounded-3xl border border-gray-200 shadow-sm overflow-hidden">
            <div className="px-8 py-6 bg-gray-50 border-b border-gray-200 flex items-center gap-3">
              <ShieldCheck className="w-6 h-6 text-blue-600" />
              <h2 className="text-xl font-bold text-gray-900">System Controls</h2>
            </div>
            
            <div className="p-8 space-y-6">
              {[
                { title: 'Enable Maintenance Mode', desc: 'Prevent customers from accessing the storefront while you make changes.', icon: Globe },
                { title: 'Email Notifications', desc: 'Send automated transaction and order status emails to customers.', icon: Mail },
                { title: 'Push Notifications', desc: 'Enable browser push notifications for marketing and system alerts.', icon: Smartphone },
              ].map((item, i) => (
                <div key={i} className="flex items-center justify-between py-4 border-b border-gray-100 last:border-0">
                  <div className="flex items-center gap-4">
                    <div className="p-3 bg-gray-50 rounded-xl">
                      <item.icon className="w-6 h-6 text-gray-400" />
                    </div>
                    <div>
                      <p className="text-lg font-bold text-gray-900">{item.title}</p>
                      <p className="text-sm text-gray-500 max-w-md">{item.desc}</p>
                    </div>
                  </div>
                  <button className="w-12 h-6 bg-blue-600 rounded-full relative transition-all group">
                    <div className="absolute top-1 right-1 w-4 h-4 bg-white rounded-full shadow-sm"></div>
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
