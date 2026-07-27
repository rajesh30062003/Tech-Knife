import React from 'react';

type StatusType = 
  | 'Completed' | 'In Progress' | 'Pending' | 'Planning' | 'On Time' 
  | 'Approved' | 'Rejected' | 'Active' | 'Critical' | 'Urgent'
  | 'High' | 'Medium' | 'Low' | 'Open' | 'Resolved' | 'Disbursed';

interface StatusBadgeProps {
  status: StatusType | string;
  size?: 'sm' | 'md';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, size = 'sm' }) => {
  const getColors = (val: string) => {
    switch (val.toLowerCase()) {
      case 'completed':
      case 'approved':
      case 'on time':
      case 'active':
      case 'resolved':
      case 'disbursed':
        return 'bg-emerald-50 dark:bg-emerald-950/50 text-emerald-700 dark:text-emerald-400 border-emerald-200 dark:border-emerald-800';
      case 'in progress':
      case 'open':
        return 'bg-blue-50 dark:bg-blue-950/50 text-blue-700 dark:text-blue-400 border-blue-200 dark:border-blue-800';
      case 'pending':
      case 'planning':
      case 'medium':
        return 'bg-amber-50 dark:bg-amber-950/50 text-amber-700 dark:text-amber-400 border-amber-200 dark:border-amber-800';
      case 'rejected':
      case 'critical':
      case 'urgent':
      case 'high':
        return 'bg-rose-50 dark:bg-rose-950/50 text-rose-700 dark:text-rose-400 border-rose-200 dark:border-rose-800';
      case 'low':
      default:
        return 'bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border-slate-200 dark:border-slate-700';
    }
  };

  const sizeClasses = size === 'sm' 
    ? 'px-2 py-0.5 text-xs font-medium' 
    : 'px-2.5 py-1 text-sm font-semibold';

  return (
    <span className={`inline-flex items-center rounded-full border ${getColors(status)} ${sizeClasses}`}>
      <span className="w-1.5 h-1.5 rounded-full bg-current mr-1.5 opacity-75"></span>
      {status}
    </span>
  );
};
