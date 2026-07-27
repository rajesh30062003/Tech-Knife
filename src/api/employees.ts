import { apiClient } from './client';
import { Role } from '../types';

export interface EmployeeData {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  role: Role;
  department: string;
  designation: string;
  joinDate: string;
  status: 'Active' | 'On Leave' | 'Suspended' | 'Terminated';
  salary: number;
  avatarUrl?: string;
  address?: string;
  emergencyContact?: string;
  bio?: string;
  skills?: string[];
  managerId?: string;
}

export interface EmployeeTimelineEvent {
  id: string;
  employeeId: string;
  date: string;
  title: string;
  description: string;
  type: 'onboarding' | 'promotion' | 'review' | 'transfer' | 'award';
  actor: string;
}

export interface EmployeeStats {
  totalCount: number;
  activeCount: number;
  onLeaveCount: number;
  suspendedCount: number;
  departmentBreakdown: { department: string; count: number }[];
  avgSalary: number;
  recentHiresCount: number;
}

// Initial Mock Employees Data Store for local fallback
export let mockEmployees: EmployeeData[] = [
  {
    id: 'EMP-101',
    firstName: 'Alexander',
    lastName: 'Vance',
    email: 'a.vance@techknife.com',
    phone: '+1 (555) 019-2834',
    role: 'ROLE_MD',
    department: 'Executive Leadership',
    designation: 'Managing Director',
    joinDate: '2021-03-15',
    status: 'Active',
    salary: 240000,
    avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300',
    address: '450 Silicon Valley Way, San Jose, CA',
    emergencyContact: 'Elena Vance (+1 555-900-1122)',
    bio: 'Over 15 years of executive technology leadership across enterprise SaaS platforms.',
    skills: ['Executive Leadership', 'Corporate Strategy', 'Enterprise Sales', 'Governance'],
    managerId: ''
  },
  {
    id: 'EMP-102',
    firstName: 'Sarah',
    lastName: 'Connor',
    email: 's.connor@techknife.com',
    phone: '+1 (555) 018-9921',
    role: 'ROLE_CTO',
    department: 'Engineering & DevOps',
    designation: 'Chief Technology Officer',
    joinDate: '2021-06-01',
    status: 'Active',
    salary: 210000,
    avatarUrl: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=300',
    address: '880 Cybernetics Blvd, San Francisco, CA',
    emergencyContact: 'John Connor (+1 555-888-2345)',
    bio: 'Lead architect of high-concurrency cloud microservices and Spring Security compliance.',
    skills: ['System Architecture', 'Spring Security', 'Kubernetes', 'Java/TypeScript'],
    managerId: 'EMP-101'
  },
  {
    id: 'EMP-103',
    firstName: 'Marcus',
    lastName: 'Brody',
    email: 'm.brody@techknife.com',
    phone: '+1 (555) 017-3342',
    role: 'ROLE_MANAGER',
    department: 'Product Management',
    designation: 'Senior Engineering Manager',
    joinDate: '2022-01-10',
    status: 'Active',
    salary: 165000,
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300',
    address: '124 Innovation Drive, Oakland, CA',
    emergencyContact: 'Rachel Brody (+1 555-444-9988)',
    bio: 'Agile team manager focusing on developer productivity and continuous release pipelines.',
    skills: ['Agile Scrum', 'Roadmap Planning', 'Technical Leadership', 'React / Node'],
    managerId: 'EMP-102'
  },
  {
    id: 'EMP-104',
    firstName: 'Elena',
    lastName: 'Rostova',
    email: 'e.rostova@techknife.com',
    phone: '+1 (555) 016-8812',
    role: 'ROLE_EMPLOYEE',
    department: 'Engineering & DevOps',
    designation: 'Senior Frontend Lead',
    joinDate: '2022-09-18',
    status: 'Active',
    salary: 140000,
    avatarUrl: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=300',
    address: '55 Pine Street, San Francisco, CA',
    emergencyContact: 'Dmitri Rostov (+1 555-333-7711)',
    bio: 'Frontend expert specialized in React 19, Tailwind CSS, and ultra-responsive UI design.',
    skills: ['React', 'TypeScript', 'Tailwind CSS', 'Performance Optimization'],
    managerId: 'EMP-103'
  },
  {
    id: 'EMP-105',
    firstName: 'David',
    lastName: 'Miller',
    email: 'd.miller@techknife.com',
    phone: '+1 (555) 014-7723',
    role: 'ROLE_EMPLOYEE',
    department: 'Client Growth & CRM',
    designation: 'Growth Lead',
    joinDate: '2023-02-01',
    status: 'On Leave',
    salary: 125000,
    avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=300',
    address: '900 Broadway Ave, San Jose, CA',
    emergencyContact: 'Claire Miller (+1 555-222-1144)',
    bio: 'Driving enterprise acquisition and recurring revenue pipelines across North America.',
    skills: ['HubSpot CRM', 'Key Account Management', 'Enterprise Sales', 'Negotiation'],
    managerId: 'EMP-101'
  },
  {
    id: 'EMP-106',
    firstName: 'Jessica',
    lastName: 'Taylor',
    email: 'j.taylor@techknife.com',
    phone: '+1 (555) 012-9988',
    role: 'ROLE_EMPLOYEE',
    department: 'Quality Assurance',
    designation: 'Lead QA Automation Specialist',
    joinDate: '2023-08-14',
    status: 'Active',
    salary: 118000,
    avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=300',
    address: '77 Market Street, San Francisco, CA',
    emergencyContact: 'Mark Taylor (+1 555-111-8899)',
    bio: 'Automated test suite designer with focus on Playwright, Jest, and integration pipelines.',
    skills: ['Playwright', 'Selenium', 'CI/CD Pipelines', 'API Testing'],
    managerId: 'EMP-103'
  }
];

let mockTimelineEvents: EmployeeTimelineEvent[] = [
  { id: 'TL-1', employeeId: 'EMP-104', date: '2022-09-18', title: 'Onboarded to Engineering', description: 'Joined Tech Knife as Frontend Specialist', type: 'onboarding', actor: 'HR Dept' },
  { id: 'TL-2', employeeId: 'EMP-104', date: '2023-10-01', title: 'Promoted to Senior Frontend Lead', description: 'Elevated role following Sprint 12 UI overhaul', type: 'promotion', actor: 'Sarah Connor (CTO)' },
  { id: 'TL-3', employeeId: 'EMP-104', date: '2024-04-15', title: 'Excellence in Craft Award', description: 'Recognized for 100% lighthouse performance score', type: 'award', actor: 'Alexander Vance (MD)' },
  { id: 'TL-4', employeeId: 'EMP-101', date: '2021-03-15', title: 'Appointed Managing Director', description: 'Formed Tech Knife executive committee', type: 'onboarding', actor: 'Board of Directors' }
];

export const employeesApi = {
  // GET /api/employees with query params (search, department, role, status, page, limit)
  async getEmployees(params?: {
    search?: string;
    department?: string;
    role?: string;
    status?: string;
    page?: number;
    limit?: number;
  }): Promise<{ employees: EmployeeData[]; total: number; totalPages: number }> {
    try {
      const response = await apiClient.get('/users', { params });
      if (response.data?.data) {
        return {
          employees: response.data.data,
          total: response.data.total || response.data.data.length,
          totalPages: response.data.totalPages || 1
        };
      }
    } catch {
      // Fallback to local mock data
    }

    let filtered = [...mockEmployees];

    if (params?.search) {
      const q = params.search.toLowerCase();
      filtered = filtered.filter(
        e =>
          `${e.firstName} ${e.lastName}`.toLowerCase().includes(q) ||
          e.email.toLowerCase().includes(q) ||
          e.id.toLowerCase().includes(q) ||
          e.designation.toLowerCase().includes(q)
      );
    }

    if (params?.department && params.department !== 'ALL') {
      filtered = filtered.filter(e => e.department === params.department);
    }

    if (params?.role && params.role !== 'ALL') {
      filtered = filtered.filter(e => e.role === params.role);
    }

    if (params?.status && params.status !== 'ALL') {
      filtered = filtered.filter(e => e.status === params.status);
    }

    const page = params?.page || 1;
    const limit = params?.limit || 6;
    const startIndex = (page - 1) * limit;
    const paginated = filtered.slice(startIndex, startIndex + limit);
    const totalPages = Math.ceil(filtered.length / limit) || 1;

    return {
      employees: paginated,
      total: filtered.length,
      totalPages
    };
  },

  // GET /api/employees/:id
  async getEmployeeById(id: string): Promise<EmployeeData | null> {
    try {
      const res = await apiClient.get(`/users/${id}`);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }
    return mockEmployees.find(e => e.id === id) || null;
  },

  // POST /api/employees
  async createEmployee(data: Partial<EmployeeData>): Promise<EmployeeData> {
    try {
      const res = await apiClient.post('/users', data);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const newEmp: EmployeeData = {
      id: `EMP-${Math.floor(100 + Math.random() * 900)}`,
      firstName: data.firstName || 'New',
      lastName: data.lastName || 'Employee',
      email: data.email || `emp.${Date.now()}@techknife.com`,
      phone: data.phone || '+1 (555) 000-0000',
      role: data.role || 'ROLE_EMPLOYEE',
      department: data.department || 'Engineering & DevOps',
      designation: data.designation || 'Software Engineer',
      joinDate: data.joinDate || new Date().toISOString().split('T')[0],
      status: data.status || 'Active',
      salary: Number(data.salary) || 120000,
      avatarUrl: data.avatarUrl || `https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=300`,
      address: data.address || 'San Francisco, CA',
      bio: data.bio || 'Recently onboarded Tech Knife team member.',
      skills: data.skills || ['Software Engineering', 'Problem Solving'],
      managerId: data.managerId || ''
    };

    mockEmployees = [newEmp, ...mockEmployees];

    // Add timeline event
    mockTimelineEvents.push({
      id: `TL-${Date.now()}`,
      employeeId: newEmp.id,
      date: newEmp.joinDate,
      title: 'Employee Onboarded',
      description: `Registered as ${newEmp.designation} in ${newEmp.department}`,
      type: 'onboarding',
      actor: 'HR Operations'
    });

    return newEmp;
  },

  // PUT /api/employees/:id
  async updateEmployee(id: string, updates: Partial<EmployeeData>): Promise<EmployeeData> {
    try {
      const res = await apiClient.put(`/users/${id}`, updates);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const index = mockEmployees.findIndex(e => e.id === id);
    if (index !== -1) {
      mockEmployees[index] = { ...mockEmployees[index], ...updates };
      return mockEmployees[index];
    }
    throw new Error('Employee not found');
  },

  // DELETE /api/employees/:id
  async deleteEmployee(id: string): Promise<boolean> {
    try {
      await apiClient.delete(`/users/${id}`);
    } catch {
      // Fallback
    }
    mockEmployees = mockEmployees.filter(e => e.id !== id);
    return true;
  },

  // GET /api/employees/:id/timeline
  async getEmployeeTimeline(employeeId: string): Promise<EmployeeTimelineEvent[]> {
    try {
      const res = await apiClient.get(`/users/${employeeId}/timeline`);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }
    return mockTimelineEvents.filter(t => t.employeeId === employeeId);
  },

  // GET /api/employees/statistics
  async getStatistics(): Promise<EmployeeStats> {
    try {
      const res = await apiClient.get('/users/statistics');
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const totalCount = mockEmployees.length;
    const activeCount = mockEmployees.filter(e => e.status === 'Active').length;
    const onLeaveCount = mockEmployees.filter(e => e.status === 'On Leave').length;
    const suspendedCount = mockEmployees.filter(e => e.status === 'Suspended').length;

    const deptMap = new Map<string, number>();
    let sumSalary = 0;
    mockEmployees.forEach(e => {
      deptMap.set(e.department, (deptMap.get(e.department) || 0) + 1);
      sumSalary += e.salary;
    });

    const departmentBreakdown = Array.from(deptMap.entries()).map(([department, count]) => ({
      department,
      count
    }));

    return {
      totalCount,
      activeCount,
      onLeaveCount,
      suspendedCount,
      departmentBreakdown,
      avgSalary: Math.round(sumSalary / (totalCount || 1)),
      recentHiresCount: 2
    };
  },

  // POST /api/employees/import
  async importEmployees(jsonList: Partial<EmployeeData>[]): Promise<number> {
    let imported = 0;
    for (const item of jsonList) {
      if (item.firstName && item.email) {
        await this.createEmployee(item);
        imported++;
      }
    }
    return imported;
  }
};
