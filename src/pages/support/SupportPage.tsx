import React from 'react';
import { HelpCircle, Plus, BookOpen, MessageSquare, CheckCircle2 } from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';

export const SupportPage: React.FC = () => {
  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <HelpCircle className="w-4 h-4" />
            <span>IT Service & Help Desk</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Knowledge Base & Support Tickets</h1>
          <p className="text-xs text-slate-500">Submit technical requests, monitor SLA resolution targets, and search FAQs</p>
        </div>

        <button className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md">
          <Plus className="w-3.5 h-3.5" /> Submit Ticket
        </button>
      </div>

      {/* Tickets Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <h3 className="font-bold text-base text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
          Open Support Requests
        </h3>

        <div className="space-y-3">
          {[
            { id: 'TCK-801', subject: 'VPN Tunnel Access Disconnection on M3 Mac', category: 'Infrastructure', priority: 'High', status: 'In Progress', date: 'Oct 14, 2026' },
            { id: 'TCK-802', subject: 'Tax Slip W2 Re-generation Request', category: 'Payroll', priority: 'Medium', status: 'Resolved', date: 'Oct 10, 2026' },
          ].map((t) => (
            <div key={t.id} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between gap-4">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <span className="font-mono text-xs font-bold text-indigo-600 dark:text-indigo-400">{t.id}</span>
                  <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{t.subject}</span>
                  <StatusBadge status={t.status} />
                </div>
                <p className="text-[11px] text-slate-500">Category: {t.category} • Submitted: {t.date}</p>
              </div>

              <StatusBadge status={t.priority} />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
