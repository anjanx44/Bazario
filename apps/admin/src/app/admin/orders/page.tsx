import React from 'react';
import { 
  Search, 
  Filter, 
  Download, 
  MoreVertical, 
  Eye, 
  Truck, 
  CheckCircle,
  XCircle,
  Clock,
  ChevronLeft,
  ChevronRight,
  ExternalLink
} from 'lucide-react';

const orders = [
  { 
    id: '#ORD-7829', 
    customer: 'John Doe', 
    date: '2023-10-24 14:32', 
    amount: '$299.00', 
    payment: 'Paid',
    status: 'Delivered',
    items: 1
  },
  { 
    id: '#ORD-7828', 
    customer: 'Sarah Smith', 
    date: '2023-10-24 12:15', 
    amount: '$199.00', 
    payment: 'Paid',
    status: 'Processing',
    items: 2
  },
  { 
    id: '#ORD-7827', 
    customer: 'Mike Johnson', 
    date: '2023-10-23 09:45', 
    amount: '$89.00', 
    payment: 'Pending',
    status: 'Shipped',
    items: 1
  },
  { 
    id: '#ORD-7826', 
    customer: 'Emily Brown', 
    date: '2023-10-22 18:20', 
    amount: '$45.00', 
    payment: 'Refunded',
    status: 'Cancelled',
    items: 3
  },
];

export default function OrderManagementPage() {
  return (
    <>
      {/* Action Bar */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
        <div className="flex items-center gap-4 w-full md:w-auto">
          <div className="relative flex-1 md:w-80">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input 
              type="text" 
              placeholder="Search by order ID or customer..." 
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
          <div className="flex-1 md:flex-none flex items-center bg-gray-50 p-1 rounded-xl border border-gray-200">
            <button className="px-4 py-1.5 bg-white text-blue-600 rounded-lg text-sm font-bold shadow-sm transition-all">All Orders</button>
            <button className="px-4 py-1.5 text-gray-500 rounded-lg text-sm font-bold hover:text-gray-900 transition-all">Pending</button>
            <button className="px-4 py-1.5 text-gray-500 rounded-lg text-sm font-bold hover:text-gray-900 transition-all">Fulfilled</button>
          </div>
        </div>
      </div>

      {/* Orders Table */}
      <div className="bg-white rounded-3xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="px-6 py-4 text-left w-10">
                  <input type="checkbox" className="rounded border-gray-200 text-blue-600 focus:ring-blue-600" />
                </th>
                <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Order ID</th>
                <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Customer</th>
                <th className="px-6 py-4 text-center text-xs font-bold text-gray-500 uppercase tracking-wider">Date</th>
                <th className="px-6 py-4 text-center text-xs font-bold text-gray-500 uppercase tracking-wider">Amount</th>
                <th className="px-6 py-4 text-center text-xs font-bold text-gray-500 uppercase tracking-wider">Payment</th>
                <th className="px-6 py-4 text-center text-xs font-bold text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-4 text-right text-xs font-bold text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {orders.map((order) => (
                <tr key={order.id} className="hover:bg-gray-50 transition-colors group">
                  <td className="px-6 py-4">
                    <input type="checkbox" className="rounded border-gray-200 text-blue-600 focus:ring-blue-600" />
                  </td>
                  <td className="px-6 py-4 text-sm font-bold text-gray-900">{order.id}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{order.customer}</td>
                  <td className="px-6 py-4 text-center text-xs text-gray-500">{order.date}</td>
                  <td className="px-6 py-4 text-center text-sm font-bold text-gray-900">{order.amount}</td>
                  <td className="px-6 py-4 text-center">
                    <span className={`inline-flex items-center px-3 py-1 rounded-full text-[11px] font-bold uppercase tracking-wider border ${
                      order.payment === 'Paid' ? 'bg-emerald-50 text-emerald-700 border-emerald-100' :
                      order.payment === 'Pending' ? 'bg-amber-50 text-amber-700 border-amber-100' :
                      'bg-red-50 text-red-700 border-red-100'
                    }`}>
                      {order.payment}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-center">
                    <div className="flex justify-center">
                      <span className={`flex items-center gap-1.5 px-3 py-1 rounded-full border text-[11px] font-bold uppercase tracking-wider ${
                        order.status === 'Delivered' ? 'bg-emerald-50 text-emerald-700 border-emerald-100' :
                        order.status === 'Processing' ? 'bg-blue-50 text-blue-700 border-blue-100' :
                        order.status === 'Shipped' ? 'bg-blue-50 text-blue-600 border-blue-100' :
                        'bg-gray-50 text-gray-500 border-gray-200'
                      }`}>
                        {order.status === 'Delivered' && <CheckCircle className="w-3.5 h-3.5" />}
                        {order.status === 'Processing' && <Clock className="w-3.5 h-3.5" />}
                        {order.status === 'Shipped' && <Truck className="w-3.5 h-3.5" />}
                        {order.status === 'Cancelled' && <XCircle className="w-3.5 h-3.5" />}
                        {order.status}
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex justify-end gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                        <Eye className="w-5 h-5" />
                      </button>
                      <button className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                        <ExternalLink className="w-5 h-5" />
                      </button>
                      <button className="p-2 text-gray-400 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors">
                        <MoreVertical className="w-5 h-5" />
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
            Showing <span className="font-bold text-gray-900">1-4</span> of <span className="font-bold text-gray-900">128</span> orders
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
