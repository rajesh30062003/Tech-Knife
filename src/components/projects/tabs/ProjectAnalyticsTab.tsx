import React, { useState, useEffect } from 'react';
import { 
  BarChart3, CheckCircle2, AlertTriangle, Target, Zap, Clock, Loader2 
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';
import { projectWorkspaceApi, ProjectTask, ProjectRisk } from '../../../api/projectWorkspaceApi';

interface ProjectAnalyticsTabProps {
  project: EnterpriseProject;
}

export const ProjectAnalyticsTab: React.FC<ProjectAnalyticsTabProps> = ({ project }) => {
  const projectId = project.id || project.projectId || project.projectCode || '';

  const [tasks, setTasks] = useState<ProjectTask[]>([]);
  const [risks, setRisks] = useState<ProjectRisk[]>([]);
  const [milestones, setMilestones] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);

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
        console.warn('Failed to load project analytics data:', err);
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [projectId]);

  // Derived real metrics
  const totalTasks = tasks.length;
  const completedTasks = tasks.filter(t => 
    ['completed', 'done'].includes((t.status || '').toLowerCase())
  ).length;
  const inProgressTasks = tasks.filter(t => 
    ['in progress', 'in_progress', 'doing'].includes((t.status || '').toLowerCase())
  ).length;
  const codeReviewTasks = tasks.filter(t => 
    ['code review', 'code_review', 'review'].includes((t.status || '').toLowerCase())
  ).length;
  const pendingTasks = tasks.filter(t => 
    ['backlog', 'pending', 'todo'].includes((t.status || '').toLowerCase())
  ).length;

  const taskCompletionRate = totalTasks > 0 
    ? ((completedTasks / totalTasks) * 100).toFixed(1)
    : '0';

  const openBlockers = risks.filter(r => 
    !['mitigated', 'closed', 'resolved'].includes((r.status || '').toLowerCase())
  ).length;

  const totalMilestones = milestones.length;
  const completedMilestones = milestones.filter(m => 
    ['completed', 'achieved'].includes((m.status || '').toLowerCase())
  ).length;

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-cyan-600 dark:text-cyan-400 mb-1">
          <BarChart3 className="w-4 h-4" />
          <span>Calculated Engineering Metrics</span>
        </div>
        <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
          Productivity Analytics — {project.projectName || project.projectCode}
        </h3>
        <p className="text-xs text-slate-500">Live operational output derived from stored project tasks and milestones</p>
      </div>

      {isLoading ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl space-y-2">
          <Loader2 className="w-6 h-6 animate-spin text-cyan-500 mx-auto" />
          <p className="text-xs text-slate-400 font-medium">Calculating live project metrics...</p>
        </div>
      ) : (
        <>
          {/* KPI Cards Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs space-y-2">
              <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
                <span>Total Tasks</span>
                <CheckCircle2 className="w-4 h-4 text-cyan-500" />
              </div>
              <div className="text-2xl font-black text-slate-900 dark:text-white font-mono">
                {totalTasks}
              </div>
              <p className="text-[11px] text-slate-500 font-medium">{completedTasks} completed</p>
            </div>

            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs space-y-2">
              <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
                <span>Task Completion Rate</span>
                <CheckCircle2 className="w-4 h-4 text-emerald-500" />
              </div>
              <div className="text-2xl font-black text-slate-900 dark:text-white font-mono">
                {taskCompletionRate}%
              </div>
              <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-bold">
                {totalTasks > 0 ? `${completedTasks} of ${totalTasks} done` : 'No tasks recorded'}
              </p>
            </div>

            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs space-y-2">
              <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
                <span>Milestone Progress</span>
                <Target className="w-4 h-4 text-indigo-500" />
              </div>
              <div className="text-2xl font-black text-slate-900 dark:text-white font-mono">
                {totalMilestones > 0 ? `${completedMilestones}/${totalMilestones}` : '0/0'}
              </div>
              <p className="text-[11px] text-slate-500 font-medium">
                {totalMilestones > 0 ? `${Math.round((completedMilestones / totalMilestones) * 100)}% achieved` : 'No milestones set'}
              </p>
            </div>

            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs space-y-2">
              <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
                <span>Open Blockers</span>
                <AlertTriangle className="w-4 h-4 text-amber-500" />
              </div>
              <div className="text-2xl font-black text-amber-600 dark:text-amber-400 font-mono">
                {openBlockers}
              </div>
              <p className="text-[11px] text-slate-500 font-medium">
                {openBlockers > 0 ? 'Active risks require triage' : '0 open blockers'}
              </p>
            </div>

          </div>

          {/* Task Status Breakdown & Performance Section */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            
            {/* Task Status Breakdown */}
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
              <h4 className="text-sm font-extrabold text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
                Task Status Breakdown
              </h4>

              {totalTasks === 0 ? (
                <div className="py-8 text-center text-xs text-slate-400 italic">
                  No tasks recorded for this project.
                </div>
              ) : (
                <div className="space-y-3 text-xs font-medium">
                  <div>
                    <div className="flex justify-between mb-1">
                      <span>Completed Tasks</span>
                      <span className="font-mono font-bold text-emerald-500">
                        {Math.round((completedTasks / totalTasks) * 100)}% ({completedTasks} Tasks)
                      </span>
                    </div>
                    <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-emerald-500 rounded-full" 
                        style={{ width: `${(completedTasks / totalTasks) * 100}%` }} 
                      />
                    </div>
                  </div>

                  <div>
                    <div className="flex justify-between mb-1">
                      <span>In Progress</span>
                      <span className="font-mono font-bold text-cyan-500">
                        {Math.round((inProgressTasks / totalTasks) * 100)}% ({inProgressTasks} Tasks)
                      </span>
                    </div>
                    <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-cyan-500 rounded-full" 
                        style={{ width: `${(inProgressTasks / totalTasks) * 100}%` }} 
                      />
                    </div>
                  </div>

                  <div>
                    <div className="flex justify-between mb-1">
                      <span>Code Review</span>
                      <span className="font-mono font-bold text-indigo-500">
                        {Math.round((codeReviewTasks / totalTasks) * 100)}% ({codeReviewTasks} Tasks)
                      </span>
                    </div>
                    <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-indigo-500 rounded-full" 
                        style={{ width: `${(codeReviewTasks / totalTasks) * 100}%` }} 
                      />
                    </div>
                  </div>

                  <div>
                    <div className="flex justify-between mb-1">
                      <span>Backlog / Pending</span>
                      <span className="font-mono font-bold text-slate-400">
                        {Math.round((pendingTasks / totalTasks) * 100)}% ({pendingTasks} Tasks)
                      </span>
                    </div>
                    <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-slate-400 rounded-full" 
                        style={{ width: `${(pendingTasks / totalTasks) * 100}%` }} 
                      />
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Sprint Velocity & Quality / SLA Metrics */}
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
              <h4 className="text-sm font-extrabold text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
                Velocity & Quality SLA Status
              </h4>

              <div className="space-y-3 text-xs font-medium">
                
                {/* Sprint Velocity */}
                <div className="p-4 bg-slate-50 dark:bg-slate-800/60 rounded-2xl border border-slate-200/80 dark:border-slate-800 flex justify-between items-center">
                  <div>
                    <span className="font-bold text-slate-900 dark:text-white block flex items-center gap-1.5">
                      <Zap className="w-3.5 h-3.5 text-cyan-500" /> Sprint Velocity
                    </span>
                    <span className="text-[11px] text-slate-500">Calculated story points per iteration</span>
                  </div>
                  <span className="font-mono text-xs font-bold text-slate-400 italic">
                    Not enough sprint data
                  </span>
                </div>

                {/* Quality / SLA */}
                <div className="p-4 bg-slate-50 dark:bg-slate-800/60 rounded-2xl border border-slate-200/80 dark:border-slate-800 flex justify-between items-center">
                  <div>
                    <span className="font-bold text-slate-900 dark:text-white block flex items-center gap-1.5">
                      <Clock className="w-3.5 h-3.5 text-indigo-500" /> Quality & SLA Metrics
                    </span>
                    <span className="text-[11px] text-slate-500">Bug resolution & review cycle time</span>
                  </div>
                  <span className="font-mono text-xs font-bold text-slate-400 italic">
                    No quality/SLA data available
                  </span>
                </div>

              </div>
            </div>

          </div>
        </>
      )}

    </div>
  );
};
