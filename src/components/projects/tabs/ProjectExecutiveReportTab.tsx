import React, { useState, useEffect } from 'react';
import { 
  FileText, Download, CheckCircle2, Clock, AlertTriangle, 
  Calendar, User, Layers, ArrowRight, Loader2, Sparkles 
} from 'lucide-react';
import { toast } from 'sonner';
import { EnterpriseProject } from '../../../api/projects';
import { projectWorkspaceApi, ProjectTask, ProjectRisk } from '../../../api/projectWorkspaceApi';
import { apiClient } from '../../../api/client';

interface ProjectExecutiveReportTabProps {
  project: EnterpriseProject;
}

export const ProjectExecutiveReportTab: React.FC<ProjectExecutiveReportTabProps> = ({ project }) => {
  const projectId = project.id || project.projectId || project.projectCode || '';

  const [tasks, setTasks] = useState<ProjectTask[]>([]);
  const [risks, setRisks] = useState<ProjectRisk[]>([]);
  const [milestones, setMilestones] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isExporting, setIsExporting] = useState(false);

  useEffect(() => {
    const loadData = async () => {
      if (!projectId) return;
      setIsLoading(true);
      try {
        const [taskRes, riskRes, milestoneRes] = await Promise.allSettled([
          projectWorkspaceApi.getTasks(projectId),
          projectWorkspaceApi.getRisks(projectId),
          projectWorkspaceApi.getMilestones(projectId),
        ]);

        if (taskRes.status === 'fulfilled' && taskRes.value?.data) {
          setTasks(taskRes.value.data);
        }
        if (riskRes.status === 'fulfilled' && riskRes.value?.data) {
          setRisks(riskRes.value.data);
        }
        if (milestoneRes.status === 'fulfilled' && milestoneRes.value?.data) {
          setMilestones(milestoneRes.value.data);
        }
      } catch (err) {
        console.warn('Failed to load project report data:', err);
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [projectId]);

  // Derived real data arrays
  const totalTasks = tasks.length;
  const completedTasks = tasks.filter(t => 
    ['completed', 'done'].includes((t.status || '').toLowerCase())
  );
  const inProgressTasks = tasks.filter(t => 
    ['in progress', 'in_progress', 'doing'].includes((t.status || '').toLowerCase())
  );
  const openTasks = tasks.filter(t => 
    !['completed', 'done'].includes((t.status || '').toLowerCase())
  );

  const openRisks = risks.filter(r => 
    !['mitigated', 'closed', 'resolved'].includes((r.status || '').toLowerCase())
  );

  const upcomingTasks = openTasks
    .filter(t => t.dueDate)
    .sort((a, b) => new Date(a.dueDate!).getTime() - new Date(b.dueDate!).getTime());

  const dynamicSummary = `${project.projectName || 'Project'} (${project.projectCode || 'TK-PRJ'}) is currently in ${project.status || 'ACTIVE'} status with ${project.progressPercentage || project.overallProgressPercentage || 0}% overall progress. ${completedTasks.length} of ${totalTasks} tasks are completed, ${inProgressTasks.length} task(s) currently in progress, and ${openRisks.length} open risk(s) recorded in project governance.`;

  const handleExportCsv = async () => {
    setIsExporting(true);
    try {
      const res = await apiClient.get(`/project/reports/export/csv`, {
        params: { reportType: 'PROJECT_STATUS', projectId },
        responseType: 'text',
      });

      const blob = new Blob([res.data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${project.projectCode || 'project'}_executive_report.csv`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      toast.success('Executive Report exported successfully.');
    } catch (err: any) {
      toast.error('Failed to export CSV report.');
    } finally {
      setIsExporting(false);
    }
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 mb-1">
            <FileText className="w-4 h-4" />
            <span>Executive Intelligence Summary</span>
          </div>
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
            Project Executive Report — {project.projectName || project.projectCode}
          </h3>
          <p className="text-xs text-slate-500">Real work-in-progress status, task completions, and upcoming milestones</p>
        </div>

        <button
          type="button"
          onClick={handleExportCsv}
          disabled={isExporting}
          className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 shrink-0 cursor-pointer disabled:opacity-50"
        >
          <Download className="w-4 h-4" />
          <span>{isExporting ? 'Exporting...' : 'Export Executive Report (CSV)'}</span>
        </button>
      </div>

      {isLoading ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl space-y-2">
          <Loader2 className="w-6 h-6 animate-spin text-indigo-500 mx-auto" />
          <p className="text-xs text-slate-400 font-medium">Generating executive project report...</p>
        </div>
      ) : (
        <>
          {/* Generated Narrative Executive Summary Card */}
          <div className="p-5 rounded-3xl bg-indigo-500/10 border border-indigo-500/20 text-indigo-900 dark:text-indigo-200 space-y-2 shadow-xs">
            <div className="flex items-center gap-2 text-xs font-black uppercase text-indigo-600 dark:text-indigo-400">
              <Sparkles className="w-4 h-4" />
              <span>Calculated Executive Summary</span>
            </div>
            <p className="text-xs font-semibold leading-relaxed">
              {dynamicSummary}
            </p>
          </div>

          {/* Key Executive Metadata Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 text-xs">
            <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1">
              <span className="text-[10px] text-slate-400 font-bold uppercase">Current Status</span>
              <div className="font-extrabold text-indigo-600 dark:text-indigo-400 truncate">
                {project.status || 'ACTIVE'}
              </div>
            </div>

            <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1">
              <span className="text-[10px] text-slate-400 font-bold uppercase">Overall Progress</span>
              <div className="font-extrabold text-emerald-600 dark:text-emerald-400 font-mono text-sm">
                {project.progressPercentage || project.overallProgressPercentage || 0}%
              </div>
            </div>

            <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1">
              <span className="text-[10px] text-slate-400 font-bold uppercase">Project Manager</span>
              <div className="font-bold text-slate-900 dark:text-white truncate">
                {project.projectManagerName || 'Rahul Garai (EMP-004)'}
              </div>
            </div>

            <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1">
              <span className="text-[10px] text-slate-400 font-bold uppercase">Project Lead</span>
              <div className="font-bold text-slate-900 dark:text-white truncate">
                {project.projectLeadName || 'Ganesh Pal (EMP-003)'}
              </div>
            </div>

            <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1">
              <span className="text-[10px] text-slate-400 font-bold uppercase">Start Date</span>
              <div className="font-mono text-slate-700 dark:text-slate-300 font-medium truncate">
                {project.startDate || 'Not specified'}
              </div>
            </div>

            <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1">
              <span className="text-[10px] text-slate-400 font-bold uppercase">Target Date</span>
              <div className="font-mono text-slate-700 dark:text-slate-300 font-medium truncate">
                {project.endDate || project.targetEndDate || 'Not specified'}
              </div>
            </div>
          </div>

          {/* Section: Work In Progress (WIP) */}
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h4 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <Clock className="w-4 h-4 text-cyan-500" /> Work In Progress ({inProgressTasks.length})
              </h4>
            </div>

            {inProgressTasks.length === 0 ? (
              <div className="py-8 text-center text-xs text-slate-400 italic">
                No work currently in progress.
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {inProgressTasks.map((t) => (
                  <div key={t.id} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-800 space-y-2">
                    <div className="flex items-center justify-between gap-2">
                      <span className="px-2 py-0.5 rounded-md bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 font-mono text-[10px] font-bold">
                        {t.taskCode || t.id}
                      </span>
                      <span className="text-[10px] font-bold uppercase text-amber-500 px-2 py-0.5 bg-amber-500/10 rounded-md">
                        IN PROGRESS
                      </span>
                    </div>

                    <h5 className="text-xs font-extrabold text-slate-900 dark:text-white">
                      {t.title}
                    </h5>

                    <div className="flex items-center justify-between text-[11px] text-slate-500 font-medium pt-1">
                      <span>Assigned to: <strong className="text-slate-700 dark:text-slate-200">{t.assigneeName || 'Assigned Member'}</strong></span>
                      {t.dueDate && <span className="font-mono text-slate-400">Due: {t.dueDate}</span>}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Section: Recent Completions */}
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h4 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-500" /> Recent Completions ({completedTasks.length})
              </h4>
            </div>

            {completedTasks.length === 0 ? (
              <div className="py-8 text-center text-xs text-slate-400 italic">
                No recent completions recorded.
              </div>
            ) : (
              <div className="space-y-2">
                {completedTasks.map((t) => (
                  <div key={t.id} className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 flex items-center justify-between gap-3 text-xs">
                    <div className="space-y-0.5">
                      <h5 className="font-bold text-slate-900 dark:text-slate-100">{t.title}</h5>
                      <span className="text-[11px] text-slate-500">Completed by: <strong className="text-slate-700 dark:text-slate-300">{t.assigneeName || 'Team Member'}</strong></span>
                    </div>
                    <span className="px-2.5 py-1 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 font-bold text-[10px] rounded-lg border border-emerald-500/20 shrink-0">
                      COMPLETED
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Section: Upcoming Work */}
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h4 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <Calendar className="w-4 h-4 text-indigo-500" /> Upcoming Work & Milestones ({upcomingTasks.length})
              </h4>
            </div>

            {upcomingTasks.length === 0 ? (
              <div className="py-8 text-center text-xs text-slate-400 italic">
                No upcoming work scheduled.
              </div>
            ) : (
              <div className="space-y-2">
                {upcomingTasks.slice(0, 5).map((t) => (
                  <div key={t.id} className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 flex items-center justify-between gap-3 text-xs">
                    <div>
                      <h5 className="font-bold text-slate-900 dark:text-slate-100">{t.title}</h5>
                      <span className="text-[11px] text-slate-500">Assignee: {t.assigneeName || 'Unassigned'}</span>
                    </div>
                    <span className="font-mono text-slate-500 text-[11px] font-semibold shrink-0">
                      Target: {t.dueDate}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </>
      )}

    </div>
  );
};
