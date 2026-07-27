import React, { useState } from 'react';
import {
  FolderKanban,
  CheckCircle2,
  Clock,
  Calendar,
  Layers,
  CheckSquare,
  Users,
} from 'lucide-react';

interface AssignedProject {
  id: string;
  name: string;
  myRole: string;
  department: string;
  progress: number;
  status: 'In Progress' | 'Review' | 'Completed';
  deadline: string;
  teamCount: number;
  tasks: { id: string; title: string; completed: boolean }[];
}

const MOCK_PROJECTS: AssignedProject[] = [
  {
    id: 'PRJ-101',
    name: 'TechKnife Cloud Infrastructure Migration',
    myRole: 'Lead Full Stack Engineer',
    department: 'Cloud Solutions',
    progress: 78,
    status: 'In Progress',
    deadline: '2026-11-30',
    teamCount: 6,
    tasks: [
      { id: 't1', title: 'Implement JWT refresh token rotation API', completed: true },
      { id: 't2', title: 'Migrate PostgreSQL database schema with Drizzle', completed: true },
      { id: 't3', title: 'Configure CDN caching & Redis session store', completed: false },
      { id: 't4', title: 'Setup automated load testing scripts', completed: false },
    ],
  },
  {
    id: 'PRJ-102',
    name: 'Enterprise Customer Portal V2',
    myRole: 'Frontend Module Lead',
    department: 'Client Engineering',
    progress: 92,
    status: 'Review',
    deadline: '2026-10-15',
    teamCount: 4,
    tasks: [
      { id: 't5', title: 'Build responsive Analytics Dashboard widgets', completed: true },
      { id: 't6', title: 'Integrate real-time notification WebSocket alerts', completed: true },
      { id: 't7', title: 'Accessibility WCAG 2.1 compliance audit', completed: true },
    ],
  },
  {
    id: 'PRJ-103',
    name: 'AI Agent Grounding & RAG Integration',
    myRole: 'API Integration Specialist',
    department: 'AI Systems',
    progress: 45,
    status: 'In Progress',
    deadline: '2026-12-20',
    teamCount: 5,
    tasks: [
      { id: 't8', title: 'Setup Gemini 1.5 Pro embeddings pipeline', completed: true },
      { id: 't9', title: 'Implement Vector database semantic indexer', completed: false },
      { id: 't10', title: 'Build agent tools schema & rate limiters', completed: false },
    ],
  },
];

export const AssignedProjectsTab: React.FC = () => {
  const [projects, setProjects] = useState<AssignedProject[]>(MOCK_PROJECTS);

  const handleToggleTask = (projectId: string, taskId: string) => {
    setProjects(
      projects.map((p) => {
        if (p.id !== projectId) return p;
        const updatedTasks = p.tasks.map((t) =>
          t.id === taskId ? { ...t, completed: !t.completed } : t
        );
        const completedCount = updatedTasks.filter((t) => t.completed).length;
        const newProgress = Math.round((completedCount / updatedTasks.length) * 100);
        return { ...p, tasks: updatedTasks, progress: newProgress };
      })
    );
  };

  return (
    <div className="space-y-8">
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg">
        <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-bold text-xs uppercase tracking-wider mb-1">
          <FolderKanban className="w-4 h-4" />
          <span>Assigned Deliverables & Sprint Projects</span>
        </div>
        <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
          Active Assigned Projects ({projects.length})
        </h3>
        <p className="text-xs text-slate-500">
          Review your project roles, target completion milestones, and assigned sprint task items
        </p>
      </div>

      {/* Projects List */}
      <div className="space-y-6">
        {projects.map((project) => (
          <div
            key={project.id}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-5"
          >
            {/* Top Bar */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
              <div>
                <div className="flex items-center gap-2 mb-1">
                  <span className="px-2.5 py-0.5 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 font-mono text-[10px] font-bold rounded-full border border-indigo-500/20">
                    {project.id}
                  </span>
                  <span className="text-xs text-slate-400 font-medium">
                    {project.department}
                  </span>
                </div>
                <h4 className="text-lg font-extrabold text-slate-900 dark:text-white">
                  {project.name}
                </h4>
                <p className="text-xs text-indigo-600 dark:text-indigo-400 font-bold mt-0.5">
                  Your Role: {project.myRole}
                </p>
              </div>

              <div className="flex items-center gap-3">
                <span className="text-xs text-slate-400 flex items-center gap-1 font-mono">
                  <Calendar className="w-3.5 h-3.5" /> Due: {project.deadline}
                </span>
                <span className="text-xs text-slate-400 flex items-center gap-1 font-mono">
                  <Users className="w-3.5 h-3.5" /> {project.teamCount} Members
                </span>
              </div>
            </div>

            {/* Progress Bar */}
            <div className="space-y-1.5">
              <div className="flex justify-between text-xs font-bold">
                <span className="text-slate-500 uppercase tracking-wider text-[10px]">
                  Sprint Completion Rate
                </span>
                <span className="text-indigo-600 dark:text-indigo-400 font-mono">
                  {project.progress}%
                </span>
              </div>
              <div className="w-full h-2.5 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-indigo-500 to-emerald-500 rounded-full transition-all duration-500"
                  style={{ width: `${project.progress}%` }}
                />
              </div>
            </div>

            {/* Task Items Checklist */}
            <div className="pt-2">
              <h5 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3 flex items-center gap-1.5">
                <CheckSquare className="w-3.5 h-3.5 text-indigo-500" />
                Assigned Sprint Tasks ({project.tasks.filter((t) => t.completed).length} / {project.tasks.length})
              </h5>

              <div className="space-y-2">
                {project.tasks.map((task) => (
                  <label
                    key={task.id}
                    className={`flex items-center gap-3 p-3 rounded-2xl border transition-all cursor-pointer text-xs font-medium ${
                      task.completed
                        ? 'bg-emerald-500/5 border-emerald-500/20 text-slate-500 line-through dark:text-slate-400'
                        : 'bg-slate-50 dark:bg-slate-800/50 border-slate-200/80 dark:border-slate-800 text-slate-900 dark:text-slate-200 hover:border-indigo-400'
                    }`}
                  >
                    <input
                      type="checkbox"
                      checked={task.completed}
                      onChange={() => handleToggleTask(project.id, task.id)}
                      className="w-4 h-4 rounded text-indigo-600 focus:ring-indigo-500 cursor-pointer"
                    />
                    <span>{task.title}</span>
                  </label>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
