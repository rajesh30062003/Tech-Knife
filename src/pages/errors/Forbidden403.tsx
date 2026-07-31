import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldAlert, Home } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const Forbidden403: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col items-center justify-center p-6 text-center">
      <div className="p-4 bg-rose-500/10 text-rose-400 rounded-3xl mb-6 ring-1 ring-rose-500/20">
        <ShieldAlert className="w-16 h-16" />
      </div>
      <span className="text-xs font-mono font-bold tracking-widest text-rose-400 uppercase mb-2">Error 403: Access Restricted</span>
      <h1 className="text-4xl sm:text-5xl font-extrabold text-white tracking-tight mb-4">
        Insufficient Authority Level
      </h1>
      <p className="text-slate-400 max-w-lg text-sm mb-6 leading-relaxed">
        Your current account role (<span className="text-indigo-400 font-bold">{user?.role || 'UNAUTHENTICATED'}</span>) is not authorized to access this module. Please contact your Enterprise Administrator if you require elevated privileges.
      </p>

      <Link
        to="/dashboard"
        className="inline-flex items-center gap-2 px-5 py-2.5 text-xs font-semibold text-white bg-slate-800 hover:bg-slate-700 rounded-xl transition-all"
      >
        <Home className="w-4 h-4" /> Return to Dashboard
      </Link>
    </div>
  );
};
