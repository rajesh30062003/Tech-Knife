import React, { useState } from 'react';
import { FolderKanban, Plus, GitBranch, Calendar, Search, Filter } from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';

export const ProjectsPage: React.FC = () => {
  const [activeView, setActiveView] = useState<'kanban' | 'list'>('kanban');

  const columns = [
    {
      title: 'Backlog',
      color: 'border-slate-300 dark:border-slate-700',
      tasks: [
        { id: 'TK-12', title: 'OAuth2 Social Login Provider Interceptor', assignee: 'Lucas Chen', priority: 'Medium', tags: ['Backend', 'Security'] },
        { id: 'TK-15', title: 'Export Payroll Summary to PDF/XLSX', assignee: 'Elena Rostova', priority: 'Low', tags: ['Reports'] },
      ]
    },
    {
      title: 'In Progress',
      color: 'border-blue-500',
      tasks: [
        { id: 'TK-04', title: 'Spring Security 6 Stateless JWT Filter', assignee: 'Sarah Connor', priority: 'High', tags: ['Spring Boot', 'Auth'] },
        { id: 'TK-08', title: 'Client Ticket Resolution Escalation Matrix', assignee: 'David Miller', priority: 'Urgent', tags: ['Support'] },
      ]
    },
    {
      title: 'Code Review',
      color: 'border-purple-500',
      tasks: [
        { id: 'TK-02', title: 'MongoDB Atlas Indexing & Sharding Rules', assignee: 'Alexander Vance', priority: 'High', tags: ['Database'] },
      ]
    },
    {
      title: 'Completed',
      color: 'border-emerald-500',
      tasks: [
        { id: 'TK-01', title: 'Tailwind CSS Enterprise Theme System', assignee: 'Elena Rostova', priority: 'Medium', tags: ['Frontend'] },
      ]
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <FolderKanban className="w-4 h-4" />
            <span>Agile Sprint Delivery</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Projects & Kanban Board</h1>
          <p className="text-xs text-slate-500">Track user story cards, pull requests, and sprint milestone delivery</p>
        </div>

        <div className="flex items-center gap-3">
          <div className="bg-slate-100 dark:bg-slate-800 p-1 rounded-xl flex items-center text-xs font-semibold">
            <button
              onClick={() => setActiveView('kanban')}
              className={`px-3 py-1.5 rounded-lg transition-all ${
                activeView === 'kanban' ? 'bg-white dark:bg-slate-900 text-indigo-600 shadow-xs' : 'text-slate-500'
              }`}
            >
              Kanban Board
            </button>
            <button
              onClick={() => setActiveView('list')}
              className={`px-3 py-1.5 rounded-lg transition-all ${
                activeView === 'list' ? 'bg-white dark:bg-slate-900 text-indigo-600 shadow-xs' : 'text-slate-500'
              }`}
            >
              List View
            </button>
          </div>

          <button className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-md">
            <Plus className="w-3.5 h-3.5" /> Create Task Card
          </button>
        </div>
      </div>

      {/* Kanban Board Layout */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {columns.map((col) => (
          <div key={col.title} className="bg-slate-100/70 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 space-y-3">
            <div className={`flex items-center justify-between border-l-4 ${col.color} pl-2.5 py-1`}>
              <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{col.title}</span>
              <span className="px-2 py-0.5 text-[10px] font-bold rounded-full bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300">
                {col.tasks.length}
              </span>
            </div>

            <div className="space-y-3">
              {col.tasks.map((task) => (
                <div key={task.id} className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl shadow-xs space-y-2 hover:border-indigo-400 transition-colors cursor-pointer">
                  <div className="flex items-center justify-between">
                    <span className="font-mono text-[10px] font-bold text-indigo-600 dark:text-indigo-400">{task.id}</span>
                    <StatusBadge status={task.priority} />
                  </div>
                  <h4 className="font-semibold text-xs text-slate-900 dark:text-slate-100 leading-snug">{task.title}</h4>
                  
                  <div className="flex flex-wrap gap-1">
                    {task.tags.map(t => (
                      <span key={t} className="px-1.5 py-0.5 text-[9px] font-semibold bg-slate-100 dark:bg-slate-800 text-slate-500 rounded">
                        {t}
                      </span>
                    ))}
                  </div>

                  <div className="pt-2 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-[11px] text-slate-400">
                    <span className="truncate">Assignee: {task.assignee}</span>
                    <GitBranch className="w-3.5 h-3.5 text-slate-400" />
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
