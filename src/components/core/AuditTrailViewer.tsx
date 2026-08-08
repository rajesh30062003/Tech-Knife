import React, { useState, useEffect } from 'react';
import {
  Shield, Search, Eye, RefreshCw, CheckSquare, FileText, Upload, Download,
  GitBranch, Activity, Video, Users, Calendar, AlertTriangle, Layers, Clock
} from 'lucide-react';
import { auditApi } from '../../api/coreServices';
import { projectsApi, ProjectActivity } from '../../api/projects';
import { ActivityLog, AuditLogEntry } from '../../types';

interface AuditTrailViewerProps {
  entityType?: string;
  entityId?: string;
}

export const AuditTrailViewer: React.FC<AuditTrailViewerProps> = ({ entityType, entityId }) => {
  const [activeTab, setActiveTab] = useState<'activity' | 'audit'>('activity');
  const [activityLogs, setActivityLogs] = useState<ActivityLog[]>([]);
  const [projectActivities, setProjectActivities] = useState<ProjectActivity[]>([]);
  const [auditEntries, setAuditEntries] = useState<AuditLogEntry[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [selectedEntry, setSelectedEntry] = useState<AuditLogEntry | ProjectActivity | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const loadLogs = async () => {
    setIsLoading(true);
    try {
      if (entityId) {
        const pRes = await projectsApi.getActivities(entityId).catch(() => null);
        if (pRes?.data && Array.isArray(pRes.data)) {
          setProjectActivities(pRes.data);
        } else {
          setProjectActivities([]);
        }
      }

      const [act, aud] = await Promise.all([
        auditApi.getActivityLogs().catch(() => []),
        auditApi.getAuditLogs().catch(() => [])
      ]);
      setActivityLogs(act || []);
      setAuditEntries(aud || []);
    } catch (err) {
      console.warn('Failed to load audit logs:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
  }, [entityId]);

  const filteredProjectActivities = projectActivities.filter((act) => {
    const q = searchQuery.toLowerCase();
    const matchesQuery = !q ||
      (act.action || '').toLowerCase().includes(q) ||
      (act.description || '').toLowerCase().includes(q) ||
      (act.performedBy || '').toLowerCase().includes(q) ||
      (act.fieldModified || '').toLowerCase().includes(q) ||
      (act.oldValue || '').toLowerCase().includes(q) ||
      (act.newValue || '').toLowerCase().includes(q);

    if (!matchesQuery) return false;
    if (categoryFilter === 'ALL') return true;

    const type = (act.activityType || '').toUpperCase();
    const action = (act.action || '').toUpperCase();

    if (categoryFilter === 'TASKS') return type === 'TASK' || action.includes('TASK');
    if (categoryFilter === 'DOCUMENTS') return type === 'DOCUMENT' || action.includes('DOC') || action.includes('FILE');
    if (categoryFilter === 'REPOSITORY') return type === 'REPOSITORY' || action.includes('REPO') || action.includes('LINK');
    if (categoryFilter === 'MEETINGS') return type === 'MEETING' || action.includes('MEETING') || action.includes('SYNC');
    if (categoryFilter === 'STATUS') return type === 'STATUS' || action.includes('STATUS');
    if (categoryFilter === 'TEAM') return type === 'TEAM' || action.includes('TEAM') || action.includes('MEMBER');

    return true;
  });

  const filteredActivity = activityLogs.filter((l) => {
    if (entityId) {
      const eId = entityId.toLowerCase();
      const matchesEntity = (l.description || '').toLowerCase().includes(eId) ||
        (l.module || '').toLowerCase().includes(eId);
      if (!matchesEntity) return false;
    }
    const q = searchQuery.toLowerCase();
    if (!q) return true;
    return (
      (l.userName || '').toLowerCase().includes(q) ||
      (l.module || '').toLowerCase().includes(q) ||
      (l.action || '').toLowerCase().includes(q) ||
      (l.description || '').toLowerCase().includes(q)
    );
  });

  const filteredAudit = auditEntries.filter((a) => {
    if (entityId) {
      const eId = entityId.toLowerCase();
      const matchesEntity = (a.entityId || '').toLowerCase().includes(eId) ||
        (a.entityName || '').toLowerCase().includes(eId);
      if (!matchesEntity) return false;
    }
    const q = searchQuery.toLowerCase();
    if (!q) return true;
    return (
      (a.userName || '').toLowerCase().includes(q) ||
      (a.entityName || '').toLowerCase().includes(q) ||
      (a.entityId || '').toLowerCase().includes(q) ||
      (a.action || '').toLowerCase().includes(q)
    );
  });

  const CATEGORY_TABS = [
    { id: 'ALL', label: 'All Activities' },
    { id: 'TASKS', label: 'Tasks' },
    { id: 'DOCUMENTS', label: 'Documents' },
    { id: 'REPOSITORY', label: 'Repos & Links' },
    { id: 'MEETINGS', label: 'Meetings' },
    { id: 'STATUS', label: 'Status' },
    { id: 'TEAM', label: 'Team' },
  ];

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      {/* Header & Controls */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs uppercase tracking-wider font-bold text-indigo-600 dark:text-indigo-400 mb-1">
              <Shield className="w-4 h-4" />
              <span>Security & Audit Infrastructure</span>
            </div>
            <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">
              Project Activity & Audit Trail {entityId ? `— ${entityId}` : ''}
            </h2>
            <p className="text-xs text-slate-500">
              Persistent, real-time audit ledger recording task updates, document uploads, link edits, and team actions
            </p>
          </div>

          <button
            type="button"
            onClick={loadLogs}
            className="px-3.5 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-all shadow-xs flex items-center gap-1.5 self-start sm:self-auto cursor-pointer"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${isLoading ? 'animate-spin' : ''}`} /> Refresh Stream
          </button>
        </div>

        {/* Category Tabs & Search Bar */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-3">
          <div className="flex flex-wrap items-center gap-1.5">
            {CATEGORY_TABS.map((tab) => (
              <button
                key={tab.id}
                type="button"
                onClick={() => setCategoryFilter(tab.id)}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                  categoryFilter === tab.id
                    ? 'bg-indigo-600 text-white shadow-xs'
                    : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>

          <div className="relative max-w-xs w-full">
            <Search className="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Search activity stream..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-8 pr-3 py-2 text-xs rounded-xl bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100 font-medium"
            />
          </div>
        </div>
      </div>

      {/* Main Stream Display */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        {isLoading ? (
          <div className="py-12 text-center text-xs text-slate-400 flex items-center justify-center gap-2">
            <RefreshCw className="w-4 h-4 animate-spin text-indigo-500" /> Loading project activity trail...
          </div>
        ) : (entityId && projectActivities.length > 0) ? (
          filteredProjectActivities.length === 0 ? (
            <div className="py-12 text-center text-xs text-slate-400">
              No recorded project activities match the selected filter.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-400 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200 dark:border-slate-800">
                  <tr>
                    <th className="py-3 px-4">Action</th>
                    <th className="py-3 px-4">Details</th>
                    <th className="py-3 px-4">Done By</th>
                    <th className="py-3 px-4">State Diff</th>
                    <th className="py-3 px-4">Timestamp</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
                  {filteredProjectActivities.map((act) => (
                    <tr key={act.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition-colors">
                      <td className="py-3.5 px-4 font-mono font-extrabold text-[11px] text-indigo-600 dark:text-indigo-400 whitespace-nowrap">
                        <span className="px-2 py-0.5 rounded-md bg-indigo-50 dark:bg-indigo-950 border border-indigo-200 dark:border-indigo-900/50">
                          {act.action}
                        </span>
                      </td>

                      <td className="py-3.5 px-4 text-slate-800 dark:text-slate-200 font-medium max-w-md">
                        <div>{act.description || act.fieldModified || 'Project Action'}</div>
                        {act.fieldModified && (
                          <span className="text-[10px] text-slate-400 font-mono">Field: {act.fieldModified}</span>
                        )}
                      </td>

                      <td className="py-3.5 px-4 font-bold text-slate-900 dark:text-slate-100 whitespace-nowrap">
                        <div>{act.performedBy || 'System'}</div>
                        <span className="text-[10px] text-indigo-500 font-semibold">
                          {(act.userRole || 'ROLE_USER').replace('ROLE_', '')}
                        </span>
                      </td>

                      <td className="py-3.5 px-4 font-mono text-[11px]">
                        {act.oldValue || act.newValue ? (
                          <div className="flex items-center gap-1">
                            <span className="text-rose-500 line-through text-[10px] max-w-[100px] truncate">{act.oldValue || 'None'}</span>
                            <span className="text-slate-400">→</span>
                            <span className="text-emerald-600 dark:text-emerald-400 font-bold max-w-[120px] truncate">{act.newValue || 'Updated'}</span>
                          </div>
                        ) : (
                          <span className="text-slate-400">—</span>
                        )}
                      </td>

                      <td className="py-3.5 px-4 text-slate-400 text-[11px] font-mono whitespace-nowrap">
                        {act.timestamp ? new Date(act.timestamp).toLocaleString() : '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )
        ) : filteredActivity.length === 0 ? (
          <div className="py-12 text-center text-xs text-slate-400">
            No activity logs recorded for this project yet.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-400 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-3 px-4">User</th>
                  <th className="py-3 px-4">Module</th>
                  <th className="py-3 px-4">Action</th>
                  <th className="py-3 px-4">Description</th>
                  <th className="py-3 px-4">IP Address / Client</th>
                  <th className="py-3 px-4">Timestamp</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
                {filteredActivity.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="py-3 px-4 font-bold text-slate-900 dark:text-slate-100">
                      <div>{log.userName}</div>
                      <span className="text-[10px] text-indigo-600 dark:text-indigo-400 font-semibold">{(log.userRole || '').replace('ROLE_', '')}</span>
                    </td>

                    <td className="py-3 px-4">
                      <span className="px-2 py-0.5 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-bold text-[10px]">
                        {log.module}
                      </span>
                    </td>

                    <td className="py-3 px-4 font-mono font-extrabold text-[11px] text-indigo-600 dark:text-indigo-300">
                      {log.action}
                    </td>

                    <td className="py-3 px-4 text-slate-700 dark:text-slate-300 font-medium max-w-sm truncate">
                      {log.description}
                    </td>

                    <td className="py-3 px-4 text-slate-500 font-mono text-[11px]">
                      <div>{log.ipAddress}</div>
                      <div className="text-[10px] text-slate-400 truncate max-w-[120px]">{log.browser}</div>
                    </td>

                    <td className="py-3 px-4 text-slate-400 text-[11px] font-medium whitespace-nowrap">
                      {log.timestamp ? new Date(log.timestamp).toLocaleString() : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* State Transformation Diff Modal */}
      {selectedEntry && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-2xl w-full p-6 space-y-4 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div>
                <h3 className="font-extrabold text-base text-slate-900 dark:text-white">State Transformation Audit Diff</h3>
                <p className="text-xs text-slate-500">
                  {selectedEntry.entityName} ({selectedEntry.entityId}) modified by {selectedEntry.userName}
                </p>
              </div>
              <button 
                type="button"
                onClick={() => setSelectedEntry(null)} 
                className="text-slate-400 hover:text-slate-600 font-bold text-xs"
              >
                Close
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs font-mono">
              <div className="p-4 rounded-2xl bg-rose-50/50 dark:bg-rose-950/20 border border-rose-200 dark:border-rose-900/50 space-y-1">
                <span className="text-[10px] uppercase font-bold text-rose-600 dark:text-rose-400 block">Previous State (Old)</span>
                <pre className="text-[11px] text-slate-800 dark:text-slate-200 overflow-x-auto">
                  {JSON.stringify(selectedEntry.oldValue, null, 2)}
                </pre>
              </div>

              <div className="p-4 rounded-2xl bg-emerald-50/50 dark:bg-emerald-950/20 border border-emerald-200 dark:border-emerald-900/50 space-y-1">
                <span className="text-[10px] uppercase font-bold text-emerald-600 dark:text-emerald-400 block">New State (Applied)</span>
                <pre className="text-[11px] text-slate-800 dark:text-slate-200 overflow-x-auto">
                  {JSON.stringify(selectedEntry.newValue, null, 2)}
                </pre>
              </div>
            </div>

            <div className="flex justify-end pt-2">
              <button
                type="button"
                onClick={() => setSelectedEntry(null)}
                className="px-4 py-2 bg-indigo-600 text-white font-bold text-xs rounded-xl shadow-xs cursor-pointer"
              >
                Done Inspecting
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
