import React, { useState, useEffect } from 'react';
import { 
  Activity, Shield, Clock, RefreshCw, Loader2, User 
} from 'lucide-react';
import { EnterpriseProject, projectsApi, ProjectActivity } from '../../../api/projects';

interface ProjectAuditTabProps {
  project: EnterpriseProject;
}

export const ProjectAuditTab: React.FC<ProjectAuditTabProps> = ({ project }) => {
  const projectId = project.id || project.projectId || '';

  const [activities, setActivities] = useState<ProjectActivity[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const loadActivities = async () => {
    if (!projectId) return;
    setIsLoading(true);
    try {
      const res = await projectsApi.getActivities(projectId);
      if (res.data) {
        setActivities(res.data);
      }
    } catch (err) {
      console.warn('Failed to load project activities');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadActivities();
  }, [projectId]);

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
            <p className="text-xs text-slate-500">Track status transitions, member assignments, and link updates</p>
          </div>

          <button
            onClick={loadActivities}
            className="px-4 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-all shadow-xs flex items-center gap-1.5 self-start sm:self-auto"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${isLoading ? 'animate-spin' : ''}`} /> Refresh Stream
          </button>
        </div>
      </div>

      {/* Activity Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        {isLoading ? (
          <div className="py-12 text-center text-xs text-slate-400 flex items-center justify-center gap-2">
            <Loader2 className="w-4 h-4 animate-spin text-indigo-500" /> Loading project activity trail...
          </div>
        ) : activities.length === 0 ? (
          <div className="py-12 text-center text-xs text-slate-400">
            No activity trail logged for this project yet.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-400 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-3 px-4">Action</th>
                  <th className="py-3 px-4">Executed By</th>
                  <th className="py-3 px-4">Modified Field</th>
                  <th className="py-3 px-4">Previous Value</th>
                  <th className="py-3 px-4">New Value</th>
                  <th className="py-3 px-4">Timestamp</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
                {activities.map((act) => (
                  <tr key={act.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="py-3.5 px-4 font-mono font-extrabold text-[11px] text-indigo-600 dark:text-indigo-400">
                      {act.action}
                    </td>

                    <td className="py-3.5 px-4 font-bold text-slate-900 dark:text-slate-100">
                      <div>{act.performedBy}</div>
                      <span className="text-[10px] text-slate-400 font-semibold">{act.userRole}</span>
                    </td>

                    <td className="py-3.5 px-4 text-slate-700 dark:text-slate-300 font-medium">
                      {act.fieldModified || 'General Metadata'}
                    </td>

                    <td className="py-3.5 px-4 text-slate-500 font-mono text-[11px]">
                      {act.oldValue || '—'}
                    </td>

                    <td className="py-3.5 px-4 font-mono text-[11px] text-emerald-600 dark:text-emerald-400 font-bold">
                      {act.newValue || '—'}
                    </td>

                    <td className="py-3.5 px-4 text-slate-400 text-[11px] font-mono whitespace-nowrap">
                      {new Date(act.timestamp).toLocaleString()}
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
