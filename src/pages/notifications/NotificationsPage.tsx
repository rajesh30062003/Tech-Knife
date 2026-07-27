import React from 'react';
import { Bell, ShieldAlert, CheckCircle2, Info, Check } from 'lucide-react';

export const NotificationsPage: React.FC = () => {
  return (
    <div className="space-y-8 max-w-4xl">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Bell className="w-4 h-4" />
            <span>Activity Stream & Operational Alerts</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Notifications Inbox</h1>
          <p className="text-xs text-slate-500">System security notifications, leave approvals, and sprint deliverables</p>
        </div>

        <button className="inline-flex items-center gap-2 px-4 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-700 dark:text-slate-300 font-semibold text-xs rounded-xl transition-all">
          <Check className="w-3.5 h-3.5" /> Mark All as Read
        </button>
      </div>

      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <div className="space-y-3">
          {[
            { title: 'Leave Request Approved', msg: 'Your time-off application for Nov 12 - Nov 15 was approved by Marcus Brody.', time: '10 mins ago', type: 'success' },
            { title: 'Security Alert: New Session Login', msg: 'A new session was authenticated from IP 192.168.1.45 (macOS Chrome).', time: '1 hour ago', type: 'warning' },
            { title: 'September Payroll Disbursed', msg: 'Your net salary slip of $7,850.00 has been transferred via direct deposit.', time: '2 days ago', type: 'info' },
          ].map((n, idx) => (
            <div key={idx} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-start gap-3">
              <div className="p-2 bg-indigo-50 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400 rounded-lg mt-0.5">
                <Bell className="w-4 h-4" />
              </div>
              <div className="flex-1 space-y-0.5">
                <div className="flex items-center justify-between">
                  <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100">{n.title}</h4>
                  <span className="text-[10px] text-slate-400">{n.time}</span>
                </div>
                <p className="text-xs text-slate-600 dark:text-slate-300">{n.msg}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
