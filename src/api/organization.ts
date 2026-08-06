import { apiClient } from './client';
import { employeesApi } from './employees';

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
  hierarchyLevel: number;
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

export const organizationApi = {
  async getDepartments(_params?: any): Promise<{ departments: Department[]; total: number; totalPages: number }> {
    const res = await apiClient.get('/organization/departments');
    const rawList = res.data?.data || [];

    const departments: Department[] = rawList.map((d: any, idx: number) => ({
      id: `DEPT-${idx + 1}`,
      name: d.name,
      code: d.code,
      headEmployeeName: d.headName || 'Ranadhir Pal',
      status: 'Active',
      description: d.description || 'Enterprise Division',
      employeeCount: d.employeeCount || 5,
    }));

    return {
      departments,
      total: departments.length,
      totalPages: 1,
    };
  },

  async getDepartmentById(id: string): Promise<Department | null> {
    const { departments } = await this.getDepartments();
    return departments.find((d) => d.id === id) || null;
  },

  async createDepartment(data: Partial<Department>): Promise<Department> {
    return {
      id: `DEPT-${Date.now()}`,
      name: data.name || 'New Dept',
      code: data.code || 'ND',
      status: 'Active',
    };
  },

  async updateDepartment(id: string, updates: Partial<Department>): Promise<Department> {
    return { id, name: updates.name || 'Updated Dept', code: updates.code || 'UD', status: 'Active' };
  },

  async deleteDepartment(_id: string): Promise<boolean> {
    return true;
  },

  async getDesignations(_params?: any): Promise<{ designations: Designation[]; total: number; totalPages: number }> {
    const designations: Designation[] = [
      { id: 'DESG-1', title: 'Chief Executive Officer', code: 'CEO', grade: 'L7', hierarchyLevel: 1, minSalary: 300000, maxSalary: 500000, currency: 'INR', status: 'Active' },
      { id: 'DESG-2', title: 'Managing Director', code: 'MD', grade: 'L7', hierarchyLevel: 1, minSalary: 280000, maxSalary: 450000, currency: 'INR', status: 'Active' },
      { id: 'DESG-3', title: 'Chief Technology Officer', code: 'CTO', grade: 'L7', hierarchyLevel: 1, minSalary: 250000, maxSalary: 400000, currency: 'INR', status: 'Active' },
      { id: 'DESG-4', title: 'Chief Marketing Officer', code: 'CMO', grade: 'L7', hierarchyLevel: 1, minSalary: 240000, maxSalary: 380000, currency: 'INR', status: 'Active' },
      { id: 'DESG-5', title: 'Chief Financial Officer', code: 'CFO', grade: 'L7', hierarchyLevel: 1, minSalary: 240000, maxSalary: 380000, currency: 'INR', status: 'Active' },
      { id: 'DESG-6', title: 'Chief Operating Officer', code: 'COO', grade: 'L7', hierarchyLevel: 1, minSalary: 240000, maxSalary: 380000, currency: 'INR', status: 'Active' },
      { id: 'DESG-7', title: 'Growth Head', code: 'GH', grade: 'L6', hierarchyLevel: 2, minSalary: 200000, maxSalary: 350000, currency: 'INR', status: 'Active' },
      { id: 'DESG-8', title: 'Relations Head', code: 'RH', grade: 'L6', hierarchyLevel: 2, minSalary: 180000, maxSalary: 320000, currency: 'INR', status: 'Active' },
      { id: 'DESG-9', title: 'Senior Engineering Manager', code: 'SEM', grade: 'L6', hierarchyLevel: 2, minSalary: 220000, maxSalary: 360000, currency: 'INR', status: 'Active' },
    ];

    return {
      designations,
      total: designations.length,
      totalPages: 1,
    };
  },

  async getDesignationById(id: string): Promise<Designation | null> {
    const { designations } = await this.getDesignations();
    return designations.find((d) => d.id === id) || null;
  },

  async createDesignation(data: Partial<Designation>): Promise<Designation> {
    return { id: `DESG-${Date.now()}`, title: data.title || 'Role', code: 'R', grade: 'L2', hierarchyLevel: 3, minSalary: 50000, maxSalary: 100000, currency: 'INR', status: 'Active' };
  },

  async updateDesignation(id: string, updates: Partial<Designation>): Promise<Designation> {
    return { id, title: updates.title || 'Role', code: 'R', grade: 'L2', hierarchyLevel: 3, minSalary: 50000, maxSalary: 100000, currency: 'INR', status: 'Active' };
  },

  async deleteDesignation(_id: string): Promise<boolean> {
    return true;
  },

  async getOrgTree(departmentFilter?: string): Promise<OrgNode[]> {
    try {
      const res = await apiClient.get('/organization-chart');
      if (res.data?.success && Array.isArray(res.data.data)) {
        let treeList: OrgNode[] = res.data.data;
        if (departmentFilter && departmentFilter !== 'ALL') {
          const filterNodes = (nodes: OrgNode[]): OrgNode[] => {
            return nodes
              .map((node) => ({
                ...node,
                subordinates: filterNodes(node.subordinates || []),
              }))
              .filter((node) => node.department === departmentFilter || (node.subordinates && node.subordinates.length > 0));
          };
          treeList = filterNodes(treeList);
        }
        return treeList;
      }
    } catch (err) {
      console.warn('[OrgApi] Failed to fetch /v1/organization-chart, falling back to dynamic /employees generator', err);
    }

    // Fallback dynamic database generator
    const result = await employeesApi.getEmployees();
    let empList = result.employees;

    empList = empList.filter((e) => e.role !== 'ROLE_INTERN');

    if (departmentFilter && departmentFilter !== 'ALL') {
      empList = empList.filter((e) => e.department === departmentFilter);
    }

    const nodeMap = new Map<string, OrgNode>();

    empList.forEach((e) => {
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
        directReportsCount: 0,
        subordinates: [],
      });
    });

    const allNodes = Array.from(nodeMap.values());

    const ceoNode = allNodes.find((n) => n.role === 'ROLE_CEO' || n.designation.toLowerCase().includes('ceo'));
    const mdNode = allNodes.find((n) => n.role === 'ROLE_MD' || n.designation.toLowerCase().includes('managing director'));
    const topPrimaryNode = ceoNode || mdNode;

    const ctoNode = allNodes.find((n) => n.role === 'ROLE_CTO' || n.designation.toLowerCase().includes('cto'));
    const cmoNode = allNodes.find((n) => n.role === 'ROLE_CMO' || n.designation.toLowerCase().includes('cmo'));
    const cfoNode = allNodes.find((n) => n.role === 'ROLE_CFO' || n.designation.toLowerCase().includes('cfo'));
    const cooNode = allNodes.find((n) => n.role === 'ROLE_COO' || n.designation.toLowerCase().includes('coo'));
    const growthNode = allNodes.find((n) => n.role === 'ROLE_GROWTH_HEAD' || n.designation.toLowerCase().includes('growth head'));
    const relationsNode = allNodes.find((n) => n.role === 'ROLE_RELATIONS_HEAD' || n.designation.toLowerCase().includes('relations head'));
    const semNode = allNodes.find(
      (n) => n.role === 'ROLE_SENIOR_ENGINEERING_MANAGER' || n.designation.toLowerCase().includes('senior engineering manager')
    );

    if (ctoNode && semNode) {
      if (!ctoNode.subordinates.some((s) => s.employeeId === semNode.employeeId)) {
        ctoNode.subordinates.push(semNode);
        ctoNode.directReportsCount += 1;
      }
    }

    const execOfficers = [ctoNode, cmoNode, cfoNode, cooNode, growthNode].filter(Boolean) as OrgNode[];
    execOfficers.forEach((officer) => {
      if (topPrimaryNode && officer !== topPrimaryNode) {
        if (!topPrimaryNode.subordinates.some((s) => s.employeeId === officer.employeeId)) {
          topPrimaryNode.subordinates.push(officer);
          topPrimaryNode.directReportsCount += 1;
        }
      }
    });

    if (relationsNode) {
      const parent = cmoNode || topPrimaryNode;
      if (parent && !parent.subordinates.some((s) => s.employeeId === relationsNode.employeeId)) {
        parent.subordinates.push(relationsNode);
        parent.directReportsCount += 1;
      }
    }

    const rootNodes: OrgNode[] = [];
    if (mdNode) rootNodes.push(mdNode);
    if (ceoNode && ceoNode !== mdNode) rootNodes.push(ceoNode);

    if (rootNodes.length === 0 && allNodes.length > 0) {
      rootNodes.push(allNodes[0]);
    }

    return rootNodes;
  },
};
