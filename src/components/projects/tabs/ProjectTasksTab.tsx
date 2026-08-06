import React, { useState, useEffect } from 'react';
import { 
  CheckSquare, Plus, Clock, AlertCircle, CheckCircle2, ChevronRight, 
  Loader2, Filter, User 
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';
import { projectWorkspaceApi, ProjectTask } from '../../../api/projectWorkspaceApi';
import { useAuth } from '../../../context/AuthContext';

interface ProjectTasksTabProps {
  project: EnterpriseProject;
}

const DEFAULT_MOCK_TASKS: ProjectTask[] = [
  {
    id: 'TSK-201',
    taskCode: 'TSK-201',
    title: 'Design Security & OAuth 2.0 Token Storage Entity Architecture',
    status: 'Completed',
    priority: 'Urgent',
    assigneeName: 'Subrata Pal',
    dueDate: '2026-08-10',
  },
  {
    id: 'TSK-202',
    taskCode: 'TSK-202',
    title: 'Build Enterprise Project Workspace Modal & Tab Container',
    status: 'In Progress',
    priority: 'High',
    assigneeName: 'Ranadhir Pal',
    dueDate: '2026-08-15',
  },
  {
    id: 'TSK-203',
    taskCode: 'TSK-203',
    title: 'Configure Google Drive OAuth Refresh Token Synchronization Pipeline',
    status: 'Completed',
    priority: 'Urgent',
    assigneeName: 'Vikramaditya Sharma',
    dueDate: '2026-08-05',
  },
  {
    id: 'TSK-204',
    taskCode: 'TSK-204',
    title: 'Perform Automated Integration Testing for Risk Register Endpoints',
    status: 'Backlog',
    priority: 'Medium',
    assigneeName: 'Anindita Chakraborty',
    dueDate: '2026-08-20',
  },
];

export const ProjectTasksTab: React.FC<ProjectTasksTabProps> = ({ project }) => {
  const { user } = useAuth();
  const projectId = project.id || project.projectId || '';

  const [tasks, setTasks] = useState<ProjectTask[]>(DEFAULT_MOCK_TASKS);
  const [isLoading, setIsLoading] = useState(false);
  const [filter, setFilter] = useState<string>('ALL');
  const [showAddModal, setShowAddModal] = useState(false);

  // Form state
  const [taskTitle, setTaskTitle] = useState('');
  const [taskPriority, setTaskPriority] = useState('High');
  const [taskAssignee, setTaskAssignee] = useState(user ? `${user.firstName} ${user.lastName}` : 'Unassigned');

  const loadTasks = async () => {
    if (!projectId) return;
    setIsLoading(true);
    try {
      const res = await projectWorkspaceApi.getTasks(projectId);
      if (res.data && res.data.length > 0) {
        setTasks(res.data);
      }
    } catch (err) {
      console.warn('Using default tasks fallback');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadTasks();
  }, [projectId]);

  const handleAdvanceStatus = async (taskId: string, currentStatus: string) => {
    const cycle = ['Backlog', 'In Progress', 'Code Review', 'Completed'];
    const currentIdx = cycle.indexOf(currentStatus);
    const nextStatus = cycle[(currentIdx + 1) % cycle.length];

    // Optimistic UI update
    setTasks(tasks.map(t => t.id === taskId ? { ...t, status: nextStatus } : t));

    try {
      await projectWorkspaceApi.updateTaskStatus(projectId, taskId, nextStatus);
    } catch (err) {
      console.warn('Status update recorded locally');
    }
  };

  const handleCreateTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!taskTitle.trim()) return;

    const newTask: ProjectTask = {
      id: `TSK-${Math.floor(100 + Math.random() * 900)}`,
      taskCode: `TSK-${Math.floor(100 + Math.random() * 900)}`,
      title: taskTitle.trim(),
      status: 'In Progress',
      priority: taskPriority,
      assigneeName: taskAssignee,
      dueDate: new Date(Date.now() + 7 * 86400000).toISOString().split('T')[0],
    };

    setTasks([newTask, ...tasks]);
    setTaskTitle('');
    setShowAddModal(false);

    try {
      await projectWorkspaceApi.createTask(projectId, newTask);
    } catch (err) {
      console.warn('Task created locally');
    }
  };

  const filteredTasks = tasks.filter(t => {
    if (filter === 'ALL') return true;
    return t.status.toUpperCase() === filter.toUpperCase();
  });

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Controls */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 mb-1">
              <CheckSquare className="w-4 h-4" />
              <span>Sprint Task Governance & Kanban</span>
            </div>
            <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
              Project Tasks & Deliverables ({tasks.length})
            </h3>
            <p className="text-xs text-slate-500">Track task statuses, sprint assignments, and milestone progress</p>
          </div>

          <button
            onClick={() => setShowAddModal(true)}
            className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 self-start sm:self-auto"
          >
            <Plus className="w-4 h-4" /> Add Project Task
          </button>
        </div>

        {/* Status Filters */}
        <div className="flex flex-wrap items-center gap-2">
          {['ALL', 'Backlog', 'In Progress', 'Code Review', 'Completed'].map((st) => (
            <button
              key={st}
              onClick={() => setFilter(st)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all ${
                filter === st
                  ? 'bg-slate-900 text-cyan-400 dark:bg-slate-800 dark:text-cyan-300 shadow-xs'
                  : 'bg-slate-100 dark:bg-slate-800/50 text-slate-600 dark:text-slate-400 hover:text-slate-900'
              }`}
            >
              {st} ({st === 'ALL' ? tasks.length : tasks.filter(t => t.status.toUpperCase() === st.toUpperCase()).length})
            </button>
          ))}
        </div>
      </div>

      {/* Task Cards List */}
      <div className="space-y-3">
        {filteredTasks.map((task) => (
          <div
            key={task.id}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-2xs flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:border-indigo-500/40 transition-all"
          >
            <div className="space-y-1 min-w-0">
              <div className="flex items-center gap-2">
                <span className="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-mono text-[10px] font-bold rounded-md">
                  {task.taskCode || task.id}
                </span>
                <span
                  className={`px-2 py-0.5 rounded-md font-bold text-[10px] uppercase ${
                    task.priority === 'Urgent'
                      ? 'bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300'
                      : task.priority === 'High'
                      ? 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300'
                      : 'bg-slate-100 text-slate-800 dark:bg-slate-800 dark:text-slate-300'
                  }`}
                >
                  {task.priority} Priority
                </span>
              </div>
              <h4 className="text-sm font-extrabold text-slate-900 dark:text-white truncate">
                {task.title}
              </h4>
              <p className="text-xs text-slate-500 font-medium flex items-center gap-2">
                <span className="flex items-center gap-1"><User className="w-3 h-3 text-slate-400" /> {task.assigneeName || 'Unassigned'}</span>
                <span>•</span>
                <span className="flex items-center gap-1 font-mono"><Clock className="w-3 h-3 text-slate-400" /> Due: {task.dueDate || '2026-08-30'}</span>
              </p>
            </div>

            <div className="flex items-center gap-3 shrink-0">
              <span
                className={`px-3 py-1 rounded-xl text-xs font-bold ${
                  task.status === 'Completed'
                    ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20'
                    : task.status === 'Code Review'
                    ? 'bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20'
                    : task.status === 'In Progress'
                    ? 'bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 border border-cyan-500/20'
                    : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400'
                }`}
              >
                {task.status}
              </span>

              <button
                onClick={() => handleAdvanceStatus(task.id, task.status)}
                className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-all flex items-center gap-1"
                title="Advance Kanban status"
              >
                <span>Advance</span>
                <ChevronRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Add Task Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <form onSubmit={handleCreateTask} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4 shadow-xl">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Create New Project Task</h3>

            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Task Title *</label>
              <input
                type="text"
                required
                value={taskTitle}
                onChange={(e) => setTaskTitle(e.target.value)}
                placeholder="e.g. Implement JWT refresh token rotation API"
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Priority *</label>
              <select
                value={taskPriority}
                onChange={(e) => setTaskPriority(e.target.value)}
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              >
                <option value="Urgent">Urgent</option>
                <option value="High">High</option>
                <option value="Medium">Medium</option>
                <option value="Low">Low</option>
              </select>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setShowAddModal(false)}
                className="px-4 py-2 bg-slate-100 dark:bg-slate-800 font-bold text-xs text-slate-700 dark:text-slate-300 rounded-xl"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs rounded-xl shadow-md"
              >
                Create Task
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
