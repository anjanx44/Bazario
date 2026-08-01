'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Mail, Lock, Eye, EyeOff, ShieldCheck, ArrowRight } from 'lucide-react';

export default function AdminLoginPage() {
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      // TODO: integrate with NextAuth.js credentials provider — signIn('credentials', { email, password })
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="relative min-h-screen bg-[#070a13] flex flex-col items-center justify-center overflow-hidden px-4">

      {/* Background micro-grid overlay */}
      <div
        className="pointer-events-none absolute inset-0 opacity-[0.03]"
        style={{
          backgroundImage: 'radial-gradient(circle, #ffffff 1px, transparent 1px)',
          backgroundSize: '24px 24px',
        }}
      />

      {/* Ambient glow — top left */}
      <div className="pointer-events-none absolute -top-40 -left-40 w-[560px] h-[560px] rounded-full bg-indigo-600/10 blur-[120px] animate-pulse" />

      {/* Ambient glow — bottom right */}
      <div className="pointer-events-none absolute -bottom-40 -right-40 w-[600px] h-[600px] rounded-full bg-cyan-500/10 blur-[150px] animate-pulse delay-700" />

      {/* Glassmorphic auth card */}
      <div className="relative z-10 w-full max-w-md backdrop-blur-xl bg-white/[0.03] border border-white/[0.08] rounded-2xl p-8 shadow-[0_0_50px_rgba(0,0,0,0.5)] hover:border-cyan-500/30 transition-all duration-300">

        {/* Header section */}
        <div className="flex flex-col items-center text-center mb-8">
          {/* Shield badge */}
          <div className="flex items-center justify-center w-14 h-14 rounded-xl bg-cyan-500/10 border border-cyan-500/20 text-cyan-400 mb-5 shadow-[0_0_20px_rgba(6,182,212,0.15)]">
            <ShieldCheck className="w-7 h-7" />
          </div>

          {/* Title */}
          <h1 className="text-2xl font-bold leading-tight mb-2 bg-gradient-to-r from-cyan-400 via-indigo-400 to-purple-400 bg-clip-text text-transparent">
            Bazario Control Center
          </h1>

          {/* Subtitle */}
          <p className="text-[10px] font-semibold tracking-widest uppercase text-slate-500">
            Authorized Administrators Only
          </p>
        </div>

        {/* Credentials form */}
        <form onSubmit={handleSubmit} className="space-y-5">

          {/* Admin Email field */}
          <div className="space-y-1.5">
            <label htmlFor="email" className="block text-xs font-semibold text-slate-300 tracking-wide uppercase">
              Admin Email
            </label>
            <div className="relative">
              <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 pointer-events-none">
                <Mail className="w-4 h-4" />
              </span>
              <input
                id="email"
                type="email"
                autoComplete="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@bazario.com"
                className="w-full bg-black/40 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 rounded-xl pl-11 pr-4 py-3 text-sm text-slate-200 placeholder-slate-600 outline-none transition-all duration-200"
              />
            </div>
          </div>

          {/* Password field */}
          <div className="space-y-1.5">
            <div className="flex items-center justify-between">
              <label htmlFor="password" className="block text-xs font-semibold text-slate-300 tracking-wide uppercase">
                Password
              </label>
              <Link
                href="/admin/forgot-password"
                className="text-xs font-semibold text-cyan-400 hover:text-cyan-300 transition-colors"
              >
                Forgot Password?
              </Link>
            </div>
            <div className="relative">
              <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 pointer-events-none">
                <Lock className="w-4 h-4" />
              </span>
              <input
                id="password"
                type={showPassword ? 'text' : 'password'}
                autoComplete="current-password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••••••"
                className="w-full bg-black/40 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 rounded-xl pl-11 pr-12 py-3 text-sm text-slate-200 placeholder-slate-600 outline-none transition-all duration-200"
              />
              <button
                type="button"
                onClick={() => setShowPassword((prev) => !prev)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 hover:text-cyan-400 transition-colors"
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>

          {/* Authenticate CTA */}
          <button
            type="submit"
            disabled={isLoading}
            className="w-full flex items-center justify-center gap-2 py-3.5 rounded-xl font-semibold text-sm text-white bg-gradient-to-r from-cyan-500 to-indigo-600 hover:from-cyan-400 hover:to-indigo-500 hover:shadow-[0_0_30px_rgba(6,182,212,0.5)] transition-all duration-200 active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed disabled:active:scale-100"
          >
            {isLoading ? (
              <>
                <svg
                  className="w-4 h-4 animate-spin"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                </svg>
                Authenticating...
              </>
            ) : (
              <>
                Authenticate Access
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </form>

        {/* Security notice */}
        <div className="mt-6 flex items-start gap-3 rounded-xl bg-cyan-500/5 border border-cyan-500/10 px-4 py-3">
          <ShieldCheck className="w-4 h-4 text-cyan-500 mt-0.5 flex-shrink-0" />
          <p className="text-[11px] text-slate-500 leading-relaxed">
            This portal is restricted to authorized Bazario administrators. All access attempts are logged and monitored.
          </p>
        </div>
      </div>

      {/* Footer */}
      <div className="relative z-10 mt-8 flex flex-col items-center gap-3">
        {/* Security badge */}
        <div className="flex items-center gap-2 text-[10px] font-semibold tracking-[0.15em] uppercase text-slate-600">
          <Lock className="w-3 h-3 text-cyan-700" />
          <span>256-bit Encrypted Admin Session</span>
        </div>

        {/* Back to storefront */}
        <p className="text-sm text-slate-500">
          Not an admin?{' '}
          <Link
            href="/"
            className="font-semibold text-cyan-400 hover:text-cyan-300 transition-colors"
          >
            Return to Storefront
          </Link>
        </p>
      </div>
    </div>
  );
}
