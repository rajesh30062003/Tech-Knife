import React, { useState } from 'react';
import {
  FileSpreadsheet,
  FileText,
  Download,
  Calendar,
  Filter,
  CheckCircle2,
  Loader2,
  BarChart3,
  Sparkles
} from 'lucide-react';
import { ReportFormat } from '../../types';

interface UniversalReportExporterProps {
  defaultModule?: string;
}

const MODULES = [
  'Employees Directory',
  'Intern Cohort & PPO',
  'Projects & Deliverables',
  'Payroll & Disbursal Ledger',
  'Customer Accounts & Revenue',
  'Attendance & Hours',
  'Support & Service SLA',
];

export const UniversalReportExporter: React.FC<UniversalReportExporterProps> = ({
  defaultModule = 'Employees Directory',
}) => {
  const [selectedModule, setSelectedModule] = useState(defaultModule);
  const [format, setFormat] = useState<ReportFormat>('CSV');
  const [dateFrom, setDateFrom] = useState('2026-01-01');
  const [dateTo, setDateTo] = useState('2026-07-23');
  const [isExporting, setIsExporting] = useState(false);
  const [exportSuccess, setExportSuccess] = useState(false);

  const handleExport = () => {
    setIsExporting(true);
    setExportSuccess(false);

    setTimeout(() => {
      // Mock generated tabular CSV / Report download
      const headers = ['Record_ID', 'Module', 'Generated_At', 'Exported_By', 'Format', 'Status'];
      const rows = [
        ['RPT-2026-01', selectedModule, new Date().toISOString(), 'Executive Admin', format, 'CONFIRMED'],
        ['RPT-2026-02', selectedModule, new Date().toISOString(), 'Executive Admin', format, 'VERIFIED'],
      ];

      const content = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows.map((e) => e.join(','))].join('\n');
      const encodedUri = encodeURI(content);
      const link = document.createElement('a');
      link.setAttribute('href', encodedUri);
      link.setAttribute(
        'download',
        `TechKnife_${selectedModule.replace(/\s+/g, '_')}_${format}_${new Date().toISOString().split('T')[0]}.${format.toLowerCase()}`
      );
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      setIsExporting(false);
      setExportSuccess(true);
      setTimeout(() => setExportSuccess(false), 4000);
    }, 800);
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-5">
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold uppercase text-indigo-600 dark:text-indigo-400">
            <BarChart3 className="w-4 h-4" />
            <span>Universal Analytics & Report Engine</span>
          </div>
          <h3 className="font-extrabold text-base text-slate-900 dark:text-white">Export Executive Reports</h3>
          <p className="text-xs text-slate-500">Generate formatted PDF summaries, Excel spreadsheets, or CSV data exports</p>
        </div>
      </div>

      {exportSuccess && (
        <div className="p-3 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-2xl text-xs text-emerald-800 dark:text-emerald-200 font-bold flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-600" /> Executive report successfully compiled & downloaded!
        </div>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Module Picker */}
        <div>
          <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Target Module *</label>
          <select
            value={selectedModule}
            onChange={(e) => setSelectedModule(e.target.value)}
            className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
          >
            {MODULES.map((m) => (
              <option key={m} value={m}>
                {m}
              </option>
            ))}
          </select>
        </div>

        {/* Format Selector */}
        <div>
          <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Export Format *</label>
          <select
            value={format}
            onChange={(e) => setFormat(e.target.value as ReportFormat)}
            className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
          >
            <option value="CSV">CSV Data File (.csv)</option>
            <option value="EXCEL">Excel Workbook (.xlsx)</option>
            <option value="PDF">Formatted PDF Report (.pdf)</option>
          </select>
        </div>

        {/* Date From */}
        <div>
          <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Date From</label>
          <input
            type="date"
            value={dateFrom}
            onChange={(e) => setDateFrom(e.target.value)}
            className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
          />
        </div>

        {/* Date To */}
        <div>
          <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Date To</label>
          <input
            type="date"
            value={dateTo}
            onChange={(e) => setDateTo(e.target.value)}
            className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
          />
        </div>
      </div>

      <div className="flex items-center justify-end pt-2">
        <button
          onClick={handleExport}
          disabled={isExporting}
          className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 disabled:opacity-50"
        >
          {isExporting ? (
            <Loader2 className="w-4 h-4 animate-spin" />
          ) : (
            <>
              <Download className="w-4 h-4" /> Download Compiled Report
            </>
          )}
        </button>
      </div>
    </div>
  );
};
