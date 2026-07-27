import React, { useState } from 'react';
import { X, CheckSquare, Calendar, Loader2 } from 'lucide-react';
import { Intern, InternTask } from '../../types';

interface InternTasksModalProps {
  intern: Intern | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (internId: string, task: Partial<InternTask>) => Promise<void>;
}

export const InternTasksModal: React.FC<InternTasksModalProps> = ({
  intern,
  isOpen,
  onClose,
  onSubmit,
}) => {
  const [taskTitle, setTaskTitle] = useState('');
  const [taskType, setTaskType] = useState<'daily' | 'weekly'>('daily');
  const [dueDate, setDueDate] = useState('Tomorrow');
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!isOpen || !intern) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!taskTitle.trim()) return;

    setIsSubmitting(true);
    try {
      await onSubmit(intern.id, {
        title: taskTitle,
        type: taskType,
        dueDate,
      });
      setTaskTitle('');
      onClose();
    } catch (err) {
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-xl">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
          <div className="flex items-center gap-2">
            <CheckSquare className="w-5 h-5 text-cyan-600 dark:text-cyan-400" />
            <h3 className="font-bold text-base text-slate-900 dark:text-white">
              Assign Task to {intern.firstName}
            </h3>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600">
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4 text-xs">
          <div>
            <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Task Title / Sprint Objective *</label>
            <input
              type="text"
              required
              value={taskTitle}
              onChange={(e) => setTaskTitle(e.target.value)}
              placeholder="e.g. Write integration test suite for Auth endpoints"
              className="w-full px-3.5 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Task Scope</label>
              <select
                value={taskType}
                onChange={(e) => setTaskType(e.target.value as 'daily' | 'weekly')}
                className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white"
              >
                <option value="daily">Daily Task</option>
                <option value="weekly">Weekly Milestone</option>
              </select>
            </div>

            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Target Due Date</label>
              <input
                type="text"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
                placeholder="e.g. End of Week"
                className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white"
              />
            </div>
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
              className="px-5 py-2 bg-cyan-600 hover:bg-cyan-500 text-white font-bold rounded-xl shadow flex items-center gap-2"
            >
              {isSubmitting && <Loader2 className="w-3.5 h-3.5 animate-spin" />}
              <span>Assign Task</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
