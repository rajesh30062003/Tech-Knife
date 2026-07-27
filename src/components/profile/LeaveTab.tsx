import React, { useState } from 'react';
import {
  CalendarDays,
  Plus,
  CheckCircle2,
  Clock,
  AlertCircle,
  X,
  Send,
  Loader2,
  Calendar,
} from 'lucide-react';

interface LeaveApplication {
  id: string;
  type: string;
  startDate: string;
  endDate: string;
  days: number;
  reason: string;
  status: 'Approved' | 'Pending' | 'Rejected';
  appliedDate: string;
  approver?: string;
}

const INITIAL_LEAVE_HISTORY: LeaveApplication[] = [
  {
    id: 'LV-101',
    type: 'Annual Vacation',
    startDate: '2026-11-12',
    endDate: '2026-11-15',
    days: 3,
    reason: 'Family annual vacation trip',
    status: 'Approved',
    appliedDate: '2026-10-01',
    approver: 'Marcus Brody',
  },
  {
    id: 'LV-102',
    type: 'Casual Leave',
    startDate: '2026-10-28',
    endDate: '2026-10-28',
    days: 1,
    reason: 'Personal home utility maintenance',
    status: 'Pending',
    appliedDate: '2026-10-18',
  },
  {
    id: 'LV-103',
    type: 'Sick Leave',
    startDate: '2026-08-04',
    endDate: '2026-08-04',
    days: 1,
    reason: 'Medical appointment',
    status: 'Approved',
    appliedDate: '2026-08-03',
    approver: 'Marcus Brody',
  },
];

export const LeaveTab: React.FC = () => {
  const [leaveHistory, setLeaveHistory] = useState<LeaveApplication[]>(INITIAL_LEAVE_HISTORY);
  const [showApplyModal, setShowApplyModal] = useState(false);

  // Form state
  const [leaveType, setLeaveType] = useState('Annual Vacation');
  const [startDate, setStartDate] = useState('2026-11-20');
  const [endDate, setEndDate] = useState('2026-11-21');
  const [reason, setReason] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmitLeave = (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    setTimeout(() => {
      const newLeave: LeaveApplication = {
        id: `LV-${Math.floor(100 + Math.random() * 900)}`,
        type: leaveType,
        startDate,
        endDate,
        days: startDate === endDate ? 1 : 2,
        reason,
        status: 'Pending',
        appliedDate: new Date().toISOString().split('T')[0],
      };

      setLeaveHistory([newLeave, ...leaveHistory]);
      setIsSubmitting(false);
      setShowApplyModal(false);
      setReason('');
    }, 800);
  };

  return (
    <div className="space-y-8">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-bold text-xs uppercase tracking-wider mb-1">
            <CalendarDays className="w-4 h-4" />
            <span>Time Off & Annual Leave Quotas</span>
          </div>
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
            Leave Requests & Quotas Summary
          </h3>
          <p className="text-xs text-slate-500">
            Submit time-off requests, monitor leave balances, and review manager approval logs
          </p>
        </div>

        <button
          onClick={() => setShowApplyModal(true)}
          className="px-5 py-3 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs rounded-2xl shadow-md transition-transform hover:scale-105 flex items-center gap-2 shrink-0"
        >
          <Plus className="w-4 h-4" /> Apply for Time-Off
        </button>
      </div>

      {/* Leave Quota Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-sm">
          <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Annual Paid Vacation</span>
          <div className="text-2xl font-black text-slate-900 dark:text-white">14 / 20 Days Left</div>
          <span className="text-[11px] text-emerald-500 font-semibold block">6 Days Consumed</span>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-sm">
          <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Sick & Medical Quota</span>
          <div className="text-2xl font-black text-slate-900 dark:text-white">8 / 10 Days Left</div>
          <span className="text-[11px] text-indigo-500 font-semibold block">2 Days Consumed</span>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-sm">
          <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Casual / Personal Quota</span>
          <div className="text-2xl font-black text-slate-900 dark:text-white">3 / 5 Days Left</div>
          <span className="text-[11px] text-amber-500 font-semibold block">1 Application Pending</span>
        </div>
      </div>

      {/* Leave History Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-lg">
        <div className="border-b border-slate-100 dark:border-slate-800 pb-3">
          <h3 className="text-base font-bold text-slate-900 dark:text-white flex items-center gap-2">
            <Calendar className="w-4 h-4 text-indigo-500" />
            Submitted Leave Applications
          </h3>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600 dark:text-slate-300">
            <thead className="bg-slate-50 dark:bg-slate-800/60 uppercase font-bold text-slate-400 text-[10px] tracking-wider">
              <tr>
                <th className="py-3.5 px-4">Leave Category</th>
                <th className="py-3.5 px-4">Dates</th>
                <th className="py-3.5 px-4">Duration</th>
                <th className="py-3.5 px-4">Reason</th>
                <th className="py-3.5 px-4">Applied On</th>
                <th className="py-3.5 px-4">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800 font-medium">
              {leaveHistory.map((item) => (
                <tr key={item.id} className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors">
                  <td className="py-4 px-4 font-bold text-slate-900 dark:text-white">{item.type}</td>
                  <td className="py-4 px-4 font-mono text-slate-700 dark:text-slate-300">
                    {item.startDate} {item.startDate !== item.endDate && `→ ${item.endDate}`}
                  </td>
                  <td className="py-4 px-4 font-bold">{item.days} Day(s)</td>
                  <td className="py-4 px-4 text-slate-500 max-w-xs truncate">{item.reason}</td>
                  <td className="py-4 px-4 text-slate-400 text-[11px]">{item.appliedDate}</td>
                  <td className="py-4 px-4">
                    {item.status === 'Approved' ? (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-emerald-500/10 text-emerald-500 border border-emerald-500/20">
                        <CheckCircle2 className="w-3 h-3" /> Approved by {item.approver || 'Manager'}
                      </span>
                    ) : item.status === 'Pending' ? (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-amber-500/10 text-amber-500 border border-amber-500/20">
                        <Clock className="w-3 h-3" /> Awaiting Manager
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-rose-500/10 text-rose-500 border border-rose-500/20">
                        <AlertCircle className="w-3 h-3" /> Rejected
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Leave Application Modal */}
      {showApplyModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 sm:p-8 max-w-lg w-full shadow-2xl space-y-6 relative animate-scaleIn">
            <button
              onClick={() => setShowApplyModal(false)}
              className="absolute top-5 right-5 p-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl bg-slate-100 dark:bg-slate-800 transition-colors"
            >
              <X className="w-4 h-4" />
            </button>

            <div className="border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
                Apply for Time-Off Leave
              </h3>
              <p className="text-xs text-slate-500">
                Submit your leave application for approval by your reporting manager
              </p>
            </div>

            <form onSubmit={handleSubmitLeave} className="space-y-4">
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">
                  Leave Type *
                </label>
                <select
                  value={leaveType}
                  onChange={(e) => setLeaveType(e.target.value)}
                  className="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-semibold text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="Annual Vacation">Annual Vacation Leave</option>
                  <option value="Sick Leave">Sick & Medical Leave</option>
                  <option value="Casual Leave">Casual Personal Leave</option>
                  <option value="Maternity/Paternity">Parental Leave</option>
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">
                    Start Date *
                  </label>
                  <input
                    type="date"
                    required
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                    className="w-full px-3.5 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-semibold text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">
                    End Date *
                  </label>
                  <input
                    type="date"
                    required
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                    className="w-full px-3.5 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-semibold text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">
                  Reason for Time-Off *
                </label>
                <textarea
                  required
                  rows={3}
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                  placeholder="Provide brief details regarding your time-off request..."
                  className="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-semibold text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 resize-none"
                />
              </div>

              <button
                type="submit"
                disabled={isSubmitting || !reason.trim()}
                className="w-full py-3 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs rounded-xl shadow transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
              >
                {isSubmitting ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4" />}
                <span>Submit Leave Application</span>
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
