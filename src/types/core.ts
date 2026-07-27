import { Role } from './index';

// 1. Universal File Storage Types
export type FileCategory = 
  | 'Documents' 
  | 'Images' 
  | 'Videos' 
  | 'Certificates' 
  | 'Invoices' 
  | 'Payslips' 
  | 'Employee Photos' 
  | 'Customer Documents' 
  | 'Project Files';

export interface StorageFile {
  id: string;
  name: string;
  category: FileCategory;
  url: string;
  publicId: string;
  fileSize: number; // in bytes
  format: string;
  uploadedBy: string;
  uploadedByEmail: string;
  module: string;
  isPrivate: boolean;
  createdAt: string;
}

// 2. Universal Notification Types
export type NotificationChannel = 'EMAIL' | 'DATABASE' | 'BROWSER' | 'MOBILE_PUSH';

export interface NotificationTemplate {
  id: string;
  code: string;
  name: string;
  channels: NotificationChannel[];
  subjectTemplate: string;
  bodyTemplate: string;
  description: string;
  updatedAt: string;
}

export interface SystemNotification {
  id: string;
  recipientEmail: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error';
  channels: NotificationChannel[];
  module: string;
  status: 'QUEUED' | 'DELIVERED' | 'FAILED' | 'READ';
  readAt?: string;
  createdAt: string;
  actionUrl?: string;
}

// 3. Universal Approval Workflow Types
export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

export interface ApprovalStep {
  stepNumber: number;
  stepName: string;
  approverRole: Role;
  approverEmail?: string;
  approverName?: string;
  status: ApprovalStatus;
  comment?: string;
  actionAt?: string;
}

export interface ApprovalWorkflow {
  id: string;
  workflowNumber: string;
  title: string;
  module: 'LEAVE' | 'EXPENSE' | 'PAYROLL' | 'ONBOARDING' | 'DOCUMENT' | 'PURCHASE_ORDER';
  requesterName: string;
  requesterEmail: string;
  currentStepIndex: number;
  totalSteps: number;
  status: ApprovalStatus;
  payload: Record<string, any>;
  steps: ApprovalStep[];
  createdAt: string;
  updatedAt: string;
}

// 4. Universal Activity & Audit Log Types
export interface ActivityLog {
  id: string;
  userId: string;
  userName: string;
  userRole: Role;
  module: string;
  action: string;
  description: string;
  ipAddress: string;
  browser: string;
  timestamp: string;
}

export interface AuditLogEntry {
  id: string;
  userId: string;
  userName: string;
  entityName: string;
  entityId: string;
  action: 'CREATE' | 'UPDATE' | 'DELETE' | 'STATUS_CHANGE';
  oldValue: Record<string, any> | null;
  newValue: Record<string, any> | null;
  ipAddress: string;
  browser: string;
  timestamp: string;
}

// 5. Universal Search Engine Types
export type SearchModuleType = 'Employees' | 'Projects' | 'Customers' | 'Interns' | 'Documents' | 'Attendance' | 'Payroll' | 'Tickets';

export interface UniversalSearchResult {
  id: string;
  title: string;
  subtitle: string;
  module: SearchModuleType;
  badge: string;
  badgeColor?: string;
  url: string;
  snippet?: string;
  metadata?: Record<string, any>;
}

// 6. Universal Dashboard Widget Engine Types
export type WidgetType = 'kpi' | 'chart' | 'activity' | 'shortcut' | 'table' | 'quick_action';

export interface DashboardWidgetConfig {
  id: string;
  title: string;
  widgetType: WidgetType;
  colSpan: 1 | 2 | 3 | 4;
  enabled: boolean;
  order: number;
  category: string;
  dataUrl?: string;
}

// 7. Universal Report Engine Types
export type ReportFormat = 'PDF' | 'EXCEL' | 'CSV';

export interface ReportConfig {
  module: string;
  title: string;
  format: ReportFormat;
  dateFrom?: string;
  dateTo?: string;
  filters?: Record<string, any>;
}

// 8. Universal Settings Types
export interface SystemSettings {
  companyInfo: {
    name: string;
    taxId: string;
    contactEmail: string;
    supportPhone: string;
    website: string;
    address: string;
    logoUrl: string;
  };
  smtpConfig: {
    host: string;
    port: number;
    username: string;
    sslEnabled: boolean;
    senderName: string;
    senderEmail: string;
  };
  cloudinaryConfig: {
    cloudName: string;
    apiKey: string;
    defaultFolder: string;
    enabled: boolean;
  };
  githubConfig: {
    organization: string;
    enabled: boolean;
    webhookUrl: string;
  };
  themeSettings: {
    primaryColor: string;
    defaultMode: 'light' | 'dark' | 'system';
    compactDensity: boolean;
  };
  workingHours: {
    startTime: string;
    endTime: string;
    timezone: string;
    workDays: string[];
  };
  holidayCalendar: Array<{
    id: string;
    name: string;
    date: string;
    type: 'National' | 'Corporate' | 'Optional';
  }>;
}

// 9. Universal Role Permission Types
export interface DynamicRole {
  role: Role;
  displayName: string;
  description: string;
  isSystem: boolean;
  permissions: string[];
  menuPermissions: string[];
  apiPermissions: string[];
  featureFlags: Record<string, boolean>;
}
