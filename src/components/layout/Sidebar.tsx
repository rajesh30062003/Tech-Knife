import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, Shield, Users, Briefcase, GraduationCap, 
  Building2, FolderKanban, DollarSign, Clock, CalendarDays, 
  Target, UserPlus, Calendar, BarChart3, HelpCircle, 
  Bell, Settings, UserCheck, Globe, ChevronRight, FileText, Ticket,
  Award, GitFork, Layers, UploadCloud, ShieldCheck, Activity, Key, Package, Laptop
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { NavItem, Role } from '../../types';
import { EXECUTIVE_ROLES, MANAGEMENT_ROLES } from '../../utils/rbac';

interface SidebarProps {
  isOpen: boolean;
  onClose?: () => void;
}

const ALL_AUTHENTICATED: Role[] = [
  'ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MD', 'ROLE_CEO', 'ROLE_CTO', 'ROLE_CMO', 
  'ROLE_DIRECTOR', 'ROLE_MANAGER', 'ROLE_EMPLOYEE', 'ROLE_INTERN', 'ROLE_CUSTOMER'
];

const INTERNAL_STAFF: Role[] = [
  'ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MD', 'ROLE_CEO', 'ROLE_CTO', 'ROLE_CMO', 
  'ROLE_DIRECTOR', 'ROLE_MANAGER', 'ROLE_EMPLOYEE', 'ROLE_INTERN'
];

const NAV_ITEMS: NavItem[] = [
  // Core
  { title: 'Unified Dashboard', path: '/dashboard', icon: 'LayoutDashboard', roles: ALL_AUTHENTICATED, category: 'Core' },
  { title: 'Public Portal', path: '/', icon: 'Globe', roles: ALL_AUTHENTICATED, category: 'Core' },
  
  // Executive Governance & Strategic Portals
  { title: 'Executive Governance', path: '/admin', icon: 'Shield', roles: EXECUTIVE_ROLES, badge: 'Executive', category: 'Governance' },
  { title: 'Manager Desk', path: '/manager', icon: 'Users', roles: MANAGEMENT_ROLES, category: 'Governance' },
  { title: 'Employee Portal', path: '/employee', icon: 'Briefcase', roles: [...MANAGEMENT_ROLES, 'ROLE_EMPLOYEE'], category: 'Operations' },
  { title: 'Intern Desk', path: '/intern', icon: 'GraduationCap', roles: [...MANAGEMENT_ROLES, 'ROLE_INTERN'], category: 'Operations' },
  { title: 'Customer Portal', path: '/customer', icon: 'Building2', roles: [...EXECUTIVE_ROLES, 'ROLE_MANAGER', 'ROLE_CUSTOMER'], category: 'Client Space' },
  { title: 'Customer Accounts', path: '/customers', icon: 'Building2', roles: [...EXECUTIVE_ROLES, 'ROLE_MANAGER'], category: 'Client Space' },

  // Enterprise Core Platform Services
  { title: 'CMS Content Manager', path: '/cms', icon: 'Globe', roles: EXECUTIVE_ROLES, badge: 'CMS', category: 'Enterprise Core' },
  { title: 'Assets & Inventory', path: '/assets', icon: 'Package', roles: INTERNAL_STAFF, category: 'Enterprise Core' },
  { title: 'Approval Workflows', path: '/workflows', icon: 'ShieldCheck', roles: INTERNAL_STAFF, badge: 'Engine', category: 'Enterprise Core' },
  { title: 'Storage & Vault', path: '/storage', icon: 'UploadCloud', roles: INTERNAL_STAFF, category: 'Enterprise Core' },
  { title: 'Activity & Audit Logs', path: '/audit-logs', icon: 'Activity', roles: EXECUTIVE_ROLES, category: 'Enterprise Core' },
  { title: 'Roles & Permissions', path: '/roles-permissions', icon: 'Key', roles: EXECUTIVE_ROLES, category: 'Enterprise Core' },

  // Organization & Hierarchy
  { title: 'Departments', path: '/departments', icon: 'Building2', roles: INTERNAL_STAFF, category: 'Human Capital' },
  { title: 'Designation Matrix', path: '/designations', icon: 'Award', roles: INTERNAL_STAFF, category: 'Human Capital' },
  { title: 'Organization Chart', path: '/org-structure', icon: 'GitFork', roles: INTERNAL_STAFF, badge: 'Chart', category: 'Human Capital' },

  // Project Execution & Operations
  { title: 'Projects & Deliverables', path: '/projects', icon: 'FolderKanban', roles: ALL_AUTHENTICATED, badge: 'Active', category: 'Execution' },
  { title: 'Corporate Calendar', path: '/calendar', icon: 'Calendar', roles: INTERNAL_STAFF, category: 'Execution' },

  // Human Capital Management
  { title: 'Employees Directory', path: '/employees', icon: 'Users', roles: INTERNAL_STAFF, category: 'Human Capital' },
  { title: 'Interns Cohort', path: '/interns', icon: 'GraduationCap', roles: INTERNAL_STAFF, category: 'Human Capital' },
  { title: 'Attendance & Time Logs', path: '/attendance', icon: 'Clock', roles: INTERNAL_STAFF, category: 'Human Capital' },
  { title: 'Leave Management', path: '/leave', icon: 'CalendarDays', roles: INTERNAL_STAFF, category: 'Human Capital' },
  { title: 'Payroll & Compensation', path: '/payroll', icon: 'DollarSign', roles: [...MANAGEMENT_ROLES, 'ROLE_EMPLOYEE'], category: 'Human Capital' },

  // Enterprise Growth
  { title: 'CRM & Deals Pipeline', path: '/crm', icon: 'Target', roles: MANAGEMENT_ROLES, category: 'Growth' },
  { title: 'Talent Recruitment', path: '/recruitment', icon: 'UserPlus', roles: MANAGEMENT_ROLES, category: 'Growth' },
  { title: 'Executive Analytics', path: '/reports', icon: 'BarChart3', roles: MANAGEMENT_ROLES, category: 'Growth' },

  // Client Services & Support
  { title: 'Support & Service Tickets', path: '/support', icon: 'Ticket', roles: ALL_AUTHENTICATED, category: 'Support' },
  { title: 'Notifications Center', path: '/notifications', icon: 'Bell', roles: ALL_AUTHENTICATED, badge: '3', category: 'Support' },
  { title: 'System Configuration', path: '/settings', icon: 'Settings', roles: MANAGEMENT_ROLES, category: 'Support' },
  { title: 'Account Profile', path: '/profile', icon: 'UserCheck', roles: ALL_AUTHENTICATED, category: 'Support' },
];

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const { user } = useAuth();

  const getIcon = (iconName: string) => {
    switch (iconName) {
      case 'LayoutDashboard': return <LayoutDashboard className="w-4 h-4 shrink-0" />;
      case 'Shield': return <Shield className="w-4 h-4 shrink-0" />;
      case 'Users': return <Users className="w-4 h-4 shrink-0" />;
      case 'Briefcase': return <Briefcase className="w-4 h-4 shrink-0" />;
      case 'GraduationCap': return <GraduationCap className="w-4 h-4 shrink-0" />;
      case 'Building2': return <Building2 className="w-4 h-4 shrink-0" />;
      case 'FolderKanban': return <FolderKanban className="w-4 h-4 shrink-0" />;
      case 'DollarSign': return <DollarSign className="w-4 h-4 shrink-0" />;
      case 'Clock': return <Clock className="w-4 h-4 shrink-0" />;
      case 'CalendarDays': return <CalendarDays className="w-4 h-4 shrink-0" />;
      case 'Target': return <Target className="w-4 h-4 shrink-0" />;
      case 'UserPlus': return <UserPlus className="w-4 h-4 shrink-0" />;
      case 'Calendar': return <Calendar className="w-4 h-4 shrink-0" />;
      case 'BarChart3': return <BarChart3 className="w-4 h-4 shrink-0" />;
      case 'HelpCircle': return <HelpCircle className="w-4 h-4 shrink-0" />;
      case 'Ticket': return <Ticket className="w-4 h-4 shrink-0" />;
      case 'Bell': return <Bell className="w-4 h-4 shrink-0" />;
      case 'Settings': return <Settings className="w-4 h-4 shrink-0" />;
      case 'UserCheck': return <UserCheck className="w-4 h-4 shrink-0" />;
      case 'Globe': return <Globe className="w-4 h-4 shrink-0" />;
      case 'Award': return <Award className="w-4 h-4 shrink-0" />;
      case 'GitFork': return <GitFork className="w-4 h-4 shrink-0" />;
      case 'Layers': return <Layers className="w-4 h-4 shrink-0" />;
      case 'UploadCloud': return <UploadCloud className="w-4 h-4 shrink-0" />;
      case 'ShieldCheck': return <ShieldCheck className="w-4 h-4 shrink-0" />;
      case 'Activity': return <Activity className="w-4 h-4 shrink-0" />;
      case 'Key': return <Key className="w-4 h-4 shrink-0" />;
      case 'Package': return <Package className="w-4 h-4 shrink-0" />;
      case 'Laptop': return <Laptop className="w-4 h-4 shrink-0" />;
      default: return <LayoutDashboard className="w-4 h-4 shrink-0" />;
    }
  };

  const filteredItems = NAV_ITEMS.filter(item => {
    if (!user) return true;
    const userRoles = user.roles && user.roles.length > 0 ? user.roles : [user.role];
    return userRoles.some(r => item.roles.includes(r));
  });

  const categories = Array.from(new Set(filteredItems.map(item => item.category || 'General')));

  const formatRoleDisplay = (role: Role) => {
    switch (role) {
      case 'ROLE_SUPER_ADMIN': return 'SUPER ADMIN';
      case 'ROLE_MD': return 'MANAGING DIRECTOR';
      case 'ROLE_CEO': return 'CHIEF EXECUTIVE (CEO)';
      case 'ROLE_CTO': return 'CHIEF TECH (CTO)';
      case 'ROLE_CMO': return 'CHIEF MARKETING (CMO)';
      case 'ROLE_DIRECTOR': return 'DIRECTOR';
      case 'ROLE_MANAGER': return 'MANAGER';
      case 'ROLE_EMPLOYEE': return 'EMPLOYEE';
      case 'ROLE_INTERN': return 'INTERN';
      case 'ROLE_CUSTOMER': return 'CUSTOMER';
      default: return role.replace('ROLE_', '');
    }
  };

  return (
    <aside
      className={`
        /* Base Enterprise Fixed Layout */
        bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800 
        flex flex-col overflow-hidden shrink-0 transition-all duration-300 ease-in-out select-none
        
        /* Mobile Overlay Drawer (<1024px) */
        fixed top-16 bottom-0 left-0 z-40 lg:z-auto lg:relative lg:top-0 lg:bottom-auto lg:h-full
        ${isOpen 
          ? 'w-64 translate-x-0 shadow-2xl lg:shadow-none' 
          : 'w-64 -translate-x-full lg:w-20 lg:translate-x-0'
        }
      `}
    >
      {/* Independent Scrolling Navigation Menu Area */}
      <div className="flex-1 overflow-y-auto py-4 px-3 space-y-5 custom-scrollbar">
        {categories.map(category => {
          const itemsInCategory = filteredItems.filter(item => (item.category || 'General') === category);
          if (itemsInCategory.length === 0) return null;

          return (
            <div key={category} className="space-y-1">
              {/* Category Header Label (Hidden in icon collapsed mode) */}
              <div className={`px-3 text-[10px] font-bold uppercase tracking-widest text-slate-400 dark:text-slate-500 mb-1.5 transition-opacity ${
                !isOpen ? 'lg:hidden' : 'block'
              }`}>
                {category}
              </div>

              {/* Divider in Collapsed Icon Mode */}
              {!isOpen && (
                <div className="hidden lg:block my-2 border-t border-slate-100 dark:border-slate-800" />
              )}

              {itemsInCategory.map(item => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  title={item.title}
                  onClick={() => {
                    if (window.innerWidth < 1024 && onClose) {
                      onClose();
                    }
                  }}
                  className={({ isActive }) =>
                    `flex items-center justify-between px-3 py-2.5 text-xs font-bold rounded-xl transition-all group ${
                      isActive
                        ? 'bg-slate-900 dark:bg-slate-800 text-cyan-400 border-l-4 border-cyan-500 shadow-md shadow-slate-900/10'
                        : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800/80 hover:text-slate-900 dark:hover:text-slate-100'
                    } ${!isOpen ? 'lg:justify-center lg:px-2' : ''}`
                  }
                >
                  <div className="flex items-center gap-2.5 truncate">
                    {getIcon(item.icon)}
                    <span className={`truncate ${!isOpen ? 'lg:hidden' : 'inline'}`}>
                      {item.title}
                    </span>
                  </div>

                  {item.badge && (
                    <span className={`ml-1 px-1.5 py-0.5 text-[9px] font-bold rounded-full bg-cyan-500/10 text-cyan-600 dark:bg-cyan-400/20 dark:text-cyan-300 shrink-0 ${
                      !isOpen ? 'lg:hidden' : 'inline'
                    }`}>
                      {item.badge}
                    </span>
                  )}
                </NavLink>
              ))}
            </div>
          );
        })}
      </div>

      {/* Active Role Footer (Independent Fixed Footer at bottom of Sidebar) */}
      <div className="p-3 border-t border-slate-200 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-900/60 shrink-0">
        <div className={`flex items-center justify-between px-2.5 py-2 rounded-xl bg-slate-100 dark:bg-slate-800/80 border border-slate-200/50 dark:border-slate-700/50 ${
          !isOpen ? 'lg:justify-center' : ''
        }`}>
          <div className="flex items-center gap-2 overflow-hidden">
            <div className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse shrink-0"></div>
            <div className={`flex flex-col min-w-0 ${!isOpen ? 'lg:hidden' : 'flex'}`}>
              <span className="text-[9px] uppercase font-extrabold text-slate-400 tracking-wider">Active Role</span>
              <span className="text-[11px] font-extrabold text-slate-800 dark:text-slate-200 truncate">
                {user ? formatRoleDisplay(user.role) : 'GUEST'}
              </span>
            </div>
          </div>
          <ChevronRight className={`w-3.5 h-3.5 text-slate-400 shrink-0 ${!isOpen ? 'lg:hidden' : 'block'}`} />
        </div>
      </div>
    </aside>
  );
};
