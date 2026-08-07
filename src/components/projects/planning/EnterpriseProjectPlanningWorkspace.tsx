import React, { useState, useEffect, useRef } from 'react';
import { 
  FileText, Zap, Layout, Type, List, CheckSquare, Table as TableIcon, Image as ImageIcon, 
  Code, Quote, Link2, AtSign, Smile, MessageSquare, History, RotateCcw, Save, Plus, 
  Download, Eye, Edit3, Trash2, Users, Move, Square, Circle, Diamond, Database, User, 
  ArrowRight, ZoomIn, ZoomOut, Maximize2, Undo, Redo, Sparkles, Check, Clock, Lock
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';
import { useAuth } from '../../../context/AuthContext';

interface EnterpriseProjectPlanningWorkspaceProps {
  project: EnterpriseProject;
}

export type PlanningDocCategory = 
  | 'Project Plan'
  | 'Business Workflow'
  | 'Technical Workflow'
  | 'Architecture Notes'
  | 'Meeting Notes'
  | 'Sprint Notes'
  | 'Deployment Notes'
  | 'Testing Notes'
  | 'Risk Notes'
  | 'Client Notes'
  | 'Release Notes'
  | 'Roadmap';

interface VersionSnapshot {
  id: string;
  versionNumber: number;
  savedBy: string;
  savedByRole: string;
  savedAt: string;
  docTitle: string;
  content: string;
  diagramDataJson: string;
}

interface DiagramNode {
  id: string;
  type: 'rectangle' | 'circle' | 'diamond' | 'cylinder' | 'actor';
  label: string;
  x: number;
  y: number;
  width: number;
  height: number;
  color: string;
}

interface DiagramEdge {
  id: string;
  fromId: string;
  toId: string;
  label?: string;
}

const DOC_CATEGORIES: PlanningDocCategory[] = [
  'Project Plan',
  'Business Workflow',
  'Technical Workflow',
  'Architecture Notes',
  'Meeting Notes',
  'Sprint Notes',
  'Deployment Notes',
  'Testing Notes',
  'Risk Notes',
  'Client Notes',
  'Release Notes',
  'Roadmap'
];

export const EnterpriseProjectPlanningWorkspace: React.FC<EnterpriseProjectPlanningWorkspaceProps> = ({ project }) => {
  const { user } = useAuth();
  const currentUserName = user ? `${user.firstName} ${user.lastName}` : 'Corporate Engineer';
  const currentUserRole = user?.role || 'Engineer';

  const [selectedCategory, setSelectedCategory] = useState<PlanningDocCategory>('Project Plan');
  const [docTitle, setDocTitle] = useState<string>(`${project.projectName || 'Project'} Master Execution & Architecture Plan`);
  const [docContent, setDocContent] = useState<string>(
    `# 1. Executive Summary & Goals\nThis master document outlines the technical architecture, business requirements, and operational workflows for ${project.projectName}.\n\n## Key Objectives:\n- High-availability microservices architecture on Java Spring Boot 3.5 & Node.js\n- MongoDB Atlas distributed persistence with automatic replication\n- Real-time STOMP WebSocket communication pipeline\n- Google Drive OAuth 2.0 file storage integration\n\n# 2. Technical Stack & Dependencies\n- Frontend: React 18, TypeScript, Tailwind CSS, Lucide Icons, Vite\n- Backend: Spring Boot 3.5, Java 21, Spring Security JWT, SockJS STOMP\n- Storage: Google Drive API v3, Cloudinary CDN\n\n# 3. Sprint Deliverables Checklist\n- [x] Configure OAuth 2.0 Refresh Token rotation\n- [x] Implement WebSocket chat attachment streaming\n- [ ] Deploy multi-region MongoDB Atlas cluster\n- [ ] Finalize Security & Role-Based Access Control (RBAC) rules`
  );

  // Active collaboration states
  const [activeUsers] = useState<{ id: string; name: string; avatar: string; color: string; status: string }[]>([
    { id: '1', name: currentUserName, avatar: currentUserName.charAt(0), color: '#06b6d4', status: 'Editing' },
    { id: '2', name: 'Ranadhir Pal', avatar: 'R', color: '#10b981', status: 'Viewing' },
    { id: '3', name: 'Vikramaditya Sharma', avatar: 'V', color: '#6366f1', status: 'Viewing' },
  ]);
  const [isTyping] = useState(false);
  const [lastSavedAt, setLastSavedAt] = useState<string>('Just now');
  const [isAutoSaving, setIsAutoSaving] = useState(false);

  // Version History states
  const [showVersionHistory, setShowVersionHistory] = useState(false);
  const [versions, setVersions] = useState<VersionSnapshot[]>([
    {
      id: 'v-1',
      versionNumber: 1,
      savedBy: 'System Auto-Save',
      savedByRole: 'System',
      savedAt: new Date(Date.now() - 3600000).toLocaleTimeString(),
      docTitle: `${project.projectName} Initial Plan`,
      content: `# Initial Draft\nCreated automatically for project initialization.`,
      diagramDataJson: '[]'
    }
  ]);

  // Diagram Editor Canvas States
  const [nodes, setNodes] = useState<DiagramNode[]>([
    { id: 'n1', type: 'actor', label: 'Client / User', x: 50, y: 80, width: 130, height: 60, color: 'bg-cyan-500/20 border-cyan-500 text-cyan-300' },
    { id: 'n2', type: 'rectangle', label: 'Vite Frontend App', x: 230, y: 80, width: 150, height: 60, color: 'bg-indigo-500/20 border-indigo-500 text-indigo-300' },
    { id: 'n3', type: 'diamond', label: 'Security Filter', x: 430, y: 70, width: 140, height: 80, color: 'bg-amber-500/20 border-amber-500 text-amber-300' },
    { id: 'n4', type: 'rectangle', label: 'Spring Boot API', x: 620, y: 80, width: 150, height: 60, color: 'bg-emerald-500/20 border-emerald-500 text-emerald-300' },
    { id: 'n5', type: 'cylinder', label: 'MongoDB Atlas', x: 820, y: 80, width: 140, height: 60, color: 'bg-rose-500/20 border-rose-500 text-rose-300' },
  ]);
  const [edges, setEdges] = useState<DiagramEdge[]>([
    { id: 'e1', fromId: 'n1', toId: 'n2', label: 'HTTP / WS' },
    { id: 'e2', fromId: 'n2', toId: 'n3', label: 'JWT Header' },
    { id: 'e3', fromId: 'n3', toId: 'n4', label: 'Authorized' },
    { id: 'e4', fromId: 'n4', toId: 'n5', label: 'Persistence' },
  ]);
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);
  const [zoomLevel, setZoomLevel] = useState<number>(1);
  const [isDraggingNode, setIsDraggingNode] = useState(false);
  const [dragOffset, setDragOffset] = useState<{ x: number; y: number }>({ x: 0, y: 0 });

  // Auto-save effect simulation
  const handleSaveDocument = () => {
    setIsAutoSaving(true);
    setTimeout(() => {
      setIsAutoSaving(false);
      const nowStr = new Date().toLocaleTimeString();
      setLastSavedAt(nowStr);

      const newVer: VersionSnapshot = {
        id: `v-${Date.now()}`,
        versionNumber: versions.length + 1,
        savedBy: currentUserName,
        savedByRole: currentUserRole,
        savedAt: nowStr,
        docTitle: docTitle,
        content: docContent,
        diagramDataJson: JSON.stringify({ nodes, edges })
      };
      setVersions([newVer, ...versions]);
    }, 600);
  };

  const handleRestoreVersion = (ver: VersionSnapshot) => {
    setDocTitle(ver.docTitle);
    setDocContent(ver.content);
    try {
      if (ver.diagramDataJson) {
        const parsed = JSON.parse(ver.diagramDataJson);
        if (parsed.nodes) setNodes(parsed.nodes);
        if (parsed.edges) setEdges(parsed.edges);
      }
    } catch (e) {
      console.warn('Failed to parse diagram data from snapshot');
    }
    setShowVersionHistory(false);
  };

  const addDiagramNode = (type: DiagramNode['type']) => {
    const newNode: DiagramNode = {
      id: `n-${Date.now()}`,
      type: type,
      label: `New ${type.toUpperCase()}`,
      x: 100 + Math.random() * 200,
      y: 100 + Math.random() * 150,
      width: type === 'diamond' ? 130 : 140,
      height: type === 'diamond' ? 70 : 60,
      color: 'bg-cyan-500/20 border-cyan-500 text-cyan-300'
    };
    setNodes([...nodes, newNode]);
    setSelectedNodeId(newNode.id);
  };

  const deleteSelectedNode = () => {
    if (!selectedNodeId) return;
    setNodes(nodes.filter(n => n.id !== selectedNodeId));
    setEdges(edges.filter(e => e.fromId !== selectedNodeId && e.toId !== selectedNodeId));
    setSelectedNodeId(null);
  };

  const handleNodeMouseDown = (e: React.MouseEvent, nodeId: string) => {
    e.stopPropagation();
    setSelectedNodeId(nodeId);
    setIsDraggingNode(true);
    const node = nodes.find(n => n.id === nodeId);
    if (node) {
      setDragOffset({ x: e.clientX - node.x, y: e.clientY - node.y });
    }
  };

  const handleCanvasMouseMove = (e: React.MouseEvent) => {
    if (!isDraggingNode || !selectedNodeId) return;
    const newX = e.clientX - dragOffset.x;
    const newY = e.clientY - dragOffset.y;
    setNodes(nodes.map(n => n.id === selectedNodeId ? { ...n, x: Math.max(10, newX), y: Math.max(10, newY) } : n));
  };

  const handleCanvasMouseUp = () => {
    setIsDraggingNode(false);
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Top Header Bar */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-extrabold uppercase tracking-wider text-cyan-600 dark:text-cyan-400 mb-1">
              <Zap className="w-4 h-4" />
              <span>Notion & Confluence Style Collaborative Planning</span>
            </div>
            <input
              type="text"
              value={docTitle}
              onChange={(e) => setDocTitle(e.target.value)}
              className="text-xl sm:text-2xl font-black text-slate-900 dark:text-white bg-transparent border-b border-transparent hover:border-slate-300 dark:hover:border-slate-700 focus:border-cyan-500 focus:outline-none w-full transition-colors"
            />
            <p className="text-xs text-slate-500 font-medium flex items-center gap-2 mt-1">
              <span>Last saved: {lastSavedAt}</span>
              <span>•</span>
              <span className="text-emerald-500 font-bold flex items-center gap-1">
                <Check className="w-3 h-3" /> Auto-Save Active
              </span>
            </p>
          </div>

          {/* Actions & Presence */}
          <div className="flex flex-wrap items-center gap-3">
            {/* Live Presence Avatars */}
            <div className="flex items-center -space-x-2 mr-2">
              {activeUsers.map(u => (
                <div
                  key={u.id}
                  style={{ backgroundColor: u.color }}
                  className="w-8 h-8 rounded-full text-slate-950 font-black text-xs flex items-center justify-center border-2 border-white dark:border-slate-900 shadow-sm"
                  title={`${u.name} (${u.status})`}
                >
                  {u.avatar}
                </div>
              ))}
            </div>

            <button
              onClick={() => setShowVersionHistory(!showVersionHistory)}
              className="px-3.5 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-2xl transition-all flex items-center gap-1.5"
            >
              <History className="w-4 h-4 text-cyan-500" />
              <span>Versions ({versions.length})</span>
            </button>

            <button
              onClick={handleSaveDocument}
              disabled={isAutoSaving}
              className="px-4 py-2 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-2xl shadow-md transition-all flex items-center gap-1.5"
            >
              {isAutoSaving ? <Clock className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
              <span>{isAutoSaving ? 'Saving...' : 'Save Draft'}</span>
            </button>
          </div>
        </div>

        {/* Document Template Category Switcher */}
        <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none">
          {DOC_CATEGORIES.map(cat => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold whitespace-nowrap transition-all ${
                selectedCategory === cat
                  ? 'bg-cyan-500 text-slate-950 shadow-xs'
                  : 'bg-slate-100 dark:bg-slate-800/60 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      {/* Main Workspace Layout (Editor + Canvas + Sidebar) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Left Column: Rich Text Document Editor */}
        <div className="lg:col-span-7 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <h3 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <FileText className="w-4 h-4 text-cyan-500" /> Collaborative Document Editor ({selectedCategory})
            </h3>

            {/* Quick Formatting Bar */}
            <div className="flex items-center gap-1 bg-slate-100 dark:bg-slate-800/80 p-1 rounded-xl text-xs">
              <button onClick={() => setDocContent(prev => prev + '\n# New Section Title')} className="p-1.5 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-slate-700 dark:text-slate-300 font-bold" title="Add Heading">H1</button>
              <button onClick={() => setDocContent(prev => prev + '\n## Sub Heading')} className="p-1.5 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-slate-700 dark:text-slate-300 font-bold" title="Add Subheading">H2</button>
              <button onClick={() => setDocContent(prev => prev + '\n- Bullet point item')} className="p-1.5 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-slate-700 dark:text-slate-300" title="Bullet List"><List className="w-3.5 h-3.5" /></button>
              <button onClick={() => setDocContent(prev => prev + '\n- [ ] Checklist task item')} className="p-1.5 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-slate-700 dark:text-slate-300" title="Checklist"><CheckSquare className="w-3.5 h-3.5" /></button>
              <button onClick={() => setDocContent(prev => prev + '\n```javascript\n// Code Block\nconsole.log("Tech Knife System");\n```')} className="p-1.5 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-slate-700 dark:text-slate-300" title="Code Block"><Code className="w-3.5 h-3.5" /></button>
              <button onClick={() => setDocContent(prev => prev + '\n> "Enterprise architecture note or quote"')} className="p-1.5 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-slate-700 dark:text-slate-300" title="Quote"><Quote className="w-3.5 h-3.5" /></button>
              <button onClick={() => setDocContent(prev => prev + ' @Ranadhir Pal ')} className="p-1.5 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-cyan-400 font-bold" title="Mention Employee">@</button>
            </div>
          </div>

          <textarea
            value={docContent}
            onChange={(e) => setDocContent(e.target.value)}
            rows={18}
            className="w-full text-xs font-mono p-4 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 focus:outline-none focus:border-cyan-500 leading-relaxed"
            placeholder="Type markdown, headings (#), checklists (- [ ]), code blocks (```), @mentions..."
          />
        </div>

        {/* Right Column: Visual Diagram Canvas */}
        <div className="lg:col-span-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4 flex flex-col justify-between">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <h3 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <Layout className="w-4 h-4 text-cyan-500" /> Interactive Diagram Canvas
            </h3>

            <div className="flex items-center gap-1.5">
              <button onClick={() => addDiagramNode('rectangle')} className="p-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 rounded-lg text-slate-700 dark:text-slate-300" title="Add Rectangle"><Square className="w-3.5 h-3.5" /></button>
              <button onClick={() => addDiagramNode('circle')} className="p-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 rounded-lg text-slate-700 dark:text-slate-300" title="Add Circle"><Circle className="w-3.5 h-3.5" /></button>
              <button onClick={() => addDiagramNode('diamond')} className="p-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 rounded-lg text-slate-700 dark:text-slate-300" title="Add Decision Diamond"><Diamond className="w-3.5 h-3.5" /></button>
              <button onClick={() => addDiagramNode('cylinder')} className="p-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 rounded-lg text-slate-700 dark:text-slate-300" title="Add Database Cylinder"><Database className="w-3.5 h-3.5" /></button>
              {selectedNodeId && (
                <button onClick={deleteSelectedNode} className="p-1.5 bg-rose-500/10 text-rose-500 hover:bg-rose-500/20 rounded-lg" title="Delete Selected Node"><Trash2 className="w-3.5 h-3.5" /></button>
              )}
            </div>
          </div>

          {/* Canvas Draw Area */}
          <div
            onMouseMove={handleCanvasMouseMove}
            onMouseUp={handleCanvasMouseUp}
            className="w-full h-[380px] rounded-2xl bg-slate-950 border border-slate-800 relative overflow-hidden select-none"
            style={{ backgroundImage: 'radial-gradient(#334155 1px, transparent 1px)', backgroundSize: '16px 16px' }}
          >
            {/* SVG Connecting Edges */}
            <svg className="absolute inset-0 w-full h-full pointer-events-none">
              <defs>
                <marker id="arrow" viewBox="0 0 10 10" refX="5" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
                  <path d="M 0 0 L 10 5 L 0 10 z" fill="#06b6d4" />
                </marker>
              </defs>
              {edges.map(e => {
                const fromNode = nodes.find(n => n.id === e.fromId);
                const toNode = nodes.find(n => n.id === e.toId);
                if (!fromNode || !toNode) return null;
                const x1 = fromNode.x + fromNode.width / 2;
                const y1 = fromNode.y + fromNode.height / 2;
                const x2 = toNode.x + toNode.width / 2;
                const y2 = toNode.y + toNode.height / 2;
                return (
                  <g key={e.id}>
                    <line x1={x1} y1={y1} x2={x2} y2={y2} stroke="#06b6d4" strokeWidth="2" strokeDasharray="4" markerEnd="url(#arrow)" />
                    {e.label && (
                      <text x={(x1 + x2) / 2} y={(y1 + y2) / 2 - 6} fill="#94a3b8" fontSize="10" textAnchor="middle" fontWeight="bold">
                        {e.label}
                      </text>
                    )}
                  </g>
                );
              })}
            </svg>

            {/* Draggable Diagram Nodes */}
            {nodes.map(node => {
              const isSelected = selectedNodeId === node.id;
              return (
                <div
                  key={node.id}
                  onMouseDown={(e) => handleNodeMouseDown(e, node.id)}
                  style={{ left: node.x, top: node.y, width: node.width, height: node.height }}
                  className={`absolute rounded-xl border-2 p-2 flex items-center justify-center text-center text-xs font-bold cursor-move transition-shadow ${node.color} ${
                    isSelected ? 'ring-2 ring-cyan-400 shadow-lg z-10' : 'z-0'
                  }`}
                >
                  <span className="truncate">{node.label}</span>
                </div>
              );
            })}
          </div>

          <p className="text-[11px] text-slate-500 font-medium text-center">
            Drag nodes to reposition • Connects automatically • Auto-saves to document version history
          </p>
        </div>
      </div>

      {/* Version History Modal */}
      {showVersionHistory && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-lg w-full p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <History className="w-4 h-4 text-cyan-500" /> Planning Version Audit Log
              </h3>
              <button onClick={() => setShowVersionHistory(false)} className="text-slate-400 hover:text-white font-bold">✕</button>
            </div>

            <div className="space-y-3 max-h-80 overflow-y-auto">
              {versions.map(v => (
                <div key={v.id} className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 flex items-center justify-between gap-3">
                  <div>
                    <h4 className="text-xs font-extrabold text-slate-900 dark:text-white">Version #{v.versionNumber} — {v.docTitle}</h4>
                    <p className="text-[10px] text-slate-500 font-mono">Saved by {v.savedBy} ({v.savedByRole}) at {v.savedAt}</p>
                  </div>

                  <button
                    onClick={() => handleRestoreVersion(v)}
                    className="px-3 py-1.5 bg-cyan-500/10 text-cyan-400 hover:bg-cyan-500/20 font-bold text-xs rounded-xl flex items-center gap-1 border border-cyan-500/30"
                  >
                    <RotateCcw className="w-3.5 h-3.5" /> Restore
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
