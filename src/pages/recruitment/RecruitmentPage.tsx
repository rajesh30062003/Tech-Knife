import React from 'react';
import { UserPlus, Briefcase, Plus, Star, Calendar, FileText } from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';

export const RecruitmentPage: React.FC = () => {
  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <UserPlus className="w-4 h-4" />
            <span>Talent Acquisition Portal</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Recruitment & Candidate Pipeline</h1>
          <p className="text-xs text-slate-500">Manage active job openings, interview evaluations, and offer letter generation</p>
        </div>

        <button className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md">
          <Plus className="w-3.5 h-3.5" /> Post Job Opening
        </button>
      </div>

      {/* Active Jobs & Candidates */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Open Requisitions */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
          <h3 className="font-bold text-base text-slate-900 dark:text-white">Active Requisitions</h3>

          <div className="space-y-3">
            {[
              { role: 'Senior Spring Boot Architect', dept: 'Engineering', applicants: 24, status: 'Active' },
              { role: 'DevOps & Cloud Specialist', dept: 'Infrastructure', applicants: 18, status: 'Active' },
              { role: 'Lead UI/UX Designer', dept: 'Product', applicants: 12, status: 'Planning' },
            ].map((j, idx) => (
              <div key={idx} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 space-y-1">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{j.role}</span>
                  <StatusBadge status={j.status} />
                </div>
                <p className="text-[11px] text-slate-500">{j.dept} • {j.applicants} Applicants</p>
              </div>
            ))}
          </div>
        </div>

        {/* Candidate Pipeline */}
        <div className="lg:col-span-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
          <h3 className="font-bold text-base text-slate-900 dark:text-white">Candidate Pipeline & Ratings</h3>

          <div className="space-y-3">
            {[
              { name: 'Michael Chang', position: 'Senior Spring Boot Architect', stage: 'Technical Round', rating: 4.8, date: 'Interviewed Oct 12' },
              { name: 'Jessica Taylor', position: 'DevOps & Cloud Specialist', stage: 'HR Round', rating: 4.5, date: 'Interviewed Oct 13' },
              { name: 'David Kim', position: 'Senior Spring Boot Architect', stage: 'Offered', rating: 4.9, date: 'Offer Letter Dispatched' },
            ].map((c, idx) => (
              <div key={idx} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between gap-4">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{c.name}</span>
                    <StatusBadge status={c.stage} />
                  </div>
                  <p className="text-xs text-slate-500">{c.position} • {c.date}</p>
                </div>

                <div className="flex items-center gap-1 font-bold text-amber-500 text-xs">
                  <Star className="w-3.5 h-3.5 fill-current" />
                  <span>{c.rating} / 5.0</span>
                </div>
              </div>
            ))}
          </div>
        </div>

      </div>
    </div>
  );
};
