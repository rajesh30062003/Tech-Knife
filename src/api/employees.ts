import { apiClient } from './client';
import { Role } from '../types';

export interface EmployeeData {
  id: string;
  employeeId?: string;
  employeeCode?: string;
  firstName: string;
  lastName: string;
  email: string;
  officialEmail?: string;
  phone?: string;
  mobileNumber?: string;
  role: Role;
  department: string;
  designation: string;
  joinDate?: string;
  joiningDate?: string;
  status: 'Active' | 'On Leave' | 'Suspended' | 'Terminated';
  salary?: number;
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

export function extractList<T = any>(res: any): T[] {
  if (!res) return [];
  const payload = res.data;
  if (!payload) return [];
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload.data)) return payload.data;
  if (Array.isArray(payload.content)) return payload.content;
  if (Array.isArray(payload.data?.content)) return payload.data.content;
  return [];
}

export const employeesApi = {
  // GET /api/employees with query params (search, department, role, status)
  async getEmployees(params?: {
    search?: string;
    department?: string;
    role?: string;
    status?: string;
    page?: number;
    limit?: number;
  }): Promise<{ employees: EmployeeData[]; total: number; totalPages: number }> {
    const cleanParams: any = { limit: 500, ...params };
    if (cleanParams.department === 'ALL' || cleanParams.department === 'all') delete cleanParams.department;
    if (cleanParams.status === 'ALL' || cleanParams.status === 'all') delete cleanParams.status;
    if (cleanParams.role === 'ALL' || cleanParams.role === 'all') delete cleanParams.role;
    if (!cleanParams.search) delete cleanParams.search;

    const response = await apiClient.get('/employees', { params: cleanParams });
    const list = extractList(response);
    const totalElements = response.data?.data?.totalElements ?? response.data?.totalElements ?? list.length;
    const totalPages = response.data?.data?.totalPages ?? response.data?.totalPages ?? 1;

    const rawList = Array.isArray(list) ? list : [];
    const filteredList = rawList.filter((e: any) => {
      if (!e) return false;
      const type = (e.employmentType || e.role || '').toString().toUpperCase();
      const empId = (e.employeeId || e.employeeCode || e.id || '').toString().toUpperCase();
      return type !== 'INTERN' && type !== 'ROLE_INTERN' && !empId.startsWith('INT-');
    });

    const mappedEmployees: EmployeeData[] = filteredList.map((e: any) => ({
      id: e.employeeId || e.id || e._id,
      employeeId: e.employeeId,
      employeeCode: e.employeeCode,
      firstName: e.firstName || e.fullName?.split(' ')[0] || 'Employee',
      lastName: e.lastName || e.fullName?.split(' ')[1] || '',
      email: e.officialEmail || e.email || '',
      officialEmail: e.officialEmail || e.email || '',
      phone: e.mobileNumber || e.phoneNumber || '',
      mobileNumber: e.mobileNumber || e.phoneNumber || '',
      role: e.role || 'ROLE_EMPLOYEE',
      department: e.department || e.departmentId || 'Engineering',
      designation: e.designation || e.designationId || 'Staff',
      joinDate: e.joiningDate || '2025-01-01',
      joiningDate: e.joiningDate || '2025-01-01',
      status: e.employmentStatus || e.status || 'Active',
      salary: e.payroll?.netSalary || e.salary || 100000,
      avatarUrl: e.profilePhoto || `https://ui-avatars.com/api/?name=${encodeURIComponent(e.fullName || `${e.firstName || ''} ${e.lastName || ''}`.trim() || 'Employee')}`,
      address: e.presentAddress || e.address || '',
      skills: Array.isArray(e.skills) ? e.skills : [],
    }));

    return {
      employees: mappedEmployees,
      total: totalElements,
      totalPages: totalPages,
    };
  },

  // GET /api/employees/:id
  async getEmployeeById(id: string): Promise<EmployeeData | null> {
    const res = await apiClient.get(`/employees/${id}`);
    const e = res.data?.data;
    if (!e) return null;

    return {
      id: e.employeeId || e.id || e._id,
      employeeId: e.employeeId,
      employeeCode: e.employeeCode,
      firstName: e.firstName || e.fullName?.split(' ')[0] || 'Employee',
      lastName: e.lastName || e.fullName?.split(' ')[1] || '',
      email: e.officialEmail || e.email,
      officialEmail: e.officialEmail || e.email,
      phone: e.mobileNumber || e.phoneNumber || '',
      mobileNumber: e.mobileNumber || e.phoneNumber || '',
      role: e.role,
      department: e.department,
      designation: e.designation,
      joinDate: e.joiningDate || '2025-01-01',
      joiningDate: e.joiningDate || '2025-01-01',
      status: e.employmentStatus || 'Active',
      salary: e.payroll?.netSalary || 100000,
      avatarUrl: e.profilePhoto || `https://ui-avatars.com/api/?name=${encodeURIComponent(e.fullName || e.firstName)}`,
      address: e.presentAddress || '',
      skills: e.skills || [],
    };
  },

  // POST /api/employees
  async createEmployee(data: Partial<EmployeeData>): Promise<EmployeeData> {
    const res = await apiClient.post('/employees', {
      firstName: data.firstName,
      lastName: data.lastName,
      officialEmail: data.email || data.officialEmail,
      role: data.role || 'ROLE_EMPLOYEE',
      designation: data.designation || 'Software Engineer',
      department: data.department || 'Engineering',
      mobileNumber: data.phone || data.mobileNumber || '+91 98765 43210',
      joiningDate: data.joinDate || data.joiningDate || new Date().toISOString().split('T')[0],
      skills: data.skills || ['Enterprise Solutions'],
    });

    const e = res.data?.data;
    return {
      id: e.employeeId || e.id,
      firstName: e.firstName,
      lastName: e.lastName,
      email: e.officialEmail,
      phone: e.mobileNumber,
      role: e.role,
      department: e.department,
      designation: e.designation,
      joinDate: e.joiningDate,
      status: 'Active',
      salary: e.payroll?.netSalary || 100000,
    };
  },

  // PUT /api/employees/:id
  async updateEmployee(id: string, updates: Partial<EmployeeData>): Promise<EmployeeData> {
    const res = await apiClient.put(`/employees/${id}`, updates);
    return res.data?.data;
  },

  // DELETE /api/employees/:id
  async deleteEmployee(id: string): Promise<boolean> {
    await apiClient.delete(`/employees/${id}`);
    return true;
  },

  // GET /api/employees/statistics
  async getStatistics(): Promise<EmployeeStats> {
    const { employees } = await this.getEmployees();
    const totalCount = employees.length;
    const activeCount = employees.filter((e) => e.status === 'Active').length;
    const onLeaveCount = employees.filter((e) => e.status === 'On Leave').length;
    const suspendedCount = employees.filter((e) => e.status === 'Suspended').length;

    const deptMap = new Map<string, number>();
    let sumSalary = 0;
    employees.forEach((e) => {
      deptMap.set(e.department, (deptMap.get(e.department) || 0) + 1);
      sumSalary += e.salary || 0;
    });

    const departmentBreakdown = Array.from(deptMap.entries()).map(([department, count]) => ({
      department,
      count,
    }));

    return {
      totalCount,
      activeCount,
      onLeaveCount,
      suspendedCount,
      departmentBreakdown,
      avgSalary: Math.round(sumSalary / (totalCount || 1)),
      recentHiresCount: employees.length,
    };
  },

  async importEmployees(jsonList: Partial<EmployeeData>[]): Promise<number> {
    let count = 0;
    for (const item of jsonList) {
      if (item.firstName && item.email) {
        await this.createEmployee(item);
        count++;
      }
    }
    return count;
  },
};
