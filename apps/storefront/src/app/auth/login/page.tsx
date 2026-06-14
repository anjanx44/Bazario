'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Mail, Lock, Eye, EyeOff, ShoppingBag } from 'lucide-react';

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [rememberMe, setRememberMe] = useState<boolean>(false);
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    // TODO: integrate with NextAuth.js credentials provider
  };

  const handleGoogleSignIn = () => {
    // TODO: integrate with NextAuth.js Google provider — signIn('google')
  };

  return (
    <div className="relative min-h-screen bg-[#090d16] flex flex-col items-center justify-center overflow-hidden px-4">

      {/* Background grid overlay */}
      <div
        className="pointer-events-none absolute inset-0"
        style={{
          backgroundImage:
            'linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px)',
          backgroundSize: '48px 48px',
        }}
      />

      {/* Ambient glow — top right */}
      <div className="pointer-events-none absolute -top-32 -right-32 w-[520px] h-[520px] rounded-full bg-cyan-600/10 blur-[130px] animate-pulse" />

      {/* Ambient glow — bottom left */}
      <div className="pointer-events-none absolute -bottom-32 -left-32 w-[480px] h-[480px] rounded-full bg-purple-600/10 blur-[120px] animate-pulse delay-500" />

      {/* Brand logo */}
      <div className="relative z-10 flex items-center gap-3 mb-8">
        <div className="flex items-center justify-center w-11 h-11 rounded-2xl bg-gradient-to-br from-cyan-500 to-indigo-600 shadow-[0_0_20px_rgba(34,211,238,0.35)]">
          <ShoppingBag className="w-5 h-5 text-white" />
        </div>
        <span className="text-2xl font-bold tracking-tight bg-gradient-to-r from-cyan-400 to-indigo-400 bg-clip-text text-transparent">
          Bazario
        </span>
      </div>

      {/* Glassmorphic auth card */}
      <div className="relative z-10 w-full max-w-md backdrop-blur-xl bg-white/[0.02] border border-white/[0.06] rounded-2xl p-8 shadow-[0_0_50px_rgba(0,0,0,0.4)]">

        {/* Welcome header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold leading-tight mb-3 bg-gradient-to-r from-cyan-400 via-indigo-400 to-purple-400 bg-clip-text text-transparent">
            Welcome back to Bazario
          </h1>
          <p className="text-slate-400 text-sm">
            Enter your credentials to access the cockpit.
          </p>
        </div>

        {/* Credentials form */}
        <form onSubmit={handleSubmit} className="space-y-5">

          {/* Email field */}
          <div className="space-y-1.5">
            <label htmlFor="email" className="block text-xs font-semibold text-slate-300 tracking-wide uppercase">
              Email Address
            </label>
            <div className="relative">
              <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500">
                <Mail className="w-4 h-4" />
              </span>
              <input
                id="email"
                type="email"
                autoComplete="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@company.com"
                className="w-full bg-black/30 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 rounded-xl pl-11 pr-4 py-3 text-sm text-slate-200 placeholder-slate-600 outline-none transition-all duration-200"
              />
            </div>
          </div>

          {/* Password field */}
          <div className="space-y-1.5">
            <div className="flex items-center justify-between">
              <label htmlFor="password" className="block text-xs font-semibold text-slate-300 tracking-wide uppercase">
                Security Key
              </label>
              <Link
                href="/auth/forgot-password"
                className="text-xs font-semibold text-cyan-400 hover:text-cyan-300 transition-colors"
              >
                Forgot Password?
              </Link>
            </div>
            <div className="relative">
              <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500">
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
                className="w-full bg-black/30 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 rounded-xl pl-11 pr-12 py-3 text-sm text-slate-200 placeholder-slate-600 outline-none transition-all duration-200"
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

          {/* Remember me toggle */}
          <div className="flex items-center gap-3">
            <button
              type="button"
              role="switch"
              aria-checked={rememberMe}
              onClick={() => setRememberMe((prev) => !prev)}
              className={`relative inline-flex h-5 w-9 flex-shrink-0 cursor-pointer rounded-full border border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                rememberMe ? 'bg-cyan-500' : 'bg-slate-700'
              }`}
            >
              <span
                className={`pointer-events-none inline-block h-4 w-4 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out mt-0.5 ${
                  rememberMe ? 'translate-x-4' : 'translate-x-0.5'
                }`}
              />
            </button>
            <span className="text-sm text-slate-400">Stay signed in for 30 days</span>
          </div>

          {/* Sign In CTA */}
          <button
            type="submit"
            className="w-full flex items-center justify-center gap-2 py-3.5 rounded-xl font-semibold text-sm text-white bg-gradient-to-r from-cyan-500 to-indigo-600 hover:from-cyan-400 hover:to-indigo-500 hover:shadow-[0_0_30px_rgba(34,211,238,0.35)] transition-all duration-200 active:scale-[0.98]"
          >
            Sign In to Account
            <span aria-hidden="true">→</span>
          </button>
        </form>

        {/* Divider */}
        <div className="flex items-center gap-3 my-6">
          <div className="flex-1 h-px bg-white/[0.06]" />
          <span className="text-[10px] font-semibold tracking-[0.15em] uppercase text-slate-600">
            or secure login via
          </span>
          <div className="flex-1 h-px bg-white/[0.06]" />
        </div>

        {/* Google Sign In */}
        <button
          type="button"
          onClick={handleGoogleSignIn}
          className="w-full flex items-center justify-center gap-3 py-3.5 rounded-xl border border-white/[0.08] bg-white/[0.03] hover:bg-white/[0.06] hover:border-white/[0.12] text-sm font-medium text-slate-200 transition-all duration-200 active:scale-[0.98]"
        >
          {/* Google colorful SVG icon */}
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
            <path d="M17.64 9.20455C17.64 8.56636 17.5827 7.95273 17.4764 7.36364H9V10.845H13.8436C13.635 11.97 13.0009 12.9232 12.0477 13.5614V15.8195H14.9564C16.6582 14.2527 17.64 11.9455 17.64 9.20455Z" fill="#4285F4"/>
            <path d="M9 18C11.43 18 13.4673 17.1941 14.9564 15.8195L12.0477 13.5614C11.2418 14.1014 10.2109 14.4204 9 14.4204C6.65591 14.4204 4.67182 12.8373 3.96409 10.71H0.957275V13.0418C2.43818 15.9832 5.48182 18 9 18Z" fill="#34A853"/>
            <path d="M3.96409 10.71C3.78409 10.17 3.68182 9.59318 3.68182 9C3.68182 8.40682 3.78409 7.83 3.96409 7.29V4.95818H0.957275C0.347727 6.17318 0 7.54773 0 9C0 10.4523 0.347727 11.8268 0.957275 13.0418L3.96409 10.71Z" fill="#FBBC05"/>
            <path d="M9 3.57955C10.3214 3.57955 11.5077 4.03364 12.4405 4.92545L15.0218 2.34409C13.4632 0.891818 11.4259 0 9 0C5.48182 0 2.43818 2.01682 0.957275 4.95818L3.96409 7.29C4.67182 5.16273 6.65591 3.57955 9 3.57955Z" fill="#EA4335"/>
          </svg>
          Sign In with Google
        </button>
      </div>

      {/* Footer */}
      <div className="relative z-10 mt-8 flex flex-col items-center gap-3">
        {/* Security badge */}
        <div className="flex items-center gap-2 text-[10px] font-semibold tracking-[0.15em] uppercase text-slate-600">
          <Lock className="w-3 h-3 text-cyan-700" />
          <span>Quantum Encrypted Session</span>
        </div>

        {/* Register link */}
        <p className="text-sm text-slate-500">
          New to Bazario?{' '}
          <Link
            href="/auth/register"
            className="font-semibold text-cyan-400 hover:text-cyan-300 transition-colors"
          >
            Create secure account
          </Link>
        </p>
      </div>
    </div>
  );
}
