import React from 'react';
import { Sparkles, Activity } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="mt-auto border-t border-slate-200 dark:border-slate-800 bg-white/50 dark:bg-slate-900/50 py-4 px-6 text-xs text-slate-500 dark:text-slate-400">
      <div className="flex flex-col sm:flex-row items-center justify-between gap-3 max-w-7xl mx-auto">
        
        <div className="flex items-center gap-2">
          <div className="p-1 bg-indigo-600 text-white rounded">
            <Sparkles className="w-3 h-3" />
          </div>
          <span className="font-semibold text-slate-700 dark:text-slate-300">
            Tech Knife Enterprise Management System
          </span>
          <span className="text-[10px] px-1.5 py-0.5 rounded bg-slate-100 dark:bg-slate-800 font-mono">
            v1.0.0-UI
          </span>
        </div>

        <div className="flex items-center gap-4">
          <div className="flex items-center gap-1.5 text-emerald-600 dark:text-emerald-400 font-medium text-[11px]">
            <Activity className="w-3 h-3 animate-pulse" />
            <span>Core Engines Operational</span>
          </div>
          <span>&copy; {new Date().getFullYear()} Tech Knife Inc.</span>
        </div>

      </div>
    </footer>
  );
};
