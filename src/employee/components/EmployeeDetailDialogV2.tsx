import React from 'react';
import {
  X,
  Mail,
  Phone,
  Building2,
  Calendar,
  DollarSign,
  Github,
  Award,
  Clock,
  Shield,
  User,
  Heart,
  Briefcase,
  Edit2,
  Trash2,
} from 'lucide-react';
import { EmployeeResponse } from '../types/employeeV2';

interface EmployeeDetailDialogV2Props {
  isOpen: boolean;
  employee: EmployeeResponse | null;
  onClose: () => void;
  onEdit: (employee: EmployeeResponse) => void;
  onDelete: (employee: EmployeeResponse) => void;
}

export const EmployeeDetailDialogV2: React.FC<EmployeeDetailDialogV2Props> = ({
  isOpen,
  employee,
  onClose,
  onEdit,
  onDelete,
}) => {
  if (!isOpen || !employee) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-in fade-in duration-200">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-2xl max-h-[90vh] flex flex-col shadow-2xl overflow-hidden">
        {/* Profile Banner & Header */}
        <div className="relative bg-gradient-to-r from-indigo-900 via-slate-900 to-indigo-950 p-6 text-white">
          <button
            onClick={onClose}
            className="absolute top-4 right-4 p-2 rounded-full bg-white/10 hover:bg-white/20 text-white transition-colors"
          >
            <X className="w-4 h-4" />
          </button>

          <div className="flex flex-col sm:flex-row items-center sm:items-start gap-4">
            <img
              src={
                employee.profileImage ||
                'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=300'
              }
              alt={employee.fullName}
              className="w-20 h-20 rounded-2xl object-cover border-2 border-white/20 shadow-lg shrink-0"
            />
            <div className="text-center sm:text-left space-y-1">
              <div className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-white/10 text-[11px] font-mono font-semibold">
                {employee.employeeId}
              </div>
              <h2 className="text-xl font-black">{employee.fullName}</h2>
              <p className="text-xs text-indigo-200 font-medium">
                {employee.designationId} • {employee.departmentId}
              </p>
            </div>
          </div>
        </div>

        {/* Profile Content Grid */}
        <div className="flex-1 overflow-y-auto p-6 space-y-5 text-xs">
          {/* Key Metrics Row */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 bg-slate-50 dark:bg-slate-800/60 p-3.5 rounded-2xl border border-slate-200/80 dark:border-slate-800">
            <div>
              <span className="block text-[10px] text-slate-400 font-semibold uppercase">Status</span>
              <span className="font-bold text-slate-800 dark:text-slate-200">{employee.status}</span>
            </div>
            <div>
              <span className="block text-[10px] text-slate-400 font-semibold uppercase">Employment</span>
              <span className="font-bold text-slate-800 dark:text-slate-200">{employee.employmentType.replace('_', ' ')}</span>
            </div>
            <div>
              <span className="block text-[10px] text-slate-400 font-semibold uppercase">Joining Date</span>
              <span className="font-bold text-slate-800 dark:text-slate-200">{employee.joiningDate}</span>
            </div>
            <div>
              <span className="block text-[10px] text-slate-400 font-semibold uppercase">Annual Compensation</span>
              <span className="font-bold text-emerald-600 dark:text-emerald-400">${employee.salary.toLocaleString()}</span>
            </div>
          </div>

          {/* Contact Details */}
          <div className="space-y-2">
            <h4 className="font-bold text-slate-800 dark:text-slate-200 border-b border-slate-100 dark:border-slate-800 pb-1 flex items-center gap-1.5">
              <Mail className="w-3.5 h-3.5 text-indigo-500" /> Contact & Communication
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-slate-600 dark:text-slate-300">
              <div><span className="text-slate-400">Official Email:</span> {employee.officialEmail}</div>
              <div><span className="text-slate-400">Personal Email:</span> {employee.personalEmail || 'N/A'}</div>
              <div><span className="text-slate-400">Primary Phone:</span> {employee.primaryMobile}</div>
              <div><span className="text-slate-400">Alternate Phone:</span> {employee.alternateMobile || 'N/A'}</div>
            </div>
          </div>

          {/* Personal Details */}
          <div className="space-y-2">
            <h4 className="font-bold text-slate-800 dark:text-slate-200 border-b border-slate-100 dark:border-slate-800 pb-1 flex items-center gap-1.5">
              <User className="w-3.5 h-3.5 text-indigo-500" /> Demographics & Identity
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-slate-600 dark:text-slate-300">
              <div><span className="text-slate-400">Gender:</span> {employee.gender || 'Not specified'}</div>
              <div><span className="text-slate-400">Date of Birth:</span> {employee.dob || 'Not recorded'}</div>
              <div><span className="text-slate-400">Blood Group:</span> {employee.bloodGroup ? employee.bloodGroup.replace('_', ' ') : 'N/A'}</div>
              <div><span className="text-slate-400">Reporting Manager:</span> {employee.managerId || 'Direct / None'}</div>
            </div>
          </div>

          {/* Skills & GitHub */}
          <div className="space-y-2">
            <h4 className="font-bold text-slate-800 dark:text-slate-200 border-b border-slate-100 dark:border-slate-800 pb-1 flex items-center gap-1.5">
              <Award className="w-3.5 h-3.5 text-indigo-500" /> Technical Skills & Integrations
            </h4>
            {employee.skills && employee.skills.length > 0 ? (
              <div className="flex flex-wrap gap-1.5 pt-1">
                {employee.skills.map((skill, idx) => (
                  <span
                    key={idx}
                    className="px-2.5 py-1 rounded-lg bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 font-semibold border border-indigo-200/50"
                  >
                    {skill}
                  </span>
                ))}
              </div>
            ) : (
              <p className="text-slate-400 italic">No technical skills recorded.</p>
            )}

            {employee.githubUsername && (
              <div className="flex items-center gap-2 pt-2 text-slate-700 dark:text-slate-300">
                <Github className="w-4 h-4 text-slate-500" />
                <span>GitHub: github.com/{employee.githubUsername}</span>
              </div>
            )}
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-4 border-t border-slate-200 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-800/40 flex items-center justify-between">
          <button
            onClick={() => {
              onClose();
              onDelete(employee);
            }}
            className="px-3.5 py-2 text-xs font-bold text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/50 rounded-xl transition-colors flex items-center gap-1.5"
          >
            <Trash2 className="w-3.5 h-3.5" /> Delete Employee
          </button>

          <div className="flex items-center gap-2">
            <button
              onClick={onClose}
              className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"
            >
              Close
            </button>
            <button
              onClick={() => {
                onClose();
                onEdit(employee);
              }}
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs rounded-xl transition-all shadow-md flex items-center gap-1.5"
            >
              <Edit2 className="w-3.5 h-3.5" /> Edit Profile
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
