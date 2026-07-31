import React, { useState } from 'react';
import { 
  ChevronDown, ChevronRight, Crown, Mail, Phone, Building, Briefcase, 
  CheckCircle2, XCircle, Shield, User, Users, Sparkles 
} from 'lucide-react';
import { OrgNode } from '../../api/organization';

interface OrgChartNodeProps {
  node: OrgNode;
  onSelectNode: (node: OrgNode) => void;
  selectedId?: string;
  searchQuery?: string;
  depth?: number;
}

export const OrgChartNode: React.FC<OrgChartNodeProps> = ({
  node,
  onSelectNode,
  selectedId,
  searchQuery = '',
  depth = 0,
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(true);
  const hasSubordinates = node.subordinates && node.subordinates.length > 0;
  const isSelected = selectedId === node.employeeId;

  // Search matching highlight check
  const isSearchMatch = Boolean(
    searchQuery.trim() &&
      (`${node.firstName} ${node.lastName}`.toLowerCase().includes(searchQuery.toLowerCase()) ||
        node.employeeId.toLowerCase().includes(searchQuery.toLowerCase()) ||
        node.designation.toLowerCase().includes(searchQuery.toLowerCase()) ||
        node.department.toLowerCase().includes(searchQuery.toLowerCase()) ||
        node.email.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  return (
    <div className="flex flex-col items-center relative">
      {/* Employee Node Card */}
      <div
        onClick={() => onSelectNode(node)}
        className={`group relative p-4 rounded-2xl border transition-all duration-200 cursor-pointer w-[280px] sm:w-[300px] bg-white dark:bg-slate-900 shadow-sm hover:shadow-xl ${
          isSearchMatch
            ? 'ring-4 ring-amber-400 border-amber-500 bg-amber-50/30 dark:bg-amber-950/40 shadow-amber-500/20'
            : isSelected
            ? 'border-indigo-600 ring-2 ring-indigo-500/40 bg-indigo-50/20 dark:bg-indigo-950/40'
            : 'border-slate-200 dark:border-slate-800 hover:border-indigo-400 dark:hover:border-indigo-600'
        }`}
      >
        {isSearchMatch && (
          <span className="absolute -top-2.5 right-4 px-2 py-0.5 rounded-full bg-amber-500 text-slate-950 font-extrabold text-[9px] uppercase tracking-wider flex items-center gap-1 shadow-sm">
            <Sparkles className="w-2.5 h-2.5" /> Match Found
          </span>
        )}

        <div className="flex items-start gap-3">
          {/* Profile Photo Avatar */}
          <div className="relative shrink-0 mt-0.5">
            <img
              src={node.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(node.firstName + ' ' + node.lastName)}&background=0D8ABC&color=fff`}
              alt={`${node.firstName} ${node.lastName}`}
              className="w-12 h-12 rounded-2xl object-cover border-2 border-indigo-100 dark:border-indigo-950 shadow-xs"
            />
            <span
              className={`absolute -bottom-1 -right-1 w-3 h-3 rounded-full border-2 border-white dark:border-slate-900 ${
                (node as any).status === 'Inactive' ? 'bg-rose-500' : 'bg-emerald-500'
              }`}
              title={`Status: ${(node as any).status || 'Active'}`}
            />
          </div>

          {/* Employee Details */}
          <div className="flex-1 min-w-0 space-y-0.5">
            <div className="flex items-center justify-between gap-1">
              <h4 className="text-xs font-extrabold text-slate-900 dark:text-white truncate group-hover:text-indigo-600 transition-colors">
                {node.firstName} {node.lastName}
              </h4>
              <span className="text-[10px] font-mono font-bold text-slate-400 bg-slate-100 dark:bg-slate-800 px-1.5 py-0.5 rounded shrink-0">
                {node.employeeId}
              </span>
            </div>

            <div className="text-[11px] font-bold text-indigo-600 dark:text-indigo-400 truncate flex items-center gap-1">
              <Briefcase className="w-3 h-3 shrink-0" />
              <span className="truncate">{node.designation}</span>
            </div>

            <div className="text-[10px] text-slate-500 dark:text-slate-400 truncate flex items-center gap-1">
              <Building className="w-3 h-3 shrink-0" />
              <span className="truncate">{node.department} • {(node as any).company || 'Tech Knife'}</span>
            </div>

            <div className="text-[10px] text-slate-400 truncate flex items-center gap-1">
              <Mail className="w-3 h-3 shrink-0" />
              <span className="truncate">{node.email}</span>
            </div>

            <div className="text-[10px] text-slate-400 truncate flex items-center gap-1">
              <Phone className="w-3 h-3 shrink-0" />
              <span className="truncate">{(node as any).mobileNumber || '+91 98765 43210'}</span>
            </div>
          </div>
        </div>

        {/* Card Footer Badges */}
        <div className="mt-3 pt-2.5 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-[10px]">
          <span className="font-semibold text-slate-400 flex items-center gap-1 truncate">
            <User className="w-3 h-3" />
            <span className="truncate">Manager: {node.managerId || 'Board / CEO'}</span>
          </span>

          <span className="px-2 py-0.5 rounded-full bg-indigo-50 dark:bg-indigo-950/80 text-indigo-600 dark:text-indigo-400 font-extrabold shrink-0 border border-indigo-200/50">
            {node.directReportsCount} Direct Reports
          </span>
        </div>

        {/* Expand / Collapse Action */}
        {hasSubordinates && (
          <button
            onClick={(e) => {
              e.stopPropagation();
              setIsExpanded(!isExpanded);
            }}
            className="mt-2.5 w-full py-1 text-[10px] font-bold text-slate-600 dark:text-slate-300 hover:text-indigo-600 bg-slate-50 dark:bg-slate-800/80 rounded-xl flex items-center justify-center gap-1 transition-colors border border-slate-200/50 dark:border-slate-700/50"
          >
            {isExpanded ? (
              <>
                <ChevronDown className="w-3 h-3" /> Collapse Subordinates ({node.subordinates.length})
              </>
            ) : (
              <>
                <ChevronRight className="w-3 h-3" /> Expand Subordinates ({node.subordinates.length})
              </>
            )}
          </button>
        )}
      </div>

      {/* Connecting Stem */}
      {hasSubordinates && isExpanded && (
        <div className="w-0.5 h-6 bg-indigo-300 dark:bg-indigo-800/80 my-1"></div>
      )}

      {/* Subordinates Container */}
      {hasSubordinates && isExpanded && (
        <div className="flex flex-wrap justify-center gap-8 relative pt-2">
          {node.subordinates.map((sub) => (
            <OrgChartNode
              key={sub.employeeId}
              node={sub}
              onSelectNode={onSelectNode}
              selectedId={selectedId}
              searchQuery={searchQuery}
              depth={depth + 1}
            />
          ))}
        </div>
      )}
    </div>
  );
};

interface OrgChartTreeProps {
  tree: OrgNode[];
  onSelectNode: (node: OrgNode) => void;
  selectedId?: string;
  searchQuery?: string;
  zoomScale?: number;
}

export const OrgChartTree: React.FC<OrgChartTreeProps> = ({
  tree,
  onSelectNode,
  selectedId,
  searchQuery = '',
  zoomScale = 1,
}) => {
  if (!tree || tree.length === 0) {
    return (
      <div className="p-16 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl">
        <Users className="w-12 h-12 text-slate-300 dark:text-slate-700 mx-auto mb-3" />
        <h3 className="text-base font-bold text-slate-700 dark:text-slate-300">No active organizational records found</h3>
        <p className="text-xs text-slate-500">Ensure employees exist in MongoDB Atlas and reporting managers are assigned.</p>
      </div>
    );
  }

  return (
    <div className="w-full overflow-auto p-8 bg-slate-50/50 dark:bg-slate-900/40 rounded-3xl border border-slate-200/80 dark:border-slate-800/80 min-h-[600px] flex flex-col items-center custom-scrollbar">
      {/* Zoom Container Wrapper */}
      <div
        className="transition-transform duration-250 ease-out origin-top flex flex-col items-center"
        style={{ transform: `scale(${zoomScale})` }}
      >
        {/* Board of Directors Top Header Banner */}
        <div className="flex flex-col items-center mb-6 shrink-0">
          <div className="px-6 py-3 rounded-2xl bg-gradient-to-r from-slate-950 via-indigo-950 to-slate-950 border border-indigo-500/50 text-white shadow-xl flex items-center gap-3">
            <Crown className="w-4 h-4 text-amber-400 animate-pulse" />
            <span className="text-xs font-extrabold tracking-wider uppercase">Board of Directors</span>
            <span className="px-2 py-0.5 rounded bg-indigo-500/30 text-[10px] font-mono text-indigo-300">Governing Body</span>
          </div>
          <div className="w-0.5 h-6 bg-indigo-400 dark:bg-indigo-600 my-1"></div>
        </div>

        {/* CEO / MD Top Executive Roots */}
        <div className="inline-flex flex-row justify-center items-start gap-12 py-2">
          {tree.map((rootNode) => (
            <OrgChartNode
              key={rootNode.employeeId}
              node={rootNode}
              onSelectNode={onSelectNode}
              selectedId={selectedId}
              searchQuery={searchQuery}
            />
          ))}
        </div>
      </div>
    </div>
  );
};
