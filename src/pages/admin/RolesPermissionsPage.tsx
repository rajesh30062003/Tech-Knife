import React, { useState, useEffect } from 'react';
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
  Loader2
} from 'lucide-react';
import { permissionsApi } from '../../api/coreServices';
import { DynamicRole } from '../../types';
import { useAuth } from '../../context/AuthContext';

const AVAILABLE_PERMISSIONS = [
  'USER_READ', 'USER_WRITE', 'USER_DELETE',
  'PROJECT_READ', 'PROJECT_WRITE', 'PROJECT_DELETE',
  'PAYROLL_READ', 'PAYROLL_WRITE',
  'CRM_READ', 'CRM_WRITE',
  'RECRUITMENT_READ', 'RECRUITMENT_WRITE',
  'SYSTEM_ADMIN'
];

const AVAILABLE_MENUS = [
  '/dashboard', '/admin', '/manager', '/employee', '/intern', '/customer',
  '/employees', '/interns', '/customers', '/projects', '/payroll',
  '/attendance', '/leave', '/crm', '/recruitment', '/reports', '/support',
  '/notifications', '/settings', '/profile'
];

export const RolesPermissionsPage: React.FC = () => {
  const { user } = useAuth();
  const [roles, setRoles] = useState<DynamicRole[]>([]);
  const [selectedRoleCode, setSelectedRoleCode] = useState<string>('ROLE_ADMIN');
  const [isSaving, setIsSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);

  useEffect(() => {
    const load = async () => {
      const data = await permissionsApi.getRoles();
      setRoles(data);
    };
    load();
  }, []);

  const selectedRole = roles.find((r) => r.role === selectedRoleCode) || roles[0];

  const handleTogglePermission = (perm: string) => {
    if (!selectedRole) return;
    const hasPerm = selectedRole.permissions.includes(perm);
    const updatedPerms = hasPerm
      ? selectedRole.permissions.filter((p) => p !== perm)
      : [...selectedRole.permissions, perm];

    setRoles(
      roles.map((r) => (r.role === selectedRoleCode ? { ...r, permissions: updatedPerms } : r))
    );
  };

  const handleToggleMenu = (menuPath: string) => {
    if (!selectedRole) return;
    const hasMenu = selectedRole.menuPermissions.includes(menuPath);
    const updatedMenus = hasMenu
      ? selectedRole.menuPermissions.filter((m) => m !== menuPath)
      : [...selectedRole.menuPermissions, menuPath];

    setRoles(
      roles.map((r) => (r.role === selectedRoleCode ? { ...r, menuPermissions: updatedMenus } : r))
    );
  };

  const handleToggleFeature = (flagKey: string) => {
    if (!selectedRole) return;
    const currentVal = !!selectedRole.featureFlags[flagKey];
    setRoles(
      roles.map((r) =>
        r.role === selectedRoleCode
          ? {
              ...r,
              featureFlags: { ...r.featureFlags, [flagKey]: !currentVal },
            }
          : r
      )
    );
  };

  const handleSave = async () => {
    if (!selectedRole) return;
    setIsSaving(true);
    setSaveSuccess(false);
    try {
      await permissionsApi.updateRole(
        selectedRole.role,
        selectedRole,
        `${user?.firstName} ${user?.lastName}`
      );
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 3000);
    } finally {
      setIsSaving(false);
    }
  };

  if (!selectedRole) return null;

  return (
    <div className="space-y-8 pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Shield className="w-4 h-4" />
            <span>Universal Security & Authorization Matrix</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Role & Permission Engine</h1>
          <p className="text-xs text-slate-500">Configure fine-grained system permissions, menu visibility & feature flags across enterprise roles</p>
        </div>

        <button
          onClick={handleSave}
          disabled={isSaving}
          className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl transition-all shadow-md flex items-center gap-2 self-start sm:self-auto disabled:opacity-50"
        >
          {isSaving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />} Save Permission Matrix
        </button>
      </div>

      {saveSuccess && (
        <div className="p-4 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-2xl text-xs text-emerald-800 dark:text-emerald-200 font-bold flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-600" /> Permission matrix updated for {selectedRole.displayName}!
        </div>
      )}

      {/* Role Picker */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3">
        {roles.map((r) => (
          <button
            key={r.role}
            onClick={() => setSelectedRoleCode(r.role)}
            className={`p-4 rounded-2xl border text-left transition-all ${
              selectedRoleCode === r.role
                ? 'border-indigo-600 bg-indigo-50/50 dark:bg-indigo-950/40 shadow-xs'
                : 'border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 hover:border-slate-300 dark:hover:border-slate-700'
            }`}
          >
            <div className="text-xs font-extrabold text-slate-900 dark:text-white">{r.displayName}</div>
            <div className="text-[10px] text-slate-500 font-mono mt-0.5">{r.role}</div>
            <div className="mt-2 text-[10px] text-indigo-600 dark:text-indigo-400 font-bold">
              {r.permissions.length} Active Perms
            </div>
          </button>
        ))}
      </div>

      {/* Matrix Controls */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* 1. System Operations Permissions */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-xs">
          <h3 className="font-extrabold text-sm text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2 flex items-center gap-2">
            <Key className="w-4 h-4 text-indigo-500" /> API & Operations Permissions
          </h3>

          <div className="space-y-2">
            {AVAILABLE_PERMISSIONS.map((perm) => {
              const isChecked = selectedRole.permissions.includes(perm);
              return (
                <div
                  key={perm}
                  onClick={() => handleTogglePermission(perm)}
                  className="p-3 rounded-2xl bg-slate-50 hover:bg-slate-100 dark:bg-slate-800/40 dark:hover:bg-slate-800/80 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between cursor-pointer transition-colors"
                >
                  <span className="font-mono text-xs font-bold text-slate-800 dark:text-slate-200">{perm}</span>
                  {isChecked ? (
                    <CheckSquare className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />
                  ) : (
                    <Square className="w-4 h-4 text-slate-400" />
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* 2. Menu Navigation Visibility */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-xs">
          <h3 className="font-extrabold text-sm text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2 flex items-center gap-2">
            <Layers className="w-4 h-4 text-purple-500" /> Menu Routes Visibility
          </h3>

          <div className="space-y-2 max-h-[500px] overflow-y-auto custom-scrollbar pr-1">
            {AVAILABLE_MENUS.map((menuPath) => {
              const isChecked = selectedRole.menuPermissions.includes(menuPath);
              return (
                <div
                  key={menuPath}
                  onClick={() => handleToggleMenu(menuPath)}
                  className="p-3 rounded-2xl bg-slate-50 hover:bg-slate-100 dark:bg-slate-800/40 dark:hover:bg-slate-800/80 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between cursor-pointer transition-colors"
                >
                  <span className="font-mono text-xs font-semibold text-slate-700 dark:text-slate-300">{menuPath}</span>
                  {isChecked ? (
                    <CheckSquare className="w-4 h-4 text-purple-600 dark:text-purple-400" />
                  ) : (
                    <Square className="w-4 h-4 text-slate-400" />
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* 3. Feature Flags */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-xs">
          <h3 className="font-extrabold text-sm text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2 flex items-center gap-2">
            <Sparkles className="w-4 h-4 text-cyan-500" /> Module Feature Flags
          </h3>

          <div className="space-y-3">
            {[
              { key: 'enableAuditLogs', title: 'Audit Trail Inspection', desc: 'Allows viewing immutable system activity logs' },
              { key: 'enableCloudinaryUploads', title: 'Cloudinary Asset Storage', desc: 'Allows uploading documents to Cloudinary vault' },
              { key: 'enableApprovalWorkflows', title: 'Approval Workflow Engine', desc: 'Allows acting as an approver in workflows' },
              { key: 'enableGithubIntegration', title: 'GitHub Code Integration', desc: 'Allows viewing repository pull requests & status' },
            ].map((f) => {
              const isEnabled = !!selectedRole.featureFlags[f.key];
              return (
                <div
                  key={f.key}
                  onClick={() => handleToggleFeature(f.key)}
                  className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between cursor-pointer"
                >
                  <div>
                    <div className="font-bold text-xs text-slate-900 dark:text-white">{f.title}</div>
                    <div className="text-[11px] text-slate-500">{f.desc}</div>
                  </div>
                  {isEnabled ? (
                    <span className="px-2.5 py-1 rounded-full bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 font-extrabold text-[10px]">
                      Enabled
                    </span>
                  ) : (
                    <span className="px-2.5 py-1 rounded-full bg-slate-200 dark:bg-slate-800 text-slate-500 font-extrabold text-[10px]">
                      Disabled
                    </span>
                  )}
                </div>
              );
            })}
          </div>
        </div>

      </div>
    </div>
  );
};
