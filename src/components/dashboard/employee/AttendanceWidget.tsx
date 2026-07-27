import React, { useState, useEffect } from 'react';
import { Clock, MapPin, CheckCircle2, Play, Square, AlertCircle, ShieldCheck, ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export const AttendanceWidget: React.FC = () => {
  const [clockedIn, setClockedIn] = useState(true);
  const [clockInTime, setClockInTime] = useState('09:00 AM');
  const [elapsedSeconds, setElapsedSeconds] = useState(25840); // ~7h 10m
  const [showLocationDetails, setShowLocationDetails] = useState(false);

  useEffect(() => {
    let interval: any = null;
    if (clockedIn) {
      interval = setInterval(() => {
        setElapsedSeconds((prev) => prev + 1);
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [clockedIn]);

  const formatElapsedTime = (totalSeconds: number) => {
    const hrs = Math.floor(totalSeconds / 3600);
    const mins = Math.floor((totalSeconds % 3600) / 60);
    const secs = totalSeconds % 60;
    return `${hrs}h ${mins}m ${secs < 10 ? '0' : ''}${secs}s`;
  };

  const handleToggleClock = () => {
    if (!clockedIn) {
      setClockedIn(true);
      const nowStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      setClockInTime(nowStr);
    } else {
      setClockedIn(false);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg flex flex-col justify-between space-y-5 h-full relative overflow-hidden transition-all hover:border-indigo-500/30">
      {/* Widget Header */}
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
            <Clock className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Attendance & Time Log</h3>
            <p className="text-[11px] text-slate-500">Real-time GPS & Corporate IP Tagged</p>
          </div>
        </div>

        <Link
          to="/attendance"
          className="text-xs font-bold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center gap-1"
        >
          View Log <ChevronRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      {/* Main Punch Clock Display */}
      <div className="p-4 rounded-2xl bg-gradient-to-br from-slate-50 to-indigo-50/30 dark:from-slate-950 dark:to-indigo-950/20 border border-slate-200/80 dark:border-slate-800 space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span
              className={`w-2.5 h-2.5 rounded-full ${
                clockedIn ? 'bg-emerald-500 animate-pulse' : 'bg-slate-400'
              }`}
            />
            <span className="text-xs font-bold text-slate-800 dark:text-slate-200 uppercase tracking-wider">
              {clockedIn ? 'Active Punch In' : 'Clocked Out'}
            </span>
          </div>
          <span className="text-[11px] font-mono text-slate-500 dark:text-slate-400">
            {clockedIn ? `Since ${clockInTime}` : 'Shift Ended'}
          </span>
        </div>

        {/* Counter Display */}
        <div className="text-center py-2">
          <div className="text-3xl sm:text-4xl font-black font-mono tracking-tight text-slate-900 dark:text-white">
            {formatElapsedTime(elapsedSeconds)}
          </div>
          <span className="text-[11px] text-slate-500 font-medium">Shift Target: 8h 00m (40h/week)</span>
        </div>

        {/* Action Punch Button */}
        <button
          onClick={handleToggleClock}
          className={`w-full py-3 rounded-xl font-extrabold text-xs shadow-md transition-all flex items-center justify-center gap-2 ${
            clockedIn
              ? 'bg-rose-600 hover:bg-rose-500 text-white shadow-rose-600/20'
              : 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-emerald-600/20'
          }`}
        >
          {clockedIn ? (
            <>
              <Square className="w-4 h-4 fill-current" />
              <span>Clock Out Now</span>
            </>
          ) : (
            <>
              <Play className="w-4 h-4 fill-current" />
              <span>Clock In (San Jose HQ)</span>
            </>
          )}
        </button>
      </div>

      {/* Location / IP Verification */}
      <div className="space-y-2 text-xs">
        <div className="flex items-center justify-between text-slate-500">
          <span className="flex items-center gap-1.5 font-medium">
            <MapPin className="w-3.5 h-3.5 text-indigo-500" />
            San Jose HQ (192.168.1.45)
          </span>
          <span className="inline-flex items-center gap-1 text-[10px] font-bold text-emerald-500 bg-emerald-500/10 px-2 py-0.5 rounded-full">
            <ShieldCheck className="w-3 h-3" /> Verified
          </span>
        </div>

        {/* Weekly Progress Bar */}
        <div className="space-y-1 pt-1">
          <div className="flex justify-between text-[11px] font-bold">
            <span className="text-slate-400 uppercase tracking-wider text-[10px]">Weekly Hours Completed</span>
            <span className="text-slate-900 dark:text-slate-200 font-mono">35.2 / 40.0 hrs (88%)</span>
          </div>
          <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
            <div className="h-full bg-indigo-600 rounded-full" style={{ width: '88%' }} />
          </div>
        </div>
      </div>
    </div>
  );
};
