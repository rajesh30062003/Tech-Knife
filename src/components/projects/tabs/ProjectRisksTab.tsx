import React, { useState, useEffect } from 'react';
import { 
  ShieldAlert, Plus, CheckCircle2, ShieldCheck, AlertTriangle, Loader2, Edit3 
} from 'lucide-react';
import { toast } from 'sonner';
import { EnterpriseProject } from '../../../api/projects';
import { projectWorkspaceApi, ProjectRisk } from '../../../api/projectWorkspaceApi';
import { useAuth } from '../../../context/AuthContext';
import { canApproveProjectStatus } from '../../../constants/projectStatus';

interface ProjectRisksTabProps {
  project: EnterpriseProject;
}

export const ProjectRisksTab: React.FC<ProjectRisksTabProps> = ({ project }) => {
  const { user } = useAuth();
  const projectId = project.id || project.projectId || project.projectCode || '';

  const isApprover = canApproveProjectStatus(user, project);

  const [risks, setRisks] = useState<ProjectRisk[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingRisk, setEditingRisk] = useState<ProjectRisk | null>(null);

  // Form state
  const [riskTitle, setRiskTitle] = useState('');
  const [riskDesc, setRiskDesc] = useState('');
  const [riskSeverity, setRiskSeverity] = useState('HIGH');
  const [riskMitigation, setRiskMitigation] = useState('');
  const [riskStatus, setRiskStatus] = useState('IDENTIFIED');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const loadRisks = async () => {
    if (!projectId) return;
    setIsLoading(true);
    try {
      const res = await projectWorkspaceApi.getRisks(projectId);
      if (res?.data) {
        setRisks(res.data);
      } else {
        setRisks([]);
      }
    } catch (err) {
      console.warn('Could not fetch project risks:', err);
      setRisks([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadRisks();
  }, [projectId]);

  const handleOpenAddModal = () => {
    setRiskTitle('');
    setRiskDesc('');
    setRiskSeverity('HIGH');
    setRiskMitigation('');
    setRiskStatus('IDENTIFIED');
    setEditingRisk(null);
    setShowAddModal(true);
  };

  const handleOpenEditModal = (risk: ProjectRisk) => {
    setEditingRisk(risk);
    setRiskTitle(risk.title || '');
    setRiskDesc(risk.description || '');
    setRiskSeverity(risk.severity || 'HIGH');
    setRiskMitigation(risk.mitigationPlan || '');
    setRiskStatus(risk.status || 'IDENTIFIED');
    setShowAddModal(true);
  };

  const handleSaveRisk = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!riskTitle.trim()) {
      toast.error('Risk title is required.');
      return;
    }

    setIsSubmitting(true);
    try {
      if (editingRisk) {
        const updated: Partial<ProjectRisk> = {
          title: riskTitle.trim(),
          description: riskDesc.trim() || 'Identified project execution risk.',
          severity: riskSeverity,
          mitigationPlan: riskMitigation.trim(),
          status: riskStatus,
        };
        await projectWorkspaceApi.updateRisk(projectId, editingRisk.id, updated);
        toast.success('Project risk updated successfully.');
      } else {
        const newRisk: Partial<ProjectRisk> = {
          riskCode: `RSK-${Math.floor(100 + Math.random() * 900)}`,
          title: riskTitle.trim(),
          description: riskDesc.trim() || 'Identified project execution risk.',
          severity: riskSeverity,
          impact: 'Evaluated by Project Governance',
          likelihood: 'MEDIUM',
          mitigationPlan: riskMitigation.trim() || 'Mitigation strategy under review.',
          status: 'IDENTIFIED',
          reportedBy: user ? `${user.firstName} ${user.lastName}` : 'Governance Admin',
        };
        await projectWorkspaceApi.createRisk(projectId, newRisk);
        toast.success('Project risk reported successfully.');
      }

      setShowAddModal(false);
      await loadRisks();
    } catch (err: any) {
      toast.error(err.response?.data?.message || err.message || 'Failed to save project risk.');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Derived Summary Counts
  const highRisks = risks.filter(r => (r.severity || '').toUpperCase() === 'HIGH').length;
  const mediumRisks = risks.filter(r => (r.severity || '').toUpperCase() === 'MEDIUM').length;
  const lowRisks = risks.filter(r => (r.severity || '').toUpperCase() === 'LOW').length;
  const openRisks = risks.filter(r => !['MITIGATED', 'CLOSED', 'RESOLVED'].includes((r.status || '').toUpperCase())).length;
  const mitigatedRisks = risks.filter(r => ['MITIGATED', 'CLOSED', 'RESOLVED'].includes((r.status || '').toUpperCase())).length;

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-rose-600 dark:text-rose-400 mb-1">
              <ShieldAlert className="w-4 h-4" />
              <span>Project Risk Governance & Mitigation Matrix</span>
            </div>
            <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
              Identified Risks & Contingency Register ({risks.length})
            </h3>
            <p className="text-xs text-slate-500">Monitor project risk severity, likelihood, and resolution strategies</p>
          </div>

          <button
            onClick={handleOpenAddModal}
            className="px-4 py-2.5 bg-rose-600 hover:bg-rose-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 self-start sm:self-auto cursor-pointer"
          >
            <Plus className="w-4 h-4" /> Report New Risk
          </button>
        </div>

        {/* Risk Summary Badge Matrix */}
        <div className="grid grid-cols-2 sm:grid-cols-5 gap-3 text-xs font-bold pt-1">
          <div className="p-3 bg-rose-500/10 border border-rose-500/20 text-rose-700 dark:text-rose-300 rounded-2xl flex flex-col justify-between">
            <span className="text-[10px] uppercase text-rose-500 font-mono">High Severity</span>
            <span className="text-xl font-black">{highRisks}</span>
          </div>

          <div className="p-3 bg-amber-500/10 border border-amber-500/20 text-amber-700 dark:text-amber-300 rounded-2xl flex flex-col justify-between">
            <span className="text-[10px] uppercase text-amber-500 font-mono">Medium Severity</span>
            <span className="text-xl font-black">{mediumRisks}</span>
          </div>

          <div className="p-3 bg-blue-500/10 border border-blue-500/20 text-blue-700 dark:text-blue-300 rounded-2xl flex flex-col justify-between">
            <span className="text-[10px] uppercase text-blue-500 font-mono">Low Severity</span>
            <span className="text-xl font-black">{lowRisks}</span>
          </div>

          <div className="p-3 bg-orange-500/10 border border-orange-500/20 text-orange-700 dark:text-orange-300 rounded-2xl flex flex-col justify-between">
            <span className="text-[10px] uppercase text-orange-500 font-mono">Active Open</span>
            <span className="text-xl font-black">{openRisks}</span>
          </div>

          <div className="p-3 bg-emerald-500/10 border border-emerald-500/20 text-emerald-700 dark:text-emerald-300 rounded-2xl flex flex-col justify-between">
            <span className="text-[10px] uppercase text-emerald-500 font-mono">Mitigated / Resolved</span>
            <span className="text-xl font-black">{mitigatedRisks}</span>
          </div>
        </div>
      </div>

      {/* Risk Cards Container */}
      {isLoading ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl space-y-2">
          <Loader2 className="w-6 h-6 animate-spin text-rose-500 mx-auto" />
          <p className="text-xs text-slate-400 font-medium">Fetching project risk register...</p>
        </div>
      ) : risks.length === 0 ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl space-y-2">
          <ShieldCheck className="w-8 h-8 text-emerald-500 mx-auto" />
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white">
            No risks recorded for this project.
          </h4>
          <p className="text-xs text-slate-500 max-w-sm mx-auto">
            This project has no open or historical risk entries logged in governance.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {risks.map((risk) => (
            <div
              key={risk.id}
              className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4"
            >
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 dark:border-slate-800 pb-3">
                <div className="flex items-center gap-2">
                  <span className="px-2.5 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 font-mono text-[10px] font-bold rounded-md">
                    {risk.riskCode || risk.id}
                  </span>
                  <span
                    className={`px-2.5 py-0.5 rounded-full font-bold text-[10px] uppercase ${
                      (risk.severity || '').toUpperCase() === 'HIGH'
                        ? 'bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300'
                        : (risk.severity || '').toUpperCase() === 'MEDIUM'
                        ? 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300'
                        : 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300'
                    }`}
                  >
                    {risk.severity || 'MEDIUM'} Severity
                  </span>
                </div>

                <div className="flex items-center gap-2">
                  <span
                    className={`px-3 py-1 rounded-xl text-xs font-bold ${
                      ['MITIGATED', 'CLOSED', 'RESOLVED'].includes((risk.status || '').toUpperCase())
                        ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 flex items-center gap-1'
                        : 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20 flex items-center gap-1'
                    }`}
                  >
                    {['MITIGATED', 'CLOSED', 'RESOLVED'].includes((risk.status || '').toUpperCase()) 
                      ? <ShieldCheck className="w-3.5 h-3.5" /> 
                      : <AlertTriangle className="w-3.5 h-3.5" />
                    }
                    {risk.status}
                  </span>

                  {isApprover && (
                    <button
                      onClick={() => handleOpenEditModal(risk)}
                      className="p-1.5 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-600 dark:text-slate-300 rounded-lg text-xs"
                      title="Edit Risk"
                    >
                      <Edit3 className="w-3.5 h-3.5" />
                    </button>
                  )}
                </div>
              </div>

              <div>
                <h4 className="text-sm font-extrabold text-slate-900 dark:text-white mb-1">
                  {risk.title}
                </h4>
                <p className="text-xs text-slate-600 dark:text-slate-300 font-medium">
                  {risk.description}
                </p>
                {risk.reportedBy && (
                  <span className="text-[10px] text-slate-400 mt-1 block">
                    Reported by: <strong className="text-slate-600 dark:text-slate-300">{risk.reportedBy}</strong>
                  </span>
                )}
              </div>

              {risk.mitigationPlan && (
                <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800 space-y-1">
                  <span className="text-[10px] uppercase font-bold text-indigo-600 dark:text-indigo-400 flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500" /> Active Mitigation Strategy
                  </span>
                  <p className="text-xs text-slate-700 dark:text-slate-300 font-medium">
                    {risk.mitigationPlan}
                  </p>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Add / Edit Risk Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <form onSubmit={handleSaveRisk} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4 shadow-xl">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">
              {editingRisk ? 'Update Project Risk' : 'Report New Project Risk'}
            </h3>

            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Risk Title *</label>
              <input
                type="text"
                required
                value={riskTitle}
                onChange={(e) => setRiskTitle(e.target.value)}
                placeholder="e.g. Cloud API Rate Limiting on Uploads"
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Risk Severity *</label>
              <select
                value={riskSeverity}
                onChange={(e) => setRiskSeverity(e.target.value)}
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              >
                <option value="HIGH">HIGH Severity</option>
                <option value="MEDIUM">MEDIUM Severity</option>
                <option value="LOW">LOW Severity</option>
              </select>
            </div>

            {editingRisk && (
              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Risk Status *</label>
                <select
                  value={riskStatus}
                  onChange={(e) => setRiskStatus(e.target.value)}
                  className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
                >
                  <option value="IDENTIFIED">IDENTIFIED / OPEN</option>
                  <option value="MITIGATED">MITIGATED</option>
                  <option value="RESOLVED">RESOLVED</option>
                  <option value="CLOSED">CLOSED</option>
                </select>
              </div>
            )}

            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Risk Description</label>
              <textarea
                rows={2}
                value={riskDesc}
                onChange={(e) => setRiskDesc(e.target.value)}
                placeholder="Describe potential technical or delivery impact..."
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Mitigation Plan</label>
              <textarea
                rows={2}
                value={riskMitigation}
                onChange={(e) => setRiskMitigation(e.target.value)}
                placeholder="Outline action plan to resolve or prevent..."
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
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
                disabled={isSubmitting}
                className="px-4 py-2 bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs rounded-xl shadow-md disabled:opacity-50"
              >
                {isSubmitting ? 'Saving...' : 'Save Risk'}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
