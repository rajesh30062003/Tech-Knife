import React, { useState } from 'react';
import { 
  Shield, Key, Users, FileText, Lock, RefreshCw, CheckCircle, Search, Filter, 
  UserPlus, UserX, UserMinus, Building2, Layers, Check, AlertTriangle, Plus, Trash2, Ban
} from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useAuth } from '../../context/AuthContext';
import { Role, UserProfile } from '../../types';
import { 
  canCreateEmployee, canCreateIntern, canDeleteEmployee, canSuspendEmployee, 
  canManageDepartments, canChangeSalary 
} from '../../utils/rbac';

export const AdminDashboard: React.FC = () => {
  const { user } = useAuth();
  const [filterRole, setFilterRole] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showDeptModal, setShowDeptModal] = useState(false);

  // Form State
  const [newUser, setNewUser] = useState({
    firstName: '',
    lastName: '',
    email: '',
    designation: '',
    department: 'Engineering',
    role: 'ROLE_EMPLOYEE' as Role,
  });

  const [departments, setDepartments] = useState([
    { name: 'Executive Leadership', lead: 'Ranadhir Pal (CEO)', members: 2, budget: '₹ 67,00,000' },
    { name: 'Technology Management', lead: 'Sourav Roy (MD)', members: 2, budget: '₹ 50,00,000' },
    { name: 'Engineering & DevOps', lead: 'Ganesh Pal (Sr. Developer)', members: 4, budget: '₹ 35,00,000' },
    { name: 'Systems Infrastructure', lead: 'Rahul Garai (System Developer)', members: 4, budget: '₹ 30,00,000' },
  ]);

  const [newDeptName, setNewDeptName] = useState('');
  const [newDeptLead, setNewDeptLead] = useState('');
  const [newDeptBudget, setNewDeptBudget] = useState('');

  const [usersList, setUsersList] = useState<Partial<UserProfile>[]>([
    { id: 'usr-101', firstName: 'Alexander', lastName: 'Vance', email: 'a.vance@techknife.com', role: 'ROLE_MD', department: 'Executive Leadership', designation: 'Managing Director', enabled: true, accountNonLocked: true },
    { id: 'usr-102', firstName: 'Sarah', lastName: 'Connor', email: 's.connor@techknife.com', role: 'ROLE_CTO', department: 'Engineering & DevOps', designation: 'Chief Technology Officer', enabled: true, accountNonLocked: true },
    { id: 'usr-103', firstName: 'Marcus', lastName: 'Brody', email: 'm.brody@techknife.com', role: 'ROLE_MANAGER', department: 'Product Management', designation: 'Senior Engineering Manager', enabled: true, accountNonLocked: true },
    { id: 'usr-104', firstName: 'Elena', lastName: 'Rostova', email: 'e.rostova@techknife.com', role: 'ROLE_EMPLOYEE', department: 'Engineering & DevOps', designation: 'Senior Frontend Engineer', enabled: true, accountNonLocked: true },
    { id: 'usr-105', firstName: 'Lucas', lastName: 'Chen', email: 'l.chen@techknife.com', role: 'ROLE_INTERN', department: 'Engineering & DevOps', designation: 'DevOps Intern', enabled: true, accountNonLocked: true },
    { id: 'usr-106', firstName: 'Apex', lastName: 'Corp', email: 'client@apex.com', role: 'ROLE_CUSTOMER', department: 'Client Space', designation: 'Enterprise Client Representative', enabled: true, accountNonLocked: true },
  ]);

  const [auditLogs] = useState([
    { id: 'log-101', principal: 'a.vance@techknife.com', action: 'UPDATE_SECURITY_POLICY', module: 'SecurityConfig', ip: '192.168.1.45', time: '10 mins ago', status: 'SUCCESS' },
    { id: 'log-102', principal: 's.connor@techknife.com', action: 'ASSIGN_ROLE_SUPER_ADMIN', module: 'UserController', ip: '10.0.4.12', time: '25 mins ago', status: 'SUCCESS' },
    { id: 'log-103', principal: 'm.brody@techknife.com', action: 'APPROVE_LEAVE_REQUEST', module: 'LeaveService', ip: '192.168.2.11', time: '1 hour ago', status: 'SUCCESS' },
    { id: 'log-104', principal: 'unknown_ip', action: 'FAILED_LOGIN_ATTEMPT', module: 'AuthController', ip: '45.142.120.9', time: '2 hours ago', status: 'FAILED: Invalid Credentials' },
  ]);

  // Actions
  const handleCreateUser = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newUser.firstName || !newUser.email) return;

    const created: Partial<UserProfile> = {
      id: `usr-${Date.now().toString().slice(-4)}`,
      firstName: newUser.firstName,
      lastName: newUser.lastName,
      email: newUser.email,
      role: newUser.role,
      department: newUser.department,
      designation: newUser.designation || (newUser.role === 'ROLE_INTERN' ? 'Intern' : 'Employee'),
      enabled: true,
      accountNonLocked: true,
    };

    setUsersList([created, ...usersList]);
    setShowCreateModal(false);
    setNewUser({ firstName: '', lastName: '', email: '', designation: '', department: 'Engineering', role: 'ROLE_EMPLOYEE' });
  };

  const handleToggleSuspend = (id: string) => {
    setUsersList(usersList.map(u => {
      if (u.id === id) {
        return { ...u, accountNonLocked: !u.accountNonLocked };
      }
      return u;
    }));
  };

  const handleDeleteUser = (id: string) => {
    setUsersList(usersList.filter(u => u.id !== id));
  };

  const handleAddDepartment = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newDeptName) return;
    setDepartments([...departments, {
      name: newDeptName,
      lead: newDeptLead || 'Unassigned',
      members: 1,
      budget: newDeptBudget || '$100,000'
    }]);
    setNewDeptName('');
    setNewDeptLead('');
    setNewDeptBudget('');
    setShowDeptModal(false);
  };

  const filteredUsers = usersList.filter(u => {
    const matchesSearch = `${u.firstName} ${u.lastName} ${u.email} ${u.designation}`.toLowerCase().includes(searchQuery.toLowerCase());
    if (filterRole === 'ALL') return matchesSearch;
    return matchesSearch && u.role === filterRole;
  });

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-rose-600 dark:text-rose-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Shield className="w-4 h-4" />
            <span>Super Admin & Enterprise Governance Desk</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Admin Governance & RBAC Center</h1>
          <p className="text-xs text-slate-500">Manage organizational structure, employee identities, departments, and audit logs</p>
        </div>

        <div className="flex items-center gap-2">
          {canManageDepartments(user) && (
            <button
              onClick={() => setShowDeptModal(true)}
              className="inline-flex items-center gap-2 px-3.5 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-700 dark:text-slate-200 font-semibold text-xs rounded-xl transition-all"
            >
              <Building2 className="w-3.5 h-3.5" /> Add Department
            </button>
          )}

          {(canCreateEmployee(user) || canCreateIntern(user)) && (
            <button
              onClick={() => setShowCreateModal(true)}
              className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md"
            >
              <UserPlus className="w-3.5 h-3.5" /> Create User / Intern
            </button>
          )}
        </div>
      </div>

      {/* Admin Quick Metrics */}
      <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <div className="flex items-center justify-between text-slate-500 mb-2">
            <span className="text-xs font-semibold uppercase">Total Registered Identities</span>
            <Users className="w-4 h-4 text-indigo-500" />
          </div>
          <div className="text-2xl font-bold text-slate-900 dark:text-white">{usersList.length} Accounts</div>
          <p className="text-[11px] text-emerald-600 dark:text-emerald-400 mt-1">11 Roles Configured</p>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <div className="flex items-center justify-between text-slate-500 mb-2">
            <span className="text-xs font-semibold uppercase">Departments</span>
            <Building2 className="w-4 h-4 text-emerald-500" />
          </div>
          <div className="text-2xl font-bold text-slate-900 dark:text-white">{departments.length} Units</div>
          <p className="text-[11px] text-slate-500 mt-1">Managed by Exec & Leads</p>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <div className="flex items-center justify-between text-slate-500 mb-2">
            <span className="text-xs font-semibold uppercase">Database Node</span>
            <CheckCircle className="w-4 h-4 text-purple-500" />
          </div>
          <div className="text-2xl font-bold text-slate-900 dark:text-white">Active Atlas</div>
          <p className="text-[11px] text-slate-500 mt-1">MongoDB Shard Cluster UP</p>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl">
          <div className="flex items-center justify-between text-slate-500 mb-2">
            <span className="text-xs font-semibold uppercase">RBAC Security Status</span>
            <Lock className="w-4 h-4 text-amber-500" />
          </div>
          <div className="text-2xl font-bold text-slate-900 dark:text-white">Strict Guard</div>
          <p className="text-[11px] text-slate-500 mt-1">JWT Bearer Enforced</p>
        </div>
      </div>

      {/* User Management Section */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <h3 className="font-bold text-base text-slate-900 dark:text-white">User Accounts & Role Permissions</h3>
            <p className="text-xs text-slate-500">View, suspend, or manage user capabilities and account locking</p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <div className="relative">
              <Search className="w-3.5 h-3.5 absolute left-3 top-2.5 text-slate-400" />
              <input
                type="text"
                placeholder="Search user email or name..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-9 pr-3 py-1.5 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white w-48 focus:w-60 transition-all"
              />
            </div>

            <select
              value={filterRole}
              onChange={(e) => setFilterRole(e.target.value)}
              className="text-xs px-3 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-medium"
            >
              <option value="ALL">All Roles</option>
              <option value="ROLE_SUPER_ADMIN">Super Admin</option>
              <option value="ROLE_MD">MD</option>
              <option value="ROLE_CEO">CEO</option>
              <option value="ROLE_CTO">CTO</option>
              <option value="ROLE_CMO">CMO</option>
              <option value="ROLE_DIRECTOR">Director</option>
              <option value="ROLE_MANAGER">Manager</option>
              <option value="ROLE_EMPLOYEE">Employee</option>
              <option value="ROLE_INTERN">Intern</option>
              <option value="ROLE_CUSTOMER">Customer</option>
            </select>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600 dark:text-slate-300">
            <thead className="bg-slate-50 dark:bg-slate-800/60 uppercase font-semibold text-slate-500">
              <tr>
                <th className="py-3 px-4">User</th>
                <th className="py-3 px-4">Assigned Role</th>
                <th className="py-3 px-4">Department & Designation</th>
                <th className="py-3 px-4">Account Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {filteredUsers.map((u) => (
                <tr key={u.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/30 transition-colors">
                  <td className="py-3.5 px-4">
                    <div className="font-bold text-slate-900 dark:text-slate-100">{u.firstName} {u.lastName}</div>
                    <div className="text-[11px] text-slate-400 font-mono">{u.email}</div>
                  </td>
                  <td className="py-3.5 px-4">
                    <span className="px-2 py-0.5 font-bold text-[10px] rounded-md bg-indigo-50 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-300 border border-indigo-200/50 dark:border-indigo-800/50">
                      {u.role?.replace('ROLE_', '')}
                    </span>
                  </td>
                  <td className="py-3.5 px-4">
                    <div className="font-semibold text-slate-800 dark:text-slate-200">{u.designation}</div>
                    <div className="text-[11px] text-slate-400">{u.department}</div>
                  </td>
                  <td className="py-3.5 px-4">
                    <StatusBadge status={u.accountNonLocked ? 'Active' : 'Critical'} />
                  </td>
                  <td className="py-3.5 px-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      {canSuspendEmployee(user) && (
                        <button
                          onClick={() => handleToggleSuspend(u.id!)}
                          title={u.accountNonLocked ? 'Suspend User' : 'Unsuspend User'}
                          className={`px-2.5 py-1 text-[11px] font-semibold rounded-lg transition-colors ${
                            u.accountNonLocked 
                              ? 'bg-amber-50 hover:bg-amber-100 text-amber-700 dark:bg-amber-950/60 dark:text-amber-300'
                              : 'bg-emerald-50 hover:bg-emerald-100 text-emerald-700 dark:bg-emerald-950/60 dark:text-emerald-300'
                          }`}
                        >
                          {u.accountNonLocked ? 'Suspend' : 'Unsuspend'}
                        </button>
                      )}

                      {canDeleteEmployee(user) && (
                        <button
                          onClick={() => handleDeleteUser(u.id!)}
                          title="Delete User"
                          className="p-1.5 bg-rose-50 hover:bg-rose-100 text-rose-600 dark:bg-rose-950/60 dark:text-rose-400 rounded-lg transition-colors"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Departments Section */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <div className="border-b border-slate-100 dark:border-slate-800 pb-3 flex items-center justify-between">
          <div>
            <h3 className="font-bold text-base text-slate-900 dark:text-white">Departmental Structure & Budgets</h3>
            <p className="text-xs text-slate-500">Corporate units managed by Executive Directors & Managers</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {departments.map((d, idx) => (
            <div key={idx} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 space-y-2">
              <div className="flex items-center justify-between">
                <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{d.name}</span>
                <span className="text-[10px] font-bold px-2 py-0.5 bg-indigo-100 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400 rounded-full">
                  {d.members} Staff
                </span>
              </div>
              <p className="text-[11px] text-slate-500">Lead: <span className="font-semibold text-slate-700 dark:text-slate-300">{d.lead}</span></p>
              <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-semibold">Allocated Budget: {d.budget}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Audit Log Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4">
        <div className="border-b border-slate-100 dark:border-slate-800 pb-3">
          <h3 className="font-bold text-base text-slate-900 dark:text-white">AOP System Audit Logs & Security Trace</h3>
          <p className="text-xs text-slate-500">Real-time rest endpoints intercepted by Spring Aspect</p>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600 dark:text-slate-300">
            <thead className="bg-slate-50 dark:bg-slate-800/60 uppercase font-semibold text-slate-500">
              <tr>
                <th className="py-3 px-4">Principal Email</th>
                <th className="py-3 px-4">Interception Method</th>
                <th className="py-3 px-4">Module Class</th>
                <th className="py-3 px-4">Origin IP</th>
                <th className="py-3 px-4">Timestamp</th>
                <th className="py-3 px-4">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {auditLogs.map((log) => (
                <tr key={log.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/30">
                  <td className="py-3.5 px-4 font-mono font-semibold text-slate-900 dark:text-slate-100">{log.principal}</td>
                  <td className="py-3.5 px-4 font-mono text-indigo-600 dark:text-indigo-400">{log.action}</td>
                  <td className="py-3.5 px-4 text-slate-500">{log.module}</td>
                  <td className="py-3.5 px-4 font-mono text-[11px] text-slate-400">{log.ip}</td>
                  <td className="py-3.5 px-4 text-slate-400">{log.time}</td>
                  <td className="py-3.5 px-4">
                    <StatusBadge status={log.status.includes('SUCCESS') ? 'Completed' : 'Critical'} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal: Create Employee / Intern */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-bold text-base text-slate-900 dark:text-white">Create Employee / Intern Identity</h3>
              <button onClick={() => setShowCreateModal(false)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>

            <form onSubmit={handleCreateUser} className="space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">First Name</label>
                  <input
                    type="text"
                    required
                    value={newUser.firstName}
                    onChange={(e) => setNewUser({ ...newUser, firstName: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Last Name</label>
                  <input
                    type="text"
                    required
                    value={newUser.lastName}
                    onChange={(e) => setNewUser({ ...newUser, lastName: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                  />
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Corporate Email</label>
                <input
                  type="email"
                  required
                  value={newUser.email}
                  onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                  placeholder="name@techknife.com"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Role Assignment</label>
                <select
                  value={newUser.role}
                  onChange={(e) => setNewUser({ ...newUser, role: e.target.value as Role })}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                >
                  <option value="ROLE_EMPLOYEE">Employee</option>
                  <option value="ROLE_INTERN">Intern</option>
                  <option value="ROLE_MANAGER">Manager</option>
                  <option value="ROLE_DIRECTOR">Director</option>
                  <option value="ROLE_CTO">CTO</option>
                  <option value="ROLE_CMO">CMO</option>
                  <option value="ROLE_CEO">CEO</option>
                  <option value="ROLE_MD">Managing Director</option>
                  <option value="ROLE_CUSTOMER">Customer</option>
                </select>
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Department</label>
                <select
                  value={newUser.department}
                  onChange={(e) => setNewUser({ ...newUser, department: e.target.value })}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                >
                  {departments.map((d) => (
                    <option key={d.name} value={d.name}>{d.name}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Designation</label>
                <input
                  type="text"
                  value={newUser.designation}
                  onChange={(e) => setNewUser({ ...newUser, designation: e.target.value })}
                  placeholder="e.g. Fullstack Engineer"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div className="pt-3 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-3.5 py-1.5 text-xs text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl shadow-md"
                >
                  Create User
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal: Add Department */}
      {showDeptModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-bold text-base text-slate-900 dark:text-white">Add New Organizational Unit</h3>
              <button onClick={() => setShowDeptModal(false)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>

            <form onSubmit={handleAddDepartment} className="space-y-3">
              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Department Name</label>
                <input
                  type="text"
                  required
                  value={newDeptName}
                  onChange={(e) => setNewDeptName(e.target.value)}
                  placeholder="e.g. Quality Assurance & Testing"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Department Lead</label>
                <input
                  type="text"
                  value={newDeptLead}
                  onChange={(e) => setNewDeptLead(e.target.value)}
                  placeholder="e.g. Ganesh Pal (Sr. Developer)"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Allocated Annual Budget</label>
                <input
                  type="text"
                  value={newDeptBudget}
                  onChange={(e) => setNewDeptBudget(e.target.value)}
                  placeholder="e.g. $350,000"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div className="pt-3 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowDeptModal(false)}
                  className="px-3.5 py-1.5 text-xs text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl shadow-md"
                >
                  Save Department
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
