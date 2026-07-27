import React, { useState } from 'react';
import { 
  Users, CheckCircle2, Clock, CalendarDays, AlertCircle, ArrowUpRight, 
  Check, X, FolderKanban, Plus, DollarSign, UserCheck, Shield 
} from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useAuth } from '../../context/AuthContext';
import { canAssignProjects, canApprovePayroll, canManageLeave, canManageAttendance } from '../../utils/rbac';

export const ManagerDashboard: React.FC = () => {
  const { user } = useAuth();
  
  const [leaveRequests, setLeaveRequests] = useState([
    { id: 'lv-01', name: 'Elena Rostova', type: 'Annual Leave', duration: '3 Days (Oct 12 - Oct 14)', reason: 'Attending React Advanced Summit', status: 'Pending' },
    { id: 'lv-02', name: 'Lucas Chen', type: 'Casual Leave', duration: '1 Day (Oct 18)', reason: 'Personal family commitment', status: 'Pending' },
    { id: 'lv-03', name: 'David Miller', type: 'Sick Leave', duration: '2 Days (Oct 20 - Oct 21)', reason: 'Medical appointment', status: 'Pending' },
  ]);

  const [projectAssignments, setProjectAssignments] = useState([
    { id: 'pa-01', projectName: 'Apex Cloud Modernization', lead: 'Sarah Connor', teamSize: 5, status: 'Active' },
    { id: 'pa-02', projectName: 'Tech Knife Spring Boot Gateway', lead: 'Alexander Vance', teamSize: 3, status: 'Active' },
    { id: 'pa-03', projectName: 'MongoDB Atlas Data Pipeline', lead: 'Marcus Brody', teamSize: 4, status: 'In Review' },
  ]);

  const [showAssignModal, setShowAssignModal] = useState(false);
  const [newProject, setNewProject] = useState({ name: '', lead: 'Elena Rostova', teamSize: 2 });

  const handleLeaveAction = (id: string, action: 'Approved' | 'Rejected') => {
    setLeaveRequests(leaveRequests.map(item => item.id === id ? { ...item, status: action } : item));
  };

  const handleAssignProject = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newProject.name) return;
    setProjectAssignments([
      ...projectAssignments,
      { id: `pa-${Date.now().toString().slice(-2)}`, projectName: newProject.name, lead: newProject.lead, teamSize: Number(newProject.teamSize), status: 'Active' }
    ]);
    setShowAssignModal(false);
    setNewProject({ name: '', lead: 'Elena Rostova', teamSize: 2 });
  };

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-amber-600 dark:text-amber-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Users className="w-4 h-4" />
            <span>Management & Executive Team Desk</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Team Allocations, Projects & Approvals</h1>
          <p className="text-xs text-slate-500">Assign projects, review team leaves, approve payroll items, and track member utilization</p>
        </div>

        {canAssignProjects(user) && (
          <button
            onClick={() => setShowAssignModal(true)}
            className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md"
          >
            <Plus className="w-3.5 h-3.5" /> Assign Project to Member
          </button>
        )}
      </div>

      {/* Grid: Pending Approvals & Project Assignments */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        
        {/* Leave Approvals */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <div>
              <h3 className="font-bold text-base text-slate-900 dark:text-white">Pending Leave Approvals</h3>
              <p className="text-xs text-slate-500">Requires Manager/Lead Authorization</p>
            </div>
            <span className="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-amber-100 dark:bg-amber-950 text-amber-700 dark:text-amber-300">
              {leaveRequests.filter(l => l.status === 'Pending').length} Pending
            </span>
          </div>

          <div className="space-y-3">
            {leaveRequests.map((item) => (
              <div key={item.id} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 space-y-2">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{item.name}</span>
                  <div className="flex items-center gap-2">
                    <span className="text-[11px] font-semibold text-indigo-600 dark:text-indigo-400">{item.type}</span>
                    <StatusBadge status={item.status} />
                  </div>
                </div>
                <p className="text-xs text-slate-600 dark:text-slate-300 font-medium">{item.duration}</p>
                <p className="text-[11px] text-slate-400 italic">"{item.reason}"</p>
                
                {item.status === 'Pending' && canManageLeave(user) && (
                  <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-200/40 dark:border-slate-700/40">
                    <button
                      onClick={() => handleLeaveAction(item.id, 'Rejected')}
                      className="px-3 py-1 bg-rose-50 hover:bg-rose-100 text-rose-600 text-xs font-semibold rounded-lg transition-colors flex items-center gap-1"
                    >
                      <X className="w-3.5 h-3.5" /> Reject
                    </button>
                    <button
                      onClick={() => handleLeaveAction(item.id, 'Approved')}
                      className="px-3 py-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold rounded-lg transition-colors flex items-center gap-1 shadow-xs"
                    >
                      <Check className="w-3.5 h-3.5" /> Approve
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Project Assignments */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
          <div className="border-b border-slate-100 dark:border-slate-800 pb-3 flex items-center justify-between">
            <div>
              <h3 className="font-bold text-base text-slate-900 dark:text-white">Active Project Allocations</h3>
              <p className="text-xs text-slate-500">Resource ownership & sprint assignment tracking</p>
            </div>
            <FolderKanban className="w-4 h-4 text-indigo-500" />
          </div>

          <div className="space-y-3">
            {projectAssignments.map((p) => (
              <div key={p.id} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 space-y-1.5">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{p.projectName}</span>
                  <StatusBadge status={p.status} />
                </div>
                <div className="flex items-center justify-between text-[11px] text-slate-500">
                  <span>Lead: <strong className="text-slate-700 dark:text-slate-300">{p.lead}</strong></span>
                  <span>{p.teamSize} Allocated Members</span>
                </div>
              </div>
            ))}
          </div>
        </div>

      </div>

      {/* Modal: Assign Project */}
      {showAssignModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-bold text-base text-slate-900 dark:text-white">Assign Project / Sprint Deliverable</h3>
              <button onClick={() => setShowAssignModal(false)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>

            <form onSubmit={handleAssignProject} className="space-y-3">
              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Project Name</label>
                <input
                  type="text"
                  required
                  value={newProject.name}
                  onChange={(e) => setNewProject({ ...newProject, name: e.target.value })}
                  placeholder="e.g. AI Search Grounding Integration"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Assigned Lead / Owner</label>
                <select
                  value={newProject.lead}
                  onChange={(e) => setNewProject({ ...newProject, lead: e.target.value })}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                >
                  <option value="Elena Rostova">Elena Rostova (Senior Dev)</option>
                  <option value="Lucas Chen">Lucas Chen (Intern)</option>
                  <option value="Marcus Brody">Marcus Brody (Manager)</option>
                  <option value="Sarah Connor">Sarah Connor (CTO)</option>
                </select>
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Allocated Team Size</label>
                <input
                  type="number"
                  min="1"
                  max="20"
                  value={newProject.teamSize}
                  onChange={(e) => setNewProject({ ...newProject, teamSize: Number(e.target.value) })}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div className="pt-3 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowAssignModal(false)}
                  className="px-3.5 py-1.5 text-xs text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl shadow-md"
                >
                  Assign Project
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
