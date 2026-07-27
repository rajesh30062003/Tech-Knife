import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';
import { Sidebar } from './Sidebar';
import { Breadcrumb } from './Breadcrumb';
import { Footer } from './Footer';

export const DashboardLayout: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 flex flex-col font-sans transition-colors">
      <Navbar
        toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)}
        isSidebarOpen={isSidebarOpen}
      />
      
      <div className="flex-1 flex overflow-hidden">
        <Sidebar isOpen={isSidebarOpen} />
        
        <main className="flex-1 flex flex-col min-w-0 overflow-y-auto">
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
