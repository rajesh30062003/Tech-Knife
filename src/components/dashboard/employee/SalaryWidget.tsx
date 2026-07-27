import React from 'react';
import { DollarSign, Download, Lock, CheckCircle2, ShieldCheck, ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export const SalaryWidget: React.FC = () => {
  const handleDownloadLatestPayslip = () => {
    const textContent = `
============================================================
              TECH KNIFE ENTERPRISE PAYROLL SLIP
============================================================
Pay Period      : September 2026
Disbursement Date: Oct 01, 2026
Status          : Disbursed & Verified
------------------------------------------------------------
EARNINGS BREAKDOWN:
  Basic Base Salary     : $8,500.00
  House Rent Allowance  : $1,200.00
  Special Allowances    : $1,000.00
  TOTAL EARNINGS        : $10,700.00

DEDUCTIONS:
  Income Tax (PAYE)     : $1,850.00
  401K / Provident Fund : $600.00
  TOTAL DEDUCTIONS      : $2,450.00
------------------------------------------------------------
NET PAY DISBURSED       : $8,250.00 USD
============================================================
Digital HMAC Seal: TechKnife-Payroll-Verified-2026
`;

    const blob = new Blob([textContent], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `TechKnife_Payslip_Sep_2026.txt`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-5 flex flex-col justify-between h-full transition-all hover:border-indigo-500/30">
      {/* Widget Header */}
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400">
            <DollarSign className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Compensation & Salary</h3>
            <p className="text-[11px] text-slate-500">Verified Direct Deposit Disbursements</p>
          </div>
        </div>

        <Link
          to="/payroll"
          className="text-xs font-bold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center gap-1"
        >
          All Slips <ChevronRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      {/* Main Net Pay Display */}
      <div className="p-4 rounded-2xl bg-gradient-to-br from-emerald-900 via-slate-900 to-indigo-950 text-white space-y-3 shadow-md relative overflow-hidden">
        <div className="flex items-center justify-between text-xs">
          <span className="text-emerald-400 font-bold uppercase tracking-wider text-[10px] flex items-center gap-1">
            <ShieldCheck className="w-3.5 h-3.5" /> Direct Deposit Active
          </span>
          <span className="px-2 py-0.5 bg-emerald-500/20 text-emerald-300 text-[10px] font-mono font-bold rounded-full border border-emerald-500/30">
            Oct 01, 2026 Paid
          </span>
        </div>

        <div>
          <span className="text-[11px] text-slate-300 block font-medium">Last Disbursed Net Salary</span>
          <div className="text-3xl font-black font-mono tracking-tight text-white mt-0.5">
            $8,250.00 <span className="text-xs font-normal text-slate-300">/ mo</span>
          </div>
        </div>

        <div className="pt-2 border-t border-white/10 flex justify-between items-center text-[11px] text-slate-300">
          <span>Annual Base Package: <strong>$135,000/yr</strong></span>
          <span className="flex items-center gap-1 text-emerald-400 font-bold">
            <CheckCircle2 className="w-3.5 h-3.5" /> Verified
          </span>
        </div>
      </div>

      {/* Salary Breakdown Cards */}
      <div className="grid grid-cols-2 gap-3 text-xs">
        <div className="p-3 bg-slate-50 dark:bg-slate-950/60 rounded-2xl border border-slate-200/80 dark:border-slate-800">
          <span className="text-[10px] font-bold text-slate-400 uppercase">Gross Basic Component</span>
          <div className="text-sm font-extrabold text-slate-900 dark:text-white font-mono mt-0.5">$8,500.00</div>
        </div>

        <div className="p-3 bg-slate-50 dark:bg-slate-950/60 rounded-2xl border border-slate-200/80 dark:border-slate-800">
          <span className="text-[10px] font-bold text-slate-400 uppercase">Tax & 401K Deductions</span>
          <div className="text-sm font-extrabold text-rose-500 font-mono mt-0.5">-$2,450.00</div>
        </div>
      </div>

      {/* Action CTA */}
      <button
        onClick={handleDownloadLatestPayslip}
        className="w-full py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs rounded-xl shadow transition-colors flex items-center justify-center gap-2"
      >
        <Download className="w-4 h-4" /> Download Latest Payslip (PDF)
      </button>
    </div>
  );
};
