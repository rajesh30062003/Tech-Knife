import React, { useState } from 'react';
import {
  DollarSign,
  Download,
  FileText,
  Lock,
  CheckCircle2,
  Calendar,
  X,
  Printer,
  ShieldCheck,
  TrendingUp,
} from 'lucide-react';
import { UserProfile } from '../../types';

interface PayslipItem {
  id: string;
  month: string;
  basicPay: number;
  hra: number;
  specialAllowance: number;
  taxDeduction: number;
  pfDeduction: number;
  netPay: number;
  disbursementDate: string;
  status: 'Disbursed' | 'Processing';
}

const MOCK_PAYSLIPS: PayslipItem[] = [
  {
    id: 'SLIP-2026-09',
    month: 'September 2026',
    basicPay: 8500,
    hra: 1200,
    specialAllowance: 1000,
    taxDeduction: 1850,
    pfDeduction: 600,
    netPay: 8250,
    disbursementDate: 'Oct 01, 2026',
    status: 'Disbursed',
  },
  {
    id: 'SLIP-2026-08',
    month: 'August 2026',
    basicPay: 8500,
    hra: 1200,
    specialAllowance: 800,
    taxDeduction: 1750,
    pfDeduction: 600,
    netPay: 8150,
    disbursementDate: 'Sep 01, 2026',
    status: 'Disbursed',
  },
  {
    id: 'SLIP-2026-07',
    month: 'July 2026',
    basicPay: 8500,
    hra: 1200,
    specialAllowance: 1500,
    taxDeduction: 1900,
    pfDeduction: 600,
    netPay: 8700,
    disbursementDate: 'Aug 01, 2026',
    status: 'Disbursed',
  },
  {
    id: 'SLIP-2026-06',
    month: 'June 2026',
    basicPay: 8500,
    hra: 1200,
    specialAllowance: 1000,
    taxDeduction: 1850,
    pfDeduction: 600,
    netPay: 8250,
    disbursementDate: 'Jul 01, 2026',
    status: 'Disbursed',
  },
];

interface SalaryHistoryTabProps {
  user: UserProfile;
}

export const SalaryHistoryTab: React.FC<SalaryHistoryTabProps> = ({ user }) => {
  const [selectedSlip, setSelectedSlip] = useState<PayslipItem | null>(null);

  const handleDownloadSlip = (slip: PayslipItem) => {
    const textContent = `
============================================================
              TECH KNIFE ENTERPRISE PAYROLL SLIP
============================================================
Employee Name   : ${user.firstName} ${user.lastName}
Employee ID     : ${user.id}
Designation     : ${user.designation || 'Senior Full Stack Engineer'}
Department      : ${user.department || 'Engineering'}
Pay Period      : ${slip.month}
Disbursement Date: ${slip.disbursementDate}
Status          : ${slip.status}
------------------------------------------------------------
EARNINGS BREAKDOWN:
  Basic Salary          : $${slip.basicPay.toLocaleString()}.00
  House Rent Allowance  : $${slip.hra.toLocaleString()}.00
  Special Allowances    : $${slip.specialAllowance.toLocaleString()}.00
  TOTAL EARNINGS        : $${(slip.basicPay + slip.hra + slip.specialAllowance).toLocaleString()}.00

DEDUCTIONS:
  Income Tax (PAYE)     : $${slip.taxDeduction.toLocaleString()}.00
  Provident Fund / 401K : $${slip.pfDeduction.toLocaleString()}.00
  TOTAL DEDUCTIONS      : $${(slip.taxDeduction + slip.pfDeduction).toLocaleString()}.00
------------------------------------------------------------
NET PAY DISBURSED       : $${slip.netPay.toLocaleString()}.00 USD
============================================================
Verified Digital Seal: TechKnife-Payroll-HMAC-2026-SECURED
`;

    const blob = new Blob([textContent], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `TechKnife_Payslip_${slip.month.replace(/\s+/g, '_')}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="space-y-8">
      {/* Read-Only Salary Banner */}
      <div className="bg-gradient-to-r from-emerald-900/90 via-slate-900 to-indigo-950 border border-emerald-500/30 rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6 relative overflow-hidden">
        <div className="space-y-2 max-w-2xl relative z-10">
          <div className="flex items-center gap-2 text-emerald-400 font-bold text-xs uppercase tracking-wider">
            <ShieldCheck className="w-4 h-4" />
            <span>Compensation & Salary History</span>
            <span className="px-2 py-0.5 bg-amber-500/20 text-amber-300 border border-amber-500/30 text-[10px] rounded-full flex items-center gap-1 font-mono">
              <Lock className="w-3 h-3" /> Read-Only
            </span>
          </div>
          <h3 className="text-2xl font-black">
            ${(user.salary || 135000).toLocaleString()} USD <span className="text-sm font-normal text-slate-300">/ year</span>
          </h3>
          <p className="text-xs text-slate-300 leading-relaxed">
            Your compensation package is administered by Tech Knife HR & Payroll. Salary changes are executed exclusively by authorized executive personnel.
          </p>
        </div>

        <button
          onClick={() => handleDownloadSlip(MOCK_PAYSLIPS[0])}
          className="px-5 py-3 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs rounded-2xl shadow-lg transition-transform hover:scale-105 flex items-center gap-2 shrink-0 relative z-10"
        >
          <Download className="w-4 h-4" /> Download Latest Payslip
        </button>
      </div>

      {/* Salary History Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-lg">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <h3 className="text-base font-bold text-slate-900 dark:text-white flex items-center gap-2">
              <FileText className="w-4 h-4 text-emerald-500" />
              Monthly Direct Deposit Slips
            </h3>
            <p className="text-xs text-slate-500">Historical record of monthly salary disbursements and tax deductions</p>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600 dark:text-slate-300">
            <thead className="bg-slate-50 dark:bg-slate-800/60 uppercase font-bold text-slate-400 text-[10px] tracking-wider">
              <tr>
                <th className="py-3.5 px-4">Pay Period</th>
                <th className="py-3.5 px-4">Basic Pay</th>
                <th className="py-3.5 px-4">Allowances</th>
                <th className="py-3.5 px-4">Deductions</th>
                <th className="py-3.5 px-4">Net Disbursed</th>
                <th className="py-3.5 px-4">Disbursement Date</th>
                <th className="py-3.5 px-4">Status</th>
                <th className="py-3.5 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800 font-medium">
              {MOCK_PAYSLIPS.map((slip) => {
                const totalAllowances = slip.hra + slip.specialAllowance;
                const totalDeductions = slip.taxDeduction + slip.pfDeduction;

                return (
                  <tr key={slip.id} className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="py-4 px-4 font-bold text-slate-900 dark:text-white flex items-center gap-2">
                      <Calendar className="w-3.5 h-3.5 text-indigo-500" />
                      {slip.month}
                    </td>
                    <td className="py-4 px-4 font-mono">${slip.basicPay.toLocaleString()}</td>
                    <td className="py-4 px-4 font-mono text-emerald-600 dark:text-emerald-400">
                      +${totalAllowances.toLocaleString()}
                    </td>
                    <td className="py-4 px-4 font-mono text-rose-500">
                      -${totalDeductions.toLocaleString()}
                    </td>
                    <td className="py-4 px-4 font-mono font-extrabold text-slate-900 dark:text-white text-sm">
                      ${slip.netPay.toLocaleString()}
                    </td>
                    <td className="py-4 px-4 text-slate-500">{slip.disbursementDate}</td>
                    <td className="py-4 px-4">
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-emerald-500/10 text-emerald-500 border border-emerald-500/20">
                        <CheckCircle2 className="w-3 h-3" /> Disbursed
                      </span>
                    </td>
                    <td className="py-4 px-4 text-right space-x-2">
                      <button
                        onClick={() => setSelectedSlip(slip)}
                        className="px-3 py-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-[11px] rounded-xl transition-colors"
                      >
                        View Slip
                      </button>
                      <button
                        onClick={() => handleDownloadSlip(slip)}
                        className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-[11px] rounded-xl transition-colors inline-flex items-center gap-1"
                      >
                        <Download className="w-3 h-3" /> PDF
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Interactive Payslip Detail Modal */}
      {selectedSlip && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 sm:p-8 max-w-xl w-full shadow-2xl space-y-6 relative animate-scaleIn">
            <button
              onClick={() => setSelectedSlip(null)}
              className="absolute top-5 right-5 p-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl bg-slate-100 dark:bg-slate-800 transition-colors"
            >
              <X className="w-4 h-4" />
            </button>

            <div className="border-b border-slate-100 dark:border-slate-800 pb-4">
              <span className="text-[10px] font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 block mb-1">
                Official Compensation Statement
              </span>
              <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
                Payslip: {selectedSlip.month}
              </h3>
              <p className="text-xs text-slate-500">
                Disbursed to {user.firstName} {user.lastName} on {selectedSlip.disbursementDate}
              </p>
            </div>

            {/* Breakdown Table */}
            <div className="space-y-4">
              <div className="p-4 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-200/60 dark:border-slate-800 space-y-2 text-xs">
                <div className="flex justify-between font-bold text-slate-400 uppercase text-[10px] border-b border-slate-200/60 dark:border-slate-700/60 pb-1">
                  <span>Earnings Category</span>
                  <span>Amount ($)</span>
                </div>
                <div className="flex justify-between font-medium">
                  <span className="text-slate-700 dark:text-slate-300">Basic Base Salary</span>
                  <span className="font-mono">${selectedSlip.basicPay.toLocaleString()}</span>
                </div>
                <div className="flex justify-between font-medium">
                  <span className="text-slate-700 dark:text-slate-300">House Rent Allowance (HRA)</span>
                  <span className="font-mono text-emerald-600 dark:text-emerald-400">+${selectedSlip.hra.toLocaleString()}</span>
                </div>
                <div className="flex justify-between font-medium">
                  <span className="text-slate-700 dark:text-slate-300">Special Technical Allowances</span>
                  <span className="font-mono text-emerald-600 dark:text-emerald-400">+${selectedSlip.specialAllowance.toLocaleString()}</span>
                </div>
              </div>

              <div className="p-4 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-200/60 dark:border-slate-800 space-y-2 text-xs">
                <div className="flex justify-between font-bold text-slate-400 uppercase text-[10px] border-b border-slate-200/60 dark:border-slate-700/60 pb-1">
                  <span>Deductions</span>
                  <span>Amount ($)</span>
                </div>
                <div className="flex justify-between font-medium">
                  <span className="text-slate-700 dark:text-slate-300">Income Tax Withheld (PAYE)</span>
                  <span className="font-mono text-rose-500">-${selectedSlip.taxDeduction.toLocaleString()}</span>
                </div>
                <div className="flex justify-between font-medium">
                  <span className="text-slate-700 dark:text-slate-300">Provident Fund / Retirement 401K</span>
                  <span className="font-mono text-rose-500">-${selectedSlip.pfDeduction.toLocaleString()}</span>
                </div>
              </div>

              {/* Total Net Disbursed */}
              <div className="p-4 bg-indigo-600 text-white rounded-2xl flex items-center justify-between shadow-lg">
                <div>
                  <span className="text-[10px] font-bold uppercase tracking-wider block opacity-80">Net Direct Deposit</span>
                  <span className="text-2xl font-black font-mono">${selectedSlip.netPay.toLocaleString()} USD</span>
                </div>
                <span className="px-3 py-1 bg-white/20 text-white font-bold text-xs rounded-xl backdrop-blur-sm">
                  Verified Paid
                </span>
              </div>
            </div>

            <div className="flex gap-3 pt-2">
              <button
                onClick={() => window.print()}
                className="flex-1 py-2.5 px-4 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-colors flex items-center justify-center gap-2"
              >
                <Printer className="w-4 h-4" /> Print Statement
              </button>
              <button
                onClick={() => handleDownloadSlip(selectedSlip)}
                className="flex-1 py-2.5 px-4 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs rounded-xl shadow transition-colors flex items-center justify-center gap-2"
              >
                <Download className="w-4 h-4" /> Download Text Slip
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
