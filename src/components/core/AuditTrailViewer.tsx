import React, { useState, useEffect } from 'react';
import {
  Activity,
  Search,
  Filter,
  Shield,
  Clock,
  Globe,
  Terminal,
  FileCode,
  Eye,
  RefreshCw
} from 'lucide-react';
import { auditApi } from '../../api/coreServices';
import { ActivityLog, AuditLogEntry } from '../../types';

export const AuditTrailViewer: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'activity' | 'audit'>('activity');
  const [activityLogs, setActivityLogs] = useState<ActivityLog[]>([]);
  const [auditEntries, setAuditEntries] = useState<AuditLogEntry[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedEntry, setSelectedEntry] = useState<AuditLogEntry | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const loadLogs = async () => {
    setIsLoading(true);
    try {
      const [act, aud] = await Promise.all([auditApi.getActivityLogs(), auditApi.getAuditLogs()]);
      setActivityLogs(act);
      setAuditEntries(aud);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
  }, []);

  const filteredActivity = activityLogs.filter(
    (l) =>
      l.userName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.module.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.action.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.description.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredAudit = auditEntries.filter(
    (a) =>
      a.userName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      a.entityName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      a.entityId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      a.action.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Header & Controls */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs uppercase tracking-wider font-bold text-indigo-600 dark:text-indigo-400 mb-1">
              <Shield className="w-4 h-4" />
              <span>Security & Audit Infrastructure</span>
            </div>
            <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">Enterprise Activity & Audit Console</h2>
            <p className="text-xs text-slate-500">Immutable execution ledger tracking database modifications, security actions & user sessions</p>
          </div>

          <button
            onClick={loadLogs}
            className="px-3.5 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-all shadow-xs flex items-center gap-1.5 self-start sm:self-auto"
          >
            <RefreshCw className="w-3.5 h-3.5" /> Refresh Audit Stream
          </button>
        </div>

        {/* Tab & Search Bar */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div className="flex items-center gap-2 p-1 bg-slate-100 dark:bg-slate-800 rounded-2xl w-fit">
            <button
              onClick={() => setActiveTab('activity')}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                activeTab === 'activity'
                  ? 'bg-indigo-600 text-white shadow-xs'
                  : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              Activity Stream ({activityLogs.length})
            </button>
            <button
              onClick={() => setActiveTab('audit')}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                activeTab === 'audit'
                  ? 'bg-indigo-600 text-white shadow-xs'
                  : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              Database Audit Trail ({auditEntries.length})
            </button>
          </div>

          <div className="relative max-w-xs w-full">
            <Search className="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Filter by user, module, IP..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-8 pr-3 py-2 text-xs rounded-xl bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100 font-medium"
            />
          </div>
        </div>
      </div>

      {/* Content Stream */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        {isLoading ? (
          <div className="py-12 text-center text-xs text-slate-400">Loading audit ledger stream...</div>
        ) : activeTab === 'activity' ? (
          /* Activity Stream Table */
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
                      <span className="text-[10px] text-indigo-600 dark:text-indigo-400 font-semibold">{log.userRole.replace('ROLE_', '')}</span>
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
                      {new Date(log.timestamp).toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          /* Database Audit Entries Table */
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-400 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-3 px-4">Entity</th>
                  <th className="py-3 px-4">Action Type</th>
                  <th className="py-3 px-4">Executed By</th>
                  <th className="py-3 px-4">IP / Browser</th>
                  <th className="py-3 px-4">Timestamp</th>
                  <th className="py-3 px-4 text-right">State Diff</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
                {filteredAudit.map((entry) => (
                  <tr key={entry.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="py-3 px-4 font-bold text-slate-900 dark:text-slate-100">
                      <div>{entry.entityName}</div>
                      <span className="text-[10px] font-mono text-slate-400">{entry.entityId}</span>
                    </td>

                    <td className="py-3 px-4">
                      <span
                        className={`px-2 py-0.5 rounded-md font-extrabold text-[10px] ${
                          entry.action === 'CREATE'
                            ? 'bg-emerald-100 text-emerald-800'
                            : entry.action === 'UPDATE'
                            ? 'bg-blue-100 text-blue-800'
                            : entry.action === 'DELETE'
                            ? 'bg-rose-100 text-rose-800'
                            : 'bg-amber-100 text-amber-800'
                        }`}
                      >
                        {entry.action}
                      </span>
                    </td>

                    <td className="py-3 px-4 font-semibold text-slate-800 dark:text-slate-200">{entry.userName}</td>

                    <td className="py-3 px-4 text-slate-500 font-mono text-[11px]">{entry.ipAddress}</td>

                    <td className="py-3 px-4 text-slate-400 text-[11px] font-medium whitespace-nowrap">
                      {new Date(entry.timestamp).toLocaleString()}
                    </td>

                    <td className="py-3 px-4 text-right">
                      <button
                        onClick={() => setSelectedEntry(entry)}
                        className="px-2.5 py-1 bg-indigo-50 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400 font-extrabold rounded-lg text-[10px] hover:bg-indigo-100 transition-colors inline-flex items-center gap-1"
                      >
                        <Eye className="w-3 h-3" /> View Diff
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* JSON Diff Modal */}
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
              <button onClick={() => setSelectedEntry(null)} className="text-slate-400 hover:text-slate-600 font-bold text-xs">
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
                onClick={() => setSelectedEntry(null)}
                className="px-4 py-2 bg-indigo-600 text-white font-bold text-xs rounded-xl shadow-xs"
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
