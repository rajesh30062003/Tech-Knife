import React, { useState } from 'react';
import { DollarSign, Download, FileText, CheckCircle2, Shield, ArrowUpRight, Check, AlertCircle } from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useAuth } from '../../context/AuthContext';
import { canApprovePayroll, canChangeSalary } from '../../utils/rbac';

export const PayrollPage: React.FC = () => {
  const { user } = useAuth();
  const [payrollBatches, setPayrollBatches] = useState([
    { id: 'PAY-2026-10', month: 'October 2026', totalEmployees: 48, totalAmount: '$384,500.00', status: 'Pending' },
    { id: 'PAY-2026-09', month: 'September 2026', totalEmployees: 47, totalAmount: '$376,000.00', status: 'Disbursed' },
  ]);

  const [personalSlips] = useState([
    { month: 'September 2026', basic: 8500, bonus: 1200, tax: 1850, net: 7850, date: 'Disbursed Oct 01', status: 'Disbursed' },
    { month: 'August 2026', basic: 8500, bonus: 800, tax: 1750, net: 7550, date: 'Disbursed Sep 01', status: 'Disbursed' },
    { month: 'July 2026', basic: 8500, bonus: 1500, tax: 1900, net: 8100, date: 'Disbursed Aug 01', status: 'Disbursed' },
  ]);

  const handleApproveBatch = (id: string) => {
    setPayrollBatches(payrollBatches.map(p => p.id === id ? { ...p, status: 'Disbursed' } : p));
  };

  const handleDownloadSlip = (month: string) => {
    const dummyContent = `TECH KNIFE ENTERPRISE PAYROLL SLIP\nEmployee: ${user?.firstName} ${user?.lastName}\nMonth: ${month}\nStatus: Verified Disbursed\nDesignation: ${user?.designation}`;
    const blob = new Blob([dummyContent], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Payslip-${month.replace(' ', '-')}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-emerald-600 dark:text-emerald-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <DollarSign className="w-4 h-4" />
            <span>Financial Compensation & Disbursements</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Payroll & Direct Slips</h1>
          <p className="text-xs text-slate-500">Monthly compensation breakdowns, tax deductions, and verified direct deposit slips</p>
        </div>

        <button 
          onClick={() => handleDownloadSlip('Current-Year')}
          className="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md"
        >
          <Download className="w-3.5 h-3.5" /> Download Tax Year W2 Form
        </button>
      </div>

      {/* Admin/Manager Approval Section */}
      {canApprovePayroll(user) && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <div>
              <h3 className="font-bold text-base text-slate-900 dark:text-white">Corporate Payroll Approval Queue</h3>
              <p className="text-xs text-slate-500">Authorize monthly direct deposit disbursements for staff</p>
            </div>
            <Shield className="w-4 h-4 text-emerald-500" />
          </div>

          <div className="space-y-3">
            {payrollBatches.map((batch) => (
              <div key={batch.id} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="font-mono text-xs font-bold text-slate-400">{batch.id}</span>
                    <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{batch.month}</span>
                    <StatusBadge status={batch.status} />
                  </div>
                  <p className="text-[11px] text-slate-500">
                    {batch.totalEmployees} Employees • Total Disbursal: <strong className="text-slate-700 dark:text-slate-300">{batch.totalAmount}</strong>
                  </p>
                </div>

                <div className="flex items-center justify-end gap-3">
                  {batch.status === 'Pending' ? (
                    <button
                      onClick={() => handleApproveBatch(batch.id)}
                      className="px-4 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-xs rounded-xl transition-all shadow-xs flex items-center gap-1.5"
                    >
                      <Check className="w-3.5 h-3.5" /> Authorize Payroll Disbursal
                    </button>
                  ) : (
                    <span className="text-xs font-semibold text-emerald-600 dark:text-emerald-400 flex items-center gap-1">
                      <CheckCircle2 className="w-3.5 h-3.5" /> Disbursed
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Personal Payslips */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
          <h3 className="font-bold text-base text-slate-900 dark:text-white">My Monthly Disbursed Payslips</h3>
          <span className="text-xs text-slate-400 font-mono">Direct Deposit: Chase ****2834</span>
        </div>

        <div className="space-y-3">
          {personalSlips.map((slip) => (
            <div key={slip.month} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{slip.month}</span>
                  <StatusBadge status={slip.status} />
                </div>
                <p className="text-[11px] text-slate-500">
                  Basic: ${slip.basic} • Allowances: ${slip.bonus} • Tax Deductions: ${slip.tax}
                </p>
              </div>

              <div className="flex items-center justify-between md:justify-end gap-6">
                <div className="text-right">
                  <span className="text-xs text-slate-400 block">Net Disbursed</span>
                  <span className="text-base font-bold text-emerald-600 dark:text-emerald-400">${slip.net}.00</span>
                </div>

                <button
                  onClick={() => handleDownloadSlip(slip.month)}
                  className="px-3 py-1.5 bg-indigo-50 dark:bg-indigo-950/60 hover:bg-indigo-100 text-indigo-600 dark:text-indigo-400 font-semibold text-xs rounded-lg transition-colors flex items-center gap-1.5"
                >
                  <Download className="w-3.5 h-3.5" /> PDF
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
