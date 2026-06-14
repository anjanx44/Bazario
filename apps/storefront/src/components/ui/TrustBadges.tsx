import React from 'react';
import { Truck, Shield, Headphones } from 'lucide-react';

export const TrustBadges = () => {
  const badges = [
    {
      icon: <Truck className="w-6 h-6" />,
      title: 'Free Shipping',
      description: 'On orders over $50'
    },
    {
      icon: <Shield className="w-6 h-6" />,
      title: 'Secure Payments',
      description: 'SSL Encrypted Checkout'
    },
    {
      icon: <Headphones className="w-6 h-6" />,
      title: '24/7 Support',
      description: 'Expert help anytime'
    }
  ];

  return (
    <section className="bg-gray-50 py-12">
      <div className="max-w-7xl mx-auto px-4 md:px-12 grid grid-cols-1 md:grid-cols-3 gap-6">
        {badges.map((badge, index) => (
          <div key={index} className="flex items-center gap-4 p-6 border border-gray-200 rounded-lg bg-white shadow-sm hover:shadow-md transition-shadow">
            <div className="bg-blue-100 p-3 rounded-full text-blue-700">
              {badge.icon}
            </div>
            <div>
              <p className="font-bold text-gray-900">{badge.title}</p>
              <p className="text-sm font-semibold text-gray-500">{badge.description}</p>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};
