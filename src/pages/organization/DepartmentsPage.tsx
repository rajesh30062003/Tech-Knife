import React, { useState, useEffect } from 'react';
import { 
  Building2, Plus, Search, Filter, RefreshCw, Edit3, Trash2, CheckCircle2, 
  XCircle, Users, DollarSign, MapPin, ChevronLeft, ChevronRight, Save, X, AlertCircle 
} from 'lucide-react';
import { Department, organizationApi } from '../../api/organization';
import { EmployeeData, employeesApi } from '../../api/employees';
import { ConfirmationDialog } from '../../components/employee/ConfirmationDialog';

export const DepartmentsPage: React.FC = () => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Filters & Pagination
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(6);

  // Employee list for department head selection
  const [employeesList, setEmployeesList] = useState<EmployeeData[]>([]);

  // Modal States
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [editingDepartment, setEditingDepartment] = useState<Department | null>(null);
  const [deletingDepartment, setDeletingDepartment] = useState<Department | null>(null);

  // Form State
  const [formData, setFormData] = useState<Partial<Department>>({
    name: '',
    code: '',
    headEmployeeId: '',
    status: 'Active',
    description: '',
    budget: 1000000,
    location: 'San Francisco HQ'
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
      const result = await organizationApi.getDepartments({
        search: searchQuery,
        status: selectedStatus,
        page: currentPage,
        limit: pageSize,
      });
      setDepartments(result.departments);
      setTotalCount(result.total);
      setTotalPages(result.totalPages);

      const empRes = await employeesApi.getEmployees({ limit: 100 });
      setEmployeesList(empRes.employees);
    } catch {
      setToastMessage({ type: 'error', text: 'Failed to fetch departments records.' });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [searchQuery, selectedStatus, currentPage, pageSize]);

  const handleOpenCreate = () => {
    setEditingDepartment(null);
    setFormData({
      name: '',
      code: '',
      headEmployeeId: '',
      status: 'Active',
      description: '',
      budget: 1000000,
      location: 'San Francisco HQ'
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (dept: Department) => {
    setEditingDepartment(dept);
    setFormData({ ...dept });
    setIsModalOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.code) {
      setToastMessage({ type: 'error', text: 'Please fill required fields (Name, Code).' });
      return;
    }

    const headEmp = employeesList.find(emp => emp.id === formData.headEmployeeId);
    const headName = headEmp ? `${headEmp.firstName} ${headEmp.lastName}` : '';

    try {
      if (editingDepartment) {
        await organizationApi.updateDepartment(editingDepartment.id, {
          ...formData,
          headEmployeeName: headName,
        });
        setToastMessage({ type: 'success', text: `Department "${formData.name}" updated successfully!` });
      } else {
        await organizationApi.createDepartment({
          ...formData,
          headEmployeeName: headName,
        });
        setToastMessage({ type: 'success', text: `Department "${formData.name}" created successfully!` });
      }
      setIsModalOpen(false);
      loadData();
    } catch {
      setToastMessage({ type: 'error', text: 'Failed to save department details.' });
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deletingDepartment) return;
    try {
      await organizationApi.deleteDepartment(deletingDepartment.id);
      setToastMessage({ type: 'success', text: `Department "${deletingDepartment.name}" removed.` });
      setDeletingDepartment(null);
      loadData();
    } catch {
      setToastMessage({ type: 'error', text: 'Error removing department.' });
    }
  };

  const handleToggleStatus = async (dept: Department) => {
    const nextStatus = dept.status === 'Active' ? 'Inactive' : 'Active';
    try {
      await organizationApi.updateDepartment(dept.id, { status: nextStatus });
      setToastMessage({ type: 'success', text: `${dept.name} status set to ${nextStatus}` });
      loadData();
    } catch {
      setToastMessage({ type: 'error', text: 'Failed to update status.' });
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
            <Building2 className="w-3.5 h-3.5" />
            <span>Organization Structure</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">
            Department Management
          </h1>
          <p className="text-xs text-slate-500">
            Configure business units, assign department heads, locations, and track headcount metrics
          </p>
        </div>

        <button
          onClick={handleOpenCreate}
          className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 shrink-0 self-start md:self-auto"
        >
          <Plus className="w-4 h-4" /> Add Department
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xs flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
              setCurrentPage(1);
            }}
            placeholder="Search department code, name, or head..."
            className="w-full pl-9 pr-4 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
          />
        </div>

        <div className="flex items-center gap-3 w-full sm:w-auto">
          <div className="flex items-center gap-2 text-xs text-slate-500 font-semibold shrink-0">
            <Filter className="w-3.5 h-3.5" /> Filter Status:
          </div>
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

      {/* Departments Grid Cards */}
      {isLoading ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <div className="w-6 h-6 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-2"></div>
          <p className="text-xs text-slate-400">Loading department structures...</p>
        </div>
      ) : departments.length === 0 ? (
        <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-3">
          <Building2 className="w-10 h-10 text-slate-300 dark:text-slate-700 mx-auto" />
          <h3 className="text-sm font-bold text-slate-700 dark:text-slate-300">No departments found</h3>
          <p className="text-xs text-slate-500">Try clearing search filters or add a new department.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {departments.map((dept) => (
            <div
              key={dept.id}
              className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-xs space-y-4 hover:border-indigo-300 dark:hover:border-indigo-700/50 transition-all flex flex-col justify-between"
            >
              <div className="space-y-3">
                {/* Badge Header */}
                <div className="flex items-center justify-between">
                  <span className="px-2.5 py-1 bg-indigo-50 dark:bg-indigo-950/80 text-indigo-600 dark:text-indigo-400 font-extrabold text-[10px] rounded-lg border border-indigo-200/50 uppercase tracking-wider">
                    {dept.code}
                  </span>
                  <button
                    onClick={() => handleToggleStatus(dept)}
                    className={`px-2.5 py-1 text-[10px] font-bold rounded-full flex items-center gap-1 transition-colors ${
                      dept.status === 'Active'
                        ? 'bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 border border-emerald-200/50'
                        : 'bg-slate-100 dark:bg-slate-800 text-slate-500 border border-slate-300/50'
                    }`}
                  >
                    {dept.status === 'Active' ? (
                      <CheckCircle2 className="w-3 h-3 text-emerald-500" />
                    ) : (
                      <XCircle className="w-3 h-3 text-slate-400" />
                    )}
                    <span>{dept.status}</span>
                  </button>
                </div>

                <div>
                  <h3 className="text-base font-extrabold text-slate-900 dark:text-white">
                    {dept.name}
                  </h3>
                  <p className="text-xs text-slate-500 line-clamp-2 mt-1">
                    {dept.description || 'Enterprise department division.'}
                  </p>
                </div>

                {/* Details Grid */}
                <div className="grid grid-cols-2 gap-2 pt-2 border-t border-slate-100 dark:border-slate-800/80 text-xs">
                  <div className="space-y-0.5">
                    <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">
                      Department Head
                    </span>
                    <span className="font-bold text-slate-800 dark:text-slate-200 truncate block">
                      {dept.headEmployeeName || 'Unassigned'}
                    </span>
                  </div>

                  <div className="space-y-0.5">
                    <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">
                      Staff Count
                    </span>
                    <span className="font-bold text-indigo-600 dark:text-indigo-400 flex items-center gap-1">
                      <Users className="w-3.5 h-3.5" /> {dept.employeeCount || 0} Staff
                    </span>
                  </div>

                  <div className="space-y-0.5">
                    <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">
                      Annual Budget
                    </span>
                    <span className="font-semibold text-slate-700 dark:text-slate-300 flex items-center gap-0.5">
                      <DollarSign className="w-3 h-3 text-emerald-500" />
                      {dept.budget ? dept.budget.toLocaleString() : 'N/A'}
                    </span>
                  </div>

                  <div className="space-y-0.5">
                    <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">
                      Location
                    </span>
                    <span className="font-semibold text-slate-700 dark:text-slate-300 truncate flex items-center gap-1">
                      <MapPin className="w-3 h-3 text-slate-400" />
                      {dept.location || 'Main Office'}
                    </span>
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex items-center justify-end gap-2 pt-3 border-t border-slate-100 dark:border-slate-800">
                <button
                  onClick={() => handleOpenEdit(dept)}
                  className="px-3 py-1.5 text-xs font-semibold text-indigo-600 hover:bg-indigo-50 dark:hover:bg-indigo-950/50 rounded-xl transition-colors flex items-center gap-1"
                >
                  <Edit3 className="w-3.5 h-3.5" /> Edit
                </button>

                <button
                  onClick={() => setDeletingDepartment(dept)}
                  className="px-3 py-1.5 text-xs font-semibold text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/50 rounded-xl transition-colors flex items-center gap-1"
                >
                  <Trash2 className="w-3.5 h-3.5" /> Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination Footer */}
      {totalPages > 1 && (
        <div className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl flex items-center justify-between text-xs font-medium text-slate-500">
          <div>
            Showing <span className="font-bold text-slate-900 dark:text-white">{departments.length}</span> of <span className="font-bold text-slate-900 dark:text-white">{totalCount}</span> departments
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

      {/* CREATE / EDIT DEPARTMENT MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-lg p-6 sm:p-8 space-y-6 shadow-2xl animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
              <div>
                <h2 className="text-lg font-extrabold text-slate-900 dark:text-white">
                  {editingDepartment ? 'Edit Department' : 'Create New Department'}
                </h2>
                <p className="text-xs text-slate-500">
                  {editingDepartment ? 'Update organizational department configuration' : 'Register a new enterprise business unit'}
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
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Department Name *</label>
                  <input
                    type="text"
                    required
                    value={formData.name || ''}
                    onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                    placeholder="e.g. Cybersecurity & Cloud"
                    className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Code / Abbr *</label>
                  <input
                    type="text"
                    required
                    value={formData.code || ''}
                    onChange={(e) => setFormData(prev => ({ ...prev, code: e.target.value.toUpperCase() }))}
                    placeholder="e.g. SEC"
                    className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-mono uppercase"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Department Head</label>
                <select
                  value={formData.headEmployeeId || ''}
                  onChange={(e) => setFormData(prev => ({ ...prev, headEmployeeId: e.target.value }))}
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50 font-medium"
                >
                  <option value="">-- Select Department Head --</option>
                  {employeesList.map(emp => (
                    <option key={emp.id} value={emp.id}>
                      {emp.firstName} {emp.lastName} ({emp.designation})
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
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
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Annual Budget ($)</label>
                  <input
                    type="number"
                    value={formData.budget || 1000000}
                    onChange={(e) => setFormData(prev => ({ ...prev, budget: Number(e.target.value) }))}
                    className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Primary Location</label>
                <input
                  type="text"
                  value={formData.location || ''}
                  onChange={(e) => setFormData(prev => ({ ...prev, location: e.target.value }))}
                  placeholder="e.g. San Francisco Tech Hub"
                  className="w-full px-3.5 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/50"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">Description</label>
                <textarea
                  rows={2}
                  value={formData.description || ''}
                  onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                  placeholder="Responsibilities and domain description..."
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
                  {editingDepartment ? 'Save Changes' : 'Create Department'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation */}
      <ConfirmationDialog
        isOpen={Boolean(deletingDepartment)}
        title="Delete Department"
        message={`Are you sure you want to delete the department "${deletingDepartment?.name}"? Staff belonging to this unit will need reassignment.`}
        confirmLabel="Delete Department"
        variant="danger"
        onConfirm={handleDeleteConfirm}
        onClose={() => setDeletingDepartment(null)}
      />
    </div>
  );
};
