import React, { useState, useEffect, useMemo } from 'react';
import {
  Shield,
  Lock,
  CheckSquare,
  Square,
  Save,
  CheckCircle2,
  Key,
  Layers,
  Sparkles,
  Loader2,
  Users,
  Search,
  RotateCcw,
  AlertTriangle,
  X,
  Filter,
  Check,
  UserCheck,
  Eye,
  Sliders
} from 'lucide-react';
import { permissionsApi } from '../../api/coreServices';
import { DynamicRole, RoleAssignedUser } from '../../types';
import { useAuth } from '../../context/AuthContext';

const MODULE_CATEGORIES = [
  { id: 'ALL', label: 'All Modules' },
  { id: 'User Management', label: 'User Management' },
  { id: 'Employees', label: 'Employees Directory' },
  { id: 'Interns', label: 'Intern Cohort' },
  { id: 'Roles & Permissions', label: 'Roles & Permissions' },
  { id: 'Projects', label: 'Projects & Sprints' },
  { id: 'Tasks', label: 'Tasks Delegation' },
  { id: 'Vault & Documents', label: 'Vault & Documents' },
  { id: 'Engineering', label: 'Engineering & Repos' },
  { id: 'Collaboration', label: 'Meetings & Events' },
  { id: 'Audit & Compliance', label: 'Audit & Activity Logs' },
  { id: 'System', label: 'System Settings' },
];

const ALL_PERMISSIONS = [
  { code: 'USER_READ', name: 'View User Accounts', module: 'User Management', desc: 'Allows viewing user profiles & system credentials' },
  { code: 'USER_CREATE', name: 'Create User Accounts', module: 'User Management', desc: 'Allows creating new user login credentials' },
  { code: 'USER_WRITE', name: 'Edit User Accounts', module: 'User Management', desc: 'Allows modifying user accounts & credentials' },
  { code: 'USER_DELETE', name: 'Delete User Accounts', module: 'User Management', desc: 'Allows permanently deactivating user accounts' },

  { code: 'EMPLOYEE_READ', name: 'View Employee Directory', module: 'Employees', desc: 'Access staff directory & employee profiles' },
  { code: 'EMPLOYEE_CREATE', name: 'Add New Employee', module: 'Employees', desc: 'Create new employee profiles & onboarding' },
  { code: 'EMPLOYEE_WRITE', name: 'Edit Employee Records', module: 'Employees', desc: 'Update designation, salary & department' },
  { code: 'EMPLOYEE_DELETE', name: 'Terminate / Remove Employee', module: 'Employees', desc: 'Deactivate or offboard employees' },

  { code: 'INTERN_READ', name: 'View Intern Cohort', module: 'Interns', desc: 'Access intern directory & performance scores' },
  { code: 'INTERN_CREATE', name: 'Add New Intern', module: 'Interns', desc: 'Onboard new interns into the cohort' },
  { code: 'INTERN_WRITE', name: 'Edit Intern Record', module: 'Interns', desc: 'Update intern mentors, tasks & stipend' },
  { code: 'INTERN_DELETE', name: 'Remove Intern', module: 'Interns', desc: 'Offboard or terminate intern record' },

  { code: 'ROLE_READ', name: 'View Role Engine', module: 'Roles & Permissions', desc: 'View roles, perms & security matrix' },
  { code: 'ROLE_WRITE', name: 'Configure Roles & Matrix', module: 'Roles & Permissions', desc: 'Modify system roles, route access & feature flags' },
  { code: 'ROLE_DELETE', name: 'Delete Custom Roles', module: 'Roles & Permissions', desc: 'Delete non-baseline custom enterprise roles' },

  { code: 'PROJECT_READ', name: 'View Projects', module: 'Projects', desc: 'Access enterprise projects & sprint deliverables' },
  { code: 'PROJECT_CREATE', name: 'Create Projects', module: 'Projects', desc: 'Initialize new projects & assign budgets' },
  { code: 'PROJECT_WRITE', name: 'Edit Projects', module: 'Projects', desc: 'Update project status, progress & team leads' },
  { code: 'PROJECT_DELETE', name: 'Delete Projects', module: 'Projects', desc: 'Archive or remove enterprise projects' },

  { code: 'TASK_READ', name: 'View Tasks', module: 'Tasks', desc: 'Access sprint tasks & task boards' },
  { code: 'TASK_CREATE', name: 'Create Tasks', module: 'Tasks', desc: 'Create new tasks & sprint backlog items' },
  { code: 'TASK_WRITE', name: 'Edit / Update Tasks', module: 'Tasks', desc: 'Modify task status, priority & progress' },
  { code: 'TASK_ASSIGN', name: 'Assign Tasks', module: 'Tasks', desc: 'Delegate tasks to team members & interns' },
  { code: 'TASK_DELETE', name: 'Delete Tasks', module: 'Tasks', desc: 'Remove tasks from project boards' },

  { code: 'DOCUMENT_READ', name: 'View Storage Vault', module: 'Vault & Documents', desc: 'Access document vault & company files' },
  { code: 'DOCUMENT_UPLOAD', name: 'Upload Vault Files', module: 'Vault & Documents', desc: 'Upload documents & Cloudinary assets' },
  { code: 'DOCUMENT_WRITE', name: 'Edit Document Metadata', module: 'Vault & Documents', desc: 'Modify document titles, categories & privacy' },
  { code: 'DOCUMENT_DELETE', name: 'Delete Vault Files', module: 'Vault & Documents', desc: 'Remove documents from central storage' },
  { code: 'DOCUMENT_DOWNLOAD', name: 'Download Vault Files', module: 'Vault & Documents', desc: 'Download original document files' },

  { code: 'REPOSITORY_READ', name: 'View Repositories', module: 'Engineering', desc: 'View GitHub code repositories & status' },
  { code: 'REPOSITORY_CREATE', name: 'Create Repositories', module: 'Engineering', desc: 'Initialize code repositories' },
  { code: 'REPOSITORY_WRITE', name: 'Push Code / Pull Requests', module: 'Engineering', desc: 'Commit code and manage pull requests' },

  { code: 'MEETING_READ', name: 'View Corporate Calendar', module: 'Collaboration', desc: 'View corporate meetings & standups' },
  { code: 'MEETING_CREATE', name: 'Schedule Meetings', module: 'Collaboration', desc: 'Create new calendar events & syncs' },

  { code: 'ACTIVITY_READ', name: 'View System Activity Logs', module: 'Audit & Compliance', desc: 'Inspect real-time system activity logs' },
  { code: 'AUDIT_LOG_READ', name: 'View Immutable Audit Trail', module: 'Audit & Compliance', desc: 'Inspect security audit logs & change history' },

  { code: 'REPORT_READ', name: 'View Executive Analytics', module: 'Reports', desc: 'Access executive reports & analytics' },
  { code: 'REPORT_EXPORT', name: 'Export Reports', module: 'Reports', desc: 'Export report data to PDF/CSV' },

  { code: 'APPROVAL_READ', name: 'View Approval Queue', module: 'Approvals', desc: 'Access pending approval workflow requests' },
  { code: 'APPROVAL_APPROVE', name: 'Approve Workflows', module: 'Approvals', desc: 'Sanction leave, expense & budget requests' },
  { code: 'APPROVAL_REJECT', name: 'Reject Workflows', module: 'Approvals', desc: 'Reject approval workflow requests' },

  { code: 'SETTINGS_READ', name: 'View System Configuration', module: 'System', desc: 'Access system settings & environment config' },
  { code: 'SETTINGS_WRITE', name: 'Modify System Configuration', module: 'System', desc: 'Update system parameters, SMTP & integration keys' },

  { code: 'DASHBOARD_READ', name: 'Access Core Dashboard', module: 'Dashboard', desc: 'Access primary dashboard view' },
];

const AVAILABLE_MENUS = [
  { path: '/dashboard', label: 'Unified Dashboard', category: 'Core' },
  { path: '/admin', label: 'Executive Governance', category: 'Governance' },
  { path: '/manager', label: 'Manager Desk', category: 'Governance' },
  { path: '/employee', label: 'Employee Desk', category: 'Operations' },
  { path: '/intern', label: 'Intern Desk', category: 'Operations' },
  { path: '/customer', label: 'Customer Space', category: 'Client Space' },
  { path: '/employees', label: 'Employees Directory', category: 'Human Capital' },
  { path: '/interns', label: 'Interns Cohort', category: 'Human Capital' },
  { path: '/customers', label: 'Customer Accounts', category: 'Client Space' },
  { path: '/projects', label: 'Projects & Deliverables', category: 'Execution' },
  { path: '/tasks', label: 'Task Backlog', category: 'Execution' },
  { path: '/repositories', label: 'Code Repositories', category: 'Engineering' },
  { path: '/documents', label: 'Storage & Vault', category: 'Enterprise Core' },
  { path: '/meetings', label: 'Corporate Calendar', category: 'Execution' },
  { path: '/reports', label: 'Executive Analytics', category: 'Growth' },
  { path: '/payroll', label: 'Payroll & Compensation', category: 'Human Capital' },
  { path: '/attendance', label: 'Attendance & Time Logs', category: 'Human Capital' },
  { path: '/leave', label: 'Leave Management', category: 'Human Capital' },
  { path: '/crm', label: 'CRM & Deals Pipeline', category: 'Growth' },
  { path: '/recruitment', label: 'Talent Recruitment', category: 'Growth' },
  { path: '/audit-logs', label: 'Activity & Audit Logs', category: 'Enterprise Core' },
  { path: '/roles-permissions', label: 'Roles & Permissions', category: 'Enterprise Core' },
  { path: '/settings', label: 'System Configuration', category: 'Support' },
  { path: '/profile', label: 'Account Profile', category: 'Support' },
];

export const RolesPermissionsPage: React.FC = () => {
  const { user, refetchPermissions } = useAuth();
  const [roles, setRoles] = useState<DynamicRole[]>([]);
  const [pristineRoles, setPristineRoles] = useState<DynamicRole[]>([]);
  const [selectedRoleCode, setSelectedRoleCode] = useState<string>('ROLE_CEO');
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedModule, setSelectedModule] = useState('ALL');
  
  // Modals
  const [showUsersModal, setShowUsersModal] = useState(false);
  const [showResetConfirmModal, setShowResetConfirmModal] = useState(false);
  const [showUnsavedConfirmModal, setShowUnsavedConfirmModal] = useState<string | null>(null);

  const loadRoles = async () => {
    try {
      setIsLoading(true);
      const data = await permissionsApi.getRoles();
      setRoles(data);
      setPristineRoles(JSON.parse(JSON.stringify(data)));
      if (data.length > 0 && !data.some((r) => r.role === selectedRoleCode)) {
        setSelectedRoleCode(data[0].role);
      }
    } catch (err: any) {
      setErrorMessage(err.message || 'Failed to load roles from server.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadRoles();
  }, []);

  const selectedRole = useMemo(
    () => roles.find((r) => r.role === selectedRoleCode) || roles[0],
    [roles, selectedRoleCode]
  );

  const pristineSelectedRole = useMemo(
    () => pristineRoles.find((r) => r.role === selectedRoleCode),
    [pristineRoles, selectedRoleCode]
  );

  // Check unsaved changes
  const hasUnsavedChanges = useMemo(() => {
    if (!selectedRole || !pristineSelectedRole) return false;
    const p1 = JSON.stringify(selectedRole.permissions || []);
    const p2 = JSON.stringify(pristineSelectedRole.permissions || []);
    const m1 = JSON.stringify(selectedRole.menuPermissions || []);
    const m2 = JSON.stringify(pristineSelectedRole.menuPermissions || []);
    const f1 = JSON.stringify(selectedRole.featureFlags || {});
    const f2 = JSON.stringify(pristineSelectedRole.featureFlags || {});
    return p1 !== p2 || m1 !== m2 || f1 !== f2;
  }, [selectedRole, pristineSelectedRole]);

  // Filtered Permissions
  const filteredPermissions = useMemo(() => {
    return ALL_PERMISSIONS.filter((p) => {
      const matchesSearch =
        p.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
        p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        p.desc.toLowerCase().includes(searchQuery.toLowerCase());
      const matchesModule = selectedModule === 'ALL' || p.module === selectedModule;
      return matchesSearch && matchesModule;
    });
  }, [searchQuery, selectedModule]);

  const handleRoleSelect = (targetCode: string) => {
    if (hasUnsavedChanges && targetCode !== selectedRoleCode) {
      setShowUnsavedConfirmModal(targetCode);
    } else {
      setSelectedRoleCode(targetCode);
    }
  };

  const handleTogglePermission = (permCode: string) => {
    if (!selectedRole) return;
    const currentPerms = selectedRole.permissions || [];
    const hasPerm = currentPerms.includes(permCode);
    const updated = hasPerm
      ? currentPerms.filter((p) => p !== permCode)
      : [...currentPerms, permCode];

    setRoles((prev) =>
      prev.map((r) => (r.role === selectedRoleCode ? { ...r, permissions: updated } : r))
    );
  };

  const handleToggleMenu = (menuPath: string) => {
    if (!selectedRole) return;
    const currentMenus = selectedRole.menuPermissions || [];
    const hasMenu = currentMenus.includes(menuPath);
    const updated = hasMenu
      ? currentMenus.filter((m) => m !== menuPath)
      : [...currentMenus, menuPath];

    setRoles((prev) =>
      prev.map((r) => (r.role === selectedRoleCode ? { ...r, menuPermissions: updated } : r))
    );
  };

  const handleToggleFeature = (flagKey: string) => {
    if (!selectedRole) return;
    const currentFlags = selectedRole.featureFlags || {};
    const val = !!currentFlags[flagKey];
    setRoles((prev) =>
      prev.map((r) =>
        r.role === selectedRoleCode
          ? { ...r, featureFlags: { ...currentFlags, [flagKey]: !val } }
          : r
      )
    );
  };

  // Bulk Actions
  const handleBulkEnableModule = () => {
    if (!selectedRole) return;
    const codesToEnable = filteredPermissions.map((p) => p.code);
    const currentPerms = new Set(selectedRole.permissions || []);
    codesToEnable.forEach((c) => currentPerms.add(c));

    setRoles((prev) =>
      prev.map((r) =>
        r.role === selectedRoleCode ? { ...r, permissions: Array.from(currentPerms) } : r
      )
    );
  };

  const handleBulkDisableModule = () => {
    if (!selectedRole) return;
    const codesToDisable = new Set(filteredPermissions.map((p) => p.code));
    const currentPerms = selectedRole.permissions || [];
    const updated = currentPerms.filter((c) => !codesToDisable.has(c));

    setRoles((prev) =>
      prev.map((r) => (r.role === selectedRoleCode ? { ...r, permissions: updated } : r))
    );
  };

  const handleSelectAllPermissions = () => {
    if (!selectedRole) return;
    const allCodes = ALL_PERMISSIONS.map((p) => p.code);
    setRoles((prev) =>
      prev.map((r) => (r.role === selectedRoleCode ? { ...r, permissions: allCodes } : r))
    );
  };

  const handleClearAllPermissions = () => {
    if (!selectedRole) return;
    setRoles((prev) =>
      prev.map((r) => (r.role === selectedRoleCode ? { ...r, permissions: [] } : r))
    );
  };

  const handleDiscardChanges = () => {
    setRoles(JSON.parse(JSON.stringify(pristineRoles)));
    setShowUnsavedConfirmModal(null);
  };

  const handleSave = async () => {
    if (!selectedRole) return;
    setIsSaving(true);
    setSaveSuccess(null);
    setErrorMessage(null);

    try {
      const updatedList = await permissionsApi.updateRole(
        selectedRole.role,
        {
          permissions: selectedRole.permissions,
          menuPermissions: selectedRole.menuPermissions,
          featureFlags: selectedRole.featureFlags,
        },
        `${user?.firstName} ${user?.lastName}`
      );

      setRoles(updatedList);
      setPristineRoles(JSON.parse(JSON.stringify(updatedList)));
      await refetchPermissions();
      setSaveSuccess(`Permission matrix saved successfully for ${selectedRole.displayName}!`);
      setTimeout(() => setSaveSuccess(null), 4000);
    } catch (err: any) {
      const serverMsg = err.response?.data?.message || err.message || 'Failed to save permission matrix.';
      setErrorMessage(serverMsg);
    } finally {
      setIsSaving(false);
    }
  };

  const handleResetToBaseline = async () => {
    if (!selectedRole) return;
    try {
      setIsSaving(true);
      setShowResetConfirmModal(false);
      const updatedList = await permissionsApi.resetRole(selectedRole.role);
      setRoles(updatedList);
      setPristineRoles(JSON.parse(JSON.stringify(updatedList)));
      await refetchPermissions();
      setSaveSuccess(`Role ${selectedRole.displayName} reset to baseline default configuration.`);
      setTimeout(() => setSaveSuccess(null), 4000);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || err.message || 'Reset failed');
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <Loader2 className="w-10 h-10 text-indigo-600 animate-spin" />
        <p className="text-xs font-extrabold text-slate-600 dark:text-slate-400">Loading Enterprise Role & Permission Engine...</p>
      </div>
    );
  }

  if (!selectedRole) return null;

  return (
    <div className="space-y-8 pb-16">
      {/* Top Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-bold text-xs uppercase tracking-wider mb-1">
            <Shield className="w-4 h-4" />
            <span>Universal Enterprise Security & Access Control</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Role & Permission Engine</h1>
          <p className="text-xs text-slate-500">Configure fine-grained system perms, menu route access & feature flags persisted to MongoDB</p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setShowResetConfirmModal(true)}
            className="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-300 font-bold text-xs rounded-xl transition-all flex items-center gap-2"
          >
            <RotateCcw className="w-3.5 h-3.5" /> Reset Role Default
          </button>

          <button
            onClick={handleSave}
            disabled={isSaving || !hasUnsavedChanges}
            className={`px-5 py-2.5 text-white font-extrabold text-xs rounded-xl transition-all shadow-md flex items-center gap-2 ${
              hasUnsavedChanges
                ? 'bg-indigo-600 hover:bg-indigo-500 shadow-indigo-500/20 ring-2 ring-indigo-500/50'
                : 'bg-slate-400 dark:bg-slate-700 opacity-60 cursor-not-allowed'
            }`}
          >
            {isSaving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />} Save Permission Matrix
          </button>
        </div>
      </div>

      {/* Notifications / Alerts */}
      {saveSuccess && (
        <div className="p-4 bg-emerald-50 dark:bg-emerald-950/50 border border-emerald-200 dark:border-emerald-800 rounded-2xl text-xs text-emerald-800 dark:text-emerald-200 font-bold flex items-center justify-between">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
            <span>{saveSuccess}</span>
          </div>
          <button onClick={() => setSaveSuccess(null)}><X className="w-4 h-4" /></button>
        </div>
      )}

      {errorMessage && (
        <div className="p-4 bg-rose-50 dark:bg-rose-950/50 border border-rose-200 dark:border-rose-800 rounded-2xl text-xs text-rose-800 dark:text-rose-200 font-bold flex items-center justify-between">
          <div className="flex items-center gap-2">
            <AlertTriangle className="w-4 h-4 text-rose-600 dark:text-rose-400" />
            <span>{errorMessage}</span>
          </div>
          <button onClick={() => setErrorMessage(null)}><X className="w-4 h-4" /></button>
        </div>
      )}

      {/* Unsaved Changes Banner */}
      {hasUnsavedChanges && (
        <div className="p-4 bg-amber-50 dark:bg-amber-950/50 border border-amber-300 dark:border-amber-700 rounded-2xl text-xs text-amber-900 dark:text-amber-200 font-bold flex items-center justify-between shadow-xs">
          <div className="flex items-center gap-2">
            <Sliders className="w-4 h-4 text-amber-600 animate-pulse" />
            <span>You have unsaved permission changes for {selectedRole.displayName}.</span>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handleDiscardChanges}
              className="px-3 py-1 bg-amber-100 dark:bg-amber-900 hover:bg-amber-200 text-amber-900 dark:text-amber-100 text-[11px] font-extrabold rounded-lg"
            >
              Discard
            </button>
            <button
              onClick={handleSave}
              className="px-3 py-1 bg-amber-600 hover:bg-amber-500 text-white text-[11px] font-extrabold rounded-lg shadow-xs"
            >
              Save Now
            </button>
          </div>
        </div>
      )}

      {/* Dynamic Role Cards Header */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3">
        {roles.map((r) => {
          const isSelected = selectedRoleCode === r.role;
          return (
            <div
              key={r.role}
              onClick={() => handleRoleSelect(r.role)}
              className={`p-4 rounded-2xl border text-left cursor-pointer transition-all relative ${
                isSelected
                  ? 'border-indigo-600 bg-indigo-50/60 dark:bg-indigo-950/50 shadow-md ring-2 ring-indigo-500/20'
                  : 'border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 hover:border-slate-300 dark:hover:border-slate-700 shadow-xs'
              }`}
            >
              <div className="flex items-center justify-between">
                <span className="text-xs font-extrabold text-slate-900 dark:text-white truncate">{r.displayName}</span>
                <span className="px-2 py-0.5 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 text-[9px] font-bold">
                  Rank {r.hierarchyLevel || 30}
                </span>
              </div>
              <div className="text-[10px] text-slate-500 font-mono mt-0.5">{r.role}</div>

              <div className="mt-3 flex items-center justify-between text-[10px]">
                <span className="text-indigo-600 dark:text-indigo-400 font-bold">
                  {r.permissions?.length || 0} Perms
                </span>
                <span className="text-emerald-600 dark:text-emerald-400 font-extrabold flex items-center gap-1">
                  <Users className="w-3 h-3" /> {r.userCount || 0} Users
                </span>
              </div>

              {isSelected && (
                <div className="mt-2 pt-2 border-t border-indigo-200 dark:border-indigo-800/60 flex items-center justify-between text-[9px] text-slate-500">
                  <span>Updated by {r.updatedBy?.split(' ')[0] || 'Admin'}</span>
                  <span className="text-indigo-600 dark:text-indigo-400 font-bold">Selected</span>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Role Details Bar */}
      <div className="p-4 bg-slate-900 dark:bg-slate-800 rounded-2xl text-white flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-sm">
        <div className="flex items-center gap-3">
          <div className="p-2.5 rounded-xl bg-indigo-600 text-white font-mono font-bold text-xs">
            {selectedRole.role}
          </div>
          <div>
            <div className="font-extrabold text-sm flex items-center gap-2">
              {selectedRole.displayName}
              <span className="px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 text-[10px] font-bold">Active</span>
            </div>
            <p className="text-xs text-slate-400">{selectedRole.description}</p>
          </div>
        </div>

        <button
          onClick={() => setShowUsersModal(true)}
          className="px-4 py-2 bg-indigo-500/20 hover:bg-indigo-500/30 text-indigo-200 font-extrabold text-xs rounded-xl transition-all border border-indigo-400/30 flex items-center gap-2 self-start sm:self-auto"
        >
          <UserCheck className="w-4 h-4 text-indigo-400" /> View {selectedRole.userCount || 0} Assigned Users
        </button>
      </div>

      {/* Filter & Search Bar for Permissions */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 p-4 rounded-2xl shadow-xs">
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 flex-1">
          {/* Search */}
          <div className="relative flex-1 max-w-md">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search permissions by code, title or module..."
              className="w-full pl-9 pr-4 py-1.5 text-xs rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
            />
          </div>

          {/* Module Filter */}
          <div className="flex items-center gap-2">
            <Filter className="w-4 h-4 text-slate-400" />
            <select
              value={selectedModule}
              onChange={(e) => setSelectedModule(e.target.value)}
              className="px-3 py-1.5 text-xs rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white font-semibold"
            >
              {MODULE_CATEGORIES.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Bulk Action Buttons */}
        <div className="flex items-center gap-2 wrap">
          <button
            onClick={handleBulkEnableModule}
            title="Enable all permissions matching filter"
            className="px-3 py-1.5 bg-emerald-50 hover:bg-emerald-100 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 font-extrabold text-[11px] rounded-xl border border-emerald-200 dark:border-emerald-800"
          >
            Enable Module
          </button>
          <button
            onClick={handleBulkDisableModule}
            title="Disable all permissions matching filter"
            className="px-3 py-1.5 bg-rose-50 hover:bg-rose-100 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 font-extrabold text-[11px] rounded-xl border border-rose-200 dark:border-rose-800"
          >
            Disable Module
          </button>
          <button
            onClick={handleSelectAllPermissions}
            className="px-3 py-1.5 bg-indigo-50 hover:bg-indigo-100 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-300 font-extrabold text-[11px] rounded-xl border border-indigo-200 dark:border-indigo-800"
          >
            Select All
          </button>
          <button
            onClick={handleClearAllPermissions}
            className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-extrabold text-[11px] rounded-xl"
          >
            Clear All
          </button>
        </div>
      </div>

      {/* 3 Matrix Panels Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* 1. API & Operations Permissions Catalog */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-xs">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <h3 className="font-extrabold text-sm text-slate-900 dark:text-white flex items-center gap-2">
              <Key className="w-4 h-4 text-indigo-500" /> API & Operations Permissions
            </h3>
            <span className="text-[10px] font-extrabold px-2.5 py-0.5 rounded-full bg-indigo-100 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300">
              {filteredPermissions.filter((p) => selectedRole.permissions?.includes(p.code)).length} / {filteredPermissions.length} Enabled
            </span>
          </div>

          <div className="space-y-2 max-h-[600px] overflow-y-auto custom-scrollbar pr-1">
            {filteredPermissions.length === 0 ? (
              <div className="text-center py-8 text-xs text-slate-400 font-bold">
                No permissions found matching query.
              </div>
            ) : (
              filteredPermissions.map((perm) => {
                const isChecked = (selectedRole.permissions || []).includes(perm.code);
                return (
                  <div
                    key={perm.code}
                    onClick={() => handleTogglePermission(perm.code)}
                    className={`p-3 rounded-2xl border transition-all cursor-pointer ${
                      isChecked
                        ? 'bg-indigo-50/50 hover:bg-indigo-50 dark:bg-indigo-950/30 dark:hover:bg-indigo-950/60 border-indigo-200 dark:border-indigo-800/80'
                        : 'bg-slate-50 hover:bg-slate-100 dark:bg-slate-800/30 dark:hover:bg-slate-800/70 border-slate-200/60 dark:border-slate-800'
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="font-mono text-xs font-extrabold text-slate-900 dark:text-white">{perm.code}</span>
                      {isChecked ? (
                        <CheckSquare className="w-4.5 h-4.5 text-indigo-600 dark:text-indigo-400 shrink-0" />
                      ) : (
                        <Square className="w-4.5 h-4.5 text-slate-400 shrink-0" />
                      )}
                    </div>
                    <div className="font-bold text-[11px] text-slate-700 dark:text-slate-300 mt-1">{perm.name}</div>
                    <div className="text-[10px] text-slate-500 mt-0.5 flex items-center justify-between">
                      <span>{perm.desc}</span>
                      <span className="font-semibold px-1.5 py-0.2 rounded bg-slate-200/60 dark:bg-slate-800 text-slate-600 dark:text-slate-400 text-[9px]">
                        {perm.module}
                      </span>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* 2. Menu Navigation Visibility */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-xs">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <h3 className="font-extrabold text-sm text-slate-900 dark:text-white flex items-center gap-2">
              <Layers className="w-4 h-4 text-purple-500" /> Menu Routes Visibility
            </h3>
            <span className="text-[10px] font-extrabold px-2.5 py-0.5 rounded-full bg-purple-100 dark:bg-purple-950 text-purple-700 dark:text-purple-300">
              {selectedRole.menuPermissions?.length || 0} / {AVAILABLE_MENUS.length} Routes
            </span>
          </div>

          <div className="space-y-2 max-h-[600px] overflow-y-auto custom-scrollbar pr-1">
            {AVAILABLE_MENUS.map((m) => {
              const isChecked = (selectedRole.menuPermissions || []).includes(m.path);
              return (
                <div
                  key={m.path}
                  onClick={() => handleToggleMenu(m.path)}
                  className={`p-3 rounded-2xl border transition-all cursor-pointer flex items-center justify-between ${
                    isChecked
                      ? 'bg-purple-50/50 hover:bg-purple-50 dark:bg-purple-950/30 dark:hover:bg-purple-950/60 border-purple-200 dark:border-purple-800/80'
                      : 'bg-slate-50 hover:bg-slate-100 dark:bg-slate-800/30 dark:hover:bg-slate-800/70 border-slate-200/60 dark:border-slate-800'
                  }`}
                >
                  <div>
                    <div className="font-extrabold text-xs text-slate-900 dark:text-white flex items-center gap-2">
                      <span>{m.label}</span>
                      <span className="text-[9px] px-1.5 py-0.2 rounded bg-slate-200/60 dark:bg-slate-800 text-slate-500 font-normal">
                        {m.category}
                      </span>
                    </div>
                    <div className="font-mono text-[10px] text-slate-500 mt-0.5">{m.path}</div>
                  </div>
                  {isChecked ? (
                    <CheckSquare className="w-4.5 h-4.5 text-purple-600 dark:text-purple-400 shrink-0" />
                  ) : (
                    <Square className="w-4.5 h-4.5 text-slate-400 shrink-0" />
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* 3. Feature Flags */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-xs">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <h3 className="font-extrabold text-sm text-slate-900 dark:text-white flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-cyan-500" /> Module Feature Flags
            </h3>
            <span className="text-[10px] font-extrabold px-2.5 py-0.5 rounded-full bg-cyan-100 dark:bg-cyan-950 text-cyan-700 dark:text-cyan-300">
              {Object.values(selectedRole.featureFlags || {}).filter(Boolean).length} Active
            </span>
          </div>

          <div className="space-y-3">
            {[
              { key: 'enableAuditLogs', title: 'Audit Trail Inspection', desc: 'Allows viewing immutable system activity logs' },
              { key: 'enableCloudinaryUploads', title: 'Cloudinary Asset Storage', desc: 'Allows uploading documents to Cloudinary vault' },
              { key: 'enableApprovalWorkflows', title: 'Approval Workflow Engine', desc: 'Allows acting as an approver in workflows' },
              { key: 'enableGithubIntegration', title: 'GitHub Code Integration', desc: 'Allows viewing repository pull requests & status' },
              { key: 'enableEmployeeManagement', title: 'Employee Directory & Mgmt', desc: 'Access employee directory & management' },
              { key: 'enableInternManagement', title: 'Internship Cohort Operations', desc: 'Access intern tasks & cohort certificates' },
              { key: 'enableProjectManagement', title: 'Project & Sprint Delivery', desc: 'Access project sprint boards & delivery' },
              { key: 'enableTaskManagement', title: 'Task Delegation & Tracking', desc: 'Task delegation & time logs' },
            ].map((f) => {
              const isEnabled = !!(selectedRole.featureFlags || {})[f.key];
              return (
                <div
                  key={f.key}
                  onClick={() => handleToggleFeature(f.key)}
                  className={`p-3.5 rounded-2xl border cursor-pointer transition-all flex items-center justify-between ${
                    isEnabled
                      ? 'bg-cyan-50/50 hover:bg-cyan-50 dark:bg-cyan-950/30 dark:hover:bg-cyan-950/60 border-cyan-200 dark:border-cyan-800'
                      : 'bg-slate-50 dark:bg-slate-800/30 border-slate-200/60 dark:border-slate-800'
                  }`}
                >
                  <div>
                    <div className="font-bold text-xs text-slate-900 dark:text-white">{f.title}</div>
                    <div className="text-[11px] text-slate-500 mt-0.5">{f.desc}</div>
                  </div>
                  {isEnabled ? (
                    <span className="px-2.5 py-1 rounded-full bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 font-extrabold text-[10px] shrink-0">
                      Enabled
                    </span>
                  ) : (
                    <span className="px-2.5 py-1 rounded-full bg-slate-200 dark:bg-slate-800 text-slate-500 font-extrabold text-[10px] shrink-0">
                      Disabled
                    </span>
                  )}
                </div>
              );
            })}
          </div>
        </div>

      </div>

      {/* Modal: View Assigned Users for Selected Role */}
      {showUsersModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 max-w-2xl w-full space-y-4 shadow-2xl animate-in fade-in zoom-in duration-200">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div>
                <h3 className="font-extrabold text-base text-slate-900 dark:text-white flex items-center gap-2">
                  <UserCheck className="w-5 h-5 text-indigo-500" /> Users Assigned to {selectedRole.displayName}
                </h3>
                <p className="text-xs text-slate-500">
                  Showing active accounts linked to role code <span className="font-mono text-indigo-600 font-bold">{selectedRole.role}</span>
                </p>
              </div>
              <button
                onClick={() => setShowUsersModal(false)}
                className="p-1 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-3 max-h-[400px] overflow-y-auto custom-scrollbar pr-1">
              {(!selectedRole.assignedUsers || selectedRole.assignedUsers.length === 0) ? (
                <div className="text-center py-8 text-xs text-slate-400 font-bold">
                  No active users currently assigned to this role.
                </div>
              ) : (
                selectedRole.assignedUsers.map((u: RoleAssignedUser) => (
                  <div
                    key={u.id}
                    className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between"
                  >
                    <div className="flex items-center gap-3">
                      <img
                        src={u.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(u.fullName)}`}
                        alt={u.fullName}
                        className="w-9 h-9 rounded-full object-cover border border-slate-300 dark:border-slate-700"
                      />
                      <div>
                        <div className="font-extrabold text-xs text-slate-900 dark:text-white">{u.fullName}</div>
                        <div className="text-[11px] text-slate-500 font-mono">{u.email}</div>
                      </div>
                    </div>

                    <div className="text-right">
                      <div className="text-xs font-bold text-slate-800 dark:text-slate-200">{u.designation}</div>
                      <div className="text-[10px] text-slate-500">
                        {u.department} • <span className="text-emerald-600 font-extrabold">{u.status}</span>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>

            <div className="pt-3 border-t border-slate-100 dark:border-slate-800 flex justify-end">
              <button
                onClick={() => setShowUsersModal(false)}
                className="px-5 py-2 bg-slate-900 dark:bg-slate-800 hover:bg-slate-800 text-white font-extrabold text-xs rounded-xl"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: Reset Confirmation */}
      {showResetConfirmModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 max-w-md w-full space-y-4 shadow-2xl">
            <div className="flex items-center gap-3 text-amber-600 dark:text-amber-400">
              <AlertTriangle className="w-6 h-6" />
              <h3 className="font-extrabold text-base text-slate-900 dark:text-white">Reset Role to Default?</h3>
            </div>
            <p className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed">
              Are you sure you want to reset permissions and menu visibility for <span className="font-bold text-slate-900 dark:text-white">{selectedRole.displayName}</span> to baseline default settings?
            </p>
            <div className="flex items-center justify-end gap-3 pt-2">
              <button
                onClick={() => setShowResetConfirmModal(false)}
                className="px-4 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-bold text-xs rounded-xl"
              >
                Cancel
              </button>
              <button
                onClick={handleResetToBaseline}
                className="px-4 py-2 bg-amber-600 hover:bg-amber-500 text-white font-extrabold text-xs rounded-xl shadow-xs"
              >
                Reset Role Baseline
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: Unsaved Changes Navigation Confirmation */}
      {showUnsavedConfirmModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 max-w-md w-full space-y-4 shadow-2xl">
            <div className="flex items-center gap-3 text-amber-600 dark:text-amber-400">
              <Sliders className="w-6 h-6 animate-bounce" />
              <h3 className="font-extrabold text-base text-slate-900 dark:text-white">Unsaved Changes</h3>
            </div>
            <p className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed">
              You have unsaved permission modifications for <span className="font-bold">{selectedRole.displayName}</span>. Leaving without saving will discard your changes.
            </p>
            <div className="flex items-center justify-end gap-2 pt-2">
              <button
                onClick={() => setShowUnsavedConfirmModal(null)}
                className="px-3.5 py-2 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-bold text-xs rounded-xl"
              >
                Cancel
              </button>
              <button
                onClick={() => {
                  handleDiscardChanges();
                  setSelectedRoleCode(showUnsavedConfirmModal);
                  setShowUnsavedConfirmModal(null);
                }}
                className="px-3.5 py-2 bg-rose-600 hover:bg-rose-500 text-white font-extrabold text-xs rounded-xl shadow-xs"
              >
                Discard Changes
              </button>
              <button
                onClick={async () => {
                  await handleSave();
                  setSelectedRoleCode(showUnsavedConfirmModal);
                  setShowUnsavedConfirmModal(null);
                }}
                className="px-3.5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-xs"
              >
                Save & Continue
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
