import React from 'react';
import { BarChart3, TrendingUp, CheckCircle2, Clock, AlertTriangle, Zap, Target } from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';

interface ProjectAnalyticsTabProps {
  project: EnterpriseProject;
}

export const ProjectAnalyticsTab: React.FC<ProjectAnalyticsTabProps> = ({ project }) => {
  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-cyan-600 dark:text-cyan-400 mb-1">
          <BarChart3 className="w-4 h-4" />
          <span>Engineering Velocity & Output</span>
        </div>
        <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
          Productivity & Velocity Analytics
        </h3>
        <p className="text-xs text-slate-500">Monitor iteration velocity, story points completion rate, and delivery health</p>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs space-y-2">
          <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
            <span>Sprint Velocity</span>
            <Zap className="w-4 h-4 text-cyan-500" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white font-mono">
            42 <span className="text-xs text-emerald-500 font-sans font-bold">PTS/Sprint</span>
          </div>
          <p className="text-[11px] text-slate-500 font-medium">+12% vs last 3 iterations</p>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs space-y-2">
          <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
            <span>Task Completion Rate</span>
            <CheckCircle2 className="w-4 h-4 text-emerald-500" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white font-mono">
            88.5%
          </div>
          <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-bold">High delivery efficiency</p>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs space-y-2">
          <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
            <span>On-Time Milestone Rate</span>
            <Target className="w-4 h-4 text-indigo-500" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white font-mono">
            94%
          </div>
          <p className="text-[11px] text-slate-500 font-medium">16/17 milestones met</p>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs space-y-2">
          <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
            <span>Open Blockers</span>
            <AlertTriangle className="w-4 h-4 text-amber-500" />
          </div>
          <div className="text-2xl font-black text-amber-600 dark:text-amber-400 font-mono">
            2
          </div>
          <p className="text-[11px] text-slate-500 font-medium">Requires triage</p>
        </div>

      </div>

      {/* Task Status Distribution & Delivery Performance */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
            Task Status Breakdown
          </h4>

          <div className="space-y-3 text-xs font-medium">
            <div>
              <div className="flex justify-between mb-1">
                <span>Completed Tasks</span>
                <span className="font-mono font-bold text-emerald-500">65% (26 Tasks)</span>
              </div>
              <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                <div className="h-full bg-emerald-500 rounded-full" style={{ width: '65%' }} />
              </div>
            </div>

            <div>
              <div className="flex justify-between mb-1">
                <span>In Progress</span>
                <span className="font-mono font-bold text-cyan-500">22% (9 Tasks)</span>
              </div>
              <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                <div className="h-full bg-cyan-500 rounded-full" style={{ width: '22%' }} />
              </div>
            </div>

            <div>
              <div className="flex justify-between mb-1">
                <span>Code Review</span>
                <span className="font-mono font-bold text-indigo-500">8% (3 Tasks)</span>
              </div>
              <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                <div className="h-full bg-indigo-500 rounded-full" style={{ width: '8%' }} />
              </div>
            </div>

            <div>
              <div className="flex justify-between mb-1">
                <span>Backlog</span>
                <span className="font-mono font-bold text-slate-400">5% (2 Tasks)</span>
              </div>
              <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                <div className="h-full bg-slate-400 rounded-full" style={{ width: '5%' }} />
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
            Quality & SLA Metrics
          </h4>

          <div className="space-y-3 text-xs font-medium">
            <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-2xl border border-slate-200/80 dark:border-slate-800 flex justify-between items-center">
              <div>
                <span className="font-bold text-slate-900 dark:text-white block">Avg Bug Resolution Time</span>
                <span className="text-[11px] text-slate-500">SLA target: &lt; 24 hours</span>
              </div>
              <span className="font-mono text-sm font-black text-cyan-500">14.2 Hours</span>
            </div>

            <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-2xl border border-slate-200/80 dark:border-slate-800 flex justify-between items-center">
              <div>
                <span className="font-bold text-slate-900 dark:text-white block">PR Review Cycle Time</span>
                <span className="text-[11px] text-slate-500">SLA target: &lt; 8 hours</span>
              </div>
              <span className="font-mono text-sm font-black text-emerald-500">4.5 Hours</span>
            </div>

            <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-2xl border border-slate-200/80 dark:border-slate-800 flex justify-between items-center">
              <div>
                <span className="font-bold text-slate-900 dark:text-white block">Build Success Rate</span>
                <span className="text-[11px] text-slate-500">CI/CD Pipeline</span>
              </div>
              <span className="font-mono text-sm font-black text-indigo-500">98.4%</span>
            </div>
          </div>
        </div>

      </div>

    </div>
  );
};
