import React, { useState, useEffect } from 'react';
import { 
  Search, Command, Briefcase, Users, FileText, Settings, Shield, 
  Terminal, ArrowRight, CornerDownLeft, Sparkles, Folder, Layers, Zap
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface CommandPaletteProps {
  isOpen: boolean;
  onClose: () => void;
}

interface CommandItem {
  id: string;
  title: string;
  category: 'Navigation' | 'Projects' | 'DevOps' | 'Security' | 'Reports';
  shortcut?: string;
  action: () => void;
}

export const CommandPalette: React.FC<CommandPaletteProps> = ({ isOpen, onClose }) => {
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [selectedIndex, setSelectedIndex] = useState(0);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        if (isOpen) onClose();
        else {
          // Open triggered by global listener if implemented
        }
      } else if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const commands: CommandItem[] = [
    { id: 'c1', title: 'Go to Projects Workspace', category: 'Navigation', shortcut: 'G P', action: () => { navigate('/projects'); onClose(); } },
    { id: 'c2', title: 'Go to Employee Directory', category: 'Navigation', shortcut: 'G E', action: () => { navigate('/employees'); onClose(); } },
    { id: 'c3', title: 'Go to Intern Management', category: 'Navigation', shortcut: 'G I', action: () => { navigate('/interns'); onClose(); } },
    { id: 'c4', title: 'Go to Customer Portal', category: 'Navigation', shortcut: 'G C', action: () => { navigate('/customer-portal'); onClose(); } },
    { id: 'c5', title: 'Go to Executive Reports & Audit', category: 'Navigation', shortcut: 'G R', action: () => { navigate('/reports'); onClose(); } },
    { id: 'c6', title: 'Trigger Universal PDF Export', category: 'Reports', action: () => { navigate('/reports'); onClose(); } },
    { id: 'c7', title: 'Inspect Security & OAuth 2.0 Tokens', category: 'Security', action: () => { navigate('/settings'); onClose(); } },
    { id: 'c8', title: 'View CI/CD DevOps Pipeline Status', category: 'DevOps', action: () => { navigate('/projects'); onClose(); } },
  ];

  const filtered = commands.filter(c => c.title.toLowerCase().includes(query.toLowerCase()) || c.category.toLowerCase().includes(query.toLowerCase()));

  const handleSelect = (index: number) => {
    if (filtered[index]) {
      filtered[index].action();
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-start justify-center pt-20 p-4 select-none animate-in fade-in duration-150">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-2xl w-full shadow-2xl overflow-hidden flex flex-col">
        
        {/* Input Bar */}
        <div className="p-4 border-b border-slate-200 dark:border-slate-800 flex items-center gap-3">
          <Search className="w-5 h-5 text-cyan-500 shrink-0" />
          <input
            type="text"
            autoFocus
            value={query}
            onChange={(e) => { setQuery(e.target.value); setSelectedIndex(0); }}
            placeholder="Type a command, search projects, or jump to page... (Ctrl + K)"
            className="w-full text-sm font-medium bg-transparent border-none outline-none text-slate-900 dark:text-slate-100 placeholder-slate-400"
          />
          <span className="px-2 py-1 bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-500 font-mono text-[10px] font-bold rounded-lg shrink-0">
            ESC
          </span>
        </div>

        {/* Command Items Stream */}
        <div className="max-h-96 overflow-y-auto p-2 divide-y divide-slate-100 dark:divide-slate-800/60">
          {filtered.length === 0 ? (
            <div className="p-8 text-center text-xs text-slate-400 font-medium">
              No matching commands or workspace assets found.
            </div>
          ) : (
            filtered.map((cmd, idx) => (
              <div
                key={cmd.id}
                onClick={() => handleSelect(idx)}
                onMouseEnter={() => setSelectedIndex(idx)}
                className={`p-3 rounded-2xl flex items-center justify-between gap-3 cursor-pointer transition-all ${
                  selectedIndex === idx
                    ? 'bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 font-extrabold'
                    : 'text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800/40'
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className={`p-2 rounded-xl ${selectedIndex === idx ? 'bg-cyan-500 text-slate-950' : 'bg-slate-100 dark:bg-slate-800 text-slate-500'}`}>
                    <Briefcase className="w-4 h-4" />
                  </div>
                  <div>
                    <h4 className="text-xs font-bold">{cmd.title}</h4>
                    <span className="text-[10px] text-slate-400 uppercase font-mono">{cmd.category}</span>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  {cmd.shortcut && (
                    <span className="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-500 font-mono text-[10px] font-bold rounded-md">
                      {cmd.shortcut}
                    </span>
                  )}
                  <CornerDownLeft className="w-3.5 h-3.5 text-slate-400" />
                </div>
              </div>
            ))
          )}
        </div>

        {/* Command Palette Footer */}
        <div className="p-3 bg-slate-50 dark:bg-slate-950 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between text-[11px] text-slate-400 font-medium px-4">
          <span className="flex items-center gap-1.5">
            <Command className="w-3 h-3 text-cyan-500" /> TechKnife Enterprise Command Palette
          </span>
          <span className="font-mono text-[10px]">Use ↑ ↓ to navigate, ↵ to select</span>
        </div>
      </div>
    </div>
  );
};
