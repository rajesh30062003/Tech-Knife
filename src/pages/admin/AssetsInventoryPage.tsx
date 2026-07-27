import React, { useState } from 'react';
import { motion } from 'motion/react';
import {
  Laptop, Package, ShoppingCart, ShieldAlert, Cpu, HardDrive,
  Plus, Search, Filter, ArrowUpRight, CheckCircle2, Clock, AlertTriangle,
  UserCheck, Building2, Tag, Download
} from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';

interface AssetItem {
  id: string;
  assetTag: string;
  name: string;
  category: 'Laptop / PC' | 'Server Hardware' | 'Mobile Device' | 'Networking' | 'Office Supply';
  serialNumber: string;
  assignedTo: string;
  department: string;
  purchaseDate: string;
  cost: number;
  status: 'In Use' | 'Available' | 'Under Maintenance' | 'Retired';
}

const INITIAL_ASSETS: AssetItem[] = [
  { id: '1', assetTag: 'TK-LAP-2026-001', name: 'MacBook Pro 16" M3 Max (64GB)', category: 'Laptop / PC', serialNumber: 'C02G30X1MD6M', assignedTo: 'Sarah Jenkins', department: 'Engineering', purchaseDate: '2026-01-15', cost: 3499, status: 'In Use' },
  { id: '2', assetTag: 'TK-SRV-2026-042', name: 'Dell PowerEdge R760 Rack Server', category: 'Server Hardware', serialNumber: 'DELL-9948271', assignedTo: 'Cloud Ops Team', department: 'Infrastructure', purchaseDate: '2025-11-10', cost: 12500, status: 'In Use' },
  { id: '3', assetTag: 'TK-LAP-2026-014', name: 'Dell XPS 15 OLED (32GB)', category: 'Laptop / PC', serialNumber: 'DXP-882012', assignedTo: 'Alex Thorne', department: 'AI Lab', purchaseDate: '2026-02-01', cost: 2299, status: 'In Use' },
  { id: '4', assetTag: 'TK-NET-2026-008', name: 'Cisco Catalyst 9300 48-Port Switch', category: 'Networking', serialNumber: 'CSCO-449102', assignedTo: 'IT Desk', department: 'IT Ops', purchaseDate: '2025-08-20', cost: 4800, status: 'Available' },
  { id: '5', assetTag: 'TK-MOB-2026-088', name: 'iPad Pro 12.9" M2 (Cellular)', category: 'Mobile Device', serialNumber: 'IPD-330192', assignedTo: 'Unassigned', department: 'Executive', purchaseDate: '2026-03-12', cost: 1299, status: 'Available' },
  { id: '6', assetTag: 'TK-LAP-2025-102', name: 'Lenovo ThinkPad P1 Gen 6', category: 'Laptop / PC', serialNumber: 'LNV-110293', assignedTo: 'IT Service Bench', department: 'IT Ops', purchaseDate: '2025-04-10', cost: 2100, status: 'Under Maintenance' },
];

export const AssetsInventoryPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'all' | 'procurement' | 'maintenance'>('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');
  const [assets, setAssets] = useState<AssetItem[]>(INITIAL_ASSETS);
  const [showAddModal, setShowAddModal] = useState(false);

  // Form State
  const [name, setName] = useState('');
  const [category, setCategory] = useState<AssetItem['category']>('Laptop / PC');
  const [assignedTo, setAssignedTo] = useState('');
  const [cost, setCost] = useState('');

  const handleCreateAsset = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name) return;
    const newAsset: AssetItem = {
      id: Date.now().toString(),
      assetTag: `TK-AST-2026-${Math.floor(100 + Math.random() * 900)}`,
      name,
      category,
      serialNumber: `SN-${Math.floor(100000 + Math.random() * 900000)}`,
      assignedTo: assignedTo || 'Unassigned (IT Vault)',
      department: 'Engineering',
      purchaseDate: new Date().toISOString().split('T')[0],
      cost: parseFloat(cost) || 1500,
      status: assignedTo ? 'In Use' : 'Available',
    };
    setAssets([newAsset, ...assets]);
    setName('');
    setAssignedTo('');
    setCost('');
    setShowAddModal(false);
  };

  const filteredAssets = assets.filter((a) => {
    const matchesSearch =
      a.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      a.assetTag.toLowerCase().includes(searchTerm.toLowerCase()) ||
      a.assignedTo.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = categoryFilter === 'ALL' || a.category === categoryFilter;
    return matchesSearch && matchesCategory;
  });

  const totalCost = assets.reduce((acc, item) => acc + item.cost, 0);

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-950 to-blue-950 border border-slate-800 rounded-2xl p-6 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <Package className="w-6 h-6 text-blue-400" />
            <h1 className="text-2xl font-black tracking-tight">Enterprise Assets, Inventory & Procurement</h1>
          </div>
          <p className="text-xs text-slate-300 mt-1 max-w-2xl">
            Track hardware lifecycle, laptops, servers, mobile devices, IT inventory stock, serial numbers, warranty coverage, and corporate procurement requests.
          </p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-xl shadow-lg shadow-blue-600/30 flex items-center gap-2 transition-all"
        >
          <Plus className="w-4 h-4" />
          <span>Register New Hardware Asset</span>
        </button>
      </div>

      {/* Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase">Total IT Hardware</span>
            <Laptop className="w-4 h-4 text-blue-500" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white mt-1">{assets.length} Units</div>
          <div className="text-[11px] text-slate-500 mt-1">Valued at ${totalCost.toLocaleString()}</div>
        </div>
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase">Deployed In Use</span>
            <UserCheck className="w-4 h-4 text-emerald-500" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white mt-1">
            {assets.filter((a) => a.status === 'In Use').length} Allocated
          </div>
          <div className="text-[11px] text-emerald-600 font-semibold mt-1">Active Employee Assignment</div>
        </div>
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase">In IT Storage Vault</span>
            <Package className="w-4 h-4 text-indigo-500" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white mt-1">
            {assets.filter((a) => a.status === 'Available').length} Ready
          </div>
          <div className="text-[11px] text-indigo-600 font-semibold mt-1">Instant Onboarding Ready</div>
        </div>
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase">Maintenance Desk</span>
            <AlertTriangle className="w-4 h-4 text-amber-500" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white mt-1">
            {assets.filter((a) => a.status === 'Under Maintenance').length} Servicing
          </div>
          <div className="text-[11px] text-amber-600 font-semibold mt-1">Under Vendor Warranty</div>
        </div>
      </div>

      {/* Main Table Container */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-4">
        {/* Filters and Controls */}
        <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-3 w-full sm:w-auto">
            <div className="relative flex-1 sm:w-72">
              <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search Tag, Device, Assignee..."
                className="w-full pl-9 pr-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-800 dark:text-slate-200"
              />
            </div>
            <select
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value)}
              className="p-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 font-medium"
            >
              <option value="ALL">All Categories</option>
              <option value="Laptop / PC">Laptop / PC</option>
              <option value="Server Hardware">Server Hardware</option>
              <option value="Mobile Device">Mobile Device</option>
              <option value="Networking">Networking</option>
            </select>
          </div>

          <div className="flex items-center gap-2">
            <button className="px-3 py-1.5 text-xs font-bold rounded-xl border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 flex items-center gap-1.5">
              <Download className="w-3.5 h-3.5" />
              <span>Export CSV</span>
            </button>
          </div>
        </div>

        {/* Data Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="bg-slate-50 dark:bg-slate-800/60 text-slate-500 dark:text-slate-400 uppercase font-semibold">
                <th className="p-3">Asset Tag</th>
                <th className="p-3">Hardware Device</th>
                <th className="p-3">Category</th>
                <th className="p-3">Serial No</th>
                <th className="p-3">Assignee</th>
                <th className="p-3">Cost</th>
                <th className="p-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {filteredAssets.map((asset) => (
                <tr key={asset.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors">
                  <td className="p-3 font-mono font-bold text-blue-600 dark:text-blue-400">{asset.assetTag}</td>
                  <td className="p-3">
                    <div className="font-extrabold text-slate-900 dark:text-white">{asset.name}</div>
                    <div className="text-[10px] text-slate-400">Purchased {asset.purchaseDate}</div>
                  </td>
                  <td className="p-3 font-medium text-slate-600 dark:text-slate-300">{asset.category}</td>
                  <td className="p-3 font-mono text-slate-500">{asset.serialNumber}</td>
                  <td className="p-3 font-bold text-slate-800 dark:text-slate-200">{asset.assignedTo}</td>
                  <td className="p-3 font-bold text-slate-900 dark:text-slate-100">${asset.cost.toLocaleString()}</td>
                  <td className="p-3">
                    <StatusBadge
                      status={
                        asset.status === 'In Use'
                          ? 'Completed'
                          : asset.status === 'Available'
                          ? 'Active'
                          : asset.status === 'Under Maintenance'
                          ? 'In Progress'
                          : 'Failed'
                      }
                      size="sm"
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 w-full max-w-md space-y-4 shadow-2xl">
            <h3 className="text-lg font-black text-slate-900 dark:text-white">Register Hardware Asset</h3>
            <form onSubmit={handleCreateAsset} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Device Name / Model</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Lenovo ThinkPad X1 Carbon"
                  className="w-full p-2.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Category</label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value as AssetItem['category'])}
                  className="w-full p-2.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 font-semibold"
                >
                  <option value="Laptop / PC">Laptop / PC</option>
                  <option value="Server Hardware">Server Hardware</option>
                  <option value="Mobile Device">Mobile Device</option>
                  <option value="Networking">Networking</option>
                </select>
              </div>
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Assigned Employee (Optional)</label>
                <input
                  type="text"
                  value={assignedTo}
                  onChange={(e) => setAssignedTo(e.target.value)}
                  placeholder="Leave blank if in IT Vault"
                  className="w-full p-2.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Purchase Cost ($)</label>
                <input
                  type="number"
                  value={cost}
                  onChange={(e) => setCost(e.target.value)}
                  placeholder="2400"
                  className="w-full p-2.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 font-mono"
                />
              </div>
              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 text-xs font-bold text-slate-500 hover:text-slate-800 dark:hover:text-slate-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-xl shadow-md"
                >
                  Register Asset
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
