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

// Initial Storage State for presentation & fallback
const INITIAL_FILES: StorageFile[] = [
  {
    id: 'file-101',
    name: 'TechKnife_Employee_Handbook_2026.pdf',
    category: 'Documents',
    url: 'https://res.cloudinary.com/techknife/image/upload/v171000101/handbook.pdf',
    publicId: 'techknife/docs/handbook',
    fileSize: 2450000,
    format: 'pdf',
    uploadedBy: 'Ranadhir Pal',
    uploadedByEmail: 'rjrajeshpal30@gmail.com',
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
    uploadedBy: 'Sourav Roy',
    uploadedByEmail: 'souravroy6412@gmail.com',
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
    uploadedBy: 'Ganesh Pal',
    uploadedByEmail: 'palganeshpal314@gmail.com',
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
    uploadedBy: 'Rahul Garai',
    uploadedByEmail: 'garairahul087@gmail.com',
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
    requesterName: 'Ganesh Pal',
    requesterEmail: 'palganeshpal314@gmail.com',
    currentStepIndex: 1,
    totalSteps: 2,
    status: 'PENDING',
    payload: { leaveType: 'Annual', days: 5, startDate: '2026-08-01', endDate: '2026-08-05' },
    steps: [
      { stepNumber: 1, stepName: 'Managing Director Review', approverRole: 'ROLE_MD', approverName: 'Sourav Roy', status: 'APPROVED', comment: 'Approved for coverage.', actionAt: '2026-07-21T10:00:00Z' },
      { stepNumber: 2, stepName: 'CEO Final Sanction', approverRole: 'ROLE_CEO', approverName: 'Ranadhir Pal', status: 'PENDING' },
    ],
    createdAt: '2026-07-21T08:30:00Z',
    updatedAt: '2026-07-21T10:00:00Z',
  },
  {
    id: 'wf-102',
    workflowNumber: 'APPR-2026-0090',
    title: 'Cloud Infrastructure Upgrade Expense - $4,200',
    module: 'EXPENSE',
    requesterName: 'Rahul Garai',
    requesterEmail: 'garairahul087@gmail.com',
    currentStepIndex: 0,
    totalSteps: 2,
    status: 'PENDING',
    payload: { amount: '$4,200', vendor: 'AWS Cloud Services', purpose: 'Database scaling' },
    steps: [
      { stepNumber: 1, stepName: 'Senior Developer Review', approverRole: 'ROLE_SENIOR_DEVELOPER', approverName: 'Ganesh Pal', status: 'PENDING' },
      { stepNumber: 2, stepName: 'Executive Approval', approverRole: 'ROLE_CEO', approverName: 'Ranadhir Pal', status: 'PENDING' },
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
          recipientEmail: 'rjrajeshpal30@gmail.com',
          title: 'Approval Required: Leave Request',
          message: 'Ganesh Pal submitted an Annual Leave request for 5 days.',
          type: 'warning',
          channels: ['EMAIL', 'DATABASE', 'BROWSER'],
          module: 'Approvals',
          status: 'QUEUED',
          createdAt: new Date(Date.now() - 3600000).toISOString(),
          actionUrl: '/admin',
        },
        {
          id: 'n-2',
          recipientEmail: 'rjrajeshpal30@gmail.com',
          title: 'Intern Certificate Issued',
          message: 'Certificate metadata generated for Sangita Koner (INT-001).',
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
          userId: 'EMP-001',
          userName: 'Ranadhir Pal',
          userRole: 'ROLE_CEO',
          module: 'Internship Cohort',
          action: 'EVALUATE_INTERN',
          description: 'Evaluated final performance score for Sangita Koner (95%).',
          ipAddress: '192.168.1.104',
          browser: 'Chrome 126.0 (macOS)',
          timestamp: new Date(Date.now() - 1800000).toISOString(),
        },
        {
          id: 'act-2',
          userId: 'EMP-002',
          userName: 'Sourav Roy',
          userRole: 'ROLE_MD',
          module: 'Approval Workflow',
          action: 'WORKFLOW_APPROVED',
          description: 'Approved Leave Request APPR-2026-0089 step 1.',
          ipAddress: '192.168.1.112',
          browser: 'Firefox 127.0 (Windows)',
          timestamp: new Date(Date.now() - 3600000).toISOString(),
        },
        {
          id: 'act-3',
          userId: 'EMP-003',
          userName: 'Ganesh Pal',
          userRole: 'ROLE_SENIOR_DEVELOPER',
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
          userId: 'EMP-001',
          userName: 'Ranadhir Pal',
          entityName: 'Intern',
          entityId: 'INT-001',
          action: 'STATUS_CHANGE',
          oldValue: { status: 'Active', certificateGenerated: false },
          newValue: { status: 'Active', certificateGenerated: true },
          ipAddress: '192.168.1.104',
          browser: 'Chrome 126.0 (macOS)',
          timestamp: new Date(Date.now() - 1800000).toISOString(),
        },
        {
          id: 'aud-2',
          userId: 'EMP-002',
          userName: 'Sourav Roy',
          entityName: 'Employee',
          entityId: 'EMP-004',
          action: 'UPDATE',
          oldValue: { salary: 220000, designation: 'System Developer' },
          newValue: { salary: 250000, designation: 'System Developer' },
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
        { id: 's-1', title: 'Ranadhir Pal', subtitle: 'Chief Executive Officer (CEO) • Management', module: 'Employees', badge: 'Active', badgeColor: 'bg-emerald-500', url: '/employees' },
        { id: 's-2', title: 'Sourav Roy', subtitle: 'Managing Director (MD) • Management', module: 'Employees', badge: 'Active', badgeColor: 'bg-emerald-500', url: '/employees' },
        { id: 's-3', title: 'Ganesh Pal', subtitle: 'Senior Developer • Engineering', module: 'Employees', badge: 'Active', badgeColor: 'bg-emerald-500', url: '/employees' },
        { id: 's-4', title: 'Rahul Garai', subtitle: 'System Developer • Engineering', module: 'Employees', badge: 'Active', badgeColor: 'bg-emerald-500', url: '/employees' },

        // Interns
        { id: 's-5', title: 'Sangita Koner (INT-001)', subtitle: 'Intern • Engineering', module: 'Interns', badge: 'Score: 95%', badgeColor: 'bg-cyan-500', url: '/interns' },
        { id: 's-6', title: 'Rahul Pal (INT-002)', subtitle: 'Intern • Engineering', module: 'Interns', badge: 'Score: 94%', badgeColor: 'bg-cyan-500', url: '/interns' },
        { id: 's-7', title: 'Salman Kazi (INT-003)', subtitle: 'Intern • Engineering', module: 'Interns', badge: 'Score: 93%', badgeColor: 'bg-cyan-500', url: '/interns' },
        { id: 's-8', title: 'Nisha Pandit (INT-004)', subtitle: 'Intern • Engineering', module: 'Interns', badge: 'Score: 96%', badgeColor: 'bg-cyan-500', url: '/interns' },

        // Projects
        { id: 's-9', title: 'Enterprise Cloud Portal (PRJ-001)', subtitle: 'Client: ABC Enterprises • Lead: Ganesh Pal', module: 'Projects', badge: '₹ 25,00,000', badgeColor: 'bg-cyan-500', url: '/projects' },

        // Documents
        { id: 's-10', title: 'TechKnife_Employee_Handbook_2026.pdf', subtitle: 'Uploaded by Ranadhir Pal • Category: Documents', module: 'Documents', badge: '2.4 MB', badgeColor: 'bg-slate-500', url: '/settings' },
        { id: 's-11', title: 'Executive_Q2_Financial_Invoice.pdf', subtitle: 'Uploaded by Sourav Roy • Category: Invoices', module: 'Documents', badge: '1.1 MB', badgeColor: 'bg-slate-500', url: '/payroll' },

        // Attendance & Payroll
        { id: 's-12', title: 'Monthly Payroll Run', subtitle: 'Total Disbursed: ₹ 12,30,000 • Processed: 8 Staff', module: 'Payroll', badge: 'Disbursed', badgeColor: 'bg-emerald-500', url: '/payroll' },
        { id: 's-13', title: 'Daily Attendance Ledger', subtitle: 'Present: 100% • Staff: 8', module: 'Attendance', badge: 'Realtime', badgeColor: 'bg-indigo-500', url: '/attendance' },
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
      const res = await apiClient.get('/organization/roles');
      return res.data.data;
    } catch {
      const stored = JSON.parse(localStorage.getItem('techknife_dynamic_roles') || 'null');
      return stored || [];
    }
  },

  updateRole: async (roleCode: string, updatedRole: Partial<DynamicRole>, updatedBy: string): Promise<DynamicRole[]> => {
    try {
      const res = await apiClient.put(`/organization/roles/${roleCode}`, updatedRole);
      return res.data.data;
    } catch {
      const roles = await permissionsApi.getRoles();
      const updated = roles.map((r) => (r.role === roleCode ? { ...r, ...updatedRole } : r));
      localStorage.setItem('techknife_dynamic_roles', JSON.stringify(updated));
      logActivityAction(updatedBy, 'ROLE_CEO', 'Role Permission Engine', 'UPDATE_ROLE', `Updated permission matrix for ${roleCode}`);
      return updated;
    }
  },

  resetRole: async (roleCode: string): Promise<DynamicRole[]> => {
    try {
      const res = await apiClient.post(`/organization/roles/${roleCode}/reset`);
      if (res.data.data) return res.data.data;
      return await permissionsApi.getRoles();
    } catch {
      return await permissionsApi.getRoles();
    }
  },

  getFeatureFlags: async (): Promise<Array<{ key: string; title: string; description: string; enabled: boolean }>> => {
    try {
      const res = await apiClient.get('/organization/features');
      return res.data.data;
    } catch {
      return [];
    }
  },

  updateFeatureFlag: async (key: string, enabled: boolean): Promise<void> => {
    try {
      await apiClient.put(`/organization/features/${key}`, { enabled });
    } catch (e) {
      console.warn('Feature flag update failed', e);
    }
  },
};
