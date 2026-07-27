import React, { useState } from 'react';
import { 
  ChevronDown, ChevronRight, User, Users, Mail, Building2, Shield, ArrowUpRight, Award 
} from 'lucide-react';
import { OrgNode } from '../../api/organization';

interface OrgChartNodeProps {
  node: OrgNode;
  onSelectNode: (node: OrgNode) => void;
  selectedId?: string;
  depth?: number;
}

export const OrgChartNode: React.FC<OrgChartNodeProps> = ({
  node,
  onSelectNode,
  selectedId,
  depth = 0,
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(true);
  const hasSubordinates = node.subordinates && node.subordinates.length > 0;
  const isSelected = selectedId === node.employeeId;

  return (
    <div className="flex flex-col items-center relative">
      {/* Node Card */}
      <div
        onClick={() => onSelectNode(node)}
        className={`group relative p-4 rounded-2xl border transition-all cursor-pointer min-w-[240px] max-w-[280px] bg-white dark:bg-slate-900 shadow-xs hover:shadow-md ${
          isSelected
            ? 'border-indigo-600 ring-2 ring-indigo-500/30 dark:ring-indigo-500/40 bg-indigo-50/20 dark:bg-indigo-950/30'
            : 'border-slate-200 dark:border-slate-800 hover:border-indigo-300 dark:hover:border-indigo-700'
        }`}
      >
        <div className="flex items-center gap-3">
          {/* Avatar */}
          <div className="relative shrink-0">
            <img
              src={node.avatarUrl || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200'}
              alt={`${node.firstName} ${node.lastName}`}
              className="w-11 h-11 rounded-full object-cover border-2 border-indigo-100 dark:border-indigo-950"
            />
            {node.directReportsCount > 0 && (
              <span className="absolute -bottom-1 -right-1 px-1.5 py-0.2 bg-indigo-600 text-white text-[9px] font-extrabold rounded-full border border-white dark:border-slate-900 shadow-2xs">
                {node.directReportsCount}
              </span>
            )}
          </div>

          {/* Info */}
          <div className="flex-1 min-w-0">
            <h4 className="text-xs font-extrabold text-slate-900 dark:text-white truncate group-hover:text-indigo-600 transition-colors">
              {node.firstName} {node.lastName}
            </h4>
            <div className="text-[11px] font-semibold text-indigo-600 dark:text-indigo-400 truncate">
              {node.designation}
            </div>
            <div className="text-[10px] text-slate-400 truncate">
              {node.department}
            </div>
          </div>
        </div>

        {/* Expand / Collapse Action */}
        {hasSubordinates && (
          <button
            onClick={(e) => {
              e.stopPropagation();
              setIsExpanded(!isExpanded);
            }}
            className="mt-3 w-full py-1 text-[10px] font-bold text-slate-500 hover:text-indigo-600 bg-slate-50 dark:bg-slate-800/80 rounded-lg flex items-center justify-center gap-1 transition-colors"
          >
            {isExpanded ? (
              <>
                <ChevronDown className="w-3 h-3" /> Hide Subordinates ({node.subordinates.length})
              </>
            ) : (
              <>
                <ChevronRight className="w-3 h-3" /> Show Direct Reports ({node.subordinates.length})
              </>
            )}
          </button>
        )}
      </div>

      {/* Connecting Vertical Stem */}
      {hasSubordinates && isExpanded && (
        <div className="w-0.5 h-6 bg-slate-200 dark:bg-slate-800 my-1"></div>
      )}

      {/* Subordinates Container */}
      {hasSubordinates && isExpanded && (
        <div className="flex flex-wrap justify-center gap-6 relative pt-2">
          {node.subordinates.map((sub) => (
            <OrgChartNode
              key={sub.employeeId}
              node={sub}
              onSelectNode={onSelectNode}
              selectedId={selectedId}
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
}

export const OrgChartTree: React.FC<OrgChartTreeProps> = ({
  tree,
  onSelectNode,
  selectedId,
}) => {
  if (!tree || tree.length === 0) {
    return (
      <div className="p-12 text-center bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl">
        <Users className="w-10 h-10 text-slate-300 dark:text-slate-700 mx-auto mb-2" />
        <h3 className="text-sm font-bold text-slate-700 dark:text-slate-300">No organizational tree nodes found</h3>
        <p className="text-xs text-slate-500">Ensure employees have reporting managers assigned.</p>
      </div>
    );
  }

  return (
    <div className="w-full overflow-x-auto p-6 bg-slate-50/50 dark:bg-slate-900/30 rounded-3xl border border-slate-200/80 dark:border-slate-800/80 min-h-[500px] flex justify-center custom-scrollbar">
      <div className="inline-flex flex-col items-center gap-8 py-4">
        {tree.map((rootNode) => (
          <OrgChartNode
            key={rootNode.employeeId}
            node={rootNode}
            onSelectNode={onSelectNode}
            selectedId={selectedId}
          />
        ))}
      </div>
    </div>
  );
};
