import React, { useState } from 'react';
import { CalendarDays, Plus, Clock, CheckCircle2, ChevronRight, Send, X, Loader2 } from 'lucide-react';
import { Link } from 'react-router-dom';

interface LeaveQuota {
  title: string;
  remaining: number;
  total: number;
  consumed: number;
  color: string;
}

export const LeaveBalanceWidget: React.FC = () => {
  const [showModal, setShowModal] = useState(false);
  const [quotas, setQuotas] = useState<LeaveQuota[]>([
    { title: 'Annual Vacation', remaining: 14, total: 20, consumed: 6, color: 'from-emerald-500 to-teal-600' },
    { title: 'Sick & Medical', remaining: 8, total: 10, consumed: 2, color: 'from-indigo-500 to-indigo-700' },
    { title: 'Casual Personal', remaining: 3, total: 5, consumed: 2, color: 'from-amber-500 to-amber-600' },
  ]);

  // Apply leave form state
  const [leaveType, setLeaveType] = useState('Annual Vacation');
  const [startDate, setStartDate] = useState('2026-11-20');
  const [endDate, setEndDate] = useState('2026-11-21');
  const [reason, setReason] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMsg, setSuccessMsg] = useState(false);

  const handleApplyLeave = (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    setTimeout(() => {
      setIsSubmitting(false);
      setSuccessMsg(true);
      setTimeout(() => {
        setSuccessMsg(false);
        setShowModal(false);
        setReason('');
      }, 1500);
    }, 800);
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-5 flex flex-col justify-between h-full transition-all hover:border-indigo-500/30">
      {/* Widget Header */}
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-amber-500/10 text-amber-600 dark:text-amber-400">
            <CalendarDays className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Leave Quota Balance</h3>
            <p className="text-[11px] text-slate-500">Annual Paid Time-Off & Sick Leave</p>
          </div>
        </div>

        <button
          onClick={() => setShowModal(true)}
          className="px-3 py-1.5 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs rounded-xl shadow transition-transform hover:scale-105 flex items-center gap-1 shrink-0"
        >
          <Plus className="w-3.5 h-3.5" /> Request Off
        </button>
      </div>

      {/* Quota Progress Cards */}
      <div className="space-y-3 flex-1">
        {quotas.map((q, idx) => {
          const pct = Math.round((q.remaining / q.total) * 100);
          return (
            <div
              key={idx}
              className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-950/60 border border-slate-200/80 dark:border-slate-800 space-y-2"
            >
              <div className="flex items-center justify-between text-xs">
                <span className="font-bold text-slate-800 dark:text-slate-200">{q.title}</span>
                <span className="font-mono font-extrabold text-slate-900 dark:text-white">
                  {q.remaining} / {q.total} Days
                </span>
              </div>

              {/* Progress bar */}
              <div className="w-full h-2 bg-slate-200 dark:bg-slate-800 rounded-full overflow-hidden">
                <div
                  className={`h-full bg-gradient-to-r ${q.color} rounded-full transition-all duration-500`}
                  style={{ width: `${pct}%` }}
                />
              </div>

              <div className="flex justify-between text-[10px] text-slate-400 font-medium">
                <span>{q.consumed} Days Used</span>
                <span>{pct}% Available</span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Upcoming Approved Leave Notice */}
      <div className="p-3 rounded-2xl bg-indigo-50/50 dark:bg-indigo-950/30 border border-indigo-500/20 flex items-center justify-between text-xs">
        <div className="flex items-center gap-2">
          <Clock className="w-4 h-4 text-indigo-500 shrink-0" />
          <span className="text-slate-700 dark:text-slate-300 font-semibold text-[11px]">
            Upcoming: Nov 12 - Nov 15 (3 Days)
          </span>
        </div>
        <span className="px-2 py-0.5 bg-emerald-500/10 text-emerald-500 text-[10px] font-bold rounded-full border border-emerald-500/20">
          Approved
        </span>
      </div>

      {/* Leave Application Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 max-w-md w-full shadow-2xl space-y-4 relative">
            <button
              onClick={() => setShowModal(false)}
              className="absolute top-4 right-4 p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl bg-slate-100 dark:bg-slate-800"
            >
              <X className="w-4 h-4" />
            </button>

            <h4 className="text-base font-extrabold text-slate-900 dark:text-white">Quick Leave Request</h4>

            {successMsg ? (
              <div className="p-4 bg-emerald-500/10 border border-emerald-500/30 rounded-2xl text-emerald-500 text-xs font-bold text-center flex items-center justify-center gap-2">
                <CheckCircle2 className="w-5 h-5" /> Leave application submitted to your manager!
              </div>
            ) : (
              <form onSubmit={handleApplyLeave} className="space-y-3 text-xs">
                <div>
                  <label className="block font-bold text-slate-400 uppercase tracking-wider mb-1">Leave Type</label>
                  <select
                    value={leaveType}
                    onChange={(e) => setLeaveType(e.target.value)}
                    className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                  >
                    <option value="Annual Vacation">Annual Vacation Leave</option>
                    <option value="Sick Leave">Sick & Medical Leave</option>
                    <option value="Casual Leave">Casual Personal Leave</option>
                  </select>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block font-bold text-slate-400 uppercase tracking-wider mb-1">Start Date</label>
                    <input
                      type="date"
                      required
                      value={startDate}
                      onChange={(e) => setStartDate(e.target.value)}
                      className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                    />
                  </div>
                  <div>
                    <label className="block font-bold text-slate-400 uppercase tracking-wider mb-1">End Date</label>
                    <input
                      type="date"
                      required
                      value={endDate}
                      onChange={(e) => setEndDate(e.target.value)}
                      className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                    />
                  </div>
                </div>

                <div>
                  <label className="block font-bold text-slate-400 uppercase tracking-wider mb-1">Reason</label>
                  <textarea
                    required
                    rows={2}
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    placeholder="Brief description for manager review..."
                    className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium resize-none"
                  />
                </div>

                <button
                  type="submit"
                  disabled={isSubmitting || !reason.trim()}
                  className="w-full py-2.5 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl shadow transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
                >
                  {isSubmitting ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4" />}
                  Submit Application
                </button>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
