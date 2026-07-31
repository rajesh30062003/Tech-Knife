import React from 'react';

type StatusType = 
  | 'Completed' | 'In Progress' | 'Pending' | 'Planning' | 'On Time' 
  | 'Approved' | 'Rejected' | 'Active' | 'Critical' | 'Urgent'
  | 'High' | 'Medium' | 'Low' | 'Open' | 'Resolved' | 'Disbursed'
  | 'PLANNED' | 'REQUIREMENT_GATHERING' | 'DESIGN' | 'BACKEND_DEVELOPMENT'
  | 'FRONTEND_DEVELOPMENT' | 'FULLSTACK_DEVELOPMENT' | 'API_INTEGRATION'
  | 'IN_PROGRESS' | 'CODE_REVIEW' | 'TESTING' | 'QA' | 'UAT' | 'DEPLOYMENT'
  | 'LIVE' | 'MAINTENANCE' | 'ON_HOLD' | 'BLOCKED' | 'COMPLETED' | 'ARCHIVED' | 'CANCELLED';

interface StatusBadgeProps {
  status?: StatusType | string | null;
  size?: 'sm' | 'md';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, size = 'sm' }) => {
  const formatLabel = (raw?: string | null) => {
    if (!raw) return '-';
    const clean = String(raw).replace(/_/g, ' ').toLowerCase();
    return clean.charAt(0).toUpperCase() + clean.slice(1);
  };

  const getColors = (val?: string | null) => {
    const normalized = val ? String(val).replace(/_/g, ' ').toLowerCase() : '';
    switch (normalized) {
      case 'completed':
      case 'approved':
      case 'on time':
      case 'active':
      case 'resolved':
      case 'disbursed':
      case 'live':
        return 'bg-emerald-50 dark:bg-emerald-950/50 text-emerald-700 dark:text-emerald-400 border-emerald-200 dark:border-emerald-800';
      case 'in progress':
      case 'open':
      case 'development':
      case 'backend development':
      case 'frontend development':
      case 'fullstack development':
      case 'api integration':
        return 'bg-blue-50 dark:bg-blue-950/50 text-blue-700 dark:text-blue-400 border-blue-200 dark:border-blue-800';
      case 'pending':
      case 'planning':
      case 'planned':
      case 'medium':
      case 'design':
      case 'requirement gathering':
        return 'bg-amber-50 dark:bg-amber-950/50 text-amber-700 dark:text-amber-400 border-amber-200 dark:border-amber-800';
      case 'testing':
      case 'qa':
      case 'code review':
      case 'uat':
      case 'deployment':
        return 'bg-purple-50 dark:bg-purple-950/50 text-purple-700 dark:text-purple-400 border-purple-200 dark:border-purple-800';
      case 'rejected':
      case 'critical':
      case 'urgent':
      case 'high':
      case 'blocked':
      case 'cancelled':
        return 'bg-rose-50 dark:bg-rose-950/50 text-rose-700 dark:text-rose-400 border-rose-200 dark:border-rose-800';
      case 'low':
      case 'on hold':
      case 'maintenance':
      case 'archived':
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
      {formatLabel(status)}
    </span>
  );
};
