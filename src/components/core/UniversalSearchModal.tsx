import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Search,
  X,
  Users,
  FolderKanban,
  Building2,
  GraduationCap,
  FileText,
  DollarSign,
  Ticket,
  Clock,
  ArrowRight,
  Sparkles,
  Command
} from 'lucide-react';
import { searchApi } from '../../api/coreServices';
import { UniversalSearchResult, SearchModuleType } from '../../types';

interface UniversalSearchModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const MODULE_TABS: Array<{ id: 'ALL' | SearchModuleType; label: string; icon: React.ReactNode }> = [
  { id: 'ALL', label: 'All Modules', icon: <Sparkles className="w-3.5 h-3.5" /> },
  { id: 'Employees', label: 'Employees', icon: <Users className="w-3.5 h-3.5" /> },
  { id: 'Projects', label: 'Projects', icon: <FolderKanban className="w-3.5 h-3.5" /> },
  { id: 'Customers', label: 'Customers', icon: <Building2 className="w-3.5 h-3.5" /> },
  { id: 'Interns', label: 'Interns', icon: <GraduationCap className="w-3.5 h-3.5" /> },
  { id: 'Documents', label: 'Documents', icon: <FileText className="w-3.5 h-3.5" /> },
  { id: 'Payroll', label: 'Payroll', icon: <DollarSign className="w-3.5 h-3.5" /> },
  { id: 'Tickets', label: 'Tickets', icon: <Ticket className="w-3.5 h-3.5" /> },
];

export const UniversalSearchModal: React.FC<UniversalSearchModalProps> = ({ isOpen, onClose }) => {
  const navigate = useNavigate();
  const inputRef = useRef<HTMLInputElement>(null);

  const [query, setQuery] = useState('');
  const [activeTab, setActiveTab] = useState<'ALL' | SearchModuleType>('ALL');
  const [results, setResults] = useState<UniversalSearchResult[]>([]);
  const [isSearching, setIsSearching] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => inputRef.current?.focus(), 50);
    } else {
      setQuery('');
      setResults([]);
    }
  }, [isOpen]);

  useEffect(() => {
    const fetchResults = async () => {
      if (!query.trim()) {
        setResults([]);
        return;
      }
      setIsSearching(true);
      try {
        const res = await searchApi.universalSearch(query);
        setResults(res);
      } finally {
        setIsSearching(false);
      }
    };

    const timer = setTimeout(fetchResults, 200);
    return () => clearTimeout(timer);
  }, [query]);

  if (!isOpen) return null;

  const filteredResults = activeTab === 'ALL'
    ? results
    : results.filter((r) => r.module === activeTab);

  const handleSelectResult = (result: UniversalSearchResult) => {
    onClose();
    navigate(result.url);
  };

  const getModuleIcon = (module: SearchModuleType) => {
    switch (module) {
      case 'Employees': return <Users className="w-4 h-4 text-indigo-500" />;
      case 'Projects': return <FolderKanban className="w-4 h-4 text-blue-500" />;
      case 'Customers': return <Building2 className="w-4 h-4 text-purple-500" />;
      case 'Interns': return <GraduationCap className="w-4 h-4 text-cyan-500" />;
      case 'Documents': return <FileText className="w-4 h-4 text-amber-500" />;
      case 'Attendance': return <Clock className="w-4 h-4 text-emerald-500" />;
      case 'Payroll': return <DollarSign className="w-4 h-4 text-emerald-500" />;
      case 'Tickets': return <Ticket className="w-4 h-4 text-rose-500" />;
      default: return <Sparkles className="w-4 h-4 text-indigo-500" />;
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-start justify-center pt-16 px-4 animate-in fade-in duration-200">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-2xl shadow-2xl overflow-hidden flex flex-col max-h-[80vh]">
        
        {/* Search Header */}
        <div className="p-4 border-b border-slate-200 dark:border-slate-800 flex items-center gap-3">
          <Search className="w-5 h-5 text-indigo-600 dark:text-indigo-400 shrink-0" />
          <input
            ref={inputRef}
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search across Employees, Projects, Interns, Documents, Tickets..."
            className="w-full text-sm bg-transparent border-none outline-none text-slate-900 dark:text-white placeholder-slate-400 font-medium"
          />
          <button
            onClick={onClose}
            className="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Filter Tabs */}
        <div className="px-4 py-2 border-b border-slate-100 dark:border-slate-800/80 bg-slate-50/50 dark:bg-slate-950/50 flex items-center gap-1.5 overflow-x-auto custom-scrollbar">
          {MODULE_TABS.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`px-3 py-1.5 rounded-xl text-xs font-semibold flex items-center gap-1.5 whitespace-nowrap transition-all ${
                activeTab === tab.id
                  ? 'bg-indigo-600 text-white shadow-xs'
                  : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200/60 dark:hover:bg-slate-800'
              }`}
            >
              {tab.icon}
              <span>{tab.label}</span>
            </button>
          ))}
        </div>

        {/* Search Results Body */}
        <div className="flex-1 overflow-y-auto p-4 space-y-2 custom-scrollbar">
          {isSearching ? (
            <div className="py-12 text-center text-xs text-slate-400 font-semibold flex items-center justify-center gap-2">
              <Sparkles className="w-4 h-4 animate-spin text-indigo-500" /> Searching enterprise database index...
            </div>
          ) : query.trim() === '' ? (
            <div className="py-10 text-center space-y-2">
              <div className="w-10 h-10 rounded-2xl bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400 flex items-center justify-center mx-auto">
                <Command className="w-5 h-5" />
              </div>
              <h4 className="font-extrabold text-sm text-slate-800 dark:text-slate-200">Universal Enterprise Search</h4>
              <p className="text-xs text-slate-500 max-w-sm mx-auto">
                Type employee names, project codes, client accounts, intern IDs, invoice numbers, or support tickets.
              </p>
            </div>
          ) : filteredResults.length === 0 ? (
            <div className="py-10 text-center space-y-2">
              <FileText className="w-8 h-8 text-slate-400 mx-auto" />
              <h4 className="font-bold text-xs text-slate-700 dark:text-slate-300">No Matching Results Found</h4>
              <p className="text-[11px] text-slate-500">No record matched "{query}" under {activeTab} module.</p>
            </div>
          ) : (
            filteredResults.map((item) => (
              <div
                key={item.id}
                onClick={() => handleSelectResult(item)}
                className="p-3.5 rounded-2xl bg-slate-50 hover:bg-indigo-50/70 dark:bg-slate-800/40 dark:hover:bg-slate-800/90 border border-slate-200/60 dark:border-slate-800 transition-all cursor-pointer flex items-center justify-between group"
              >
                <div className="flex items-center gap-3">
                  <div className="p-2.5 bg-white dark:bg-slate-900 rounded-xl shadow-xs border border-slate-200/80 dark:border-slate-800">
                    {getModuleIcon(item.module)}
                  </div>
                  <div>
                    <div className="font-extrabold text-xs text-slate-900 dark:text-white flex items-center gap-2">
                      <span>{item.title}</span>
                      <span className="px-2 py-0.5 text-[9px] font-bold rounded-full bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300">
                        {item.module}
                      </span>
                    </div>
                    <p className="text-[11px] text-slate-500 font-medium mt-0.5">{item.subtitle}</p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <span className={`px-2.5 py-1 rounded-lg text-[10px] font-extrabold text-white ${item.badgeColor || 'bg-indigo-600'}`}>
                    {item.badge}
                  </span>
                  <ArrowRight className="w-4 h-4 text-slate-400 group-hover:text-indigo-600 dark:group-hover:text-indigo-400 group-hover:translate-x-0.5 transition-all" />
                </div>
              </div>
            ))
          )}
        </div>

        {/* Footer */}
        <div className="p-3 border-t border-slate-100 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/80 flex items-center justify-between text-[11px] text-slate-400 font-semibold">
          <span>Enterprise Search Bar</span>
          <div className="flex items-center gap-2">
            <kbd className="px-1.5 py-0.5 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded text-[10px]">ESC</kbd> to close
          </div>
        </div>

      </div>
    </div>
  );
};
