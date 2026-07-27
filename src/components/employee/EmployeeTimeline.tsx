import React from 'react';
import { Award, GraduationCap, Briefcase, TrendingUp, UserCheck, Calendar } from 'lucide-react';
import { EmployeeTimelineEvent } from '../../api/employees';

interface EmployeeTimelineProps {
  events: EmployeeTimelineEvent[];
}

export const EmployeeTimeline: React.FC<EmployeeTimelineProps> = ({ events }) => {
  const getEventIcon = (type: EmployeeTimelineEvent['type']) => {
    switch (type) {
      case 'onboarding':
        return <UserCheck className="w-3.5 h-3.5 text-indigo-500" />;
      case 'promotion':
        return <TrendingUp className="w-3.5 h-3.5 text-emerald-500" />;
      case 'award':
        return <Award className="w-3.5 h-3.5 text-amber-500" />;
      case 'transfer':
        return <Briefcase className="w-3.5 h-3.5 text-cyan-500" />;
      case 'review':
      default:
        return <GraduationCap className="w-3.5 h-3.5 text-purple-500" />;
    }
  };

  if (!events || events.length === 0) {
    return (
      <div className="p-6 text-center text-xs text-slate-400 bg-slate-50 dark:bg-slate-800/40 rounded-2xl border border-dashed border-slate-200 dark:border-slate-800">
        No history timeline events logged for this staff member yet.
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2 text-xs font-bold text-slate-400 uppercase tracking-wider">
        <Calendar className="w-3.5 h-3.5 text-indigo-500" />
        <span>Career Milestones & Timeline History</span>
      </div>

      <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-slate-200 dark:before:bg-slate-800">
        {events.map((evt) => (
          <div key={evt.id} className="relative group">
            {/* Dot */}
            <div className="absolute -left-[1.375rem] top-1 w-6 h-6 rounded-full bg-white dark:bg-slate-900 border-2 border-slate-300 dark:border-slate-700 flex items-center justify-center shadow-xs group-hover:border-indigo-500 transition-colors">
              {getEventIcon(evt.type)}
            </div>

            {/* Event Card */}
            <div className="bg-slate-50 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-800 rounded-xl p-3.5 space-y-1 hover:border-indigo-300 dark:hover:border-indigo-700 transition-all">
              <div className="flex items-center justify-between">
                <span className="font-bold text-xs text-slate-900 dark:text-white">{evt.title}</span>
                <span className="text-[10px] font-mono text-slate-400">{evt.date}</span>
              </div>
              <p className="text-xs text-slate-600 dark:text-slate-300">{evt.description}</p>
              <div className="text-[10px] text-slate-400 font-semibold pt-1">
                Authorized / Logged by: <strong className="text-slate-700 dark:text-slate-300">{evt.actor}</strong>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
