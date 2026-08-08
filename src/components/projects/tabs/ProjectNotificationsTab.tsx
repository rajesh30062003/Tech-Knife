import React, { useState } from 'react';
import { Bell, CheckCheck, Trash2, FileText, CheckSquare, ShieldCheck, GitBranch, MessageSquare, Clock } from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';

interface ProjectNotificationsTabProps {
  project: EnterpriseProject;
}

interface NotificationItem {
  id: string;
  title: string;
  description: string;
  timestamp: string;
  isRead: boolean;
  type: 'DOCUMENT' | 'TASK' | 'SECURITY' | 'CHAT' | 'SYSTEM';
}

const DEFAULT_NOTIFICATIONS: NotificationItem[] = [
  {
    id: 'NOT-1',
    title: 'Google OAuth 2.0 Upload Stream Verified',
    description: 'System verified PDF streaming preview and Google Drive file storage endpoint for project TK-PRJ-9841.',
    timestamp: '10 mins ago',
    isRead: false,
    type: 'DOCUMENT',
  },
  {
    id: 'NOT-2',
    title: 'Task Status Updated: Core Architecture Spec',
    description: 'Task TK-PRJ-9841-TSK-01 status updated to COMPLETED by Rajesh Pal (Tech Lead).',
    timestamp: '1 hour ago',
    isRead: false,
    type: 'TASK',
  },
  {
    id: 'NOT-3',
    title: 'New Chat Message in #TK-PRJ-9841',
    description: 'Ananya Sharma attached design-specs.pdf to the project channel.',
    timestamp: '3 hours ago',
    isRead: true,
    type: 'CHAT',
  },
  {
    id: 'NOT-4',
    title: 'Security Audit Signoff Completed',
    description: 'Enterprise Security Suite completed SAIF compliance audit with 0 open vulnerabilities.',
    timestamp: 'Yesterday',
    isRead: true,
    type: 'SECURITY',
  },
];

export const ProjectNotificationsTab: React.FC<ProjectNotificationsTabProps> = ({ project }) => {
  const [notifications, setNotifications] = useState<NotificationItem[]>(DEFAULT_NOTIFICATIONS);
  const [filter, setFilter] = useState<'ALL' | 'UNREAD'>('ALL');

  const filtered = notifications.filter(n => filter === 'ALL' || !n.isRead);

  const handleMarkAllRead = () => {
    setNotifications(notifications.map(n => ({ ...n, isRead: true })));
  };

  const handleClearAll = () => {
    setNotifications([]);
  };

  const handleToggleRead = (id: string) => {
    setNotifications(notifications.map(n => n.id === id ? { ...n, isRead: !n.isRead } : n));
  };

  const getIcon = (type: string) => {
    switch (type) {
      case 'DOCUMENT': return <FileText className="w-4 h-4 text-cyan-500" />;
      case 'TASK': return <CheckSquare className="w-4 h-4 text-emerald-500" />;
      case 'SECURITY': return <ShieldCheck className="w-4 h-4 text-indigo-500" />;
      case 'CHAT': return <MessageSquare className="w-4 h-4 text-amber-500" />;
      default: return <Bell className="w-4 h-4 text-slate-400" />;
    }
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-cyan-600 dark:text-cyan-400 mb-1">
            <Bell className="w-4 h-4" />
            <span>Workspace Activity Stream</span>
          </div>
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
            Notifications ({notifications.filter(n => !n.isRead).length} Unread)
          </h3>
          <p className="text-xs text-slate-500">Real-time alerts for tasks, documents, chat messages, and system events</p>
        </div>

        <div className="flex items-center gap-2 self-start sm:self-auto">
          <button
            onClick={handleMarkAllRead}
            className="px-3.5 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-all flex items-center gap-1.5"
          >
            <CheckCheck className="w-3.5 h-3.5 text-emerald-500" /> Mark All Read
          </button>
          <button
            onClick={handleClearAll}
            className="px-3.5 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-rose-500/10 hover:text-rose-500 text-slate-600 dark:text-slate-400 font-bold text-xs rounded-xl transition-all flex items-center gap-1.5"
          >
            <Trash2 className="w-3.5 h-3.5" /> Clear
          </button>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200 dark:border-slate-800 pb-3">
        <button
          onClick={() => setFilter('ALL')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
            filter === 'ALL'
              ? 'bg-cyan-500 text-slate-950 font-black'
              : 'bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-800'
          }`}
        >
          All Notifications ({notifications.length})
        </button>
        <button
          onClick={() => setFilter('UNREAD')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
            filter === 'UNREAD'
              ? 'bg-cyan-500 text-slate-950 font-black'
              : 'bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-800'
          }`}
        >
          Unread Only ({notifications.filter(n => !n.isRead).length})
        </button>
      </div>

      {/* Notifications List */}
      {filtered.length === 0 ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl space-y-2">
          <Bell className="w-8 h-8 text-slate-400 mx-auto" />
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white">No Notifications</h4>
          <p className="text-xs text-slate-500">You are all caught up with current project alerts.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {filtered.map((item) => (
            <div
              key={item.id}
              onClick={() => handleToggleRead(item.id)}
              className={`p-4 rounded-3xl border transition-all cursor-pointer flex items-start gap-3.5 ${
                item.isRead
                  ? 'bg-white dark:bg-slate-900/60 border-slate-200 dark:border-slate-800/80 opacity-80'
                  : 'bg-white dark:bg-slate-900 border-cyan-500/30 shadow-xs ring-1 ring-cyan-500/10'
              }`}
            >
              <div className="p-2.5 rounded-2xl bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/60 shrink-0 mt-0.5">
                {getIcon(item.type)}
              </div>

              <div className="flex-1 space-y-1">
                <div className="flex items-center justify-between gap-2">
                  <h5 className="text-xs font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                    {item.title}
                    {!item.isRead && (
                      <span className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse" />
                    )}
                  </h5>
                  <span className="text-[10px] text-slate-400 font-mono flex items-center gap-1 shrink-0">
                    <Clock className="w-3 h-3" /> {item.timestamp}
                  </span>
                </div>
                <p className="text-xs text-slate-600 dark:text-slate-300 font-medium">
                  {item.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}

    </div>
  );
};
