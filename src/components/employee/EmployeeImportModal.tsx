import React, { useState } from 'react';
import { Download, Upload, FileText, CheckCircle2, AlertCircle, X } from 'lucide-react';
import { EmployeeData, employeesApi } from '../../api/employees';

interface EmployeeImportModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const EmployeeImportModal: React.FC<EmployeeImportModalProps> = ({ isOpen, onClose, onSuccess }) => {
  const [fileContent, setFileContent] = useState<Partial<EmployeeData>[]>([]);
  const [fileName, setFileName] = useState('');
  const [isImporting, setIsImporting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  if (!isOpen) return null;

  const handleDownloadSample = () => {
    const sampleData = [
      {
        firstName: 'John',
        lastName: 'Doe',
        email: 'j.doe@techknife.com',
        phone: '+1 (555) 123-4567',
        role: 'ROLE_EMPLOYEE',
        department: 'Engineering & DevOps',
        designation: 'Software Engineer',
        joinDate: '2026-08-01',
        salary: 130000
      },
      {
        firstName: 'Alice',
        lastName: 'Smith',
        email: 'a.smith@techknife.com',
        phone: '+1 (555) 987-6543',
        role: 'ROLE_MANAGER',
        department: 'Product Management',
        designation: 'Product Manager',
        joinDate: '2026-08-15',
        salary: 155000
      }
    ];

    const jsonStr = JSON.stringify(sampleData, null, 2);
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'employee_import_sample.json';
    a.click();
    URL.revokeObjectURL(url);
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    setErrorMsg('');
    const file = e.target.files?.[0];
    if (!file) return;

    setFileName(file.name);

    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const text = event.target?.result as string;
        if (file.name.endsWith('.json')) {
          const parsed = JSON.parse(text);
          if (Array.isArray(parsed)) {
            setFileContent(parsed);
          } else {
            setErrorMsg('JSON file must contain an array of employee objects.');
          }
        } else if (file.name.endsWith('.csv')) {
          // Parse CSV
          const lines = text.split('\n').filter(l => l.trim().length > 0);
          if (lines.length < 2) {
            setErrorMsg('CSV file is empty or missing headers.');
            return;
          }
          const headers = lines[0].split(',').map(h => h.trim().replace(/^"|"$/g, ''));
          const rows: Partial<EmployeeData>[] = [];

          for (let i = 1; i < lines.length; i++) {
            const values = lines[i].split(',').map(v => v.trim().replace(/^"|"$/g, ''));
            const obj: any = {};
            headers.forEach((h, index) => {
              obj[h] = values[index] || '';
            });
            rows.push({
              firstName: obj.firstName || obj.First_Name || 'Imported',
              lastName: obj.lastName || obj.Last_Name || 'User',
              email: obj.email || obj.Email || `emp.${Date.now()}@techknife.com`,
              phone: obj.phone || '+1 (555) 000-0000',
              role: obj.role || 'ROLE_EMPLOYEE',
              department: obj.department || 'Engineering & DevOps',
              designation: obj.designation || 'Software Engineer',
              joinDate: obj.joinDate || new Date().toISOString().split('T')[0],
              salary: Number(obj.salary) || 120000
            });
          }
          setFileContent(rows);
        } else {
          setErrorMsg('Unsupported file format. Please upload .json or .csv');
        }
      } catch (err) {
        setErrorMsg('Failed to parse file. Please check formatting.');
      }
    };
    reader.readAsText(file);
  };

  const handleExecuteImport = async () => {
    if (fileContent.length === 0) return;
    setIsImporting(true);
    try {
      await employeesApi.importEmployees(fileContent);
      setIsImporting(false);
      onSuccess();
      onClose();
    } catch {
      setIsImporting(false);
      setErrorMsg('Error executing employee batch import.');
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-lg p-6 space-y-4 shadow-2xl animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-bold text-sm">
            <Upload className="w-4 h-4" />
            <span>Batch Import Staff Directory</span>
          </div>
          <button onClick={onClose} className="p-1 text-slate-400 hover:text-slate-600">
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="space-y-3">
          <p className="text-xs text-slate-500">
            Upload a JSON or CSV file containing employee records to bulk register new staff members into Tech Knife Enterprise directory.
          </p>

          <button
            onClick={handleDownloadSample}
            className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-700 dark:text-slate-200 font-semibold text-xs rounded-xl transition-colors"
          >
            <Download className="w-3.5 h-3.5" /> Download JSON Sample Format
          </button>

          {/* File dropzone */}
          <div className="relative border-2 border-dashed border-slate-300 dark:border-slate-700 hover:border-indigo-500 rounded-2xl p-6 text-center space-y-2 bg-slate-50 dark:bg-slate-800/40 transition-colors">
            <input
              type="file"
              accept=".json,.csv"
              onChange={handleFileUpload}
              className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
            />
            <FileText className="w-8 h-8 mx-auto text-indigo-500" />
            <div className="text-xs text-slate-600 dark:text-slate-300 font-semibold">
              {fileName ? (
                <span className="text-indigo-600 dark:text-indigo-400 font-bold">{fileName}</span>
              ) : (
                'Click or drag JSON / CSV file here'
              )}
            </div>
          </div>

          {errorMsg && (
            <div className="p-3 bg-rose-50 dark:bg-rose-950/50 text-rose-700 dark:text-rose-300 rounded-xl text-xs flex items-center gap-2 border border-rose-200 dark:border-rose-800">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{errorMsg}</span>
            </div>
          )}

          {fileContent.length > 0 && !errorMsg && (
            <div className="p-3 bg-emerald-50 dark:bg-emerald-950/50 text-emerald-800 dark:text-emerald-300 rounded-xl text-xs flex items-center justify-between border border-emerald-200 dark:border-emerald-800">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
                <span className="font-bold">Validated {fileContent.length} employee records ready for import.</span>
              </div>
            </div>
          )}
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
            disabled={fileContent.length === 0 || isImporting}
            onClick={handleExecuteImport}
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center gap-2"
          >
            {isImporting ? 'Importing Records...' : `Import ${fileContent.length} Staff Members`}
          </button>
        </div>
      </div>
    </div>
  );
};
