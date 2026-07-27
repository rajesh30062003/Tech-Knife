import React, { useState } from 'react';
import {
  CheckCircle2,
  XCircle,
  Clock,
  Ban,
  ShieldCheck,
  Send,
  MessageSquare,
  ChevronRight,
  UserCheck
} from 'lucide-react';
import { ApprovalWorkflow, ApprovalStatus } from '../../types';
import { approvalApi } from '../../api/coreServices';
import { useAuth } from '../../context/AuthContext';

interface ApprovalWorkflowCardProps {
  workflow: ApprovalWorkflow;
  onWorkflowUpdated?: () => void;
}

export const ApprovalWorkflowCard: React.FC<ApprovalWorkflowCardProps> = ({
  workflow,
  onWorkflowUpdated,
}) => {
  const { user } = useAuth();
  const [activeComment, setActiveComment] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);

  const handleAction = async (stepNumber: number, status: 'APPROVED' | 'REJECTED') => {
    setIsProcessing(true);
    try {
      await approvalApi.processApprovalStep(
        workflow.id,
        stepNumber,
        status,
        `${user?.firstName} ${user?.lastName}`,
        activeComment
      );
      setActiveComment('');
      if (onWorkflowUpdated) onWorkflowUpdated();
    } finally {
      setIsProcessing(false);
    }
  };

  const getStatusBadge = (status: ApprovalStatus) => {
    switch (status) {
      case 'APPROVED':
        return (
          <span className="px-2.5 py-1 rounded-full bg-emerald-100 dark:bg-emerald-950/80 text-emerald-700 dark:text-emerald-300 font-extrabold text-[10px] flex items-center gap-1">
            <CheckCircle2 className="w-3 h-3" /> Approved
          </span>
        );
      case 'REJECTED':
        return (
          <span className="px-2.5 py-1 rounded-full bg-rose-100 dark:bg-rose-950/80 text-rose-700 dark:text-rose-300 font-extrabold text-[10px] flex items-center gap-1">
            <XCircle className="w-3 h-3" /> Rejected
          </span>
        );
      case 'CANCELLED':
        return (
          <span className="px-2.5 py-1 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 font-extrabold text-[10px] flex items-center gap-1">
            <Ban className="w-3 h-3" /> Cancelled
          </span>
        );
      default:
        return (
          <span className="px-2.5 py-1 rounded-full bg-amber-100 dark:bg-amber-950/80 text-amber-700 dark:text-amber-300 font-extrabold text-[10px] flex items-center gap-1">
            <Clock className="w-3 h-3 animate-spin" /> Pending Approval
          </span>
        );
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-5 shadow-xs">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 dark:border-slate-800 pb-3">
        <div>
          <div className="flex items-center gap-2 text-xs font-mono font-bold text-indigo-600 dark:text-indigo-400">
            <ShieldCheck className="w-4 h-4" />
            <span>{workflow.workflowNumber}</span>
            <span className="px-2 py-0.5 rounded bg-slate-100 dark:bg-slate-800 text-[10px] uppercase font-bold text-slate-600 dark:text-slate-300">
              Module: {workflow.module}
            </span>
          </div>
          <h3 className="font-extrabold text-base text-slate-900 dark:text-white mt-1">{workflow.title}</h3>
          <p className="text-xs text-slate-500">
            Requested by <strong className="text-slate-800 dark:text-slate-200">{workflow.requesterName}</strong> ({workflow.requesterEmail})
          </p>
        </div>

        {getStatusBadge(workflow.status)}
      </div>

      {/* Payload Summary */}
      <div className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/60 dark:border-slate-800 text-xs grid grid-cols-2 sm:grid-cols-3 gap-3 font-medium">
        {Object.entries(workflow.payload).map(([k, v]) => (
          <div key={k}>
            <span className="text-[10px] uppercase tracking-wider text-slate-400 font-bold block">{k}:</span>
            <span className="text-slate-900 dark:text-slate-100 font-bold">{String(v)}</span>
          </div>
        ))}
      </div>

      {/* Approval Step Timeline */}
      <div className="space-y-3">
        <h4 className="text-xs font-extrabold uppercase tracking-wider text-slate-400">Approval Workflow Sequence</h4>

        <div className="space-y-2">
          {workflow.steps.map((step) => {
            const isCurrent = workflow.status === 'PENDING' && step.status === 'PENDING';

            return (
              <div
                key={step.stepNumber}
                className={`p-4 rounded-2xl border transition-all ${
                  isCurrent
                    ? 'border-indigo-500 bg-indigo-50/30 dark:bg-indigo-950/20 shadow-xs'
                    : 'border-slate-200/80 dark:border-slate-800 bg-slate-50/40 dark:bg-slate-950/40'
                }`}
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                  <div className="flex items-center gap-3">
                    <div
                      className={`w-7 h-7 rounded-full font-extrabold text-xs flex items-center justify-center shrink-0 ${
                        step.status === 'APPROVED'
                          ? 'bg-emerald-500 text-white'
                          : step.status === 'REJECTED'
                          ? 'bg-rose-500 text-white'
                          : 'bg-indigo-600 text-white'
                      }`}
                    >
                      {step.stepNumber}
                    </div>
                    <div>
                      <div className="font-extrabold text-xs text-slate-900 dark:text-white">{step.stepName}</div>
                      <div className="text-[11px] text-slate-500 font-semibold">
                        Role: {step.approverRole.replace('ROLE_', '')} • Approver:{' '}
                        <strong className="text-indigo-600 dark:text-indigo-400">{step.approverName || 'Pending Assignment'}</strong>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    {getStatusBadge(step.status)}
                    {step.actionAt && (
                      <span className="text-[10px] text-slate-400 font-medium">
                        {new Date(step.actionAt).toLocaleTimeString()}
                      </span>
                    )}
                  </div>
                </div>

                {step.comment && (
                  <div className="mt-2 text-[11px] text-slate-600 dark:text-slate-300 italic bg-white dark:bg-slate-900 p-2 rounded-xl border border-slate-200 dark:border-slate-800">
                    "{step.comment}"
                  </div>
                )}

                {/* Review Action Controls */}
                {isCurrent && (
                  <div className="mt-3 pt-3 border-t border-slate-200/80 dark:border-slate-800 space-y-2">
                    <input
                      type="text"
                      placeholder="Add reviewer comments (optional)..."
                      value={activeComment}
                      onChange={(e) => setActiveComment(e.target.value)}
                      className="w-full text-xs p-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-900 dark:text-slate-100"
                    />

                    <div className="flex items-center justify-end gap-2">
                      <button
                        onClick={() => handleAction(step.stepNumber, 'REJECTED')}
                        disabled={isProcessing}
                        className="px-3.5 py-1.5 bg-rose-600 hover:bg-rose-500 text-white font-extrabold text-xs rounded-xl shadow-xs transition-all disabled:opacity-50"
                      >
                        Reject Request
                      </button>
                      <button
                        onClick={() => handleAction(step.stepNumber, 'APPROVED')}
                        disabled={isProcessing}
                        className="px-3.5 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white font-extrabold text-xs rounded-xl shadow-xs transition-all disabled:opacity-50"
                      >
                        Approve Step
                      </button>
                    </div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
