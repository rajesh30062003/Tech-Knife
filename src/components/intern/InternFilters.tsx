import React from 'react';
import { Search, Filter, RefreshCw, GraduationCap, Users } from 'lucide-react';

interface InternFiltersProps {
  searchQuery: string;
  setSearchQuery: (q: string) => void;
  departmentFilter: string;
  setDepartmentFilter: (dept: string) => void;
  statusFilter: string;
  setStatusFilter: (status: string) => void;
  mentorFilter: string;
  setMentorFilter: (mentor: string) => void;
  departments: string[];
  mentors: string[];
  onReset: () => void;
}

export const InternFilters: React.FC<InternFiltersProps> = ({
  searchQuery,
  setSearchQuery,
  departmentFilter,
  setDepartmentFilter,
  statusFilter,
  setStatusFilter,
  mentorFilter,
  setMentorFilter,
  departments,
  mentors,
  onReset,
}) => {
  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm space-y-3">
      <div className="flex flex-col md:flex-row items-center justify-between gap-3">
        {/* Search Input */}
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search by intern ID, name, email, university, or track..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-xs bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl focus:outline-none focus:ring-2 focus:ring-cyan-500/50 text-slate-900 dark:text-slate-100 font-medium"
          />
        </div>

        {/* Filters */}
        <div className="flex flex-wrap items-center gap-2 w-full md:w-auto">
          {/* Department */}
          <select
            value={departmentFilter}
            onChange={(e) => setDepartmentFilter(e.target.value)}
            className="px-3 py-2 text-xs bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-800 dark:text-slate-200 font-medium focus:outline-none focus:ring-2 focus:ring-cyan-500/50"
          >
            <option value="ALL">All Departments</option>
            {departments.map((d) => (
              <option key={d} value={d}>
                {d}
              </option>
            ))}
          </select>

          {/* Status */}
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 text-xs bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-800 dark:text-slate-200 font-medium focus:outline-none focus:ring-2 focus:ring-cyan-500/50"
          >
            <option value="ALL">All Statuses</option>
            <option value="Active">Active</option>
            <option value="On Review">On Review</option>
            <option value="Graduated">Graduated</option>
            <option value="Suspended">Suspended</option>
            <option value="Converted to Employee">Converted to Employee</option>
          </select>

          {/* Mentor */}
          <select
            value={mentorFilter}
            onChange={(e) => setMentorFilter(e.target.value)}
            className="px-3 py-2 text-xs bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-800 dark:text-slate-200 font-medium focus:outline-none focus:ring-2 focus:ring-cyan-500/50"
          >
            <option value="ALL">All Mentors</option>
            {mentors.map((m) => (
              <option key={m} value={m}>
                {m}
              </option>
            ))}
          </select>

          {/* Reset button */}
          <button
            onClick={onReset}
            className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-600 dark:text-slate-300 rounded-xl text-xs transition-colors"
            title="Reset Filters"
          >
            <RefreshCw className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </div>
  );
};
