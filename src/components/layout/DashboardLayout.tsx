import React, { useState, useEffect } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Navbar } from './Navbar';
import { Sidebar } from './Sidebar';
import { Breadcrumb } from './Breadcrumb';
import { Footer } from './Footer';

export const DashboardLayout: React.FC = () => {
  // Mobile drawer state & Desktop collapse state
  const [isSidebarOpen, setIsSidebarOpen] = useState<boolean>(true);
  const location = useLocation();

  // Auto-close mobile drawer on route change on screens < 1024px
  useEffect(() => {
    if (window.innerWidth < 1024) {
      setIsSidebarOpen(false);
    }
  }, [location.pathname]);

  // Handle window resize
  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 1024) {
        setIsSidebarOpen(true);
      }
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <div className="h-screen w-screen overflow-hidden bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 flex flex-col font-['Plus_Jakarta_Sans','Inter',sans-serif] antialiased select-none-text">
      
      {/* 1. TOP FIXED HEADER (Height: 64px / h-16, Non-scrolling, z-40) */}
      <div className="h-16 w-full shrink-0 z-40 border-b border-slate-200 dark:border-slate-800">
        <Navbar
          toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)}
          isSidebarOpen={isSidebarOpen}
        />
      </div>

      {/* 2. BODY LAYOUT FLEX WRAPPER (Height: calc(100vh - 64px), Non-scrolling shell) */}
      <div className="flex-1 flex overflow-hidden relative">
        
        {/* Mobile Backdrop Overlay (Active when mobile drawer is open <1024px) */}
        {isSidebarOpen && (
          <div
            onClick={() => setIsSidebarOpen(false)}
            className="fixed inset-0 top-16 z-30 bg-slate-950/60 backdrop-blur-xs lg:hidden transition-opacity"
            aria-hidden="true"
          />
        )}

        {/* 3. INDEPENDENT SCROLLING LEFT SIDEBAR */}
        <Sidebar
          isOpen={isSidebarOpen}
          onClose={() => setIsSidebarOpen(false)}
        />

        {/* 4. INDEPENDENT SCROLLING MAIN CONTENT AREA */}
        <main className="flex-1 h-full overflow-y-auto overflow-x-hidden custom-scrollbar bg-slate-50 dark:bg-slate-950 flex flex-col min-w-0">
          <div className="p-4 sm:p-6 md:p-8 max-w-7xl w-full mx-auto flex-1 flex flex-col gap-6">
            <Breadcrumb />
            <div className="flex-1">
              <Outlet />
            </div>
          </div>
          <Footer />
        </main>
      </div>

    </div>
  );
};
