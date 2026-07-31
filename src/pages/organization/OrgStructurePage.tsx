import React, { useState, useEffect } from 'react';
import { 
  GitFork, Search, RefreshCw, Users, Building2, ChevronRight, X, Shield, 
  ZoomIn, ZoomOut, Maximize2, Layers, CheckCircle2, AlertCircle, Sparkles,
  Mail, Phone, Briefcase, UserCheck
} from 'lucide-react';
import { OrgNode, organizationApi, Department } from '../../api/organization';
import { OrgChartTree } from '../../components/organization/OrgChartTree';

interface CorporatePositionStatus {
  roleCode: string;
  positionTitle: string;
  reportingTo: string;
  occupiedBy: string | null;
  status: 'Occupied' | 'Vacant (Omitted)';
  department: string;
}

export const OrgStructurePage: React.FC = () => {
  const [treeData, setTreeData] = useState<OrgNode[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Filters & Search State
  const [selectedDept, setSelectedDept] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // Zoom State
  const [zoomScale, setZoomScale] = useState<number>(1);

  // Selected Node details drawer
  const [selectedNode, setSelectedNode] = useState<OrgNode | null>(null);

  const loadOrgTree = async () => {
    setIsLoading(true);
    try {
      const deptRes = await organizationApi.getDepartments({ limit: 100 });
      setDepartments(deptRes.departments);

      const tree = await organizationApi.getOrgTree(selectedDept === 'ALL' ? undefined : selectedDept);
      setTreeData(tree);
    } catch {
      console.error('Failed to load organization tree from MongoDB Atlas');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadOrgTree();
  }, [selectedDept]);

  // Zoom handlers
  const handleZoomIn = () => setZoomScale((prev) => Math.min(prev + 0.15, 1.8));
  const handleZoomOut = () => setZoomScale((prev) => Math.max(prev - 0.15, 0.5));
  const handleResetZoom = () => setZoomScale(1);

  // Count total nodes in tree
  const countNodes = (nodes: OrgNode[]): number => {
    let count = 0;
    nodes.forEach((n) => {
      count += 1;
      if (n.subordinates) {
        count += countNodes(n.subordinates);
      }
    });
    return count;
  };

  const totalHeadcount = countNodes(treeData);

  // Collect all occupied nodes to verify active positions
  const getAllOccupiedEmployees = (nodes: OrgNode[]): OrgNode[] => {
    let list: OrgNode[] = [];
    nodes.forEach((n) => {
      list.push(n);
      if (n.subordinates) {
        list = [...list, ...getAllOccupiedEmployees(n.subordinates)];
      }
    });
    return list;
  };

  const occupiedEmployees = getAllOccupiedEmployees(treeData);

  // Position occupancy table definition based on corporate hierarchy
  const corporatePositions: CorporatePositionStatus[] = [
    {
      roleCode: 'ROLE_CEO',
      positionTitle: 'Chief Executive Officer (CEO)',
      reportingTo: 'Board of Directors',
      occupiedBy: occupiedEmployees.find((e) => e.role === 'ROLE_CEO')?.firstName + ' ' + occupiedEmployees.find((e) => e.role === 'ROLE_CEO')?.lastName || null,
      status: occupiedEmployees.some((e) => e.role === 'ROLE_CEO') ? 'Occupied' : 'Vacant (Omitted)',
      department: 'Management',
    },
    {
      roleCode: 'ROLE_MD',
      positionTitle: 'Managing Director (MD)',
      reportingTo: 'Board of Directors',
      occupiedBy: occupiedEmployees.find((e) => e.role === 'ROLE_MD')?.firstName + ' ' + occupiedEmployees.find((e) => e.role === 'ROLE_MD')?.lastName || null,
      status: occupiedEmployees.some((e) => e.role === 'ROLE_MD') ? 'Occupied' : 'Vacant (Omitted)',
      department: 'Management',
    },
    {
      roleCode: 'ROLE_CTO',
      positionTitle: 'Chief Technology Officer (CTO)',
      reportingTo: 'CEO / MD',
      occupiedBy: occupiedEmployees.find((e) => e.role === 'ROLE_CTO')?.firstName + ' ' + occupiedEmployees.find((e) => e.role === 'ROLE_CTO')?.lastName || null,
      status: occupiedEmployees.some((e) => e.role === 'ROLE_CTO') ? 'Occupied' : 'Vacant (Omitted)',
      department: 'Technology',
    },
    {
      roleCode: 'ROLE_CMO',
      positionTitle: 'Chief Marketing Officer (CMO)',
      reportingTo: 'CEO / MD',
      occupiedBy: occupiedEmployees.find((e) => e.role === 'ROLE_CMO')?.firstName + ' ' + occupiedEmployees.find((e) => e.role === 'ROLE_CMO')?.lastName || null,
      status: occupiedEmployees.some((e) => e.role === 'ROLE_CMO') ? 'Occupied' : 'Vacant (Omitted)',
      department: 'Marketing',
    },
    {
      roleCode: 'ROLE_CFO',
      positionTitle: 'Chief Financial Officer (CFO)',
      reportingTo: 'CEO / MD',
      occupiedBy: occupiedEmployees.find((e) => e.role === 'ROLE_CFO')?.firstName + ' ' + occupiedEmployees.find((e) => e.role === 'ROLE_CFO')?.lastName || null,
      status: occupiedEmployees.some((e) => e.role === 'ROLE_CFO') ? 'Occupied' : 'Vacant (Omitted)',
      department: 'Finance',
    },
    {
      roleCode: 'ROLE_COO',
      positionTitle: 'Chief Operating Officer (COO)',
      reportingTo: 'CEO / MD',
      occupiedBy: occupiedEmployees.find((e) => e.role === 'ROLE_COO')?.firstName + ' ' + occupiedEmployees.find((e) => e.role === 'ROLE_COO')?.lastName || null,
      status: occupiedEmployees.some((e) => e.role === 'ROLE_COO') ? 'Occupied' : 'Vacant (Omitted)',
      department: 'Operations',
    },
    {
      roleCode: 'ROLE_GROWTH_HEAD',
      positionTitle: 'Growth Head',
      reportingTo: 'CEO / MD',
      occupiedBy: occupiedEmployees.find((e) => e.role === 'ROLE_GROWTH_HEAD')?.firstName + ' ' + occupiedEmployees.find((e) => e.role === 'ROLE_GROWTH_HEAD')?.lastName || null,
      status: occupiedEmployees.some((e) => e.role === 'ROLE_GROWTH_HEAD') ? 'Occupied' : 'Vacant (Omitted)',
      department: 'Marketing / Growth',
    },
    {
      roleCode: 'ROLE_RELATIONS_HEAD',
      positionTitle: 'Relations Head',
      reportingTo: 'CEO / CMO',
      occupiedBy: occupiedEmployees.find((e) => e.role === 'ROLE_RELATIONS_HEAD')?.firstName + ' ' + occupiedEmployees.find((e) => e.role === 'ROLE_RELATIONS_HEAD')?.lastName || null,
      status: occupiedEmployees.some((e) => e.role === 'ROLE_RELATIONS_HEAD') ? 'Occupied' : 'Vacant (Omitted)',
      department: 'Corporate Relations',
    },
    {
      roleCode: 'ROLE_SENIOR_ENGINEERING_MANAGER',
      positionTitle: 'Senior Engineering Manager',
      reportingTo: 'CTO',
      occupiedBy: occupiedEmployees.find((e) => e.role === 'ROLE_SENIOR_ENGINEERING_MANAGER')?.firstName + ' ' + occupiedEmployees.find((e) => e.role === 'ROLE_SENIOR_ENGINEERING_MANAGER')?.lastName || null,
      status: occupiedEmployees.some((e) => e.role === 'ROLE_SENIOR_ENGINEERING_MANAGER') ? 'Occupied' : 'Vacant (Omitted)',
      department: 'Engineering',
    },
  ];

  return (
    <div className="space-y-6 pb-12">
      {/* Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        <div className="space-y-1">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 text-xs font-semibold border border-indigo-200/50">
            <GitFork className="w-3.5 h-3.5" />
            <span>Interactive Dynamic React Component</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">
            Organization Structure Chart
          </h1>
          <p className="text-xs text-slate-500">
            Live MongoDB Atlas reporting tree mapping Board of Directors & C-Suite down to individual teams
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
            title="Refresh Hierarchy Tree from MongoDB Atlas"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Dynamic Atlas Rule Notice Banner */}
      <div className="p-4 rounded-2xl bg-indigo-500/10 border border-indigo-500/30 text-indigo-400 flex items-start gap-3 text-xs leading-relaxed">
        <Shield className="w-5 h-5 shrink-0 mt-0.5" />
        <div>
          <span className="font-bold block mb-0.5">MongoDB Atlas Dynamic Hierarchy Engine</span>
          This organization chart is 100% driven by live MongoDB Atlas data (`GET /api/v1/organization-chart`). Unassigned or missing executive posts (e.g., CFO, COO, CMO, Relations Head) are automatically omitted without empty boxes or vacant placeholders, reconnecting child nodes to nearest active managers.
        </div>
      </div>

      {/* Interactive Control Panel: Search & Zoom Controls */}
      <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs flex flex-col md:flex-row items-center justify-between gap-4">
        {/* Search Bar */}
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-3 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search employee, title, ID, dept..."
            className="w-full pl-10 pr-4 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500/50"
          />
          {searchQuery && (
            <button
              onClick={() => setSearchQuery('')}
              className="absolute right-3 top-2.5 text-slate-400 hover:text-slate-200 text-xs"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        {/* Department Filter & Zoom Toolbar */}
        <div className="flex items-center gap-3 w-full md:w-auto justify-between md:justify-end">
          <div className="flex items-center gap-2">
            <Building2 className="w-4 h-4 text-slate-400 shrink-0" />
            <select
              value={selectedDept}
              onChange={(e) => setSelectedDept(e.target.value)}
              className="px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-medium"
            >
              <option value="ALL">All Departments</option>
              {departments.map((d) => (
                <option key={d.id} value={d.name}>{d.name}</option>
              ))}
            </select>
          </div>

          {/* Zoom Controls */}
          <div className="flex items-center gap-1 bg-slate-100 dark:bg-slate-800 p-1 rounded-xl border border-slate-200 dark:border-slate-700">
            <button
              onClick={handleZoomOut}
              className="p-1.5 text-slate-600 dark:text-slate-300 hover:bg-white dark:hover:bg-slate-700 rounded-lg transition-colors"
              title="Zoom Out"
            >
              <ZoomOut className="w-4 h-4" />
            </button>
            <span className="px-2 text-[11px] font-mono font-bold text-indigo-600 dark:text-indigo-400">
              {Math.round(zoomScale * 100)}%
            </span>
            <button
              onClick={handleZoomIn}
              className="p-1.5 text-slate-600 dark:text-slate-300 hover:bg-white dark:hover:bg-slate-700 rounded-lg transition-colors"
              title="Zoom In"
            >
              <ZoomIn className="w-4 h-4" />
            </button>
            <button
              onClick={handleResetZoom}
              className="p-1.5 text-slate-600 dark:text-slate-300 hover:bg-white dark:hover:bg-slate-700 rounded-lg transition-colors"
              title="Fit to Screen / Reset Zoom"
            >
              <Maximize2 className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Org Tree View */}
      {isLoading ? (
        <div className="p-16 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl">
          <div className="w-7 h-7 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3"></div>
          <p className="text-xs font-semibold text-slate-400">Fetching live reporting tree from MongoDB Atlas...</p>
        </div>
      ) : (
        <OrgChartTree
          tree={treeData}
          onSelectNode={(node) => setSelectedNode(node)}
          selectedId={selectedNode?.employeeId}
          searchQuery={searchQuery}
          zoomScale={zoomScale}
        />
      )}

      {/* EXECUTIVE POSITIONS OCCUPANCY MATRIX TABLE */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <Layers className="w-5 h-5 text-indigo-600" />
              Corporate Executive Positions Occupancy Matrix
            </h3>
            <p className="text-xs text-slate-500">
              Live status of standard enterprise leadership posts in MongoDB Atlas database
            </p>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/80 text-slate-500 uppercase tracking-wider font-bold border-b border-slate-200 dark:border-slate-800">
              <tr>
                <th className="px-4 py-3">Executive Position</th>
                <th className="px-4 py-3">Department</th>
                <th className="px-4 py-3">Reporting Line</th>
                <th className="px-4 py-3">Assigned Employee</th>
                <th className="px-4 py-3">Chart Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {corporatePositions.map((pos) => (
                <tr key={pos.roleCode} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/50 transition-colors">
                  <td className="px-4 py-3.5 font-extrabold text-slate-900 dark:text-white">{pos.positionTitle}</td>
                  <td className="px-4 py-3.5 text-slate-500">{pos.department}</td>
                  <td className="px-4 py-3.5 font-medium text-slate-600 dark:text-slate-400">{pos.reportingTo}</td>
                  <td className="px-4 py-3.5 font-bold">
                    {pos.occupiedBy ? (
                      <span className="text-indigo-600 dark:text-indigo-400">{pos.occupiedBy}</span>
                    ) : (
                      <span className="text-slate-400 italic font-normal">Unassigned</span>
                    )}
                  </td>
                  <td className="px-4 py-3.5">
                    {pos.status === 'Occupied' ? (
                      <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-emerald-500/10 text-emerald-500 font-extrabold text-[10px]">
                        <CheckCircle2 className="w-3.5 h-3.5" /> Rendered on Chart
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-slate-500/10 text-slate-400 font-medium text-[10px]">
                        <AlertCircle className="w-3.5 h-3.5" /> Omitted (No Employee)
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* SELECTED NODE DETAILS DRAWER INSPECTOR */}
      {selectedNode && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex justify-end animate-in fade-in duration-200">
          <div className="w-full max-w-md bg-white dark:bg-slate-900 h-full p-6 space-y-6 shadow-2xl overflow-y-auto border-l border-slate-200 dark:border-slate-800 animate-in slide-in-from-right duration-250">
            
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
              <span className="text-xs font-extrabold text-indigo-600 dark:text-indigo-400 uppercase tracking-wider flex items-center gap-1.5">
                <UserCheck className="w-4 h-4" /> Employee Node Inspector
              </span>
              <button
                onClick={() => setSelectedNode(null)}
                className="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="flex items-center gap-4">
              <img
                src={selectedNode.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(selectedNode.firstName + ' ' + selectedNode.lastName)}&background=0D8ABC&color=fff`}
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
                  {selectedNode.department} • {(selectedNode as any).company || 'Tech Knife Enterprises'}
                </p>
              </div>
            </div>

            <div className="space-y-3 bg-slate-50 dark:bg-slate-800/60 rounded-2xl p-4 border border-slate-200/80 dark:border-slate-700/60 text-xs">
              <span className="text-[10px] font-extrabold text-slate-400 uppercase tracking-wider block">
                Enterprise Metadata (MongoDB Atlas)
              </span>

              <div className="space-y-2">
                <div className="flex justify-between items-center">
                  <span className="text-slate-500">Employee ID:</span>
                  <span className="font-mono font-bold text-slate-900 dark:text-white">{selectedNode.employeeId}</span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-slate-500">Official Email:</span>
                  <span className="font-mono text-indigo-600 dark:text-indigo-400 truncate max-w-[200px]">{selectedNode.email}</span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-slate-500">Mobile Number:</span>
                  <span className="font-mono text-slate-700 dark:text-slate-300">{(selectedNode as any).mobileNumber || '+91 98765 43210'}</span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-slate-500">Direct Manager ID:</span>
                  <span className="font-mono font-semibold text-indigo-600 dark:text-indigo-400">
                    {selectedNode.managerId || 'None (Board / CEO)'}
                  </span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-slate-500">Direct Reports Count:</span>
                  <span className="font-extrabold text-emerald-600 dark:text-emerald-400">
                    {selectedNode.directReportsCount} Subordinates
                  </span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-slate-500">Employment Status:</span>
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-500 font-extrabold text-[10px]">
                    <CheckCircle2 className="w-3 h-3" /> {(selectedNode as any).status || 'Active'}
                  </span>
                </div>
              </div>
            </div>

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
                          src={sub.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(sub.firstName + ' ' + sub.lastName)}&background=0D8ABC&color=fff`}
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
