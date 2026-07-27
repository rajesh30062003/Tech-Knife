import React from 'react';
import { Calendar, Plus, Clock, Users, MapPin } from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';

export const CalendarPage: React.FC = () => {
  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Calendar className="w-4 h-4" />
            <span>Enterprise Room & Meeting Scheduler</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Calendar & Conference Booking</h1>
          <p className="text-xs text-slate-500">Book meeting rooms, schedule engineering sprints, and track release deadlines</p>
        </div>

        <button className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md">
          <Plus className="w-3.5 h-3.5" /> Book Conference Room
        </button>
      </div>

      {/* Upcoming Events */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <h3 className="font-bold text-base text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
          Scheduled Meetings & Sprint Reviews
        </h3>

        <div className="space-y-3">
          {[
            { title: 'Weekly Engineering Sprint Retrospective', time: 'Today, 2:00 PM - 3:00 PM', room: 'Conference Room 4B (San Jose)', organizer: 'Marcus Brody' },
            { title: 'Quarterly Financial & Payroll Review', time: 'Tomorrow, 10:00 AM - 11:30 AM', room: 'Executive Boardroom A', organizer: 'Alexander Vance' },
            { title: 'Apex Enterprises Client Sync', time: 'Oct 18, 4:00 PM - 4:45 PM', room: 'Virtual Meet #12', organizer: 'David Miller' },
          ].map((e, idx) => (
            <div key={idx} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between gap-4">
              <div className="space-y-1">
                <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{e.title}</span>
                <p className="text-xs text-indigo-600 dark:text-indigo-400 font-medium flex items-center gap-2">
                  <Clock className="w-3.5 h-3.5" /> {e.time}
                </p>
                <p className="text-[11px] text-slate-500 flex items-center gap-2">
                  <MapPin className="w-3.5 h-3.5 text-slate-400" /> {e.room} • Organizer: {e.organizer}
                </p>
              </div>

              <span className="px-2.5 py-1 text-[10px] font-bold rounded bg-indigo-100 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300">
                Confirmed
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
