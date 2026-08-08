import React, { useState } from 'react';
import {
  Bell,
  Shield,
  DollarSign,
  Calendar,
  FolderKanban,
  Check,
  Trash2,
  CheckCircle2,
} from 'lucide-react';

interface NotificationItem {
  id: string;
  category: 'security' | 'payroll' | 'leave' | 'projects';
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
}

const MOCK_NOTIFICATIONS: NotificationItem[] = [
  {
    id: 'notif-1',
    category: 'leave',
    title: 'Leave Application Approved',
    message: 'Your time-off application for Nov 12 - Nov 15 was approved by Sourav Roy.',
    timestamp: '10 minutes ago',
    read: false,
  },
  {
    id: 'notif-2',
    category: 'security',
    title: 'Security Alert: New Session Login',
    message: 'A new session was authenticated from IP 192.168.1.45 (macOS Chrome).',
    timestamp: '1 hour ago',
    read: false,
  },
  {
    id: 'notif-3',
    category: 'payroll',
    title: 'September Payroll Disbursed',
    message: 'Your net salary slip of $8,250.00 has been transferred via direct deposit.',
    timestamp: '2 days ago',
    read: true,
  },
  {
    id: 'notif-4',
    category: 'projects',
    title: 'Assigned to TechKnife Cloud Migration',
    message: 'You have been designated Lead Full Stack Engineer for PRJ-101 deliverable.',
    timestamp: '4 days ago',
    read: true,
  },
];

export const NotificationsTab: React.FC = () => {
  const [notifs, setNotifs] = useState<NotificationItem[]>(MOCK_NOTIFICATIONS);
  const [filter, setFilter] = useState<'all' | 'unread' | 'security' | 'payroll' | 'projects'>('all');

  const handleMarkAllRead = () => {
    setNotifs(notifs.map((n) => ({ ...n, read: true })));
  };

  const handleToggleRead = (id: string) => {
    setNotifs(notifs.map((n) => (n.id === id ? { ...n, read: !n.read } : n)));
  };

  const handleClearAll = () => {
    setNotifs([]);
  };

  const filteredNotifs = notifs.filter((n) => {
    if (filter === 'unread') return !n.read;
    if (filter === 'security') return n.category === 'security';
    if (filter === 'payroll') return n.category === 'payroll';
    if (filter === 'projects') return n.category === 'projects';
    return true;
  });

  const unreadCount = notifs.filter((n) => !n.read).length;

  const getCategoryIcon = (category: string) => {
    switch (category) {
      case 'security':
        return <Shield className="w-4 h-4 text-amber-500" />;
      case 'payroll':
        return <DollarSign className="w-4 h-4 text-emerald-500" />;
      case 'leave':
        return <Calendar className="w-4 h-4 text-indigo-500" />;
      default:
        return <FolderKanban className="w-4 h-4 text-teal-500" />;
    }
  };

  return (
    <div className="space-y-8">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-bold text-xs uppercase tracking-wider mb-1">
            <Bell className="w-4 h-4" />
            <span>Activity Stream & Operational Notifications</span>
          </div>
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <span>Notifications Inbox</span>
            {unreadCount > 0 && (
              <span className="px-2.5 py-0.5 bg-indigo-600 text-white text-xs font-mono font-bold rounded-full">
                {unreadCount} Unread
              </span>
            )}
          </h3>
          <p className="text-xs text-slate-500">
            System security alerts, payroll disbursement vouchers, and project milestones
          </p>
        </div>

        <div className="flex items-center gap-2 shrink-0">
          <button
            onClick={handleMarkAllRead}
            disabled={unreadCount === 0}
            className="px-3.5 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-colors disabled:opacity-50 flex items-center gap-1.5"
          >
            <Check className="w-3.5 h-3.5" /> Mark All as Read
          </button>
          <button
            onClick={handleClearAll}
            disabled={notifs.length === 0}
            className="px-3.5 py-2 bg-rose-500/10 hover:bg-rose-500/20 text-rose-500 font-bold text-xs rounded-xl transition-colors disabled:opacity-50 flex items-center gap-1.5"
          >
            <Trash2 className="w-3.5 h-3.5" /> Clear All
          </button>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex flex-wrap gap-2">
        {(['all', 'unread', 'security', 'payroll', 'projects'] as const).map((f) => (
          <button
            key={f}
            onClick={() => setFilter(f)}
            className={`px-3.5 py-1.5 text-xs font-bold rounded-xl uppercase tracking-wider transition-all ${
              filter === f
                ? 'bg-indigo-600 text-white shadow-md'
                : 'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-500 hover:text-slate-900 dark:hover:text-white'
            }`}
          >
            {f}
          </button>
        ))}
      </div>

      {/* Notifications List */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-3">
        {filteredNotifs.length === 0 ? (
          <div className="py-12 text-center space-y-2">
            <CheckCircle2 className="w-8 h-8 text-emerald-500 mx-auto" />
            <h4 className="text-sm font-bold text-slate-800 dark:text-slate-200">You're All Caught Up!</h4>
            <p className="text-xs text-slate-500">No notifications found in this view.</p>
          </div>
        ) : (
          filteredNotifs.map((item) => (
            <div
              key={item.id}
              onClick={() => handleToggleRead(item.id)}
              className={`p-4 rounded-2xl border transition-all cursor-pointer flex items-start gap-3.5 ${
                item.read
                  ? 'bg-slate-50/50 dark:bg-slate-950/40 border-slate-100 dark:border-slate-800/60 opacity-80'
                  : 'bg-indigo-50/30 dark:bg-indigo-950/20 border-indigo-500/30 shadow-sm'
              }`}
            >
              <div className="p-2.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm shrink-0">
                {getCategoryIcon(item.category)}
              </div>

              <div className="flex-1 min-w-0 space-y-0.5">
                <div className="flex items-center justify-between gap-2">
                  <h4 className="font-extrabold text-xs text-slate-900 dark:text-white truncate">
                    {item.title}
                  </h4>
                  <span className="text-[10px] text-slate-400 font-mono shrink-0">
                    {item.timestamp}
                  </span>
                </div>
                <p className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed">
                  {item.message}
                </p>
              </div>

              {!item.read && (
                <span className="w-2.5 h-2.5 rounded-full bg-indigo-600 shrink-0 mt-2" title="Unread" />
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};
