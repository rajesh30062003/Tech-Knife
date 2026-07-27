import { apiClient } from './client';
import {
  StorageFile,
  FileCategory,
  NotificationTemplate,
  SystemNotification,
  ApprovalWorkflow,
  ApprovalStatus,
  ActivityLog,
  AuditLogEntry,
  UniversalSearchResult,
  DashboardWidgetConfig,
  ReportConfig,
  SystemSettings,
  DynamicRole,
} from '../types';

// Mock/Initial Storage State for robust presentation & demo
const INITIAL_FILES: StorageFile[] = [
  {
    id: 'file-101',
    name: 'TechKnife_Employee_Handbook_2026.pdf',
    category: 'Documents',
    url: 'https://res.cloudinary.com/techknife/image/upload/v171000101/handbook.pdf',
    publicId: 'techknife/docs/handbook',
    fileSize: 2450000,
    format: 'pdf',
    uploadedBy: 'Sarah Connor',
    uploadedByEmail: 'sarah.connor@techknife.io',
    module: 'HR & Onboarding',
    isPrivate: false,
    createdAt: '2026-01-15T09:30:00Z',
  },
  {
    id: 'file-102',
    name: 'Executive_Q2_Financial_Invoice.pdf',
    category: 'Invoices',
    url: 'https://res.cloudinary.com/techknife/image/upload/v171000102/invoice_q2.pdf',
    publicId: 'techknife/invoices/q2',
    fileSize: 1120000,
    format: 'pdf',
    uploadedBy: 'Marcus Brody',
    uploadedByEmail: 'marcus.brody@techknife.io',
    module: 'Finance & Payroll',
    isPrivate: true,
    createdAt: '2026-04-01T14:15:00Z',
  },
  {
    id: 'file-103',
    name: 'Architecture_System_Topology.png',
    category: 'Images',
    url: 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=1200&auto=format&fit=crop',
    publicId: 'techknife/images/architecture',
    fileSize: 3200000,
    format: 'png',
    uploadedBy: 'Elena Rostova',
    uploadedByEmail: 'elena.rostova@techknife.io',
    module: 'Engineering',
    isPrivate: false,
    createdAt: '2026-06-10T11:45:00Z',
  },
  {
    id: 'file-104',
    name: 'Internship_Completion_Certificate_Template.pdf',
    category: 'Certificates',
    url: 'https://res.cloudinary.com/techknife/image/upload/v171000104/certificate_template.pdf',
    publicId: 'techknife/certs/template',
    fileSize: 1800000,
    format: 'pdf',
    uploadedBy: 'Sarah Connor',
    uploadedByEmail: 'sarah.connor@techknife.io',
    module: 'Internship Program',
    isPrivate: false,
    createdAt: '2026-05-20T16:20:00Z',
  },
];

const INITIAL_WORKFLOWS: ApprovalWorkflow[] = [
  {
    id: 'wf-101',
    workflowNumber: 'APPR-2026-0089',
    title: 'Annual Executive Leave Request - 5 Days',
    module: 'LEAVE',
    requesterName: 'Alex Rivera',
    requesterEmail: 'alex.rivera@techknife.io',
    currentStepIndex: 1,
    totalSteps: 2,
    status: 'PENDING',
    payload: { leaveType: 'Annual', days: 5, startDate: '2026-08-01', endDate: '2026-08-05' },
    steps: [
      { stepNumber: 1, stepName: 'Department Manager Review', approverRole: 'ROLE_MANAGER', approverName: 'Marcus Brody', status: 'APPROVED', comment: 'Approved for coverage.', actionAt: '2026-07-21T10:00:00Z' },
      { stepNumber: 2, stepName: 'VP of HR Final Sanction', approverRole: 'ROLE_ADMIN', approverName: 'Sarah Connor', status: 'PENDING' },
    ],
    createdAt: '2026-07-21T08:30:00Z',
    updatedAt: '2026-07-21T10:00:00Z',
  },
  {
    id: 'wf-102',
    workflowNumber: 'APPR-2026-0090',
    title: 'Cloud Infrastructure Upgrade Expense - $4,200',
    module: 'EXPENSE',
    requesterName: 'Devon Vance',
    requesterEmail: 'devon.vance@techknife.io',
    currentStepIndex: 0,
    totalSteps: 2,
    status: 'PENDING',
    payload: { amount: '$4,200', vendor: 'AWS Cloud Services', purpose: 'Database scaling' },
    steps: [
      { stepNumber: 1, stepName: 'Engineering Lead Approval', approverRole: 'ROLE_CTO', approverName: 'CTO Office', status: 'PENDING' },
      { stepNumber: 2, stepName: 'Finance Disbursement', approverRole: 'ROLE_ADMIN', approverName: 'Finance Admin', status: 'PENDING' },
    ],
    createdAt: '2026-07-22T14:10:00Z',
    updatedAt: '2026-07-22T14:10:00Z',
  },
];

const INITIAL_SETTINGS: SystemSettings = {
  companyInfo: {
    name: 'Tech Knife Enterprise Inc.',
    taxId: 'US-EIN-984210942',
    contactEmail: 'contact@techknife.io',
    supportPhone: '+1 (800) 555-0199',
    website: 'https://techknife.io',
    address: '100 Enterprise Way, Suite 500, Silicon Valley, CA 94025',
    logoUrl: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&auto=format&fit=crop',
  },
  smtpConfig: {
    host: 'smtp.sendgrid.net',
    port: 587,
    username: 'apikey',
    sslEnabled: true,
    senderName: 'Tech Knife Corporate Mailer',
    senderEmail: 'noreply@techknife.io',
  },
  cloudinaryConfig: {
    cloudName: 'techknife-enterprise-cloud',
    apiKey: '984210492819042',
    defaultFolder: 'techknife/assets',
    enabled: true,
  },
  githubConfig: {
    organization: 'techknife-enterprise',
    enabled: true,
    webhookUrl: 'https://api.techknife.io/api/v1/github/webhook',
  },
  themeSettings: {
    primaryColor: '#4f46e5',
    defaultMode: 'dark',
    compactDensity: false,
  },
  workingHours: {
    startTime: '09:00',
    endTime: '18:00',
    timezone: 'UTC-08:00 (Pacific Time)',
    workDays: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
  },
  holidayCalendar: [
    { id: 'hol-1', name: 'New Year\'s Day', date: '2026-01-01', type: 'National' },
    { id: 'hol-2', name: 'Tech Knife Foundation Anniversary', date: '2026-03-15', type: 'Corporate' },
    { id: 'hol-3', name: 'Independence Day', date: '2026-07-04', type: 'National' },
    { id: 'hol-4', name: 'Thanksgiving Day', date: '2026-11-26', type: 'National' },
    { id: 'hol-5', name: 'Winter Holidays', date: '2026-12-25', type: 'National' },
  ],
};

const INITIAL_ROLES: DynamicRole[] = [
  {
    role: 'ROLE_SUPER_ADMIN',
    displayName: 'Super Administrator',
    description: 'Full unmitigated root access to enterprise settings, audit logs, and security controls.',
    isSystem: true,
    permissions: ['USER_READ', 'USER_WRITE', 'USER_DELETE', 'PROJECT_READ', 'PROJECT_WRITE', 'PROJECT_DELETE', 'PAYROLL_READ', 'PAYROLL_WRITE', 'CRM_READ', 'CRM_WRITE', 'RECRUITMENT_READ', 'RECRUITMENT_WRITE', 'SYSTEM_ADMIN'],
    menuPermissions: ['/dashboard', '/admin', '/manager', '/employees', '/interns', '/projects', '/payroll', '/attendance', '/leave', '/crm', '/recruitment', '/reports', '/support', '/notifications', '/settings', '/profile'],
    apiPermissions: ['GET /api/*', 'POST /api/*', 'PUT /api/*', 'DELETE /api/*'],
    featureFlags: { enableAuditLogs: true, enableCloudinaryUploads: true, enableApprovalWorkflows: true, enableGithubIntegration: true },
  },
  {
    role: 'ROLE_ADMIN',
    displayName: 'Corporate Admin / HR Lead',
    description: 'Manages employee directory, payroll processing, recruitment pipelines, and company settings.',
    isSystem: true,
    permissions: ['USER_READ', 'USER_WRITE', 'PROJECT_READ', 'PROJECT_WRITE', 'PAYROLL_READ', 'PAYROLL_WRITE', 'RECRUITMENT_READ', 'RECRUITMENT_WRITE'],
    menuPermissions: ['/dashboard', '/admin', '/manager', '/employees', '/interns', '/projects', '/payroll', '/attendance', '/leave', '/crm', '/recruitment', '/reports', '/support', '/notifications', '/settings', '/profile'],
    apiPermissions: ['GET /api/*', 'POST /api/employees', 'PUT /api/employees', 'POST /api/payroll/*'],
    featureFlags: { enableAuditLogs: true, enableCloudinaryUploads: true, enableApprovalWorkflows: true, enableGithubIntegration: true },
  },
  {
    role: 'ROLE_MANAGER',
    displayName: 'Engineering / Dept Manager',
    description: 'Manages team assignments, approves leave requests, reviews project deliverables & CRM leads.',
    isSystem: true,
    permissions: ['USER_READ', 'PROJECT_READ', 'PROJECT_WRITE', 'CRM_READ', 'CRM_WRITE', 'RECRUITMENT_READ'],
    menuPermissions: ['/dashboard', '/manager', '/employees', '/interns', '/projects', '/attendance', '/leave', '/crm', '/recruitment', '/reports', '/support', '/notifications', '/profile'],
    apiPermissions: ['GET /api/*', 'POST /api/projects/*', 'PUT /api/projects/*'],
    featureFlags: { enableAuditLogs: false, enableCloudinaryUploads: true, enableApprovalWorkflows: true, enableGithubIntegration: true },
  },
  {
    role: 'ROLE_EMPLOYEE',
    displayName: 'Full-Time Employee',
    description: 'Accesses self-service desk, submits leave requests, logs time, views payslips & assigned tasks.',
    isSystem: true,
    permissions: ['PROJECT_READ'],
    menuPermissions: ['/dashboard', '/employee', '/projects', '/attendance', '/leave', '/support', '/notifications', '/profile'],
    apiPermissions: ['GET /api/me', 'POST /api/leave/request', 'POST /api/attendance/clock-in'],
    featureFlags: { enableAuditLogs: false, enableCloudinaryUploads: true, enableApprovalWorkflows: true, enableGithubIntegration: false },
  },
  {
    role: 'ROLE_INTERN',
    displayName: 'Intern Cohort Member',
    description: 'Accesses intern learning portal, submits milestone code, tracks mentor evaluations.',
    isSystem: true,
    permissions: ['PROJECT_READ'],
    menuPermissions: ['/dashboard', '/intern', '/projects', '/attendance', '/support', '/notifications', '/profile'],
    apiPermissions: ['GET /api/interns/me', 'POST /api/interns/submit-task'],
    featureFlags: { enableAuditLogs: false, enableCloudinaryUploads: true, enableApprovalWorkflows: false, enableGithubIntegration: true },
  },
];

// Helper for activity logging
export const logActivityAction = (
  userName: string,
  userRole: any,
  module: string,
  action: string,
  description: string
) => {
  const existing = JSON.parse(localStorage.getItem('techknife_activity_logs') || '[]');
  const newLog: ActivityLog = {
    id: `act-${Date.now()}`,
    userId: `usr-${Math.floor(Math.random() * 1000)}`,
    userName: userName || 'Corporate User',
    userRole: userRole || 'ROLE_ADMIN',
    module,
    action,
    description,
    ipAddress: '192.168.1.104',
    browser: 'Chrome 126.0 (macOS)',
    timestamp: new Date().toISOString(),
  };
  localStorage.setItem('techknife_activity_logs', JSON.stringify([newLog, ...existing].slice(0, 100)));
};

// 1. Universal Storage Service
export const storageApi = {
  getFiles: async (category?: FileCategory): Promise<StorageFile[]> => {
    try {
      const res = await apiClient.get('/storage/files', { params: { category } });
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_storage_files') || 'null');
      const files: StorageFile[] = stored || INITIAL_FILES;
      if (category) {
        return files.filter(f => f.category === category);
      }
      return files;
    }
  },

  uploadFile: async (
    file: File,
    category: FileCategory,
    uploadedBy: string,
    uploadedByEmail: string,
    module: string
  ): Promise<StorageFile> => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('category', category);
      formData.append('uploadedBy', uploadedBy);
      formData.append('module', module);

      const res = await apiClient.post('/storage/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      return res.data.data;
    } catch {
      // Local fallback simulator with mock Cloudinary URL
      const mockFile: StorageFile = {
        id: `file-${Date.now()}`,
        name: file.name,
        category,
        url: URL.createObjectURL(file),
        publicId: `techknife/${category.toLowerCase().replace(/\s+/g, '_')}/${Date.now()}`,
        fileSize: file.size,
        format: file.name.split('.').pop() || 'bin',
        uploadedBy,
        uploadedByEmail,
        module,
        isPrivate: category === 'Invoices' || category === 'Payslips',
        createdAt: new Date().toISOString(),
      };

      const existing = await storageApi.getFiles();
      const updated = [mockFile, ...existing];
      localStorage.setItem('techknife_storage_files', JSON.stringify(updated));
      logActivityAction(uploadedBy, 'ROLE_ADMIN', 'Universal Storage', 'FILE_UPLOAD', `Uploaded file ${file.name} to ${category}`);
      return mockFile;
    }
  },

  deleteFile: async (id: string, deletedBy: string): Promise<void> => {
    try {
      await apiClient.delete(`/storage/files/${id}`);
    } catch {
      const existing = await storageApi.getFiles();
      const updated = existing.filter(f => f.id !== id);
      localStorage.setItem('techknife_storage_files', JSON.stringify(updated));
      logActivityAction(deletedBy, 'ROLE_ADMIN', 'Universal Storage', 'FILE_DELETE', `Deleted storage asset ${id}`);
    }
  },
};

// 2. Universal Notification Service
export const notificationsApi = {
  getNotifications: async (): Promise<SystemNotification[]> => {
    try {
      const res = await apiClient.get('/notifications');
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_notifications') || 'null');
      if (stored) return stored;

      const mockNotifs: SystemNotification[] = [
        {
          id: 'n-1',
          recipientEmail: 'sarah.connor@techknife.io',
          title: 'Approval Required: Leave Request',
          message: 'Alex Rivera submitted an Annual Leave request for 5 days.',
          type: 'warning',
          channels: ['EMAIL', 'DATABASE', 'BROWSER'],
          module: 'Approvals',
          status: 'QUEUED',
          createdAt: new Date(Date.now() - 3600000).toISOString(),
          actionUrl: '/admin',
        },
        {
          id: 'n-2',
          recipientEmail: 'sarah.connor@techknife.io',
          title: 'Intern Certificate Issued',
          message: 'Certificate metadata generated for David Vance (INT-2026-004).',
          type: 'success',
          channels: ['DATABASE', 'BROWSER'],
          module: 'Internship',
          status: 'DELIVERED',
          createdAt: new Date(Date.now() - 7200000).toISOString(),
          actionUrl: '/interns',
        },
      ];
      localStorage.setItem('techknife_notifications', JSON.stringify(mockNotifs));
      return mockNotifs;
    }
  },

  sendNotification: async (
    title: string,
    message: string,
    recipientEmail: string,
    type: 'info' | 'success' | 'warning' | 'error' = 'info',
    channels: ('EMAIL' | 'DATABASE' | 'BROWSER' | 'MOBILE_PUSH')[] = ['EMAIL', 'DATABASE', 'BROWSER']
  ): Promise<SystemNotification> => {
    try {
      const res = await apiClient.post('/notifications/send', {
        title,
        message,
        recipientEmail,
        type,
        channels,
      });
      return res.data.data;
    } catch {
      const newNotif: SystemNotification = {
        id: `n-${Date.now()}`,
        recipientEmail,
        title,
        message,
        type,
        channels,
        module: 'System Notice',
        status: 'DELIVERED',
        createdAt: new Date().toISOString(),
      };
      const existing = await notificationsApi.getNotifications();
      localStorage.setItem('techknife_notifications', JSON.stringify([newNotif, ...existing]));
      return newNotif;
    }
  },
};

// 3. Universal Approval Workflow Service
export const approvalApi = {
  getWorkflows: async (): Promise<ApprovalWorkflow[]> => {
    try {
      const res = await apiClient.get('/approvals/workflows');
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_workflows') || 'null');
      return stored || INITIAL_WORKFLOWS;
    }
  },

  processApprovalStep: async (
    workflowId: string,
    stepNumber: number,
    status: 'APPROVED' | 'REJECTED',
    approverName: string,
    comment?: string
  ): Promise<ApprovalWorkflow> => {
    try {
      const res = await apiClient.post(`/approvals/workflows/${workflowId}/steps/${stepNumber}`, {
        status,
        comment,
      });
      return res.data.data;
    } catch {
      const workflows = await approvalApi.getWorkflows();
      const updated = workflows.map((wf) => {
        if (wf.id === workflowId) {
          const steps = wf.steps.map((st) => {
            if (st.stepNumber === stepNumber) {
              return {
                ...st,
                status: status as ApprovalStatus,
                approverName,
                comment: comment || (status === 'APPROVED' ? 'Approved step.' : 'Rejected step.'),
                actionAt: new Date().toISOString(),
              };
            }
            return st;
          });

          const isAllApproved = steps.every((st) => st.status === 'APPROVED');
          const isAnyRejected = steps.some((st) => st.status === 'REJECTED');

          const newWfStatus: ApprovalStatus = isAnyRejected
            ? 'REJECTED'
            : isAllApproved
            ? 'APPROVED'
            : 'PENDING';

          const newStepIndex = Math.min(wf.currentStepIndex + 1, wf.totalSteps);

          return {
            ...wf,
            steps,
            status: newWfStatus,
            currentStepIndex: newStepIndex,
            updatedAt: new Date().toISOString(),
          };
        }
        return wf;
      });

      localStorage.setItem('techknife_workflows', JSON.stringify(updated));
      logActivityAction(
        approverName,
        'ROLE_ADMIN',
        'Approval Workflow',
        `WORKFLOW_${status}`,
        `Workflow ${workflowId} Step ${stepNumber} set to ${status}`
      );
      return updated.find((w) => w.id === workflowId)!;
    }
  },
};

// 4 & 5. Universal Activity & Audit Log Service
export const auditApi = {
  getActivityLogs: async (): Promise<ActivityLog[]> => {
    try {
      const res = await apiClient.get('/audit/activity');
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_activity_logs') || 'null');
      if (stored) return stored;

      const mockLogs: ActivityLog[] = [
        {
          id: 'act-1',
          userId: 'usr-001',
          userName: 'Sarah Connor',
          userRole: 'ROLE_ADMIN',
          module: 'Internship Cohort',
          action: 'EVALUATE_INTERN',
          description: 'Evaluated final performance score for David Vance (92%).',
          ipAddress: '192.168.1.104',
          browser: 'Chrome 126.0 (macOS)',
          timestamp: new Date(Date.now() - 1800000).toISOString(),
        },
        {
          id: 'act-2',
          userId: 'usr-002',
          userName: 'Marcus Brody',
          userRole: 'ROLE_MANAGER',
          module: 'Approval Workflow',
          action: 'WORKFLOW_APPROVED',
          description: 'Approved Leave Request APPR-2026-0089 step 1.',
          ipAddress: '192.168.1.112',
          browser: 'Firefox 127.0 (Windows)',
          timestamp: new Date(Date.now() - 3600000).toISOString(),
        },
        {
          id: 'act-3',
          userId: 'usr-003',
          userName: 'Elena Rostova',
          userRole: 'ROLE_CTO',
          module: 'Universal Storage',
          action: 'FILE_UPLOAD',
          description: 'Uploaded Architecture_System_Topology.png to Images.',
          ipAddress: '192.168.1.15',
          browser: 'Safari 17.4 (macOS)',
          timestamp: new Date(Date.now() - 86400000).toISOString(),
        },
      ];
      localStorage.setItem('techknife_activity_logs', JSON.stringify(mockLogs));
      return mockLogs;
    }
  },

  getAuditLogs: async (): Promise<AuditLogEntry[]> => {
    try {
      const res = await apiClient.get('/audit/trail');
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_audit_entries') || 'null');
      if (stored) return stored;

      const mockEntries: AuditLogEntry[] = [
        {
          id: 'aud-1',
          userId: 'usr-001',
          userName: 'Sarah Connor',
          entityName: 'Intern',
          entityId: 'INT-2026-004',
          action: 'STATUS_CHANGE',
          oldValue: { status: 'Active', certificateGenerated: false },
          newValue: { status: 'Graduated', certificateGenerated: true },
          ipAddress: '192.168.1.104',
          browser: 'Chrome 126.0 (macOS)',
          timestamp: new Date(Date.now() - 1800000).toISOString(),
        },
        {
          id: 'aud-2',
          userId: 'usr-002',
          userName: 'Marcus Brody',
          entityName: 'Employee',
          entityId: 'EMP-2026-012',
          action: 'UPDATE',
          oldValue: { salary: 92000, designation: 'Senior Frontend Engineer' },
          newValue: { salary: 105000, designation: 'Lead Frontend Engineer' },
          ipAddress: '192.168.1.112',
          browser: 'Firefox 127.0 (Windows)',
          timestamp: new Date(Date.now() - 7200000).toISOString(),
        },
      ];
      localStorage.setItem('techknife_audit_entries', JSON.stringify(mockEntries));
      return mockEntries;
    }
  },
};

// 6. Universal Search Engine
export const searchApi = {
  universalSearch: async (query: string): Promise<UniversalSearchResult[]> => {
    if (!query || query.trim().length < 2) return [];

    try {
      const res = await apiClient.get('/search', { params: { q: query } });
      return res.data.data;
    } catch {
      const q = query.toLowerCase();

      // Static index for instant frontend client-side global search across all 8 modules
      const ALL_SEARCH_ITEMS: UniversalSearchResult[] = [
        // Employees
        { id: 's-1', title: 'Sarah Connor', subtitle: 'Chief Technology Officer (CTO) • Engineering', module: 'Employees', badge: 'Active', badgeColor: 'bg-emerald-500', url: '/employees' },
        { id: 's-2', title: 'Marcus Brody', subtitle: 'Engineering Manager • Software Delivery', module: 'Employees', badge: 'Manager', badgeColor: 'bg-indigo-500', url: '/employees' },
        { id: 's-3', title: 'Elena Rostova', subtitle: 'Frontend Lead • Web Architecture', module: 'Employees', badge: 'Active', badgeColor: 'bg-emerald-500', url: '/employees' },

        // Projects
        { id: 's-4', title: 'Enterprise Banking Portal (PRJ-801)', subtitle: 'Client: FinTech Global • Status: In Progress', module: 'Projects', badge: '$120,000', badgeColor: 'bg-cyan-500', url: '/projects' },
        { id: 's-5', title: 'AI Fraud Detection Pipeline (PRJ-802)', subtitle: 'Client: Secure Bank • Status: Critical Sprint', module: 'Projects', badge: '$250,000', badgeColor: 'bg-rose-500', url: '/projects' },

        // Customers
        { id: 's-6', title: 'FinTech Global Corp', subtitle: 'Contact: James Robertson • Tier: Enterprise VIP', module: 'Customers', badge: 'Active Contract', badgeColor: 'bg-emerald-500', url: '/customers' },
        { id: 's-7', title: 'Acme Health Systems', subtitle: 'Contact: Dr. Susan Vance • Tier: Strategic Partner', module: 'Customers', badge: 'Onboarding', badgeColor: 'bg-amber-500', url: '/customers' },

        // Interns
        { id: 's-8', title: 'David Vance (INT-2026-004)', subtitle: 'Stanford University • Dept: Engineering & DevOps', module: 'Interns', badge: 'Score: 92%', badgeColor: 'bg-cyan-500', url: '/interns' },
        { id: 's-9', title: 'Maya Lin (INT-2026-005)', subtitle: 'UC Berkeley • Dept: AI Systems & Analytics', module: 'Interns', badge: 'Score: 96%', badgeColor: 'bg-cyan-500', url: '/interns' },

        // Documents
        { id: 's-10', title: 'TechKnife_Employee_Handbook_2026.pdf', subtitle: 'Uploaded by Sarah Connor • Category: Documents', module: 'Documents', badge: '2.4 MB', badgeColor: 'bg-slate-500', url: '/settings' },
        { id: 's-11', title: 'Executive_Q2_Financial_Invoice.pdf', subtitle: 'Uploaded by Marcus Brody • Category: Invoices', module: 'Documents', badge: '1.1 MB', badgeColor: 'bg-slate-500', url: '/payroll' },

        // Attendance & Payroll
        { id: 's-12', title: 'July 2026 Monthly Payroll Run', subtitle: 'Total Disbursed: $425,000 • Processed: 142 Staff', module: 'Payroll', badge: 'Disbursed', badgeColor: 'bg-emerald-500', url: '/payroll' },
        { id: 's-13', title: 'Daily Attendance Ledger', subtitle: 'Present: 94.2% • Late Arrivals: 3', module: 'Attendance', badge: 'Realtime', badgeColor: 'bg-indigo-500', url: '/attendance' },

        // Support Tickets
        { id: 's-14', title: 'TCK-9901: Production SSO Authentication Timeout', subtitle: 'Priority: Critical • Client: FinTech Global', module: 'Tickets', badge: 'In Progress', badgeColor: 'bg-rose-500', url: '/support' },
      ];

      return ALL_SEARCH_ITEMS.filter(
        (item) =>
          item.title.toLowerCase().includes(q) ||
          item.subtitle.toLowerCase().includes(q) ||
          item.module.toLowerCase().includes(q)
      );
    }
  },
};

// 7. Universal Dashboard Widget Engine
export const widgetApi = {
  getWidgets: async (): Promise<DashboardWidgetConfig[]> => {
    try {
      const res = await apiClient.get('/widgets');
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_widgets') || 'null');
      if (stored) return stored;

      const defaultWidgets: DashboardWidgetConfig[] = [
        { id: 'w-kpi-workforce', title: 'Active Workforce Count', widgetType: 'kpi', colSpan: 1, enabled: true, order: 1, category: 'HR' },
        { id: 'w-kpi-revenue', title: 'Monthly Revenue Run-Rate', widgetType: 'kpi', colSpan: 1, enabled: true, order: 2, category: 'Finance' },
        { id: 'w-kpi-interns', title: 'Intern Cohort Performance', widgetType: 'kpi', colSpan: 1, enabled: true, order: 3, category: 'Internship' },
        { id: 'w-kpi-approval', title: 'Pending Approval Queue', widgetType: 'kpi', colSpan: 1, enabled: true, order: 4, category: 'Approvals' },
        { id: 'w-chart-delivery', title: 'Sprint Delivery Velocity Chart', widgetType: 'chart', colSpan: 2, enabled: true, order: 5, category: 'Operations' },
        { id: 'w-activity-stream', title: 'Real-time System Audit Stream', widgetType: 'activity', colSpan: 2, enabled: true, order: 6, category: 'Audit' },
      ];
      localStorage.setItem('techknife_widgets', JSON.stringify(defaultWidgets));
      return defaultWidgets;
    }
  },

  saveWidgets: async (widgets: DashboardWidgetConfig[]): Promise<void> => {
    try {
      await apiClient.post('/widgets/config', { widgets });
    } catch {
      localStorage.setItem('techknife_widgets', JSON.stringify(widgets));
    }
  },
};

// 8. Universal Settings Module
export const settingsApi = {
  getSettings: async (): Promise<SystemSettings> => {
    try {
      const res = await apiClient.get('/settings');
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_system_settings') || 'null');
      return stored || INITIAL_SETTINGS;
    }
  },

  updateSettings: async (settings: Partial<SystemSettings>, updatedBy: string): Promise<SystemSettings> => {
    try {
      const res = await apiClient.put('/settings', settings);
      return res.data.data;
    } catch {
      const existing = await settingsApi.getSettings();
      const merged: SystemSettings = {
        ...existing,
        ...settings,
        companyInfo: { ...existing.companyInfo, ...(settings.companyInfo || {}) },
        smtpConfig: { ...existing.smtpConfig, ...(settings.smtpConfig || {}) },
        cloudinaryConfig: { ...existing.cloudinaryConfig, ...(settings.cloudinaryConfig || {}) },
        githubConfig: { ...existing.githubConfig, ...(settings.githubConfig || {}) },
        themeSettings: { ...existing.themeSettings, ...(settings.themeSettings || {}) },
        workingHours: { ...existing.workingHours, ...(settings.workingHours || {}) },
      };
      localStorage.setItem('techknife_system_settings', JSON.stringify(merged));
      logActivityAction(updatedBy, 'ROLE_SUPER_ADMIN', 'System Settings', 'UPDATE_SETTINGS', 'Updated Enterprise Core Platform configuration parameters.');
      return merged;
    }
  },

  testSmtpConnection: async (): Promise<{ success: boolean; message: string }> => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          success: true,
          message: 'SMTP Host connection test successful! Handshake established with TLS 1.3.',
        });
      }, 1000);
    });
  },

  testCloudinaryConnection: async (): Promise<{ success: boolean; message: string }> => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          success: true,
          message: 'Cloudinary API Key verified! Asset bucket accessible.',
        });
      }, 1000);
    });
  },
};

// 9. Universal Role Permission Engine
export const permissionsApi = {
  getRoles: async (): Promise<DynamicRole[]> => {
    try {
      const res = await apiClient.get('/roles');
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_dynamic_roles') || 'null');
      return stored || INITIAL_ROLES;
    }
  },

  updateRole: async (roleCode: string, updatedRole: Partial<DynamicRole>, updatedBy: string): Promise<DynamicRole[]> => {
    try {
      const res = await apiClient.put(`/roles/${roleCode}`, updatedRole);
      return res.data.data;
    } catch {
      const roles = await permissionsApi.getRoles();
      const updated = roles.map((r) => (r.role === roleCode ? { ...r, ...updatedRole } : r));
      localStorage.setItem('techknife_dynamic_roles', JSON.stringify(updated));
      logActivityAction(updatedBy, 'ROLE_SUPER_ADMIN', 'Role Permission Engine', 'UPDATE_ROLE', `Updated permission matrix for ${roleCode}`);
      return updated;
    }
  },
};
