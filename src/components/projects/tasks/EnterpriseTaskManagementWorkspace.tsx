import React, { useState, useEffect } from 'react';
import { 
  CheckSquare, Plus, Clock, AlertCircle, CheckCircle2, ChevronRight, ChevronDown, 
  Loader2, Filter, User, Layers, Calendar, GitBranch, Share2, MessageSquare, 
  Paperclip, Tag, ThumbsUp, Eye, Pin, Smile, Trash2, Edit3, Lock, ShieldCheck, 
  Sparkles, Award, UserCheck, CornerDownRight, Network, Activity, FileText,
  ArrowRight, Check, X
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';
import { projectWorkspaceApi } from '../../../api/projectWorkspaceApi';
import { useAuth } from '../../../context/AuthContext';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

interface EnterpriseTaskManagementWorkspaceProps {
  project: EnterpriseProject;
  initialView?: 'list' | 'kanban' | 'tree' | 'timeline' | 'calendar' | 'dependency_graph' | 'mindmap' | 'code_review';
}

export type TaskPriority = 'Urgent' | 'High' | 'Medium' | 'Low';
export type TaskStatus = 'Backlog' | 'To Do' | 'In Progress' | 'Code Review' | 'Testing' | 'Completed' | 'Blocked';

export interface AuditUserInfo {
  name: string;
  role: string;
  avatar: string;
  timestamp: string;
}

export interface TaskComment {
  id: string;
  authorName: string;
  authorRole: string;
  authorAvatar: string;
  content: string;
  createdAt: string;
}

export interface TaskActivity {
  id: string;
  user: string;
  action: string;
  timestamp: string;
}

export interface ExtendedProjectTask {
  id: string;
  taskCode?: string;
  taskNumber?: string;
  title: string;
  description?: string;
  status: TaskStatus | string;
  priority: TaskPriority | string;
  labels?: string[];
  sprint?: string;
  epic?: string;
  storyPoints?: number;
  estimatedHours?: number;
  assignedEmployeeId?: string;
  assignedEmployeeName?: string;
  assigneeName?: string;
  reviewerId?: string;
  reviewerName?: string;
  reporterName?: string;
  createdBy?: string;
  createdByInfo?: AuditUserInfo;
  completedByInfo?: AuditUserInfo;
  createdDate?: string;
  createdAt?: string;
  dueDate?: string;
  completedDate?: string;
  parentTaskId?: string;
  childTasks?: ExtendedProjectTask[];
  dependencies?: string[];
  attachments?: { name: string; url: string; size: number }[];
  comments?: TaskComment[];
  activityLog?: TaskActivity[];
  votesCount?: number;
  isWatching?: boolean;
  isPinned?: boolean;
}

const isObjectId = (str?: string) => {
  if (!str) return false;
  return /^[0-9a-fA-F]{24}$/.test(str);
};

const resolveCreatorDisplayName = (task: ExtendedProjectTask): { name: string; role: string; avatar: string } => {
  if (task.createdByInfo && task.createdByInfo.name && !isObjectId(task.createdByInfo.name)) {
    return {
      name: task.createdByInfo.name,
      role: task.createdByInfo.role || 'Engineer',
      avatar: task.createdByInfo.avatar || task.createdByInfo.name.charAt(0).toUpperCase()
    };
  }
  if (task.createdBy && !isObjectId(task.createdBy)) {
    return {
      name: task.createdBy,
      role: 'Engineer',
      avatar: task.createdBy.charAt(0).toUpperCase()
    };
  }
  return {
    name: 'Former User',
    role: 'Engineer',
    avatar: 'F'
  };
};

const normalizeStatus = (status?: string): string => {
  if (!status) return 'BACKLOG';
  const clean = status.trim().toUpperCase().replace(/-/g, '_').replace(/ /g, '_');
  if (clean === 'DONE' || clean === 'COMPLETED' || clean === 'FINISH' || clean === 'FINISHED') return 'COMPLETED';
  if (clean === 'IN_PROGRESS' || clean === 'PROGRESS') return 'IN_PROGRESS';
  if (clean === 'CODE_REVIEW' || clean === 'REVIEW' || clean === 'IN_REVIEW') return 'CODE_REVIEW';
  if (clean === 'TESTING' || clean === 'TEST') return 'TESTING';
  if (clean === 'BLOCKED') return 'BLOCKED';
  return 'BACKLOG';
};

export const EnterpriseTaskManagementWorkspace: React.FC<EnterpriseTaskManagementWorkspaceProps> = ({ 
  project, 
  initialView = 'list' 
}) => {
  const { user } = useAuth();
  const currentUserName = user ? `${user.firstName} ${user.lastName}` : 'System Admin';
  const currentUserRole = user?.role || 'Engineer';
  const projectId = project.id || project.projectId || project.projectCode;
  const isManagerOrLead = ['ROLE_MANAGER', 'ROLE_CEO', 'ROLE_SUPER_ADMIN', 'Project Manager', 'Lead'].some(
    r => currentUserRole.toUpperCase().includes(r.toUpperCase())
  );

  const [activeView, setActiveView] = useState<'list' | 'kanban' | 'tree' | 'timeline' | 'dependency_graph' | 'mindmap' | 'code_review'>(initialView as any || 'list');
  const [tasks, setTasks] = useState<ExtendedProjectTask[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [filterStatus, setFilterStatus] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [notificationMsg, setNotificationMsg] = useState<{ text: string; type: 'success' | 'error' } | null>(null);

  // Modals & Drawers
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [selectedTask, setSelectedTask] = useState<ExtendedProjectTask | null>(null);
  const [showSubTaskModal, setShowSubTaskModal] = useState<string | null>(null);

  // Form States
  const [newTitle, setNewTitle] = useState('');
  const [newDescription, setNewDescription] = useState('');
  const [newPriority, setNewPriority] = useState<TaskPriority>('High');
  const [newAssignee, setNewAssignee] = useState(currentUserName);
  const [newReviewer, setNewReviewer] = useState('');
  const [newEpic, setNewEpic] = useState('Core Deliverables');
  const [newSprint, setNewSprint] = useState('Sprint 1');
  const [newDueDate, setNewDueDate] = useState(new Date(Date.now() + 7 * 86400000).toISOString().split('T')[0]);

  const showToast = (text: string, type: 'success' | 'error' = 'success') => {
    setNotificationMsg({ text, type });
    setTimeout(() => setNotificationMsg(null), 4000);
  };

  // Load Tasks from Backend API
  const loadProjectTasks = async () => {
    setIsLoading(true);
    try {
      const res = await projectWorkspaceApi.getTasks(projectId);
      if (res.data) {
        setTasks(res.data.map((t: any) => {
          const normSt = normalizeStatus(t.status);
          const displayStatus = normSt === 'COMPLETED' ? 'Completed' : normSt === 'IN_PROGRESS' ? 'In Progress' : normSt === 'CODE_REVIEW' ? 'Code Review' : 'Backlog';
          return {
            ...t,
            taskCode: t.taskNumber || t.taskCode || t.id,
            assigneeName: t.assignedEmployeeName || t.assigneeName || 'Unassigned',
            status: displayStatus
          };
        }));
      }
    } catch (err: any) {
      console.error('Failed to fetch tasks:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadProjectTasks();
  }, [projectId]);

  // STOMP WebSocket Connection for Real-Time Task Sync
  useEffect(() => {
    const pCode = project.projectCode || projectId;
    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsHost = window.location.host;
    const wsUrl = `${wsProtocol}//${wsHost}/ws-chat`;

    let client: Client | null = null;
    try {
      const socket = new SockJS(wsUrl);
      client = new Client({
        webSocketFactory: () => socket,
        debug: () => {},
        reconnectDelay: 5000,
        onConnect: () => {
          client?.subscribe(`/topic/project.${pCode}`, (message) => {
            try {
              const payload = JSON.parse(message.body);
              if (payload.eventType === 'TaskCreated' || payload.eventType === 'TaskUpdated' || payload.event === 'TaskUpdated') {
                loadProjectTasks();
              }
            } catch (e) {}
          });
        }
      });
      client.activate();
    } catch (e) {}

    return () => {
      if (client) client.deactivate();
    };
  }, [project.projectCode, projectId]);

  // Create Task Submission
  const handleCreateTaskSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim()) return;

    const nowStr = new Date().toLocaleDateString();
    const newTaskPayload = {
      title: newTitle.trim(),
      description: newDescription.trim(),
      status: 'In Progress',
      priority: newPriority,
      assigneeName: newAssignee,
      assignedEmployeeName: newAssignee,
      reviewerName: newReviewer,
      dueDate: newDueDate,
      epic: newEpic,
      sprint: newSprint,
      storyPoints: 3,
      estimatedHours: 16,
      createdByInfo: {
        name: currentUserName,
        role: currentUserRole,
        avatar: currentUserName.charAt(0).toUpperCase(),
        timestamp: nowStr
      }
    };

    try {
      const res = await projectWorkspaceApi.createTask(projectId, newTaskPayload);
      if (res.data) {
        showToast('Task successfully created in database!', 'success');
        await loadProjectTasks();
      }
    } catch (err: any) {
      console.error('Task creation failed:', err);
      showToast('Failed to create task in backend.', 'error');
    }

    setNewTitle('');
    setNewDescription('');
    setShowCreateModal(false);
  };

  // Add Branch Subtask
  const handleAddChildTask = async (parentTaskId: string) => {
    if (!newTitle.trim()) return;
    const childPayload = {
      title: newTitle.trim(),
      status: 'In Progress',
      priority: 'Medium',
      parentTaskId: parentTaskId
    };

    try {
      await projectWorkspaceApi.createTask(projectId, childPayload);
      showToast('Child sub-task branched successfully!', 'success');
      await loadProjectTasks();
    } catch (err: any) {
      showToast('Failed to branch sub-task.', 'error');
    }

    setNewTitle('');
    setShowSubTaskModal(null);
  };

  // Complete Task & Update Backend Lifecycle
  const handleUpdateStatus = async (task: ExtendedProjectTask, newStatus: string) => {
    const previousTasks = [...tasks];
    const updatedTasks = tasks.map(t => {
      if (t.id === task.id) {
        return {
          ...t,
          status: newStatus,
          completedDate: newStatus === 'Completed' ? new Date().toISOString().split('T')[0] : t.completedDate,
          completedByInfo: newStatus === 'Completed' ? {
            name: currentUserName,
            role: currentUserRole,
            avatar: currentUserName.charAt(0).toUpperCase(),
            timestamp: new Date().toLocaleDateString()
          } : t.completedByInfo
        };
      }
      return t;
    });

    setTasks(updatedTasks);

    try {
      const res = await projectWorkspaceApi.updateTaskStatus(projectId, task.id, newStatus);
      if (res.data) {
        showToast(`Task status updated to ${newStatus}!`, 'success');
      }
    } catch (err: any) {
      console.error('Status update failed:', err);
      setTasks(previousTasks);
      showToast('Failed to update task status in backend.', 'error');
    }
  };

  // Task Deletion
  const handleDeleteTask = async (taskId: string) => {
    if (!window.confirm('Are you sure you want to delete this task?')) return;
    const previous = [...tasks];
    setTasks(tasks.filter(t => t.id !== taskId));
    try {
      await projectWorkspaceApi.deleteTask(projectId, taskId);
      showToast('Task deleted successfully!', 'success');
    } catch (err) {
      setTasks(previous);
      showToast('Failed to delete task.', 'error');
    }
  };

  // Filter Tasks Engine
  const filteredTasks = tasks.filter(t => {
    const normSt = normalizeStatus(t.status);
    let matchesStatus = true;
    if (filterStatus === 'In Progress') matchesStatus = normSt === 'IN_PROGRESS';
    else if (filterStatus === 'Completed') matchesStatus = normSt === 'COMPLETED';
    else if (filterStatus === 'Code Review') matchesStatus = normSt === 'CODE_REVIEW' || Boolean(t.reviewerName);
    else if (filterStatus !== 'ALL') matchesStatus = normSt === normalizeStatus(filterStatus);

    const matchesSearch = t.title.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          (t.taskCode && t.taskCode.toLowerCase().includes(searchQuery.toLowerCase())) ||
                          (t.assigneeName && t.assigneeName.toLowerCase().includes(searchQuery.toLowerCase()));
    return matchesStatus && matchesSearch;
  });

  // Calculate project progress percentage
  const completedCount = tasks.filter(t => normalizeStatus(t.status) === 'COMPLETED').length;
  const progressPercent = tasks.length > 0 ? Math.round((completedCount / tasks.length) * 100) : 0;

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Toast Notification */}
      {notificationMsg && (
        <div className={`p-4 rounded-2xl border font-bold text-xs flex items-center justify-between shadow-lg transition-all ${
          notificationMsg.type === 'success' 
            ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-400' 
            : 'bg-rose-500/10 border-rose-500/30 text-rose-400'
        }`}>
          <div className="flex items-center gap-2">
            {notificationMsg.type === 'success' ? <CheckCircle2 className="w-4 h-4" /> : <AlertCircle className="w-4 h-4" />}
            <span>{notificationMsg.text}</span>
          </div>
          <button onClick={() => setNotificationMsg(null)} className="p-1 hover:bg-slate-800 rounded-lg text-slate-400">
            <X className="w-3.5 h-3.5" />
          </button>
        </div>
      )}

      {/* Top Header & Progress */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-extrabold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 mb-1">
              <CheckSquare className="w-4 h-4" />
              <span>MongoDB Atlas & STOMP Synced Task Management Suite</span>
            </div>
            <h3 className="text-xl font-black text-slate-900 dark:text-white flex items-center gap-3">
              Project Deliverables & Tasks ({tasks.length})
              <span className="px-3 py-1 rounded-full text-xs font-extrabold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                {progressPercent}% Complete
              </span>
            </h3>
            <p className="text-xs text-slate-500 font-medium">Real-time persistent task tracking across multiple interactive workspace views</p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <div className="relative">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search tasks or assignee..."
                className="text-xs p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 font-medium text-slate-900 dark:text-slate-100 w-48 sm:w-64 focus:outline-none focus:border-indigo-500"
              />
            </div>

            <button
              onClick={() => setShowCreateModal(true)}
              className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2"
            >
              <Plus className="w-4 h-4" /> Create Task
            </button>
          </div>
        </div>

        {/* View Mode Navigation Tabs & Quick Filters */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 overflow-x-auto pb-1 scrollbar-none">
          <div className="flex items-center gap-1.5 bg-slate-100 dark:bg-slate-800/60 p-1 rounded-2xl text-xs font-bold shrink-0">
            <button
              onClick={() => setActiveView('list')}
              className={`px-3 py-1.5 rounded-xl transition-all flex items-center gap-1.5 ${
                activeView === 'list' ? 'bg-slate-900 text-cyan-400 dark:bg-slate-900 dark:text-cyan-300 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-white'
              }`}
            >
              <CheckSquare className="w-3.5 h-3.5" /> List View
            </button>
            <button
              onClick={() => setActiveView('kanban')}
              className={`px-3 py-1.5 rounded-xl transition-all flex items-center gap-1.5 ${
                activeView === 'kanban' ? 'bg-slate-900 text-cyan-400 dark:bg-slate-900 dark:text-cyan-300 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-white'
              }`}
            >
              <Layers className="w-3.5 h-3.5" /> Kanban Board
            </button>
            <button
              onClick={() => setActiveView('tree')}
              className={`px-3 py-1.5 rounded-xl transition-all flex items-center gap-1.5 ${
                activeView === 'tree' ? 'bg-slate-900 text-cyan-400 dark:bg-slate-900 dark:text-cyan-300 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-white'
              }`}
            >
              <GitBranch className="w-3.5 h-3.5" /> Branch Tree
            </button>
            <button
              onClick={() => setActiveView('timeline')}
              className={`px-3 py-1.5 rounded-xl transition-all flex items-center gap-1.5 ${
                activeView === 'timeline' ? 'bg-slate-900 text-cyan-400 dark:bg-slate-900 dark:text-cyan-300 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-white'
              }`}
            >
              <Clock className="w-3.5 h-3.5" /> Timeline & Gantt
            </button>
            <button
              onClick={() => setActiveView('dependency_graph')}
              className={`px-3 py-1.5 rounded-xl transition-all flex items-center gap-1.5 ${
                activeView === 'dependency_graph' ? 'bg-slate-900 text-cyan-400 dark:bg-slate-900 dark:text-cyan-300 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-white'
              }`}
            >
              <Network className="w-3.5 h-3.5" /> Dependency Graph
            </button>
            <button
              onClick={() => setActiveView('mindmap')}
              className={`px-3 py-1.5 rounded-xl transition-all flex items-center gap-1.5 ${
                activeView === 'mindmap' ? 'bg-slate-900 text-cyan-400 dark:bg-slate-900 dark:text-cyan-300 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-white'
              }`}
            >
              <Share2 className="w-3.5 h-3.5" /> Mind Map
            </button>
            <button
              onClick={() => setActiveView('code_review')}
              className={`px-3 py-1.5 rounded-xl transition-all flex items-center gap-1.5 ${
                activeView === 'code_review' ? 'bg-slate-900 text-cyan-400 dark:bg-slate-900 dark:text-cyan-300 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-white'
              }`}
            >
              <FileText className="w-3.5 h-3.5" /> Code Review
            </button>
          </div>

          {/* Quick Filter Buttons */}
          <div className="flex items-center gap-1 text-xs shrink-0">
            {['ALL', 'In Progress', 'Completed', 'Code Review'].map(st => (
              <button
                key={st}
                onClick={() => setFilterStatus(st)}
                className={`px-2.5 py-1 rounded-xl font-bold transition-all ${
                  filterStatus === st ? 'bg-indigo-600 text-white' : 'bg-slate-100 dark:bg-slate-800 text-slate-500 hover:text-slate-900'
                }`}
              >
                {st}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* VIEW 1: DETAILED LIST VIEW */}
      {activeView === 'list' && (
        <div className="space-y-3">
          {isLoading ? (
            <div className="p-12 text-center text-xs text-slate-400 flex items-center justify-center gap-2">
              <Loader2 className="w-4 h-4 animate-spin text-indigo-500" /> Loading project tasks...
            </div>
          ) : filteredTasks.length === 0 ? (
            <div className="p-12 text-center space-y-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl">
              <CheckSquare className="w-8 h-8 text-slate-300 dark:text-slate-700 mx-auto" />
              <p className="text-sm font-bold text-slate-700 dark:text-slate-300">No tasks match filter criteria</p>
            </div>
          ) : (
            filteredTasks.map(task => {
              const creator = resolveCreatorDisplayName(task);
              const isCompleted = normalizeStatus(task.status) === 'COMPLETED';

              return (
                <div
                  key={task.id}
                  className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-2xs hover:border-indigo-500/40 transition-all space-y-3"
                >
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                    <div className="space-y-1 min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <span className="px-2.5 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-mono text-[10px] font-bold rounded-lg">
                          {task.taskCode || task.id}
                        </span>
                        <span className={`px-2.5 py-0.5 rounded-lg text-[10px] font-extrabold uppercase ${
                          task.priority === 'Urgent' ? 'bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300' :
                          task.priority === 'High' ? 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300' : 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300'
                        }`}>
                          {task.priority} Priority
                        </span>
                        {task.sprint && <span className="px-2 py-0.5 bg-indigo-500/10 text-indigo-400 font-bold text-[10px] rounded-lg">{task.sprint}</span>}
                      </div>

                      <h4 className="text-base font-extrabold text-slate-900 dark:text-white truncate">
                        {task.title}
                      </h4>
                    </div>

                    <div className="flex items-center gap-3 shrink-0">
                      <span className={`px-3 py-1 rounded-xl text-xs font-extrabold ${
                        isCompleted ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20' :
                        normalizeStatus(task.status) === 'CODE_REVIEW' ? 'bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20' : 'bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 border border-cyan-500/20'
                      }`}>
                        {task.status}
                      </span>

                      {!isCompleted ? (
                        <button
                          onClick={() => handleUpdateStatus(task, 'Completed')}
                          className="px-3 py-1.5 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-black text-xs rounded-xl shadow-xs transition-all flex items-center gap-1 cursor-pointer"
                        >
                          <CheckCircle2 className="w-3.5 h-3.5" /> Complete
                        </button>
                      ) : (
                        <span className="px-3 py-1.5 bg-slate-100 dark:bg-slate-800 text-emerald-500 font-bold text-xs rounded-xl flex items-center gap-1">
                          <Check className="w-3.5 h-3.5" /> Completed
                        </span>
                      )}

                      {isManagerOrLead && (
                        <button
                          onClick={() => handleDeleteTask(task.id)}
                          className="p-1.5 bg-rose-500/10 text-rose-500 hover:bg-rose-500/20 rounded-xl"
                          title="Delete Task"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      )}
                    </div>
                  </div>

                  {/* Creator Info Header */}
                  <div className="pt-3 border-t border-slate-100 dark:border-slate-800/80 flex flex-wrap items-center justify-between gap-3 text-xs">
                    <div className="flex items-center gap-2">
                      <span className="text-[10px] uppercase font-bold text-slate-400">Created By:</span>
                      <div className="flex items-center gap-1.5">
                        <div className="w-5 h-5 rounded-full bg-indigo-600 text-white text-[10px] font-black flex items-center justify-center">
                          {creator.avatar}
                        </div>
                        <span className="font-bold text-slate-900 dark:text-slate-200">{creator.name}</span>
                        <span className="text-[10px] text-slate-400 font-mono">({creator.role})</span>
                      </div>
                    </div>

                    <div className="flex items-center gap-4 text-[11px] text-slate-400 font-medium">
                      <span>Assignee: <strong className="text-slate-700 dark:text-slate-300">{task.assigneeName || 'Unassigned'}</strong></span>
                      {task.dueDate && <span>Due: <strong className="text-indigo-400 font-mono">{task.dueDate}</strong></span>}
                    </div>
                  </div>
                </div>
              );
            })
          )}
        </div>
      )}

      {/* VIEW 2: KANBAN BOARD */}
      {activeView === 'kanban' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {(['Backlog', 'In Progress', 'Code Review', 'Completed'] as TaskStatus[]).map(st => {
            const laneTasks = tasks.filter(t => {
              const norm = normalizeStatus(t.status);
              if (st === 'Backlog') return norm === 'BACKLOG' || norm === 'TODO';
              if (st === 'In Progress') return norm === 'IN_PROGRESS';
              if (st === 'Code Review') return norm === 'CODE_REVIEW';
              if (st === 'Completed') return norm === 'COMPLETED';
              return false;
            });

            return (
              <div key={st} className="bg-slate-50 dark:bg-slate-900/60 border border-slate-200 dark:border-slate-800 rounded-3xl p-4 space-y-3">
                <div className="flex items-center justify-between pb-2 border-b border-slate-200 dark:border-slate-800">
                  <h4 className="text-xs font-black uppercase tracking-wider text-slate-900 dark:text-white flex items-center gap-2">
                    <span className={`w-2.5 h-2.5 rounded-full ${
                      st === 'Completed' ? 'bg-emerald-400' : st === 'Code Review' ? 'bg-indigo-400' : st === 'In Progress' ? 'bg-cyan-400' : 'bg-slate-400'
                    }`} />
                    {st}
                  </h4>
                  <span className="px-2 py-0.5 bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-mono font-bold text-[10px] rounded-lg">
                    {laneTasks.length}
                  </span>
                </div>

                <div className="space-y-3 max-h-[600px] overflow-y-auto pr-1">
                  {laneTasks.length === 0 ? (
                    <div className="py-8 text-center text-xs text-slate-400 font-medium">No tasks in {st}</div>
                  ) : (
                    laneTasks.map(t => {
                      const creator = resolveCreatorDisplayName(t);
                      return (
                        <div
                          key={t.id}
                          className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-2xs space-y-2.5 hover:border-cyan-500/50 transition-all"
                        >
                          <div className="flex items-center justify-between text-[10px]">
                            <span className="font-mono font-bold text-slate-400">{t.taskCode || t.id}</span>
                            <span className="font-bold text-indigo-400">{t.priority}</span>
                          </div>

                          <h5 className="text-xs font-bold text-slate-900 dark:text-white leading-snug">{t.title}</h5>

                          <div className="text-[10px] text-slate-500 flex items-center justify-between pt-2 border-t border-slate-100 dark:border-slate-800">
                            <span>By: {creator.name}</span>
                            <span>{t.assigneeName || 'Unassigned'}</span>
                          </div>

                          {/* Quick Status Movement */}
                          {st !== 'Completed' && (
                            <div className="pt-1 flex items-center justify-end gap-1">
                              <button
                                onClick={() => handleUpdateStatus(t, 'Completed')}
                                className="px-2 py-1 bg-emerald-500/10 hover:bg-emerald-500 text-emerald-500 hover:text-slate-950 font-bold text-[10px] rounded-lg transition-all flex items-center gap-1"
                              >
                                <Check className="w-3 h-3" /> Mark Done
                              </button>
                            </div>
                          )}
                        </div>
                      );
                    })
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* VIEW 3: HIERARCHICAL BRANCH TREE VIEW */}
      {activeView === 'tree' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <h4 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <GitBranch className="w-4 h-4 text-cyan-500" /> Parent Task Hierarchy & Branch Sub-Tasks
            </h4>
          </div>

          <div className="space-y-4">
            {tasks.filter(t => !t.parentTaskId).map(parent => {
              const childTasks = tasks.filter(c => c.parentTaskId === parent.id || (parent.childTasks && parent.childTasks.some(sub => sub.id === c.id)));
              const creator = resolveCreatorDisplayName(parent);

              return (
                <div key={parent.id} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 space-y-3">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                    <div className="flex items-center gap-3">
                      <span className="px-2.5 py-1 bg-cyan-500/10 text-cyan-400 font-mono font-bold text-xs rounded-xl border border-cyan-500/20">{parent.taskCode || parent.id}</span>
                      <div>
                        <h5 className="text-sm font-bold text-slate-900 dark:text-white">{parent.title}</h5>
                        <p className="text-[10px] text-slate-400">Created By: {creator.name} ({creator.role})</p>
                      </div>
                    </div>

                    <button
                      onClick={() => setShowSubTaskModal(parent.id)}
                      className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs rounded-xl flex items-center gap-1 self-start sm:self-auto"
                    >
                      <Plus className="w-3.5 h-3.5" /> Branch Sub-Task
                    </button>
                  </div>

                  {/* Branch Child Tasks */}
                  {childTasks.length > 0 && (
                    <div className="pl-6 border-l-2 border-cyan-500/30 space-y-2 pt-2">
                      {childTasks.map(child => (
                        <div key={child.id} className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex items-center justify-between text-xs">
                          <div className="flex items-center gap-2">
                            <CornerDownRight className="w-4 h-4 text-cyan-400" />
                            <span className="font-mono font-bold text-slate-400">{child.taskCode || child.id}</span>
                            <span className="font-bold text-slate-800 dark:text-slate-200">{child.title}</span>
                          </div>
                          <span className="px-2.5 py-0.5 rounded-lg bg-emerald-500/10 text-emerald-400 font-bold text-[10px]">{child.status}</span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* VIEW 4: TIMELINE & GANTT CHART */}
      {activeView === 'timeline' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
            <Clock className="w-4 h-4 text-cyan-500" /> Interactive Schedule Timeline & Gantt Chart
          </h4>

          <div className="overflow-x-auto">
            <div className="min-w-[650px] space-y-4">
              {tasks.map((t, idx) => {
                const isDone = normalizeStatus(t.status) === 'COMPLETED';
                const createdDateStr = t.createdDate || t.createdAt || '2026-08-01';
                const dueDateStr = t.dueDate || '2026-08-30';

                return (
                  <div key={t.id} className="space-y-1.5 p-3 rounded-2xl bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800">
                    <div className="flex items-center justify-between text-xs font-bold">
                      <span className="truncate max-w-xs">{t.taskCode || t.id}: {t.title}</span>
                      <div className="flex items-center gap-3 text-[11px] font-mono text-slate-400">
                        <span>Start: {createdDateStr}</span>
                        <span>Due: {dueDateStr}</span>
                      </div>
                    </div>

                    <div className="w-full h-3 bg-slate-200 dark:bg-slate-800 rounded-full overflow-hidden relative">
                      <div
                        style={{ width: isDone ? '100%' : `${Math.min(100, (idx + 1) * 25)}%` }}
                        className={`h-full rounded-full transition-all ${
                          isDone ? 'bg-emerald-500' : 'bg-gradient-to-r from-cyan-500 to-indigo-600'
                        }`}
                      />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/* VIEW 5: DEPENDENCY GRAPH VIEW */}
      {activeView === 'dependency_graph' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
            <Network className="w-4 h-4 text-cyan-500" /> Persistent Task Dependency Graph
          </h4>

          <div className="p-6 rounded-2xl bg-slate-950 border border-slate-800 min-h-[350px] flex items-center justify-center gap-6 flex-wrap relative">
            {tasks.map(t => {
              const creator = resolveCreatorDisplayName(t);
              return (
                <div key={t.id} className="p-4 rounded-2xl bg-slate-900 border-2 border-cyan-500/40 w-64 space-y-2 shadow-lg">
                  <div className="flex items-center justify-between text-[10px]">
                    <span className="font-mono font-bold text-cyan-400">{t.taskCode || t.id}</span>
                    <span className="px-2 py-0.5 bg-emerald-500/10 text-emerald-400 font-bold rounded-md">{t.status}</span>
                  </div>
                  <h5 className="text-xs font-bold text-white truncate">{t.title}</h5>
                  <div className="text-[10px] text-slate-400 border-t border-slate-800 pt-2 flex items-center justify-between">
                    <span>Created By: {creator.name}</span>
                    <span>{t.priority}</span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* VIEW 6: MIND MAP */}
      {activeView === 'mindmap' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4 text-center">
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center justify-center gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
            <Share2 className="w-4 h-4 text-cyan-500" /> Radial Mind Map Task Breakdown
          </h4>
          <div className="p-8 rounded-2xl bg-slate-950 border border-slate-800 flex flex-col items-center gap-6">
            <div className="p-4 rounded-3xl bg-indigo-600 text-white font-black text-sm shadow-xl">
              {project.projectName || 'Project Root'} Deliverables ({tasks.length})
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 w-full">
              {tasks.slice(0, 6).map(t => (
                <div key={t.id} className="p-3.5 rounded-2xl bg-slate-900 border border-slate-800 text-xs font-bold text-cyan-300">
                  {t.taskCode || t.id}: {t.title}
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* VIEW 7: CODE REVIEW */}
      {activeView === 'code_review' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
            <FileText className="w-4 h-4 text-indigo-500" /> Code Review & Pull Request Management
          </h4>

          {tasks.filter(t => normalizeStatus(t.status) === 'CODE_REVIEW' || Boolean(t.reviewerName)).length === 0 ? (
            <div className="py-12 text-center space-y-2">
              <FileText className="w-8 h-8 text-slate-300 dark:text-slate-700 mx-auto" />
              <p className="text-xs font-bold text-slate-500">No code review items currently pending</p>
              <p className="text-[11px] text-slate-400">Assign a reviewer or change task status to 'Code Review' to list items here.</p>
            </div>
          ) : (
            <div className="space-y-3">
              {tasks.filter(t => normalizeStatus(t.status) === 'CODE_REVIEW' || Boolean(t.reviewerName)).map(task => (
                <div key={task.id} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
                  <div>
                    <span className="font-mono font-bold text-indigo-400">{task.taskCode || task.id}</span>
                    <h5 className="font-extrabold text-slate-900 dark:text-white">{task.title}</h5>
                    <p className="text-[10px] text-slate-400 mt-0.5">Reviewer: {task.reviewerName || 'Assigned Reviewer'}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <button onClick={() => handleUpdateStatus(task, 'Completed')} className="px-3 py-1.5 bg-emerald-500 text-slate-950 font-bold text-xs rounded-xl flex items-center gap-1">
                      <Check className="w-3.5 h-3.5" /> Approve & Complete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Create Task Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4">
          <form onSubmit={handleCreateTaskSubmit} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-lg w-full p-6 space-y-4 shadow-2xl">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Create Enterprise Task (Saved in MongoDB Atlas)</h3>

            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Task Title *</label>
              <input
                type="text"
                required
                value={newTitle}
                onChange={(e) => setNewTitle(e.target.value)}
                placeholder="e.g. Implement OAuth 2.0 Token Audit Pipeline"
                className="w-full text-xs font-medium p-3 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Description</label>
              <textarea
                value={newDescription}
                onChange={(e) => setNewDescription(e.target.value)}
                rows={3}
                placeholder="Task requirements and acceptance criteria..."
                className="w-full text-xs font-medium p-3 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Priority</label>
                <select
                  value={newPriority}
                  onChange={(e) => setNewPriority(e.target.value as TaskPriority)}
                  className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
                >
                  <option value="Urgent">Urgent</option>
                  <option value="High">High</option>
                  <option value="Medium">Medium</option>
                  <option value="Low">Low</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Assignee</label>
                <input
                  type="text"
                  value={newAssignee}
                  onChange={(e) => setNewAssignee(e.target.value)}
                  className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-100 dark:border-slate-800">
              <button
                type="button"
                onClick={() => setShowCreateModal(false)}
                className="px-4 py-2.5 bg-slate-100 dark:bg-slate-800 font-bold text-xs text-slate-700 dark:text-slate-300 rounded-xl"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md"
              >
                Create Task
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Sub-Task Modal */}
      {showSubTaskModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4 shadow-2xl">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Add Branch Sub-Task</h3>
            <input
              type="text"
              value={newTitle}
              onChange={(e) => setNewTitle(e.target.value)}
              placeholder="Child task title..."
              className="w-full text-xs font-medium p-3 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
            />
            <div className="flex justify-end gap-2">
              <button onClick={() => setShowSubTaskModal(null)} className="px-4 py-2 bg-slate-100 dark:bg-slate-800 font-bold text-xs text-slate-300 rounded-xl">Cancel</button>
              <button onClick={() => handleAddChildTask(showSubTaskModal)} className="px-4 py-2 bg-indigo-600 text-white font-extrabold text-xs rounded-xl">Add Sub-Task</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
