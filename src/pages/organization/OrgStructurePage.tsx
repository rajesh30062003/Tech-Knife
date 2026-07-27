import React, { useState, useEffect } from 'react';
import { 
  GitFork, Search, Filter, RefreshCw, Users, Building2, Award, Mail, 
  Phone, Calendar, UserCheck, ChevronRight, X, Shield, DollarSign, Layers 
} from 'lucide-react';
import { OrgNode, organizationApi, Department } from '../../api/organization';
import { OrgChartTree } from '../../components/organization/OrgChartTree';

export const OrgStructurePage: React.FC = () => {
  const [treeData, setTreeData] = useState<OrgNode[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Filters
  const [selectedDept, setSelectedDept] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // Selected Node details modal/drawer
  const [selectedNode, setSelectedNode] = useState<OrgNode | null>(null);

  const loadOrgTree = async () => {
    setIsLoading(true);
    try {
      const deptRes = await organizationApi.getDepartments({ limit: 100 });
      setDepartments(deptRes.departments);

      const tree = await organizationApi.getOrgTree(selectedDept === 'ALL' ? undefined : selectedDept);
      setTreeData(tree);
    } catch {
      console.error('Failed to load organization tree');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadOrgTree();
  }, [selectedDept]);

  // Count total nodes in tree
  const countNodes = (nodes: OrgNode[]): number => {
    let count = 0;
    nodes.forEach(n => {
      count += 1;
      if (n.subordinates) {
        count += countNodes(n.subordinates);
      }
    });
    return count;
  };

  const totalHeadcount = countNodes(treeData);

  return (
    <div className="space-y-6 pb-12">
      {/* Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        <div className="space-y-1">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 text-xs font-semibold border border-indigo-200/50">
            <GitFork className="w-3.5 h-3.5" />
            <span>Interactive Hierarchy</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">
            Organization Structure Chart
          </h1>
          <p className="text-xs text-slate-500">
            Visual reporting tree mapping executive leadership down to individual team contributors
          </p>
        </div>

        <div className="flex items-center gap-3 shrink-0 self-start md:self-auto">
          <div className="px-3.5 py-2 rounded-2xl bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 text-xs font-bold text-slate-700 dark:text-slate-300 flex items-center gap-2">
            <Users className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />
            <span>Mapped Headcount: {totalHeadcount}</span>
          </div>
          <button
            onClick={loadOrgTree}
            className="p-2 text-slate-500 hover:text-indigo-600 dark:hover:text-indigo-400 rounded-2xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 transition-colors"
            title="Refresh Hierarchy Tree"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-2 text-xs text-slate-500 font-semibold shrink-0">
          <Building2 className="w-4 h-4 text-slate-400" /> Filter Department Branch:
        </div>

        <div className="flex items-center gap-3 w-full sm:w-auto">
          <select
            value={selectedDept}
            onChange={(e) => setSelectedDept(e.target.value)}
            className="w-full sm:w-64 px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-medium"
          >
            <option value="ALL">All Enterprise Departments</option>
            {departments.map((d) => (
              <option key={d.id} value={d.name}>{d.name}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Org Tree View */}
      {isLoading ? (
        <div className="p-16 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl">
          <div className="w-7 h-7 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3"></div>
          <p className="text-xs font-semibold text-slate-400">Rendering corporate reporting hierarchy...</p>
        </div>
      ) : (
        <OrgChartTree
          tree={treeData}
          onSelectNode={(node) => setSelectedNode(node)}
          selectedId={selectedNode?.employeeId}
        />
      )}

      {/* SELECTED NODE DETAILS DRAWER */}
      {selectedNode && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex justify-end animate-in fade-in duration-200">
          <div className="w-full max-w-md bg-white dark:bg-slate-900 h-full p-6 space-y-6 shadow-2xl overflow-y-auto border-l border-slate-200 dark:border-slate-800 animate-in slide-in-from-right duration-250">
            
            {/* Header */}
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
              <span className="text-xs font-extrabold text-indigo-600 dark:text-indigo-400 uppercase tracking-wider">
                Node Inspector
              </span>
              <button
                onClick={() => setSelectedNode(null)}
                className="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Profile Brief */}
            <div className="flex items-center gap-4">
              <img
                src={selectedNode.avatarUrl || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=300'}
                alt={selectedNode.firstName}
                className="w-16 h-16 rounded-2xl object-cover border-2 border-indigo-500/20 shadow-md"
              />
              <div>
                <h3 className="text-lg font-extrabold text-slate-900 dark:text-white">
                  {selectedNode.firstName} {selectedNode.lastName}
                </h3>
                <p className="text-xs font-bold text-indigo-600 dark:text-indigo-400">
                  {selectedNode.designation}
                </p>
                <p className="text-xs text-slate-400">
                  {selectedNode.department}
                </p>
              </div>
            </div>

            {/* Reporting Line Section */}
            <div className="space-y-3 bg-slate-50 dark:bg-slate-800/60 rounded-2xl p-4 border border-slate-200/80 dark:border-slate-700/60">
              <span className="text-[10px] font-extrabold text-slate-400 uppercase tracking-wider block">
                Reporting Line
              </span>

              <div className="space-y-2 text-xs">
                <div className="flex justify-between items-center">
                  <span className="text-slate-500">Employee ID:</span>
                  <span className="font-mono font-bold text-slate-900 dark:text-white">{selectedNode.employeeId}</span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-slate-500">Direct Manager ID:</span>
                  <span className="font-mono font-semibold text-indigo-600 dark:text-indigo-400">
                    {selectedNode.managerId || 'None (Top Executive)'}
                  </span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-slate-500">Direct Reports Count:</span>
                  <span className="font-extrabold text-emerald-600 dark:text-emerald-400">
                    {selectedNode.directReportsCount} Subordinates
                  </span>
                </div>
              </div>
            </div>

            {/* Direct Reports List */}
            {selectedNode.subordinates && selectedNode.subordinates.length > 0 && (
              <div className="space-y-3">
                <span className="text-xs font-bold text-slate-700 dark:text-slate-300 block">
                  Direct Reports ({selectedNode.subordinates.length})
                </span>

                <div className="space-y-2 max-h-60 overflow-y-auto pr-1">
                  {selectedNode.subordinates.map((sub) => (
                    <div
                      key={sub.employeeId}
                      onClick={() => setSelectedNode(sub)}
                      className="p-3 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700/80 rounded-xl flex items-center justify-between cursor-pointer hover:border-indigo-500 transition-colors"
                    >
                      <div className="flex items-center gap-3">
                        <img
                          src={sub.avatarUrl}
                          alt={sub.firstName}
                          className="w-8 h-8 rounded-full object-cover"
                        />
                        <div>
                          <p className="text-xs font-bold text-slate-900 dark:text-white">
                            {sub.firstName} {sub.lastName}
                          </p>
                          <p className="text-[10px] text-slate-400">
                            {sub.designation}
                          </p>
                        </div>
                      </div>
                      <ChevronRight className="w-4 h-4 text-slate-400" />
                    </div>
                  ))}
                </div>
              </div>
            )}

            <button
              onClick={() => setSelectedNode(null)}
              className="w-full py-2.5 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-bold text-xs rounded-xl hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
            >
              Close Inspector
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
