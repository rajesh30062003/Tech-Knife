import React, { useState, useEffect } from 'react';
import { 
  AlertTriangle, ShieldAlert, Plus, CheckCircle2, ShieldCheck, Loader2 
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';
import { projectWorkspaceApi, ProjectRisk } from '../../../api/projectWorkspaceApi';
import { useAuth } from '../../../context/AuthContext';

interface ProjectRisksTabProps {
  project: EnterpriseProject;
}

const DEFAULT_MOCK_RISKS: ProjectRisk[] = [
  {
    id: 'RSK-301',
    riskCode: 'RSK-301',
    title: 'Google Drive Storage Quota Limit on Service Account',
    description: 'Service Account authentication failed with 403 storageQuotaExceeded during automated file uploads.',
    severity: 'HIGH',
    impact: 'Critical - Blocks cloud file storage for user deliverables',
    likelihood: 'HIGH',
    mitigationPlan: 'Migrated backend authentication to Google OAuth 2.0 Offline Authorization Code Flow with automatic refresh token rotation.',
    status: 'MITIGATED',
    reportedBy: 'Security Governance',
  },
  {
    id: 'RSK-302',
    riskCode: 'RSK-302',
    title: 'Database Connection Pool Exhaustion under Concurrent Load',
    description: 'Potential Tomcat thread starvation if HikariCP max pool size is exceeded during peak payroll disbursal.',
    severity: 'MEDIUM',
    impact: 'Moderate - High API latency on analytics queries',
    likelihood: 'LOW',
    mitigationPlan: 'Configured Redis caching for read-heavy dashboard metrics and set max pool size to 30.',
    status: 'IDENTIFIED',
    reportedBy: 'DevOps & Infra',
  },
];

export const ProjectRisksTab: React.FC<ProjectRisksTabProps> = ({ project }) => {
  const { user } = useAuth();
  const projectId = project.id || project.projectId || '';

  const [risks, setRisks] = useState<ProjectRisk[]>(DEFAULT_MOCK_RISKS);
  const [isLoading, setIsLoading] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);

  // Form state
  const [riskTitle, setRiskTitle] = useState('');
  const [riskDesc, setRiskDesc] = useState('');
  const [riskSeverity, setRiskSeverity] = useState('HIGH');
  const [riskMitigation, setRiskMitigation] = useState('');

  const loadRisks = async () => {
    if (!projectId) return;
    setIsLoading(true);
    try {
      const res = await projectWorkspaceApi.getRisks(projectId);
      if (res.data && res.data.length > 0) {
        setRisks(res.data);
      }
    } catch (err) {
      console.warn('Using default risks fallback');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadRisks();
  }, [projectId]);

  const handleCreateRisk = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!riskTitle.trim()) return;

    const newRisk: ProjectRisk = {
      id: `RSK-${Math.floor(100 + Math.random() * 900)}`,
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

    setRisks([newRisk, ...risks]);
    setRiskTitle('');
    setRiskDesc('');
    setRiskMitigation('');
    setShowAddModal(false);

    try {
      await projectWorkspaceApi.createRisk(projectId, newRisk);
    } catch (err) {
      console.warn('Risk created locally');
    }
  };

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
            onClick={() => setShowAddModal(true)}
            className="px-4 py-2.5 bg-rose-600 hover:bg-rose-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 self-start sm:self-auto"
          >
            <Plus className="w-4 h-4" /> Report New Risk
          </button>
        </div>
      </div>

      {/* Risk Cards */}
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
                    risk.severity === 'HIGH'
                      ? 'bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300'
                      : risk.severity === 'MEDIUM'
                      ? 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300'
                      : 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300'
                  }`}
                >
                  {risk.severity} Severity
                </span>
              </div>

              <span
                className={`px-3 py-1 rounded-xl text-xs font-bold ${
                  risk.status === 'MITIGATED' || risk.status === 'CLOSED'
                    ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 flex items-center gap-1'
                    : 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20 flex items-center gap-1'
                }`}
              >
                {risk.status === 'MITIGATED' ? <ShieldCheck className="w-3.5 h-3.5" /> : <AlertTriangle className="w-3.5 h-3.5" />}
                {risk.status}
              </span>
            </div>

            <div>
              <h4 className="text-sm font-extrabold text-slate-900 dark:text-white mb-1">
                {risk.title}
              </h4>
              <p className="text-xs text-slate-600 dark:text-slate-300 font-medium">
                {risk.description}
              </p>
            </div>

            {risk.mitigationPlan && (
              <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800 space-y-1">
                <span className="text-[10px] uppercase font-bold text-indigo-600 dark:text-indigo-400 block flex items-center gap-1">
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

      {/* Add Risk Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <form onSubmit={handleCreateRisk} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4 shadow-xl">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Report New Project Risk</h3>

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
                className="px-4 py-2 bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs rounded-xl shadow-md"
              >
                Submit Risk
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
