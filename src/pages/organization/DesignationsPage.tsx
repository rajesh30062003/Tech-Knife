import React, { useState, useEffect } from 'react';
import { 
  Award, Plus, Search, Filter, RefreshCw, Edit3, Trash2, CheckCircle2, 
  XCircle, DollarSign, Layers, ChevronLeft, ChevronRight, Save, X, AlertCircle, Building2 
} from 'lucide-react';
import { Designation, Department, organizationApi } from '../../api/organization';
import { ConfirmationDialog } from '../../components/employee/ConfirmationDialog';

const GRADE_COLOR_MAP: Record<string, string> = {
  L7: 'bg-purple-100 text-purple-700 dark:bg-purple-950/80 dark:text-purple-300 border-purple-200/60',
  L6: 'bg-indigo-100 text-indigo-700 dark:bg-indigo-950/80 dark:text-indigo-300 border-indigo-200/60',
  L5: 'bg-blue-100 text-blue-700 dark:bg-blue-950/80 dark:text-blue-300 border-blue-200/60',
  L4: 'bg-cyan-100 text-cyan-700 dark:bg-cyan-950/80 dark:text-cyan-300 border-cyan-200/60',
  L3: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-950/80 dark:text-emerald-300 border-emerald-200/60',
  L2: 'bg-amber-100 text-amber-700 dark:bg-amber-950/80 dark:text-amber-300 border-amber-200/60',
  L1: 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300 border-slate-200/60',
};

export const DesignationsPage: React.FC = () => {
  const [designations, setDesignations] = useState<Designation[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Filters & Pagination
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [selectedDept, setSelectedDept] = useState<string>('ALL');
  const [selectedGrade, setSelectedGrade] = useState<string>('ALL');
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(8);

  // Modal States
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [editingDesignation, setEditingDesignation] = useState<Designation | null>(null);
  const [deletingDesignation, setDeletingDesignation] = useState<Designation | null>(null);

  // Form State
  const [formData, setFormData] = useState<Partial<Designation>>({
    title: '',
    code: '',
    grade: 'L2',
    hierarchyLevel: 2,
    departmentName: 'Engineering & DevOps',
    minSalary: 85000,
    maxSalary: 130000,
    currency: 'USD',
    status: 'Active',
    description: ''
  });

  // Notification Toast
  const [toastMessage, setToastMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  useEffect(() => {
    if (toastMessage) {
      const timer = setTimeout(() => setToastMessage(null), 3500);
      return () => clearTimeout(timer);
    }
  }, [toastMessage]);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const deptRes = await organizationApi.getDepartments({ limit: 100 });
      setDepartments(deptRes.departments);

      const res = await organizationApi.getDesignations({
        search: searchQuery,
        departmentId: selectedDept,
        grade: selectedGrade,
        status: selectedStatus,
        page: currentPage,
        limit: pageSize,
      });
      setDesignations(res.designations);
      setTotalCount(res.total);
      setTotalPages(res.totalPages);
    } catch {
      setToastMessage({ type: 'error', text: 'Failed to fetch designation records.' });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [searchQuery, selectedDept, selectedGrade, selectedStatus, currentPage, pageSize]);

  const handleOpenCreate = () => {
    setEditingDesignation(null);
    setFormData({
      title: '',
      code: '',
      grade: 'L2',
      hierarchyLevel: 2,
      departmentName: departments.length > 0 ? departments[0].name : 'Engineering & DevOps',
      minSalary: 85000,
      maxSalary: 130000,
      currency: 'USD',
      status: 'Active',
      description: ''
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (desg: Designation) => {
    setEditingDesignation(desg);
    setFormData({ ...desg });
    setIsModalOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.title || !formData.code) {
      setToastMessage({ type: 'error', text: 'Please fill required fields (Title, Code).' });
      return;
    }

    try {
      if (editingDesignation) {
        await organizationApi.updateDesignation(editingDesignation.id, formData);
        setToastMessage({ type: 'success', text: `Designation "${formData.title}" updated successfully!` });
      } else {
        await organizationApi.createDesignation(formData);
        setToastMessage({ type: 'success', text: `Designation "${formData.title}" created successfully!` });
      }
      setIsModalOpen(false);
      loadData();
    } catch {
      setToastMessage({ type: 'error', text: 'Failed to save designation.' });
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deletingDesignation) return;
    try {
      await organizationApi.deleteDesignation(deletingDesignation.id);
      setToastMessage({ type: 'success', text: `Designation "${deletingDesignation.title}" removed.` });
      setDeletingDesignation(null);
      loadData();
    } catch {
      setToastMessage({ type: 'error', text: 'Failed to delete designation.' });
    }
  };

  return (
    <div className="space-y-6 pb-12">
      {/* Toast Notification */}
      {toastMessage && (
        <div
          className={`fixed top-5 right-5 z-50 px-4 py-3 rounded-2xl shadow-xl flex items-center gap-3 text-xs font-bold animate-in fade-in slide-in-from-top-3 duration-200 border ${
            toastMessage.type === 'success'
              ? 'bg-emerald-900 text-emerald-100 border-emerald-700'
              : 'bg-rose-900 text-rose-100 border-rose-700'
          }`}
        >
          {toastMessage.type === 'success' ? (
            <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          ) : (
            <AlertCircle className="w-4 h-4 text-rose-400" />
          )}
          <span>{toastMessage.text}</span>
        </div>
      )}

      {/* Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        <div className="space-y-1">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 text-xs font-semibold border border-indigo-200/50">
            <Award className="w-3.5 h-3.5" />
            <span>Job Hierarchy & Compensation</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">
            Designation Matrix
          </h1>
          <p className="text-xs text-slate-500">
            Define corporate job roles, hierarchy levels (L1 - L7), salary bands, and department assignments
          </p>
        </div>

        <button
          onClick={handleOpenCreate}
          className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 shrink-0 self-start md:self-auto"
        >
          <Plus className="w-4 h-4" /> Add Designation
        </button>
      </div>

      {/* Search & Filter Bar */}
      <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs flex flex-col lg:flex-row items-center justify-between gap-4">
        <div className="relative w-full lg:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
              setCurrentPage(1);
            }}
            placeholder="Search job title, code, or department..."
            className="w-full pl-9 pr-4 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
          />
        </div>

        <div className="flex flex-wrap items-center gap-3 w-full lg:w-auto">
          {/* Department Filter */}
          <div className="flex items-center gap-1.5 text-xs text-slate-500 font-semibold">
            <Building2 className="w-3.5 h-3.5" />
            <select
              value={selectedDept}
              onChange={(e) => {
                setSelectedDept(e.target.value);
                setCurrentPage(1);
              }}
              className="px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-medium"
            >
              <option value="ALL">All Departments</option>
              {departments.map((d) => (
                <option key={d.id} value={d.name}>{d.name}</option>
              ))}
            </select>
          </div>

          {/* Grade Filter */}
          <div className="flex items-center gap-1.5 text-xs text-slate-500 font-semibold">
            <Layers className="w-3.5 h-3.5" />
            <select
              value={selectedGrade}
              onChange={(e) => {
                setSelectedGrade(e.target.value);
                setCurrentPage(1);
              }}
              className="px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-medium"
            >
              <option value="ALL">All Grades</option>
              <option value="L7">Grade L7 (Executive C-Suite)</option>
              <option value="L6">Grade L6 (Director)</option>
              <option value="L5">Grade L5 (Manager)</option>
              <option value="L4">Grade L4 (Lead Specialist)</option>
              <option value="L3">Grade L3 (Senior Staff)</option>
              <option value="L2">Grade L2 (Mid-Level)</option>
              <option value="L1">Grade L1 (Junior / Associate)</option>
            </select>
          </div>

          {/* Status Filter */}
          <select
            value={selectedStatus}
            onChange={(e) => {
              setSelectedStatus(e.target.value);
              setCurrentPage(1);
            }}
            className="px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-medium"
          >
            <option value="ALL">All Statuses</option>
            <option value="Active">Active</option>
            <option value="Inactive">Inactive</option>
          </select>

          <button
            onClick={loadData}
            className="p-2 text-slate-500 hover:text-indigo-600 dark:hover:text-indigo-400 rounded-xl bg-slate-100 dark:bg-slate-800 transition-colors"
            title="Refresh list"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Designation Table */}
      {isLoading ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <div className="w-6 h-6 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-2"></div>
          <p className="text-xs text-slate-400">Loading designation hierarchy...</p>
        </div>
      ) : designations.length === 0 ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-3">
          <Award className="w-10 h-10 text-slate-300 dark:text-slate-700 mx-auto" />
          <h3 className="text-sm font-bold text-slate-700 dark:text-slate-300">No designations found</h3>
          <p className="text-xs text-slate-500">Adjust search criteria or create a new designation record.</p>
        </div>
      ) : (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl overflow-hidden shadow-xs">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-400 uppercase tracking-wider font-extrabold text-[10px] border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-3.5 px-4">Designation Title & Code</th>
                  <th className="py-3.5 px-4">Grade & Hierarchy</th>
                  <th className="py-3.5 px-4">Department Unit</th>
                  <th className="py-3.5 px-4">Benchmark Salary Range</th>
                  <th className="py-3.5 px-4">Staff Count</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/80">
                {designations.map((desg) => {
                  const gradeClass = GRADE_COLOR_MAP[desg.grade] || 'bg-slate-100 text-slate-700';

                  return (
                    <tr key={desg.id} className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors">
                      <td className="py-3.5 px-4">
                        <div className="font-extrabold text-slate-900 dark:text-white">
                          {desg.title}
                        </div>
                        <div className="text-[10px] font-mono text-slate-400 uppercase">
                          {desg.code}
                        </div>
                      </td>

                      <td className="py-3.5 px-4">
                        <div className="flex items-center gap-2">
                          <span className={`px-2.5 py-0.5 text-[10px] font-extrabold rounded-md border ${gradeClass}`}>
                            {desg.grade}
                          </span>
                          <span className="text-[11px] font-medium text-slate-500">
                            Level {desg.hierarchyLevel}
                          </span>
                        </div>
                      </td>

                      <td className="py-3.5 px-4 font-semibold text-slate-700 dark:text-slate-300">
                        {desg.departmentName || 'Global / Shared'}
                      </td>

                      <td className="py-3.5 px-4">
                        <div className="font-bold text-slate-900 dark:text-white flex items-center gap-1">
                          <DollarSign className="w-3.5 h-3.5 text-emerald-500" />
                          ${desg.minSalary.toLocaleString()} - ${desg.maxSalary.toLocaleString()} {desg.currency}
                        </div>
                      </td>

                      <td className="py-3.5 px-4 font-bold text-indigo-600 dark:text-indigo-400">
                        {desg.employeeCount || 0} Staff
                      </td>

                      <td className="py-3.5 px-4">
                        <span
                          className={`px-2.5 py-1 text-[10px] font-bold rounded-full inline-flex items-center gap-1 ${
                            desg.status === 'Active'
                              ? 'bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400'
                              : 'bg-slate-100 dark:bg-slate-800 text-slate-500'
                          }`}
                        >
                          {desg.status === 'Active' ? (
                            <CheckCircle2 className="w-3 h-3 text-emerald-500" />
                          ) : (
                            <XCircle className="w-3 h-3 text-slate-400" />
                          )}
                          {desg.status}
                        </span>
                      </td>

                      <td className="py-3.5 px-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            onClick={() => handleOpenEdit(desg)}
                            className="p-1.5 text-slate-400 hover:text-indigo-600 dark:hover:text-indigo-400 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Edit designation"
                          >
                            <Edit3 className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => setDeletingDesignation(desg)}
                            className="p-1.5 text-slate-400 hover:text-rose-600 dark:hover:text-rose-400 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Delete designation"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Pagination Footer */}
      {totalPages > 1 && (
        <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl flex items-center justify-between text-xs font-medium text-slate-500">
          <div>
            Showing <span className="font-bold text-slate-900 dark:text-white">{designations.length}</span> of <span className="font-bold text-slate-900 dark:text-white">{totalCount}</span> designations
          </div>
          <div className="flex items-center gap-2">
            <button
              disabled={currentPage <= 1}
              onClick={() => setCurrentPage(p => p - 1)}
              className="p-2 border border-slate-200 dark:border-slate-800 rounded-xl disabled:opacity-40 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <span>
              Page <strong className="text-slate-900 dark:text-white">{currentPage}</strong> of {totalPages}
            </span>
            <button
              disabled={currentPage >= totalPages}
              onClick={() => setCurrentPage(p => p + 1)}
              className="p-2 border border-slate-200 dark:border-slate-800 rounded-xl disabled:opacity-40 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}

      {/* CREATE / EDIT DESIGNATION MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-lg p-6 sm:p-8 space-y-6 shadow-2xl animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
              <div>
                <h2 className="text-lg font-extrabold text-slate-900 dark:text-white">
                  {editingDesignation ? 'Edit Designation' : 'Create New Designation'}
                </h2>
                <p className="text-xs text-slate-500">
                  {editingDesignation ? 'Update grade, compensation band, or department' : 'Add job role to corporate designation hierarchy'}
                </p>
              </div>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Designation Title *</label>
                  <input
                    type="text"
                    required
                    value={formData.title || ''}
                    onChange={(e) => setFormData(prev => ({ ...prev, title: e.target.value }))}
                    placeholder="e.g. Lead Security Architect"
                    className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Role Code *</label>
                  <input
                    type="text"
                    required
                    value={formData.code || ''}
                    onChange={(e) => setFormData(prev => ({ ...prev, code: e.target.value.toUpperCase() }))}
                    placeholder="e.g. ENG-LSA"
                    className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-mono uppercase"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Hierarchy Grade *</label>
                  <select
                    value={formData.grade || 'L2'}
                    onChange={(e) => setFormData(prev => ({ ...prev, grade: e.target.value as any }))}
                    className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-semibold"
                  >
                    <option value="L7">Grade L7 — Executive C-Suite (Level 7)</option>
                    <option value="L6">Grade L6 — Director / VP (Level 6)</option>
                    <option value="L5">Grade L5 — Senior Manager (Level 5)</option>
                    <option value="L4">Grade L4 — Lead Specialist (Level 4)</option>
                    <option value="L3">Grade L3 — Senior Staff (Level 3)</option>
                    <option value="L2">Grade L2 — Mid Professional (Level 2)</option>
                    <option value="L1">Grade L1 — Associate / Junior (Level 1)</option>
                  </select>
                </div>

                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Department Unit *</label>
                  <select
                    value={formData.departmentName || ''}
                    onChange={(e) => setFormData(prev => ({ ...prev, departmentName: e.target.value }))}
                    className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-medium"
                  >
                    {departments.map((d) => (
                      <option key={d.id} value={d.name}>{d.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Min Salary ($) *</label>
                  <input
                    type="number"
                    required
                    value={formData.minSalary || 85000}
                    onChange={(e) => setFormData(prev => ({ ...prev, minSalary: Number(e.target.value) }))}
                    className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Max Salary ($) *</label>
                  <input
                    type="number"
                    required
                    value={formData.maxSalary || 130000}
                    onChange={(e) => setFormData(prev => ({ ...prev, maxSalary: Number(e.target.value) }))}
                    className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Status</label>
                <select
                  value={formData.status || 'Active'}
                  onChange={(e) => setFormData(prev => ({ ...prev, status: e.target.value as any }))}
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                >
                  <option value="Active">Active</option>
                  <option value="Inactive">Inactive</option>
                </select>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Role Responsibilities</label>
                <textarea
                  rows={2}
                  value={formData.description || ''}
                  onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                  placeholder="Responsibilities, required background, and grade scope..."
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                ></textarea>
              </div>

              <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100 dark:border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center gap-2"
                >
                  <Save className="w-4 h-4" />
                  {editingDesignation ? 'Save Designation' : 'Create Designation'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation */}
      <ConfirmationDialog
        isOpen={Boolean(deletingDesignation)}
        title="Delete Designation"
        message={`Are you sure you want to delete designation "${deletingDesignation?.title}"?`}
        confirmLabel="Delete Designation"
        variant="danger"
        onConfirm={handleDeleteConfirm}
        onClose={() => setDeletingDesignation(null)}
      />
    </div>
  );
};
