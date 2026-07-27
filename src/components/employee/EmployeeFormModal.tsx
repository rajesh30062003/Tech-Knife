import React, { useState, useEffect } from 'react';
import { X, Save, User, Building2, Shield, DollarSign, Calendar, Mail, Phone, MapPin, UserCheck } from 'lucide-react';
import { EmployeeData, employeesApi } from '../../api/employees';
import { organizationApi, Department, Designation } from '../../api/organization';
import { Role } from '../../types';
import { AvatarUpload } from './AvatarUpload';

interface EmployeeFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: Partial<EmployeeData>) => Promise<void>;
  initialData?: EmployeeData | null;
  isLoading?: boolean;
}

export const EmployeeFormModal: React.FC<EmployeeFormModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  isLoading = false,
}) => {
  const [formData, setFormData] = useState<Partial<EmployeeData>>({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    role: 'ROLE_EMPLOYEE',
    department: 'Engineering & DevOps',
    designation: 'Software Engineer',
    managerId: '',
    joinDate: new Date().toISOString().split('T')[0],
    status: 'Active',
    salary: 120000,
    avatarUrl: '',
    address: '',
    emergencyContact: '',
    bio: '',
    skills: ['TypeScript', 'React', 'Agile']
  });

  const [skillsInput, setSkillsInput] = useState('');
  const [departmentsList, setDepartmentsList] = useState<Department[]>([]);
  const [designationsList, setDesignationsList] = useState<Designation[]>([]);
  const [managersList, setManagersList] = useState<EmployeeData[]>([]);

  useEffect(() => {
    if (isOpen) {
      // Load departments, designations, and managers for select options
      organizationApi.getDepartments({ limit: 100 }).then(res => setDepartmentsList(res.departments));
      organizationApi.getDesignations({ limit: 100 }).then(res => setDesignationsList(res.designations));
      employeesApi.getEmployees({ limit: 100 }).then(res => setManagersList(res.employees));
    }
  }, [isOpen]);

  useEffect(() => {
    if (initialData) {
      setFormData(initialData);
      setSkillsInput(initialData.skills ? initialData.skills.join(', ') : '');
    } else {
      setFormData({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        role: 'ROLE_EMPLOYEE',
        department: 'Engineering & DevOps',
        designation: 'Software Engineer',
        managerId: '',
        joinDate: new Date().toISOString().split('T')[0],
        status: 'Active',
        salary: 120000,
        avatarUrl: '',
        address: '',
        emergencyContact: '',
        bio: '',
        skills: ['TypeScript', 'React', 'Agile']
      });
      setSkillsInput('TypeScript, React, Agile');
    }
  }, [initialData, isOpen]);

  if (!isOpen) return null;

  const handleChange = (field: keyof EmployeeData, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmitForm = async (e: React.FormEvent) => {
    e.preventDefault();
    const parsedSkills = skillsInput
      .split(',')
      .map(s => s.trim())
      .filter(s => s.length > 0);

    await onSubmit({
      ...formData,
      skills: parsedSkills,
      salary: Number(formData.salary)
    });
  };

  const isEdit = Boolean(initialData?.id);
  // Filter managers list to prevent self-selection
  const availableManagers = managersList.filter(m => m.id !== initialData?.id);

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 overflow-y-auto">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-2xl p-6 sm:p-8 space-y-6 shadow-2xl animate-in fade-in zoom-in-95 duration-150 my-8">
        
        {/* Header */}
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">
              {isEdit ? 'Edit Employee Profile' : 'Register New Staff Member'}
            </h2>
            <p className="text-xs text-slate-500 mt-0.5">
              {isEdit ? 'Update employee designation, department, manager or compensation' : 'Add new talent to Tech Knife enterprise directory'}
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmitForm} className="space-y-6">
          {/* Avatar Upload Component */}
          <AvatarUpload
            currentUrl={formData.avatarUrl}
            name={`${formData.firstName || ''} ${formData.lastName || ''}`}
            onChange={(url) => handleChange('avatarUrl', url)}
          />

          {/* Personal Info Grid */}
          <div className="space-y-4">
            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider block">
              Personal Information
            </span>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">First Name *</label>
                <input
                  type="text"
                  required
                  value={formData.firstName || ''}
                  onChange={(e) => handleChange('firstName', e.target.value)}
                  placeholder="e.g. Sarah"
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Last Name *</label>
                <input
                  type="text"
                  required
                  value={formData.lastName || ''}
                  onChange={(e) => handleChange('lastName', e.target.value)}
                  placeholder="e.g. Connor"
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Corporate Email *</label>
                <input
                  type="email"
                  required
                  value={formData.email || ''}
                  onChange={(e) => handleChange('email', e.target.value)}
                  placeholder="s.connor@techknife.com"
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Phone Number</label>
                <input
                  type="text"
                  value={formData.phone || ''}
                  onChange={(e) => handleChange('phone', e.target.value)}
                  placeholder="+1 (555) 000-0000"
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                />
              </div>
            </div>
          </div>

          {/* Job & Org Information */}
          <div className="space-y-4">
            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider block">
              Job & Position Details
            </span>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Department *</label>
                <select
                  value={formData.department || 'Engineering & DevOps'}
                  onChange={(e) => handleChange('department', e.target.value)}
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                >
                  {departmentsList.length > 0 ? (
                    departmentsList.map(dept => (
                      <option key={dept.id} value={dept.name}>{dept.name}</option>
                    ))
                  ) : (
                    <>
                      <option value="Executive Leadership">Executive Leadership</option>
                      <option value="Engineering & DevOps">Engineering & DevOps</option>
                      <option value="Product Management">Product Management</option>
                      <option value="Client Growth & CRM">Client Growth & CRM</option>
                      <option value="Quality Assurance">Quality Assurance</option>
                      <option value="Human Capital & HR">Human Capital & HR</option>
                    </>
                  )}
                </select>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Job Designation *</label>
                <select
                  value={formData.designation || 'Software Engineer'}
                  onChange={(e) => handleChange('designation', e.target.value)}
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                >
                  {designationsList.length > 0 ? (
                    designationsList.map(desg => (
                      <option key={desg.id} value={desg.title}>
                        {desg.title} ({desg.grade})
                      </option>
                    ))
                  ) : (
                    <>
                      <option value="Managing Director">Managing Director (L7)</option>
                      <option value="Chief Technology Officer">Chief Technology Officer (L7)</option>
                      <option value="Senior Engineering Manager">Senior Engineering Manager (L5)</option>
                      <option value="Senior Frontend Lead">Senior Frontend Lead (L4)</option>
                      <option value="Growth Lead">Growth Lead (L4)</option>
                      <option value="Lead QA Automation Specialist">Lead QA Automation Specialist (L4)</option>
                      <option value="Software Engineer">Software Engineer (L2)</option>
                    </>
                  )}
                </select>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Reporting Manager *</label>
                <select
                  value={formData.managerId || ''}
                  onChange={(e) => handleChange('managerId', e.target.value)}
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-medium"
                >
                  <option value="">-- No Direct Manager (Top Level Executive) --</option>
                  {availableManagers.map(m => (
                    <option key={m.id} value={m.id}>
                      {m.firstName} {m.lastName} — {m.designation} ({m.department})
                    </option>
                  ))}
                </select>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">RBAC System Role *</label>
                <select
                  value={formData.role || 'ROLE_EMPLOYEE'}
                  onChange={(e) => handleChange('role', e.target.value as Role)}
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                >
                  <option value="ROLE_MD">Managing Director</option>
                  <option value="ROLE_CTO">Chief Technology Officer</option>
                  <option value="ROLE_MANAGER">Manager</option>
                  <option value="ROLE_EMPLOYEE">Employee</option>
                </select>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Status *</label>
                <select
                  value={formData.status || 'Active'}
                  onChange={(e) => handleChange('status', e.target.value)}
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                >
                  <option value="Active">Active</option>
                  <option value="On Leave">On Leave</option>
                  <option value="Suspended">Suspended</option>
                </select>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Annual Base Salary ($) *</label>
                <input
                  type="number"
                  required
                  value={formData.salary || 120000}
                  onChange={(e) => handleChange('salary', e.target.value)}
                  placeholder="120000"
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Date of Joining *</label>
                <input
                  type="date"
                  required
                  value={formData.joinDate || ''}
                  onChange={(e) => handleChange('joinDate', e.target.value)}
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                />
              </div>
            </div>
          </div>

          {/* Skills & Bio */}
          <div className="space-y-4">
            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider block">
              Skills & Professional Background
            </span>

            <div className="space-y-3">
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Skills (Comma-separated)</label>
                <input
                  type="text"
                  value={skillsInput}
                  onChange={(e) => setSkillsInput(e.target.value)}
                  placeholder="e.g. React, TypeScript, Spring Security, DevOps"
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Professional Bio</label>
                <textarea
                  rows={2}
                  value={formData.bio || ''}
                  onChange={(e) => handleChange('bio', e.target.value)}
                  placeholder="Summary of experience and background..."
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                ></textarea>
              </div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100 dark:border-slate-800">
            <button
              type="button"
              onClick={onClose}
              disabled={isLoading}
              className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center gap-2"
            >
              {isLoading ? (
                <>
                  <span className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
                  Saving...
                </>
              ) : (
                <>
                  <Save className="w-4 h-4" />
                  {isEdit ? 'Update Employee' : 'Create Employee Record'}
                </>
              )}
            </button>
          </div>
        </form>

      </div>
    </div>
  );
};
