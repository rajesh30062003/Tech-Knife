import React, { useState, useEffect } from 'react';
import { Bell, Check, Loader2, CheckCircle2, ShieldAlert, FileText, CheckSquare, Clock } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { apiClient } from '../../api/client';

interface NotificationRecord {
  id: string;
  title: string;
  message: string;
  type?: string;
  createdAt?: string;
  read?: boolean;
}

export const NotificationsPage: React.FC = () => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState<NotificationRecord[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const loadNotifications = async () => {
    if (!user?.id && !user?.email) return;
    setIsLoading(true);
    try {
      const targetId = user.id || user.email;
      const res = await apiClient.get(`/notifications/user/${targetId}`);
      if (res?.data?.data && Array.isArray(res.data.data)) {
        setNotifications(res.data.data);
      } else {
        setNotifications([]);
      }
    } catch (err) {
      console.warn('Could not fetch user notifications:', err);
      setNotifications([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadNotifications();
  }, [user]);

  const handleMarkAllRead = async () => {
    if (!user?.id && !user?.email) return;
    try {
      const targetId = user.id || user.email;
      await apiClient.put(`/notifications/user/${targetId}/read-all`);
      setNotifications(prev => prev.map(n => ({ ...n, read: true })));
    } catch (err) {
      setNotifications(prev => prev.map(n => ({ ...n, read: true })));
    }
  };

  return (
    <div className="space-y-8 max-w-4xl text-slate-800 dark:text-slate-200">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Bell className="w-4 h-4" />
            <span>Operational Activity & Notifications</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Notifications Inbox</h1>
          <p className="text-xs text-slate-500">System notifications, project task assignments, and security alerts</p>
        </div>

        <button
          onClick={handleMarkAllRead}
          className="inline-flex items-center gap-2 px-4 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-700 dark:text-slate-300 font-semibold text-xs rounded-xl transition-all cursor-pointer"
        >
          <Check className="w-3.5 h-3.5" /> Mark All as Read
        </button>
      </div>

      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4 shadow-xs">
        {isLoading ? (
          <div className="py-12 text-center text-xs text-slate-400 flex items-center justify-center gap-2">
            <Loader2 className="w-4 h-4 animate-spin text-indigo-500" /> Loading notifications inbox...
          </div>
        ) : notifications.length === 0 ? (
          <div className="py-12 text-center text-xs text-slate-400 space-y-2">
            <Bell className="w-8 h-8 text-slate-300 dark:text-slate-700 mx-auto" />
            <h4 className="font-bold text-slate-900 dark:text-white">No notifications inbox messages.</h4>
            <p className="text-slate-500">You are completely caught up with system alerts.</p>
          </div>
        ) : (
          <div className="space-y-3">
            {notifications.map((n) => (
              <div
                key={n.id}
                className={`p-4 rounded-xl border flex items-start gap-3 transition-all ${
                  n.read
                    ? 'bg-slate-50/50 dark:bg-slate-800/40 border-slate-200/60 dark:border-slate-800 opacity-75'
                    : 'bg-white dark:bg-slate-900 border-indigo-500/30 shadow-xs'
                }`}
              >
                <div className="p-2 bg-indigo-50 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400 rounded-lg mt-0.5 shrink-0">
                  <Bell className="w-4 h-4" />
                </div>
                <div className="flex-1 space-y-0.5">
                  <div className="flex items-center justify-between">
                    <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100">{n.title}</h4>
                    {n.createdAt && (
                      <span className="text-[10px] text-slate-400 font-mono flex items-center gap-1">
                        <Clock className="w-3 h-3" /> {new Date(n.createdAt).toLocaleDateString()}
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-slate-600 dark:text-slate-300 font-medium">{n.message}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
