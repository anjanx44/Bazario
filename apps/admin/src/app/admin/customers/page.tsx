import React from 'react';
import { 
  Search, 
  Filter, 
  UserPlus, 
  Mail, 
  Phone, 
  MapPin, 
  MoreVertical, 
  Edit, 
  Trash2, 
  ChevronLeft, 
  ChevronRight,
  ShieldCheck,
  Ban
} from 'lucide-react';

const customers = [
  { 
    id: '1', 
    name: 'John Doe', 
    email: 'john.doe@example.com', 
    phone: '+1 (555) 123-4567', 
    location: 'New York, USA',
    orders: 12,
    spent: '$2,450.00',
    status: 'Active',
    joined: 'Oct 12, 2023'
  },
  { 
    id: '2', 
    name: 'Sarah Smith', 
    email: 'sarah.s@company.com', 
    phone: '+1 (555) 987-6543', 
    location: 'London, UK',
    orders: 5,
    spent: '$890.00',
    status: 'Active',
    joined: 'Sep 28, 2023'
  },
  { 
    id: '3', 
    name: 'Mike Johnson', 
    email: 'mike.j@web.net', 
    phone: '+1 (555) 456-7890', 
    location: 'Berlin, Germany',
    orders: 0,
    spent: '$0.00',
    status: 'Inactive',
    joined: 'Aug 15, 2023'
  },
];

export default function CustomerManagementPage() {
  return (
    <>
      {/* Stats Bar */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        {[
          { label: 'Total Customers', value: '12,842' },
          { label: 'New This Month', value: '842' },
          { label: 'Active Now', value: '1,204' },
          { label: 'Avg. LTV', value: '$452.00' },
        ].map((stat) => (
          <div key={stat.label} className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm">
            <p className="text-xs text-gray-500 uppercase tracking-wider mb-1">{stat.label}</p>
            <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
          </div>
        ))}
      </div>

      {/* Action Bar */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
        <div className="flex items-center gap-4 w-full md:w-auto">
          <div className="relative flex-1 md:w-80">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input 
              type="text" 
              placeholder="Search customers..." 
              className="w-full pl-10 pr-4 py-2.5 bg-white border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 transition-all"
            />
          </div>
          <button className="flex items-center gap-2 px-4 py-2.5 border border-gray-200 rounded-xl text-sm font-bold text-gray-500 hover:bg-gray-50 transition-all">
            <Filter className="w-5 h-5" />
            Filters
          </button>
        </div>

        <button className="flex items-center gap-2 px-6 py-2.5 bg-blue-600 text-white rounded-xl text-sm font-bold shadow-lg shadow-blue-600/20 hover:opacity-90 active:scale-95 transition-all">
          <UserPlus className="w-5 h-5" />
          Add Customer
        </button>
      </div>

      {/* Customers Table */}
      <div className="bg-white rounded-3xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="px-6 py-4 w-10">
                  <input type="checkbox" className="rounded border-gray-200 text-blue-600 focus:ring-blue-600" />
                </th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Customer</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Contact</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Activity</th>
                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-4 text-right text-xs font-bold text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {customers.map((customer) => (
                <tr key={customer.id} className="hover:bg-gray-50 transition-colors group">
                  <td className="px-6 py-4">
                    <input type="checkbox" className="rounded border-gray-200 text-blue-600 focus:ring-blue-600" />
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-4">
                      <div className="w-12 h-12 bg-blue-50 text-blue-600 rounded-full flex items-center justify-center font-bold text-sm">
                        {customer.name.split(' ').map(n => n[0]).join('')}
                      </div>
                      <div>
                        <p className="text-sm font-bold text-gray-900">{customer.name}</p>
                        <p className="text-xs text-gray-500 flex items-center gap-1">
                          <MapPin className="w-3 h-3" /> {customer.location}
                        </p>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="space-y-1">
                      <p className="text-xs text-gray-900 flex items-center gap-2 italic">
                        <Mail className="w-3.5 h-3.5 text-blue-600" /> {customer.email}
                      </p>
                      <p className="text-xs text-gray-500 flex items-center gap-2">
                        <Phone className="w-3.5 h-3.5 text-blue-600" /> {customer.phone}
                      </p>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="space-y-1">
                      <p className="text-sm font-bold text-gray-900">{customer.orders} Orders</p>
                      <p className="text-[11px] text-gray-500 uppercase tracking-wider">Spent: {customer.spent}</p>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full border text-[11px] font-bold uppercase tracking-wider ${
                      customer.status === 'Active' ? 'bg-emerald-50 text-emerald-700 border-emerald-100' : 'bg-gray-50 text-gray-500 border-gray-200'
                    }`}>
                      <span className={`w-1.5 h-1.5 rounded-full ${customer.status === 'Active' ? 'bg-emerald-600' : 'bg-gray-400'}`}></span>
                      {customer.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex justify-end gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors" title="Enable/Verify">
                        <ShieldCheck className="w-5 h-5" />
                      </button>
                      <button className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors" title="Edit Profile">
                        <Edit className="w-5 h-5" />
                      </button>
                      <button className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors" title="Block Customer">
                        <Ban className="w-5 h-5" />
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
            Showing <span className="font-bold text-gray-900">1-3</span> of <span className="font-bold text-gray-900">1,240</span> customers
          </p>
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
