import React from 'react';
import { FolderKanban, Users, Calendar, CheckCircle2, ChevronRight, Layers, ArrowUpRight } from 'lucide-react';
import { Link } from 'react-router-dom';

interface AssignedProjectCard {
  id: string;
  name: string;
  role: string;
  progress: number;
  status: 'In Sprint' | 'Final Testing' | 'Backlog';
  dueDate: string;
  completedTasks: number;
  totalTasks: number;
  teamAvatars: string[];
}

const ASSIGNED_PROJECTS: AssignedProjectCard[] = [
  {
    id: 'PRJ-101',
    name: 'TechKnife Cloud Infrastructure Migration',
    role: 'Lead Full Stack Engineer',
    progress: 78,
    status: 'In Sprint',
    dueDate: 'Nov 30, 2026',
    completedTasks: 18,
    totalTasks: 22,
    teamAvatars: [
      'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=150',
      'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=150',
      'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=150',
    ],
  },
  {
    id: 'PRJ-102',
    name: 'Enterprise Customer Portal V2',
    role: 'Frontend Module Lead',
    progress: 92,
    status: 'Final Testing',
    dueDate: 'Oct 28, 2026',
    completedTasks: 24,
    totalTasks: 26,
    teamAvatars: [
      'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=150',
      'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=150',
    ],
  },
];

export const ProjectProgressWidget: React.FC = () => {
  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-5 flex flex-col justify-between h-full transition-all hover:border-indigo-500/30">
      {/* Widget Header */}
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
            <FolderKanban className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Active Project Progress</h3>
            <p className="text-[11px] text-slate-500">Your assigned sprint deliverables & milestones</p>
          </div>
        </div>

        <Link
          to="/projects"
          className="text-xs font-bold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center gap-1"
        >
          All Projects <ChevronRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      {/* Projects Cards List */}
      <div className="space-y-4 flex-1">
        {ASSIGNED_PROJECTS.map((prj) => (
          <div
            key={prj.id}
            className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-950/60 border border-slate-200/80 dark:border-slate-800 space-y-3 transition-all hover:shadow-md"
          >
            <div className="flex items-start justify-between gap-3">
              <div>
                <div className="flex items-center gap-2 mb-1">
                  <span className="px-2 py-0.5 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 font-mono text-[10px] font-bold rounded-md">
                    {prj.id}
                  </span>
                  <span className="text-[10px] font-bold uppercase tracking-wider text-emerald-500 bg-emerald-500/10 px-2 py-0.5 rounded-full">
                    {prj.status}
                  </span>
                </div>
                <h4 className="text-sm font-extrabold text-slate-900 dark:text-white leading-tight">
                  {prj.name}
                </h4>
                <p className="text-[11px] font-medium text-slate-500 mt-0.5">Role: {prj.role}</p>
              </div>

              <div className="text-right shrink-0">
                <span className="text-lg font-black font-mono text-indigo-600 dark:text-indigo-400">
                  {prj.progress}%
                </span>
                <span className="text-[10px] text-slate-400 block">Sprint Velocity</span>
              </div>
            </div>

            {/* Progress Bar */}
            <div className="space-y-1">
              <div className="w-full h-2 bg-slate-200 dark:bg-slate-800 rounded-full overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-indigo-500 to-emerald-500 rounded-full transition-all duration-500"
                  style={{ width: `${prj.progress}%` }}
                />
              </div>
            </div>

            {/* Footer details: Team & Deadline */}
            <div className="flex items-center justify-between text-[11px] pt-1">
              <div className="flex items-center gap-2">
                <div className="flex -space-x-2 overflow-hidden">
                  {prj.teamAvatars.map((url, i) => (
                    <img
                      key={i}
                      src={url}
                      alt="Team Member"
                      className="inline-block h-6 w-6 rounded-full ring-2 ring-white dark:ring-slate-900 object-cover"
                    />
                  ))}
                </div>
                <span className="text-slate-400 font-medium">
                  {prj.completedTasks}/{prj.totalTasks} Tasks
                </span>
              </div>

              <span className="flex items-center gap-1 font-mono text-slate-500">
                <Calendar className="w-3 h-3 text-indigo-500" /> Due {prj.dueDate}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
