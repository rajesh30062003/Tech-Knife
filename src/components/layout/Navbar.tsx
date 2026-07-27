import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  Sun, Moon, Laptop, Bell, Search, Shield, User, LogOut, 
  Menu, X, ChevronDown, Check
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useTheme, ThemeMode } from '../../context/ThemeContext';
import { Role } from '../../types';
import { UniversalSearchModal } from '../core/UniversalSearchModal';
import { Logo } from '../common/Logo';

interface NavbarProps {
  toggleSidebar: () => void;
  isSidebarOpen: boolean;
}

const ROLES_LIST: { role: Role; label: string; color: string }[] = [
  { role: 'ROLE_SUPER_ADMIN', label: 'Super Admin', color: 'bg-rose-500' },
  { role: 'ROLE_ADMIN', label: 'Admin', color: 'bg-purple-500' },
  { role: 'ROLE_MANAGER', label: 'Manager', color: 'bg-amber-500' },
  { role: 'ROLE_EMPLOYEE', label: 'Employee', color: 'bg-emerald-500' },
  { role: 'ROLE_INTERN', label: 'Intern', color: 'bg-cyan-500' },
  { role: 'ROLE_CUSTOMER', label: 'Customer', color: 'bg-indigo-500' }
];

export const Navbar: React.FC<NavbarProps> = ({ toggleSidebar, isSidebarOpen }) => {
  const { user, switchRole, logout } = useAuth();
  const { theme, themeMode, setThemeMode } = useTheme();
  const navigate = useNavigate();

  const [showRoleMenu, setShowRoleMenu] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showThemeMenu, setShowThemeMenu] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault();
        setIsSearchOpen(true);
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  return (
    <>
      <header className="sticky top-0 z-30 h-16 bg-white/90 dark:bg-slate-900/90 backdrop-blur-md border-b border-slate-200 dark:border-slate-800 transition-colors">
        <div className="h-full px-4 flex items-center justify-between gap-4">
          
          {/* Left Section: Toggle & Brand Logo */}
          <div className="flex items-center gap-3">
            <button
              onClick={toggleSidebar}
              className="p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors"
              title={isSidebarOpen ? "Collapse sidebar" : "Expand sidebar"}
            >
              {isSidebarOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>

            <Link to="/dashboard" className="flex items-center">
              <Logo variant="full" size="md" />
            </Link>
          </div>

          {/* Center Section: Search Bar Trigger */}
          <div
            onClick={() => setIsSearchOpen(true)}
            className="hidden md:flex flex-1 max-w-md items-center justify-between px-3.5 py-1.5 bg-slate-100 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/80 rounded-xl cursor-pointer hover:border-blue-500 transition-all text-xs font-medium text-slate-500 dark:text-slate-400"
          >
            <div className="flex items-center gap-2">
              <Search className="w-4 h-4 text-slate-400" />
              <span>Universal Search (employees, projects, tickets, leads)...</span>
            </div>
            <kbd className="px-1.5 py-0.5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded text-[10px] font-bold text-slate-500">
              ⌘K
            </kbd>
          </div>

          {/* Right Section: Role Switcher, Theme, Notifications & Profile */}
          <div className="flex items-center gap-2">
            
            {/* Interactive Role Switcher Dropdown */}
            <div className="relative">
              <button
                onClick={() => {
                  setShowRoleMenu(!showRoleMenu);
                  setShowThemeMenu(false);
                  setShowUserMenu(false);
                }}
                className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold rounded-lg bg-blue-50 dark:bg-blue-950/60 text-blue-700 dark:text-blue-300 border border-blue-200/60 dark:border-blue-800/60 hover:bg-blue-100 dark:hover:bg-blue-900 transition-all"
                title="Simulator: Switch User Role"
              >
                <Shield className="w-3.5 h-3.5" />
                <span className="hidden lg:inline">Role:</span>
                <span className="font-bold">{user?.role?.replace('ROLE_', '') || 'SUPER_ADMIN'}</span>
                <ChevronDown className="w-3.5 h-3.5" />
              </button>

              {showRoleMenu && (
                <div className="absolute right-0 mt-2 w-56 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl shadow-xl py-2 z-50 animate-in fade-in slide-in-from-top-2 duration-150">
                  <div className="px-3 py-1.5 text-[10px] font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500 border-b border-slate-100 dark:border-slate-800 mb-1">
                    Simulate Role Access
                  </div>
                  {ROLES_LIST.map((item) => (
                    <button
                      key={item.role}
                      onClick={() => {
                        switchRole(item.role);
                        setShowRoleMenu(false);
                      }}
                      className={`w-full flex items-center justify-between px-3 py-2 text-xs text-left hover:bg-slate-50 dark:hover:bg-slate-800 ${
                        user?.role === item.role ? 'font-bold text-blue-600 dark:text-blue-400 bg-blue-50/50 dark:bg-blue-950/30' : 'text-slate-700 dark:text-slate-300'
                      }`}
                    >
                      <div className="flex items-center gap-2">
                        <span className={`w-2 h-2 rounded-full ${item.color}`}></span>
                        {item.label}
                      </div>
                      {user?.role === item.role && <Check className="w-3.5 h-3.5 text-blue-600" />}
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Theme Selector (Light / Dark / System) */}
            <div className="relative">
              <button
                onClick={() => {
                  setShowThemeMenu(!showThemeMenu);
                  setShowRoleMenu(false);
                  setShowUserMenu(false);
                }}
                className="p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors"
                title="Theme Settings"
              >
                {themeMode === 'light' && <Sun className="w-4 h-4 text-amber-500" />}
                {themeMode === 'dark' && <Moon className="w-4 h-4 text-blue-400" />}
                {themeMode === 'system' && <Laptop className="w-4 h-4 text-slate-400" />}
              </button>

              {showThemeMenu && (
                <div className="absolute right-0 mt-2 w-44 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl shadow-xl py-1.5 z-50">
                  <div className="px-3 py-1 text-[10px] font-bold uppercase tracking-wider text-slate-400 border-b border-slate-100 dark:border-slate-800 mb-1">
                    Theme Mode
                  </div>
                  {[
                    { mode: 'light' as ThemeMode, label: 'Light', icon: Sun },
                    { mode: 'dark' as ThemeMode, label: 'Dark', icon: Moon },
                    { mode: 'system' as ThemeMode, label: 'System', icon: Laptop },
                  ].map((item) => {
                    const IconComp = item.icon;
                    return (
                      <button
                        key={item.mode}
                        onClick={() => {
                          setThemeMode(item.mode);
                          setShowThemeMenu(false);
                        }}
                        className={`w-full flex items-center justify-between px-3 py-2 text-xs text-left hover:bg-slate-50 dark:hover:bg-slate-800 ${
                          themeMode === item.mode ? 'font-bold text-blue-600 dark:text-blue-400 bg-blue-50/50 dark:bg-blue-950/30' : 'text-slate-700 dark:text-slate-300'
                        }`}
                      >
                        <div className="flex items-center gap-2">
                          <IconComp className="w-4 h-4" />
                          {item.label}
                        </div>
                        {themeMode === item.mode && <Check className="w-3.5 h-3.5" />}
                      </button>
                    );
                  })}
                </div>
              )}
            </div>

            {/* Notifications Link */}
            <Link
              to="/notifications"
              className="relative p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors"
              title="Notifications"
            >
              <Bell className="w-4 h-4" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-blue-600 rounded-full ring-2 ring-white dark:ring-slate-900"></span>
            </Link>

            {/* User Profile Menu */}
            {user ? (
              <div className="relative">
                <button
                  onClick={() => {
                    setShowUserMenu(!showUserMenu);
                    setShowRoleMenu(false);
                    setShowThemeMenu(false);
                  }}
                  className="flex items-center gap-2 p-1 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                >
                  {user.avatarUrl ? (
                    <img
                      src={user.avatarUrl}
                      alt={user.firstName}
                      className="w-8 h-8 rounded-full object-cover border border-slate-200 dark:border-slate-700"
                    />
                  ) : (
                    <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center text-xs font-bold shadow-xs">
                      {user.firstName?.[0] || 'T'}{user.lastName?.[0] || 'K'}
                    </div>
                  )}
                </button>

                {showUserMenu && (
                  <div className="absolute right-0 mt-2 w-60 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl shadow-xl py-2 z-50">
                    <div className="px-4 py-2 border-b border-slate-100 dark:border-slate-800">
                      <p className="text-sm font-semibold text-slate-900 dark:text-white">
                        {user.firstName} {user.lastName}
                      </p>
                      <p className="text-xs text-slate-500 dark:text-slate-400 truncate">{user.email}</p>
                      <div className="mt-1">
                        <span className="inline-block px-2 py-0.5 text-[10px] font-bold rounded bg-blue-100 dark:bg-blue-950 text-blue-700 dark:text-blue-300">
                          {user.designation || 'Enterprise User'}
                        </span>
                      </div>
                    </div>

                    <Link
                      to="/profile"
                      onClick={() => setShowUserMenu(false)}
                      className="flex items-center gap-2 px-4 py-2 text-xs text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800"
                    >
                      <User className="w-4 h-4" /> Account Profile
                    </Link>

                    <button
                      onClick={() => {
                        logout();
                        setShowUserMenu(false);
                        navigate('/login');
                      }}
                      className="w-full flex items-center gap-2 px-4 py-2 text-xs text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/30"
                    >
                      <LogOut className="w-4 h-4" /> Sign Out
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <Link
                to="/login"
                className="px-3.5 py-1.5 text-xs font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors"
              >
                Sign In
              </Link>
            )}

          </div>
        </div>
      </header>
      <UniversalSearchModal isOpen={isSearchOpen} onClose={() => setIsSearchOpen(false)} />
    </>
  );
};
