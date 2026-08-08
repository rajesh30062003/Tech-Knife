import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { 
  X, Briefcase, Info, Users, UserCheck, Calendar, Zap, GitBranch, 
  Settings, ExternalLink, Github, Globe, Shield, Clock, DollarSign, 
  Cpu, Database, Cloud, Terminal, CheckCircle2, AlertCircle, Edit3, 
  Trash2, Link2, Download, Save, Loader2, Sparkles, ShieldCheck, UserPlus, FileText,
  BarChart3, CheckSquare, Layers, AlertTriangle, ChevronRight, Plus, RefreshCw, Filter, User, Lock, Award,
  MessageSquare, Send, Paperclip, Smile, Pin, Search, PhoneCall, Video, Bot, BookOpen, Activity, Bell, Eye, CornerDownRight, Tag, CheckCheck,
  PanelLeftClose, PanelLeftOpen, Menu, ChevronLeft
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
import { EnterpriseSecuritySuite } from '../security/EnterpriseSecuritySuite';
import { EnterpriseProjectPlanningWorkspace } from './planning/EnterpriseProjectPlanningWorkspace';
import { EnterpriseTaskManagementWorkspace } from './tasks/EnterpriseTaskManagementWorkspace';
import { ProjectDocumentsTab } from './tabs/ProjectDocumentsTab';
import { ProjectOverviewTab } from './tabs/ProjectOverviewTab';
import { ProjectSprintTab } from './tabs/ProjectSprintTab';
import { ProjectMilestonesTab } from './tabs/ProjectMilestonesTab';
import { ProjectMeetingsTab } from './tabs/ProjectMeetingsTab';
import { ProjectRisksTab } from './tabs/ProjectRisksTab';
import { ProjectAnalyticsTab } from './tabs/ProjectAnalyticsTab';
import { ProjectNotificationsTab } from './tabs/ProjectNotificationsTab';
import { ProjectExecutiveReportTab } from './tabs/ProjectExecutiveReportTab';
import { 
  PROJECT_STATUS_LIST, 
  getStatusProgress, 
  normalizeProjectStatus, 
  canApproveProjectStatus, 
  getStatusLabel 
} from '../../constants/projectStatus';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export type EnterpriseWorkspaceTab = 
  | 'overview' 
  | 'planning' 
  | 'srs_documents' 
  | 'conversations' 
  | 'tasks' 
  | 'kanban' 
  | 'sprint' 
  | 'timeline' 
  | 'meetings' 
  | 'devops' 
  | 'risks' 
  | 'analytics' 
  | 'reports' 
  | 'audit' 
  | 'notifications' 
  | 'wiki' 
  | 'ai_workspace' 
  | 'security' 
  | 'settings';

interface EnterpriseProjectWorkspaceProps {
  project: EnterpriseProject | null;
  isOpen: boolean;
  onClose: () => void;
  onProjectUpdated?: (updatedProject?: EnterpriseProject) => void;
  initialTab?: EnterpriseWorkspaceTab;
}

interface ChatAttachmentItem {
  id?: string;
  driveFileId?: string;
  fileName?: string;
  mimeType?: string;
  fileSize?: number;
  previewUrl?: string;
  downloadUrl?: string;
  thumbnailUrl?: string;
  uploadedBy?: string;
  uploadedAt?: string;
  // Legacy fields fallback
  name?: string;
  type?: string;
  url?: string;
  fileUrl?: string;
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
  attachments?: ChatAttachmentItem[];
  reactions?: { emoji: string; count: number; userIds: string[] }[];
}

const formatBytes = (bytes?: number) => {
  if (!bytes || bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
};

const AttachmentCard: React.FC<{
  attachment: ChatAttachmentItem;
  onPreviewPdf: (url: string, title: string) => void;
  onPreviewImage: (url: string) => void;
}> = ({ attachment, onPreviewPdf, onPreviewImage }) => {
  const fileName = attachment.fileName || attachment.name || 'Attachment';
  const mimeType = (attachment.mimeType || attachment.type || '').toLowerCase();
  const fileId = attachment.driveFileId || attachment.id;
  const rawPreviewUrl = attachment.previewUrl || attachment.fileUrl || attachment.url || (fileId ? `/api/v1/drive/preview/${fileId}` : '#');
  const rawDownloadUrl = attachment.downloadUrl || attachment.fileUrl || attachment.url || (fileId ? `/api/v1/drive/download/${fileId}` : rawPreviewUrl);
  const fileSize = attachment.fileSize;

  const handleBinaryDownload = async () => {
    console.log('[ATTACHMENT DOWNLOAD TRIGGERED]', { fileName, fileId, rawDownloadUrl });
    try {
      const res = await fetch(rawDownloadUrl);
      console.log('[ATTACHMENT DOWNLOAD RESPONSE]', { status: res.status, contentType: res.headers.get('content-type'), contentLength: res.headers.get('content-length') });
      const blob = await res.blob();
      const blobUrl = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = blobUrl;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(blobUrl);
    } catch (err) {
      console.error('[ATTACHMENT DOWNLOAD ERROR]', err);
      const a = document.createElement('a');
      a.href = rawDownloadUrl;
      a.download = fileName;
      a.target = '_blank';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    }
  };

  const handlePreviewClick = () => {
    console.log('[ATTACHMENT PREVIEW TRIGGERED]', { fileName, fileId, rawPreviewUrl });
    onPreviewPdf(rawPreviewUrl, fileName);
  };

  const isPdf = mimeType.includes('pdf') || fileName.toLowerCase().endsWith('.pdf');
  const isImage = mimeType.startsWith('image/') || /\.(png|jpg|jpeg|gif|webp|svg)$/i.test(fileName);
  const isVideo = mimeType.startsWith('video/') || /\.(mp4|webm|mov|mkv)$/i.test(fileName);
  const isAudio = mimeType.startsWith('audio/') || /\.(mp3|wav|ogg|m4a)$/i.test(fileName);

  if (isPdf) {
    return (
      <div className="p-3 rounded-2xl bg-slate-900/20 dark:bg-slate-950/60 border border-slate-700/30 flex items-center justify-between gap-3 text-xs">
        <div className="flex items-center gap-2.5 min-w-0">
          <div className="w-9 h-9 rounded-xl bg-rose-500/20 text-rose-400 font-black flex items-center justify-center text-[10px] shrink-0 border border-rose-500/30">
            PDF
          </div>
          <div className="min-w-0">
            <h5 className="font-bold text-slate-900 dark:text-slate-100 truncate text-[11px] font-mono">{fileName}</h5>
            <span className="text-[10px] text-slate-400">{formatBytes(fileSize)} • Document</span>
          </div>
        </div>
        <div className="flex items-center gap-1.5 shrink-0">
          <button
            type="button"
            onClick={handlePreviewClick}
            className="px-2.5 py-1 bg-slate-800 hover:bg-slate-700 text-cyan-300 font-extrabold rounded-lg text-[10px] inline-flex items-center gap-1 border border-cyan-500/30 transition-colors"
          >
            <Eye className="w-3 h-3" /> Preview
          </button>
          <button
            type="button"
            onClick={handleBinaryDownload}
            className="p-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-lg text-[10px] transition-colors"
            title="Download PDF"
          >
            <Download className="w-3 h-3" />
          </button>
        </div>
      </div>
    );
  }

  if (isImage) {
    return (
      <div className="space-y-1.5 p-2 rounded-2xl bg-slate-900/20 dark:bg-slate-950/60 border border-slate-700/30">
        <img
          src={rawPreviewUrl}
          alt={fileName}
          onClick={() => onPreviewImage(rawPreviewUrl)}
          className="max-h-48 rounded-xl object-cover w-full cursor-pointer hover:opacity-95 transition-opacity"
        />
        <div className="flex items-center justify-between px-1 text-[10px] text-slate-400">
          <span className="truncate font-mono font-medium">{fileName} ({formatBytes(fileSize)})</span>
          <button type="button" onClick={handleBinaryDownload} className="p-1 hover:text-cyan-300">
            <Download className="w-3 h-3" />
          </button>
        </div>
      </div>
    );
  }

  if (isVideo) {
    return (
      <div className="space-y-1.5 p-2 rounded-2xl bg-slate-900/20 dark:bg-slate-950/60 border border-slate-700/30">
        <video src={rawPreviewUrl} controls className="max-h-56 rounded-xl w-full bg-black" />
        <div className="flex items-center justify-between px-1 text-[10px] text-slate-400">
          <span className="truncate font-mono font-medium">{fileName} ({formatBytes(fileSize)})</span>
          <button type="button" onClick={handleBinaryDownload} className="p-1 hover:text-cyan-300">
            <Download className="w-3 h-3" />
          </button>
        </div>
      </div>
    );
  }

  if (isAudio) {
    return (
      <div className="p-3 rounded-2xl bg-slate-900/20 dark:bg-slate-950/60 border border-slate-700/30 space-y-1">
        <div className="flex items-center justify-between text-xs">
          <span className="font-bold text-slate-900 dark:text-slate-100 truncate text-[11px] font-mono">{fileName}</span>
          <button type="button" onClick={handleBinaryDownload} className="p-1 hover:text-cyan-300">
            <Download className="w-3 h-3" />
          </button>
        </div>
        <audio src={rawPreviewUrl} controls className="w-full h-8" />
      </div>
    );
  }

  return (
    <div className="p-3 rounded-2xl bg-slate-900/20 dark:bg-slate-950/60 border border-slate-700/30 flex items-center justify-between gap-3 text-xs">
      <div className="flex items-center gap-2.5 min-w-0">
        <div className="w-9 h-9 rounded-xl bg-cyan-500/20 text-cyan-400 font-black flex items-center justify-center text-[10px] shrink-0 border border-cyan-500/30 uppercase">
          {fileName.split('.').pop() || 'FILE'}
        </div>
        <div className="min-w-0">
          <h5 className="font-bold text-slate-900 dark:text-slate-100 truncate text-[11px] font-mono">{fileName}</h5>
          <span className="text-[10px] text-slate-400">{formatBytes(fileSize)}</span>
        </div>
      </div>
      <div className="flex items-center gap-1.5 shrink-0">
        {rawPreviewUrl && rawPreviewUrl !== '#' && (
          <button
            type="button"
            onClick={() => onPreviewPdf(rawPreviewUrl, fileName)}
            className="px-2.5 py-1 bg-slate-800 hover:bg-slate-700 text-cyan-300 font-extrabold rounded-lg text-[10px] inline-flex items-center gap-1"
          >
            <Eye className="w-3 h-3" /> View
          </button>
        )}
        <button
          type="button"
          onClick={handleBinaryDownload}
          className="p-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-lg text-[10px]"
          title="Download File"
        >
          <Download className="w-3 h-3" />
        </button>
      </div>
    </div>
  );
};

export const EnterpriseProjectWorkspace: React.FC<EnterpriseProjectWorkspaceProps> = ({
  project: initialProject,
  isOpen,
  onClose,
  onProjectUpdated,
  initialTab = 'overview',
}) => {
  const { user } = useAuth();
  const [currentProject, setCurrentProject] = useState<EnterpriseProject | null>(initialProject);

  useEffect(() => {
    setCurrentProject(initialProject);
  }, [initialProject]);

  const handleChildProjectUpdated = (updatedProject?: EnterpriseProject) => {
    if (updatedProject) {
      setCurrentProject(updatedProject);
    }
    if (onProjectUpdated) {
      onProjectUpdated(updatedProject);
    }
  };

  const project = currentProject || initialProject;

  const [activeTab, setActiveTab] = useState<EnterpriseWorkspaceTab>(initialTab);
  const [isSaving, setIsSaving] = useState(false);
  const [saveMessage, setSaveMessage] = useState<string | null>(null);

  // Responsive Sidebar States
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
  const [isMobileDrawerOpen, setIsMobileDrawerOpen] = useState(false);

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
  const [pendingAttachment, setPendingAttachment] = useState<ChatAttachmentItem | null>(null);
  const [isUploadingAttachment, setIsUploadingAttachment] = useState(false);

  // Modal Preview States
  const [pdfPreviewUrl, setPdfPreviewUrl] = useState<string | null>(null);
  const [pdfPreviewTitle, setPdfPreviewTitle] = useState<string>('');
  const [imageLightboxUrl, setImageLightboxUrl] = useState<string | null>(null);

  const fileInputRef = useRef<HTMLInputElement>(null);
  const chatBottomRef = useRef<HTMLDivElement>(null);

  // Keyboard shortcut listener (Esc key)
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        if (isMobileDrawerOpen) {
          setIsMobileDrawerOpen(false);
        } else if (pdfPreviewUrl) {
          setPdfPreviewUrl(null);
        } else if (imageLightboxUrl) {
          setImageLightboxUrl(null);
        } else if (isOpen) {
          onClose();
        }
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isMobileDrawerOpen, pdfPreviewUrl, imageLightboxUrl, isOpen, onClose]);

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
            attachments: m.attachments || [],
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

    const stompClient = new Client({
      webSocketFactory: () => new SockJS('/ws-chat'),
      reconnectDelay: 5000,
      debug: (str) => console.log("[STOMP]", str),
      onConnect: () => {
        console.log(`[STOMP CONNECTED] Subscribing to /topic/project.${projectCode}`);
        stompClient.subscribe(`/topic/project.${projectCode}`, (message) => {
          try {
            console.log("[STOMP FRAME RECEIVED RAW]", message.body);
            const data = JSON.parse(message.body);
            console.log("[STOMP PARSED DATA]", data);
            console.log("[STOMP ATTACHMENTS IN PAYLOAD]", data.attachments);
            if (data.content && data.id) {
              setChatMessages(prev => {
                if (prev.some(m => m.id === data.id)) return prev;
                const filtered = prev.filter(m => !(m.id.startsWith('temp-') && m.content === data.content));
                const newMsgList = [...filtered, {
                  id: data.id,
                  senderId: data.senderId || 'u-remote',
                  senderName: data.senderName || 'Team Member',
                  senderRole: data.senderRole || 'Engineer',
                  content: data.content,
                  sentAt: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                  isRead: true,
                  attachments: data.attachments || [],
                }];
                console.log("[REACT STATE UPDATED AFTER STOMP RECEIVE]", newMsgList);
                return newMsgList;
              });
            }
          } catch (e) {
            console.error("MESSAGE PROCESSING ERROR", e);
          }
        });
      },
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [project]);

  useEffect(() => {
    chatBottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [chatMessages]);

  // Attachment File Upload Handler
  const handleFileAttachmentSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !project) return;

    setIsUploadingAttachment(true);
    try {
      const pCode = project.projectCode || project.projectId || project.id;
      const userName = user ? `${user.firstName} ${user.lastName}` : 'Corporate User';
      const res = await projectWorkspaceApi.uploadDriveDocument(file, pCode, 'Chat Attachment', userName);
      if (res.data) {
        const rec = res.data;
        const localObjUrl = URL.createObjectURL(file);
        setPendingAttachment({
          id: rec.id || rec.fileId || `att-${Date.now()}`,
          driveFileId: rec.fileId || rec.id,
          fileName: rec.name || file.name,
          mimeType: rec.mimeType || file.type || 'application/octet-stream',
          fileSize: rec.fileSize || file.size,
          previewUrl: rec.webViewLink || rec.secureUrl || localObjUrl,
          downloadUrl: rec.webContentLink || `/api/v1/drive/download/${rec.fileId || rec.id}`,
          thumbnailUrl: (file.type.startsWith('image/') || file.type.startsWith('video/')) ? (rec.secureUrl || rec.webViewLink || localObjUrl) : '',
          uploadedBy: userName,
          uploadedAt: new Date().toISOString(),
        });
      }
    } catch (err: any) {
      console.error('Drive upload failed for chat attachment:', err);
      const msg = err?.response?.data?.message || 'Attachment upload failed. Storage configuration or network error.';
      alert(msg);
      setPendingAttachment(null);
    } finally {
      setIsUploadingAttachment(false);
    }
  };

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
  const isApprover = canApproveProjectStatus(user, project);
  const currentStatus = normalizeProjectStatus(project.status);
  const progress = getStatusProgress(currentStatus, Math.round(Number(project.overallProgressPercentage ?? project.progressPercentage ?? 0)));

  // Handlers
  const handleSendChatMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    const content = chatInput.trim();
    if ((!content && !pendingAttachment) || !project) return;

    const currentUserId = user?.id || 'u-curr';
    const currentUserName = user ? `${user.firstName} ${user.lastName}` : 'You';
    const projectCode = project.projectCode || project.projectId || project.id;
    const tempId = `temp-${Date.now()}`;

    const attachmentPayload = pendingAttachment ? [pendingAttachment] : [];

    // 1. Optimistic UI update
    const optimisticMsg: ChatMessage = {
      id: tempId,
      senderId: currentUserId,
      senderName: currentUserName,
      senderRole: isManager ? 'Project Manager' : isLead ? 'Technical Lead' : 'Engineering Team',
      content: content || (pendingAttachment ? `[Attachment: ${pendingAttachment.fileName}]` : ''),
      sentAt: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      isRead: true,
      attachments: attachmentPayload,
    };

    setChatMessages(prev => [...prev, optimisticMsg]);
    setChatInput('');
    setPendingAttachment(null);

    try {
      // 2. Send POST /messages/send using authenticated apiClient
      await apiClient.post('/messages/send', {
        subject: projectCode,
        content: optimisticMsg.content,
        attachments: attachmentPayload,
      }, {
        params: {
          senderId: currentUserId,
          senderName: currentUserName,
        }
      });
    } catch (err) {
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
      const derivedProg = getStatusProgress(settingsStatus, progress);
      await projectsApi.updateStatus(project.id, settingsStatus, 'Status updated via workspace settings', derivedProg);
      await projectsApi.update(project.id, { priority: settingsPriority, projectVisibility: settingsVisibility });
      setSaveMessage('Project settings & status updated!');
      if (onProjectUpdated) onProjectUpdated();
      setTimeout(() => setSaveMessage(null), 3000);
    } catch (err) {
      alert('Failed to update settings.');
    } finally {
      setIsSaving(false);
    }
  };

  // Categorized Navigation Groups for Sleek Enterprise Hierarchy
  const navGroups = [
    {
      title: 'CORE WORKSPACE',
      items: [
        { id: 'overview', label: 'Overview Dashboard', icon: Briefcase },
        { id: 'planning', label: 'Planning & Strategy', icon: Zap },
        { id: 'srs_documents', label: 'SRS & Documents', icon: FileText },
        { id: 'conversations', label: 'Conversations & Chat', icon: MessageSquare, badge: 'LIVE' },
      ]
    },
    {
      title: 'EXECUTION & TRACKING',
      items: [
        { id: 'tasks', label: 'Task Backlog', icon: CheckSquare },
        { id: 'kanban', label: 'Kanban Board', icon: Layers },
        { id: 'sprint', label: 'Sprint Management', icon: Clock },
        { id: 'timeline', label: 'Timeline & Milestones', icon: Calendar },
        { id: 'meetings', label: 'Meetings & Syncs', icon: Video },
      ]
    },
    {
      title: 'GOVERNANCE & ANALYTICS',
      items: [
        { id: 'devops', label: 'Repository & DevOps', icon: GitBranch },
        { id: 'risks', label: 'Risks & Governance', icon: AlertTriangle },
        { id: 'analytics', label: 'Productivity Analytics', icon: BarChart3 },
        { id: 'reports', label: 'Executive Reports', icon: Download },
        { id: 'audit', label: 'Audit Trail Stream', icon: Shield },
        { id: 'notifications', label: 'Notifications', icon: Bell },
      ]
    },
    {
      title: 'KNOWLEDGE & TOOLS',
      items: [
        { id: 'wiki', label: 'Knowledge Base & Wiki', icon: BookOpen },
        { id: 'ai_workspace', label: 'AI Copilot Assistant', icon: Bot },
        { id: 'security', label: 'Security & SSO', icon: ShieldCheck },
        { id: 'settings', label: 'Project Settings', icon: Settings },
      ]
    }
  ];

  const currentUserId = user?.id || 'u-curr';

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-2 sm:p-4 lg:p-6 overflow-hidden select-none animate-in fade-in duration-200">
      
      {/* Central Enterprise SaaS Workspace Container */}
      <motion.div 
        initial={{ opacity: 0, scale: 0.98, y: 8 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.98, y: 8 }}
        transition={{ duration: 0.2 }}
        className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/90 rounded-2xl lg:rounded-3xl max-w-[1600px] w-full h-[92vh] flex flex-col shadow-2xl overflow-hidden"
      >
        
        {/* Workspace Top Sticky Header Bar */}
        <div className="px-4 py-3 sm:px-6 sm:py-4 bg-slate-950 text-white border-b border-slate-800/80 shrink-0 flex items-center justify-between gap-3 sm:gap-4 font-sans">
          
          {/* Left Side: Mobile Drawer Button, Code, Status & Title */}
          <div className="flex items-center gap-2.5 sm:gap-3 min-w-0">
            
            {/* Mobile Sidebar Hamburger Toggle Button */}
            <button
              onClick={() => setIsMobileDrawerOpen(true)}
              className="md:hidden p-2 text-slate-300 hover:text-white rounded-xl bg-slate-800/60 border border-slate-700/60"
              aria-label="Open navigation menu"
            >
              <Menu className="w-5 h-5" />
            </button>

            <span className="px-2.5 py-1 bg-cyan-500/10 text-cyan-400 font-mono text-xs font-bold rounded-lg border border-cyan-500/20 shrink-0">
              {projectCode}
            </span>

            <div className="shrink-0">
              {isApprover ? (
                <select
                  value={currentStatus}
                  onChange={async (e) => {
                    const newStatus = e.target.value;
                    const newProgress = getStatusProgress(newStatus, progress);
                    try {
                      await projectsApi.updateStatus(project.id, newStatus, 'Status updated via workspace header', newProgress);
                      if (onProjectUpdated) onProjectUpdated();
                    } catch (err) {
                      console.error('Failed to update status from workspace header:', err);
                    }
                  }}
                  className="text-xs font-extrabold px-2.5 py-1 rounded-lg bg-slate-900 text-cyan-400 border border-slate-700 focus:outline-none focus:ring-2 focus:ring-cyan-500 cursor-pointer"
                >
                  {PROJECT_STATUS_LIST.map((s) => (
                    <option key={s.value} value={s.value}>{s.label}</option>
                  ))}
                </select>
              ) : (
                <StatusBadge status={currentStatus} />
              )}
            </div>

            <h1 className="text-base sm:text-lg lg:text-xl font-extrabold text-white tracking-tight truncate max-w-xs sm:max-w-md lg:max-w-xl">
              {projectName}
            </h1>

            <span className="text-xs text-slate-400 font-medium hidden lg:inline-flex items-center gap-1.5 shrink-0 truncate border-l border-slate-800 pl-3">
              <span>{project.department || 'Engineering'}</span>
              <span className="text-slate-600">•</span>
              <span className="text-slate-300 font-semibold">{project.client || project.clientOrganization || 'Enterprise Client'}</span>
            </span>
          </div>

          {/* Right Side: Quick Metric Pills & Actions */}
          <div className="flex items-center gap-2.5 sm:gap-4 shrink-0">
            
            {/* Target Release Date Pill */}
            <div className="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-slate-900 rounded-xl border border-slate-800 text-xs">
              <Calendar className="w-3.5 h-3.5 text-indigo-400" />
              <span className="text-slate-400 font-medium">Target Release:</span>
              <span className="text-indigo-300 font-bold font-mono">
                {project.endDate || project.targetEndDate || project.estimatedCompletion ? new Date(project.endDate || project.targetEndDate || project.estimatedCompletion!).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }) : 'Not set'}
              </span>
            </div>

            {/* Progress Pill */}
            <div className="hidden md:flex items-center gap-2.5 px-3 py-1.5 bg-slate-900 rounded-xl border border-slate-800 text-xs">
              <Sparkles className="w-3.5 h-3.5 text-cyan-400" />
              <span className="text-slate-400 font-medium">Progress:</span>
              <span className="text-cyan-400 font-bold font-mono">{progress}%</span>
              <div className="w-16 h-1.5 bg-slate-800 rounded-full overflow-hidden">
                <div className="h-full bg-cyan-400 rounded-full" style={{ width: `${progress}%` }} />
              </div>
            </div>

            {/* Desktop Sidebar Collapse Toggle */}
            <button
              onClick={() => setIsSidebarCollapsed(!isSidebarCollapsed)}
              className="hidden md:flex p-2 text-slate-400 hover:text-white rounded-xl hover:bg-slate-800/80 transition-colors"
              title={isSidebarCollapsed ? "Expand sidebar" : "Collapse sidebar"}
            >
              {isSidebarCollapsed ? <PanelLeftOpen className="w-5 h-5" /> : <PanelLeftClose className="w-5 h-5" />}
            </button>

            {/* Close Modal Button */}
            <button
              onClick={onClose}
              className="p-2 text-slate-400 hover:text-white hover:bg-slate-800/80 rounded-xl transition-colors"
              title="Close Workspace (Esc)"
              aria-label="Close Workspace"
            >
              <X className="w-5 h-5 sm:w-6 sm:h-6" />
            </button>
          </div>
        </div>

        {/* Global Save Alert Banner */}
        {saveMessage && (
          <div className="px-6 py-2.5 bg-emerald-500/10 border-b border-emerald-500/20 text-xs text-emerald-400 font-bold flex items-center gap-2 shrink-0">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" /> {saveMessage}
          </div>
        )}

        {/* Main Workspace Inner Shell */}
        <div className="flex-1 flex overflow-hidden bg-slate-50/50 dark:bg-slate-950/60">
          
          {/* Desktop Left Collapsible Navigation Sidebar */}
          <motion.div 
            animate={{ width: isSidebarCollapsed ? 76 : 280 }}
            transition={{ duration: 0.25, ease: "easeInOut" }}
            className="bg-slate-950 border-r border-slate-800/80 overflow-y-auto p-3 space-y-5 shrink-0 no-scrollbar hidden md:flex flex-col justify-between"
          >
            <div className="space-y-4">
              {navGroups.map((group, groupIdx) => (
                <div key={groupIdx} className="space-y-1">
                  {!isSidebarCollapsed ? (
                    <span className="text-[10px] uppercase font-extrabold text-slate-500 px-3 tracking-wider block mb-1.5">
                      {group.title}
                    </span>
                  ) : (
                    <div className="h-px bg-slate-800/60 my-2 mx-1" />
                  )}

                  {group.items.map((item) => {
                    const isActive = activeTab === item.id;
                    const IconComp = item.icon;
                    return (
                      <button
                        key={item.id}
                        onClick={() => setActiveTab(item.id as EnterpriseWorkspaceTab)}
                        title={isSidebarCollapsed ? item.label : undefined}
                        className={`w-full px-3 py-2.5 rounded-xl text-xs font-bold transition-all flex items-center justify-between gap-3 ${
                          isActive
                            ? 'bg-gradient-to-r from-cyan-500/20 to-indigo-500/20 text-cyan-400 border-l-4 border-cyan-400 font-extrabold shadow-xs backdrop-blur-xs'
                            : 'text-slate-400 hover:bg-slate-900 hover:text-slate-200'
                        } ${isSidebarCollapsed ? 'justify-center px-0' : ''}`}
                      >
                        <div className="flex items-center gap-3 min-w-0">
                          <IconComp className={`w-4 h-4 shrink-0 ${isActive ? 'text-cyan-400' : 'text-slate-400'}`} />
                          {!isSidebarCollapsed && (
                            <span className="truncate">{item.label}</span>
                          )}
                        </div>

                        {!isSidebarCollapsed && item.badge && (
                          <span className="px-1.5 py-0.5 bg-cyan-500/20 text-cyan-300 font-mono text-[9px] font-extrabold rounded-md border border-cyan-500/30">
                            {item.badge}
                          </span>
                        )}
                      </button>
                    );
                  })}
                </div>
              ))}
            </div>

            {/* Sidebar Footer Info */}
            {!isSidebarCollapsed && (
              <div className="p-3 rounded-xl bg-slate-900/60 border border-slate-800/80 text-[11px] text-slate-500 font-medium space-y-1">
                <div className="flex items-center gap-2 text-slate-300 font-bold">
                  <Activity className="w-3.5 h-3.5 text-cyan-400" />
                  <span>SaaS Engine v3.5</span>
                </div>
                <p className="text-[10px] text-slate-500">Real-time synced workspace</p>
              </div>
            )}
          </motion.div>

          {/* Mobile Drawer Navigation Sidebar */}
          <AnimatePresence>
            {isMobileDrawerOpen && (
              <>
                <motion.div 
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                  onClick={() => setIsMobileDrawerOpen(false)}
                  className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs md:hidden"
                />
                <motion.div 
                  initial={{ x: '-100%' }}
                  animate={{ x: 0 }}
                  exit={{ x: '-100%' }}
                  transition={{ duration: 0.25, ease: "easeOut" }}
                  className="fixed inset-y-0 left-0 z-50 w-72 bg-slate-950 border-r border-slate-800 p-4 flex flex-col justify-between overflow-y-auto md:hidden"
                >
                  <div className="space-y-6">
                    <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                      <div className="flex items-center gap-2">
                        <Briefcase className="w-5 h-5 text-cyan-400" />
                        <span className="font-extrabold text-sm text-white font-mono">{projectCode}</span>
                      </div>
                      <button 
                        onClick={() => setIsMobileDrawerOpen(false)}
                        className="p-1.5 text-slate-400 hover:text-white rounded-lg bg-slate-900"
                      >
                        <X className="w-5 h-5" />
                      </button>
                    </div>

                    <div className="space-y-5">
                      {navGroups.map((group, groupIdx) => (
                        <div key={groupIdx} className="space-y-1">
                          <span className="text-[10px] uppercase font-extrabold text-slate-500 px-3 tracking-wider block mb-1.5">
                            {group.title}
                          </span>
                          {group.items.map((item) => {
                            const isActive = activeTab === item.id;
                            const IconComp = item.icon;
                            return (
                              <button
                                key={item.id}
                                onClick={() => {
                                  setActiveTab(item.id as EnterpriseWorkspaceTab);
                                  setIsMobileDrawerOpen(false);
                                }}
                                className={`w-full px-3 py-2.5 rounded-xl text-xs font-bold transition-all flex items-center justify-between gap-3 ${
                                  isActive
                                    ? 'bg-cyan-500 text-slate-950 font-black shadow-md'
                                    : 'text-slate-300 hover:bg-slate-900 hover:text-white'
                                }`}
                              >
                                <div className="flex items-center gap-3 min-w-0">
                                  <IconComp className="w-4 h-4 shrink-0" />
                                  <span className="truncate">{item.label}</span>
                                </div>
                                {item.badge && (
                                  <span className="px-1.5 py-0.5 bg-cyan-400/20 text-cyan-300 font-mono text-[9px] font-black rounded-md">
                                    {item.badge}
                                  </span>
                                )}
                              </button>
                            );
                          })}
                        </div>
                      ))}
                    </div>
                  </div>
                </motion.div>
              </>
            )}
          </AnimatePresence>

          {/* Center Main Viewport Workspace Content Panel */}
          <div className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 space-y-6 scroll-smooth">
            
            {/* Pending Status Change Banner */}
            {project.pendingStatusRequest && (
              <div className="p-4 rounded-2xl bg-amber-500/10 border border-amber-500/30 text-amber-700 dark:text-amber-300 text-xs flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 shadow-xs">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="px-2 py-0.5 rounded-md bg-amber-500 text-slate-950 font-black text-[10px] uppercase font-mono tracking-wider">
                      Pending Status Approval
                    </span>
                    <span className="text-[11px] text-slate-400 font-mono">{project.pendingStatusRequest.requestedAt}</span>
                  </div>
                  <p className="text-xs font-bold">
                    {getStatusLabel(project.status)} → <span className="text-cyan-400 font-black">{getStatusLabel(project.pendingStatusRequest.requestedStatus)}</span>
                  </p>
                  <p className="text-[11px] text-slate-500 dark:text-slate-400">
                    Requested by: <strong className="text-slate-700 dark:text-slate-200">{project.pendingStatusRequest.requestedBy}</strong>
                    {project.pendingStatusRequest.reason && <span> — "{project.pendingStatusRequest.reason}"</span>}
                  </p>
                </div>
                {isApprover && (
                  <div className="flex items-center gap-2 shrink-0">
                    <button
                      type="button"
                      onClick={async () => {
                        const targetStatus = project.pendingStatusRequest!.requestedStatus;
                        const targetProgress = getStatusProgress(targetStatus, progress);
                        await projectsApi.update(project.id, { pendingStatusRequest: null });
                        await projectsApi.updateStatus(project.id, targetStatus, 'Approved pending status request', targetProgress);
                        if (onProjectUpdated) onProjectUpdated();
                      }}
                      className="px-3.5 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white font-extrabold text-xs rounded-xl shadow-xs cursor-pointer"
                    >
                      Approve Change
                    </button>
                    <button
                      type="button"
                      onClick={async () => {
                        await projectsApi.update(project.id, { pendingStatusRequest: null });
                        if (onProjectUpdated) onProjectUpdated();
                      }}
                      className="px-3.5 py-1.5 bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-extrabold text-xs rounded-xl cursor-pointer"
                    >
                      Reject
                    </button>
                  </div>
                )}
              </div>
            )}

            {/* OVERVIEW DASHBOARD TAB */}
            {activeTab === 'overview' && (
              <ProjectOverviewTab project={project} />
            )}

            {/* ENTERPRISE SLACK / TEAMS STYLE 1-COLUMN PROJECT WORKSPACE CHAT */}
            {activeTab === 'conversations' && (
              <div className="p-4 sm:p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex flex-col justify-between overflow-hidden h-[74vh] shadow-xs">
                
                {/* Channel Header */}
                <div className="pb-3.5 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
                  <div>
                    <h3 className="text-sm font-black text-slate-900 dark:text-white flex items-center gap-2">
                      <MessageSquare className="w-4 h-4 text-cyan-500" /> {projectName} Channel ({projectCode})
                    </h3>
                    <p className="text-[11px] text-slate-500 font-medium">STOMP SockJS `/ws-chat` • WebSocket Synced</p>
                  </div>

                  <div className="flex items-center gap-2 text-xs">
                    <span className="px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 font-extrabold text-[10px] flex items-center gap-1.5 border border-emerald-500/20">
                      <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" /> Connected
                    </span>
                  </div>
                </div>

                {/* Messages Stream */}
                <div className="flex-1 overflow-y-auto p-3 space-y-4">
                  <div className="text-center my-2">
                    <span className="px-3 py-1 bg-slate-100 dark:bg-slate-800 text-slate-400 font-mono text-[10px] font-bold rounded-full">
                      Today
                    </span>
                  </div>

                  {chatMessages.map((msg) => {
                    const isMe = msg.senderId === currentUserId || msg.senderName === 'You';
                    return (
                      <div key={msg.id} className={`flex items-end gap-2.5 ${isMe ? 'justify-end' : 'justify-start'}`}>
                        {!isMe && (
                          <div className="w-8 h-8 rounded-2xl bg-slate-800 text-cyan-400 font-black text-xs flex items-center justify-center shrink-0 border border-slate-700">
                            {msg.senderName.charAt(0)}
                          </div>
                        )}

                        <div className={`space-y-1 max-w-lg ${isMe ? 'items-end' : 'items-start'}`}>
                          {!isMe && (
                            <div className="flex items-center gap-2 text-[10px] px-1">
                              <span className="font-extrabold text-slate-900 dark:text-white">{msg.senderName}</span>
                              <span className="text-slate-400 font-mono">({msg.senderRole})</span>
                            </div>
                          )}

                          <div className={`p-3.5 rounded-2xl text-xs font-medium leading-relaxed shadow-xs ${
                            isMe 
                              ? 'bg-cyan-500 text-slate-950 font-bold rounded-br-none' 
                              : 'bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 rounded-bl-none border border-slate-200/60 dark:border-slate-700/60'
                          }`}>
                            {msg.content && <div className="whitespace-pre-wrap leading-relaxed">{msg.content}</div>}

                            {/* Attachment Cards Rendering */}
                            {msg.attachments && msg.attachments.length > 0 && (
                              <div className="mt-2.5 space-y-2 pt-2 border-t border-slate-700/20">
                                {msg.attachments.map((att: any, attIdx: number) => (
                                  <AttachmentCard
                                    key={att.id || attIdx}
                                    attachment={att}
                                    onPreviewPdf={(url, title) => { setPdfPreviewUrl(url); setPdfPreviewTitle(title); }}
                                    onPreviewImage={(url) => setImageLightboxUrl(url)}
                                  />
                                ))}
                              </div>
                            )}

                            <div className={`flex items-center justify-end gap-1.5 mt-1.5 text-[9px] ${isMe ? 'text-slate-800 font-mono' : 'text-slate-400'}`}>
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

                {/* Staged Pending Attachment Badge */}
                {pendingAttachment && (
                  <div className="px-3.5 py-2 mx-3 mb-2 rounded-2xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-between text-xs text-cyan-400 font-bold">
                    <div className="flex items-center gap-2.5 truncate">
                      <Paperclip className="w-4 h-4 shrink-0 text-cyan-400" />
                      <span className="truncate">{pendingAttachment.fileName} ({formatBytes(pendingAttachment.fileSize)})</span>
                    </div>
                    <button onClick={() => setPendingAttachment(null)} className="p-1 text-slate-400 hover:text-rose-400 rounded-lg">✕</button>
                  </div>
                )}

                {/* Chat Input Bar */}
                <form onSubmit={handleSendChatMessage} className="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center gap-2.5">
                  <input
                    type="file"
                    ref={fileInputRef}
                    onChange={handleFileAttachmentSelect}
                    className="hidden"
                  />
                  <button
                    type="button"
                    onClick={() => fileInputRef.current?.click()}
                    disabled={isUploadingAttachment}
                    className="p-3 text-slate-400 hover:text-cyan-400 rounded-2xl bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 transition-colors shrink-0"
                    title="Attach Files (PDF, Images, Video, Audio, DOCX, ZIP)"
                  >
                    {isUploadingAttachment ? <Loader2 className="w-4 h-4 animate-spin text-cyan-400" /> : <Paperclip className="w-4 h-4" />}
                  </button>

                  <input
                    type="text"
                    value={chatInput}
                    onChange={(e) => setChatInput(e.target.value)}
                    placeholder="Type a message or attach document/media files..."
                    className="flex-1 text-xs p-3 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 font-medium text-slate-900 dark:text-slate-100 focus:outline-none focus:border-cyan-500"
                  />
                  <button type="submit" className="px-5 py-3 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-2xl shadow-md transition-all flex items-center gap-1.5 shrink-0">
                    <Send className="w-4 h-4" />
                    <span>Send</span>
                  </button>
                </form>
              </div>
            )}

            {/* SRS & DOCUMENTS TAB */}
            {activeTab === 'srs_documents' && (
              <ProjectDocumentsTab project={project} />
            )}

            {/* PLANNING WORKSPACE TAB */}
            {activeTab === 'planning' && <EnterpriseProjectPlanningWorkspace project={project} />}

            {/* TASKS & KANBAN MANAGEMENT WORKSPACE TABS */}
            {activeTab === 'tasks' && <EnterpriseTaskManagementWorkspace project={project} initialView="list" />}
            {activeTab === 'kanban' && <EnterpriseTaskManagementWorkspace project={project} initialView="kanban" />}
            {activeTab === 'sprint' && <ProjectSprintTab project={project} />}
            {activeTab === 'timeline' && <ProjectMilestonesTab project={project} />}
            {activeTab === 'meetings' && <ProjectMeetingsTab project={project} />}

            {/* RISKS & GOVERNANCE TAB */}
            {activeTab === 'risks' && <ProjectRisksTab project={project} />}

            {/* PRODUCTIVITY ANALYTICS TAB */}
            {activeTab === 'analytics' && <ProjectAnalyticsTab project={project} />}

            {/* NOTIFICATIONS TAB */}
            {activeTab === 'notifications' && <ProjectNotificationsTab project={project} />}

            {/* REPORTS & AUDIT TAB */}
            {activeTab === 'reports' && <ProjectExecutiveReportTab project={project} />}
            {activeTab === 'audit' && <AuditTrailViewer entityType="Project" entityId={projectCode} />}

            {/* REPOSITORY & DEVOPS TAB */}
            {activeTab === 'devops' && (
              <EnterpriseDevOpsWorkspace 
                project={project} 
                onProjectUpdated={handleChildProjectUpdated} 
                isApprover={isApprover} 
              />
            )}

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
                  {isApprover && <button onClick={handleSaveSettings} disabled={isSaving} className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md">Save Settings</button>}
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
                  <div>
                    <label className="font-bold block mb-1">Status Lifecycle</label>
                    {isApprover ? (
                      <select value={settingsStatus} onChange={(e) => setSettingsStatus(e.target.value)} className="w-full text-xs font-bold p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100">
                        {PROJECT_STATUS_LIST.map((s) => (
                          <option key={s.value} value={s.value}>{s.label}</option>
                        ))}
                      </select>
                    ) : (
                      <StatusBadge status={settingsStatus} />
                    )}
                  </div>
                  <div>
                    <label className="font-bold block mb-1">Derived Overall Progress</label>
                    <div className="p-2.5 rounded-xl bg-slate-100 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/80 font-mono font-bold text-cyan-600 dark:text-cyan-400">
                      {getStatusProgress(settingsStatus, progress)}% <span className="text-[10px] text-slate-400 font-sans font-normal">(Derived from status)</span>
                    </div>
                  </div>
                </div>
              </div>
            )}

          </div>
        </div>

        {/* Workspace Sticky Footer Bar */}
        <div className="px-4 py-3 sm:px-6 bg-white dark:bg-slate-950 border-t border-slate-200 dark:border-slate-800/80 flex items-center justify-between gap-4 shrink-0 text-xs font-medium text-slate-500">
          <div className="flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <span className="font-mono text-slate-600 dark:text-slate-400 font-semibold">Enterprise Workspace OS Active</span>
          </div>

          <button 
            onClick={onClose} 
            className="px-4 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800/80 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold rounded-xl transition-all"
          >
            Close Workspace
          </button>
        </div>

      </motion.div>

      {/* PDF PREVIEW MODAL */}
      {pdfPreviewUrl && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl w-full max-w-5xl h-[85vh] flex flex-col overflow-hidden shadow-2xl">
            <div className="px-6 py-4 border-b border-slate-800 flex items-center justify-between">
              <div className="flex items-center gap-2 truncate">
                <FileText className="w-5 h-5 text-rose-400" />
                <h3 className="font-black text-sm text-white truncate font-mono">{pdfPreviewTitle || 'PDF Document Preview'}</h3>
              </div>
              <div className="flex items-center gap-2">
                <a
                  href={pdfPreviewUrl}
                  download={pdfPreviewTitle || 'document.pdf'}
                  className="px-3 py-1.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-xl inline-flex items-center gap-1.5"
                >
                  <Download className="w-3.5 h-3.5" /> Download PDF
                </a>
                <button
                  type="button"
                  onClick={() => setPdfPreviewUrl(null)}
                  className="p-2 text-slate-400 hover:text-white rounded-xl bg-slate-800"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            </div>
            <div className="flex-1 bg-slate-950 p-2">
              <iframe
                src={pdfPreviewUrl}
                title="PDF Preview"
                className="w-full h-full rounded-2xl border-none"
              />
            </div>
          </div>
        </div>
      )}

      {/* IMAGE LIGHTBOX MODAL */}
      {imageLightboxUrl && (
        <div
          className="fixed inset-0 z-50 bg-slate-950/90 backdrop-blur-md flex items-center justify-center p-4"
          onClick={() => setImageLightboxUrl(null)}
        >
          <div className="relative max-w-5xl max-h-[90vh] flex flex-col items-center">
            <button
              type="button"
              onClick={() => setImageLightboxUrl(null)}
              className="absolute -top-12 right-0 p-2 text-white bg-slate-800 rounded-full hover:bg-slate-700"
            >
              <X className="w-5 h-5" />
            </button>
            <img src={imageLightboxUrl} alt="Preview" className="max-h-[85vh] rounded-2xl object-contain shadow-2xl" />
          </div>
        </div>
      )}
    </div>
  );
};
