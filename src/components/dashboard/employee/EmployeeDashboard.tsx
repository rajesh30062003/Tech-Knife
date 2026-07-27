import React, { useState } from 'react';
import {
  Briefcase,
  ShieldCheck,
  Zap,
  LayoutGrid,
  ListFilter,
  User,
  Clock,
  CheckCircle2,
  Lock,
  Sparkles,
  ArrowRight,
  ShieldAlert,
  Bell,
  RefreshCw,
} from 'lucide-react';
import { useAuth } from '../../../context/AuthContext';
import { AttendanceWidget } from './AttendanceWidget';
import { TodaysTasksWidget } from './TodaysTasksWidget';
import { ProjectProgressWidget } from './ProjectProgressWidget';
import { LeaveBalanceWidget } from './LeaveBalanceWidget';
import { SalaryWidget } from './SalaryWidget';
import { NotificationsWidget } from './NotificationsWidget';
import { MeetingsWidget } from './MeetingsWidget';
import { CalendarWidget } from './CalendarWidget';
import { GithubActivityWidget } from './GithubActivityWidget';
import { Link } from 'react-router-dom';

export const EmployeeDashboard: React.FC = () => {
  const { user } = useAuth();
  const [layoutMode, setLayoutMode] = useState<'grid' | 'focused'>('grid');
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefreshData = () => {
    setIsRefreshing(true);
    setTimeout(() => setIsRefreshing(false), 600);
  };

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-12">
      {/* Top Welcome & Executive Hero Banner */}
      <div className="relative p-6 sm:p-8 rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white shadow-xl overflow-hidden border border-slate-800">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="flex flex-wrap items-center gap-2">
              <span className="px-3 py-1 rounded-full bg-indigo-500/20 text-indigo-300 text-xs font-bold border border-indigo-500/30 flex items-center gap-1">
                <Briefcase className="w-3.5 h-3.5" /> Employee Workspace
              </span>
              <span className="px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-xs font-bold border border-emerald-500/30 flex items-center gap-1">
                <ShieldCheck className="w-3.5 h-3.5" /> ID: {user?.id || 'EMP-2026'}
              </span>
            </div>

            <h1 className="text-2xl sm:text-3xl font-black tracking-tight">
              Welcome back, {user?.firstName} {user?.lastName}!
            </h1>
            <p className="text-xs sm:text-sm text-slate-300 max-w-xl">
              {user?.designation || 'Senior Full Stack Engineer'} • {user?.department || 'Engineering'} Department
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3 shrink-0">
            <button
              onClick={handleRefreshData}
              className="p-2.5 bg-white/10 hover:bg-white/20 text-white rounded-xl border border-white/20 transition-all"
              title="Refresh Dashboard Data"
            >
              <RefreshCw className={`w-4 h-4 ${isRefreshing ? 'animate-spin' : ''}`} />
            </button>

            <div className="bg-white/10 backdrop-blur-md p-1 rounded-xl border border-white/20 flex gap-1">
              <button
                onClick={() => setLayoutMode('grid')}
                className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
                  layoutMode === 'grid' ? 'bg-indigo-600 text-white shadow' : 'text-slate-300 hover:text-white'
                }`}
              >
                <LayoutGrid className="w-3.5 h-3.5" /> All Widgets (9)
              </button>
              <button
                onClick={() => setLayoutMode('focused')}
                className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
                  layoutMode === 'focused' ? 'bg-indigo-600 text-white shadow' : 'text-slate-300 hover:text-white'
                }`}
              >
                <Zap className="w-3.5 h-3.5" /> Sprint Focus
              </button>
            </div>

            <Link
              to="/profile"
              className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs rounded-xl shadow transition-all flex items-center gap-1.5"
            >
              <User className="w-4 h-4" /> Account Profile
            </Link>
          </div>
        </div>
      </div>

      {/* RBAC Boundary Enforced Banner */}
      <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs flex items-center justify-between gap-4">
        <div className="flex items-center gap-3 text-xs">
          <div className="p-2 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 rounded-xl shrink-0">
            <ShieldAlert className="w-4 h-4" />
          </div>
          <div className="text-slate-600 dark:text-slate-400">
            <strong className="text-slate-900 dark:text-white">Role Boundaries Active: </strong>
            You are logged in as <span className="font-mono font-bold text-indigo-600 dark:text-indigo-400">ROLE_EMPLOYEE</span>. You can manage tasks, log punch times, track salary slips, request leave, and sync git commits.
          </div>
        </div>

        <Link
          to="/change-password"
          className="text-xs font-bold text-slate-500 hover:text-indigo-600 dark:hover:text-indigo-400 whitespace-nowrap flex items-center gap-1 shrink-0"
        >
          <Lock className="w-3.5 h-3.5" /> Security Credentials
        </Link>
      </div>

      {/* 9 Widgets Layout Grid */}
      {layoutMode === 'grid' ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* Row 1: Attendance, Today's Task, Project Progress */}
          <div className="h-[460px]">
            <AttendanceWidget />
          </div>
          <div className="h-[460px]">
            <TodaysTasksWidget />
          </div>
          <div className="h-[460px]">
            <ProjectProgressWidget />
          </div>

          {/* Row 2: Leave Balance, Salary, Notifications */}
          <div className="h-[460px]">
            <LeaveBalanceWidget />
          </div>
          <div className="h-[460px]">
            <SalaryWidget />
          </div>
          <div className="h-[460px]">
            <NotificationsWidget />
          </div>

          {/* Row 3: Meetings, Calendar, GitHub Activity */}
          <div className="h-[460px]">
            <MeetingsWidget />
          </div>
          <div className="h-[460px]">
            <CalendarWidget />
          </div>
          <div className="h-[460px]">
            <GithubActivityWidget />
          </div>
        </div>
      ) : (
        /* Focused Mode View */
        <div className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 space-y-6">
              <div className="h-[460px]">
                <TodaysTasksWidget />
              </div>
              <div className="h-[460px]">
                <GithubActivityWidget />
              </div>
            </div>

            <div className="space-y-6">
              <div className="h-[460px]">
                <AttendanceWidget />
              </div>
              <div className="h-[460px]">
                <MeetingsWidget />
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
