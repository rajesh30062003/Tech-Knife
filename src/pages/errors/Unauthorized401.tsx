import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Lock, LogIn, ArrowLeft } from 'lucide-react';
import { motion } from 'motion/react';

export const Unauthorized401: React.FC = () => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const isExpired = searchParams.get('session_expired') === 'true';

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col items-center justify-center p-6 text-center">
      <motion.div
        initial={{ scale: 0.8, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 0.3 }}
        className="p-4 bg-amber-500/10 text-amber-400 rounded-3xl mb-6 ring-1 ring-amber-500/20"
      >
        <Lock className="w-16 h-16" />
      </motion.div>
      <span className="text-xs font-mono font-bold tracking-widest text-amber-400 uppercase mb-2">
        Error 401: Unauthorized
      </span>
      <h1 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight mb-4">
        {isExpired ? 'Session Expired' : 'Authentication Required'}
      </h1>
      <p className="text-slate-400 max-w-lg text-sm mb-8 leading-relaxed">
        {isExpired
          ? 'Your security session has expired or your access token was revoked. Please log in with your official email credentials to resume.'
          : 'You must be authenticated with valid enterprise credentials to access this protected module.'}
      </p>

      <div className="flex flex-wrap items-center justify-center gap-4">
        <Link
          to="/login"
          className="inline-flex items-center gap-2 px-6 py-3 text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-500 rounded-xl transition-all shadow-lg shadow-indigo-600/25"
        >
          <LogIn className="w-4 h-4" /> Go to Login Page
        </Link>
        <Link
          to="/"
          className="inline-flex items-center gap-2 px-6 py-3 text-sm font-semibold text-slate-300 bg-slate-800 hover:bg-slate-700 rounded-xl transition-all border border-slate-700"
        >
          <ArrowLeft className="w-4 h-4" /> Home Page
        </Link>
      </div>
    </div>
  );
};
