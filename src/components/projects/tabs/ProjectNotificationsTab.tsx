import React, { useState, useEffect } from 'react';
import { 
  Bell, CheckCheck, Trash2, FileText, CheckSquare, ShieldCheck, 
  Clock, Loader2, UserCheck, ShieldAlert, ArrowRight 
} from 'lucide-react';
import { EnterpriseProject, projectsApi, ProjectActivity } from '../../../api/projects';

interface ProjectNotificationsTabProps {
  project: EnterpriseProject;
}

export interface NotificationItem {
  id: string;
  title: string;
  description: string;
  timestamp: string;
  isRead: boolean;
  type: 'DOCUMENT' | 'TASK' | 'STATUS' | 'TEAM' | 'RISK' | 'MILESTONE' | 'SYSTEM';
}

export const ProjectNotificationsTab: React.FC<ProjectNotificationsTabProps> = ({ project }) => {
  const projectId = project.id || project.projectId || project.projectCode || '';

  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [filter, setFilter] = useState<string>('ALL');
  const [isLoading, setIsLoading] = useState(true);

  const loadProjectNotifications = async () => {
    if (!projectId) return;
    setIsLoading(true);
    try {
      const items: NotificationItem[] = [];

      // 1. Pending Status Request Notification
      if (project.pendingStatusRequest) {
        items.push({
          id: `NOT-STATUS-REQ-${project.pendingStatusRequest.requestedAt}`,
          title: `Project Status Change Requested: ${project.pendingStatusRequest.requestedStatus}`,
          description: `Requested by ${project.pendingStatusRequest.requestedBy} (${project.pendingStatusRequest.requestedByRole || 'Member'}) — "${project.pendingStatusRequest.reason || 'Status update'}"`,
          timestamp: project.pendingStatusRequest.requestedAt,
          isRead: false,
          type: 'STATUS',
        });
      }

      // 2. Fetch Project Activity Ledger Events
      const actRes = await projectsApi.getActivities(projectId);
      if (actRes?.data && Array.isArray(actRes.data)) {
        actRes.data.forEach((act: ProjectActivity) => {
          let nType: NotificationItem['type'] = 'SYSTEM';
          const actName = (act.action || '').toUpperCase();

          if (actName.includes('TASK')) nType = 'TASK';
          else if (actName.includes('STATUS')) nType = 'STATUS';
          else if (actName.includes('DOCUMENT') || actName.includes('FILE') || actName.includes('DRIVE')) nType = 'DOCUMENT';
          else if (actName.includes('MEMBER') || actName.includes('TEAM') || actName.includes('ASSIGN')) nType = 'TEAM';
          else if (actName.includes('RISK')) nType = 'RISK';
          else if (actName.includes('MILESTONE')) nType = 'MILESTONE';

          items.push({
            id: `NOT-ACT-${act.id}`,
            title: `${act.action}: ${act.fieldModified || 'Project Activity'}`,
            description: `${act.performedBy} (${act.userRole}) executed ${act.action}${act.oldValue ? ` [${act.oldValue} → ${act.newValue}]` : ''}`,
            timestamp: act.timestamp ? new Date(act.timestamp).toLocaleString() : 'Recently',
            isRead: false,
            type: nType,
          });
        });
      }

      setNotifications(items);
    } catch (err) {
      console.warn('Failed to load project notifications:', err);
      setNotifications([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadProjectNotifications();
  }, [projectId]);

  const filtered = notifications.filter(n => {
    if (filter === 'UNREAD') return !n.isRead;
    if (filter === 'ALL') return true;
    return n.type === filter;
  });

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
      case 'STATUS': return <ShieldCheck className="w-4 h-4 text-amber-500" />;
      case 'TEAM': return <UserCheck className="w-4 h-4 text-indigo-500" />;
      case 'RISK': return <ShieldAlert className="w-4 h-4 text-rose-500" />;
      default: return <Bell className="w-4 h-4 text-slate-400" />;
    }
  };

  const CATEGORY_TABS = [
    { id: 'ALL', label: 'All' },
    { id: 'UNREAD', label: `Unread (${notifications.filter(n => !n.isRead).length})` },
    { id: 'TASKS', label: 'Tasks' },
    { id: 'STATUS', label: 'Status' },
    { id: 'DOCUMENTS', label: 'Documents' },
    { id: 'TEAM', label: 'Team' },
    { id: 'RISK', label: 'Risks' },
  ];

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-cyan-600 dark:text-cyan-400 mb-1">
            <Bell className="w-4 h-4" />
            <span>Project Notification Center</span>
          </div>
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
            Notifications Inbox ({notifications.filter(n => !n.isRead).length} Unread)
          </h3>
          <p className="text-xs text-slate-500">Real-time alerts for project tasks, status requests, team assignments, and documents</p>
        </div>

        <div className="flex items-center gap-2 self-start sm:self-auto">
          <button
            type="button"
            onClick={handleMarkAllRead}
            className="px-3.5 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-all flex items-center gap-1.5 cursor-pointer"
          >
            <CheckCheck className="w-3.5 h-3.5 text-emerald-500" /> Mark All Read
          </button>
          <button
            type="button"
            onClick={handleClearAll}
            className="px-3.5 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-rose-500/10 hover:text-rose-500 text-slate-600 dark:text-slate-400 font-bold text-xs rounded-xl transition-all flex items-center gap-1.5 cursor-pointer"
          >
            <Trash2 className="w-3.5 h-3.5" /> Clear
          </button>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex flex-wrap items-center gap-2 border-b border-slate-200 dark:border-slate-800 pb-3">
        {CATEGORY_TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setFilter(tab.id)}
            className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer ${
              filter === tab.id
                ? 'bg-cyan-500 text-slate-950 font-black'
                : 'bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-800 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Notifications List */}
      {isLoading ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl space-y-2">
          <Loader2 className="w-6 h-6 animate-spin text-cyan-500 mx-auto" />
          <p className="text-xs text-slate-400 font-medium">Fetching project notifications stream...</p>
        </div>
      ) : filtered.length === 0 ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl space-y-2">
          <Bell className="w-8 h-8 text-slate-400 mx-auto" />
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white">
            No project notifications yet.
          </h4>
          <p className="text-xs text-slate-500">
            There are currently no active notifications logged for this project.
          </p>
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
