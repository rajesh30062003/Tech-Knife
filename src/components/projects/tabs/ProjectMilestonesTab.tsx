import React, { useState } from 'react';
import { Calendar, Plus, CheckCircle2, Clock, AlertCircle, CheckSquare, Layers } from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';

interface ProjectMilestonesTabProps {
  project: EnterpriseProject;
}

interface MilestoneItem {
  id: string;
  title: string;
  dueDate: string;
  status: 'COMPLETED' | 'IN_PROGRESS' | 'UPCOMING';
  owner: string;
  deliverables: string[];
}

const DEFAULT_MILESTONES: MilestoneItem[] = [
  {
    id: 'MLS-101',
    title: 'Architecture Spec & SRS Approval',
    dueDate: '2026-06-30',
    status: 'COMPLETED',
    owner: 'Rajesh Pal (Tech Lead)',
    deliverables: ['System Architecture Document', 'Data Schemas', 'API Contracts'],
  },
  {
    id: 'MLS-102',
    title: 'Google OAuth 2.0 Integration & Drive Stream Engine',
    dueDate: '2026-08-10',
    status: 'IN_PROGRESS',
    owner: 'Backend Governance Team',
    deliverables: ['OAuth Refresh Token Rotation', '2-Column SRS PDF Preview', 'Chat Attachment Storage'],
  },
  {
    id: 'MLS-103',
    title: 'User Acceptance Testing & Production Release',
    dueDate: '2026-09-01',
    status: 'UPCOMING',
    owner: 'Product Engineering',
    deliverables: ['Performance Load Audits', 'UAT Signoff', 'Production Deployment'],
  },
];

export const ProjectMilestonesTab: React.FC<ProjectMilestonesTabProps> = ({ project }) => {
  const [milestones, setMilestones] = useState<MilestoneItem[]>(DEFAULT_MILESTONES);
  const [showAddModal, setShowAddModal] = useState(false);

  // Form State
  const [title, setTitle] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [owner, setOwner] = useState('');
  const [deliverablesText, setDeliverablesText] = useState('');

  const handleCreateMilestone = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    const delList = deliverablesText
      ? deliverablesText.split(',').map(s => s.trim()).filter(Boolean)
      : ['Deliverable Verification'];

    const newMilestone: MilestoneItem = {
      id: `MLS-${Math.floor(104 + Math.random() * 50)}`,
      title: title.trim(),
      dueDate: dueDate || new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0],
      status: 'UPCOMING',
      owner: owner.trim() || 'Engineering Team',
      deliverables: delList,
    };

    setMilestones([...milestones, newMilestone]);
    setTitle('');
    setDueDate('');
    setOwner('');
    setDeliverablesText('');
    setShowAddModal(false);
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 mb-1">
            <Calendar className="w-4 h-4" />
            <span>Timeline & Deliverable Milestones</span>
          </div>
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
            Milestones Schedule ({milestones.length})
          </h3>
          <p className="text-xs text-slate-500">Track key project target dates, stage gates, and core deliverables</p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" /> Add Milestone
        </button>
      </div>

      {/* Timeline List */}
      <div className="relative border-l-2 border-slate-200 dark:border-slate-800 ml-4 pl-6 space-y-6">
        {milestones.map((ms) => (
          <div key={ms.id} className="relative group">
            {/* Timeline Dot */}
            <div
              className={`absolute -left-[31px] top-1.5 w-4 h-4 rounded-full border-2 bg-white dark:bg-slate-900 ${
                ms.status === 'COMPLETED'
                  ? 'border-emerald-500 bg-emerald-500'
                  : ms.status === 'IN_PROGRESS'
                  ? 'border-cyan-400 bg-cyan-400 animate-pulse'
                  : 'border-slate-400 dark:border-slate-600'
              }`}
            />

            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-3">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
                <div className="flex items-center gap-2">
                  <span className="px-2.5 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 font-mono text-[10px] font-bold rounded-md">
                    {ms.id}
                  </span>
                  <h4 className="text-base font-extrabold text-slate-900 dark:text-white">{ms.title}</h4>
                </div>

                <span
                  className={`px-3 py-1 rounded-full text-xs font-bold self-start sm:self-auto ${
                    ms.status === 'COMPLETED'
                      ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300'
                      : ms.status === 'IN_PROGRESS'
                      ? 'bg-cyan-100 text-cyan-800 dark:bg-cyan-950 dark:text-cyan-300'
                      : 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300'
                  }`}
                >
                  {ms.status.replace('_', ' ')}
                </span>
              </div>

              <div className="flex flex-wrap items-center gap-4 text-xs font-medium text-slate-500">
                <span className="flex items-center gap-1.5"><Calendar className="w-3.5 h-3.5 text-indigo-400" /> Target Date: <strong className="text-slate-800 dark:text-slate-200 font-mono">{ms.dueDate}</strong></span>
                <span className="flex items-center gap-1.5"><Clock className="w-3.5 h-3.5 text-slate-400" /> Owner: <strong className="text-slate-800 dark:text-slate-200">{ms.owner}</strong></span>
              </div>

              {ms.deliverables && ms.deliverables.length > 0 && (
                <div className="pt-2">
                  <span className="text-[10px] font-extrabold uppercase text-slate-400 block mb-2">Key Deliverables</span>
                  <div className="flex flex-wrap gap-2">
                    {ms.deliverables.map((del, idx) => (
                      <span key={idx} className="px-3 py-1 bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-800 text-slate-700 dark:text-slate-300 text-xs font-medium rounded-xl flex items-center gap-1.5">
                        <CheckSquare className="w-3 h-3 text-cyan-500" /> {del}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>

      {/* Add Milestone Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <form onSubmit={handleCreateMilestone} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4 shadow-xl">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Add Project Milestone</h3>

            <div>
              <label className="text-xs font-bold block mb-1">Milestone Title *</label>
              <input
                type="text"
                required
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g. Beta Candidate Signoff"
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold block mb-1">Target Due Date</label>
              <input
                type="date"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold block mb-1">Owner / Lead</label>
              <input
                type="text"
                value={owner}
                onChange={(e) => setOwner(e.target.value)}
                placeholder="e.g. Lead Engineer"
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold block mb-1">Deliverables (comma-separated)</label>
              <input
                type="text"
                value={deliverablesText}
                onChange={(e) => setDeliverablesText(e.target.value)}
                placeholder="e.g. Load Tests, API Docs, Deployment Script"
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
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
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md"
              >
                Save Milestone
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
