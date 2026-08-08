import React, { useState } from 'react';
import {
  UserCheck,
  Mail,
  Building2,
  Briefcase,
  CheckCircle2,
  AlertCircle,
  Camera,
  Lock,
  Clock,
  User,
  DollarSign,
  CalendarDays,
  FolderKanban,
  Bell,
  Key,
} from 'lucide-react';
import { GraduationCap } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { ChangePassword } from '../auth/ChangePassword';
import { ProfileOverviewTab } from '../../components/profile/ProfileOverviewTab';
import { AvatarUploadTab } from '../../components/profile/AvatarUploadTab';
import { SalaryHistoryTab } from '../../components/profile/SalaryHistoryTab';
import { AttendanceTab } from '../../components/profile/AttendanceTab';
import { LeaveTab } from '../../components/profile/LeaveTab';
import { AssignedProjectsTab } from '../../components/profile/AssignedProjectsTab';
import { NotificationsTab } from '../../components/profile/NotificationsTab';
import { AcademicPortfolio } from '../../components/profile/academic/AcademicPortfolio';

type ProfileTab =
  | 'overview'
  | 'academic'
  | 'avatar'
  | 'salary'
  | 'attendance'
  | 'leave'
  | 'projects'
  | 'notifications'
  | 'security';

export const ProfilePage: React.FC = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<ProfileTab>('overview');

  if (!user) {
    return (
      <div className="p-8 text-center text-slate-500">
        Loading employee profile data...
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-12">
      {/* Page Header */}
      <div>
        <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
          <UserCheck className="w-4 h-4" />
          <span>Enterprise User Identity & Employee Hub</span>
        </div>
        <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">
          Employee Account & Workspace
        </h1>
        <p className="text-xs sm:text-sm text-slate-500">
          Manage your personal profile, credentials, view compensation history, attendance, leaves, assigned deliverables, and activity notifications.
        </p>
      </div>

      {/* Main Profile Card Header */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 sm:p-8 shadow-xl flex flex-col md:flex-row items-center md:items-start gap-6 relative overflow-hidden">
        <div className="relative group shrink-0">
          <img
            src={
              user.avatarUrl ||
              'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=250'
            }
            alt={user.firstName}
            className="w-24 h-24 rounded-2xl object-cover border-2 border-indigo-600 shadow-xl ring-4 ring-indigo-500/10"
          />
          <button
            onClick={() => setActiveTab('avatar')}
            className="absolute -bottom-2 -right-2 p-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl shadow-lg transition-transform hover:scale-110"
            title="Update Profile Picture"
          >
            <Camera className="w-4 h-4" />
          </button>
        </div>

        <div className="space-y-2 text-center md:text-left flex-1 min-w-0">
          <div className="flex flex-wrap items-center justify-center md:justify-start gap-2">
            <h2 className="text-2xl font-black text-slate-900 dark:text-white">
              {user.firstName} {user.lastName}
            </h2>
            <span className="px-3 py-1 text-xs font-mono font-bold rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20">
              {user.role.replace('ROLE_', '')}
            </span>
            {user.emailVerified ? (
              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-[11px] font-bold rounded-full bg-emerald-500/10 text-emerald-500 border border-emerald-500/20">
                <CheckCircle2 className="w-3.5 h-3.5" /> Verified
              </span>
            ) : (
              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-[11px] font-bold rounded-full bg-amber-500/10 text-amber-500 border border-amber-500/20">
                <AlertCircle className="w-3.5 h-3.5" /> Email Unverified
              </span>
            )}
          </div>

          <div className="flex flex-wrap items-center justify-center md:justify-start gap-4 text-xs text-slate-500 dark:text-slate-400">
            <span className="flex items-center gap-1.5 font-medium">
              <Briefcase className="w-3.5 h-3.5 text-slate-400" />
              {user.designation || 'Senior Full Stack Engineer'}
            </span>
            <span className="flex items-center gap-1.5 font-medium">
              <Building2 className="w-3.5 h-3.5 text-slate-400" />
              {user.department || 'Frontend Engineering'}
            </span>
            <span className="flex items-center gap-1.5 font-mono text-indigo-600 dark:text-indigo-400 font-semibold">
              <Mail className="w-3.5 h-3.5" />
              {user.email}
            </span>
          </div>

          {/* Quick Info Strip */}
          <div className="pt-3 border-t border-slate-100 dark:border-slate-800/80 flex flex-wrap gap-6 text-xs text-slate-500">
            <div>
              <span className="text-[10px] text-slate-400 block uppercase font-bold">Employee ID</span>
              <span className="font-mono text-slate-700 dark:text-slate-300 font-bold">{user.id}</span>
            </div>
            <div>
              <span className="text-[10px] text-slate-400 block uppercase font-bold">Contact Phone</span>
              <span className="font-mono text-slate-700 dark:text-slate-300 font-bold">{user.phoneNumber || '+1 (555) 018-7712'}</span>
            </div>
            <div>
              <span className="text-[10px] text-slate-400 block uppercase font-bold">Annual Salary</span>
              <span className="font-mono text-emerald-600 dark:text-emerald-400 font-extrabold flex items-center gap-0.5">
                <Lock className="w-3 h-3 text-amber-500" /> ${ (user.salary || 135000).toLocaleString() }
              </span>
            </div>
            <div>
              <span className="text-[10px] text-slate-400 block uppercase font-bold">Reporting Manager</span>
              <span className="font-bold text-slate-700 dark:text-slate-300 flex items-center gap-1">
                <UserCheck className="w-3 h-3 text-indigo-500" /> {user.managerName || 'Ganesh Pal'}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Profile Tabs Navigation Bar */}
      <div className="flex overflow-x-auto no-scrollbar border-b border-slate-200 dark:border-slate-800 gap-1 sm:gap-2 pb-0.5">
        <button
          onClick={() => setActiveTab('overview')}
          className={`pb-3 px-3.5 sm:px-4 font-bold text-xs uppercase tracking-wider transition-all border-b-2 whitespace-nowrap ${
            activeTab === 'overview'
              ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <span className="flex items-center gap-2">
            <User className="w-4 h-4" /> Overview
          </span>
        </button>

        <button
          onClick={() => setActiveTab('academic')}
          className={`pb-3 px-3.5 sm:px-4 font-bold text-xs uppercase tracking-wider transition-all border-b-2 whitespace-nowrap ${
            activeTab === 'academic'
              ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <span className="flex items-center gap-2">
            <GraduationCap className="w-4 h-4 text-indigo-500" /> Academic Portfolio
          </span>
        </button>

        <button
          onClick={() => setActiveTab('avatar')}
          className={`pb-3 px-3.5 sm:px-4 font-bold text-xs uppercase tracking-wider transition-all border-b-2 whitespace-nowrap ${
            activeTab === 'avatar'
              ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <span className="flex items-center gap-2">
            <Camera className="w-4 h-4" /> Upload Avatar
          </span>
        </button>

        <button
          onClick={() => setActiveTab('salary')}
          className={`pb-3 px-3.5 sm:px-4 font-bold text-xs uppercase tracking-wider transition-all border-b-2 whitespace-nowrap ${
            activeTab === 'salary'
              ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <span className="flex items-center gap-2">
            <DollarSign className="w-4 h-4 text-emerald-500" /> Salary History
          </span>
        </button>

        <button
          onClick={() => setActiveTab('attendance')}
          className={`pb-3 px-3.5 sm:px-4 font-bold text-xs uppercase tracking-wider transition-all border-b-2 whitespace-nowrap ${
            activeTab === 'attendance'
              ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <span className="flex items-center gap-2">
            <Clock className="w-4 h-4" /> Attendance
          </span>
        </button>

        <button
          onClick={() => setActiveTab('leave')}
          className={`pb-3 px-3.5 sm:px-4 font-bold text-xs uppercase tracking-wider transition-all border-b-2 whitespace-nowrap ${
            activeTab === 'leave'
              ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <span className="flex items-center gap-2">
            <CalendarDays className="w-4 h-4" /> Leave Quotas
          </span>
        </button>

        <button
          onClick={() => setActiveTab('projects')}
          className={`pb-3 px-3.5 sm:px-4 font-bold text-xs uppercase tracking-wider transition-all border-b-2 whitespace-nowrap ${
            activeTab === 'projects'
              ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <span className="flex items-center gap-2">
            <FolderKanban className="w-4 h-4" /> Assigned Projects
          </span>
        </button>

        <button
          onClick={() => setActiveTab('notifications')}
          className={`pb-3 px-3.5 sm:px-4 font-bold text-xs uppercase tracking-wider transition-all border-b-2 whitespace-nowrap ${
            activeTab === 'notifications'
              ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <span className="flex items-center gap-2">
            <Bell className="w-4 h-4" /> Notifications
          </span>
        </button>

        <button
          onClick={() => setActiveTab('security')}
          className={`pb-3 px-3.5 sm:px-4 font-bold text-xs uppercase tracking-wider transition-all border-b-2 whitespace-nowrap ${
            activeTab === 'security'
              ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <span className="flex items-center gap-2">
            <Key className="w-4 h-4" /> Change Password
          </span>
        </button>
      </div>

      {/* Active Tab Content */}
      {activeTab === 'overview' && <ProfileOverviewTab user={user} />}
      {activeTab === 'academic' && <AcademicPortfolio />}
      {activeTab === 'avatar' && <AvatarUploadTab />}
      {activeTab === 'salary' && <SalaryHistoryTab user={user} />}
      {activeTab === 'attendance' && <AttendanceTab />}
      {activeTab === 'leave' && <LeaveTab />}
      {activeTab === 'projects' && <AssignedProjectsTab />}
      {activeTab === 'notifications' && <NotificationsTab />}
      {activeTab === 'security' && <ChangePassword />}
    </div>
  );
};
