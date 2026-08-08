import React, { useState, useEffect } from 'react';
import { 
  Shield, Clock, RefreshCw, Loader2, User, Search, Filter, ArrowRight 
} from 'lucide-react';
import { EnterpriseProject, projectsApi, ProjectActivity } from '../../../api/projects';

interface ProjectAuditTabProps {
  project: EnterpriseProject;
}

export const ProjectAuditTab: React.FC<ProjectAuditTabProps> = ({ project }) => {
  const projectId = project.id || project.projectId || project.projectCode || '';

  const [activities, setActivities] = useState<ProjectActivity[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('ALL');

  const loadActivities = async () => {
    if (!projectId) return;
    setIsLoading(true);
    try {
      const res = await projectsApi.getActivities(projectId);
      if (res?.data && Array.isArray(res.data)) {
        setActivities(res.data);
      } else {
        setActivities([]);
      }
    } catch (err) {
      console.warn('Failed to load project activities:', err);
      setActivities([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadActivities();
  }, [projectId]);

  const filteredActivities = activities.filter((act) => {
    const query = searchQuery.toLowerCase();
    const matchesSearch = 
      (act.action || '').toLowerCase().includes(query) ||
      (act.performedBy || '').toLowerCase().includes(query) ||
      (act.fieldModified || '').toLowerCase().includes(query) ||
      (act.oldValue || '').toLowerCase().includes(query) ||
      (act.newValue || '').toLowerCase().includes(query);

    if (!matchesSearch) return false;
    if (categoryFilter === 'ALL') return true;

    const actAction = (act.action || '').toUpperCase();
    if (categoryFilter === 'TASKS') return actAction.includes('TASK');
    if (categoryFilter === 'STATUS') return actAction.includes('STATUS');
    if (categoryFilter === 'DOCUMENTS') return actAction.includes('DOCUMENT') || actAction.includes('FILE') || actAction.includes('DRIVE');
    if (categoryFilter === 'TEAM') return actAction.includes('MEMBER') || actAction.includes('TEAM') || actAction.includes('ASSIGN');
    if (categoryFilter === 'RISKS') return actAction.includes('RISK');
    if (categoryFilter === 'MILESTONES') return actAction.includes('MILESTONE');

    return true;
  });

  const CATEGORY_TABS = [
    { id: 'ALL', label: 'All Actions' },
    { id: 'STATUS', label: 'Status Requests' },
    { id: 'TASKS', label: 'Tasks' },
    { id: 'DOCUMENTS', label: 'Documents' },
    { id: 'TEAM', label: 'Team' },
    { id: 'RISKS', label: 'Risks' },
    { id: 'MILESTONES', label: 'Milestones' },
  ];

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 mb-1">
              <Shield className="w-4 h-4" />
              <span>Immutable Execution & Audit Ledger</span>
            </div>
            <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
              Project Audit & Activity Log ({activities.length})
            </h3>
            <p className="text-xs text-slate-500">
              Complete history of actions answering WHO, WHAT, WHEN, WHERE, and BEFORE/AFTER changes
            </p>
          </div>

          <button
            type="button"
            onClick={loadActivities}
            className="px-4 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-all shadow-xs flex items-center gap-1.5 self-start sm:self-auto cursor-pointer"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${isLoading ? 'animate-spin' : ''}`} /> Refresh Stream
          </button>
        </div>

        {/* Filter Tabs & Search Bar */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div className="flex flex-wrap items-center gap-1.5">
            {CATEGORY_TABS.map((tab) => (
              <button
                key={tab.id}
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
              placeholder="Search by WHO, WHAT, or change..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-8 pr-3 py-2 text-xs rounded-xl bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100 font-medium"
            />
          </div>
        </div>
      </div>

      {/* Activity Log Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        {isLoading ? (
          <div className="py-12 text-center text-xs text-slate-400 flex items-center justify-center gap-2">
            <Loader2 className="w-4 h-4 animate-spin text-indigo-500" /> Loading project activity ledger...
          </div>
        ) : filteredActivities.length === 0 ? (
          <div className="py-12 text-center text-xs text-slate-400">
            No project activity recorded yet.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-400 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-3 px-4">Action (WHAT)</th>
                  <th className="py-3 px-4">Executed By (WHO)</th>
                  <th className="py-3 px-4">Target / Field</th>
                  <th className="py-3 px-4">Previous State (BEFORE)</th>
                  <th className="py-3 px-4">New State (AFTER)</th>
                  <th className="py-3 px-4">Timestamp (WHEN)</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
                {filteredActivities.map((act) => (
                  <tr key={act.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="py-3.5 px-4 font-mono font-extrabold text-[11px] text-indigo-600 dark:text-indigo-400">
                      {act.action}
                    </td>

                    <td className="py-3.5 px-4 font-bold text-slate-900 dark:text-slate-100">
                      <div>{act.performedBy || 'SYSTEM'}</div>
                      <span className="text-[10px] text-slate-400 font-semibold">{act.userRole || 'ROLE_USER'}</span>
                    </td>

                    <td className="py-3.5 px-4 text-slate-700 dark:text-slate-300 font-medium">
                      {act.fieldModified || act.details || 'Project Record'}
                    </td>

                    <td className="py-3.5 px-4 text-slate-500 font-mono text-[11px]">
                      {act.oldValue || '—'}
                    </td>

                    <td className="py-3.5 px-4 font-mono text-[11px] text-emerald-600 dark:text-emerald-400 font-bold">
                      {act.newValue || '—'}
                    </td>

                    <td className="py-3.5 px-4 text-slate-400 text-[11px] font-mono whitespace-nowrap">
                      {act.timestamp ? new Date(act.timestamp).toLocaleString() : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

    </div>
  );
};
