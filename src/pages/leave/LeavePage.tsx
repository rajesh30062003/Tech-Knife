import React, { useState } from 'react';
import { CalendarDays, Plus, CheckCircle2, Clock, AlertCircle, Calendar } from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';

export const LeavePage: React.FC = () => {
  const [showModal, setShowModal] = useState(false);

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <CalendarDays className="w-4 h-4" />
            <span>Time Off & Leave Balance Management</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Leave Requests & Quotas</h1>
          <p className="text-xs text-slate-500">Apply for time off, view annual quotas, and monitor team availability</p>
        </div>

        <button
          onClick={() => setShowModal(true)}
          className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md"
        >
          <Plus className="w-3.5 h-3.5" /> Apply Time-Off Request
        </button>
      </div>

      {/* Leave Balances Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <span className="text-xs text-slate-400 font-semibold uppercase">Annual Vacation Leave</span>
          <div className="text-2xl font-extrabold text-slate-900 dark:text-white mt-1">14 / 20 Days</div>
          <p className="text-[11px] text-emerald-600 dark:text-emerald-400 mt-1">6 Days Taken This Year</p>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <span className="text-xs text-slate-400 font-semibold uppercase">Sick & Casual Quota</span>
          <div className="text-2xl font-extrabold text-slate-900 dark:text-white mt-1">8 / 10 Days</div>
          <p className="text-[11px] text-indigo-600 dark:text-indigo-400 mt-1">2 Days Taken This Year</p>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <span className="text-xs text-slate-400 font-semibold uppercase">Pending Reviews</span>
          <div className="text-2xl font-extrabold text-slate-900 dark:text-white mt-1">1 Request</div>
          <p className="text-[11px] text-amber-600 dark:text-amber-400 mt-1">Awaiting Manager Approval</p>
        </div>
      </div>

      {/* Leave History */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <div className="border-b border-slate-100 dark:border-slate-800 pb-3">
          <h3 className="font-bold text-base text-slate-900 dark:text-white">Leave History & Applications</h3>
        </div>

        <div className="space-y-3">
          {[
            { type: 'Annual Vacation', range: 'Nov 12 - Nov 15, 2026', days: 3, reason: 'Family trip', status: 'Approved' },
            { type: 'Casual Leave', range: 'Oct 28, 2026', days: 1, reason: 'Personal errands', status: 'Pending' },
            { type: 'Sick Leave', range: 'Aug 04, 2026', days: 1, reason: 'Dental appointment', status: 'Approved' },
          ].map((item, idx) => (
            <div key={idx} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between gap-4">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{item.type}</span>
                  <StatusBadge status={item.status} />
                </div>
                <p className="text-xs text-slate-600 dark:text-slate-300">
                  {item.range} ({item.days} Day{item.days > 1 ? 's' : ''})
                </p>
                <p className="text-[11px] text-slate-400 italic">"{item.reason}"</p>
              </div>

              <span className="text-xs font-mono font-semibold text-indigo-600 dark:text-indigo-400">
                Ref: LV-2026-0{idx + 1}
              </span>
            </div>
          ))}
        </div>
      </div>

      {/* Modal Mock */}
      {showModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 max-w-md w-full space-y-4 shadow-2xl">
            <h3 className="text-base font-bold text-slate-900 dark:text-white">New Time-Off Request</h3>
            
            <div className="space-y-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300 block mb-1">Leave Type</label>
                <select className="w-full text-xs p-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800">
                  <option>Annual Vacation</option>
                  <option>Sick Leave</option>
                  <option>Casual Leave</option>
                </select>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300 block mb-1">Start Date</label>
                  <input type="date" className="w-full text-xs p-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800" />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300 block mb-1">End Date</label>
                  <input type="date" className="w-full text-xs p-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800" />
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300 block mb-1">Reason</label>
                <textarea rows={3} placeholder="Brief details for lead review..." className="w-full text-xs p-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800" />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl">
                Cancel
              </button>
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-500 rounded-xl shadow-md">
                Submit Application
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};
