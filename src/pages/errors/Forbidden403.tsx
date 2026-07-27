import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldAlert, Shield, Home } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const Forbidden403: React.FC = () => {
  const { user, switchRole } = useAuth();

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col items-center justify-center p-6 text-center">
      <div className="p-4 bg-rose-500/10 text-rose-400 rounded-3xl mb-6 ring-1 ring-rose-500/20">
        <ShieldAlert className="w-16 h-16 animate-bounce" />
      </div>
      <span className="text-xs font-mono font-bold tracking-widest text-rose-400 uppercase mb-2">Error 403: Forbidden</span>
      <h1 className="text-4xl sm:text-5xl font-extrabold text-white tracking-tight mb-4">
        Insufficient Authority Level
      </h1>
      <p className="text-slate-400 max-w-lg text-sm mb-6 leading-relaxed">
        Your current role authority level (<span className="text-indigo-400 font-bold">{user?.role || 'UNAUTHENTICATED'}</span>) does not possess permission to access this protected enterprise route.
      </p>

      {/* Quick Role Switcher Tip */}
      <div className="bg-slate-800/80 border border-slate-700/80 p-4 rounded-2xl max-w-md w-full mb-8 text-xs text-left">
        <div className="flex items-center gap-2 font-bold text-amber-400 mb-2">
          <Shield className="w-4 h-4" />
          <span>Interactive Role Simulator Tip</span>
        </div>
        <p className="text-slate-300 mb-3">
          Use the top Navbar Role dropdown to switch to <span className="text-white font-semibold">SUPER_ADMIN</span> or <span className="text-white font-semibold">ADMIN</span> to preview this module!
        </p>
        <button
          onClick={() => switchRole('ROLE_SUPER_ADMIN')}
          className="w-full py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-lg text-center transition-colors"
        >
          Switch to Super Admin Role
        </button>
      </div>

      <Link
        to="/dashboard"
        className="inline-flex items-center gap-2 px-5 py-2.5 text-xs font-semibold text-white bg-slate-800 hover:bg-slate-700 rounded-xl transition-all"
      >
        <Home className="w-4 h-4" /> Back to Safety
      </Link>
    </div>
  );
};
