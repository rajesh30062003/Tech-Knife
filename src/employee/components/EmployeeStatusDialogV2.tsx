import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { X, ShieldAlert, CheckCircle2 } from 'lucide-react';
import {
  updateEmployeeStatusSchema,
  UpdateEmployeeStatusFormValues,
} from '../schemas/employeeSchema';
import { EmployeeResponse, EmployeeStatus } from '../types/employeeV2';

interface EmployeeStatusDialogV2Props {
  isOpen: boolean;
  employee: EmployeeResponse | null;
  onClose: () => void;
  onSubmit: (status: EmployeeStatus, reason?: string) => Promise<void>;
  isSubmitting?: boolean;
}

export const EmployeeStatusDialogV2: React.FC<EmployeeStatusDialogV2Props> = ({
  isOpen,
  employee,
  onClose,
  onSubmit,
  isSubmitting = false,
}) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<UpdateEmployeeStatusFormValues>({
    resolver: zodResolver(updateEmployeeStatusSchema),
    defaultValues: {
      status: 'ACTIVE',
      statusReason: '',
    },
  });

  useEffect(() => {
    if (employee) {
      reset({
        status: employee.status,
        statusReason: '',
      });
    }
  }, [employee, reset, isOpen]);

  if (!isOpen || !employee) return null;

  const handleFormSubmit = async (data: UpdateEmployeeStatusFormValues) => {
    await onSubmit(data.status, data.statusReason);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-in fade-in duration-200">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-md shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between p-5 border-b border-slate-200 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-800/40">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-amber-50 dark:bg-amber-950/60 text-amber-600 dark:text-amber-400">
              <ShieldAlert className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-slate-900 dark:text-white">
                Update Operational Status
              </h3>
              <p className="text-[11px] text-slate-500">
                Staff member: <span className="font-semibold text-slate-700 dark:text-slate-300">{employee.fullName}</span>
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={handleSubmit(handleFormSubmit)} className="p-5 space-y-4 text-xs">
          <div>
            <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
              Select Target Status *
            </label>
            <select
              {...register('status')}
              className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 font-semibold focus:ring-2 focus:ring-indigo-500/30 outline-none"
            >
              <option value="ACTIVE">ACTIVE - Fully Operational</option>
              <option value="INACTIVE">INACTIVE - On Leave / Inactive</option>
              <option value="SUSPENDED">SUSPENDED - Temporary Hold</option>
              <option value="TERMINATED">TERMINATED - Offboarded</option>
              <option value="RESIGNED">RESIGNED - Voluntary Separation</option>
            </select>
          </div>

          <div>
            <label className="block font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
              Status Change Reason / Notes
            </label>
            <textarea
              {...register('statusReason')}
              rows={3}
              placeholder="Provide context or official authorization note for this status change..."
              className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500/30 outline-none resize-none"
            />
            {errors.statusReason && (
              <p className="text-[11px] text-rose-500 mt-1">{errors.statusReason.message}</p>
            )}
          </div>

          {/* Action Footer */}
          <div className="pt-3 border-t border-slate-200 dark:border-slate-800 flex items-center justify-end gap-2.5">
            <button
              type="button"
              onClick={onClose}
              className="px-3.5 py-2 font-semibold text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl transition-all shadow-md flex items-center gap-1.5 disabled:opacity-50"
            >
              {isSubmitting ? (
                <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
              ) : (
                <CheckCircle2 className="w-4 h-4" />
              )}
              <span>Update Status</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
