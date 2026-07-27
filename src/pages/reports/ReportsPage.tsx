import React from 'react';
import { BarChart3, Download, FileText, TrendingUp, DollarSign, Users } from 'lucide-react';

export const ReportsPage: React.FC = () => {
  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <BarChart3 className="w-4 h-4" />
            <span>Executive Intelligence Center</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Reports & Export Center</h1>
          <p className="text-xs text-slate-500">Generate executive summaries, financial velocity metrics, and productivity reports</p>
        </div>

        <button className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md">
          <Download className="w-3.5 h-3.5" /> Export All Data (CSV / JSON)
        </button>
      </div>

      {/* Reports Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="p-6 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="font-bold text-sm text-slate-900 dark:text-white">Q3 Financial & Payroll Disbursement</h3>
            <DollarSign className="w-5 h-5 text-emerald-500" />
          </div>
          <p className="text-xs text-slate-500">Comprehensive breakdown of basic salaries, tax withholdings, and bonus allocations.</p>
          <button className="px-3 py-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-xs font-semibold rounded-lg transition-colors flex items-center gap-1">
            <Download className="w-3.5 h-3.5" /> Export PDF Report
          </button>
        </div>

        <div className="p-6 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="font-bold text-sm text-slate-900 dark:text-white">Sprint Velocity & Code Review Metrics</h3>
            <TrendingUp className="w-5 h-5 text-indigo-500" />
          </div>
          <p className="text-xs text-slate-500">Engineering productivity metrics, completed story points, and commit frequency.</p>
          <button className="px-3 py-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-xs font-semibold rounded-lg transition-colors flex items-center gap-1">
            <Download className="w-3.5 h-3.5" /> Export CSV Report
          </button>
        </div>
      </div>
    </div>
  );
};
