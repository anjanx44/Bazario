import React from 'react';
import { 
  TrendingUp, 
  Users, 
  ShoppingBag, 
  DollarSign,
  ArrowUpRight,
  ArrowDownRight,
  ChevronRight,
  Download,
  Calendar,
  RefreshCw,
  Clock,
  CheckCircle2,
  Package,
  AlertCircle,
  Search,
  ChevronLeft
} from 'lucide-react';
import Image from 'next/image';

const stats = [
  { 
    name: 'Total Revenue', 
    value: '$124,592.00', 
    change: '+12.5%', 
    trend: 'up', 
    icon: DollarSign, 
    color: 'primary' 
  },
  { 
    name: 'Active Orders', 
    value: '452', 
    change: '+18.2%', 
    trend: 'up', 
    icon: ShoppingBag, 
    color: 'secondary' 
  },
  { 
    name: 'Total Customers', 
    value: '8,902', 
    change: '+8.1%', 
    trend: 'up', 
    icon: Users, 
    color: 'tertiary' 
  },
  { 
    name: 'Conversion Rate', 
    value: '3.24%', 
    change: '-2.4%', 
    trend: 'down', 
    icon: TrendingUp, 
    color: 'error' 
  },
];

const operationalAlerts = [
  { 
    id: 1, 
    title: 'Low Stock Alert', 
    description: '15 items are below threshold', 
    type: 'warning', 
    icon: AlertCircle, 
    time: '5 mins ago' 
  },
  { 
    id: 2, 
    title: 'System Update', 
    description: 'Database maintenance scheduled', 
    type: 'info', 
    icon: RefreshCw, 
    time: '1 hour ago' 
  },
  { 
    id: 3, 
    title: 'New Customer', 
    description: 'Sarah Smith joined Bazario', 
    type: 'success', 
    icon: CheckCircle2, 
    time: '2 hours ago' 
  }
];

const recentOrders = [
  { id: '#ORD-7829', customer: 'John Doe', product: 'AeroSound Elite 5', amount: '$299.00', status: 'Delivered', date: '2 mins ago' },
  { id: '#ORD-7828', customer: 'Sarah Smith', product: 'Smart Watch Pro', amount: '$199.00', status: 'Processing', date: '15 mins ago' },
  { id: '#ORD-7827', customer: 'Mike Johnson', product: 'Leather Laptop Bag', amount: '$89.00', status: 'Shipped', date: '1 hour ago' },
  { id: '#ORD-7826', customer: 'Emily Brown', product: 'Wireless Mouse', amount: '$45.00', status: 'Delivered', date: '3 hours ago' },
];

export default function DashboardPage() {
  return (
    <>
      {/* Header Section */}
      <div className="flex flex-col md:flex-row md:items-end justify-between mb-8 gap-4">
        <div>
          <h2 className="text-3xl font-bold text-gray-900">Dashboard Overview</h2>
          <p className="text-lg text-gray-500 mt-1">Real-time performance metrics and operational status.</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 rounded-full text-sm font-bold text-gray-700 hover:bg-gray-50 transition-colors shadow-sm">
            <Calendar className="w-4 h-4" />
            <span>June 2026</span>
          </button>
          <button className="p-2.5 bg-blue-600 text-white rounded-full shadow-lg shadow-blue-600/20 hover:opacity-90 active:scale-95 transition-all">
            <RefreshCw className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat) => (
          <div key={stat.name} className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm hover:shadow-md transition-all group">
            <div className="flex justify-between items-start mb-4">
              <div className="p-3 rounded-xl bg-blue-50 text-blue-600 group-hover:bg-blue-600 group-hover:text-white transition-colors">
                <stat.icon className="w-6 h-6" />
              </div>
              <div className={`flex items-center gap-1 text-xs font-bold ${stat.trend === 'up' ? 'text-emerald-600' : 'text-red-600'}`}>
                {stat.trend === 'up' ? <ArrowUpRight className="w-4 h-4" /> : <ArrowDownRight className="w-4 h-4" />}
                {stat.change}
              </div>
            </div>
            <h3 className="text-gray-500 text-xs font-bold uppercase tracking-wider mb-1">{stat.name}</h3>
            <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Analytics Section */}
        <div className="lg:col-span-2 space-y-8">
          {/* Revenue Chart Placeholder */}
          <div className="bg-white p-8 rounded-3xl border border-gray-200 shadow-sm">
            <div className="flex justify-between items-center mb-8">
              <div>
                <h2 className="text-xl font-bold text-gray-900">Revenue Analytics</h2>
                <p className="text-sm text-gray-500">Monthly sales performance against target</p>
              </div>
              <div className="flex gap-2">
                <button className="px-4 py-1.5 bg-blue-600 text-white rounded-lg text-xs font-bold">Sales</button>
                <button className="px-4 py-1.5 bg-white border border-gray-200 rounded-lg text-xs font-bold text-gray-500 hover:bg-gray-50 transition-colors">Orders</button>
              </div>
            </div>
            
            <div className="h-[300px] w-full bg-gray-50 rounded-2xl flex items-end justify-between px-8 pb-4 gap-4">
              {[40, 70, 45, 90, 65, 85, 55, 75, 50, 95, 60, 80].map((height, i) => (
                <div key={i} className="flex-1 bg-blue-600/20 hover:bg-blue-600 rounded-t-lg transition-all relative group cursor-pointer" style={{ height: `${height}%` }}>
                  <div className="absolute -top-10 left-1/2 -translate-x-1/2 bg-gray-900 text-white text-[10px] py-1 px-2 rounded opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
                    ${(height * 100).toLocaleString()}
                  </div>
                </div>
              ))}
            </div>
            <div className="flex justify-between mt-4 px-2 text-[10px] text-gray-400 uppercase tracking-widest font-bold">
              <span>Jan</span><span>Feb</span><span>Mar</span><span>Apr</span><span>May</span><span>Jun</span>
              <span>Jul</span><span>Aug</span><span>Sep</span><span>Oct</span><span>Nov</span><span>Dec</span>
            </div>
          </div>

          {/* Recent Orders Table */}
          <div className="bg-white rounded-3xl border border-gray-200 shadow-sm overflow-hidden">
            <div className="p-6 border-b border-gray-200 flex justify-between items-center">
              <h2 className="text-xl font-bold text-gray-900">Recent Orders</h2>
              <button className="text-blue-600 text-sm font-bold hover:underline">View All</button>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Order ID</th>
                    <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Customer</th>
                    <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Product</th>
                    <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Amount</th>
                    <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider text-right">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {recentOrders.map((order) => (
                    <tr key={order.id} className="hover:bg-gray-50 transition-colors group">
                      <td className="px-6 py-4 text-sm font-bold text-blue-600">{order.id}</td>
                      <td className="px-6 py-4 text-sm text-gray-900">{order.customer}</td>
                      <td className="px-6 py-4 text-sm text-gray-500">{order.product}</td>
                      <td className="px-6 py-4 text-sm font-bold text-gray-900">{order.amount}</td>
                      <td className="px-6 py-4 text-right">
                        <span className={`inline-flex px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${
                          order.status === 'Delivered' ? 'bg-emerald-100 text-emerald-700' : 
                          order.status === 'Processing' ? 'bg-amber-100 text-amber-700' : 'bg-blue-100 text-blue-700'
                        }`}>
                          {order.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Sidebar Analytics */}
        <div className="space-y-8">
          {/* Operational Alerts */}
          <div className="bg-white p-8 rounded-3xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold text-gray-900">Operational Alerts</h2>
              <span className="bg-red-100 text-red-700 text-[10px] font-bold px-2 py-0.5 rounded-full">3 New</span>
            </div>
            <div className="space-y-6">
              {operationalAlerts.map((alert) => (
                <div key={alert.id} className="flex gap-4 p-4 rounded-2xl hover:bg-gray-50 transition-colors cursor-pointer group">
                  <div className={`p-2.5 rounded-xl h-fit ${
                    alert.type === 'warning' ? 'bg-amber-100 text-amber-600' :
                    alert.type === 'info' ? 'bg-blue-100 text-blue-600' : 'bg-emerald-100 text-emerald-600'
                  }`}>
                    <alert.icon className="w-5 h-5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-bold text-gray-900 truncate group-hover:text-blue-600 transition-colors">{alert.title}</p>
                    <p className="text-sm text-gray-500 mb-2">{alert.description}</p>
                    <div className="flex items-center gap-2 text-[10px] text-gray-400">
                      <Clock className="w-3 h-3" />
                      <span>{alert.time}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
            <button className="w-full mt-6 py-3 border-2 border-gray-100 rounded-xl text-sm font-bold text-gray-500 hover:border-blue-600 hover:text-blue-600 transition-all">
              Check Maintenance Status
            </button>
          </div>

          {/* Activity Log */}
          <div className="bg-white p-8 rounded-3xl border border-gray-200 shadow-sm">
            <h2 className="text-xl font-bold text-gray-900 mb-6">Activity Log</h2>
            <div className="space-y-6">
              {[1, 2, 3, 4].map((i) => (
                <div key={i} className="flex gap-4 relative">
                  {i !== 4 && <div className="absolute left-[19px] top-10 bottom-[-24px] w-[2px] bg-gray-100"></div>}
                  <div className="w-10 h-10 rounded-full border-2 border-gray-100 bg-white flex items-center justify-center z-10">
                    <div className="w-2 h-2 rounded-full bg-blue-600"></div>
                  </div>
                  <div>
                    <p className="text-sm font-bold text-gray-900">System Backup Completed</p>
                    <p className="text-sm text-gray-500">Automated daily snapshot at 03:00 AM</p>
                    <p className="text-[10px] text-gray-400 mt-1 uppercase font-bold tracking-tighter">03:00 AM • June 14, 2026</p>
                  </div>
                </div>
              ))}
            </div>
            <button className="w-full mt-8 flex items-center justify-center gap-2 text-blue-600 text-sm font-bold hover:underline">
              <span>Download Full Log</span>
              <Download className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
