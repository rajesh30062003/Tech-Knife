import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  Sun, Moon, Laptop, Bell, Search, Shield, User, LogOut, 
  Menu, X, Check, MessageSquare, Plus, FolderKanban, Clock, CalendarDays, Ticket, Sparkles
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useTheme, ThemeMode } from '../../context/ThemeContext';
import { UniversalSearchModal } from '../core/UniversalSearchModal';
import { Logo } from '../common/Logo';

interface NavbarProps {
  toggleSidebar: () => void;
  isSidebarOpen: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({ toggleSidebar, isSidebarOpen }) => {
  const { user, logout } = useAuth();
  const { themeMode, setThemeMode } = useTheme();
  const navigate = useNavigate();

  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showThemeMenu, setShowThemeMenu] = useState(false);
  const [showQuickActions, setShowQuickActions] = useState(false);
  const [showMessagesMenu, setShowMessagesMenu] = useState(false);
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

  const closeAllMenus = () => {
    setShowUserMenu(false);
    setShowThemeMenu(false);
    setShowQuickActions(false);
    setShowMessagesMenu(false);
  };

  return (
    <>
      <header className="h-full w-full bg-white/95 dark:bg-slate-900/95 backdrop-blur-md transition-colors px-4 flex items-center justify-between gap-2 sm:gap-4 select-none">
        
        {/* Left Section: Toggle Button & Tech Knife Logo with Tagline */}
        <div className="flex items-center gap-2 sm:gap-3 shrink-0">
          <button
            onClick={toggleSidebar}
            className="p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"
            title={isSidebarOpen ? "Collapse navigation sidebar" : "Expand navigation sidebar"}
          >
            {isSidebarOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>

          <Link to="/dashboard" className="flex items-center gap-2">
            <Logo variant="full" size="md" showTagline={true} />
          </Link>
        </div>

        {/* Center Section: Universal Search Trigger (⌘K) */}
        <div
          onClick={() => setIsSearchOpen(true)}
          className="hidden md:flex flex-1 max-w-md items-center justify-between px-3.5 py-1.5 bg-slate-100 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/80 rounded-xl cursor-pointer hover:border-cyan-500 transition-all text-xs font-medium text-slate-500 dark:text-slate-400 shadow-2xs"
        >
          <div className="flex items-center gap-2 truncate">
            <Search className="w-4 h-4 text-cyan-500 shrink-0" />
            <span className="truncate">Universal Search (employees, projects, tickets, leads)...</span>
          </div>
          <kbd className="px-1.5 py-0.5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded text-[10px] font-mono font-bold text-slate-500 shrink-0">
            ⌘K
          </kbd>
        </div>

        {/* Right Section: Mobile Search Button, Quick Actions, Role Badge, Theme, Messages, Notifications & Profile */}
        <div className="flex items-center gap-1.5 sm:gap-2 shrink-0">
          
          {/* Mobile Search Button (<768px) */}
          <button
            onClick={() => setIsSearchOpen(true)}
            className="md:hidden p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"
            title="Search"
          >
            <Search className="w-4.5 h-4.5" />
          </button>

          {/* Quick Actions Dropdown */}
          <div className="relative">
            <button
              onClick={() => {
                setShowQuickActions(!showQuickActions);
                setShowUserMenu(false);
                setShowThemeMenu(false);
                setShowMessagesMenu(false);
              }}
              className="p-2 bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 hover:bg-cyan-500/20 rounded-xl transition-all border border-cyan-500/20 flex items-center gap-1 text-xs font-extrabold"
              title="Quick Actions"
            >
              <Plus className="w-4 h-4" />
              <span className="hidden sm:inline">Actions</span>
            </button>

            {showQuickActions && (
              <div className="absolute right-0 mt-2 w-52 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl py-2 z-50 animate-in fade-in slide-in-from-top-2">
                <div className="px-3 py-1.5 text-[10px] font-extrabold uppercase tracking-wider text-slate-400 border-b border-slate-100 dark:border-slate-800 mb-1">
                  Quick Actions
                </div>
                <Link
                  to="/projects"
                  onClick={closeAllMenus}
                  className="flex items-center gap-2.5 px-3 py-2 text-xs font-bold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                >
                  <FolderKanban className="w-4 h-4 text-cyan-500" /> New Project Card
                </Link>
                <Link
                  to="/support"
                  onClick={closeAllMenus}
                  className="flex items-center gap-2.5 px-3 py-2 text-xs font-bold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                >
                  <Ticket className="w-4 h-4 text-amber-500" /> Create Support Ticket
                </Link>
                <Link
                  to="/attendance"
                  onClick={closeAllMenus}
                  className="flex items-center gap-2.5 px-3 py-2 text-xs font-bold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                >
                  <Clock className="w-4 h-4 text-emerald-500" /> Log Time / Attendance
                </Link>
                <Link
                  to="/leave"
                  onClick={closeAllMenus}
                  className="flex items-center gap-2.5 px-3 py-2 text-xs font-bold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                >
                  <CalendarDays className="w-4 h-4 text-indigo-500" /> Apply Leave
                </Link>
              </div>
            )}
          </div>

          {/* Current Role Badge */}
          {user && (
            <div
              className="hidden lg:flex items-center gap-1.5 px-3 py-1 text-xs font-extrabold rounded-xl bg-slate-900 text-cyan-400 border border-slate-800 shadow-2xs"
              title="Authenticated User Role"
            >
              <Shield className="w-3.5 h-3.5 text-cyan-400" />
              <span>{user?.role?.replace('ROLE_', '') || 'EMPLOYEE'}</span>
            </div>
          )}

          {/* Theme Selector Toggle (Light / Dark / System) */}
          <div className="relative">
            <button
              onClick={() => {
                setShowThemeMenu(!showThemeMenu);
                setShowUserMenu(false);
                setShowQuickActions(false);
                setShowMessagesMenu(false);
              }}
              className="p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"
              title="Theme Settings"
            >
              {themeMode === 'light' && <Sun className="w-4 h-4 text-amber-500" />}
              {themeMode === 'dark' && <Moon className="w-4 h-4 text-cyan-400" />}
              {themeMode === 'system' && <Laptop className="w-4 h-4 text-slate-400" />}
            </button>

            {showThemeMenu && (
              <div className="absolute right-0 mt-2 w-44 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl py-1.5 z-50">
                <div className="px-3 py-1 text-[10px] font-extrabold uppercase tracking-wider text-slate-400 border-b border-slate-100 dark:border-slate-800 mb-1">
                  Theme Appearance
                </div>
                {[
                  { mode: 'light' as ThemeMode, label: 'Light Theme', icon: Sun },
                  { mode: 'dark' as ThemeMode, label: 'Dark Mode', icon: Moon },
                  { mode: 'system' as ThemeMode, label: 'System Default', icon: Laptop },
                ].map((item) => {
                  const IconComp = item.icon;
                  return (
                    <button
                      key={item.mode}
                      onClick={() => {
                        setThemeMode(item.mode);
                        setShowThemeMenu(false);
                      }}
                      className={`w-full flex items-center justify-between px-3 py-2 text-xs text-left hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors ${
                        themeMode === item.mode ? 'font-extrabold text-cyan-600 dark:text-cyan-400 bg-cyan-500/10' : 'text-slate-700 dark:text-slate-300'
                      }`}
                    >
                      <div className="flex items-center gap-2">
                        <IconComp className="w-4 h-4" />
                        {item.label}
                      </div>
                      {themeMode === item.mode && <Check className="w-3.5 h-3.5 text-cyan-500" />}
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          {/* Messages Icon Button & Dropdown */}
          <div className="relative">
            <button
              onClick={() => {
                setShowMessagesMenu(!showMessagesMenu);
                setShowUserMenu(false);
                setShowThemeMenu(false);
                setShowQuickActions(false);
              }}
              className="relative p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"
              title="Messages & Inbox"
            >
              <MessageSquare className="w-4 h-4" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-cyan-500 rounded-full ring-2 ring-white dark:ring-slate-900"></span>
            </button>

            {showMessagesMenu && (
              <div className="absolute right-0 mt-2 w-72 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl py-2 z-50">
                <div className="px-4 py-2 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
                  <span className="text-xs font-extrabold text-slate-900 dark:text-white">Messages & Dispatch</span>
                  <span className="px-2 py-0.5 text-[9px] font-extrabold rounded-full bg-cyan-500/20 text-cyan-500">2 New</span>
                </div>
                <div className="p-3 space-y-2 text-xs">
                  <div className="p-2 rounded-xl bg-slate-50 dark:bg-slate-800 space-y-1">
                    <p className="font-bold text-slate-800 dark:text-slate-200">DevOps Release Alert</p>
                    <p className="text-[11px] text-slate-500">Sprint 14 deployment build was pushed to staging.</p>
                  </div>
                  <div className="p-2 rounded-xl bg-slate-50 dark:bg-slate-800 space-y-1">
                    <p className="font-bold text-slate-800 dark:text-slate-200">HR Portal Update</p>
                    <p className="text-[11px] text-slate-500">Quarterly performance appraisal cycle open.</p>
                  </div>
                </div>
                <div className="border-t border-slate-100 dark:border-slate-800 px-4 py-2 text-center">
                  <Link to="/support" onClick={closeAllMenus} className="text-xs font-bold text-cyan-600 dark:text-cyan-400 hover:underline">
                    View All Support Tickets ➔
                  </Link>
                </div>
              </div>
            )}
          </div>

          {/* Notifications Link */}
          <Link
            to="/notifications"
            className="relative p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"
            title="Notifications Center"
          >
            <Bell className="w-4 h-4" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-rose-500 rounded-full ring-2 ring-white dark:ring-slate-900 animate-pulse"></span>
          </Link>

          {/* User Profile Dropdown Menu */}
          {user ? (
            <div className="relative">
              <button
                onClick={() => {
                  setShowUserMenu(!showUserMenu);
                  setShowThemeMenu(false);
                  setShowQuickActions(false);
                  setShowMessagesMenu(false);
                }}
                className="flex items-center gap-2 p-1 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
              >
                {user.avatarUrl ? (
                  <img
                    src={user.avatarUrl}
                    alt={user.firstName}
                    className="w-8 h-8 rounded-full object-cover border border-slate-200 dark:border-slate-700"
                  />
                ) : (
                  <div className="w-8 h-8 rounded-full bg-slate-900 text-cyan-400 border border-slate-700 flex items-center justify-center text-xs font-extrabold shadow-2xs">
                    {user.firstName?.[0] || 'T'}{user.lastName?.[0] || 'K'}
                  </div>
                )}
              </button>

              {showUserMenu && (
                <div className="absolute right-0 mt-2 w-64 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl py-2 z-50">
                  <div className="px-4 py-2 border-b border-slate-100 dark:border-slate-800">
                    <p className="text-sm font-extrabold text-slate-900 dark:text-white">
                      {user.firstName} {user.lastName}
                    </p>
                    <p className="text-xs text-slate-500 dark:text-slate-400 truncate">{user.email}</p>
                    <div className="mt-1.5 flex items-center gap-1">
                      <span className="inline-block px-2 py-0.5 text-[10px] font-extrabold rounded bg-cyan-500/10 text-cyan-600 dark:bg-cyan-400/20 dark:text-cyan-300">
                        {user.designation || 'Enterprise Specialist'}
                      </span>
                    </div>
                  </div>

                  <Link
                    to="/profile"
                    onClick={closeAllMenus}
                    className="flex items-center gap-2 px-4 py-2.5 text-xs font-bold text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                  >
                    <User className="w-4 h-4 text-cyan-500" /> Account Profile
                  </Link>

                  <button
                    onClick={() => {
                      logout();
                      closeAllMenus();
                      navigate('/login');
                    }}
                    className="w-full flex items-center gap-2 px-4 py-2.5 text-xs font-bold text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/30 transition-colors"
                  >
                    <LogOut className="w-4 h-4" /> Sign Out
                  </button>
                </div>
              )}
            </div>
          ) : (
            <Link
              to="/login"
              className="px-4 py-2 text-xs font-extrabold text-slate-950 bg-cyan-500 hover:bg-cyan-400 rounded-xl transition-all shadow-md"
            >
              Sign In
            </Link>
          )}

        </div>
      </header>

      <UniversalSearchModal isOpen={isSearchOpen} onClose={() => setIsSearchOpen(false)} />
    </>
  );
};
