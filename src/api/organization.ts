import { apiClient } from './client';
import { mockEmployees, EmployeeData, employeesApi } from './employees';

export interface Department {
  id: string;
  name: string;
  code: string;
  headEmployeeId?: string;
  headEmployeeName?: string;
  status: 'Active' | 'Inactive';
  description?: string;
  budget?: number;
  location?: string;
  employeeCount?: number;
  createdAt?: string;
}

export interface Designation {
  id: string;
  title: string;
  code: string;
  grade: 'L1' | 'L2' | 'L3' | 'L4' | 'L5' | 'L6' | 'L7';
  hierarchyLevel: number; // 1 = Junior, 7 = Executive C-Suite
  departmentId?: string;
  departmentName?: string;
  minSalary: number;
  maxSalary: number;
  currency: string;
  status: 'Active' | 'Inactive';
  description?: string;
  employeeCount?: number;
}

export interface OrgNode {
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  avatarUrl?: string;
  role: string;
  department: string;
  designation: string;
  managerId?: string;
  managerName?: string;
  directReportsCount: number;
  subordinates: OrgNode[];
}

// Initial Mock Departments Store
const INITIAL_DEPARTMENTS: Department[] = [
  {
    id: 'DEPT-01',
    name: 'Executive Leadership',
    code: 'EXEC',
    headEmployeeId: 'EMP-101',
    headEmployeeName: 'Alexander Vance',
    status: 'Active',
    description: 'Corporate governance, strategic planning, board relations, and enterprise vision.',
    budget: 2500000,
    location: 'San Jose HQ - Tower A',
    createdAt: '2021-01-01',
  },
  {
    id: 'DEPT-02',
    name: 'Engineering & DevOps',
    code: 'ENG',
    headEmployeeId: 'EMP-102',
    headEmployeeName: 'Sarah Connor',
    status: 'Active',
    description: 'Core product engineering, cloud infrastructure, backend microservices, and security.',
    budget: 4200000,
    location: 'San Francisco Tech Hub',
    createdAt: '2021-02-15',
  },
  {
    id: 'DEPT-03',
    name: 'Product Management',
    code: 'PROD',
    headEmployeeId: 'EMP-103',
    headEmployeeName: 'Marcus Brody',
    status: 'Active',
    description: 'Product lifecycle strategy, roadmaps, UX research, and agile delivery management.',
    budget: 1800000,
    location: 'San Francisco Tech Hub',
    createdAt: '2021-04-10',
  },
  {
    id: 'DEPT-04',
    name: 'Client Growth & CRM',
    code: 'CRM',
    headEmployeeId: 'EMP-105',
    headEmployeeName: 'David Miller',
    status: 'Active',
    description: 'Enterprise account acquisition, sales operations, customer success, and partner relations.',
    budget: 2100000,
    location: 'San Jose HQ - Tower B',
    createdAt: '2022-01-20',
  },
  {
    id: 'DEPT-05',
    name: 'Quality Assurance',
    code: 'QA',
    headEmployeeId: 'EMP-106',
    headEmployeeName: 'Jessica Taylor',
    status: 'Active',
    description: 'Automated testing frameworks, performance benchmarks, and release quality gates.',
    budget: 950000,
    location: 'San Francisco Tech Hub',
    createdAt: '2022-05-12',
  },
  {
    id: 'DEPT-06',
    name: 'Human Capital & HR',
    code: 'HR',
    status: 'Active',
    description: 'Talent recruitment, employee relations, payroll compliance, and org development.',
    budget: 800000,
    location: 'San Jose HQ - Tower A',
    createdAt: '2021-03-01',
  },
];

// Initial Mock Designations Store
const INITIAL_DESIGNATIONS: Designation[] = [
  {
    id: 'DESG-101',
    title: 'Managing Director',
    code: 'EXEC-MD',
    grade: 'L7',
    hierarchyLevel: 7,
    departmentId: 'DEPT-01',
    departmentName: 'Executive Leadership',
    minSalary: 220000,
    maxSalary: 350000,
    currency: 'USD',
    status: 'Active',
    description: 'Top organizational executive overseeing overall business strategy and board direction.',
  },
  {
    id: 'DESG-102',
    title: 'Chief Technology Officer',
    code: 'EXEC-CTO',
    grade: 'L7',
    hierarchyLevel: 7,
    departmentId: 'DEPT-02',
    departmentName: 'Engineering & DevOps',
    minSalary: 200000,
    maxSalary: 300000,
    currency: 'USD',
    status: 'Active',
    description: 'Head of technical strategy, enterprise architecture, and engineering standard deployment.',
  },
  {
    id: 'DESG-103',
    title: 'Senior Engineering Manager',
    code: 'ENG-MGR',
    grade: 'L5',
    hierarchyLevel: 5,
    departmentId: 'DEPT-03',
    departmentName: 'Product Management',
    minSalary: 150000,
    maxSalary: 210000,
    currency: 'USD',
    status: 'Active',
    description: 'Manages engineering team leads, sprint deliverables, and engineering resource allocation.',
  },
  {
    id: 'DESG-104',
    title: 'Senior Frontend Lead',
    code: 'ENG-SFL',
    grade: 'L4',
    hierarchyLevel: 4,
    departmentId: 'DEPT-02',
    departmentName: 'Engineering & DevOps',
    minSalary: 130000,
    maxSalary: 175000,
    currency: 'USD',
    status: 'Active',
    description: 'Technical lead for client UI/UX micro-frontends, design system components, and performance.',
  },
  {
    id: 'DESG-105',
    title: 'Growth Lead',
    code: 'CRM-GL',
    grade: 'L4',
    hierarchyLevel: 4,
    departmentId: 'DEPT-04',
    departmentName: 'Client Growth & CRM',
    minSalary: 115000,
    maxSalary: 160000,
    currency: 'USD',
    status: 'Active',
    description: 'Leads sales pipelines, CRM integrations, customer expansion, and recurring revenue growth.',
  },
  {
    id: 'DESG-106',
    title: 'Lead QA Automation Specialist',
    code: 'QA-LQA',
    grade: 'L4',
    hierarchyLevel: 4,
    departmentId: 'DEPT-05',
    departmentName: 'Quality Assurance',
    minSalary: 110000,
    maxSalary: 150000,
    currency: 'USD',
    status: 'Active',
    description: 'Oversees automated testing suites, end-to-end integration tests, and quality gates.',
  },
  {
    id: 'DESG-107',
    title: 'Software Engineer',
    code: 'ENG-SWE',
    grade: 'L2',
    hierarchyLevel: 2,
    departmentId: 'DEPT-02',
    departmentName: 'Engineering & DevOps',
    minSalary: 85000,
    maxSalary: 125000,
    currency: 'USD',
    status: 'Active',
    description: 'Builds core features, writes unit tests, and resolves technical defects across application stack.',
  },
];

// Helper to load and save to localStorage
const DEPT_KEY = 'techknife_departments';
const DESG_KEY = 'techknife_designations';

function getStoredDepartments(): Department[] {
  try {
    const raw = localStorage.getItem(DEPT_KEY);
    if (raw) return JSON.parse(raw);
  } catch {
    // fallback
  }
  return INITIAL_DEPARTMENTS;
}

function saveStoredDepartments(data: Department[]): void {
  try {
    localStorage.setItem(DEPT_KEY, JSON.stringify(data));
  } catch {
    // fallback
  }
}

function getStoredDesignations(): Designation[] {
  try {
    const raw = localStorage.getItem(DESG_KEY);
    if (raw) return JSON.parse(raw);
  } catch {
    // fallback
  }
  return INITIAL_DESIGNATIONS;
}

function saveStoredDesignations(data: Designation[]): void {
  try {
    localStorage.setItem(DESG_KEY, JSON.stringify(data));
  } catch {
    // fallback
  }
}

let mockDepartments = getStoredDepartments();
let mockDesignations = getStoredDesignations();

export const organizationApi = {
  // ================= DEPARTMENTS =================

  async getDepartments(params?: {
    search?: string;
    status?: string;
    page?: number;
    limit?: number;
  }): Promise<{ departments: Department[]; total: number; totalPages: number }> {
    try {
      const res = await apiClient.get('/departments', { params });
      if (res.data?.data) {
        return {
          departments: res.data.data,
          total: res.data.total || res.data.data.length,
          totalPages: res.data.totalPages || 1,
        };
      }
    } catch {
      // Fallback
    }

    // Refresh employee counts
    const allEmployees = await employeesApi.getEmployees({ limit: 1000 });
    const empList = allEmployees.employees;

    let filtered = mockDepartments.map((dept) => {
      const count = empList.filter((e) => e.department === dept.name).length;
      return { ...dept, employeeCount: count };
    });

    if (params?.search) {
      const q = params.search.toLowerCase();
      filtered = filtered.filter(
        (d) =>
          d.name.toLowerCase().includes(q) ||
          d.code.toLowerCase().includes(q) ||
          (d.headEmployeeName && d.headEmployeeName.toLowerCase().includes(q))
      );
    }

    if (params?.status && params.status !== 'ALL') {
      filtered = filtered.filter((d) => d.status === params.status);
    }

    const page = params?.page || 1;
    const limit = params?.limit || 10;
    const startIndex = (page - 1) * limit;
    const paginated = filtered.slice(startIndex, startIndex + limit);
    const totalPages = Math.ceil(filtered.length / limit) || 1;

    return {
      departments: paginated,
      total: filtered.length,
      totalPages,
    };
  },

  async getDepartmentById(id: string): Promise<Department | null> {
    try {
      const res = await apiClient.get(`/departments/${id}`);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }
    return mockDepartments.find((d) => d.id === id) || null;
  },

  async createDepartment(data: Partial<Department>): Promise<Department> {
    try {
      const res = await apiClient.post('/departments', data);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const newDept: Department = {
      id: `DEPT-${String(mockDepartments.length + 1).padStart(2, '0')}`,
      name: data.name || 'New Department',
      code: (data.code || 'DEPT').toUpperCase(),
      headEmployeeId: data.headEmployeeId || '',
      headEmployeeName: data.headEmployeeName || '',
      status: data.status || 'Active',
      description: data.description || 'Enterprise department unit.',
      budget: Number(data.budget) || 500000,
      location: data.location || 'Headquarters',
      createdAt: new Date().toISOString().split('T')[0],
      employeeCount: 0,
    };

    mockDepartments = [newDept, ...mockDepartments];
    saveStoredDepartments(mockDepartments);
    return newDept;
  },

  async updateDepartment(id: string, updates: Partial<Department>): Promise<Department> {
    try {
      const res = await apiClient.put(`/departments/${id}`, updates);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const index = mockDepartments.findIndex((d) => d.id === id);
    if (index !== -1) {
      mockDepartments[index] = { ...mockDepartments[index], ...updates };
      saveStoredDepartments(mockDepartments);
      return mockDepartments[index];
    }
    throw new Error('Department not found');
  },

  async deleteDepartment(id: string): Promise<boolean> {
    try {
      await apiClient.delete(`/departments/${id}`);
    } catch {
      // Fallback
    }
    mockDepartments = mockDepartments.filter((d) => d.id !== id);
    saveStoredDepartments(mockDepartments);
    return true;
  },

  // ================= DESIGNATIONS =================

  async getDesignations(params?: {
    search?: string;
    departmentId?: string;
    grade?: string;
    status?: string;
    page?: number;
    limit?: number;
  }): Promise<{ designations: Designation[]; total: number; totalPages: number }> {
    try {
      const res = await apiClient.get('/designations', { params });
      if (res.data?.data) {
        return {
          designations: res.data.data,
          total: res.data.total || res.data.data.length,
          totalPages: res.data.totalPages || 1,
        };
      }
    } catch {
      // Fallback
    }

    const allEmployees = await employeesApi.getEmployees({ limit: 1000 });
    const empList = allEmployees.employees;

    let filtered = mockDesignations.map((desg) => {
      const count = empList.filter((e) => e.designation === desg.title).length;
      return { ...desg, employeeCount: count };
    });

    if (params?.search) {
      const q = params.search.toLowerCase();
      filtered = filtered.filter(
        (d) =>
          d.title.toLowerCase().includes(q) ||
          d.code.toLowerCase().includes(q) ||
          (d.departmentName && d.departmentName.toLowerCase().includes(q))
      );
    }

    if (params?.departmentId && params.departmentId !== 'ALL') {
      filtered = filtered.filter((d) => d.departmentId === params.departmentId || d.departmentName === params.departmentId);
    }

    if (params?.grade && params.grade !== 'ALL') {
      filtered = filtered.filter((d) => d.grade === params.grade);
    }

    if (params?.status && params.status !== 'ALL') {
      filtered = filtered.filter((d) => d.status === params.status);
    }

    // Sort by hierarchyLevel descending
    filtered.sort((a, b) => b.hierarchyLevel - a.hierarchyLevel);

    const page = params?.page || 1;
    const limit = params?.limit || 10;
    const startIndex = (page - 1) * limit;
    const paginated = filtered.slice(startIndex, startIndex + limit);
    const totalPages = Math.ceil(filtered.length / limit) || 1;

    return {
      designations: paginated,
      total: filtered.length,
      totalPages,
    };
  },

  async getDesignationById(id: string): Promise<Designation | null> {
    try {
      const res = await apiClient.get(`/designations/${id}`);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }
    return mockDesignations.find((d) => d.id === id) || null;
  },

  async createDesignation(data: Partial<Designation>): Promise<Designation> {
    try {
      const res = await apiClient.post('/designations', data);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const gradeToLevelMap: Record<string, number> = {
      L1: 1,
      L2: 2,
      L3: 3,
      L4: 4,
      L5: 5,
      L6: 6,
      L7: 7,
    };

    const grade = (data.grade as any) || 'L2';
    const hierarchyLevel = gradeToLevelMap[grade] || 2;

    const newDesg: Designation = {
      id: `DESG-${Math.floor(100 + Math.random() * 900)}`,
      title: data.title || 'Software Engineer',
      code: (data.code || 'DESG').toUpperCase(),
      grade,
      hierarchyLevel,
      departmentId: data.departmentId || '',
      departmentName: data.departmentName || 'Engineering & DevOps',
      minSalary: Number(data.minSalary) || 80000,
      maxSalary: Number(data.maxSalary) || 120000,
      currency: data.currency || 'USD',
      status: data.status || 'Active',
      description: data.description || 'Designation responsibilities and domain role.',
      employeeCount: 0,
    };

    mockDesignations = [newDesg, ...mockDesignations];
    saveStoredDesignations(mockDesignations);
    return newDesg;
  },

  async updateDesignation(id: string, updates: Partial<Designation>): Promise<Designation> {
    try {
      const res = await apiClient.put(`/designations/${id}`, updates);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const index = mockDesignations.findIndex((d) => d.id === id);
    if (index !== -1) {
      if (updates.grade) {
        const gradeToLevelMap: Record<string, number> = {
          L1: 1, L2: 2, L3: 3, L4: 4, L5: 5, L6: 6, L7: 7,
        };
        updates.hierarchyLevel = gradeToLevelMap[updates.grade] || mockDesignations[index].hierarchyLevel;
      }

      mockDesignations[index] = { ...mockDesignations[index], ...updates };
      saveStoredDesignations(mockDesignations);
      return mockDesignations[index];
    }
    throw new Error('Designation not found');
  },

  async deleteDesignation(id: string): Promise<boolean> {
    try {
      await apiClient.delete(`/designations/${id}`);
    } catch {
      // Fallback
    }
    mockDesignations = mockDesignations.filter((d) => d.id !== id);
    saveStoredDesignations(mockDesignations);
    return true;
  },

  // ================= ORGANIZATION STRUCTURE / ORG TREE =================

  async getOrgTree(departmentFilter?: string): Promise<OrgNode[]> {
    try {
      const res = await apiClient.get('/organization/tree', {
        params: { department: departmentFilter },
      });
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const result = await employeesApi.getEmployees({ limit: 1000 });
    let empList = result.employees;

    if (departmentFilter && departmentFilter !== 'ALL') {
      empList = empList.filter((e) => e.department === departmentFilter);
    }

    // Build manager lookup map & children lookup map
    const nodeMap = new Map<string, OrgNode>();

    empList.forEach((e) => {
      const manager = empList.find((m) => m.id === e.managerId);
      nodeMap.set(e.id, {
        employeeId: e.id,
        firstName: e.firstName,
        lastName: e.lastName,
        email: e.email,
        avatarUrl: e.avatarUrl,
        role: e.role,
        department: e.department,
        designation: e.designation,
        managerId: e.managerId,
        managerName: manager ? `${manager.firstName} ${manager.lastName}` : undefined,
        directReportsCount: 0,
        subordinates: [],
      });
    });

    const rootNodes: OrgNode[] = [];

    // Link subordinates to managers
    nodeMap.forEach((node) => {
      if (node.managerId && nodeMap.has(node.managerId)) {
        const parentNode = nodeMap.get(node.managerId)!;
        parentNode.subordinates.push(node);
        parentNode.directReportsCount += 1;
      } else {
        rootNodes.push(node);
      }
    });

    return rootNodes;
  },
};
