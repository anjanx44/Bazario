import React from 'react';
import { 
  Search, 
  Filter, 
  Download, 
  RefreshCcw, 
  AlertTriangle, 
  ArrowUp, 
  ArrowDown,
  ChevronLeft,
  ChevronRight,
  History
} from 'lucide-react';
import Image from 'next/image';

const inventoryItems = [
  { 
    id: '1', 
    name: 'AeroSound Elite 5', 
    sku: 'AS-ELITE5-BLK', 
    stock: 45, 
    threshold: 15, 
    lastRestocked: '2023-10-15',
    status: 'Healthy',
    image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM'
  },
  { 
    id: '2', 
    name: 'Smart Watch Pro', 
    sku: 'SW-PRO-SIL', 
    stock: 12, 
    threshold: 20, 
    lastRestocked: '2023-09-28',
    status: 'Low Stock',
    image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCV54dkcMWwkMVBAqreY0UrmEIpcahUQn8gKhFq4YmBIi4IsuZvma6rp2juT2UBotGnR2bX7_u8zP2FDxaLUwc4WBSnNgDHoLm_rifzJ-hJLjTUDDHOoGQT6hlV4vtEkqXEIvy11YGAtekJum2QlBnV45yTqFMKfNFohLW92ldqFxoBgcHejEEDkbCH5_U3e6zwPCLdB1u1ixu0bGDWrRe_GTQrWRPuoNGazGKuTNpw1DVX1_oDQNeHu3pdUYPfnMMxq-PqaDxS7qU'
  },
  { 
    id: '3', 
    name: 'Leather Laptop Bag', 
    stock: 0, 
    sku: 'LB-LTH-BRW', 
    threshold: 10, 
    lastRestocked: '2023-08-12',
    status: 'Out of Stock',
    image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCewHoZeMQQEdBscS5Zz0-7GLBG8fQZMP2ZGmFLyvYUw1YgrzKcE6OiDGaEH6o9hhSCt_XXasINKhOTS8cPjMJ8-QNEHsd3c0vCpIju56rXwrbSI652k_rK7E39mckVRLonUZVWVDDV51BVWSYf62ka3qrpflQc7A-mFC60Y3TKJZlonHSbVnjI_mQy2vGSY3hN7PrTszbariSuKn28_yZjAWObv79ge_PfhdcOudpon6RKu1vFHzRzcaFQixJNSq9GJ_RCTE30t-E'
  },
];

export default function InventoryManagementPage() {
  return (
    <>
      {/* Overview Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white p-6 rounded-2xl border border-gray-200 flex items-center gap-6">
          <div className="w-14 h-14 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center">
            <RefreshCcw className="w-7 h-7" />
          </div>
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wider">Total SKUs</p>
            <p className="text-2xl font-bold text-gray-900">1,284</p>
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-gray-200 flex items-center gap-6">
          <div className="w-14 h-14 bg-red-50 text-red-600 rounded-xl flex items-center justify-center">
            <AlertTriangle className="w-7 h-7" />
          </div>
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wider">Low Stock</p>
            <p className="text-2xl font-bold text-gray-900">24 Items</p>
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-gray-200 flex items-center gap-6">
          <div className="w-14 h-14 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center">
            <History className="w-7 h-7" />
          </div>
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wider">Restock Alerts</p>
            <p className="text-2xl font-bold text-gray-900">12 New</p>
          </div>
        </div>
      </div>

      {/* Action Bar */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
        <div className="flex items-center gap-4 w-full md:w-auto">
          <div className="relative flex-1 md:w-80">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input 
              type="text" 
              placeholder="Search by SKU or Name..." 
              className="w-full pl-10 pr-4 py-2.5 bg-white border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 transition-all"
            />
          </div>
          <button className="flex items-center gap-2 px-4 py-2.5 border border-gray-200 rounded-xl text-sm font-bold text-gray-500 hover:bg-gray-50 transition-all">
            <Filter className="w-5 h-5" />
            Filters
          </button>
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <button className="flex-1 md:flex-none flex items-center justify-center gap-2 px-4 py-2.5 border border-gray-200 rounded-xl text-sm font-bold text-gray-500 hover:bg-gray-50 transition-all">
            <Download className="w-5 h-5" />
            Export CSV
          </button>
          <button className="flex-1 md:flex-none flex items-center justify-center gap-2 px-6 py-2.5 bg-blue-600 text-white rounded-xl text-sm font-bold shadow-lg shadow-blue-600/20 hover:opacity-90 transition-all">
            <RefreshCcw className="w-5 h-5" />
            Bulk Restock
          </button>
        </div>
      </div>

      {/* Inventory Table */}
      <div className="bg-white rounded-3xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="px-6 py-4 w-10">
                  <input type="checkbox" className="rounded border-gray-200 text-blue-600 focus:ring-blue-600" />
                </th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Product</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">SKU</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Stock Level</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Threshold</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider text-right">Last Restock</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {inventoryItems.map((item) => (
                <tr key={item.id} className="hover:bg-gray-50 transition-colors group">
                  <td className="px-6 py-4">
                    <input type="checkbox" className="rounded border-gray-200 text-blue-600 focus:ring-blue-600" />
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-4">
                      <div className="relative w-12 h-12 rounded-lg border border-gray-200 overflow-hidden bg-gray-100">
                        <Image 
                          src={item.image} 
                          alt={item.name} 
                          fill 
                          className="object-cover"
                          sizes="48px"
                        />
                      </div>
                      <p className="text-sm font-bold text-gray-900">{item.name}</p>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 font-mono">{item.sku}</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-24 h-2 bg-gray-100 rounded-full overflow-hidden">
                        <div 
                          className={`h-full rounded-full ${
                            item.stock === 0 ? 'bg-red-600' :
                            item.stock <= item.threshold ? 'bg-amber-500' : 'bg-emerald-500'
                          }`}
                          style={{ width: `${Math.min(100, (item.stock / (item.threshold * 3)) * 100)}%` }}
                        ></div>
                      </div>
                      <span className={`text-sm font-bold ${
                        item.stock === 0 ? 'text-red-600' :
                        item.stock <= item.threshold ? 'text-amber-600' : 'text-emerald-600'
                      }`}>
                        {item.stock}
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500">
                    {item.threshold} units
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex flex-col items-end">
                      <p className="text-sm font-medium text-gray-900">{item.lastRestocked}</p>
                      <button className="text-[10px] font-bold text-blue-600 uppercase tracking-wider mt-1 hover:underline">
                        View History
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="px-6 py-4 bg-gray-50 border-t border-gray-200 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <button className="px-4 py-2 border border-gray-200 rounded-lg text-sm font-bold text-gray-500 hover:bg-white transition-colors">
              Update Stock Levels
            </button>
          </div>
          <div className="flex items-center gap-2">
            <button className="p-2 border border-gray-200 rounded-lg text-gray-400 hover:bg-white transition-colors disabled:opacity-40" disabled>
              <ChevronLeft className="w-5 h-5" />
            </button>
            <button className="w-10 h-10 flex items-center justify-center bg-blue-600 text-white rounded-lg font-bold text-sm">1</button>
            <button className="w-10 h-10 flex items-center justify-center border border-gray-200 rounded-lg text-gray-500 hover:bg-white font-bold text-sm">2</button>
            <button className="p-2 border border-gray-200 rounded-lg text-gray-400 hover:bg-white transition-colors">
              <ChevronRight className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
