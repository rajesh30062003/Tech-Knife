import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { 
  Users, FolderKanban, DollarSign, Clock, CalendarDays, 
  TrendingUp, ShieldCheck, ArrowUpRight, Activity, 
  AlertCircle, CheckCircle2, ChevronRight, Briefcase, Plus, 
  Bell, Calendar as CalendarIcon, UserPlus, Target, FileText, Zap, ChevronLeft
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { StatusBadge } from '../../components/common/StatusBadge';

export const UnifiedDashboard: React.FC = () => {
  const { user } = useAuth();
  const [activeChartTab, setActiveChartTab] = useState<'sprint' | 'headcount' | 'revenue'>('sprint');
  const [selectedDate, setSelectedDate] = useState(23);

  const activities = [
    { time: '10:42 AM', title: 'Spring Security 6 Upgrade Completed', user: 'Sarah Connor (CTO)', tag: 'DevOps', type: 'success' },
    { time: '09:15 AM', title: 'New Customer Account Provisioned (Apex Corp)', user: 'Alexander Vance', tag: 'CRM', type: 'info' },
    { time: 'Yesterday', title: 'October Payroll Disbursement Approved ($384.5k)', user: 'Managing Director', tag: 'Finance', type: 'success' },
    { time: '2 days ago', title: 'System Security Audit Completed with 0 Vulnerabilities', user: 'AOP Guard Aspect', tag: 'Security', type: 'warning' }
  ];

  const quickActions = [
    { label: 'Add Employee', icon: UserPlus, path: '/employees', color: 'bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400' },
    { label: 'New Project', icon: FolderKanban, path: '/projects', color: 'bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400' },
    { label: 'Approve Payroll', icon: DollarSign, path: '/payroll', color: 'bg-amber-50 dark:bg-amber-950/60 text-amber-600 dark:text-amber-400' },
    { label: 'Clock In GPS', icon: Clock, path: '/attendance', color: 'bg-cyan-50 dark:bg-cyan-950/60 text-cyan-600 dark:text-cyan-400' },
    { label: 'Add CRM Deal', icon: Target, path: '/crm', color: 'bg-purple-50 dark:bg-purple-950/60 text-purple-600 dark:text-purple-400' },
    { label: 'Corporate Calendar', icon: CalendarIcon, path: '/calendar', color: 'bg-rose-50 dark:bg-rose-950/60 text-rose-600 dark:text-rose-400' }
  ];

  return (
    <div className="space-y-8">
      
      {/* Welcome Hero Banner */}
      <div className="relative p-6 sm:p-8 rounded-3xl bg-gradient-to-r from-indigo-900 via-indigo-950 to-slate-900 text-white shadow-xl overflow-hidden border border-indigo-800/40">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-500/20 text-indigo-300 text-xs font-semibold border border-indigo-400/20">
              <ShieldCheck className="w-3.5 h-3.5" />
              <span>Role: {user?.role.replace('ROLE_', '')}</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
              Welcome back, {user?.firstName} {user?.lastName}!
            </h1>
            <p className="text-xs sm:text-sm text-indigo-200/80 max-w-xl">
              {user?.designation} • {user?.department} Department
            </p>
          </div>

          <div className="flex flex-wrap gap-3">
            <Link
              to="/projects"
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold rounded-xl transition-all shadow-md"
            >
              View Active Sprints
            </Link>
            <Link
              to="/attendance"
              className="px-4 py-2 bg-white/10 hover:bg-white/20 text-white text-xs font-semibold rounded-xl transition-all border border-white/20"
            >
              Clock In Today
            </Link>
          </div>
        </div>
      </div>

      {/* Quick Actions Widget Bar */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 space-y-3">
        <div className="flex items-center gap-2 text-xs font-bold text-slate-400 uppercase tracking-wider">
          <Zap className="w-3.5 h-3.5 text-amber-500" />
          <span>Quick Actions Shortcuts</span>
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
          {quickActions.map((act, i) => (
            <Link
              key={i}
              to={act.path}
              className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50 hover:bg-indigo-50 dark:hover:bg-indigo-950/40 border border-slate-200/60 dark:border-slate-800 flex items-center gap-2.5 transition-all group"
            >
              <div className={`p-2 rounded-lg ${act.color}`}>
                <act.icon className="w-4 h-4" />
              </div>
              <span className="text-xs font-bold text-slate-800 dark:text-slate-200 group-hover:text-indigo-600 dark:group-hover:text-indigo-400 truncate">
                {act.label}
              </span>
            </Link>
          ))}
        </div>
      </div>

      {/* Analytics Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs space-y-3">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Active Deliverables</span>
            <div className="p-2 bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400 rounded-xl">
              <FolderKanban className="w-5 h-5" />
            </div>
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-900 dark:text-white">12 Sprints</div>
            <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-medium mt-1">
              ↑ 94.2% Sprint Completion Target
            </p>
          </div>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs space-y-3">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Engineering Capacity</span>
            <div className="p-2 bg-emerald-50 dark:bg-emerald-950/50 text-emerald-600 dark:text-emerald-400 rounded-xl">
              <Users className="w-5 h-5" />
            </div>
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-900 dark:text-white">148 Engineers</div>
            <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-1">
              96.4% Attendance Rate Today
            </p>
          </div>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs space-y-3">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Monthly Payroll</span>
            <div className="p-2 bg-amber-50 dark:bg-amber-950/50 text-amber-600 dark:text-amber-400 rounded-xl">
              <DollarSign className="w-5 h-5" />
            </div>
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-900 dark:text-white">$384,500</div>
            <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-medium mt-1">
              Disbursed for October 2026
            </p>
          </div>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs space-y-3">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Client Tickets SLA</span>
            <div className="p-2 bg-purple-50 dark:bg-purple-950/50 text-purple-600 dark:text-purple-400 rounded-xl">
              <Activity className="w-5 h-5" />
            </div>
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-900 dark:text-white">4 Open Tickets</div>
            <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-medium mt-1">
              99.8% SLA Target Met
            </p>
          </div>
        </div>
      </div>

      {/* Visual Analytics Chart Widget */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <h3 className="font-bold text-base text-slate-900 dark:text-white">Executive Performance Analytics Chart</h3>
            <p className="text-xs text-slate-500">Live operational metrics and comparative velocity</p>
          </div>

          <div className="flex items-center gap-1 bg-slate-100 dark:bg-slate-800 p-1 rounded-xl">
            <button
              onClick={() => setActiveChartTab('sprint')}
              className={`px-3 py-1 text-xs font-semibold rounded-lg transition-all ${
                activeChartTab === 'sprint' ? 'bg-white dark:bg-slate-900 text-indigo-600 dark:text-indigo-400 shadow-xs' : 'text-slate-600 dark:text-slate-400'
              }`}
            >
              Sprint Velocity
            </button>
            <button
              onClick={() => setActiveChartTab('headcount')}
              className={`px-3 py-1 text-xs font-semibold rounded-lg transition-all ${
                activeChartTab === 'headcount' ? 'bg-white dark:bg-slate-900 text-indigo-600 dark:text-indigo-400 shadow-xs' : 'text-slate-600 dark:text-slate-400'
              }`}
            >
              Headcount
            </button>
            <button
              onClick={() => setActiveChartTab('revenue')}
              className={`px-3 py-1 text-xs font-semibold rounded-lg transition-all ${
                activeChartTab === 'revenue' ? 'bg-white dark:bg-slate-900 text-indigo-600 dark:text-indigo-400 shadow-xs' : 'text-slate-600 dark:text-slate-400'
              }`}
            >
              ARR Growth
            </button>
          </div>
        </div>

        {/* Custom Visual SVG/Bar Chart */}
        <div className="space-y-4">
          {activeChartTab === 'sprint' && (
            <div className="space-y-3">
              <div className="text-xs font-semibold text-slate-500 flex justify-between">
                <span>Monthly Sprint Story Points Delivered</span>
                <span className="text-indigo-600 dark:text-indigo-400 font-bold">Total: 480 Points</span>
              </div>
              <div className="grid grid-cols-6 gap-2 sm:gap-4 items-end h-40 pt-4 border-b border-slate-100 dark:border-slate-800">
                {[
                  { month: 'May', val: 65, color: 'bg-indigo-400' },
                  { month: 'Jun', val: 78, color: 'bg-indigo-500' },
                  { month: 'Jul', val: 82, color: 'bg-indigo-500' },
                  { month: 'Aug', val: 90, color: 'bg-indigo-600' },
                  { month: 'Sep', val: 94, color: 'bg-indigo-600' },
                  { month: 'Oct', val: 98, color: 'bg-emerald-500' },
                ].map((bar, i) => (
                  <div key={i} className="flex flex-col items-center gap-2 h-full justify-end group">
                    <span className="text-[10px] font-bold text-slate-600 dark:text-slate-300 group-hover:scale-110 transition-transform">{bar.val}%</span>
                    <div className={`w-full rounded-t-lg transition-all group-hover:brightness-110 ${bar.color}`} style={{ height: `${bar.val}%` }}></div>
                    <span className="text-[11px] font-semibold text-slate-400">{bar.month}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeChartTab === 'headcount' && (
            <div className="space-y-3">
              <div className="text-xs font-semibold text-slate-500 flex justify-between">
                <span>Department Headcount Allocation</span>
                <span className="text-emerald-600 dark:text-emerald-400 font-bold">Total: 148 Staff</span>
              </div>
              <div className="space-y-2">
                {[
                  { dept: 'Engineering & DevOps', count: 68, pct: 75, color: 'bg-indigo-600' },
                  { dept: 'Product & Design', count: 32, pct: 50, color: 'bg-purple-600' },
                  { dept: 'Client Growth & CRM', count: 28, pct: 40, color: 'bg-emerald-600' },
                  { dept: 'Executive & Admin', count: 20, pct: 28, color: 'bg-amber-600' },
                ].map((item, i) => (
                  <div key={i} className="space-y-1">
                    <div className="flex justify-between text-xs font-semibold">
                      <span className="text-slate-800 dark:text-slate-200">{item.dept}</span>
                      <span className="text-slate-500">{item.count} Engineers</span>
                    </div>
                    <div className="w-full h-2.5 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                      <div className={`h-full rounded-full ${item.color}`} style={{ width: `${item.pct}%` }}></div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeChartTab === 'revenue' && (
            <div className="space-y-3">
              <div className="text-xs font-semibold text-slate-500 flex justify-between">
                <span>Enterprise ARR Quarterly Progression</span>
                <span className="text-emerald-600 dark:text-emerald-400 font-bold">$1.85M ARR Target</span>
              </div>
              <div className="grid grid-cols-4 gap-4 items-end h-36 pt-4 border-b border-slate-100 dark:border-slate-800">
                {[
                  { q: 'Q1 2026', val: '$1.2M', pct: 60, color: 'bg-slate-400' },
                  { q: 'Q2 2026', val: '$1.45M', pct: 75, color: 'bg-indigo-500' },
                  { q: 'Q3 2026', val: '$1.68M', pct: 88, color: 'bg-indigo-600' },
                  { q: 'Q4 2026 (Est)', val: '$1.85M', pct: 98, color: 'bg-emerald-500' },
                ].map((q, i) => (
                  <div key={i} className="flex flex-col items-center gap-2 h-full justify-end">
                    <span className="text-[11px] font-bold text-slate-700 dark:text-slate-200">{q.val}</span>
                    <div className={`w-full rounded-t-lg transition-all ${q.color}`} style={{ height: `${q.pct}%` }}></div>
                    <span className="text-[11px] font-semibold text-slate-400">{q.q}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Grid: Activities, Calendar & Notifications */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Recent Activities */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
              <div>
                <h3 className="font-bold text-base text-slate-900 dark:text-white">
                  Recent Activities & Audit Feed
                </h3>
                <p className="text-xs text-slate-500">Live operational events and audit log stream</p>
              </div>
              <Link to="/admin" className="text-xs font-semibold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center gap-1">
                Full Audit Logs <ChevronRight className="w-3.5 h-3.5" />
              </Link>
            </div>

            <div className="space-y-3">
              {activities.map((act, idx) => (
                <div key={idx} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-start gap-3">
                  <div className="p-2 rounded-lg bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 mt-0.5">
                    <Activity className="w-4 h-4" />
                  </div>
                  <div className="flex-1 space-y-1">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{act.title}</span>
                      <span className="text-[10px] text-slate-400 font-mono">{act.time}</span>
                    </div>
                    <p className="text-[11px] text-slate-500">
                      Triggered by: <strong className="text-slate-700 dark:text-slate-300">{act.user}</strong> • <span className="text-indigo-600 font-semibold">{act.tag}</span>
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Interactive Calendar & Notifications Widget */}
        <div className="space-y-6">
          
          {/* Mini Calendar Widget */}
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <CalendarIcon className="w-4 h-4 text-indigo-500" />
                <h3 className="font-bold text-sm text-slate-900 dark:text-white">October 2026</h3>
              </div>
              <Link to="/calendar" className="text-xs text-indigo-600 dark:text-indigo-400 font-semibold hover:underline">
                View All
              </Link>
            </div>

            {/* Mini Calendar Grid */}
            <div className="grid grid-cols-7 gap-1 text-center text-[10px] font-bold text-slate-400">
              {['S', 'M', 'T', 'W', 'T', 'F', 'S'].map((d, i) => (
                <div key={i} className="py-1">{d}</div>
              ))}
              {Array.from({ length: 31 }, (_, i) => i + 1).slice(0, 28).map((day) => (
                <button
                  key={day}
                  onClick={() => setSelectedDate(day)}
                  className={`py-1.5 rounded-lg text-xs font-semibold transition-all ${
                    day === selectedDate
                      ? 'bg-indigo-600 text-white shadow-xs font-bold'
                      : day === 24 || day === 28
                      ? 'bg-amber-100 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300'
                      : 'text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800'
                  }`}
                >
                  {day}
                </button>
              ))}
            </div>

            <div className="p-3 bg-indigo-50 dark:bg-indigo-950/40 rounded-xl border border-indigo-200/50 dark:border-indigo-800/50 text-xs">
              <span className="font-bold text-indigo-700 dark:text-indigo-300 block">Oct {selectedDate} Milestone:</span>
              <span className="text-slate-600 dark:text-slate-400 text-[11px]">Sprint 14 Code Freeze & Security Review</span>
            </div>
          </div>

          {/* Notifications Widget */}
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <Bell className="w-4 h-4 text-rose-500" />
                <h3 className="font-bold text-sm text-slate-900 dark:text-white">Active Alerts</h3>
              </div>
              <span className="px-2 py-0.5 text-[10px] font-bold bg-rose-100 dark:bg-rose-950 text-rose-600 rounded-full">
                3 New
              </span>
            </div>

            <div className="space-y-2.5 text-xs">
              <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-start gap-2.5">
                <AlertCircle className="w-4 h-4 text-amber-500 shrink-0 mt-0.5" />
                <div>
                  <span className="font-bold text-slate-900 dark:text-slate-100 block">Leave Approval Pending</span>
                  <span className="text-slate-500 text-[11px]">Elena Rostova requested 3 days annual leave.</span>
                </div>
              </div>

              <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-start gap-2.5">
                <CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0 mt-0.5" />
                <div>
                  <span className="font-bold text-slate-900 dark:text-slate-100 block">Payroll Disbursed</span>
                  <span className="text-slate-500 text-[11px]">Direct deposits authorized for October.</span>
                </div>
              </div>
            </div>
          </div>

        </div>

      </div>

    </div>
  );
};
