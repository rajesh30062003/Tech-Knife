import React, { useState } from 'react';
import { Download, FileSpreadsheet, FileCode, Printer, X, CheckCircle } from 'lucide-react';
import { EmployeeData } from '../../api/employees';

interface EmployeeExportModalProps {
  isOpen: boolean;
  onClose: () => void;
  employees: EmployeeData[];
}

export const EmployeeExportModal: React.FC<EmployeeExportModalProps> = ({ isOpen, onClose, employees }) => {
  const [exportFormat, setExportFormat] = useState<'csv' | 'json' | 'print'>('csv');
  const [downloaded, setDownloaded] = useState(false);

  if (!isOpen) return null;

  const handleExport = () => {
    if (exportFormat === 'csv') {
      const headers = ['ID', 'First Name', 'Last Name', 'Email', 'Phone', 'Department', 'Designation', 'Role', 'Status', 'Salary', 'Join Date'];
      const rows = employees.map(e => [
        e.id,
        `"${e.firstName}"`,
        `"${e.lastName}"`,
        `"${e.email}"`,
        `"${e.phone}"`,
        `"${e.department}"`,
        `"${e.designation}"`,
        `"${e.role}"`,
        `"${e.status}"`,
        e.salary,
        `"${e.joinDate}"`
      ]);

      const csvContent = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `TechKnife_Employees_${new Date().toISOString().split('T')[0]}.csv`;
      a.click();
      URL.revokeObjectURL(url);
    } else if (exportFormat === 'json') {
      const jsonStr = JSON.stringify(employees, null, 2);
      const blob = new Blob([jsonStr], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `TechKnife_Employees_${new Date().toISOString().split('T')[0]}.json`;
      a.click();
      URL.revokeObjectURL(url);
    } else if (exportFormat === 'print') {
      window.print();
    }

    setDownloaded(true);
    setTimeout(() => {
      setDownloaded(false);
      onClose();
    }, 1200);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-2xl animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-bold text-sm">
            <Download className="w-4 h-4" />
            <span>Export Staff Directory</span>
          </div>
          <button onClick={onClose} className="p-1 text-slate-400 hover:text-slate-600">
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="space-y-3">
          <p className="text-xs text-slate-500">
            Export <strong className="text-slate-800 dark:text-slate-200">{employees.length} employee records</strong> for compliance, auditing, or spreadsheet processing.
          </p>

          <div className="grid grid-cols-3 gap-2 pt-1">
            <button
              type="button"
              onClick={() => setExportFormat('csv')}
              className={`p-3 rounded-xl border text-center transition-all ${
                exportFormat === 'csv'
                  ? 'border-indigo-600 bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 font-bold shadow-xs'
                  : 'border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 text-slate-600 dark:text-slate-300'
              }`}
            >
              <FileSpreadsheet className="w-5 h-5 mx-auto mb-1 text-emerald-500" />
              <span className="text-xs">CSV Excel</span>
            </button>

            <button
              type="button"
              onClick={() => setExportFormat('json')}
              className={`p-3 rounded-xl border text-center transition-all ${
                exportFormat === 'json'
                  ? 'border-indigo-600 bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 font-bold shadow-xs'
                  : 'border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 text-slate-600 dark:text-slate-300'
              }`}
            >
              <FileCode className="w-5 h-5 mx-auto mb-1 text-indigo-500" />
              <span className="text-xs">JSON Data</span>
            </button>

            <button
              type="button"
              onClick={() => setExportFormat('print')}
              className={`p-3 rounded-xl border text-center transition-all ${
                exportFormat === 'print'
                  ? 'border-indigo-600 bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 font-bold shadow-xs'
                  : 'border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 text-slate-600 dark:text-slate-300'
              }`}
            >
              <Printer className="w-5 h-5 mx-auto mb-1 text-amber-500" />
              <span className="text-xs">Print View</span>
            </button>
          </div>
        </div>

        <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100 dark:border-slate-800">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={handleExport}
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center gap-2"
          >
            {downloaded ? (
              <>
                <CheckCircle className="w-4 h-4 text-emerald-300" />
                Export Generated!
              </>
            ) : (
              <>
                <Download className="w-3.5 h-3.5" />
                Export {exportFormat.toUpperCase()}
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
