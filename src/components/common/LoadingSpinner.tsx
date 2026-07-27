import React from 'react';
import { Loader2 } from 'lucide-react';

interface LoadingProps {
  fullScreen?: boolean;
  message?: string;
}

export const LoadingSpinner: React.FC<LoadingProps> = ({ 
  fullScreen = false, 
  message = 'Loading workspace state...' 
}) => {
  if (fullScreen) {
    return (
      <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex flex-col items-center justify-center text-slate-100">
        <div className="bg-slate-900 border border-slate-800 p-8 rounded-2xl shadow-2xl flex flex-col items-center gap-4 max-w-sm text-center">
          <div className="p-4 bg-indigo-500/10 rounded-full text-indigo-400 animate-pulse">
            <Loader2 className="w-10 h-10 animate-spin" />
          </div>
          <div>
            <h3 className="font-semibold text-lg text-slate-100">Tech Knife Enterprise</h3>
            <p className="text-xs text-slate-400 mt-1">{message}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center p-12 text-slate-500 dark:text-slate-400 gap-3">
      <Loader2 className="w-8 h-8 animate-spin text-indigo-600 dark:text-indigo-400" />
      <span className="text-xs font-medium tracking-wide uppercase">{message}</span>
    </div>
  );
};
