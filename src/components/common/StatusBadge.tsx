import React from 'react';
import { normalizeProjectStatus, getStatusLabel } from '../../constants/projectStatus';

interface StatusBadgeProps {
  status?: string | null;
  size?: 'sm' | 'md';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, size = 'sm' }) => {
  const normalized = normalizeProjectStatus(status);
  const label = getStatusLabel(status);

  const getColors = (val: string) => {
    switch (val) {
      case 'COMPLETED':
      case 'LIVE':
      case 'MAINTENANCE':
        return 'bg-emerald-50 dark:bg-emerald-950/50 text-emerald-700 dark:text-emerald-400 border-emerald-200 dark:border-emerald-800';
      case 'BACKEND_DEV':
      case 'FRONTEND_DEV':
      case 'FULLSTACK_DEV':
      case 'API_INTEGRATION':
        return 'bg-cyan-50 dark:bg-cyan-950/50 text-cyan-700 dark:text-cyan-400 border-cyan-200 dark:border-cyan-800';
      case 'PLANNED':
      case 'REQ_GATHERING':
      case 'DESIGN':
        return 'bg-amber-50 dark:bg-amber-950/50 text-amber-700 dark:text-amber-400 border-amber-200 dark:border-amber-800';
      case 'TESTING':
      case 'QA':
      case 'UAT':
      case 'DEPLOYMENT':
        return 'bg-purple-50 dark:bg-purple-950/50 text-purple-700 dark:text-purple-400 border-purple-200 dark:border-purple-800';
      case 'CANCELLED':
      case 'BLOCKED':
        return 'bg-rose-50 dark:bg-rose-950/50 text-rose-700 dark:text-rose-400 border-rose-200 dark:border-rose-800';
      case 'ON_HOLD':
      default:
        return 'bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border-slate-200 dark:border-slate-700';
    }
  };

  const sizeClasses = size === 'sm' 
    ? 'px-2 py-0.5 text-xs font-bold' 
    : 'px-2.5 py-1 text-sm font-black';

  return (
    <span className={`inline-flex items-center rounded-full border ${getColors(normalized)} ${sizeClasses}`}>
      <span className="w-1.5 h-1.5 rounded-full bg-current mr-1.5 opacity-80 animate-pulse"></span>
      {label}
    </span>
  );
};
