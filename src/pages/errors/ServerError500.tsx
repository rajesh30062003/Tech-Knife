import React from 'react';
import { Link } from 'react-router-dom';
import { ServerCrash, RefreshCw, Home } from 'lucide-react';

export const ServerError500: React.FC = () => {
  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col items-center justify-center p-6 text-center">
      <div className="p-4 bg-amber-500/10 text-amber-400 rounded-3xl mb-6 ring-1 ring-amber-500/20">
        <ServerCrash className="w-16 h-16 animate-pulse" />
      </div>
      <span className="text-xs font-mono font-bold tracking-widest text-amber-400 uppercase mb-2">Error 500: Internal Server Failure</span>
      <h1 className="text-4xl sm:text-5xl font-extrabold text-white tracking-tight mb-4">
        Enterprise Service Exception
      </h1>
      <p className="text-slate-400 max-w-md text-sm mb-8 leading-relaxed">
        An unhandled internal exception was caught by the Spring Boot GlobalExceptionHandler. System telemetry logs have recorded this incident.
      </p>
      <div className="flex flex-wrap items-center justify-center gap-4">
        <button
          onClick={() => window.location.reload()}
          className="inline-flex items-center gap-2 px-5 py-2.5 text-xs font-semibold text-slate-300 bg-slate-800 hover:bg-slate-700 rounded-xl transition-all"
        >
          <RefreshCw className="w-4 h-4" /> Retry Connection
        </button>
        <Link
          to="/dashboard"
          className="inline-flex items-center gap-2 px-5 py-2.5 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-500 rounded-xl transition-all shadow-lg shadow-indigo-600/30"
        >
          <Home className="w-4 h-4" /> Return to Dashboard
        </Link>
      </div>
    </div>
  );
};
