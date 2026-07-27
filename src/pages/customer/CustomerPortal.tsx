import React, { useState } from 'react';
import { 
  Building2, FileText, Ticket, DollarSign, Download, Plus, 
  Search, CheckCircle2, AlertCircle, Clock, ShieldCheck, ExternalLink 
} from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useAuth } from '../../context/AuthContext';
import { canRaiseTicket, canTrackProject, canViewInvoice, canDownloadDocuments } from '../../utils/rbac';

export const CustomerPortal: React.FC = () => {
  const { user } = useAuth();
  const [showTicketModal, setShowTicketModal] = useState(false);
  
  const [tickets, setTickets] = useState([
    { id: 'TCK-801', ticketNumber: 'TK-TICK-101', subject: 'Inquire about Phase 2 Migration Cutover', category: 'Infrastructure', priority: 'High', status: 'In Progress', createdAt: '2 hours ago' },
    { id: 'TCK-802', ticketNumber: 'TK-TICK-102', subject: 'Request Additional User Seats for MSA', category: 'Billing', priority: 'Medium', status: 'Resolved', createdAt: '3 days ago' },
  ]);

  const [newTicket, setNewTicket] = useState({
    subject: '',
    category: 'Bug' as const,
    priority: 'Medium' as const,
    details: '',
  });

  const [milestones] = useState([
    { milestone: 'Phase 1: Architecture Blueprint & Security Matrix', budget: '$45,000', status: 'Completed', date: 'Delivered Sep 30' },
    { milestone: 'Phase 2: Core Microservices & Database Sharding', budget: '$65,000', status: 'In Progress', date: 'Est. Completion Nov 15' },
    { milestone: 'Phase 3: User Acceptance Testing & Production Cutover', budget: '$30,000', status: 'Planning', date: 'Est. Completion Dec 20' },
  ]);

  const [invoices] = useState([
    { inv: 'INV-2026-001', desc: 'Phase 1 Milestone Completion', amount: '$45,000.00', status: 'Disbursed', date: 'Paid Oct 02' },
    { inv: 'INV-2026-002', desc: 'Monthly Dedicated Dev Retainer', amount: '$15,000.00', status: 'Pending', date: 'Due Oct 31' },
  ]);

  const handleCreateTicket = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTicket.subject) return;

    setTickets([
      {
        id: `TCK-${Date.now().toString().slice(-3)}`,
        ticketNumber: `TK-TICK-${Math.floor(Math.random() * 900) + 100}`,
        subject: newTicket.subject,
        category: newTicket.category,
        priority: newTicket.priority,
        status: 'Open',
        createdAt: 'Just now',
      },
      ...tickets,
    ]);

    setShowTicketModal(false);
    setNewTicket({ subject: '', category: 'Bug', priority: 'Medium', details: '' });
  };

  const handleDownloadInvoice = (invNumber: string) => {
    const dummyBlob = new Blob([`TECH KNIFE ENTERPRISE INVOICE: ${invNumber}\nStatus: Verified Disbursed\nAmount: $45,000.00`], { type: 'text/plain' });
    const url = URL.createObjectURL(dummyBlob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${invNumber}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Building2 className="w-4 h-4" />
            <span>Apex Enterprises Client Self-Service Desk</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Active Contracts, Projects & Tickets</h1>
          <p className="text-xs text-slate-500">Track deliverable progress, raise support tickets, and download verified invoices</p>
        </div>

        {canRaiseTicket(user) && (
          <button
            onClick={() => setShowTicketModal(true)}
            className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md"
          >
            <Plus className="w-3.5 h-3.5" /> Raise Support Ticket
          </button>
        )}
      </div>

      {/* Contracts & Tickets Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        
        {/* Project Milestones */}
        {canTrackProject(user) && (
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
            <div className="border-b border-slate-100 dark:border-slate-800 pb-3 flex items-center justify-between">
              <div>
                <h3 className="font-bold text-base text-slate-900 dark:text-white">Project Deliverable Milestones</h3>
                <p className="text-xs text-slate-500">Enterprise Cloud Modernization Program</p>
              </div>
              <ShieldCheck className="w-4 h-4 text-emerald-500" />
            </div>

            <div className="space-y-3">
              {milestones.map((m, idx) => (
                <div key={idx} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 space-y-1">
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{m.milestone}</span>
                    <StatusBadge status={m.status} />
                  </div>
                  <div className="flex items-center justify-between text-[11px] text-slate-500">
                    <span>Milestone Budget: <strong className="text-slate-700 dark:text-slate-300">{m.budget}</strong></span>
                    <span>{m.date}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Client Invoices */}
        {canViewInvoice(user) && (
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
            <div className="border-b border-slate-100 dark:border-slate-800 pb-3 flex items-center justify-between">
              <div>
                <h3 className="font-bold text-base text-slate-900 dark:text-white">Invoices & Financial Receipts</h3>
                <p className="text-xs text-slate-500">Download verified receipts and billing breakdowns</p>
              </div>
              <DollarSign className="w-4 h-4 text-indigo-500" />
            </div>

            <div className="space-y-3">
              {invoices.map((inv) => (
                <div key={inv.inv} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between gap-4">
                  <div className="space-y-0.5">
                    <div className="flex items-center gap-2">
                      <span className="font-mono text-xs font-bold text-slate-900 dark:text-slate-100">{inv.inv}</span>
                      <StatusBadge status={inv.status} />
                    </div>
                    <p className="text-xs text-slate-600 dark:text-slate-300">{inv.desc}</p>
                    <p className="text-[11px] text-slate-400">{inv.amount} • {inv.date}</p>
                  </div>

                  {canDownloadDocuments(user) && (
                    <button
                      onClick={() => handleDownloadInvoice(inv.inv)}
                      title="Download Invoice PDF"
                      className="p-2 bg-indigo-50 dark:bg-indigo-950/60 hover:bg-indigo-100 text-indigo-600 dark:text-indigo-400 rounded-xl transition-colors border border-indigo-200/50 dark:border-indigo-800/50"
                    >
                      <Download className="w-4 h-4" />
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

      </div>

      {/* Support Tickets Section */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <div className="border-b border-slate-100 dark:border-slate-800 pb-3 flex items-center justify-between">
          <div>
            <h3 className="font-bold text-base text-slate-900 dark:text-white">Support & Service Tickets</h3>
            <p className="text-xs text-slate-500">Track response SLAs from Tech Knife Support Team</p>
          </div>
          <Ticket className="w-4 h-4 text-purple-500" />
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600 dark:text-slate-300">
            <thead className="bg-slate-50 dark:bg-slate-800/60 uppercase font-semibold text-slate-500">
              <tr>
                <th className="py-3 px-4">Ticket Ref</th>
                <th className="py-3 px-4">Subject</th>
                <th className="py-3 px-4">Category</th>
                <th className="py-3 px-4">Priority</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4">Created</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {tickets.map((t) => (
                <tr key={t.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/30">
                  <td className="py-3.5 px-4 font-mono font-bold text-indigo-600 dark:text-indigo-400">{t.ticketNumber}</td>
                  <td className="py-3.5 px-4 font-semibold text-slate-900 dark:text-slate-100">{t.subject}</td>
                  <td className="py-3.5 px-4 text-slate-500">{t.category}</td>
                  <td className="py-3.5 px-4 font-semibold">{t.priority}</td>
                  <td className="py-3.5 px-4">
                    <StatusBadge status={t.status} />
                  </td>
                  <td className="py-3.5 px-4 text-slate-400">{t.createdAt}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal: Raise Support Ticket */}
      {showTicketModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-bold text-base text-slate-900 dark:text-white">Raise Support Ticket</h3>
              <button onClick={() => setShowTicketModal(false)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>

            <form onSubmit={handleCreateTicket} className="space-y-3">
              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Ticket Subject</label>
                <input
                  type="text"
                  required
                  value={newTicket.subject}
                  onChange={(e) => setNewTicket({ ...newTicket, subject: e.target.value })}
                  placeholder="e.g. Question regarding API rate limit cap"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Category</label>
                  <select
                    value={newTicket.category}
                    onChange={(e) => setNewTicket({ ...newTicket, category: e.target.value as any })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                  >
                    <option value="Bug">Bug</option>
                    <option value="Feature Request">Feature Request</option>
                    <option value="Billing">Billing</option>
                    <option value="Access Issue">Access Issue</option>
                    <option value="Infrastructure">Infrastructure</option>
                  </select>
                </div>

                <div>
                  <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Priority</label>
                  <select
                    value={newTicket.priority}
                    onChange={(e) => setNewTicket({ ...newTicket, priority: e.target.value as any })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                  >
                    <option value="Low">Low</option>
                    <option value="Medium">Medium</option>
                    <option value="High">High</option>
                    <option value="Critical">Critical</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Detailed Description</label>
                <textarea
                  rows={3}
                  value={newTicket.details}
                  onChange={(e) => setNewTicket({ ...newTicket, details: e.target.value })}
                  placeholder="Provide context or reproduction steps..."
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white resize-none"
                ></textarea>
              </div>

              <div className="pt-3 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowTicketModal(false)}
                  className="px-3.5 py-1.5 text-xs text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl shadow-md"
                >
                  Submit Ticket
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
