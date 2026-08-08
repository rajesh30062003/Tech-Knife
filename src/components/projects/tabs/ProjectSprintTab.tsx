import React, { useState } from 'react';
import { Clock, Plus, CheckCircle2, Calendar, Target, Play, CheckCheck, Loader2 } from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';

interface ProjectSprintTabProps {
  project: EnterpriseProject;
}

interface SprintItem {
  id: string;
  name: string;
  goal: string;
  startDate: string;
  endDate: string;
  status: 'ACTIVE' | 'PLANNED' | 'COMPLETED';
  completedPoints: number;
  totalPoints: number;
  tasksCount: number;
}

const DEFAULT_SPRINTS: SprintItem[] = [
  {
    id: 'SPR-14',
    name: 'Sprint 14 - Core Engine & OAuth Hardening',
    goal: 'Complete Google OAuth 2.0 refresh flow, SRS PDF stream preview, and Task status lifecycle.',
    startDate: '2026-08-01',
    endDate: '2026-08-14',
    status: 'ACTIVE',
    completedPoints: 34,
    totalPoints: 42,
    tasksCount: 12,
  },
  {
    id: 'SPR-15',
    name: 'Sprint 15 - Performance & Analytics Optimization',
    goal: 'Optimize MongoDB indexing, Redis query caching, and dashboard chart rendering.',
    startDate: '2026-08-15',
    endDate: '2026-08-28',
    status: 'PLANNED',
    completedPoints: 0,
    totalPoints: 38,
    tasksCount: 10,
  },
  {
    id: 'SPR-13',
    name: 'Sprint 13 - Workspace UI & STOMP WebSocket Sync',
    goal: 'Implement real-time WebSocket chat attachments and 2-column SRS document manager.',
    startDate: '2026-07-18',
    endDate: '2026-07-31',
    status: 'COMPLETED',
    completedPoints: 45,
    totalPoints: 45,
    tasksCount: 15,
  },
];

export const ProjectSprintTab: React.FC<ProjectSprintTabProps> = ({ project }) => {
  const [sprints, setSprints] = useState<SprintItem[]>(DEFAULT_SPRINTS);
  const [showAddModal, setShowAddModal] = useState(false);

  // Modal form state
  const [name, setName] = useState('');
  const [goal, setGoal] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const activeSprint = sprints.find(s => s.status === 'ACTIVE') || sprints[0];
  const activeProgress = activeSprint ? Math.round((activeSprint.completedPoints / (activeSprint.totalPoints || 1)) * 100) : 0;

  const handleCreateSprint = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;

    const newSprint: SprintItem = {
      id: `SPR-${Math.floor(16 + Math.random() * 50)}`,
      name: name.trim(),
      goal: goal.trim() || 'Deliver planned sprint commitments.',
      startDate: startDate || new Date().toISOString().split('T')[0],
      endDate: endDate || new Date(Date.now() + 14 * 86400000).toISOString().split('T')[0],
      status: 'PLANNED',
      completedPoints: 0,
      totalPoints: 30,
      tasksCount: 8,
    };

    setSprints([newSprint, ...sprints]);
    setName('');
    setGoal('');
    setStartDate('');
    setEndDate('');
    setShowAddModal(false);
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header & Action Bar */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-cyan-600 dark:text-cyan-400 mb-1">
            <Clock className="w-4 h-4" />
            <span>Agile Sprint Execution Engine</span>
          </div>
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
            Sprint Management ({sprints.length} Sprints)
          </h3>
          <p className="text-xs text-slate-500">Track active iteration velocity, story points, and sprint commitments</p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-xl shadow-md transition-all flex items-center gap-2 self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" /> Create New Sprint
        </button>
      </div>

      {/* Active Sprint Spotlight Card */}
      {activeSprint && (
        <div className="bg-gradient-to-br from-slate-900 via-slate-900 to-cyan-950 border border-cyan-500/30 rounded-3xl p-6 shadow-md text-white space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-4">
            <div className="flex items-center gap-2">
              <span className="px-3 py-1 bg-cyan-500/20 text-cyan-300 font-mono text-xs font-black rounded-lg border border-cyan-500/30">
                CURRENT ACTIVE SPRINT
              </span>
              <span className="text-xs text-slate-400 font-mono">
                {activeSprint.startDate} → {activeSprint.endDate}
              </span>
            </div>

            <span className="px-3 py-1 bg-emerald-500/20 text-emerald-400 font-bold text-xs rounded-full border border-emerald-500/30 flex items-center gap-1.5 self-start sm:self-auto">
              <Play className="w-3 h-3 fill-current" /> ACTIVE ITERATION
            </span>
          </div>

          <div>
            <h4 className="text-lg font-black text-white">{activeSprint.name}</h4>
            <p className="text-xs text-slate-300 font-medium mt-1">{activeSprint.goal}</p>
          </div>

          <div className="space-y-2 pt-2">
            <div className="flex items-center justify-between text-xs font-bold">
              <span className="text-slate-300">Story Points Completion</span>
              <span className="font-mono text-cyan-400">{activeSprint.completedPoints} / {activeSprint.totalPoints} Points ({activeProgress}%)</span>
            </div>
            <div className="w-full h-2.5 bg-slate-800 rounded-full overflow-hidden p-0.5 border border-slate-700">
              <div className="h-full bg-gradient-to-r from-cyan-400 to-emerald-400 rounded-full transition-all duration-300" style={{ width: `${activeProgress}%` }} />
            </div>
          </div>
        </div>
      )}

      {/* Sprints List */}
      <div className="space-y-4">
        <h4 className="text-xs font-extrabold uppercase tracking-wider text-slate-500 px-1">All Sprints</h4>
        
        {sprints.map((sprint) => (
          <div
            key={sprint.id}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4"
          >
            <div className="space-y-1">
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 font-mono text-[10px] font-bold rounded-md">
                  {sprint.id}
                </span>
                <span
                  className={`px-2.5 py-0.5 rounded-full font-extrabold text-[10px] uppercase ${
                    sprint.status === 'ACTIVE'
                      ? 'bg-cyan-100 text-cyan-800 dark:bg-cyan-950 dark:text-cyan-300'
                      : sprint.status === 'COMPLETED'
                      ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300'
                      : 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300'
                  }`}
                >
                  {sprint.status}
                </span>
              </div>
              <h5 className="text-sm font-extrabold text-slate-900 dark:text-white">{sprint.name}</h5>
              <p className="text-xs text-slate-500 font-medium">{sprint.goal}</p>
            </div>

            <div className="flex items-center gap-4 shrink-0 text-xs font-medium">
              <div className="text-right">
                <span className="block text-slate-400 text-[10px]">Story Points</span>
                <span className="font-mono font-bold text-slate-900 dark:text-slate-100">{sprint.completedPoints}/{sprint.totalPoints} PTS</span>
              </div>
              <div className="text-right">
                <span className="block text-slate-400 text-[10px]">Duration</span>
                <span className="font-mono text-slate-600 dark:text-slate-300">{sprint.startDate}</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Create Sprint Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <form onSubmit={handleCreateSprint} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4 shadow-xl">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Create New Sprint</h3>

            <div>
              <label className="text-xs font-bold block mb-1">Sprint Name *</label>
              <input
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. Sprint 16 - Mobile UI Enhancements"
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold block mb-1">Sprint Goal</label>
              <textarea
                rows={2}
                value={goal}
                onChange={(e) => setGoal(e.target.value)}
                placeholder="Describe key deliverable goals..."
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-bold block mb-1">Start Date</label>
                <input
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
                />
              </div>
              <div>
                <label className="text-xs font-bold block mb-1">End Date</label>
                <input
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setShowAddModal(false)}
                className="px-4 py-2 bg-slate-100 dark:bg-slate-800 font-bold text-xs rounded-xl"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-2 bg-cyan-500 text-slate-950 font-black text-xs rounded-xl shadow-md"
              >
                Save Sprint
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
