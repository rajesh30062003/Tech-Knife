import bcrypt from 'bcryptjs';
import { User } from '../models/User';
import { Employee } from '../models/Employee';
import { Customer } from '../models/Customer';
import { RoleModel, DepartmentModel, PermissionModel, FeatureFlagModel, RoutePermissionModel } from '../models/RoleAndDept';
import {
  Attendance,
  Salary,
  BankAccount,
  LeaveRequest,
  Project,
  Task,
  Announcement,
  Notification,
  Setting,
} from '../models/EnterpriseModels';
import { generateDatabaseBackups } from './backupService';

export const AUTHORITATIVE_STAFF_EMAILS = [
  'rjrajeshpal30@gmail.com',
  'souravroy6412@gmail.com',
  'palganeshpal314@gmail.com',
  'garairahul087@gmail.com',
  'sangitakoner455@gmail.com',
  'rahulpal01102002@gmail.com',
  'salmankazi1603@gmail.com',
  'nishapanditbwn@gmail.com',
];

export const ALL_PERMISSIONS_CATALOG = [
  { code: 'USER_READ', name: 'View Users', module: 'User Management', category: 'Security' },
  { code: 'USER_CREATE', name: 'Create Users', module: 'User Management', category: 'Security' },
  { code: 'USER_WRITE', name: 'Edit Users', module: 'User Management', category: 'Security' },
  { code: 'USER_DELETE', name: 'Delete Users', module: 'User Management', category: 'Security' },

  { code: 'EMPLOYEE_READ', name: 'View Employees', module: 'Employees', category: 'HR' },
  { code: 'EMPLOYEE_CREATE', name: 'Add Employee', module: 'Employees', category: 'HR' },
  { code: 'EMPLOYEE_WRITE', name: 'Edit Employee', module: 'Employees', category: 'HR' },
  { code: 'EMPLOYEE_DELETE', name: 'Remove Employee', module: 'Employees', category: 'HR' },

  { code: 'INTERN_READ', name: 'View Interns', module: 'Interns', category: 'HR' },
  { code: 'INTERN_CREATE', name: 'Add Intern', module: 'Interns', category: 'HR' },
  { code: 'INTERN_WRITE', name: 'Edit Intern', module: 'Interns', category: 'HR' },
  { code: 'INTERN_DELETE', name: 'Remove Intern', module: 'Interns', category: 'HR' },

  { code: 'ROLE_READ', name: 'View Roles & Matrix', module: 'Roles & Permissions', category: 'Security' },
  { code: 'ROLE_WRITE', name: 'Edit Roles & Matrix', module: 'Roles & Permissions', category: 'Security' },
  { code: 'ROLE_DELETE', name: 'Delete Roles', module: 'Roles & Permissions', category: 'Security' },

  { code: 'PERMISSION_READ', name: 'View Permission Catalog', module: 'Roles & Permissions', category: 'Security' },
  { code: 'PERMISSION_WRITE', name: 'Modify Permission Catalog', module: 'Roles & Permissions', category: 'Security' },

  { code: 'PROJECT_READ', name: 'View Projects', module: 'Projects', category: 'Engineering' },
  { code: 'PROJECT_CREATE', name: 'Create Project', module: 'Projects', category: 'Engineering' },
  { code: 'PROJECT_WRITE', name: 'Edit Project', module: 'Projects', category: 'Engineering' },
  { code: 'PROJECT_DELETE', name: 'Delete Project', module: 'Projects', category: 'Engineering' },

  { code: 'TASK_READ', name: 'View Tasks', module: 'Tasks', category: 'Engineering' },
  { code: 'TASK_CREATE', name: 'Create Task', module: 'Tasks', category: 'Engineering' },
  { code: 'TASK_WRITE', name: 'Edit Task', module: 'Tasks', category: 'Engineering' },
  { code: 'TASK_DELETE', name: 'Delete Task', module: 'Tasks', category: 'Engineering' },
  { code: 'TASK_ASSIGN', name: 'Assign Task', module: 'Tasks', category: 'Engineering' },

  { code: 'DOCUMENT_READ', name: 'View Documents', module: 'Vault & Documents', category: 'Storage' },
  { code: 'DOCUMENT_UPLOAD', name: 'Upload Document', module: 'Vault & Documents', category: 'Storage' },
  { code: 'DOCUMENT_WRITE', name: 'Edit Document', module: 'Vault & Documents', category: 'Storage' },
  { code: 'DOCUMENT_DELETE', name: 'Delete Document', module: 'Vault & Documents', category: 'Storage' },
  { code: 'DOCUMENT_DOWNLOAD', name: 'Download Document', module: 'Vault & Documents', category: 'Storage' },

  { code: 'REPOSITORY_READ', name: 'View Repositories', module: 'Engineering', category: 'Development' },
  { code: 'REPOSITORY_CREATE', name: 'Create Repository', module: 'Engineering', category: 'Development' },
  { code: 'REPOSITORY_WRITE', name: 'Commit / Push Repository', module: 'Engineering', category: 'Development' },
  { code: 'REPOSITORY_DELETE', name: 'Delete Repository', module: 'Engineering', category: 'Development' },

  { code: 'MEETING_READ', name: 'View Meetings', module: 'Collaboration', category: 'Events' },
  { code: 'MEETING_CREATE', name: 'Schedule Meeting', module: 'Collaboration', category: 'Events' },
  { code: 'MEETING_WRITE', name: 'Edit Meeting', module: 'Collaboration', category: 'Events' },
  { code: 'MEETING_DELETE', name: 'Cancel Meeting', module: 'Collaboration', category: 'Events' },

  { code: 'ACTIVITY_READ', name: 'View Activity Logs', module: 'Audit & Compliance', category: 'Security' },
  { code: 'AUDIT_LOG_READ', name: 'View Audit Trail', module: 'Audit & Compliance', category: 'Security' },

  { code: 'REPORT_READ', name: 'View Executive Reports', module: 'Analytics', category: 'Reports' },
  { code: 'REPORT_EXPORT', name: 'Export Reports', module: 'Analytics', category: 'Reports' },

  { code: 'APPROVAL_READ', name: 'View Approvals', module: 'Approvals', category: 'Governance' },
  { code: 'APPROVAL_CREATE', name: 'Request Approval', module: 'Approvals', category: 'Governance' },
  { code: 'APPROVAL_APPROVE', name: 'Approve Request', module: 'Approvals', category: 'Governance' },
  { code: 'APPROVAL_REJECT', name: 'Reject Request', module: 'Approvals', category: 'Governance' },

  { code: 'SETTINGS_READ', name: 'View System Settings', module: 'System', category: 'Core' },
  { code: 'SETTINGS_WRITE', name: 'Modify System Settings', module: 'System', category: 'Core' },

  { code: 'DASHBOARD_READ', name: 'Access Core Dashboard', module: 'Dashboard', category: 'Core' },
];

export const ALL_MENU_ROUTES = [
  '/dashboard',
  '/admin',
  '/manager',
  '/employee',
  '/intern',
  '/customer',
  '/employees',
  '/interns',
  '/customers',
  '/projects',
  '/tasks',
  '/repositories',
  '/documents',
  '/meetings',
  '/reports',
  '/payroll',
  '/attendance',
  '/leave',
  '/crm',
  '/recruitment',
  '/audit-logs',
  '/roles-permissions',
  '/settings',
  '/profile',
];

export const ALL_FEATURE_FLAGS = [
  { key: 'enableAuditLogs', title: 'Audit Trail Inspection', description: 'Allows viewing immutable system activity logs', enabled: true, module: 'Audit' },
  { key: 'enableCloudinaryUploads', title: 'Cloudinary Asset Storage', description: 'Allows uploading documents to Cloudinary vault', enabled: true, module: 'Storage' },
  { key: 'enableApprovalWorkflows', title: 'Approval Workflow Engine', description: 'Allows acting as an approver in workflows', enabled: true, module: 'Governance' },
  { key: 'enableGithubIntegration', title: 'GitHub Code Integration', description: 'Allows viewing repository pull requests & status', enabled: true, module: 'Engineering' },
  { key: 'enableEmployeeManagement', title: 'Employee Directory & Management', description: 'Full staff lifecycle governance', enabled: true, module: 'HR' },
  { key: 'enableInternManagement', title: 'Internship Cohort Operations', description: 'Manage intern tasks, mentors & certificates', enabled: true, module: 'HR' },
  { key: 'enableProjectManagement', title: 'Project & Sprint Delivery', description: 'Sprint tracking & task execution', enabled: true, module: 'Engineering' },
  { key: 'enableTaskManagement', title: 'Task Delegation & Tracking', description: 'Task assignment and time logs', enabled: true, module: 'Engineering' },
];

export const seedDatabase = async (): Promise<void> => {
  try {
    const defaultPasswordHash = await bcrypt.hash('Admin@123', 10);

    // 1. Seed Permissions Catalog
    for (const p of ALL_PERMISSIONS_CATALOG) {
      await PermissionModel.updateOne({ code: p.code }, { $set: p }, { upsert: true });
    }

    // 2. Seed Feature Flags
    for (const f of ALL_FEATURE_FLAGS) {
      await FeatureFlagModel.updateOne({ key: f.key }, { $set: f }, { upsert: true });
    }

    // 3. Seed Route Permissions
    for (const routePath of ALL_MENU_ROUTES) {
      const allowedRoles = routePath === '/roles-permissions' || routePath === '/admin' || routePath === '/settings' || routePath === '/audit-logs'
        ? ['ROLE_CEO', 'ROLE_MD', 'ROLE_SUPER_ADMIN', 'ROLE_ADMIN']
        : ['ROLE_CEO', 'ROLE_MD', 'ROLE_SENIOR_DEVELOPER', 'ROLE_SYSTEM_DEVELOPER', 'ROLE_INTERN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER'];
      
      await RoutePermissionModel.updateOne(
        { path: routePath },
        {
          $set: {
            path: routePath,
            title: routePath.replace('/', '').toUpperCase() || 'DASHBOARD',
            allowedRoles,
            enabled: true,
          },
        },
        { upsert: true }
      );
    }

    // 4. Baseline Roles Data
    const defaultFeatureFlags: Record<string, boolean> = {
      enableAuditLogs: true,
      enableCloudinaryUploads: true,
      enableApprovalWorkflows: true,
      enableGithubIntegration: true,
      enableEmployeeManagement: true,
      enableInternManagement: true,
      enableProjectManagement: true,
      enableTaskManagement: true,
    };

    const allPermCodes = ALL_PERMISSIONS_CATALOG.map((p) => p.code);

    const rolesData = [
      {
        name: 'Chief Executive Officer',
        code: 'ROLE_CEO',
        description: 'Executive Leadership & Highest System Governance',
        hierarchyLevel: 100,
        permissions: allPermCodes,
        menuPermissions: ALL_MENU_ROUTES,
        featureFlags: defaultFeatureFlags,
        status: 'Active',
        updatedBy: 'System Baseline',
      },
      {
        name: 'Managing Director',
        code: 'ROLE_MD',
        description: 'Managing Directorate & Enterprise Operations',
        hierarchyLevel: 90,
        permissions: allPermCodes.filter((p) => p !== 'ROLE_DELETE' && p !== 'PERMISSION_WRITE'),
        menuPermissions: ALL_MENU_ROUTES,
        featureFlags: defaultFeatureFlags,
        status: 'Active',
        updatedBy: 'System Baseline',
      },
      {
        name: 'Senior Developer',
        code: 'ROLE_SENIOR_DEVELOPER',
        description: 'Senior Software Architecture & Project Delivery Lead',
        hierarchyLevel: 70,
        permissions: [
          'DASHBOARD_READ',
          'EMPLOYEE_READ',
          'INTERN_READ',
          'PROJECT_READ',
          'PROJECT_CREATE',
          'PROJECT_WRITE',
          'TASK_READ',
          'TASK_CREATE',
          'TASK_WRITE',
          'TASK_ASSIGN',
          'DOCUMENT_READ',
          'DOCUMENT_UPLOAD',
          'DOCUMENT_DOWNLOAD',
          'REPOSITORY_READ',
          'REPOSITORY_CREATE',
          'REPOSITORY_WRITE',
          'MEETING_READ',
          'MEETING_CREATE',
          'ACTIVITY_READ',
          'AUDIT_LOG_READ',
          'REPORT_READ',
          'REPORT_EXPORT',
          'APPROVAL_READ',
          'APPROVAL_CREATE',
        ],
        menuPermissions: [
          '/dashboard',
          '/manager',
          '/employee',
          '/employees',
          '/interns',
          '/projects',
          '/tasks',
          '/repositories',
          '/documents',
          '/meetings',
          '/reports',
          '/attendance',
          '/leave',
          '/profile',
        ],
        featureFlags: defaultFeatureFlags,
        status: 'Active',
        updatedBy: 'System Baseline',
      },
      {
        name: 'System Developer',
        code: 'ROLE_SYSTEM_DEVELOPER',
        description: 'System Engineering & Infrastructure Staff',
        hierarchyLevel: 60,
        permissions: [
          'DASHBOARD_READ',
          'PROJECT_READ',
          'PROJECT_WRITE',
          'TASK_READ',
          'TASK_CREATE',
          'TASK_WRITE',
          'DOCUMENT_READ',
          'DOCUMENT_UPLOAD',
          'DOCUMENT_DOWNLOAD',
          'REPOSITORY_READ',
          'REPOSITORY_WRITE',
          'MEETING_READ',
          'MEETING_CREATE',
          'APPROVAL_READ',
          'APPROVAL_CREATE',
        ],
        menuPermissions: [
          '/dashboard',
          '/employee',
          '/projects',
          '/tasks',
          '/repositories',
          '/documents',
          '/meetings',
          '/attendance',
          '/leave',
          '/profile',
        ],
        featureFlags: defaultFeatureFlags,
        status: 'Active',
        updatedBy: 'System Baseline',
      },
      {
        name: 'Intern',
        code: 'ROLE_INTERN',
        description: 'Trainee Intern Cohort Member',
        hierarchyLevel: 30,
        permissions: [
          'DASHBOARD_READ',
          'PROJECT_READ',
          'TASK_READ',
          'TASK_WRITE',
          'DOCUMENT_READ',
          'DOCUMENT_UPLOAD',
          'REPOSITORY_READ',
          'MEETING_READ',
          'APPROVAL_CREATE',
        ],
        menuPermissions: [
          '/dashboard',
          '/intern',
          '/projects',
          '/tasks',
          '/repositories',
          '/documents',
          '/meetings',
          '/profile',
        ],
        featureFlags: {
          ...defaultFeatureFlags,
          enableEmployeeManagement: false,
          enableAuditLogs: false,
        },
        status: 'Active',
        updatedBy: 'System Baseline',
      },
      {
        name: 'Customer',
        code: 'ROLE_CUSTOMER',
        description: 'Client Representative / Customer User',
        hierarchyLevel: 10,
        permissions: ['DASHBOARD_READ', 'PROJECT_READ', 'DOCUMENT_READ'],
        menuPermissions: ['/dashboard', '/customer', '/projects', '/documents', '/profile'],
        featureFlags: { ...defaultFeatureFlags, enableEmployeeManagement: false, enableAuditLogs: false },
        status: 'Active',
        updatedBy: 'System Baseline',
      },
    ];

    for (const r of rolesData) {
      await RoleModel.updateOne({ code: r.code }, { $set: r }, { upsert: true });
    }

    // 5. Departments
    const deptsData = [
      { name: 'Management', code: 'MGMT', headName: 'Ranadhir Pal', description: 'Executive Leadership & Governance', employeeCount: 2 },
      { name: 'Technology', code: 'TECH', headName: 'Sourav Roy', description: 'Core Technology & Operations', employeeCount: 2 },
      { name: 'Engineering', code: 'ENG', headName: 'Ganesh Pal', description: 'Software Engineering & Delivery', employeeCount: 4 },
      { name: 'Systems', code: 'SYS', headName: 'Rahul Garai', description: 'Systems Architecture & Infrastructure', employeeCount: 4 },
    ];

    for (const d of deptsData) {
      await DepartmentModel.updateOne({ code: d.code }, { $set: d }, { upsert: true });
    }

    // 6. Database Cleanup: Remove all staff accounts outside authoritative list
    await User.deleteMany({
      email: { $nin: [...AUTHORITATIVE_STAFF_EMAILS, 'amit.sharma@example.com'] },
    });
    await Employee.deleteMany({
      officialEmail: { $nin: AUTHORITATIVE_STAFF_EMAILS },
    });

    // 7. Authoritative Employee Staff Data (4 Employees)
    const employees = [
      {
        employeeId: 'EMP-001',
        employeeCode: 'TK-001',
        fullName: 'Ranadhir Pal',
        firstName: 'Ranadhir',
        lastName: 'Pal',
        username: 'ranadhirpal',
        officialEmail: 'rjrajeshpal30@gmail.com',
        personalEmail: 'rjrajeshpal30@gmail.com',
        role: 'ROLE_CEO',
        designation: 'CEO',
        department: 'Management',
        employmentType: 'Employee',
        hierarchyLevel: 100,
        mobileNumber: '8503687142',
        githubUrl: 'https://github.com/rajesh30062003',
        joiningDate: '2020-01-01',
        skills: ['Executive Leadership', 'Corporate Governance', 'Strategic Vision'],
        salary: 350000,
        managerId: '',
        managerName: '',
      },
      {
        employeeId: 'EMP-002',
        employeeCode: 'TK-002',
        fullName: 'Sourav Roy',
        firstName: 'Sourav',
        lastName: 'Roy',
        username: 'souravroy',
        officialEmail: 'souravroy6412@gmail.com',
        personalEmail: 'souravroy6412@gmail.com',
        role: 'ROLE_MD',
        designation: 'Managing Director',
        department: 'Management',
        employmentType: 'Employee',
        hierarchyLevel: 90,
        mobileNumber: '9749005543',
        githubUrl: 'https://github.com/souravroy6412-crypto',
        joiningDate: '2020-02-01',
        skills: ['Operations Strategy', 'Global Partnerships', 'Enterprise Operations'],
        salary: 320000,
        managerId: '',
        managerName: '',
      },
      {
        employeeId: 'EMP-003',
        employeeCode: 'TK-003',
        fullName: 'Ganesh Pal',
        firstName: 'Ganesh',
        lastName: 'Pal',
        username: 'ganeshpal',
        officialEmail: 'palganeshpal314@gmail.com',
        personalEmail: 'palganeshpal314@gmail.com',
        role: 'ROLE_SENIOR_DEVELOPER',
        designation: 'Senior Developer',
        department: 'Engineering',
        employmentType: 'Employee',
        hierarchyLevel: 70,
        mobileNumber: '8509771608',
        githubUrl: 'https://github.com/subrata850977',
        joiningDate: '2021-03-01',
        skills: ['Fullstack Architecture', 'React & TypeScript', 'Microservices', 'Database Systems'],
        salary: 280000,
        managerId: 'EMP-001',
        managerName: 'Ranadhir Pal',
      },
      {
        employeeId: 'EMP-004',
        employeeCode: 'TK-004',
        fullName: 'Rahul Garai',
        firstName: 'Rahul',
        lastName: 'Garai',
        username: 'rahulgarai',
        officialEmail: 'garairahul087@gmail.com',
        personalEmail: 'garairahul087@gmail.com',
        role: 'ROLE_SYSTEM_DEVELOPER',
        designation: 'System Developer',
        department: 'Engineering',
        employmentType: 'Employee',
        hierarchyLevel: 60,
        mobileNumber: '9641302571',
        githubUrl: 'https://github.com/9641302571',
        joiningDate: '2021-08-01',
        skills: ['System Engineering', 'Backend Services', 'API Integration', 'Cloud Ops'],
        salary: 250000,
        managerId: 'EMP-003',
        managerName: 'Ganesh Pal',
      },
    ];

    // 8. Authoritative Intern Data (4 Interns)
    const interns = [
      {
        employeeId: 'INT-001',
        employeeCode: 'TK-INT-01',
        fullName: 'Sangita Koner',
        firstName: 'Sangita',
        lastName: 'Koner',
        username: 'sangitakoner',
        officialEmail: 'sangitakoner455@gmail.com',
        personalEmail: 'sangitakoner455@gmail.com',
        role: 'ROLE_INTERN',
        designation: 'Intern',
        department: 'Engineering',
        employmentType: 'Intern',
        hierarchyLevel: 30,
        mobileNumber: '6297747765',
        githubUrl: 'https://github.com/sangitakoner455',
        joiningDate: '2025-06-01',
        skills: ['React', 'TypeScript', 'Frontend Engineering'],
        salary: 25000,
        managerId: 'EMP-003',
        managerName: 'Ganesh Pal',
      },
      {
        employeeId: 'INT-002',
        employeeCode: 'TK-INT-02',
        fullName: 'Rahul Pal',
        firstName: 'Rahul',
        lastName: 'Pal',
        username: 'rahulpal',
        officialEmail: 'rahulpal01102002@gmail.com',
        personalEmail: 'rahulpal01102002@gmail.com',
        role: 'ROLE_INTERN',
        designation: 'Intern',
        department: 'Engineering',
        employmentType: 'Intern',
        hierarchyLevel: 30,
        mobileNumber: '6296909151',
        githubUrl: 'https://github.com/Rahulpal0001',
        joiningDate: '2025-06-01',
        skills: ['Python', 'Data Engineering', 'Web Development'],
        salary: 25000,
        managerId: 'EMP-004',
        managerName: 'Rahul Garai',
      },
      {
        employeeId: 'INT-003',
        employeeCode: 'TK-INT-03',
        fullName: 'Salman Kazi',
        firstName: 'Salman',
        lastName: 'Kazi',
        username: 'salmankazi',
        officialEmail: 'salmankazi1603@gmail.com',
        personalEmail: 'salmankazi1603@gmail.com',
        role: 'ROLE_INTERN',
        designation: 'Intern',
        department: 'Engineering',
        employmentType: 'Intern',
        hierarchyLevel: 30,
        mobileNumber: '9907701227',
        githubUrl: 'https://github.com/salmankazi1603-lab',
        joiningDate: '2025-06-01',
        skills: ['Linux Systems', 'Docker', 'API Testing'],
        salary: 25000,
        managerId: 'EMP-004',
        managerName: 'Rahul Garai',
      },
      {
        employeeId: 'INT-004',
        employeeCode: 'TK-INT-04',
        fullName: 'Nisha Pandit',
        firstName: 'Nisha',
        lastName: 'Pandit',
        username: 'nishapandit',
        officialEmail: 'nishapanditbwn@gmail.com',
        personalEmail: 'nishapanditbwn@gmail.com',
        role: 'ROLE_INTERN',
        designation: 'Intern',
        department: 'Engineering',
        employmentType: 'Intern',
        hierarchyLevel: 30,
        mobileNumber: '9083049585',
        githubUrl: 'https://github.com/nishapanditindia',
        joiningDate: '2025-06-01',
        skills: ['UI/UX Design', 'Frontend Development', 'QA Testing'],
        salary: 25000,
        managerId: 'EMP-003',
        managerName: 'Ganesh Pal',
      },
    ];

    const allStaff = [...employees, ...interns];

    for (const s of allStaff) {
      // Find role perms for User permissions array
      const roleDoc = rolesData.find((r) => r.code === s.role);
      const userPerms = roleDoc ? roleDoc.permissions : ['USER_READ', 'PROJECT_READ'];

      // Upsert User
      await User.updateOne(
        { email: s.officialEmail },
        {
          $set: {
            userId: s.employeeId,
            email: s.officialEmail,
            username: s.username,
            passwordHash: defaultPasswordHash,
            firstName: s.firstName,
            lastName: s.lastName,
            role: s.role,
            roles: [s.role],
            department: s.department,
            designation: s.designation,
            phoneNumber: s.mobileNumber,
            avatarUrl: `https://ui-avatars.com/api/?name=${encodeURIComponent(s.fullName)}&background=0D8ABC&color=fff`,
            githubUrl: s.githubUrl,
            enabled: true,
            accountNonLocked: true,
            emailVerified: true,
            permissions: userPerms,
          },
        },
        { upsert: true }
      );

      // Upsert Employee
      await Employee.updateOne(
        { officialEmail: s.officialEmail },
        {
          $set: {
            employeeId: s.employeeId,
            employeeCode: s.employeeCode,
            fullName: s.fullName,
            firstName: s.firstName,
            lastName: s.lastName,
            username: s.username,
            passwordHash: defaultPasswordHash,
            officialEmail: s.officialEmail,
            personalEmail: s.personalEmail,
            mobileNumber: s.mobileNumber,
            githubUrl: s.githubUrl,
            designation: s.designation,
            role: s.role,
            department: s.department,
            hierarchyLevel: s.hierarchyLevel,
            managerId: s.managerId || '',
            managerName: s.managerName || '',
            joiningDate: s.joiningDate,
            skills: s.skills,
            employmentStatus: 'Active',
            employmentType: s.employmentType,
            companyName: 'Tech Knife Enterprises',
            email: s.officialEmail,
            enabled: true,
            accountNonLocked: true,
            emailVerified: true,
            payroll: {
              basicSalary: s.salary * 0.6,
              hra: s.salary * 0.25,
              bonus: s.salary * 0.1,
              incentives: s.salary * 0.05,
              tax: 5000,
              professionalTax: 200,
              netSalary: s.salary,
              salarySlipHistory: [],
            },
          },
        },
        { upsert: true }
      );

      // Seed Attendance
      const todayStr = new Date().toISOString().split('T')[0];
      await Attendance.updateOne(
        { userId: s.employeeId, date: todayStr },
        {
          $set: {
            attendanceId: `ATT-${s.employeeId}-${todayStr}`,
            userId: s.employeeId,
            userEmail: s.officialEmail,
            userName: s.fullName,
            department: s.department,
            date: todayStr,
            status: 'PRESENT',
            checkInTime: '09:00 AM',
            checkOutTime: '06:00 PM',
            clockIn: '09:00 AM',
            clockOut: '06:00 PM',
            totalHours: '9.0 hrs',
            totalWorkMinutes: 480,
            totalBreakMinutes: 60,
            overtimeMinutes: 0,
            isLateArrival: false,
            isEarlyLeaving: false,
            isHalfDay: false,
            isWorkFromHome: false,
            isHoliday: false,
            isWeekend: false,
            remarks: 'On-time enterprise check-in',
            punches: [
              { punchType: 'CHECK_IN', timestamp: `${todayStr}T09:00:00.000Z`, location: 'Kolkata HQ' },
              { punchType: 'CHECK_OUT', timestamp: `${todayStr}T18:00:00.000Z`, location: 'Kolkata HQ' },
            ],
          },
        },
        { upsert: true }
      );
    }

    // 9. Default Customer Data
    const customerData = {
      customerId: 'CUST-001',
      fullName: 'Amit Sharma',
      companyName: 'ABC Enterprises',
      email: 'amit.sharma@example.com',
      mobile: '+91 98111 22334',
      address: 'Plot 45, Technology Park, Sector V, Salt Lake, Kolkata',
      state: 'West Bengal',
      city: 'Kolkata',
      pin: '700091',
      passwordHash: defaultPasswordHash,
      status: 'Active' as const,
    };

    await User.updateOne(
      { email: customerData.email },
      {
        $set: {
          userId: customerData.customerId,
          email: customerData.email,
          username: 'amitsharma',
          passwordHash: defaultPasswordHash,
          firstName: 'Amit',
          lastName: 'Sharma',
          role: 'ROLE_CUSTOMER',
          roles: ['ROLE_CUSTOMER'],
          department: 'ABC Enterprises',
          designation: 'Client Representative',
          phoneNumber: customerData.mobile,
          avatarUrl: `https://ui-avatars.com/api/?name=${encodeURIComponent(customerData.fullName)}&background=28A745&color=fff`,
          enabled: true,
          accountNonLocked: true,
          emailVerified: true,
          permissions: ['CRM_READ'],
        },
      },
      { upsert: true }
    );

    await Customer.updateOne(
      { email: customerData.email },
      {
        $set: {
          customerId: customerData.customerId,
          fullName: customerData.fullName,
          companyName: customerData.companyName,
          email: customerData.email,
          mobile: customerData.mobile,
          address: customerData.address,
          state: customerData.state,
          city: customerData.city,
          pin: customerData.pin,
          passwordHash: defaultPasswordHash,
          status: customerData.status,
          orders: [
            { orderId: 'ORD-101', title: 'Enterprise Management Cloud Suite', amount: 150000, date: '2026-01-10', status: 'Completed' },
          ],
          projects: [
            { projectId: 'PRJ-201', name: 'ABC ERP Integration', status: 'In Progress' },
          ],
        },
      },
      { upsert: true }
    );

    // 10. Core Enterprise Models
    await Project.updateOne(
      { projectId: 'PRJ-001' },
      {
        $set: {
          projectId: 'PRJ-001',
          name: 'Enterprise Cloud Portal',
          code: 'TK-ECP',
          client: 'ABC Enterprises',
          status: 'FULLSTACK_DEV',
          progress: 55,
          deadline: '2026-12-31',
          budget: '₹ 25,00,000',
          teamCount: 8,
          lead: 'Ganesh Pal',
        },
      },
      { upsert: true }
    );

    await Task.updateOne(
      { taskId: 'TSK-001' },
      {
        $set: {
          taskId: 'TSK-001',
          title: 'MongoDB Atlas Data Migration',
          projectName: 'Enterprise Cloud Portal',
          priority: 'Urgent',
          status: 'Completed',
          assignee: 'Rahul Garai',
          dueDate: '2026-07-30',
        },
      },
      { upsert: true }
    );

    await Announcement.updateOne(
      { announcementId: 'ANC-001' },
      {
        $set: {
          announcementId: 'ANC-001',
          title: 'Tech Knife Production System Launch',
          content: 'All enterprise operations are now powered by the authoritative RBAC engine and live MongoDB Atlas infrastructure.',
          author: 'Ranadhir Pal (CEO)',
          category: 'Executive Update',
          pinned: true,
        },
      },
      { upsert: true }
    );

    await Notification.updateOne(
      { notificationId: 'NTF-001' },
      {
        $set: {
          notificationId: 'NTF-001',
          userId: 'EMP-001',
          title: 'RBAC Engine Initialized',
          message: 'Full RBAC permission matrix and role catalog seeded successfully.',
          type: 'success',
          read: false,
        },
      },
      { upsert: true }
    );

    await Setting.updateOne(
      { key: 'SYSTEM_CONFIG' },
      {
        $set: {
          key: 'SYSTEM_CONFIG',
          value: {
            appName: 'Tech Knife Enterprise Management System',
            version: '2.0.0-PROD',
            dbProvider: 'MongoDB Atlas',
            backupEnabled: true,
          },
          description: 'Global system runtime preferences',
        },
      },
      { upsert: true }
    );

    console.log('[Seed Service] Full RBAC matrix, baseline roles, feature flags, permissions catalog, and authoritative staff accounts seeded successfully.');

    await generateDatabaseBackups();
  } catch (error) {
    console.error('[Seed Service Error]', error);
  }
};
