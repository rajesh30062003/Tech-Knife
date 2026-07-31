import React from 'react';
import {
  Eye,
  Edit2,
  Trash2,
  ShieldAlert,
  Building2,
  Mail,
  Phone,
  Calendar,
  MoreVertical,
  CheckCircle,
  Clock,
  XCircle,
  AlertTriangle,
} from 'lucide-react';
import { EmployeeResponse, EmployeeStatus, EmploymentType } from '../types/employeeV2';

interface EmployeeTableV2Props {
  employees: EmployeeResponse[];
  onView: (employee: EmployeeResponse) => void;
  onEdit: (employee: EmployeeResponse) => void;
  onStatusChange: (employee: EmployeeResponse) => void;
  onDelete: (employee: EmployeeResponse) => void;
  isLoading?: boolean;
}

export const EmployeeTableV2: React.FC<EmployeeTableV2Props> = ({
  employees,
  onView,
  onEdit,
  onStatusChange,
  onDelete,
  isLoading,
}) => {
  const getStatusBadge = (status: EmployeeStatus) => {
    switch (status) {
      case 'ACTIVE':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-400 border border-emerald-200/60">
            <CheckCircle className="w-3 h-3" /> Active
          </span>
        );
      case 'INACTIVE':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-700">
            <Clock className="w-3 h-3" /> Inactive
          </span>
        );
      case 'SUSPENDED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-amber-50 dark:bg-amber-950/60 text-amber-700 dark:text-amber-400 border border-amber-200/60">
            <AlertTriangle className="w-3 h-3" /> Suspended
          </span>
        );
      case 'TERMINATED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-rose-50 dark:bg-rose-950/60 text-rose-700 dark:text-rose-400 border border-rose-200/60">
            <XCircle className="w-3 h-3" /> Terminated
          </span>
        );
      case 'RESIGNED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-purple-50 dark:bg-purple-950/60 text-purple-700 dark:text-purple-400 border border-purple-200/60">
            <Clock className="w-3 h-3" /> Resigned
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-slate-100 text-slate-700">
            {status}
          </span>
        );
    }
  };

  const getEmploymentTypeBadge = (type: EmploymentType) => {
    switch (type) {
      case 'FULL_TIME':
        return (
          <span className="px-2 py-0.5 rounded-md text-[10px] font-semibold bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 border border-indigo-200/50">
            Full Time
          </span>
        );
      case 'CONTRACT':
        return (
          <span className="px-2 py-0.5 rounded-md text-[10px] font-semibold bg-cyan-50 dark:bg-cyan-950/60 text-cyan-600 dark:text-cyan-400 border border-cyan-200/50">
            Contract
          </span>
        );
      case 'PROBATION':
        return (
          <span className="px-2 py-0.5 rounded-md text-[10px] font-semibold bg-orange-50 dark:bg-orange-950/60 text-orange-600 dark:text-orange-400 border border-orange-200/50">
            Probation
          </span>
        );
      default:
        return (
          <span className="px-2 py-0.5 rounded-md text-[10px] font-semibold bg-slate-100 text-slate-600">
            {type.replace('_', ' ')}
          </span>
        );
    }
  };

  if (isLoading) {
    return (
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-12 text-center">
        <div className="w-6 h-6 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3"></div>
        <p className="text-xs text-slate-500">Loading Tech Knife staff records...</p>
      </div>
    );
  }

  if (employees.length === 0) {
    return (
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-12 text-center space-y-3">
        <Building2 className="w-10 h-10 text-slate-300 dark:text-slate-700 mx-auto" />
        <h3 className="text-sm font-bold text-slate-800 dark:text-slate-200">No Employees Found</h3>
        <p className="text-xs text-slate-500 max-w-sm mx-auto">
          No employee records match your search criteria. Try clearing filters or onboard a new employee.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl overflow-hidden shadow-xs">
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-200 dark:border-slate-800 text-[11px] uppercase tracking-wider text-slate-500 dark:text-slate-400 font-bold">
              <th className="py-3 px-4">Employee</th>
              <th className="py-3 px-4">Department & Role</th>
              <th className="py-3 px-4">Contact</th>
              <th className="py-3 px-4">Reporting Manager</th>
              <th className="py-3 px-4">Type</th>
              <th className="py-3 px-4">Current Projects</th>
              <th className="py-3 px-4">Status</th>
              <th className="py-3 px-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-xs">
            {employees.map((emp) => (
              <tr
                key={emp.id}
                className="hover:bg-slate-50/70 dark:hover:bg-slate-800/40 transition-colors"
              >
                {/* Employee Name & Code */}
                <td className="py-3.5 px-4">
                  <div className="flex items-center gap-3">
                    <img
                      src={
                        emp.profileImage ||
                        `https://ui-avatars.com/api/?name=${encodeURIComponent(emp.fullName || 'Employee')}`
                      }
                      alt={emp.fullName}
                      className="w-9 h-9 rounded-full object-cover border border-slate-200 dark:border-slate-700"
                    />
                    <div>
                      <div className="font-bold text-slate-900 dark:text-slate-100 hover:text-indigo-600 transition-colors cursor-pointer" onClick={() => onView(emp)}>
                        {emp.fullName}
                      </div>
                      <div className="text-[10px] text-indigo-600 dark:text-indigo-400 font-mono font-semibold">
                        {emp.employeeId}
                      </div>
                    </div>
                  </div>
                </td>

                {/* Department & Role */}
                <td className="py-3.5 px-4">
                  <div className="font-semibold text-slate-800 dark:text-slate-200">
                    {emp.designationId}
                  </div>
                  <div className="text-[11px] text-slate-500 dark:text-slate-400">
                    {emp.departmentId}
                  </div>
                </td>

                {/* Contact Info */}
                <td className="py-3.5 px-4 space-y-0.5">
                  <div className="flex items-center gap-1.5 text-slate-600 dark:text-slate-300">
                    <Mail className="w-3 h-3 text-slate-400 shrink-0" />
                    <span className="truncate max-w-[150px]">{emp.officialEmail}</span>
                  </div>
                  <div className="flex items-center gap-1.5 text-slate-500 dark:text-slate-400 text-[11px]">
                    <Phone className="w-3 h-3 text-slate-400 shrink-0" />
                    <span>{emp.primaryMobile || '-'}</span>
                  </div>
                </td>

                {/* Reporting Manager */}
                <td className="py-3.5 px-4 text-slate-700 dark:text-slate-300 font-medium">
                  {emp.managerName || (emp.managerId && emp.managerId !== 'Unassigned' ? emp.managerId : 'Ranadhir Pal (EMP-001 - Executive MD)')}
                </td>

                {/* Employment Type */}
                <td className="py-3.5 px-4">
                  {getEmploymentTypeBadge(emp.employmentType)}
                </td>

                {/* Current Projects */}
                <td className="py-3.5 px-4 text-slate-600 dark:text-slate-400 text-[11px]">
                  {emp.currentProjects && emp.currentProjects.length > 0 ? (
                    <div className="flex flex-wrap gap-1">
                      {emp.currentProjects.slice(0, 2).map((p, idx) => (
                        <span key={idx} className="px-2.5 py-1 rounded-lg bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 font-semibold border border-indigo-200/50">
                          {p}
                        </span>
                      ))}
                      {emp.currentProjects.length > 2 && (
                        <span className="px-2 py-0.5 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-500 font-bold">
                          +{emp.currentProjects.length - 2} More
                        </span>
                      )}
                    </div>
                  ) : (
                    <span className="px-2.5 py-1 rounded-lg bg-cyan-50 dark:bg-cyan-950/60 text-cyan-700 dark:text-cyan-300 font-semibold border border-cyan-200/50">
                      Tech Knife ERP (Core Member)
                    </span>
                  )}
                </td>

                {/* Status */}
                <td className="py-3.5 px-4">{getStatusBadge(emp.status)}</td>

                {/* Action Buttons */}
                <td className="py-3.5 px-4 text-right">
                  <div className="flex items-center justify-end gap-1">
                    <button
                      onClick={() => onView(emp)}
                      title="View Profile"
                      className="p-1.5 rounded-lg text-slate-500 hover:text-indigo-600 hover:bg-indigo-50 dark:hover:bg-indigo-950/60 transition-colors"
                    >
                      <Eye className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => onEdit(emp)}
                      title="Edit Record"
                      className="p-1.5 rounded-lg text-slate-500 hover:text-cyan-600 hover:bg-cyan-50 dark:hover:bg-cyan-950/60 transition-colors"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => onStatusChange(emp)}
                      title="Change Status"
                      className="p-1.5 rounded-lg text-slate-500 hover:text-amber-600 hover:bg-amber-50 dark:hover:bg-amber-950/60 transition-colors"
                    >
                      <ShieldAlert className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => onDelete(emp)}
                      title="Delete Employee"
                      className="p-1.5 rounded-lg text-slate-500 hover:text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/60 transition-colors"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
