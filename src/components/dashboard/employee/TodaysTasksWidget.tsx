import React, { useState } from 'react';
import {
  CheckSquare,
  Plus,
  Clock,
  AlertCircle,
  Filter,
  CheckCircle2,
  ChevronRight,
  MoreVertical,
  X,
  Loader2,
} from 'lucide-react';
import { Link } from 'react-router-dom';

interface TaskItem {
  id: string;
  title: string;
  project: string;
  priority: 'Urgent' | 'High' | 'Medium' | 'Low';
  dueTime: string;
  status: 'Backlog' | 'In Progress' | 'Code Review' | 'Completed';
}

const INITIAL_TODAYS_TASKS: TaskItem[] = [
  {
    id: 'TSK-101',
    title: 'Implement Responsive Navigation with Role Filter',
    project: 'Frontend Portal V2',
    priority: 'High',
    dueTime: '11:30 AM',
    status: 'In Progress',
  },
  {
    id: 'TSK-102',
    title: 'Connect DTO Validation Schemas for User Controller',
    project: 'Backend Core API',
    priority: 'Urgent',
    dueTime: '02:00 PM',
    status: 'Code Review',
  },
  {
    id: 'TSK-103',
    title: 'Write Unit Tests for Payroll Disbursal Service',
    project: 'Financial Engine',
    priority: 'Medium',
    dueTime: '04:30 PM',
    status: 'Backlog',
  },
  {
    id: 'TSK-104',
    title: 'Update GitHub Webhook Action for Production Build',
    project: 'DevOps & Infra',
    priority: 'Low',
    dueTime: '05:45 PM',
    status: 'Completed',
  },
];

export const TodaysTasksWidget: React.FC = () => {
  const [tasks, setTasks] = useState<TaskItem[]>(INITIAL_TODAYS_TASKS);
  const [filter, setFilter] = useState<'all' | 'pending' | 'completed'>('all');
  const [showAddModal, setShowAddModal] = useState(false);

  // Form state
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newTaskProject, setNewTaskProject] = useState('Frontend Portal V2');
  const [newTaskPriority, setNewTaskPriority] = useState<'Urgent' | 'High' | 'Medium' | 'Low'>('High');
  const [newTaskDue, setNewTaskDue] = useState('05:00 PM');

  const handleAdvanceStatus = (id: string) => {
    const cycle: TaskItem['status'][] = ['Backlog', 'In Progress', 'Code Review', 'Completed'];
    setTasks(
      tasks.map((t) => {
        if (t.id !== id) return t;
        const currentIdx = cycle.indexOf(t.status);
        const nextStatus = cycle[(currentIdx + 1) % cycle.length];
        return { ...t, status: nextStatus };
      })
    );
  };

  const handleAddTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTaskTitle.trim()) return;

    const newTask: TaskItem = {
      id: `TSK-${Math.floor(100 + Math.random() * 900)}`,
      title: newTaskTitle.trim(),
      project: newTaskProject,
      priority: newTaskPriority,
      dueTime: newTaskDue,
      status: 'In Progress',
    };

    setTasks([newTask, ...tasks]);
    setNewTaskTitle('');
    setShowAddModal(false);
  };

  const filteredTasks = tasks.filter((t) => {
    if (filter === 'pending') return t.status !== 'Completed';
    if (filter === 'completed') return t.status === 'Completed';
    return true;
  });

  const completedCount = tasks.filter((t) => t.status === 'Completed').length;

  const getPriorityBadge = (priority: TaskItem['priority']) => {
    switch (priority) {
      case 'Urgent':
        return 'bg-rose-500/10 text-rose-500 border-rose-500/20';
      case 'High':
        return 'bg-amber-500/10 text-amber-500 border-amber-500/20';
      case 'Medium':
        return 'bg-indigo-500/10 text-indigo-500 border-indigo-500/20';
      default:
        return 'bg-slate-500/10 text-slate-500 border-slate-500/20';
    }
  };

  const getStatusBadge = (status: TaskItem['status']) => {
    switch (status) {
      case 'Completed':
        return 'bg-emerald-500/10 text-emerald-500 border-emerald-500/20';
      case 'Code Review':
        return 'bg-purple-500/10 text-purple-500 border-purple-500/20';
      case 'In Progress':
        return 'bg-indigo-500/10 text-indigo-500 border-indigo-500/20';
      default:
        return 'bg-slate-500/10 text-slate-400 border-slate-500/20';
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-5 flex flex-col justify-between h-full relative transition-all hover:border-indigo-500/30">
      {/* Widget Header */}
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
            <CheckSquare className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <span>Today's Task Queue</span>
              <span className="px-2 py-0.5 bg-indigo-600 text-white text-[10px] font-mono font-bold rounded-full">
                {completedCount}/{tasks.length} Done
              </span>
            </h3>
            <p className="text-[11px] text-slate-500">Scheduled deliverables for today's sprint</p>
          </div>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="p-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl shadow transition-transform hover:scale-105"
          title="Add Micro-task"
        >
          <Plus className="w-4 h-4" />
        </button>
      </div>

      {/* Filter Chips */}
      <div className="flex items-center justify-between gap-2">
        <div className="flex gap-1.5">
          {(['all', 'pending', 'completed'] as const).map((f) => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={`px-3 py-1 text-[11px] font-bold rounded-xl capitalize transition-all ${
                filter === f
                  ? 'bg-slate-900 text-white dark:bg-slate-100 dark:text-slate-900 shadow-xs'
                  : 'bg-slate-100 dark:bg-slate-800 text-slate-500 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              {f}
            </button>
          ))}
        </div>

        <Link
          to="/projects"
          className="text-[11px] font-bold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center gap-1"
        >
          Kanban Board <ChevronRight className="w-3 h-3" />
        </Link>
      </div>

      {/* Task List */}
      <div className="space-y-2.5 flex-1 overflow-y-auto max-h-[310px] pr-1">
        {filteredTasks.length === 0 ? (
          <div className="text-center py-8 text-slate-400 text-xs">No tasks match this filter.</div>
        ) : (
          filteredTasks.map((t) => (
            <div
              key={t.id}
              className={`p-3.5 rounded-2xl border transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-3 ${
                t.status === 'Completed'
                  ? 'bg-emerald-500/5 border-emerald-500/20 text-slate-500'
                  : 'bg-slate-50 dark:bg-slate-950/60 border-slate-200/80 dark:border-slate-800 hover:border-indigo-400'
              }`}
            >
              <div className="space-y-1 min-w-0 flex-1">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="font-mono text-[10px] font-bold text-slate-400">{t.id}</span>
                  <span className={`text-xs font-bold ${t.status === 'Completed' ? 'line-through text-slate-400' : 'text-slate-900 dark:text-slate-100'}`}>
                    {t.title}
                  </span>
                </div>

                <div className="flex flex-wrap items-center gap-2 text-[11px] text-slate-500">
                  <span className="font-semibold text-slate-700 dark:text-slate-300">{t.project}</span>
                  <span>•</span>
                  <span className="flex items-center gap-1">
                    <Clock className="w-3 h-3 text-slate-400" /> Due {t.dueTime}
                  </span>
                </div>
              </div>

              <div className="flex items-center gap-2 shrink-0 self-end sm:self-auto">
                <span className={`px-2 py-0.5 text-[10px] font-bold rounded-full border ${getPriorityBadge(t.priority)}`}>
                  {t.priority}
                </span>

                <button
                  onClick={() => handleAdvanceStatus(t.id)}
                  className={`px-3 py-1 rounded-xl text-[11px] font-bold border transition-colors flex items-center gap-1 ${getStatusBadge(t.status)}`}
                  title="Click to advance status"
                >
                  {t.status === 'Completed' && <CheckCircle2 className="w-3 h-3" />}
                  {t.status}
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Add Task Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 max-w-md w-full shadow-2xl space-y-4 relative">
            <button
              onClick={() => setShowAddModal(false)}
              className="absolute top-4 right-4 p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl bg-slate-100 dark:bg-slate-800"
            >
              <X className="w-4 h-4" />
            </button>

            <h4 className="text-base font-extrabold text-slate-900 dark:text-white">Add Micro Task for Today</h4>

            <form onSubmit={handleAddTask} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold text-slate-400 uppercase tracking-wider mb-1">Task Title *</label>
                <input
                  type="text"
                  required
                  value={newTaskTitle}
                  onChange={(e) => setNewTaskTitle(e.target.value)}
                  placeholder="e.g., Refactor Auth API Middleware"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-400 uppercase tracking-wider mb-1">Project</label>
                  <select
                    value={newTaskProject}
                    onChange={(e) => setNewTaskProject(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                  >
                    <option value="Frontend Portal V2">Frontend Portal V2</option>
                    <option value="Backend Core API">Backend Core API</option>
                    <option value="Financial Engine">Financial Engine</option>
                    <option value="DevOps & Infra">DevOps & Infra</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-400 uppercase tracking-wider mb-1">Priority</label>
                  <select
                    value={newTaskPriority}
                    onChange={(e) => setNewTaskPriority(e.target.value as any)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                  >
                    <option value="Urgent">Urgent</option>
                    <option value="High">High</option>
                    <option value="Medium">Medium</option>
                    <option value="Low">Low</option>
                  </select>
                </div>
              </div>

              <button
                type="submit"
                className="w-full py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl shadow transition-colors flex items-center justify-center gap-2"
              >
                <Plus className="w-4 h-4" /> Add Task
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
