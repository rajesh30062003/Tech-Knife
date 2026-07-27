import React from 'react';
import { Target, DollarSign, Plus, Building2, User, ArrowUpRight } from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';

export const CrmPage: React.FC = () => {
  const pipeline = [
    {
      stage: 'Lead Contacted',
      val: '$120,000',
      deals: [
        { company: 'Nexus Logistics', contact: 'Robert Vance', val: '$45,000', prob: '20%' },
        { company: 'Quantum BioTech', contact: 'Dr. Aris Thorne', val: '$75,000', prob: '30%' },
      ]
    },
    {
      stage: 'Proposal Sent',
      val: '$280,000',
      deals: [
        { company: 'Apex Enterprises', contact: 'David Miller', val: '$140,000', prob: '60%' },
        { company: 'FinTech Dynamics', contact: 'Clara Oswald', val: '$140,000', prob: '70%' },
      ]
    },
    {
      stage: 'Negotiation',
      val: '$180,000',
      deals: [
        { company: 'CyberNet Global', contact: 'James Cole', val: '$180,000', prob: '85%' },
      ]
    },
    {
      stage: 'Closed Won',
      val: '$450,000',
      deals: [
        { company: 'Hyperion Energy', contact: 'Sarah Jenkins', val: '$450,000', prob: '100%' },
      ]
    }
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Target className="w-4 h-4" />
            <span>Sales & Revenue Management</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">CRM & Deals Pipeline</h1>
          <p className="text-xs text-slate-500">Track client accounts, proposal stages, and contract probability forecasts</p>
        </div>

        <button className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md">
          <Plus className="w-3.5 h-3.5" /> Add New Opportunity
        </button>
      </div>

      {/* Pipeline Board */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {pipeline.map((col) => (
          <div key={col.stage} className="bg-slate-100/70 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 space-y-3">
            <div className="flex items-center justify-between border-b border-slate-200 dark:border-slate-800 pb-2">
              <div>
                <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100">{col.stage}</h4>
                <p className="text-[11px] font-semibold text-indigo-600 dark:text-indigo-400">{col.val}</p>
              </div>
              <span className="px-2 py-0.5 text-[10px] font-bold rounded-full bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300">
                {col.deals.length}
              </span>
            </div>

            <div className="space-y-3">
              {col.deals.map((deal, idx) => (
                <div key={idx} className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl shadow-xs space-y-2 hover:border-indigo-400 transition-colors">
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{deal.company}</span>
                    <span className="text-[10px] font-semibold px-2 py-0.5 rounded bg-emerald-50 dark:bg-emerald-950 text-emerald-600">{deal.prob}</span>
                  </div>
                  <p className="text-[11px] text-slate-500 flex items-center gap-1">
                    <User className="w-3 h-3 text-slate-400" /> {deal.contact}
                  </p>
                  <div className="pt-2 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between">
                    <span className="text-xs font-extrabold text-slate-900 dark:text-white">{deal.val}</span>
                    <button className="text-[10px] font-semibold text-indigo-600 hover:underline">View Deal</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
