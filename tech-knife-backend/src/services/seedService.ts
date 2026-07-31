import bcrypt from 'bcryptjs';
import { User } from '../models/User';
import { Employee } from '../models/Employee';
import { Customer } from '../models/Customer';
import { RoleModel, DepartmentModel, PermissionModel } from '../models/RoleAndDept';
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

export const seedDatabase = async (): Promise<void> => {
  try {
    const defaultPasswordHash = await bcrypt.hash('TechKnife@2026', 10);

    // 1. Roles
    const rolesData = [
      { name: 'Super Admin', code: 'ROLE_SUPER_ADMIN', description: 'Complete system control' },
      { name: 'Admin', code: 'ROLE_ADMIN', description: 'System administration' },
      { name: 'Chief Executive Officer', code: 'ROLE_CEO', description: 'Executive Leadership' },
      { name: 'Managing Director', code: 'ROLE_MD', description: 'Managing Directorate' },
      { name: 'Chief Technology Officer', code: 'ROLE_CTO', description: 'Technology Leadership' },
      { name: 'Growth Head', code: 'ROLE_GROWTH_HEAD', description: 'Marketing & Growth Leadership' },
      { name: 'Senior Engineering Manager', code: 'ROLE_SENIOR_ENGINEERING_MANAGER', description: 'Engineering Management' },
      { name: 'Chief Marketing Officer', code: 'ROLE_CMO', description: 'Marketing Directorate' },
      { name: 'Director', code: 'ROLE_DIRECTOR', description: 'Department Director' },
      { name: 'Manager', code: 'ROLE_MANAGER', description: 'Team Manager' },
      { name: 'Employee', code: 'ROLE_EMPLOYEE', description: 'Staff Employee' },
      { name: 'Intern', code: 'ROLE_INTERN', description: 'Trainee Intern' },
      { name: 'Customer', code: 'ROLE_CUSTOMER', description: 'Client / Customer User' },
    ];

    for (const r of rolesData) {
      await RoleModel.updateOne({ code: r.code }, { $setOnInsert: r }, { upsert: true });
    }

    // 2. Departments
    const deptsData = [
      { name: 'Management', code: 'MGMT', headName: 'Ranadhir Pal', description: 'Executive Leadership & Direction' },
      { name: 'Technology', code: 'TECH', headName: 'Subrata Pal', description: 'Core Technology & Architecture' },
      { name: 'Marketing', code: 'MKTG', headName: 'Anindita Chakraborty', description: 'Global Growth & Marketing' },
      { name: 'Engineering', code: 'ENG', headName: 'Rahul Garai', description: 'Software Engineering & Delivery' },
      { name: 'Human Resources', code: 'HR', headName: 'Sarah Connor', description: 'Human Capital & People Ops' },
      { name: 'Finance', code: 'FIN', headName: 'Arthur Pendelton', description: 'Financial Operations' },
    ];

    for (const d of deptsData) {
      await DepartmentModel.updateOne({ code: d.code }, { $setOnInsert: d }, { upsert: true });
    }

    // 3. Leadership Employees Data
    const leadership = [
      {
        employeeId: 'EMP-001',
        employeeCode: 'TK-001',
        fullName: 'Ranadhir Pal',
        firstName: 'Ranadhir',
        lastName: 'Pal',
        username: 'ranadhir',
        officialEmail: 'ranadhir.pal@techknife.com',
        personalEmail: 'ranadhir@gmail.com',
        role: 'ROLE_CEO',
        designation: 'Chief Executive Officer',
        department: 'Management',
        hierarchyLevel: 1,
        mobileNumber: '+91 98765 43210',
        joiningDate: '2020-01-01',
        skills: ['Executive Strategy', 'Enterprise Governance', 'Venture Growth'],
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
        username: 'sourav',
        officialEmail: 'sourav.roy@techknife.com',
        personalEmail: 'sourav@gmail.com',
        role: 'ROLE_MD',
        designation: 'Managing Director',
        department: 'Management',
        hierarchyLevel: 1,
        mobileNumber: '+91 98765 43211',
        joiningDate: '2020-02-01',
        skills: ['Operations Strategy', 'Global Partnerships', 'Enterprise Growth'],
        salary: 320000,
        managerId: '',
        managerName: '',
      },
      {
        employeeId: 'EMP-003',
        employeeCode: 'TK-003',
        fullName: 'Subrata Pal',
        firstName: 'Subrata',
        lastName: 'Pal',
        username: 'subrata',
        officialEmail: 'subrata.pal@techknife.com',
        personalEmail: 'subrata@gmail.com',
        role: 'ROLE_CTO',
        designation: 'Chief Technology Officer',
        department: 'Technology',
        hierarchyLevel: 2,
        mobileNumber: '+91 98765 43212',
        joiningDate: '2020-03-01',
        skills: ['Cloud Architecture', 'System Scalability', 'AI/ML Engineering', 'MongoDB Atlas'],
        salary: 300000,
        managerId: 'EMP-001',
        managerName: 'Ranadhir Pal',
      },
      {
        employeeId: 'EMP-004',
        employeeCode: 'TK-004',
        fullName: 'Anindita Chakraborty',
        firstName: 'Anindita',
        lastName: 'Chakraborty',
        username: 'anindita',
        officialEmail: 'anindita.c@techknife.com',
        personalEmail: 'anindita@gmail.com',
        role: 'ROLE_GROWTH_HEAD',
        designation: 'Growth Head',
        department: 'Marketing',
        hierarchyLevel: 2,
        mobileNumber: '+91 98765 43213',
        joiningDate: '2021-05-15',
        skills: ['Performance Marketing', 'Brand Scaling', 'User Acquisition'],
        salary: 220000,
        managerId: 'EMP-002',
        managerName: 'Sourav Roy',
      },
      {
        employeeId: 'EMP-005',
        employeeCode: 'TK-005',
        fullName: 'Rahul Garai',
        firstName: 'Rahul',
        lastName: 'Garai',
        username: 'rahulgarai',
        officialEmail: 'rahul.garai@techknife.com',
        personalEmail: 'rahulgarai@gmail.com',
        role: 'ROLE_SENIOR_ENGINEERING_MANAGER',
        designation: 'Senior Engineering Manager',
        department: 'Engineering',
        hierarchyLevel: 3,
        mobileNumber: '+91 98765 43214',
        joiningDate: '2021-08-01',
        skills: ['Engineering Leadership', 'Microservices', 'System Architecture', 'Agile Operations'],
        salary: 250000,
        managerId: 'EMP-003',
        managerName: 'Subrata Pal',
      },
    ];

    // 4. Interns Data
    const interns = [
      {
        employeeId: 'INT-001',
        employeeCode: 'TK-INT-01',
        fullName: 'Rahul Pal',
        firstName: 'Rahul',
        lastName: 'Pal',
        username: 'rahulpal',
        officialEmail: 'rahul.pal@techknife.com',
        personalEmail: 'rahulpal.intern@gmail.com',
        role: 'ROLE_INTERN',
        designation: 'Engineering Intern',
        department: 'Engineering',
        hierarchyLevel: 4,
        mobileNumber: '+91 98765 43215',
        joiningDate: '2025-06-01',
        skills: ['React', 'TypeScript', 'Node.js', 'MongoDB'],
        salary: 25000,
      },
      {
        employeeId: 'INT-002',
        employeeCode: 'TK-INT-02',
        fullName: 'Sangita Koner',
        firstName: 'Sangita',
        lastName: 'Koner',
        username: 'sangita',
        officialEmail: 'sangita.k@techknife.com',
        personalEmail: 'sangitakoner@gmail.com',
        role: 'ROLE_INTERN',
        designation: 'Marketing Intern',
        department: 'Marketing',
        hierarchyLevel: 4,
        mobileNumber: '+91 98765 43216',
        joiningDate: '2025-06-01',
        skills: ['SEO', 'Content Strategy', 'Social Media Analytics'],
        salary: 25000,
      },
      {
        employeeId: 'INT-003',
        employeeCode: 'TK-INT-03',
        fullName: 'Salman Kaji',
        firstName: 'Salman',
        lastName: 'Kaji',
        username: 'salman',
        officialEmail: 'salman.k@techknife.com',
        personalEmail: 'salmankaji@gmail.com',
        role: 'ROLE_INTERN',
        designation: 'Technology Intern',
        department: 'Technology',
        hierarchyLevel: 4,
        mobileNumber: '+91 98765 43217',
        joiningDate: '2025-06-01',
        skills: ['Python', 'Docker', 'Linux', 'Database Tuning'],
        salary: 25000,
      },
    ];

    const allStaff = [...leadership, ...interns];

    for (const s of allStaff) {
      // Upsert User
      await User.updateOne(
        { email: s.officialEmail },
        {
          $setOnInsert: {
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
            enabled: true,
            accountNonLocked: true,
            emailVerified: true,
            permissions: ['USER_READ', 'PROJECT_READ'],
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
            designation: s.designation,
            role: s.role,
            department: s.department,
            hierarchyLevel: s.hierarchyLevel,
            managerId: (s as any).managerId || '',
            managerName: (s as any).managerName || '',
            joiningDate: s.joiningDate,
            skills: s.skills,
            employmentStatus: 'Active',
            employmentType: s.role === 'ROLE_INTERN' ? 'Intern' : 'Full-Time',
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

      // Seed Today's Attendance Record for every employee & intern
      const todayStr = new Date().toISOString().split('T')[0];
      await Attendance.updateOne(
        { userId: s.employeeId, date: todayStr },
        {
          $setOnInsert: {
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

    // 5. Default Customer Data
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

    // Upsert Customer User Account
    await User.updateOne(
      { email: customerData.email },
      {
        $setOnInsert: {
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

    // Upsert Customer Profile Document
    await Customer.updateOne(
      { email: customerData.email },
      {
        $setOnInsert: {
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

    // 6. Seed Default Projects, Tasks, Announcements, Notifications & Settings
    await Project.updateOne(
      { projectId: 'PRJ-001' },
      {
        $setOnInsert: {
          projectId: 'PRJ-001',
          name: 'Enterprise Cloud Portal',
          code: 'TK-ECP',
          client: 'ABC Enterprises',
          status: 'In Progress',
          progress: 85,
          deadline: '2026-12-31',
          budget: '₹ 25,00,000',
          teamCount: 8,
          lead: 'Subrata Pal',
        },
      },
      { upsert: true }
    );

    await Task.updateOne(
      { taskId: 'TSK-001' },
      {
        $setOnInsert: {
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
        $setOnInsert: {
          announcementId: 'ANC-001',
          title: 'Tech Knife Production System Launch',
          content: 'All enterprise operations are now fully backed up and powered by live MongoDB Atlas infrastructure.',
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
        $setOnInsert: {
          notificationId: 'NTF-001',
          userId: 'EMP-001',
          title: 'System Initialized',
          message: 'MongoDB Atlas live database connection established successfully.',
          type: 'success',
          read: false,
        },
      },
      { upsert: true }
    );

    await Setting.updateOne(
      { key: 'SYSTEM_CONFIG' },
      {
        $setOnInsert: {
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

    console.log('[Seed Service] Company Leadership, Interns, Customer, Roles, and initial records seeded successfully.');

    // 7. Trigger Backup Text Files Generation
    await generateDatabaseBackups();
  } catch (error) {
    console.error('[Seed Service Error]', error);
  }
};
