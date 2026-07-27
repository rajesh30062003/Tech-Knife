import React, { useState } from 'react';
import { Building2, Plus, Search, ShieldCheck, Mail, Phone, DollarSign, ArrowUpRight, Ticket, CheckCircle2 } from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useAuth } from '../../context/AuthContext';

interface CustomerClient {
  id: string;
  companyName: string;
  contactPerson: string;
  email: string;
  phone: string;
  plan: string;
  contractValue: string;
  slaHealth: string;
  openTickets: number;
  status: 'Active' | 'Pending Renewal' | 'Onboarding';
}

export const CustomersPage: React.FC = () => {
  const { user } = useAuth();
  const [searchQuery, setSearchQuery] = useState('');
  const [showModal, setShowModal] = useState(false);

  const [customers, setCustomers] = useState<CustomerClient[]>([
    { id: 'CUST-301', companyName: 'Apex Corp International', contactPerson: 'Johnathan Miller', email: 'client@apex.com', phone: '+1 (800) 555-0192', plan: 'Enterprise Platinum Tier', contractValue: '$240,000/yr', slaHealth: '99.98%', openTickets: 1, status: 'Active' },
    { id: 'CUST-302', companyName: 'Starlight Financial Systems', contactPerson: 'Amanda Sterling', email: 'a.sterling@starlight.io', phone: '+1 (800) 555-0144', plan: 'Gold SLA Tier', contractValue: '$180,000/yr', slaHealth: '100%', openTickets: 0, status: 'Active' },
    { id: 'CUST-303', companyName: 'Quantum Nexus Logistics', contactPerson: 'Robert Vance', email: 'r.vance@quantumnexus.com', phone: '+1 (800) 555-0188', plan: 'Enterprise Gold Tier', contractValue: '$150,000/yr', slaHealth: '98.5%', openTickets: 3, status: 'Pending Renewal' },
    { id: 'CUST-304', companyName: 'AeroCloud Dynamics', contactPerson: 'Helen Troy', email: 'h.troy@aerocloud.org', phone: '+1 (800) 555-0111', plan: 'Custom Cloud Architecture', contractValue: '$320,000/yr', slaHealth: '100%', openTickets: 0, status: 'Onboarding' }
  ]);

  const [newCust, setNewCust] = useState({
    companyName: '',
    contactPerson: '',
    email: '',
    phone: '',
    plan: 'Enterprise Platinum Tier',
    contractValue: '$150,000/yr'
  });

  const handleCreateCustomer = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCust.companyName || !newCust.email) return;

    const created: CustomerClient = {
      id: `CUST-${Date.now().toString().slice(-3)}`,
      companyName: newCust.companyName,
      contactPerson: newCust.contactPerson || 'Lead Representative',
      email: newCust.email,
      phone: newCust.phone || '+1 (800) 555-0000',
      plan: newCust.plan,
      contractValue: newCust.contractValue,
      slaHealth: '100%',
      openTickets: 0,
      status: 'Active'
    };

    setCustomers([created, ...customers]);
    setShowModal(false);
    setNewCust({ companyName: '', contactPerson: '', email: '', phone: '', plan: 'Enterprise Platinum Tier', contractValue: '$150,000/yr' });
  };

  const filteredCustomers = customers.filter(c =>
    `${c.companyName} ${c.contactPerson} ${c.email} ${c.plan}`.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Building2 className="w-4 h-4" />
            <span>Enterprise Client Portfolio & SLAs</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Customer Account Management</h1>
          <p className="text-xs text-slate-500">Corporate client accounts, active contracts, SLA metrics, and dedicated portal access</p>
        </div>

        <button
          onClick={() => setShowModal(true)}
          className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md"
        >
          <Plus className="w-3.5 h-3.5" /> Add Enterprise Client
        </button>
      </div>

      {/* SLA Health Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <span className="text-xs text-slate-400 font-bold uppercase">Total Managed Accounts</span>
          <div className="text-2xl font-extrabold text-slate-900 dark:text-white mt-1">{customers.length} Clients</div>
          <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-semibold mt-1">100% Retention Rate</p>
        </div>
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <span className="text-xs text-slate-400 font-bold uppercase">Combined Contract ARR</span>
          <div className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 mt-1">$890,000</div>
          <p className="text-[11px] text-slate-500 mt-1">Annual Recurring Revenue</p>
        </div>
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <span className="text-xs text-slate-400 font-bold uppercase">Systemwide SLA Target</span>
          <div className="text-2xl font-extrabold text-indigo-600 dark:text-indigo-400 mt-1">99.8%</div>
          <p className="text-[11px] text-slate-500 mt-1">Zero Breach In Last 90 Days</p>
        </div>
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <span className="text-xs text-slate-400 font-bold uppercase">Active Open Tickets</span>
          <div className="text-2xl font-extrabold text-amber-500 mt-1">4 Tickets</div>
          <p className="text-[11px] text-slate-500 mt-1">Avg Resolution: 1.4 Hours</p>
        </div>
      </div>

      {/* Directory Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
          <div className="relative flex-1 max-w-md">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Search company, contact person, or plan..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-9 pr-4 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500/50 text-slate-900 dark:text-slate-100"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {filteredCustomers.map((cust) => (
            <div key={cust.id} className="p-5 bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800 rounded-2xl space-y-4">
              <div className="flex items-start justify-between">
                <div>
                  <div className="font-bold text-sm text-slate-900 dark:text-white">{cust.companyName}</div>
                  <div className="text-[11px] font-semibold text-indigo-600 dark:text-indigo-400">{cust.plan}</div>
                </div>
                <StatusBadge status={cust.status === 'Active' ? 'Active' : cust.status === 'Onboarding' ? 'In Progress' : 'Pending'} />
              </div>

              <div className="space-y-1.5 pt-2 border-t border-slate-200/60 dark:border-slate-800 text-xs text-slate-600 dark:text-slate-300">
                <p>Representative: <strong className="text-slate-800 dark:text-slate-200">{cust.contactPerson}</strong></p>
                <div className="flex items-center gap-2">
                  <Mail className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                  <span className="truncate">{cust.email}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Phone className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                  <span>{cust.phone}</span>
                </div>
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-slate-200/60 dark:border-slate-800 text-xs">
                <div>
                  <span className="text-[10px] text-slate-400 block uppercase font-bold">Contract Value</span>
                  <span className="font-bold text-emerald-600 dark:text-emerald-400">{cust.contractValue}</span>
                </div>

                <div className="text-right">
                  <span className="text-[10px] text-slate-400 block uppercase font-bold">SLA Compliance</span>
                  <span className="font-bold text-indigo-600 dark:text-indigo-400">{cust.slaHealth}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-bold text-base text-slate-900 dark:text-white">Add Enterprise Client</h3>
              <button onClick={() => setShowModal(false)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>

            <form onSubmit={handleCreateCustomer} className="space-y-3">
              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Company Name</label>
                <input
                  type="text"
                  required
                  value={newCust.companyName}
                  onChange={(e) => setNewCust({ ...newCust, companyName: e.target.value })}
                  placeholder="e.g. Apex Corp"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Contact Person</label>
                <input
                  type="text"
                  value={newCust.contactPerson}
                  onChange={(e) => setNewCust({ ...newCust, contactPerson: e.target.value })}
                  placeholder="Johnathan Miller"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Client Email</label>
                <input
                  type="email"
                  required
                  value={newCust.email}
                  onChange={(e) => setNewCust({ ...newCust, email: e.target.value })}
                  placeholder="client@apex.com"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Contract Tier</label>
                <input
                  type="text"
                  value={newCust.plan}
                  onChange={(e) => setNewCust({ ...newCust, plan: e.target.value })}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div className="pt-3 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-3.5 py-1.5 text-xs text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl shadow-md"
                >
                  Save Client
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
