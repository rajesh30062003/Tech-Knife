import React, { useState, useEffect } from 'react';
import { ShieldCheck, Plus, Clock, CheckCircle2, Filter } from 'lucide-react';
import { approvalApi } from '../../api/coreServices';
import { ApprovalWorkflow } from '../../types';
import { ApprovalWorkflowCard } from '../../components/core/ApprovalWorkflowCard';

export const WorkflowsPage: React.FC = () => {
  const [workflows, setWorkflows] = useState<ApprovalWorkflow[]>([]);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [isLoading, setIsLoading] = useState(true);

  const loadWorkflows = async () => {
    setIsLoading(true);
    try {
      const data = await approvalApi.getWorkflows();
      setWorkflows(data);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadWorkflows();
  }, []);

  const filtered = statusFilter === 'ALL'
    ? workflows
    : workflows.filter((w) => w.status === statusFilter);

  return (
    <div className="space-y-8 pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <ShieldCheck className="w-4 h-4" />
            <span>Enterprise Approval Engine</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Approval Workflows Directory</h1>
          <p className="text-xs text-slate-500">Manage multi-stage approval sequences for leave, expenses, payroll disbursement & onboarding</p>
        </div>

        <div className="flex items-center gap-2">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="text-xs font-bold p-2.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-900 dark:text-slate-100 shadow-xs"
          >
            <option value="ALL">All Statuses</option>
            <option value="PENDING">Pending Approval</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>
      </div>

      {/* Workflows List */}
      {isLoading ? (
        <div className="p-12 text-center text-xs text-slate-400">Loading workflows...</div>
      ) : filtered.length === 0 ? (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-12 text-center space-y-2">
          <ShieldCheck className="w-10 h-10 text-slate-400 mx-auto" />
          <h3 className="font-extrabold text-sm text-slate-800 dark:text-slate-200">No Workflows Found</h3>
          <p className="text-xs text-slate-500">No active approval process matches the selected filter.</p>
        </div>
      ) : (
        <div className="space-y-6">
          {filtered.map((wf) => (
            <ApprovalWorkflowCard key={wf.id} workflow={wf} onWorkflowUpdated={loadWorkflows} />
          ))}
        </div>
      )}
    </div>
  );
};
