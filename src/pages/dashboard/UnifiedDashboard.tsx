import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { 
  Users, FolderKanban, DollarSign, Clock, CalendarDays, 
  TrendingUp, ShieldCheck, ArrowUpRight, Activity, 
  AlertCircle, CheckCircle2, ChevronRight, Briefcase, Plus, 
  Bell, Calendar as CalendarIcon, UserPlus, Target, FileText, Zap, ChevronLeft,
  Cpu, Layers, Sparkles
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { StatusBadge } from '../../components/common/StatusBadge';

export const UnifiedDashboard: React.FC = () => {
  const { user } = useAuth();
  const [activeChartTab, setActiveChartTab] = useState<'sprint' | 'headcount' | 'revenue'>('sprint');
  const [selectedDate, setSelectedDate] = useState(23);

  const activities = [
    { time: '10:42 AM', title: 'MongoDB Atlas System Sync Completed', user: 'Ganesh Pal (Sr. Developer)', tag: 'DevOps', type: 'success' },
    { time: '09:15 AM', title: 'New Customer Account Provisioned (Apex Corp)', user: 'Ranadhir Pal (CEO)', tag: 'CRM', type: 'info' },
    { time: 'Yesterday', title: 'October Payroll Disbursement Approved ($384.5k)', user: 'Sourav Roy (MD)', tag: 'Finance', type: 'success' },
    { time: '2 days ago', title: 'System Security Audit Completed with 0 Vulnerabilities', user: 'Security Governance', tag: 'Security', type: 'warning' }
  ];

  const quickActions = [
    { label: 'Add Employee', icon: UserPlus, path: '/employees', color: 'bg-slate-900 text-cyan-400 dark:bg-slate-800 dark:text-cyan-300' },
    { label: 'New Project', icon: FolderKanban, path: '/projects', color: 'bg-slate-900 text-cyan-400 dark:bg-slate-800 dark:text-cyan-300' },
    { label: 'Approve Payroll', icon: DollarSign, path: '/payroll', color: 'bg-slate-900 text-cyan-400 dark:bg-slate-800 dark:text-cyan-300' },
    { label: 'Clock In GPS', icon: Clock, path: '/attendance', color: 'bg-slate-900 text-cyan-400 dark:bg-slate-800 dark:text-cyan-300' },
    { label: 'Add CRM Deal', icon: Target, path: '/crm', color: 'bg-slate-900 text-cyan-400 dark:bg-slate-800 dark:text-cyan-300' },
    { label: 'Corporate Calendar', icon: CalendarIcon, path: '/calendar', color: 'bg-slate-900 text-cyan-400 dark:bg-slate-800 dark:text-cyan-300' }
  ];

  return (
    <div className="space-y-8">
      
      {/* TCS-Inspired Enterprise Welcome Hero Banner */}
      <div className="relative p-6 sm:p-8 rounded-3xl bg-gradient-to-r from-slate-950 via-slate-900 to-slate-950 text-white shadow-xl overflow-hidden border border-slate-800/80">
        {/* Subtle Background Geometric Glow */}
        <div className="absolute -top-24 -right-24 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute -bottom-24 -left-24 w-96 h-96 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />

        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2.5">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-cyan-950/80 text-cyan-400 text-xs font-extrabold border border-cyan-500/30">
              <Cpu className="w-3.5 h-3.5 text-cyan-400" />
              <span>Tech Knife: Infrastructure to Intelligence</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
              Welcome back, {user?.firstName} {user?.lastName}!
            </h1>
            <p className="text-xs sm:text-sm text-slate-300 max-w-xl flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-emerald-400" />
              <span>{user?.designation || 'Enterprise Specialist'} • {user?.department || 'Management'} Department</span>
            </p>
          </div>

          <div className="flex flex-wrap gap-3">
            <Link
              to="/projects"
              className="px-5 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 text-xs font-extrabold rounded-xl transition-all shadow-md flex items-center gap-2"
            >
              <span>View Enterprise Projects</span>
              <ArrowUpRight className="w-4 h-4" />
            </Link>
            <Link
              to="/attendance"
              className="px-4 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-bold rounded-xl transition-all border border-slate-700"
            >
              Clock In Today
            </Link>
          </div>
        </div>
      </div>

      {/* Quick Actions Shortcuts */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 space-y-3 shadow-xs">
        <div className="flex items-center gap-2 text-xs font-bold text-slate-400 uppercase tracking-wider">
          <Zap className="w-3.5 h-3.5 text-cyan-500" />
          <span>Enterprise Quick Action Shortcuts</span>
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
          {quickActions.map((act, i) => (
            <Link
              key={i}
              to={act.path}
              className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50 hover:bg-slate-100 dark:hover:bg-slate-800 border border-slate-200/60 dark:border-slate-800 flex items-center gap-2.5 transition-all group shadow-2xs hover:border-cyan-500/50"
            >
              <div className={`p-2 rounded-lg ${act.color}`}>
                <act.icon className="w-4 h-4" />
              </div>
              <span className="text-xs font-bold text-slate-800 dark:text-slate-200 group-hover:text-cyan-600 dark:group-hover:text-cyan-400 truncate">
                {act.label}
              </span>
            </Link>
          ))}
        </div>
      </div>

      {/* Enterprise KPI Analytics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs space-y-3 hover:border-cyan-500/40 transition-colors">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Active Deliverables</span>
            <div className="p-2 bg-slate-100 dark:bg-slate-800 text-cyan-600 dark:text-cyan-400 rounded-xl">
              <FolderKanban className="w-5 h-5" />
            </div>
          </div>
          <div>
            <div className="text-2xl font-extrabold text-slate-900 dark:text-white">12 Sprints</div>
            <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-bold mt-1 flex items-center gap-1">
              <TrendingUp className="w-3.5 h-3.5" /> 94.2% Sprint Completion Target
            </p>
          </div>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs space-y-3 hover:border-cyan-500/40 transition-colors">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Engineering Capacity</span>
            <div className="p-2 bg-slate-100 dark:bg-slate-800 text-cyan-600 dark:text-cyan-400 rounded-xl">
              <Users className="w-5 h-5" />
            </div>
          </div>
          <div>
            <div className="text-2xl font-extrabold text-slate-900 dark:text-white">148 Engineers</div>
            <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-1 font-medium">
              96.4% Active Attendance Today
            </p>
          </div>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs space-y-3 hover:border-cyan-500/40 transition-colors">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Monthly Payroll</span>
            <div className="p-2 bg-slate-100 dark:bg-slate-800 text-cyan-600 dark:text-cyan-400 rounded-xl">
              <DollarSign className="w-5 h-5" />
            </div>
          </div>
          <div>
            <div className="text-2xl font-extrabold text-slate-900 dark:text-white">$384,500</div>
            <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-bold mt-1">
              Disbursed for October 2026
            </p>
          </div>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs space-y-3 hover:border-cyan-500/40 transition-colors">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Client SLA Target</span>
            <div className="p-2 bg-slate-100 dark:bg-slate-800 text-cyan-600 dark:text-cyan-400 rounded-xl">
              <Activity className="w-5 h-5" />
            </div>
          </div>
          <div>
            <div className="text-2xl font-extrabold text-slate-900 dark:text-white">4 Open Tickets</div>
            <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-bold mt-1">
              99.8% SLA Target Met
            </p>
          </div>
        </div>
      </div>

      {/* Visual Performance Chart Widget */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-6 shadow-xs">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white flex items-center gap-2">
              <Layers className="w-5 h-5 text-cyan-500" />
              Executive Performance Analytics Chart
            </h3>
            <p className="text-xs text-slate-500">Live operational metrics and comparative delivery velocity</p>
          </div>

          <div className="flex items-center gap-1 bg-slate-100 dark:bg-slate-800 p-1 rounded-xl">
            <button
              onClick={() => setActiveChartTab('sprint')}
              className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-all ${
                activeChartTab === 'sprint' ? 'bg-slate-900 text-cyan-400 shadow-xs' : 'text-slate-600 dark:text-slate-400'
              }`}
            >
              Sprint Velocity
            </button>
            <button
              onClick={() => setActiveChartTab('headcount')}
              className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-all ${
                activeChartTab === 'headcount' ? 'bg-slate-900 text-cyan-400 shadow-xs' : 'text-slate-600 dark:text-slate-400'
              }`}
            >
              Headcount
            </button>
            <button
              onClick={() => setActiveChartTab('revenue')}
              className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-all ${
                activeChartTab === 'revenue' ? 'bg-slate-900 text-cyan-400 shadow-xs' : 'text-slate-600 dark:text-slate-400'
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
                <span className="text-cyan-600 dark:text-cyan-400 font-bold">Total: 480 Points</span>
              </div>
              <div className="grid grid-cols-6 gap-2 sm:gap-4 items-end h-40 pt-4 border-b border-slate-100 dark:border-slate-800">
                {[
                  { month: 'May', val: 65, color: 'bg-slate-700' },
                  { month: 'Jun', val: 78, color: 'bg-slate-800' },
                  { month: 'Jul', val: 82, color: 'bg-slate-800' },
                  { month: 'Aug', val: 90, color: 'bg-slate-900 dark:bg-slate-700' },
                  { month: 'Sep', val: 94, color: 'bg-slate-900 dark:bg-slate-700' },
                  { month: 'Oct', val: 98, color: 'bg-cyan-500' },
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
              <div className="space-y-2.5">
                {[
                  { dept: 'Engineering & DevOps', count: 68, pct: 75, color: 'bg-slate-900 dark:bg-slate-700' },
                  { dept: 'Product & Design', count: 32, pct: 50, color: 'bg-cyan-600' },
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
                  { q: 'Q2 2026', val: '$1.45M', pct: 75, color: 'bg-slate-700' },
                  { q: 'Q3 2026', val: '$1.68M', pct: 88, color: 'bg-slate-900 dark:bg-slate-700' },
                  { q: 'Q4 2026 (Est)', val: '$1.85M', pct: 98, color: 'bg-cyan-500' },
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
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4 shadow-xs">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
              <div>
                <h3 className="font-extrabold text-base text-slate-900 dark:text-white">
                  Recent Activities & Audit Feed
                </h3>
                <p className="text-xs text-slate-500">Live operational events and audit log stream</p>
              </div>
              <Link to="/admin" className="text-xs font-bold text-cyan-600 dark:text-cyan-400 hover:underline flex items-center gap-1">
                Full Audit Logs <ChevronRight className="w-3.5 h-3.5" />
              </Link>
            </div>

            <div className="space-y-3">
              {activities.map((act, idx) => (
                <div key={idx} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-start gap-3">
                  <div className="p-2 rounded-lg bg-slate-900 text-cyan-400 dark:bg-slate-800 mt-0.5">
                    <Activity className="w-4 h-4" />
                  </div>
                  <div className="flex-1 space-y-1">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{act.title}</span>
                      <span className="text-[10px] text-slate-400 font-mono">{act.time}</span>
                    </div>
                    <p className="text-[11px] text-slate-500">
                      Triggered by: <strong className="text-slate-700 dark:text-slate-300">{act.user}</strong> • <span className="text-cyan-600 font-semibold">{act.tag}</span>
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
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4 shadow-xs">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <CalendarIcon className="w-4 h-4 text-cyan-500" />
                <h3 className="font-extrabold text-sm text-slate-900 dark:text-white">October 2026</h3>
              </div>
              <Link to="/calendar" className="text-xs text-cyan-600 dark:text-cyan-400 font-bold hover:underline">
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
                      ? 'bg-slate-900 text-cyan-400 shadow-xs font-bold'
                      : day === 24 || day === 28
                      ? 'bg-amber-100 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300'
                      : 'text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800'
                  }`}
                >
                  {day}
                </button>
              ))}
            </div>

            <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-xl border border-slate-200 dark:border-slate-700 text-xs">
              <span className="font-extrabold text-slate-900 dark:text-white block">Oct {selectedDate} Milestone:</span>
              <span className="text-slate-600 dark:text-slate-400 text-[11px]">Sprint 14 Code Freeze & Security Review</span>
            </div>
          </div>

          {/* Notifications Widget */}
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4 shadow-xs">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <Bell className="w-4 h-4 text-rose-500" />
                <h3 className="font-extrabold text-sm text-slate-900 dark:text-white">Active Alerts</h3>
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
