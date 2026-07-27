import React, { useState } from 'react';
import {
  Users,
  UserPlus,
  BarChart3,
  Building2,
  CheckCircle2,
  AlertCircle,
  Sparkles,
  ShieldCheck,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';
import {
  useEmployeesQuery,
  useCreateEmployeeMutation,
  useUpdateEmployeeMutation,
  useUpdateEmployeeStatusMutation,
  useDeleteEmployeeMutation,
} from '../hooks/useEmployeeV2';
import { EmployeeFilterBarV2 } from '../components/EmployeeFilterBarV2';
import { EmployeeTableV2 } from '../components/EmployeeTableV2';
import { EmployeeFormDialogV2 } from '../components/EmployeeFormDialogV2';
import { EmployeeStatusDialogV2 } from '../components/EmployeeStatusDialogV2';
import { EmployeeDetailDialogV2 } from '../components/EmployeeDetailDialogV2';
import { EmployeeDeleteDialogV2 } from '../components/EmployeeDeleteDialogV2';
import { EmployeeResponse, EmployeeStatus } from '../types/employeeV2';
import { CreateEmployeeFormValues } from '../schemas/employeeSchema';

export const EmployeeManagementPage: React.FC = () => {
  // Search & Filter States
  const [searchTerm, setSearchTerm] = useState('');
  const [departmentId, setDepartmentId] = useState('ALL');
  const [designationId, setDesignationId] = useState('ALL');
  const [status, setStatus] = useState('ALL');
  const [employmentType, setEmploymentType] = useState('ALL');
  const [bloodGroup, setBloodGroup] = useState('ALL');
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  // Dialog States
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState<EmployeeResponse | null>(null);

  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [viewingEmployee, setViewingEmployee] = useState<EmployeeResponse | null>(null);

  const [isStatusOpen, setIsStatusOpen] = useState(false);
  const [statusEmployee, setStatusEmployee] = useState<EmployeeResponse | null>(null);

  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [deletingEmployee, setDeletingEmployee] = useState<EmployeeResponse | null>(null);

  // Toast State
  const [toast, setToast] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  const showToast = (type: 'success' | 'error', message: string) => {
    setToast({ type, message });
    setTimeout(() => setToast(null), 4000);
  };

  // Queries & Mutations
  const { data: pagedData, isLoading, refetch } = useEmployeesQuery({
    page,
    size,
    search: searchTerm,
    departmentId,
    status,
  });

  const createMutation = useCreateEmployeeMutation();
  const updateMutation = useUpdateEmployeeMutation();
  const statusMutation = useUpdateEmployeeStatusMutation();
  const deleteMutation = useDeleteEmployeeMutation();

  const handleResetFilters = () => {
    setSearchTerm('');
    setDepartmentId('ALL');
    setDesignationId('ALL');
    setStatus('ALL');
    setEmploymentType('ALL');
    setBloodGroup('ALL');
    setPage(0);
  };

  // Handle Form Submission (Create or Edit)
  const handleSaveEmployee = async (values: CreateEmployeeFormValues) => {
    try {
      if (editingEmployee) {
        await updateMutation.mutateAsync({
          id: editingEmployee.id,
          request: values,
        });
        showToast('success', `Employee profile updated for ${values.firstName} ${values.lastName}`);
      } else {
        await createMutation.mutateAsync(values);
        showToast('success', `Successfully onboarded ${values.firstName} ${values.lastName} (${values.employeeId})`);
      }
      setIsFormOpen(false);
      setEditingEmployee(null);
    } catch (err: any) {
      showToast('error', err?.message || 'An error occurred while saving employee record.');
    }
  };

  // Handle Status Update
  const handleStatusUpdate = async (newStatus: EmployeeStatus, reason?: string) => {
    if (!statusEmployee) return;
    try {
      await statusMutation.mutateAsync({
        id: statusEmployee.id,
        request: { status: newStatus, statusReason: reason },
      });
      showToast('success', `Status updated to ${newStatus} for ${statusEmployee.fullName}`);
      setIsStatusOpen(false);
      setStatusEmployee(null);
    } catch (err: any) {
      showToast('error', err?.message || 'Failed to update employee status.');
    }
  };

  // Handle Delete Confirmation
  const handleDeleteConfirm = async () => {
    if (!deletingEmployee) return;
    try {
      await deleteMutation.mutateAsync(deletingEmployee.id);
      showToast('success', `Employee record for ${deletingEmployee.fullName} deleted.`);
      setIsDeleteOpen(false);
      setDeletingEmployee(null);
    } catch (err: any) {
      showToast('error', err?.message || 'Failed to delete employee record.');
    }
  };

  const employeesList = pagedData?.content || [];
  const totalElements = pagedData?.totalElements || employeesList.length;
  const totalPages = pagedData?.totalPages || 1;

  // Stat Calculations
  const activeCount = employeesList.filter((e) => e.status === 'ACTIVE').length;
  const fullTimeCount = employeesList.filter((e) => e.employmentType === 'FULL_TIME').length;

  return (
    <div className="space-y-6 pb-12">
      {/* Toast Banner */}
      {toast && (
        <div
          className={`fixed top-5 right-5 z-50 px-4 py-3 rounded-2xl shadow-xl flex items-center gap-3 text-xs font-bold border transition-all animate-in fade-in slide-in-from-top-3 ${
            toast.type === 'success'
              ? 'bg-emerald-900 text-emerald-100 border-emerald-700'
              : 'bg-rose-900 text-rose-100 border-rose-700'
          }`}
        >
          {toast.type === 'success' ? (
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
          ) : (
            <AlertCircle className="w-4 h-4 text-rose-400 shrink-0" />
          )}
          <span>{toast.message}</span>
        </div>
      )}

      {/* Top Banner Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        <div className="space-y-1">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 text-xs font-semibold border border-indigo-200/50">
            <Users className="w-3.5 h-3.5" />
            <span>Human Capital Management V2</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">
            Employee Directory & Operations
          </h1>
          <p className="text-xs text-slate-500">
            Manage corporate headcount, employment status transitions, onboarding, and talent records.
          </p>
        </div>

        <button
          onClick={() => {
            setEditingEmployee(null);
            setIsFormOpen(true);
          }}
          className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs rounded-xl transition-all shadow-md flex items-center gap-2 shrink-0"
        >
          <UserPlus className="w-4 h-4" /> Onboard New Employee
        </button>
      </div>

      {/* Stats Widget Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 flex items-center gap-3.5 shadow-xs">
          <div className="p-3 rounded-xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400">
            <Users className="w-5 h-5" />
          </div>
          <div>
            <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Total Headcount</span>
            <div className="text-xl font-black text-slate-900 dark:text-white">{totalElements}</div>
          </div>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 flex items-center gap-3.5 shadow-xs">
          <div className="p-3 rounded-xl bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400">
            <ShieldCheck className="w-5 h-5" />
          </div>
          <div>
            <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Active Staff</span>
            <div className="text-xl font-black text-slate-900 dark:text-white">{activeCount}</div>
          </div>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 flex items-center gap-3.5 shadow-xs">
          <div className="p-3 rounded-xl bg-cyan-50 dark:bg-cyan-950/60 text-cyan-600 dark:text-cyan-400">
            <Building2 className="w-5 h-5" />
          </div>
          <div>
            <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Full Time Ratio</span>
            <div className="text-xl font-black text-slate-900 dark:text-white">{fullTimeCount} / {employeesList.length || 1}</div>
          </div>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 flex items-center gap-3.5 shadow-xs">
          <div className="p-3 rounded-xl bg-purple-50 dark:bg-purple-950/60 text-purple-600 dark:text-purple-400">
            <BarChart3 className="w-5 h-5" />
          </div>
          <div>
            <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Active Page</span>
            <div className="text-xl font-black text-slate-900 dark:text-white">{page + 1} / {totalPages}</div>
          </div>
        </div>
      </div>

      {/* Filter Bar */}
      <EmployeeFilterBarV2
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        departmentId={departmentId}
        setDepartmentId={setDepartmentId}
        designationId={designationId}
        setDesignationId={setDesignationId}
        status={status}
        setStatus={setStatus}
        employmentType={employmentType}
        setEmploymentType={setEmploymentType}
        bloodGroup={bloodGroup}
        setBloodGroup={setBloodGroup}
        onReset={handleResetFilters}
      />

      {/* Main Employee Table */}
      <EmployeeTableV2
        employees={employeesList}
        isLoading={isLoading}
        onView={(emp) => {
          setViewingEmployee(emp);
          setIsDetailOpen(true);
        }}
        onEdit={(emp) => {
          setEditingEmployee(emp);
          setIsFormOpen(true);
        }}
        onStatusChange={(emp) => {
          setStatusEmployee(emp);
          setIsStatusOpen(true);
        }}
        onDelete={(emp) => {
          setDeletingEmployee(emp);
          setIsDeleteOpen(true);
        }}
      />

      {/* Pagination Controls */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 text-xs font-medium">
        <div className="text-slate-500">
          Showing <span className="font-bold text-slate-800 dark:text-slate-200">{employeesList.length}</span> of{' '}
          <span className="font-bold text-slate-800 dark:text-slate-200">{totalElements}</span> total employee records
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
            disabled={page === 0}
            className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 rounded-xl transition-colors disabled:opacity-40"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
          <span className="px-3 text-slate-600 dark:text-slate-300 font-bold">
            Page {page + 1} of {totalPages}
          </span>
          <button
            onClick={() => setPage((prev) => Math.min(prev + 1, totalPages - 1))}
            disabled={page >= totalPages - 1}
            className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 rounded-xl transition-colors disabled:opacity-40"
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Modals & Dialogs */}
      <EmployeeFormDialogV2
        isOpen={isFormOpen}
        initialData={editingEmployee}
        onClose={() => {
          setIsFormOpen(false);
          setEditingEmployee(null);
        }}
        onSubmit={handleSaveEmployee}
        isSubmitting={createMutation.isPending || updateMutation.isPending}
      />

      <EmployeeStatusDialogV2
        isOpen={isStatusOpen}
        employee={statusEmployee}
        onClose={() => {
          setIsStatusOpen(false);
          setStatusEmployee(null);
        }}
        onSubmit={handleStatusUpdate}
        isSubmitting={statusMutation.isPending}
      />

      <EmployeeDetailDialogV2
        isOpen={isDetailOpen}
        employee={viewingEmployee}
        onClose={() => {
          setIsDetailOpen(false);
          setViewingEmployee(null);
        }}
        onEdit={(emp) => {
          setEditingEmployee(emp);
          setIsFormOpen(true);
        }}
        onDelete={(emp) => {
          setDeletingEmployee(emp);
          setIsDeleteOpen(true);
        }}
      />

      <EmployeeDeleteDialogV2
        isOpen={isDeleteOpen}
        employee={deletingEmployee}
        onClose={() => {
          setIsDeleteOpen(false);
          setDeletingEmployee(null);
        }}
        onConfirm={handleDeleteConfirm}
        isDeleting={deleteMutation.isPending}
      />
    </div>
  );
};
