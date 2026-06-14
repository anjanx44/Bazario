'use client';

import React, { useState, useEffect } from 'react';
import { Zap } from 'lucide-react';

export const FlashSaleStrip = () => {
  const [timeLeft, setTimeLeft] = useState({
    hours: 2,
    minutes: 45,
    seconds: 12
  });

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(prev => {
        let { hours, minutes, seconds } = prev;
        seconds--;
        if (seconds < 0) {
          seconds = 59;
          minutes--;
          if (minutes < 0) {
            minutes = 59;
            hours--;
            if (hours < 0) {
              hours = 0;
              minutes = 0;
              seconds = 0;
              clearInterval(timer);
            }
          }
        }
        return { hours, minutes, seconds };
      });
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const pad = (num: number) => num.toString().padStart(2, '0');

  return (
    <section className="w-full bg-blue-600 text-white py-4">
      <div className="max-w-7xl mx-auto px-4 md:px-12 flex flex-col md:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-2">
          <Zap className="w-6 h-6 text-orange-400 fill-orange-400" />
          <span className="text-2xl font-bold uppercase tracking-tighter italic">Flash Deals</span>
        </div>
        
        <div className="flex items-center gap-4">
          <span className="text-sm font-bold opacity-80">Ending In:</span>
          <div className="flex gap-2 font-mono font-bold text-2xl">
            <span className="bg-black/10 px-2 py-1 rounded">{pad(timeLeft.hours)}</span>
            <span className="opacity-50 self-center">:</span>
            <span className="bg-black/10 px-2 py-1 rounded">{pad(timeLeft.minutes)}</span>
            <span className="opacity-50 self-center">:</span>
            <span className="bg-black/10 px-2 py-1 rounded">{pad(timeLeft.seconds)}</span>
          </div>
        </div>
        
        <button className="text-sm font-bold bg-white text-blue-600 px-6 py-2 rounded-full hover:bg-gray-100 transition-colors">
          View All Deals
        </button>
      </div>
    </section>
  );
};
