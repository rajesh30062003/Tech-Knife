import React, { useState } from 'react';
import { Bell, Shield, DollarSign, Calendar, FolderKanban, Check, Trash2, ChevronRight, CheckCircle2 } from 'lucide-react';
import { Link } from 'react-router-dom';

interface NotificationItem {
  id: string;
  category: 'security' | 'payroll' | 'leave' | 'projects';
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
}

const INITIAL_NOTIFICATIONS: NotificationItem[] = [
  {
    id: 'notif-1',
    category: 'leave',
    title: 'Leave Application Approved',
    message: 'Your vacation request for Nov 12 - Nov 15 was approved by Marcus Brody.',
    timestamp: '15 mins ago',
    read: false,
  },
  {
    id: 'notif-2',
    category: 'security',
    title: 'Security Alert: New Session Login',
    message: 'Authenticated from San Jose HQ (macOS / Chrome).',
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
    title: 'Designated Lead Engineer for Cloud Migration',
    message: 'You were assigned to PRJ-101 deliverable by CTO.',
    timestamp: '3 days ago',
    read: true,
  },
];

export const NotificationsWidget: React.FC = () => {
  const [notifs, setNotifs] = useState<NotificationItem[]>(INITIAL_NOTIFICATIONS);

  const handleMarkAllRead = () => {
    setNotifs(notifs.map((n) => ({ ...n, read: true })));
  };

  const handleToggleRead = (id: string) => {
    setNotifs(notifs.map((n) => (n.id === id ? { ...n, read: !n.read } : n)));
  };

  const unreadCount = notifs.filter((n) => !n.read).length;

  const getCategoryIcon = (category: NotificationItem['category']) => {
    switch (category) {
      case 'security':
        return <Shield className="w-3.5 h-3.5 text-amber-500" />;
      case 'payroll':
        return <DollarSign className="w-3.5 h-3.5 text-emerald-500" />;
      case 'leave':
        return <Calendar className="w-3.5 h-3.5 text-indigo-500" />;
      default:
        return <FolderKanban className="w-3.5 h-3.5 text-teal-500" />;
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-4 flex flex-col justify-between h-full transition-all hover:border-indigo-500/30">
      {/* Widget Header */}
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-purple-500/10 text-purple-600 dark:text-purple-400">
            <Bell className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <span>Notifications Feed</span>
              {unreadCount > 0 && (
                <span className="px-2 py-0.5 bg-indigo-600 text-white text-[10px] font-mono font-bold rounded-full">
                  {unreadCount} New
                </span>
              )}
            </h3>
            <p className="text-[11px] text-slate-500">System alerts & team activity</p>
          </div>
        </div>

        <button
          onClick={handleMarkAllRead}
          disabled={unreadCount === 0}
          className="p-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-600 dark:text-slate-300 rounded-xl text-xs disabled:opacity-40"
          title="Mark All Read"
        >
          <Check className="w-3.5 h-3.5" />
        </button>
      </div>

      {/* Notifications List */}
      <div className="space-y-2.5 flex-1 overflow-y-auto max-h-[290px] pr-1">
        {notifs.length === 0 ? (
          <div className="text-center py-8 text-slate-400 text-xs">No notifications.</div>
        ) : (
          notifs.map((item) => (
            <div
              key={item.id}
              onClick={() => handleToggleRead(item.id)}
              className={`p-3 rounded-2xl border transition-all cursor-pointer flex items-start gap-3 ${
                item.read
                  ? 'bg-slate-50/50 dark:bg-slate-950/40 border-slate-100 dark:border-slate-800/60 opacity-80'
                  : 'bg-purple-50/40 dark:bg-purple-950/20 border-purple-500/30 shadow-2xs'
              }`}
            >
              <div className="p-2 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shrink-0">
                {getCategoryIcon(item.category)}
              </div>

              <div className="flex-1 min-w-0 space-y-0.5">
                <div className="flex items-center justify-between gap-1">
                  <h4 className="font-extrabold text-xs text-slate-900 dark:text-white truncate">
                    {item.title}
                  </h4>
                  <span className="text-[10px] text-slate-400 font-mono shrink-0">{item.timestamp}</span>
                </div>
                <p className="text-[11px] text-slate-600 dark:text-slate-300 line-clamp-2 leading-relaxed">
                  {item.message}
                </p>
              </div>

              {!item.read && <span className="w-2 h-2 rounded-full bg-purple-600 shrink-0 mt-1.5" />}
            </div>
          ))
        )}
      </div>

      {/* Footer Link */}
      <Link
        to="/notifications"
        className="w-full py-2 bg-slate-100 dark:bg-slate-800/80 hover:bg-slate-200 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-colors flex items-center justify-center gap-1.5"
      >
        <span>View Full Activity Inbox</span>
        <ChevronRight className="w-3.5 h-3.5" />
      </Link>
    </div>
  );
};
