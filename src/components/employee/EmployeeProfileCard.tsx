import React from 'react';
import { Mail, Phone, MapPin, Building2, Shield, Calendar, DollarSign, Award, Edit3, Trash2, Ban, CheckCircle } from 'lucide-react';
import { EmployeeData } from '../../api/employees';
import { StatusBadge } from '../common/StatusBadge';

interface EmployeeProfileCardProps {
  employee: EmployeeData;
  onEdit?: (emp: EmployeeData) => void;
  onDelete?: (emp: EmployeeData) => void;
  onToggleStatus?: (emp: EmployeeData) => void;
  canEdit?: boolean;
  canDelete?: boolean;
}

export const EmployeeProfileCard: React.FC<EmployeeProfileCardProps> = ({
  employee,
  onEdit,
  onDelete,
  onToggleStatus,
  canEdit = true,
  canDelete = true,
}) => {
  const initials = `${employee.firstName[0] || ''}${employee.lastName[0] || ''}`.toUpperCase();

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-6">
      {/* Header Profile Section */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-indigo-600 text-white font-extrabold flex items-center justify-center text-xl shadow-md overflow-hidden shrink-0 border-2 border-indigo-500/20">
            {employee.avatarUrl ? (
              <img src={employee.avatarUrl} alt={`${employee.firstName} ${employee.lastName}`} className="w-full h-full object-cover" />
            ) : (
              <span>{initials}</span>
            )}
          </div>

          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">
                {employee.firstName} {employee.lastName}
              </h2>
              <StatusBadge status={employee.status === 'Active' ? 'Active' : employee.status === 'On Leave' ? 'In Progress' : 'Critical'} />
            </div>
            <p className="text-xs font-bold text-indigo-600 dark:text-indigo-400 mt-0.5">
              {employee.designation}
            </p>
            <p className="text-[11px] text-slate-400 font-mono mt-0.5">ID: {employee.id}</p>
          </div>
        </div>

        {/* Action buttons */}
        <div className="flex items-center gap-2">
          {canEdit && onEdit && (
            <button
              onClick={() => onEdit(employee)}
              className="px-3.5 py-2 bg-indigo-50 dark:bg-indigo-950/60 hover:bg-indigo-100 text-indigo-600 dark:text-indigo-400 font-bold text-xs rounded-xl transition-colors flex items-center gap-1.5"
            >
              <Edit3 className="w-3.5 h-3.5" /> Edit Profile
            </button>
          )}

          {canEdit && onToggleStatus && (
            <button
              onClick={() => onToggleStatus(employee)}
              className={`px-3 py-2 font-bold text-xs rounded-xl transition-colors flex items-center gap-1.5 ${
                employee.status === 'Active'
                  ? 'bg-amber-100 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300 hover:bg-amber-200'
                  : 'bg-emerald-100 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 hover:bg-emerald-200'
              }`}
            >
              {employee.status === 'Active' ? (
                <>
                  <Ban className="w-3.5 h-3.5" /> Suspend
                </>
              ) : (
                <>
                  <CheckCircle className="w-3.5 h-3.5" /> Activate
                </>
              )}
            </button>
          )}

          {canDelete && onDelete && (
            <button
              onClick={() => onDelete(employee)}
              className="p-2 bg-rose-50 dark:bg-rose-950/60 hover:bg-rose-100 text-rose-600 dark:text-rose-400 rounded-xl transition-colors"
              title="Delete Employee"
            >
              <Trash2 className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>

      {/* Details Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 text-xs">
        <div className="p-3.5 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-200/60 dark:border-slate-800 space-y-1">
          <div className="flex items-center gap-2 text-slate-400 font-semibold text-[11px]">
            <Building2 className="w-3.5 h-3.5 text-indigo-500" /> Department
          </div>
          <div className="font-bold text-slate-900 dark:text-white text-sm">{employee.department}</div>
        </div>

        <div className="p-3.5 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-200/60 dark:border-slate-800 space-y-1">
          <div className="flex items-center gap-2 text-slate-400 font-semibold text-[11px]">
            <Shield className="w-3.5 h-3.5 text-indigo-500" /> RBAC Security Role
          </div>
          <div className="font-bold text-slate-900 dark:text-white text-sm">{employee.role.replace('ROLE_', '')}</div>
        </div>

        <div className="p-3.5 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-200/60 dark:border-slate-800 space-y-1">
          <div className="flex items-center gap-2 text-slate-400 font-semibold text-[11px]">
            <DollarSign className="w-3.5 h-3.5 text-emerald-500" /> Annual Base Compensation
          </div>
          <div className="font-bold text-emerald-600 dark:text-emerald-400 text-sm">
            ${employee.salary.toLocaleString()}/yr
          </div>
        </div>

        <div className="p-3.5 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-200/60 dark:border-slate-800 space-y-1">
          <div className="flex items-center gap-2 text-slate-400 font-semibold text-[11px]">
            <Mail className="w-3.5 h-3.5 text-slate-400" /> Corporate Email
          </div>
          <div className="font-bold text-slate-900 dark:text-white truncate">{employee.email}</div>
        </div>

        <div className="p-3.5 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-200/60 dark:border-slate-800 space-y-1">
          <div className="flex items-center gap-2 text-slate-400 font-semibold text-[11px]">
            <Phone className="w-3.5 h-3.5 text-slate-400" /> Direct Phone
          </div>
          <div className="font-bold text-slate-900 dark:text-white">{employee.phone}</div>
        </div>

        <div className="p-3.5 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-200/60 dark:border-slate-800 space-y-1">
          <div className="flex items-center gap-2 text-slate-400 font-semibold text-[11px]">
            <Calendar className="w-3.5 h-3.5 text-slate-400" /> Date of Joining
          </div>
          <div className="font-bold text-slate-900 dark:text-white">{employee.joinDate}</div>
        </div>
      </div>

      {/* Skills & Bio */}
      <div className="space-y-3 pt-2">
        {employee.bio && (
          <div className="p-4 bg-slate-50 dark:bg-slate-800/30 rounded-2xl border border-slate-200/60 dark:border-slate-800 text-xs">
            <span className="text-[10px] uppercase font-extrabold text-slate-400 block mb-1">Professional Bio</span>
            <p className="text-slate-700 dark:text-slate-300 leading-relaxed">{employee.bio}</p>
          </div>
        )}

        {employee.skills && employee.skills.length > 0 && (
          <div className="space-y-1.5">
            <span className="text-[10px] uppercase font-extrabold text-slate-400 block">Core Competencies & Stack</span>
            <div className="flex flex-wrap gap-1.5">
              {employee.skills.map((skill, idx) => (
                <span
                  key={idx}
                  className="px-2.5 py-1 bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 font-semibold text-[11px] rounded-lg border border-indigo-200/60 dark:border-indigo-800/50"
                >
                  {skill}
                </span>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
