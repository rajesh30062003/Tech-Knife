import React, { useState } from 'react';
import {
  Clock,
  MapPin,
  CheckCircle2,
  AlertCircle,
  Calendar,
  Zap,
  Check,
} from 'lucide-react';

interface AttendanceRecord {
  id: string;
  date: string;
  clockIn: string;
  clockOut: string;
  totalHours: string;
  location: string;
  status: 'Present' | 'Late' | 'On Leave' | 'Weekend';
}

const INITIAL_RECORDS: AttendanceRecord[] = [
  { id: 'att-1', date: 'Today (Oct 24)', clockIn: '09:00 AM', clockOut: 'In Progress', totalHours: '7h 15m', location: 'San Jose HQ (IP: 192.168.1.45)', status: 'Present' },
  { id: 'att-2', date: 'Oct 23, 2026', clockIn: '09:02 AM', clockOut: '06:15 PM', totalHours: '8h 45m', location: 'San Jose HQ', status: 'Present' },
  { id: 'att-3', date: 'Oct 22, 2026', clockIn: '09:28 AM', clockOut: '06:30 PM', totalHours: '8h 32m', location: 'Remote / VPN', status: 'Late' },
  { id: 'att-4', date: 'Oct 21, 2026', clockIn: '08:55 AM', clockOut: '06:00 PM', totalHours: '8h 35m', location: 'San Jose HQ', status: 'Present' },
  { id: 'att-5', date: 'Oct 20, 2026', clockIn: '09:00 AM', clockOut: '06:10 PM', totalHours: '8h 40m', location: 'San Jose HQ', status: 'Present' },
  { id: 'att-6', date: 'Oct 19, 2026', clockIn: '09:05 AM', clockOut: '06:15 PM', totalHours: '8h 40m', location: 'Remote / VPN', status: 'Present' },
];

export const AttendanceTab: React.FC = () => {
  const [clockedIn, setClockedIn] = useState(true);
  const [clockTime, setClockTime] = useState('09:00 AM');
  const [records, setRecords] = useState<AttendanceRecord[]>(INITIAL_RECORDS);

  const handleToggleClock = () => {
    if (!clockedIn) {
      const nowStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      setClockedIn(true);
      setClockTime(nowStr);
      setRecords([
        {
          id: `att-${Date.now()}`,
          date: 'Today',
          clockIn: nowStr,
          clockOut: 'In Progress',
          totalHours: '0h 01m',
          location: 'San Jose HQ (Geo-verified)',
          status: 'Present',
        },
        ...records,
      ]);
    } else {
      setClockedIn(false);
      setRecords(
        records.map((r, idx) =>
          idx === 0
            ? {
                ...r,
                clockOut: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                totalHours: '8h 00m',
              }
            : r
        )
      );
    }
  };

  return (
    <div className="space-y-8">
      {/* Interactive Punch Clock Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 sm:p-8 shadow-lg flex flex-col md:flex-row items-center justify-between gap-6">
        <div className="space-y-2 text-center md:text-left">
          <div className="flex items-center justify-center md:justify-start gap-2 text-indigo-600 dark:text-indigo-400 font-bold text-xs uppercase tracking-wider">
            <Clock className="w-4 h-4" />
            <span>Time & Presence Tracker</span>
          </div>
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
            Today's Punch Status:{' '}
            <span className={clockedIn ? 'text-emerald-500' : 'text-slate-400'}>
              {clockedIn ? `Clocked In (${clockTime})` : 'Clocked Out'}
            </span>
          </h3>
          <p className="text-xs text-slate-500">
            Automated IP geo-fencing logs your working hours, break durations, and overtime entries.
          </p>
        </div>

        <button
          onClick={handleToggleClock}
          className={`px-6 py-3.5 font-extrabold text-xs rounded-2xl transition-all shadow-lg flex items-center gap-2 ${
            clockedIn
              ? 'bg-rose-600 hover:bg-rose-500 text-white shadow-rose-600/30'
              : 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-emerald-600/30'
          }`}
        >
          <MapPin className="w-4 h-4" />
          <span>{clockedIn ? 'Clock Out Now' : 'Clock In Now (San Jose HQ)'}</span>
        </button>
      </div>

      {/* Monthly Attendance Summary Metrics */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-sm">
          <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Days Present</span>
          <div className="text-2xl font-black text-slate-900 dark:text-white">21 / 22 Days</div>
          <span className="text-[11px] text-emerald-500 font-semibold block">95.4% Monthly Compliance</span>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-sm">
          <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Hours Logged</span>
          <div className="text-2xl font-black text-slate-900 dark:text-white">168.5 Hrs</div>
          <span className="text-[11px] text-indigo-500 font-semibold block">+12 Hrs Overtime</span>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-sm">
          <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Late Arrivals</span>
          <div className="text-2xl font-black text-amber-500">1 Arrival</div>
          <span className="text-[11px] text-slate-400 font-semibold block">Within 15-min grace window</span>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-sm">
          <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Approved Leave</span>
          <div className="text-2xl font-black text-indigo-500">1 Day</div>
          <span className="text-[11px] text-slate-400 font-semibold block">Paid Vacation Leave</span>
        </div>
      </div>

      {/* Monthly Punch Log Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-lg">
        <div className="border-b border-slate-100 dark:border-slate-800 pb-3">
          <h3 className="text-base font-bold text-slate-900 dark:text-white flex items-center gap-2">
            <Calendar className="w-4 h-4 text-indigo-500" />
            Monthly Attendance Punch Log
          </h3>
          <p className="text-xs text-slate-500">Verifiable clock times recorded via enterprise corporate network</p>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600 dark:text-slate-300">
            <thead className="bg-slate-50 dark:bg-slate-800/60 uppercase font-bold text-slate-400 text-[10px] tracking-wider">
              <tr>
                <th className="py-3.5 px-4">Date</th>
                <th className="py-3.5 px-4">Clock In</th>
                <th className="py-3.5 px-4">Clock Out</th>
                <th className="py-3.5 px-4">Duration</th>
                <th className="py-3.5 px-4">Location / Network</th>
                <th className="py-3.5 px-4">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800 font-medium">
              {records.map((r) => (
                <tr key={r.id} className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors">
                  <td className="py-3.5 px-4 font-bold text-slate-900 dark:text-white">{r.date}</td>
                  <td className="py-3.5 px-4 font-mono text-emerald-600 dark:text-emerald-400">{r.clockIn}</td>
                  <td className="py-3.5 px-4 font-mono text-indigo-600 dark:text-indigo-400">{r.clockOut}</td>
                  <td className="py-3.5 px-4 font-mono font-bold text-slate-800 dark:text-slate-200">{r.totalHours}</td>
                  <td className="py-3.5 px-4 text-slate-500 text-[11px]">{r.location}</td>
                  <td className="py-3.5 px-4">
                    {r.status === 'Present' ? (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-emerald-500/10 text-emerald-500 border border-emerald-500/20">
                        <CheckCircle2 className="w-3 h-3" /> Present
                      </span>
                    ) : r.status === 'Late' ? (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-amber-500/10 text-amber-500 border border-amber-500/20">
                        <AlertCircle className="w-3 h-3" /> Late Arrival
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-indigo-500/10 text-indigo-500 border border-indigo-500/20">
                        On Leave
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
