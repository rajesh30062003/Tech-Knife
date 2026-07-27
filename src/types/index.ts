export type Role = 
  | 'ROLE_SUPER_ADMIN'
  | 'ROLE_ADMIN'
  | 'ROLE_CEO'
  | 'ROLE_CTO'
  | 'ROLE_CMO'
  | 'ROLE_MD'
  | 'ROLE_DIRECTOR'
  | 'ROLE_MANAGER'
  | 'ROLE_EMPLOYEE'
  | 'ROLE_INTERN'
  | 'ROLE_CUSTOMER';

export type Permission =
  | 'USER_READ'
  | 'USER_WRITE'
  | 'USER_DELETE'
  | 'PROJECT_READ'
  | 'PROJECT_WRITE'
  | 'PROJECT_DELETE'
  | 'PAYROLL_READ'
  | 'PAYROLL_WRITE'
  | 'CRM_READ'
  | 'CRM_WRITE'
  | 'RECRUITMENT_READ'
  | 'RECRUITMENT_WRITE'
  | 'SYSTEM_ADMIN';

export interface UserProfile {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  designation: string;
  department: string;
  phoneNumber?: string;
  avatarUrl?: string;
  enabled: boolean;
  accountNonLocked: boolean;
  emailVerified: boolean;
  roles: Role[];
  role: Role; // Primary role for legacy compatibility
  permissions?: Permission[];
  lastLoginAt?: string;
  createdAt?: string;
  updatedAt?: string;
  emergencyContact?: string;
  address?: string;
  joinDate?: string;
  salary?: number;
  managerId?: string;
  managerName?: string;
  managerDesignation?: string;
  bio?: string;
  skills?: string[];
}

export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  emergencyContact?: string;
  address?: string;
  bio?: string;
  skills?: string[];
  avatarUrl?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface AuthResponse {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: Role[];
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInMs: number;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  designation?: string;
  department?: string;
  phoneNumber?: string;
  roles?: Role[];
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  email: string;
  otpCode: string;
  newPassword: string;
}

export interface SendOtpRequest {
  email: string;
  type: 'EMAIL_VERIFICATION' | 'PASSWORD_RESET';
}

export interface VerifyOtpRequest {
  email: string;
  otpCode: string;
  type: 'EMAIL_VERIFICATION' | 'PASSWORD_RESET';
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ProfilePictureRequest {
  avatarUrl: string;
}

export interface NavItem {
  title: string;
  path: string;
  icon: string;
  roles: Role[];
  badge?: string;
  category?: 'Core' | 'Operations' | 'Human Capital' | 'Growth' | 'System' | 'Governance' | 'Client Space' | 'Execution' | 'Support' | 'Enterprise Core';
}

export interface Project {
  id: string;
  name: string;
  code: string;
  client: string;
  status: 'Planning' | 'In Progress' | 'On Hold' | 'Completed' | 'Critical';
  progress: number;
  deadline: string;
  budget: string;
  teamCount: number;
  lead: string;
}

export interface Task {
  id: string;
  title: string;
  projectName: string;
  priority: 'Low' | 'Medium' | 'High' | 'Urgent';
  status: 'Backlog' | 'In Progress' | 'Code Review' | 'Completed';
  assignee: string;
  dueDate: string;
}

export type AttendanceStatusType = 'PRESENT' | 'ABSENT' | 'LATE' | 'HALF_DAY' | 'HOLIDAY' | 'LEAVE' | 'WEEKEND' | 'WFH' | 'On Time' | 'Late' | 'Half Day' | 'Absent';
export type PunchType = 'CHECK_IN' | 'CHECK_OUT' | 'BREAK_START' | 'BREAK_END';

export interface PunchLog {
  punchType: PunchType;
  timestamp: string;
  location?: string;
  ipAddress?: string;
  notes?: string;
  editedByAdmin?: boolean;
  editedReason?: string;
}

export interface AttendanceRecord {
  id: string;
  userId: string;
  userEmail: string;
  userName: string;
  department: string;
  date: string;
  status: AttendanceStatusType;
  checkInTime?: string;
  checkOutTime?: string;
  clockIn?: string;
  clockOut?: string;
  totalHours?: string;
  location?: string;
  totalWorkMinutes: number;
  totalBreakMinutes: number;
  overtimeMinutes: number;
  isLateArrival: boolean;
  isEarlyLeaving: boolean;
  isHalfDay: boolean;
  isWorkFromHome: boolean;
  isHoliday: boolean;
  isWeekend: boolean;
  remarks?: string;
  locationIn?: string;
  locationOut?: string;
  ipAddress?: string;
  punches: PunchLog[];
  correctedByAdmin?: boolean;
  correctionReason?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface LeaveRequest {
  id: string;
  employeeName: string;
  type: 'Annual' | 'Sick' | 'Casual' | 'Maternity/Paternity' | 'Unpaid';
  startDate: string;
  endDate: string;
  days: number;
  reason: string;
  status: 'Pending' | 'Approved' | 'Rejected';
}

export interface Lead {
  id: string;
  companyName: string;
  contactPerson: string;
  email: string;
  value: string;
  stage: 'Lead' | 'Contacted' | 'Proposal' | 'Negotiation' | 'Closed Won' | 'Closed Lost';
  probability: number;
}

export interface SalarySlip {
  id: string;
  monthYear: string;
  basicSalary: number;
  allowances: number;
  deductions: number;
  netPay: number;
  status: 'Processed' | 'Pending' | 'Disbursed';
  payDate: string;
}

export interface Candidate {
  id: string;
  name: string;
  position: string;
  experience: string;
  stage: 'Applied' | 'Screening' | 'Technical Round' | 'HR Round' | 'Offered' | 'Hired';
  rating: number;
  appliedDate: string;
}

export interface Ticket {
  id: string;
  ticketNumber: string;
  subject: string;
  category: 'Bug' | 'Feature Request' | 'Billing' | 'Access Issue' | 'Infrastructure';
  priority: 'Low' | 'Medium' | 'High' | 'Critical';
  status: 'Open' | 'In Progress' | 'Waiting Customer' | 'Resolved';
  createdAt: string;
  client: string;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error';
  timestamp: string;
  read: boolean;
  link?: string;
}

export type InternStatus = 'Active' | 'On Review' | 'Graduated' | 'Suspended' | 'Converted to Employee';

export interface InternTask {
  id: string;
  title: string;
  type: 'daily' | 'weekly';
  dueDate: string;
  status: 'Pending' | 'Submitted' | 'Reviewed' | 'Approved';
  score?: number;
  feedback?: string;
}

export interface InternDocument {
  id: string;
  type: 'Resume' | 'Offer Letter' | 'Certificate' | 'Evaluation';
  name: string;
  url: string;
  uploadedAt: string;
}

export interface Intern {
  id: string;
  internId: string;
  firstName: string;
  lastName: string;
  officialEmail: string;
  personalEmail: string;
  primaryMobile: string;
  alternateMobile?: string;
  college: string;
  university: string;
  degree: string;
  branch: string;
  semester: string;
  cgpa: number;
  resumeUrl?: string;
  offerLetterUrl?: string;
  joiningDate: string;
  endDate: string;
  mentor: string;
  mentorId?: string;
  department: string;
  skills: string[];
  githubUsername?: string;
  performanceScore: number;
  attendance: number;
  status: InternStatus;
  certificateGenerated: boolean;
  certificateUrl?: string;
  assignedProjects?: string[];
  dailyTasks?: InternTask[];
  weeklyTasks?: InternTask[];
  finalEvaluation?: {
    technicalRating: number;
    softSkillsRating: number;
    codeQualityRating: number;
    overallFeedback: string;
    ppoRecommendation: boolean;
  };
  stipend?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface InternStats {
  totalInterns: number;
  activeCount: number;
  graduatedCount: number;
  suspendedCount: number;
  averagePerformanceScore: number;
  ppoConversionRate: number;
  certificatesIssuedCount: number;
}

export * from './core';
export * from './faculty';

export interface AttendanceSummary {
  userId: string;
  userName: string;
  department: string;
  periodYear: number;
  periodMonth?: number;
  totalDays: number;
  presentDays: number;
  absentDays: number;
  lateDays: number;
  halfDays: number;
  wfhDays: number;
  holidayDays: number;
  weekendDays: number;
  leaveDays: number;
  totalWorkingHours: number;
  totalOvertimeHours: number;
  averageDailyHours: number;
  attendancePercentage: number;
  statusBreakdown?: Record<string, number>;
}


