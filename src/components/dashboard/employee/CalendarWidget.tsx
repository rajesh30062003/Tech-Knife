import React, { useState } from 'react';
import { Calendar as CalendarIcon, ChevronLeft, ChevronRight, Clock, Star, MapPin } from 'lucide-react';
import { Link } from 'react-router-dom';

interface DayEvent {
  date: number;
  month: string;
  events: { title: string; time: string; type: 'meeting' | 'deadline' | 'holiday' | 'leave' }[];
}

const EVENTS_MAP: Record<number, DayEvent['events']> = {
  23: [
    { title: 'Daily Engineering Standup', time: '10:00 AM', type: 'meeting' },
    { title: 'Sprint PR Reviews Due', time: '05:00 PM', type: 'deadline' },
  ],
  24: [
    { title: 'Architecture Sync with CTO', time: '02:00 PM', type: 'meeting' },
  ],
  28: [
    { title: 'Enterprise Portal V2 Release Candidate', time: '06:00 PM', type: 'deadline' },
  ],
  31: [
    { title: 'Corporate Halloween Hackathon', time: 'All Day', type: 'holiday' },
  ],
};

export const CalendarWidget: React.FC = () => {
  const [selectedDay, setSelectedDay] = useState(23);
  const daysInMonth = Array.from({ length: 31 }, (_, i) => i + 1);

  const getEventTypeBadge = (type: string) => {
    switch (type) {
      case 'meeting':
        return 'bg-cyan-500/10 text-cyan-500 border-cyan-500/20';
      case 'deadline':
        return 'bg-rose-500/10 text-rose-500 border-rose-500/20';
      case 'holiday':
        return 'bg-amber-500/10 text-amber-500 border-amber-500/20';
      default:
        return 'bg-indigo-500/10 text-indigo-500 border-indigo-500/20';
    }
  };

  const selectedEvents = EVENTS_MAP[selectedDay] || [];

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-4 flex flex-col justify-between h-full transition-all hover:border-indigo-500/30">
      {/* Widget Header */}
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
            <CalendarIcon className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Corporate Calendar</h3>
            <p className="text-[11px] text-slate-500">October 2026 Schedule & Milestones</p>
          </div>
        </div>

        <Link
          to="/calendar"
          className="text-xs font-bold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center gap-1"
        >
          Full View <ChevronRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      {/* Days Grid */}
      <div className="space-y-2">
        <div className="grid grid-cols-7 text-center text-[10px] font-bold text-slate-400 uppercase tracking-wider">
          <span>Su</span>
          <span>Mo</span>
          <span>Tu</span>
          <span>We</span>
          <span>Th</span>
          <span>Fr</span>
          <span>Sa</span>
        </div>

        <div className="grid grid-cols-7 gap-1 text-center text-xs font-semibold">
          {daysInMonth.map((day) => {
            const hasEvent = !!EVENTS_MAP[day];
            const isSelected = selectedDay === day;

            return (
              <button
                key={day}
                onClick={() => setSelectedDay(day)}
                className={`py-2 rounded-xl transition-all relative font-mono text-[11px] ${
                  isSelected
                    ? 'bg-indigo-600 text-white font-bold shadow-md scale-105'
                    : 'text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800'
                }`}
              >
                <span>{day}</span>
                {hasEvent && !isSelected && (
                  <span className="absolute bottom-1 left-1/2 -translate-x-1/2 w-1 h-1 rounded-full bg-indigo-500" />
                )}
              </button>
            );
          })}
        </div>
      </div>

      {/* Selected Day Agenda Detail */}
      <div className="p-3 bg-slate-50 dark:bg-slate-950/60 rounded-2xl border border-slate-200/80 dark:border-slate-800 space-y-2">
        <div className="flex justify-between items-center text-xs font-bold border-b border-slate-200/60 dark:border-slate-800 pb-1.5">
          <span className="text-slate-900 dark:text-white">Oct {selectedDay}, 2026 Agenda</span>
          <span className="text-[10px] text-slate-400 font-mono">{selectedEvents.length} Event(s)</span>
        </div>

        {selectedEvents.length === 0 ? (
          <p className="text-[11px] text-slate-400 text-center py-2">No special milestones on this date.</p>
        ) : (
          <div className="space-y-1.5">
            {selectedEvents.map((ev, idx) => (
              <div key={idx} className="flex items-center justify-between text-[11px]">
                <div className="flex items-center gap-1.5 truncate">
                  <Clock className="w-3 h-3 text-indigo-500 shrink-0" />
                  <span className="font-semibold text-slate-800 dark:text-slate-200 truncate">{ev.title}</span>
                </div>
                <span className={`px-2 py-0.5 text-[9px] font-bold rounded-full border shrink-0 ${getEventTypeBadge(ev.type)}`}>
                  {ev.time}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
