import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { X, UserPlus, Save, Sparkles, Building2, Shield } from 'lucide-react';
import {
  createEmployeeSchema,
  CreateEmployeeFormValues,
} from '../schemas/employeeSchema';
import { EmployeeResponse } from '../types/employeeV2';

interface EmployeeFormDialogV2Props {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (values: CreateEmployeeFormValues) => Promise<void>;
  initialData?: EmployeeResponse | null;
  isSubmitting?: boolean;
}

export const EmployeeFormDialogV2: React.FC<EmployeeFormDialogV2Props> = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  isSubmitting = false,
}) => {
  const isEditing = Boolean(initialData);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(createEmployeeSchema),
    defaultValues: {
      employeeId: '',
      officialEmail: '',
      personalEmail: '',
      primaryMobile: '',
      alternateMobile: '',
      firstName: '',
      lastName: '',
      gender: 'MALE' as const,
      dob: '',
      bloodGroup: 'O_POSITIVE' as const,
      departmentId: 'Engineering & DevOps',
      designationId: 'Software Engineer',
      managerId: '',
      joiningDate: new Date().toISOString().split('T')[0],
      employmentType: 'FULL_TIME' as const,
      salary: 120000,
      skills: ['TypeScript', 'React'],
      githubUsername: '',
      profileImage: '',
      status: 'ACTIVE' as const,
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        employeeId: initialData.employeeId,
        officialEmail: initialData.officialEmail,
        personalEmail: initialData.personalEmail || '',
        primaryMobile: initialData.primaryMobile,
        alternateMobile: initialData.alternateMobile || '',
        firstName: initialData.firstName,
        lastName: initialData.lastName,
        gender: initialData.gender || 'MALE',
        dob: initialData.dob || '',
        bloodGroup: initialData.bloodGroup || 'O_POSITIVE',
        departmentId: initialData.departmentId,
        designationId: initialData.designationId,
        managerId: initialData.managerId || '',
        joiningDate: initialData.joiningDate,
        employmentType: initialData.employmentType,
        salary: initialData.salary,
        skills: initialData.skills || [],
        githubUsername: initialData.githubUsername || '',
        profileImage: initialData.profileImage || '',
        status: initialData.status,
      });
    } else {
      reset({
        employeeId: `EMP-${Math.floor(1000 + Math.random() * 9000)}`,
        officialEmail: '',
        personalEmail: '',
        primaryMobile: '',
        alternateMobile: '',
        firstName: '',
        lastName: '',
        gender: 'MALE',
        dob: '',
        bloodGroup: 'O_POSITIVE',
        departmentId: 'Engineering & DevOps',
        designationId: 'Software Engineer',
        managerId: '',
        joiningDate: new Date().toISOString().split('T')[0],
        employmentType: 'FULL_TIME',
        salary: 120000,
        skills: ['TypeScript', 'Spring Boot'],
        githubUsername: '',
        profileImage: '',
        status: 'ACTIVE',
      });
    }
  }, [initialData, reset, isOpen]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-in fade-in duration-200">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-3xl max-h-[90vh] flex flex-col shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/40">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-2xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400">
              <UserPlus className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-lg font-extrabold text-slate-900 dark:text-white">
                {isEditing ? 'Update Employee Record' : 'Onboard New Employee'}
              </h2>
              <p className="text-xs text-slate-500">
                {isEditing
                  ? `Editing profile details for ${initialData?.fullName}`
                  : 'Register a new team member with demographic and corporate details'}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-xl text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Form Body */}
        <form
          onSubmit={handleSubmit((data) => onSubmit(data as unknown as CreateEmployeeFormValues))}
          className="flex-1 overflow-y-auto p-6 space-y-6"
        >
          {/* Section 1: Demographics & Name */}
          <div className="space-y-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5" /> 1. Personal & Demographic Details
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 text-xs">
              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Employee Code ID *
                </label>
                <input
                  type="text"
                  {...register('employeeId')}
                  disabled={isEditing}
                  placeholder="e.g. EMP-1001"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl font-mono text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none disabled:opacity-60"
                />
                {errors.employeeId && (
                  <p className="text-[11px] text-rose-500 mt-1">{errors.employeeId.message}</p>
                )}
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  First Name *
                </label>
                <input
                  type="text"
                  {...register('firstName')}
                  placeholder="Alexander"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                />
                {errors.firstName && (
                  <p className="text-[11px] text-rose-500 mt-1">{errors.firstName.message}</p>
                )}
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Last Name *
                </label>
                <input
                  type="text"
                  {...register('lastName')}
                  placeholder="Vance"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                />
                {errors.lastName && (
                  <p className="text-[11px] text-rose-500 mt-1">{errors.lastName.message}</p>
                )}
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Gender
                </label>
                <select
                  {...register('gender')}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                >
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                  <option value="PREFER_NOT_TO_SAY">Prefer not to say</option>
                </select>
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Date of Birth
                </label>
                <input
                  type="date"
                  {...register('dob')}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                />
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Blood Group
                </label>
                <select
                  {...register('bloodGroup')}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                >
                  <option value="O_POSITIVE">O+</option>
                  <option value="O_NEGATIVE">O-</option>
                  <option value="A_POSITIVE">A+</option>
                  <option value="A_NEGATIVE">A-</option>
                  <option value="B_POSITIVE">B+</option>
                  <option value="B_NEGATIVE">B-</option>
                  <option value="AB_POSITIVE">AB+</option>
                  <option value="AB_NEGATIVE">AB-</option>
                </select>
              </div>
            </div>
          </div>

          {/* Section 2: Contact Details */}
          <div className="space-y-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 flex items-center gap-1.5">
              <Building2 className="w-3.5 h-3.5" /> 2. Official Contact Information
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-xs">
              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Official Email Address *
                </label>
                <input
                  type="email"
                  {...register('officialEmail')}
                  disabled={isEditing}
                  placeholder="a.vance@techknife.com"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none disabled:opacity-60"
                />
                {errors.officialEmail && (
                  <p className="text-[11px] text-rose-500 mt-1">{errors.officialEmail.message}</p>
                )}
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Personal Email
                </label>
                <input
                  type="email"
                  {...register('personalEmail')}
                  placeholder="alex.vance@gmail.com"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                />
                {errors.personalEmail && (
                  <p className="text-[11px] text-rose-500 mt-1">{errors.personalEmail.message}</p>
                )}
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Primary Mobile Number *
                </label>
                <input
                  type="text"
                  {...register('primaryMobile')}
                  placeholder="+1 (555) 019-2834"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                />
                {errors.primaryMobile && (
                  <p className="text-[11px] text-rose-500 mt-1">{errors.primaryMobile.message}</p>
                )}
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Alternate Mobile Number
                </label>
                <input
                  type="text"
                  {...register('alternateMobile')}
                  placeholder="+1 (555) 019-2835"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                />
              </div>
            </div>
          </div>

          {/* Section 3: Organizational Details */}
          <div className="space-y-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 flex items-center gap-1.5">
              <Shield className="w-3.5 h-3.5" /> 3. Department, Role & Salary
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 text-xs">
              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Department *
                </label>
                <select
                  {...register('departmentId')}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                >
                  <option value="Executive Leadership">Executive Leadership</option>
                  <option value="Engineering & DevOps">Engineering & DevOps</option>
                  <option value="Product Management">Product Management</option>
                  <option value="Client Growth & CRM">Client Growth & CRM</option>
                  <option value="Human Resources">Human Resources</option>
                  <option value="Quality Assurance">Quality Assurance</option>
                </select>
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Designation *
                </label>
                <input
                  type="text"
                  {...register('designationId')}
                  placeholder="e.g. Senior Frontend Lead"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                />
                {errors.designationId && (
                  <p className="text-[11px] text-rose-500 mt-1">{errors.designationId.message}</p>
                )}
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Manager ID / Code
                </label>
                <input
                  type="text"
                  {...register('managerId')}
                  placeholder="e.g. EMP-1001"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none font-mono"
                />
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Joining Date *
                </label>
                <input
                  type="date"
                  {...register('joiningDate')}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                />
                {errors.joiningDate && (
                  <p className="text-[11px] text-rose-500 mt-1">{errors.joiningDate.message}</p>
                )}
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Employment Type *
                </label>
                <select
                  {...register('employmentType')}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                >
                  <option value="FULL_TIME">Full Time</option>
                  <option value="PART_TIME">Part Time</option>
                  <option value="CONTRACT">Contract</option>
                  <option value="PROBATION">Probation</option>
                  <option value="TEMPORARY">Temporary</option>
                </select>
              </div>

              <div>
                <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Annual Salary ($) *
                </label>
                <input
                  type="number"
                  {...register('salary', { valueAsNumber: true })}
                  placeholder="120000"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
                />
                {errors.salary && (
                  <p className="text-[11px] text-rose-500 mt-1">{errors.salary.message}</p>
                )}
              </div>
            </div>
          </div>

          {/* Section 4: Social & Extras */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-xs">
            <div>
              <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                GitHub Username
              </label>
              <input
                type="text"
                {...register('githubUsername')}
                placeholder="alexvance-tk"
                className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
              />
            </div>

            <div>
              <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Profile Avatar URL
              </label>
              <input
                type="text"
                {...register('profileImage')}
                placeholder="https://images.unsplash.com/..."
                className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none"
              />
            </div>
          </div>

          {/* Footer Action Buttons */}
          <div className="pt-4 border-t border-slate-200 dark:border-slate-800 flex items-center justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold rounded-xl transition-all shadow-md flex items-center gap-1.5 disabled:opacity-50"
            >
              {isSubmitting ? (
                <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
              ) : (
                <Save className="w-4 h-4" />
              )}
              <span>{isEditing ? 'Save Changes' : 'Complete Onboarding'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
