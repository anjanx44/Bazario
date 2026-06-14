import React from 'react';
import { 
  Plus, 
  Search, 
  Filter, 
  MoreVertical, 
  Edit, 
  Trash2, 
  ExternalLink,
  ChevronLeft,
  ChevronRight,
  Download
} from 'lucide-react';
import Image from 'next/image';

const products = [
  { 
    id: '1', 
    name: 'AeroSound Elite 5', 
    sku: 'AS-ELITE5-BLK', 
    category: 'Audio', 
    price: '$299.00', 
    stock: 45, 
    status: 'In Stock',
    image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM'
  },
  { 
    id: '2', 
    name: 'Smart Watch Pro', 
    sku: 'SW-PRO-SIL', 
    category: 'Wearables', 
    price: '$199.00', 
    stock: 12, 
    status: 'Low Stock',
    image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCV54dkcMWwkMVBAqreY0UrmEIpcahUQn8gKhFq4YmBIi4IsuZvma6rp2juT2UBotGnR2bX7_u8zP2FDxaLUwc4WBSnNgDHoLm_rifzJ-hJLjTUDDHOoGQT6hlV4vtEkqXEIvy11YGAtekJum2QlBnV45yTqFMKfNFohLW92ldqFxoBgcHejEEDkbCH5_U3e6zwPCLdB1u1ixu0bGDWrRe_GTQrWRPuoNGazGKuTNpw1DVX1_oDQNeHu3pdUYPfnMMxq-PqaDxS7qU'
  },
  { 
    id: '3', 
    name: 'Leather Laptop Bag', 
    sku: 'LB-LTH-BRW', 
    category: 'Accessories', 
    price: '$89.00', 
    stock: 0, 
    status: 'Out of Stock',
    image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCewHoZeMQQEdBscS5Zz0-7GLBG8fQZMP2ZGmFLyvYUw1YgrzKcE6OiDGaEH6o9hhSCt_XXasINKhOTS8cPjMJ8-QNEHsd3c0vCpIju56rXwrbSI652k_rK7E39mckVRLonUZVWVDDV51BVWSYf62ka3qrpflQc7A-mFC60Y3TKJZlonHSbVnjI_mQy2vGSY3hN7PrTszbariSuKn28_yZjAWObv79ge_PfhdcOudpon6RKu1vFHzRzcaFQixJNSq9GJ_RCTE30t-E'
  },
];

export default function ProductCatalogPage() {
  return (
    <>
      {/* Action Bar */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
        <div className="flex items-center gap-4 w-full md:w-auto">
          <div className="relative flex-1 md:w-80">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input 
              type="text" 
              placeholder="Search products..." 
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
            Export
          </button>
          <button className="flex-1 md:flex-none flex items-center justify-center gap-2 px-6 py-2.5 bg-blue-600 text-white rounded-xl text-sm font-bold shadow-lg shadow-blue-600/20 hover:opacity-90 active:scale-95 transition-all">
            <Plus className="w-5 h-5" />
            Add Product
          </button>
        </div>
      </div>

      {/* Table Container */}
      <div className="bg-white rounded-3xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Product</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">SKU</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Category</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Price</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {products.map((product) => (
                <tr key={product.id} className="hover:bg-gray-50 transition-colors group">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-4">
                      <div className="relative w-12 h-12 rounded-lg border border-gray-200 overflow-hidden bg-gray-100">
                        <Image 
                          src={product.image} 
                          alt={product.name} 
                          fill 
                          className="object-cover"
                          sizes="48px"
                        />
                      </div>
                      <div>
                        <p className="text-sm font-bold text-gray-900">{product.name}</p>
                        <p className="text-xs text-gray-500">ID: {product.id}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 font-mono">{product.sku}</td>
                  <td className="px-6 py-4">
                    <span className="px-3 py-1 bg-blue-50 text-blue-600 text-xs font-bold rounded-full">
                      {product.category}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm font-bold text-gray-900">{product.price}</td>
                  <td className="px-6 py-4">
                    <div className={`flex items-center gap-1.5 px-3 py-1 rounded-full border w-fit ${
                      product.status === 'In Stock' ? 'text-emerald-700 bg-emerald-50 border-emerald-100' :
                      product.status === 'Low Stock' ? 'text-amber-700 bg-amber-50 border-amber-100' :
                      'text-red-700 bg-red-50 border-red-100'
                    }`}>
                      <span className={`w-1.5 h-1.5 rounded-full ${
                        product.status === 'In Stock' ? 'bg-emerald-600' :
                        product.status === 'Low Stock' ? 'bg-amber-600' : 'bg-red-600'
                      }`}></span>
                      <span className="text-[11px] font-bold uppercase tracking-wider">{product.status}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex justify-end gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                        <Edit className="w-5 h-5" />
                      </button>
                      <button className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                        <ExternalLink className="w-5 h-5" />
                      </button>
                      <button className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                        <Trash2 className="w-5 h-5" />
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
          <p className="text-sm text-gray-500">
            Showing <span className="font-bold text-gray-900">1-3</span> of <span className="font-bold text-gray-900">45</span> products
          </p>
          <div className="flex items-center gap-2">
            <button className="p-2 border border-gray-200 rounded-lg text-gray-400 hover:bg-white transition-colors disabled:opacity-40" disabled>
              <ChevronLeft className="w-5 h-5" />
            </button>
            <button className="w-10 h-10 flex items-center justify-center bg-blue-600 text-white rounded-lg font-bold text-sm">1</button>
            <button className="w-10 h-10 flex items-center justify-center border border-gray-200 rounded-lg text-gray-500 hover:bg-white font-bold text-sm">2</button>
            <button className="w-10 h-10 flex items-center justify-center border border-gray-200 rounded-lg text-gray-500 hover:bg-white font-bold text-sm">3</button>
            <button className="p-2 border border-gray-200 rounded-lg text-gray-400 hover:bg-white transition-colors">
              <ChevronRight className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
