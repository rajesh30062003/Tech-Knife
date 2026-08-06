import React, { useState, useEffect, useRef } from 'react';
import { 
  X, Briefcase, Info, Users, UserCheck, Calendar, Zap, GitBranch, 
  Settings, ExternalLink, Github, Globe, Shield, Clock, DollarSign, 
  Cpu, Database, Cloud, Terminal, CheckCircle2, AlertCircle, Edit3, 
  Trash2, Link2, Download, Save, Loader2, Sparkles, ShieldCheck, UserPlus, FileText,
  BarChart3, CheckSquare, Layers, AlertTriangle, ChevronRight, Plus, RefreshCw, Filter, User, Lock, Award,
  MessageSquare, Send, Paperclip, Smile, Pin, Search, PhoneCall, Video, Bot, BookOpen, Activity, Bell, Eye, CornerDownRight, Tag, CheckCheck
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { projectsApi, EnterpriseProject, ProjectActivity, ProjectLinksData } from '../../api/projects';
import { projectWorkspaceApi, ProjectTask, ProjectRisk, DriveFileRecord } from '../../api/projectWorkspaceApi';
import { apiClient } from '../../api/client';
import { StatusBadge } from '../common/StatusBadge';
import { EmployeeSelect } from '../common/EmployeeSelect';
import { InternSelect } from '../common/InternSelect';
import { UniversalReportExporter } from '../core/UniversalReportExporter';
import { UniversalFileUploader } from '../core/UniversalFileUploader';
import { AuditTrailViewer } from '../core/AuditTrailViewer';
import { EnterpriseAiWorkspace } from './ai/EnterpriseAiWorkspace';
import { EnterpriseDevOpsWorkspace } from '../devops/EnterpriseDevOpsWorkspace';
import { EnterpriseWikiWorkspace } from '../wiki/EnterpriseWikiWorkspace';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export type EnterpriseWorkspaceTab = 
  | 'overview' 
  | 'planning' 
  | 'srs_documents' 
  | 'files_media' 
  | 'conversations' 
  | 'tasks' 
  | 'kanban' 
  | 'sprint' 
  | 'timeline' 
  | 'milestones' 
  | 'meetings' 
  | 'team' 
  | 'customer' 
  | 'devops' 
  | 'risks' 
  | 'analytics' 
  | 'reports' 
  | 'audit' 
  | 'notifications' 
  | 'wiki' 
  | 'ai_workspace' 
  | 'automation' 
  | 'security' 
  | 'settings';

interface EnterpriseProjectWorkspaceProps {
  project: EnterpriseProject | null;
  isOpen: boolean;
  onClose: () => void;
  onProjectUpdated?: () => void;
  initialTab?: EnterpriseWorkspaceTab;
}

interface ChatMessage {
  id: string;
  senderId: string;
  senderName: string;
  senderRole: string;
  avatarUrl?: string;
  content: string;
  sentAt: string;
  isRead?: boolean;
  isPinned?: boolean;
  attachments?: { name: string; type: string; url: string }[];
  reactions?: { emoji: string; count: number; userIds: string[] }[];
}

export const EnterpriseProjectWorkspace: React.FC<EnterpriseProjectWorkspaceProps> = ({
  project,
  isOpen,
  onClose,
  onProjectUpdated,
  initialTab = 'overview',
}) => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<EnterpriseWorkspaceTab>(initialTab);
  const [isSaving, setIsSaving] = useState(false);
  const [saveMessage, setSaveMessage] = useState<string | null>(null);

  // RBAC Permission Helpers
  const userRoles = user?.roles || [];
  const isAdmin = userRoles.some(r => ['ROLE_ADMIN', 'ADMIN', 'ROLE_SUPER_ADMIN'].includes(r));
  const isManager = userRoles.some(r => ['ROLE_MANAGER', 'MANAGER'].includes(r));
  const isLead = userRoles.some(r => ['ROLE_LEAD', 'LEAD'].includes(r));
  const canManage = isAdmin || isManager || isLead;

  // Conversations State
  const [chatMessages, setChatMessages] = useState<ChatMessage[]>([]);
  const [chatInput, setChatInput] = useState('');
  const [memberSearch, setMemberSearch] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const chatBottomRef = useRef<HTMLDivElement>(null);

  // Load Historical Persisted Messages from Backend
  useEffect(() => {
    if (!project) return;
    const projectCode = project.projectCode || project.projectId || project.id;
    
    const loadProjectHistory = async () => {
      try {
        const res = await apiClient.get(`/messages/project/${projectCode}`);
        if (res?.data?.success && Array.isArray(res.data.data)) {
          const historicalMsgs: ChatMessage[] = res.data.data.map((m: any) => ({
            id: m.id,
            senderId: m.senderId || 'u-remote',
            senderName: m.senderName || 'Team Member',
            senderRole: 'Contributor',
            content: m.content,
            sentAt: m.sentAt ? new Date(m.sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            isRead: true,
          }));
          setChatMessages(historicalMsgs);
        }
      } catch (err) {
        console.error('Failed to load project chat history:', err);
      }
    };

    loadProjectHistory();
  }, [project]);

  // Real-Time STOMP SockJS Listener
  useEffect(() => {
    if (!project) return;
    const projectCode = project.projectCode || project.projectId || project.id;

    console.log("STEP 1 - Creating STOMP client");
    const stompClient = new Client({
      webSocketFactory: () => new SockJS('/ws-chat'),
      reconnectDelay: 5000,
      debug: (str) => console.log("[STOMP]", str),
      onConnect: () => {
        console.log("STEP 3 - STOMP Connected");
        console.log("STEP 4 - Subscribing to", `/topic/project.${projectCode}`);
        const subscription = stompClient.subscribe(`/topic/project.${projectCode}`, (message) => {
          try {
            console.log("MESSAGE CALLBACK FIRED");
            console.log("RAW MESSAGE", message);
            console.log("MESSAGE BODY", message.body);
            console.log("BEFORE JSON PARSE");
            const data = JSON.parse(message.body);
            console.log("PARSED DTO", data);
            if (data.content && data.id) {
              console.log("UPDATING CHAT STATE");
              setChatMessages(prev => {
                if (prev.some(m => m.id === data.id)) return prev;
                const filtered = prev.filter(m => !(m.id.startsWith('temp-') && m.content === data.content));
                return [...filtered, {
                  id: data.id,
                  senderId: data.senderId || 'u-remote',
                  senderName: data.senderName || 'Team Member',
                  senderRole: data.senderRole || 'Engineer',
                  content: data.content,
                  sentAt: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                  isRead: true,
                }];
              });
              console.log("CHAT STATE UPDATED");
            } else if (data.eventType === 'DOCUMENT_UPLOADED') {
              // Note: fetchWorkspaceDetails() would be implemented in parent or via context
            }
          } catch (e) {
            console.error("MESSAGE PROCESSING ERROR", e);
          }
        });
        console.log("SUBSCRIPTION ACTIVE", subscription.id);
      },
      onWebSocketError: (error) => {
        console.error("WS ERROR", error);
      },
      onStompError: (frame) => {
        console.error("STOMP ERROR", frame);
      },
      onWebSocketClose: (event) => {
        console.error("WS CLOSED", event);
      },
      onDisconnect: () => {
        console.log("STEP 6 - Disconnected");
      },
    });

    console.log("STEP 2 - Activating client");
    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [project]);

  useEffect(() => {
    chatBottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [chatMessages]);

  // Editable Form States
  const [managerId, setManagerId] = useState<string>('');
  const [leadId, setLeadId] = useState<string>('');
  const [assignedEmployees, setAssignedEmployees] = useState<string[]>([]);
  const [assignedInterns, setAssignedInterns] = useState<string[]>([]);
  const [settingsStatus, setSettingsStatus] = useState<string>('PLANNED');
  const [settingsPriority, setSettingsPriority] = useState<string>('MEDIUM');
  const [settingsVisibility, setSettingsVisibility] = useState<string>('PRIVATE');

  useEffect(() => {
    if (project) {
      setManagerId(project.projectManagerId || '');
      setLeadId(project.projectLeadId || '');
      setAssignedEmployees(project.assignedEmployees || []);
      setAssignedInterns(project.assignedInterns || []);
      setSettingsStatus(project.status || 'PLANNED');
      setSettingsPriority(project.priority || 'MEDIUM');
      setSettingsVisibility(project.projectVisibility || 'PRIVATE');
    }
  }, [project]);

  if (!isOpen || !project) return null;

  const projectCode = project.projectCode || project.projectId || project.id;
  const projectName = project.projectName || project.shortName || 'Enterprise Deliverable';
  const progress = project.overallProgressPercentage ?? project.progressPercentage ?? 68;

  // Handlers
  const handleSendChatMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    const content = chatInput.trim();
    if (!content || !project) return;

    const currentUserId = user?.id || 'u-curr';
    const currentUserName = user ? `${user.firstName} ${user.lastName}` : 'You';
    const projectCode = project.projectCode || project.projectId || project.id;
    const tempId = `temp-${Date.now()}`;

    // 1. Optimistic UI update
    const optimisticMsg: ChatMessage = {
      id: tempId,
      senderId: currentUserId,
      senderName: currentUserName,
      senderRole: isManager ? 'Project Manager' : isLead ? 'Technical Lead' : 'Engineering Team',
      content,
      sentAt: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      isRead: true,
    };

    setChatMessages(prev => [...prev, optimisticMsg]);
    setChatInput('');

    try {
      // 2. Send POST /messages/send using authenticated apiClient
      await apiClient.post('/messages/send', {
        subject: projectCode,
        content,
        attachments: [],
      }, {
        params: {
          senderId: currentUserId,
          senderName: currentUserName,
        }
      });
      // Do NOT append manually. STOMP MESSAGE event updates chatMessages with real InternalMessageDTO id.
    } catch (err) {
      // Rollback optimistic message if POST fails
      setChatMessages(prev => prev.filter(m => m.id !== tempId));
      console.error('Failed to send chat message:', err);
    }
  };

  const handleSaveTeam = async () => {
    if (!project.id) return;
    setIsSaving(true);
    try {
      await projectsApi.assignMembers(project.id, { projectManagerId: managerId, projectLeadId: leadId, assignedEmployees, assignedInterns });
      setSaveMessage('Team allocations saved successfully!');
      if (onProjectUpdated) onProjectUpdated();
      setTimeout(() => setSaveMessage(null), 3000);
    } catch (err) {
      alert('Failed to save team allocation.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleSaveSettings = async () => {
    if (!project.id) return;
    setIsSaving(true);
    try {
      await projectsApi.updateStatus(project.id, settingsStatus, 'Status updated via workspace');
      await projectsApi.update(project.id, { priority: settingsPriority, projectVisibility: settingsVisibility });
      setSaveMessage('Project settings updated!');
      if (onProjectUpdated) onProjectUpdated();
      setTimeout(() => setSaveMessage(null), 3000);
    } catch (err) {
      alert('Failed to update settings.');
    } finally {
      setIsSaving(false);
    }
  };

  // Nav Items Definition
  const navItems = [
    { id: 'overview', label: 'Overview', icon: Briefcase },
    { id: 'planning', label: 'Planning', icon: Zap },
    { id: 'srs_documents', label: 'SRS & Documents', icon: FileText },
    { id: 'files_media', label: 'Files & Media', icon: Paperclip },
    { id: 'conversations', label: 'Conversations & Chat', icon: MessageSquare, badge: 'LIVE' },
    { id: 'tasks', label: 'Tasks', icon: CheckSquare },
    { id: 'kanban', label: 'Kanban Board', icon: Layers },
    { id: 'sprint', label: 'Sprint Management', icon: Clock },
    { id: 'timeline', label: 'Timeline & Milestones', icon: Calendar },
    { id: 'meetings', label: 'Meetings', icon: Video },
    { id: 'team', label: 'Team & Capacity', icon: Users },
    { id: 'customer', label: 'Customer Approvals', icon: Award },
    { id: 'devops', label: 'Repository & DevOps', icon: GitBranch },
    { id: 'risks', label: 'Risks & Governance', icon: AlertTriangle },
    { id: 'analytics', label: 'Productivity Analytics', icon: BarChart3 },
    { id: 'reports', label: 'Executive Reports', icon: Download },
    { id: 'audit', label: 'Audit Trail Stream', icon: Shield },
    { id: 'notifications', label: 'Notifications', icon: Bell },
    { id: 'wiki', label: 'Knowledge Base & Wiki', icon: BookOpen },
    { id: 'ai_workspace', label: 'AI Copilot Assistant', icon: Bot },
    { id: 'automation', label: 'Automation Rules', icon: Cpu },
    { id: 'security', label: 'Security & SSO', icon: ShieldCheck },
    { id: 'settings', label: 'Project Settings', icon: Settings },
  ];

  const currentUserId = user?.id || 'u-curr';

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/85 backdrop-blur-md flex items-center justify-center p-2 sm:p-4 overflow-hidden select-none animate-in fade-in duration-200">
      
      {/* Full-Screen Central Workspace Container */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-7xl w-full h-[96vh] flex flex-col shadow-2xl overflow-hidden">
        
        {/* Workspace Top Header Bar */}
        <div className="p-4 sm:p-5 bg-gradient-to-r from-slate-950 via-slate-900 to-slate-950 text-white border-b border-slate-800 shrink-0 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3 min-w-0">
            <span className="px-3 py-1 bg-cyan-500/20 text-cyan-400 font-mono text-xs font-bold rounded-full border border-cyan-500/30">
              {projectCode}
            </span>
            <StatusBadge status={project.status || 'PLANNED'} />
            <h1 className="text-lg sm:text-xl font-extrabold text-white tracking-tight truncate">
              {projectName}
            </h1>
            <span className="text-xs text-slate-400 font-medium hidden sm:inline truncate">
              • {project.department || 'Engineering'} ({project.client || project.clientOrganization || 'Apex Logistics'})
            </span>
          </div>

          <div className="flex items-center gap-3 shrink-0">
            <div className="hidden lg:flex items-center gap-2 px-3 py-1 bg-slate-800/80 rounded-xl border border-slate-700/60 text-xs font-mono">
              <span className="text-slate-400">Budget:</span>
              <span className="text-emerald-400 font-bold">${(project.budget || 85000).toLocaleString()}</span>
              <span className="text-slate-400 ml-2">Progress:</span>
              <span className="text-cyan-400 font-bold">{progress}%</span>
            </div>

            <button
              onClick={onClose}
              className="p-2 text-slate-400 hover:text-white hover:bg-slate-800 rounded-2xl transition-colors"
              title="Close Workspace"
            >
              <X className="w-6 h-6" />
            </button>
          </div>
        </div>

        {/* Global Save Alert Banner */}
        {saveMessage && (
          <div className="px-6 py-2 bg-emerald-500/10 border-b border-emerald-500/20 text-xs text-emerald-400 font-bold flex items-center gap-2 shrink-0">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" /> {saveMessage}
          </div>
        )}

        {/* Main Workspace Layout */}
        <div className="flex-1 flex overflow-hidden bg-slate-50/50 dark:bg-slate-950/50">
          
          {/* Left Navigation Sidebar */}
          <div className="w-64 bg-slate-900 border-r border-slate-800 overflow-y-auto p-3 space-y-1 shrink-0 no-scrollbar hidden md:block">
            <span className="text-[10px] uppercase font-bold text-slate-500 px-3 tracking-wider block mb-2">Workspace Modules</span>
            {navItems.map((item) => (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id as EnterpriseWorkspaceTab)}
                className={`w-full px-3.5 py-2.5 rounded-2xl text-xs font-extrabold transition-all flex items-center justify-between gap-2.5 ${
                  activeTab === item.id
                    ? 'bg-cyan-500 text-slate-950 shadow-md font-black'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                }`}
              >
                <div className="flex items-center gap-2.5 min-w-0">
                  <item.icon className="w-4 h-4 shrink-0" />
                  <span className="truncate">{item.label}</span>
                </div>
                {item.badge && (
                  <span className="px-1.5 py-0.5 bg-cyan-400/20 text-cyan-300 font-mono text-[9px] font-black rounded-md">
                    {item.badge}
                  </span>
                )}
              </button>
            ))}
          </div>

          {/* Center Body Workspace Content Panel */}
          <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6">
            
            {/* OVERVIEW TAB */}
            {activeTab === 'overview' && (
              <div className="space-y-6">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                  <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-2">
                    <span className="text-[10px] uppercase font-bold text-slate-400 flex items-center gap-1"><Sparkles className="w-3.5 h-3.5 text-cyan-500" /> Overall Progress</span>
                    <div className="flex items-baseline justify-between"><span className="text-2xl font-extrabold text-cyan-600 font-mono">{progress}%</span></div>
                    <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden"><div className="h-full bg-cyan-500 rounded-full" style={{ width: `${progress}%` }} /></div>
                  </div>

                  <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-2">
                    <span className="text-[10px] uppercase font-bold text-slate-400 flex items-center gap-1"><DollarSign className="w-3.5 h-3.5 text-emerald-500" /> Budget Valuation</span>
                    <div className="flex items-baseline justify-between"><span className="text-xl font-extrabold text-emerald-600 font-mono">${(project.budget || 85000).toLocaleString()}</span><span className="text-xs text-slate-400">USD</span></div>
                    <p className="text-xs text-slate-500 font-medium">Cap Limit</p>
                  </div>

                  <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-2">
                    <span className="text-[10px] uppercase font-bold text-slate-400 flex items-center gap-1"><Clock className="w-3.5 h-3.5 text-indigo-500" /> Estimated Duration</span>
                    <div className="flex items-baseline justify-between"><span className="text-xl font-extrabold text-indigo-600 font-mono">{project.estimatedHours || 480} Hrs</span></div>
                    <p className="text-xs text-slate-500 font-medium">12 Weeks Target</p>
                  </div>

                  <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-2">
                    <span className="text-[10px] uppercase font-bold text-slate-400 flex items-center gap-1"><Users className="w-3.5 h-3.5 text-amber-500" /> Team Members</span>
                    <div className="flex items-baseline justify-between"><span className="text-xl font-extrabold text-slate-900 dark:text-white font-mono">{(project.assignedEmployees?.length || 0) + (project.assignedInterns?.length || 0)} Roster</span></div>
                    <p className="text-xs text-slate-500 font-medium">Engineers & Interns</p>
                  </div>
                </div>

                <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-3">
                  <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2"><ShieldCheck className="w-4 h-4 text-cyan-500" /> Project Description</h3>
                  <p className="text-xs text-slate-700 dark:text-slate-300 leading-relaxed font-medium">{project.description || 'Enterprise deliverable persistent in MongoDB Atlas with Spring Boot 3.5 microservices.'}</p>
                </div>
              </div>
            )}

            {/* MANDATORY WHATSAPP-STYLE 3-COLUMN PROJECT MEMBER CONVERSATIONS */}
            {activeTab === 'conversations' && (
              <div className="h-[80vh] grid grid-cols-1 lg:grid-cols-4 gap-4 overflow-hidden">
                
                {/* COLUMN 1: PROJECT MEMBER ROSTER */}
                <div className="p-4 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex flex-col space-y-3 overflow-y-auto">
                  <div className="space-y-2">
                    <h3 className="text-xs font-extrabold text-slate-900 dark:text-white flex items-center justify-between">
                      <span>Project Members</span>
                      <span className="px-2 py-0.5 bg-emerald-500/10 text-emerald-600 font-mono text-[10px] rounded-md font-bold">5 Online</span>
                    </h3>
                    <div className="relative">
                      <Search className="w-3.5 h-3.5 text-slate-400 absolute left-2.5 top-2.5" />
                      <input
                        type="text"
                        value={memberSearch}
                        onChange={(e) => setMemberSearch(e.target.value)}
                        placeholder="Search roster..."
                        className="w-full text-xs pl-8 pr-2 py-2 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 font-medium"
                      />
                    </div>
                  </div>

                  <div className="space-y-3 pt-1 text-xs">
                    {/* PM */}
                    <div className="p-2.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 space-y-1">
                      <span className="text-[9px] font-bold text-slate-400 uppercase">Project Manager</span>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2 min-w-0">
                          <span className="w-2 h-2 rounded-full bg-emerald-500" />
                          <span className="font-extrabold truncate text-slate-900 dark:text-white">{project.projectManagerName || 'Vikramaditya Sharma'}</span>
                        </div>
                        <span className="px-1.5 py-0.5 bg-cyan-500/10 text-cyan-600 font-mono text-[9px] font-bold rounded">P1</span>
                      </div>
                    </div>

                    {/* Lead */}
                    <div className="p-2.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 space-y-1">
                      <span className="text-[9px] font-bold text-slate-400 uppercase">Technical Lead</span>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2 min-w-0">
                          <span className="w-2 h-2 rounded-full bg-emerald-500" />
                          <span className="font-extrabold truncate text-slate-900 dark:text-white">{project.projectLeadName || 'Ranadhir Pal'}</span>
                        </div>
                      </div>
                    </div>

                    {/* Customer */}
                    <div className="p-2.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 space-y-1">
                      <span className="text-[9px] font-bold text-slate-400 uppercase">Customer VP</span>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2 min-w-0">
                          <span className="w-2 h-2 rounded-full bg-amber-500" />
                          <span className="font-extrabold truncate text-slate-900 dark:text-white">{project.customerRepresentative || 'Marcus Vance'}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                {/* COLUMN 2: WHATSAPP-STYLE CENTER CHAT STREAM */}
                <div className="lg:col-span-2 p-4 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex flex-col justify-between overflow-hidden">
                  
                  {/* Header */}
                  <div className="pb-3 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
                    <div>
                      <h3 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                        <MessageSquare className="w-4 h-4 text-cyan-500" /> Real-Time Channel ({projectCode})
                      </h3>
                      <p className="text-[11px] text-slate-500 font-medium">STOMP SockJS `/ws-chat` • WebSocket Synced</p>
                    </div>

                    <div className="flex items-center gap-1 text-xs">
                      <button className="p-2 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl text-slate-400 hover:text-slate-900"><PhoneCall className="w-4 h-4" /></button>
                      <button className="p-2 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl text-slate-400 hover:text-slate-900"><Video className="w-4 h-4" /></button>
                    </div>
                  </div>

                  {/* Messages Stream (WhatsApp Style Alignment) */}
                  <div className="flex-1 overflow-y-auto p-3 space-y-3">
                    <div className="text-center my-2"><span className="px-3 py-1 bg-slate-100 dark:bg-slate-800 text-slate-400 font-mono text-[10px] font-bold rounded-full">Today</span></div>

                    {chatMessages.map((msg) => {
                      const isMe = msg.senderId === currentUserId || msg.senderName === 'You';
                      return (
                        <div key={msg.id} className={`flex items-end gap-2 ${isMe ? 'justify-end' : 'justify-start'}`}>
                          {!isMe && (
                            <div className="w-7 h-7 rounded-full bg-slate-800 text-cyan-400 font-extrabold text-xs flex items-center justify-center shrink-0">
                              {msg.senderName.charAt(0)}
                            </div>
                          )}

                          <div className={`space-y-1 max-w-md ${isMe ? 'items-end' : 'items-start'}`}>
                            {!isMe && (
                              <div className="flex items-center gap-2 text-[10px]">
                                <span className="font-extrabold text-slate-900 dark:text-white">{msg.senderName}</span>
                                <span className="text-slate-400 font-mono">({msg.senderRole})</span>
                              </div>
                            )}

                            <div className={`p-3 rounded-2xl text-xs font-medium leading-relaxed shadow-xs ${
                              isMe 
                                ? 'bg-cyan-500 text-slate-950 font-bold rounded-br-none' 
                                : 'bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 rounded-bl-none border border-slate-200/60 dark:border-slate-700/60'
                            }`}>
                              {msg.content}

                              <div className={`flex items-center justify-end gap-1 mt-1 text-[9px] ${isMe ? 'text-slate-800 font-mono' : 'text-slate-400'}`}>
                                <span>{msg.sentAt}</span>
                                {isMe && <CheckCheck className="w-3.5 h-3.5 text-slate-900" />}
                              </div>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                    <div ref={chatBottomRef} />
                  </div>

                  {/* Input Form */}
                  <form onSubmit={handleSendChatMessage} className="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center gap-2">
                    <button type="button" className="p-2 text-slate-400 hover:text-slate-600"><Paperclip className="w-4 h-4" /></button>
                    <input
                      type="text"
                      value={chatInput}
                      onChange={(e) => setChatInput(e.target.value)}
                      placeholder="Type a message or @mention a team member..."
                      className="flex-1 text-xs p-2.5 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 font-medium text-slate-900 dark:text-slate-100"
                    />
                    <button type="submit" className="px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-2xl shadow-md transition-all flex items-center gap-1">
                      <Send className="w-3.5 h-3.5" />
                    </button>
                  </form>
                </div>

                {/* COLUMN 3: RIGHT CHANNEL METADATA */}
                <div className="p-4 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex flex-col space-y-4 overflow-y-auto">
                  <h3 className="text-xs font-extrabold text-slate-900 dark:text-white uppercase tracking-wider">Channel Specs</h3>
                  
                  <div className="p-3 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 space-y-2 text-xs">
                    <span className="text-[10px] text-slate-400 font-bold uppercase block">Pinned Updates</span>
                    <p className="text-[11px] text-slate-600 dark:text-slate-300 font-medium">"Google Drive OAuth 2.0 offline refresh flow is verified."</p>
                  </div>

                  <div className="p-3 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 space-y-2 text-xs">
                    <span className="text-[10px] text-slate-400 font-bold uppercase block">Shared Storage Files</span>
                    <div className="space-y-1">
                      <div className="flex items-center gap-2 font-mono text-[11px] text-cyan-600 dark:text-cyan-400 font-bold">
                        <Paperclip className="w-3 h-3" /> Architecture_Spec_v1.pdf
                      </div>
                    </div>
                  </div>
                </div>

              </div>
            )}

            {/* SRS & DOCUMENTS TAB */}
            {activeTab === 'srs_documents' && (
              <div className="space-y-6">
                <UniversalFileUploader defaultCategory="DOCUMENT" />
              </div>
            )}

            {/* TEAM & CAPACITY TAB */}
            {activeTab === 'team' && (
              <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-6">
                <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
                  <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2"><Users className="w-4 h-4 text-emerald-500" /> Roster Allocation</h3>
                  {canManage && <button onClick={handleSaveTeam} disabled={isSaving} className="px-5 py-2 bg-indigo-600 text-white font-extrabold text-xs rounded-xl shadow-md">Save Allocations</button>}
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-2"><label className="text-xs font-bold block">Project Manager</label><EmployeeSelect value={managerId} onChange={(val) => setManagerId(val as string)} multiple={false} /></div>
                  <div className="space-y-2"><label className="text-xs font-bold block">Technical Lead</label><EmployeeSelect value={leadId} onChange={(val) => setLeadId(val as string)} multiple={false} /></div>
                </div>
                <div className="space-y-2"><label className="text-xs font-bold block">Engineers ({assignedEmployees.length})</label><EmployeeSelect value={assignedEmployees} onChange={(val) => setAssignedEmployees(val as string[])} multiple={true} /></div>
                <div className="space-y-2"><label className="text-xs font-bold block">Interns ({assignedInterns.length})</label><InternSelect value={assignedInterns} onChange={(val) => setAssignedInterns(val)} multiple={true} /></div>
              </div>
            )}

            {/* REPORTS & AUDIT TAB */}
            {activeTab === 'reports' && <UniversalReportExporter defaultModule="Projects & Deliverables" />}
            {activeTab === 'audit' && <AuditTrailViewer entityType="Project" entityId={projectCode} />}

            {/* REPOSITORY & DEVOPS TAB */}
            {activeTab === 'devops' && <EnterpriseDevOpsWorkspace />}

            {/* KNOWLEDGE BASE & WIKI TAB */}
            {activeTab === 'wiki' && <EnterpriseWikiWorkspace />}

            {/* AI WORKSPACE TAB */}
            {activeTab === 'ai_workspace' && <EnterpriseAiWorkspace project={project} />}

            {/* SECURITY & SSO TAB */}
            {activeTab === 'security' && <EnterpriseSecuritySuite />}

            {/* SETTINGS TAB */}
            {activeTab === 'settings' && (
              <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-6">
                <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
                  <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Project Settings</h3>
                  {canManage && <button onClick={handleSaveSettings} disabled={isSaving} className="px-5 py-2 bg-indigo-600 text-white font-extrabold text-xs rounded-xl shadow-md">Save Settings</button>}
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
                  <div>
                    <label className="font-bold block mb-1">Status Lifecycle</label>
                    <select value={settingsStatus} onChange={(e) => setSettingsStatus(e.target.value)} className="w-full text-xs font-bold p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100">
                      <option value="PLANNED">PLANNED</option>
                      <option value="IN_PROGRESS">IN PROGRESS</option>
                      <option value="COMPLETED">COMPLETED</option>
                    </select>
                  </div>
                </div>
              </div>
            )}

          </div>
        </div>

        {/* Workspace Footer Bar */}
        <div className="p-4 bg-white dark:bg-slate-900 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between gap-4 shrink-0 text-xs font-medium text-slate-500">
          <div className="flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <span>Enterprise Workspace Operating System Active</span>
          </div>

          <button onClick={onClose} className="px-4 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-extrabold rounded-xl transition-all">
            Close Workspace
          </button>
        </div>

      </div>
    </div>
  );
};
