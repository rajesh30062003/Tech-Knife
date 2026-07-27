import React, { useState } from 'react';
import { X, UserPlus, Briefcase, DollarSign, Loader2 } from 'lucide-react';
import { Intern } from '../../types';

interface InternConvertModalProps {
  intern: Intern | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (internId: string, designation: string, salary: number) => Promise<void>;
}

export const InternConvertModal: React.FC<InternConvertModalProps> = ({
  intern,
  isOpen,
  onClose,
  onSubmit,
}) => {
  const [designation, setDesignation] = useState('Software Engineer');
  const [salary, setSalary] = useState(135000);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!isOpen || !intern) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      await onSubmit(intern.id, designation, salary);
      onClose();
    } catch (err) {
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-md p-6 space-y-4 shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
          <div className="flex items-center gap-2">
            <UserPlus className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white">
              Convert Intern to Full-Time Employee
            </h3>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600">
            <X className="w-4 h-4" />
          </button>
        </div>

        <p className="text-xs text-slate-500">
          Transitioning <strong className="text-slate-900 dark:text-white">{intern.firstName} {intern.lastName}</strong> from internship cohort ({intern.internId}) to official Employee Directory with full corporate benefits & security access.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4 text-xs">
          <div>
            <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Full-Time Job Title / Designation *</label>
            <input
              type="text"
              required
              value={designation}
              onChange={(e) => setDesignation(e.target.value)}
              placeholder="e.g. Software Engineer / Frontend Specialist"
              className="w-full px-3.5 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white"
            />
          </div>

          <div>
            <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Annual Compensation (USD) *</label>
            <input
              type="number"
              required
              min="50000"
              value={salary}
              onChange={(e) => setSalary(Number(e.target.value))}
              className="w-full px-3.5 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white"
            />
          </div>

          <div className="p-3.5 bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-200 dark:border-indigo-800 rounded-2xl text-[11px] text-indigo-900 dark:text-indigo-200">
            This action will mark the intern status as <strong>Converted to Employee</strong> and generate a new Employee record in the organization database.
          </div>

          <div className="flex items-center justify-end gap-2 pt-3 border-t border-slate-100 dark:border-slate-800">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl font-bold"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl shadow flex items-center gap-2"
            >
              {isSubmitting && <Loader2 className="w-3.5 h-3.5 animate-spin" />}
              <span>Convert to Employee</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
