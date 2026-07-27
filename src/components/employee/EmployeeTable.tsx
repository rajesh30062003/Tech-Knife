import React, { useState } from 'react';
import { Eye, Edit3, Trash2, Ban, MoreVertical, ArrowUpDown, Building2, Mail, Shield, CheckCircle } from 'lucide-react';
import { EmployeeData } from '../../api/employees';
import { StatusBadge } from '../common/StatusBadge';

interface EmployeeTableProps {
  employees: EmployeeData[];
  onView: (emp: EmployeeData) => void;
  onEdit: (emp: EmployeeData) => void;
  onDelete: (emp: EmployeeData) => void;
  onToggleStatus: (emp: EmployeeData) => void;
  canEdit?: boolean;
  canDelete?: boolean;
}

export const EmployeeTable: React.FC<EmployeeTableProps> = ({
  employees,
  onView,
  onEdit,
  onDelete,
  onToggleStatus,
  canEdit = true,
  canDelete = true,
}) => {
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [sortField, setSortField] = useState<keyof EmployeeData>('firstName');
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');

  const handleSelectAll = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.checked) {
      setSelectedIds(employees.map(emp => emp.id));
    } else {
      setSelectedIds([]);
    }
  };

  const handleSelectRow = (id: string) => {
    if (selectedIds.includes(id)) {
      setSelectedIds(selectedIds.filter(i => i !== id));
    } else {
      setSelectedIds([...selectedIds, id]);
    }
  };

  const handleSort = (field: keyof EmployeeData) => {
    if (sortField === field) {
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortOrder('asc');
    }
  };

  const sortedEmployees = [...employees].sort((a, b) => {
    const valA = a[sortField] ?? '';
    const valB = b[sortField] ?? '';
    if (valA < valB) return sortOrder === 'asc' ? -1 : 1;
    if (valA > valB) return sortOrder === 'asc' ? 1 : -1;
    return 0;
  });

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl overflow-hidden shadow-xs">
      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs border-collapse">
          <thead>
            <tr className="bg-slate-50 dark:bg-slate-800/80 border-b border-slate-200 dark:border-slate-800 text-slate-500 font-bold uppercase tracking-wider text-[10px]">
              <th className="p-4 w-10">
                <input
                  type="checkbox"
                  checked={selectedIds.length === employees.length && employees.length > 0}
                  onChange={handleSelectAll}
                  className="rounded-md border-slate-300 dark:border-slate-700 text-indigo-600 focus:ring-indigo-500"
                />
              </th>
              <th className="p-4 cursor-pointer hover:text-indigo-600" onClick={() => handleSort('firstName')}>
                <div className="flex items-center gap-1">
                  <span>Employee Name</span>
                  <ArrowUpDown className="w-3 h-3" />
                </div>
              </th>
              <th className="p-4 cursor-pointer hover:text-indigo-600" onClick={() => handleSort('department')}>
                <div className="flex items-center gap-1">
                  <span>Department & Designation</span>
                  <ArrowUpDown className="w-3 h-3" />
                </div>
              </th>
              <th className="p-4 cursor-pointer hover:text-indigo-600" onClick={() => handleSort('role')}>
                <div className="flex items-center gap-1">
                  <span>RBAC Role</span>
                  <ArrowUpDown className="w-3 h-3" />
                </div>
              </th>
              <th className="p-4 cursor-pointer hover:text-indigo-600" onClick={() => handleSort('status')}>
                <div className="flex items-center gap-1">
                  <span>Status</span>
                  <ArrowUpDown className="w-3 h-3" />
                </div>
              </th>
              <th className="p-4 cursor-pointer hover:text-indigo-600" onClick={() => handleSort('salary')}>
                <div className="flex items-center gap-1">
                  <span>Compensation</span>
                  <ArrowUpDown className="w-3 h-3" />
                </div>
              </th>
              <th className="p-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
            {sortedEmployees.length === 0 ? (
              <tr>
                <td colSpan={7} className="p-8 text-center text-slate-400 font-medium">
                  No employee records matched your filter criteria.
                </td>
              </tr>
            ) : (
              sortedEmployees.map((emp) => {
                const isSelected = selectedIds.includes(emp.id);
                const initials = `${emp.firstName[0] || ''}${emp.lastName[0] || ''}`.toUpperCase();

                return (
                  <tr
                    key={emp.id}
                    className={`hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors ${
                      isSelected ? 'bg-indigo-50/30 dark:bg-indigo-950/20' : ''
                    }`}
                  >
                    <td className="p-4">
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => handleSelectRow(emp.id)}
                        className="rounded-md border-slate-300 dark:border-slate-700 text-indigo-600 focus:ring-indigo-500"
                      />
                    </td>

                    <td className="p-4">
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-xl bg-indigo-600 text-white font-extrabold flex items-center justify-center text-xs shrink-0 shadow-xs overflow-hidden">
                          {emp.avatarUrl ? (
                            <img src={emp.avatarUrl} alt={emp.firstName} className="w-full h-full object-cover" />
                          ) : (
                            <span>{initials}</span>
                          )}
                        </div>
                        <div>
                          <div className="font-bold text-slate-900 dark:text-white">
                            {emp.firstName} {emp.lastName}
                          </div>
                          <div className="text-[11px] text-slate-400 flex items-center gap-1">
                            <Mail className="w-3 h-3" />
                            <span>{emp.email}</span>
                          </div>
                        </div>
                      </div>
                    </td>

                    <td className="p-4">
                      <div className="font-semibold text-slate-800 dark:text-slate-200">
                        {emp.designation}
                      </div>
                      <div className="text-[11px] text-slate-400 flex items-center gap-1">
                        <Building2 className="w-3 h-3" />
                        <span>{emp.department}</span>
                      </div>
                    </td>

                    <td className="p-4">
                      <span className="px-2 py-0.5 rounded-lg font-mono font-bold text-[10px] bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300">
                        {emp.role.replace('ROLE_', '')}
                      </span>
                    </td>

                    <td className="p-4">
                      <StatusBadge status={emp.status === 'Active' ? 'Active' : emp.status === 'On Leave' ? 'In Progress' : 'Critical'} />
                    </td>

                    <td className="p-4 font-bold text-emerald-600 dark:text-emerald-400">
                      ${emp.salary.toLocaleString()}/yr
                    </td>

                    <td className="p-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => onView(emp)}
                          className="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 rounded-lg transition-colors"
                          title="View Details"
                        >
                          <Eye className="w-3.5 h-3.5" />
                        </button>

                        {canEdit && (
                          <button
                            onClick={() => onEdit(emp)}
                            className="p-1.5 hover:bg-indigo-50 dark:hover:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 rounded-lg transition-colors"
                            title="Edit Employee"
                          >
                            <Edit3 className="w-3.5 h-3.5" />
                          </button>
                        )}

                        {canEdit && (
                          <button
                            onClick={() => onToggleStatus(emp)}
                            className={`p-1.5 rounded-lg transition-colors ${
                              emp.status === 'Active'
                                ? 'hover:bg-amber-50 text-amber-600'
                                : 'hover:bg-emerald-50 text-emerald-600'
                            }`}
                            title={emp.status === 'Active' ? 'Suspend Employee' : 'Activate Employee'}
                          >
                            {emp.status === 'Active' ? <Ban className="w-3.5 h-3.5" /> : <CheckCircle className="w-3.5 h-3.5" />}
                          </button>
                        )}

                        {canDelete && (
                          <button
                            onClick={() => onDelete(emp)}
                            className="p-1.5 hover:bg-rose-50 dark:hover:bg-rose-950/60 text-rose-600 dark:text-rose-400 rounded-lg transition-colors"
                            title="Delete Employee"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
