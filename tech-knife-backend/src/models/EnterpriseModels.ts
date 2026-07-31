import mongoose, { Schema, Document } from 'mongoose';

// Attendance
export interface IAttendance extends Document {
  attendanceId: string;
  userId: string;
  userEmail: string;
  userName: string;
  department: string;
  date: string;
  status: string;
  checkInTime?: string;
  checkOutTime?: string;
  clockIn?: string;
  clockOut?: string;
  totalHours?: string;
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
  punches: any[];
}

const AttendanceSchema = new Schema<IAttendance>(
  {
    attendanceId: { type: String, required: true, unique: true, index: true },
    userId: { type: String, required: true, index: true },
    userEmail: { type: String, required: true },
    userName: { type: String, required: true },
    department: { type: String, required: true },
    date: { type: String, required: true, index: true },
    status: { type: String, required: true },
    checkInTime: String,
    checkOutTime: String,
    clockIn: String,
    clockOut: String,
    totalHours: String,
    totalWorkMinutes: { type: Number, default: 480 },
    totalBreakMinutes: { type: Number, default: 60 },
    overtimeMinutes: { type: Number, default: 0 },
    isLateArrival: { type: Boolean, default: false },
    isEarlyLeaving: { type: Boolean, default: false },
    isHalfDay: { type: Boolean, default: false },
    isWorkFromHome: { type: Boolean, default: false },
    isHoliday: { type: Boolean, default: false },
    isWeekend: { type: Boolean, default: false },
    remarks: String,
    punches: [Schema.Types.Mixed],
  },
  { timestamps: true, collection: 'attendance' }
);

export const Attendance = mongoose.model<IAttendance>('Attendance', AttendanceSchema);

// Salary
export interface ISalary extends Document {
  salaryId: string;
  employeeId: string;
  employeeName: string;
  monthYear: string;
  basicSalary: number;
  hra: number;
  allowances: number;
  bonus: number;
  deductions: number;
  tax: number;
  netPay: number;
  status: string;
  payDate: string;
}

const SalarySchema = new Schema<ISalary>(
  {
    salaryId: { type: String, required: true, unique: true },
    employeeId: { type: String, required: true, index: true },
    employeeName: { type: String, required: true },
    monthYear: { type: String, required: true },
    basicSalary: { type: Number, required: true },
    hra: { type: Number, default: 0 },
    allowances: { type: Number, default: 0 },
    bonus: { type: Number, default: 0 },
    deductions: { type: Number, default: 0 },
    tax: { type: Number, default: 0 },
    netPay: { type: Number, required: true },
    status: { type: String, default: 'Disbursed' },
    payDate: { type: String, required: true },
  },
  { timestamps: true, collection: 'salary' }
);

export const Salary = mongoose.model<ISalary>('Salary', SalarySchema);

// BankAccount
export interface IBankAccount extends Document {
  accountId: string;
  employeeId: string;
  bankName: string;
  branchName: string;
  accountHolderName: string;
  accountNumber: string;
  ifscCode: string;
  upiId?: string;
  isPrimary: boolean;
}

const BankAccountSchema = new Schema<IBankAccount>(
  {
    accountId: { type: String, required: true, unique: true },
    employeeId: { type: String, required: true, index: true },
    bankName: { type: String, required: true },
    branchName: { type: String, required: true },
    accountHolderName: { type: String, required: true },
    accountNumber: { type: String, required: true },
    ifscCode: { type: String, required: true },
    upiId: String,
    isPrimary: { type: Boolean, default: true },
  },
  { timestamps: true, collection: 'bankAccounts' }
);

export const BankAccount = mongoose.model<IBankAccount>('BankAccount', BankAccountSchema);

// LeaveRequest
export interface ILeaveRequest extends Document {
  requestId: string;
  employeeId: string;
  employeeName: string;
  type: string;
  startDate: string;
  endDate: string;
  days: number;
  reason: string;
  status: 'Pending' | 'Approved' | 'Rejected';
  approvedBy?: string;
}

const LeaveRequestSchema = new Schema<ILeaveRequest>(
  {
    requestId: { type: String, required: true, unique: true },
    employeeId: { type: String, required: true, index: true },
    employeeName: { type: String, required: true },
    type: { type: String, required: true },
    startDate: { type: String, required: true },
    endDate: { type: String, required: true },
    days: { type: Number, required: true },
    reason: { type: String, required: true },
    status: { type: String, default: 'Pending', enum: ['Pending', 'Approved', 'Rejected'] },
    approvedBy: String,
  },
  { timestamps: true, collection: 'leaveRequests' }
);

export const LeaveRequest = mongoose.model<ILeaveRequest>('LeaveRequest', LeaveRequestSchema);

// Project
export interface IProject extends Document {
  projectId: string;
  name: string;
  code: string;
  client: string;
  status: string;
  progress: number;
  deadline: string;
  budget: string;
  teamCount: number;
  lead: string;
}

const ProjectSchema = new Schema<IProject>(
  {
    projectId: { type: String, required: true, unique: true },
    name: { type: String, required: true },
    code: { type: String, required: true },
    client: { type: String, required: true },
    status: { type: String, default: 'In Progress' },
    progress: { type: Number, default: 0 },
    deadline: { type: String, required: true },
    budget: { type: String, default: '$50,000' },
    teamCount: { type: Number, default: 5 },
    lead: { type: String, required: true },
  },
  { timestamps: true, collection: 'projects' }
);

export const Project = mongoose.model<IProject>('Project', ProjectSchema);

// Task
export interface ITask extends Document {
  taskId: string;
  title: string;
  projectName: string;
  priority: string;
  status: string;
  assignee: string;
  dueDate: string;
}

const TaskSchema = new Schema<ITask>(
  {
    taskId: { type: String, required: true, unique: true },
    title: { type: String, required: true },
    projectName: { type: String, required: true },
    priority: { type: String, default: 'Medium' },
    status: { type: String, default: 'In Progress' },
    assignee: { type: String, required: true },
    dueDate: { type: String, required: true },
  },
  { timestamps: true, collection: 'tasks' }
);

export const Task = mongoose.model<ITask>('Task', TaskSchema);

// Announcement
export interface IAnnouncement extends Document {
  announcementId: string;
  title: string;
  content: string;
  author: string;
  category: string;
  pinned: boolean;
}

const AnnouncementSchema = new Schema<IAnnouncement>(
  {
    announcementId: { type: String, required: true, unique: true },
    title: { type: String, required: true },
    content: { type: String, required: true },
    author: { type: String, required: true },
    category: { type: String, default: 'General' },
    pinned: { type: Boolean, default: false },
  },
  { timestamps: true, collection: 'announcements' }
);

export const Announcement = mongoose.model<IAnnouncement>('Announcement', AnnouncementSchema);

// Notification
export interface INotification extends Document {
  notificationId: string;
  userId: string;
  title: string;
  message: string;
  type: string;
  read: boolean;
  link?: string;
}

const NotificationSchema = new Schema<INotification>(
  {
    notificationId: { type: String, required: true, unique: true },
    userId: { type: String, required: true, index: true },
    title: { type: String, required: true },
    message: { type: String, required: true },
    type: { type: String, default: 'info' },
    read: { type: Boolean, default: false },
    link: String,
  },
  { timestamps: true, collection: 'notifications' }
);

export const Notification = mongoose.model<INotification>('Notification', NotificationSchema);

// Setting
export interface ISetting extends Document {
  key: string;
  value: any;
  description?: string;
}

const SettingSchema = new Schema<ISetting>(
  {
    key: { type: String, required: true, unique: true },
    value: { type: Schema.Types.Mixed, required: true },
    description: String,
  },
  { timestamps: true, collection: 'settings' }
);

export const Setting = mongoose.model<ISetting>('Setting', SettingSchema);
