import { apiClient } from '../../api/client';
import {
  CreateEmployeeRequest,
  EmployeeResponse,
  EmployeeSearchFilter,
  EmployeeSummaryResponse,
  PagedResponse,
  UpdateEmployeeRequest,
  UpdateEmployeeStatusRequest,
} from '../types/employeeV2';

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

export const employeeV2Api = {
  async createEmployee(request: CreateEmployeeRequest): Promise<EmployeeResponse> {
    const res = await apiClient.post('/employees', request);
    const e = res.data?.data;
    return {
      id: e?.employeeId || e?.id || '',
      employeeId: e?.employeeId || '',
      officialEmail: e?.officialEmail || '',
      personalEmail: e?.personalEmail || e?.officialEmail || '',
      primaryMobile: e?.mobileNumber || '',
      firstName: e?.firstName || '',
      lastName: e?.lastName || '',
      fullName: e?.fullName || `${e?.firstName || ''} ${e?.lastName || ''}`.trim(),
      gender: e?.gender || 'MALE',
      dob: e?.dateOfBirth || '1990-01-01',
      bloodGroup: e?.bloodGroup || 'O_POSITIVE',
      departmentId: e?.department || e?.departmentId || '',
      designationId: e?.designation || e?.designationId || '',
      joiningDate: e?.joiningDate || '',
      employmentType: 'FULL_TIME',
      salary: e?.payroll?.netSalary || e?.salary || 100000,
      skills: Array.isArray(e?.skills) ? e.skills : [],
      profileImage: e?.profilePhoto || '',
      status: 'ACTIVE',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      createdBy: 'ADMIN',
      updatedBy: 'ADMIN',
    };
  },

  async updateEmployee(id: string, request: UpdateEmployeeRequest): Promise<EmployeeResponse> {
    const res = await apiClient.put(`/employees/${id}`, request);
    const e = res.data?.data;
    return {
      id: e?.employeeId || e?.id || id,
      employeeId: e?.employeeId || id,
      officialEmail: e?.officialEmail || '',
      personalEmail: e?.personalEmail || e?.officialEmail || '',
      primaryMobile: e?.mobileNumber || '',
      firstName: e?.firstName || '',
      lastName: e?.lastName || '',
      fullName: e?.fullName || `${e?.firstName || ''} ${e?.lastName || ''}`.trim(),
      gender: e?.gender || 'MALE',
      dob: e?.dateOfBirth || '1990-01-01',
      bloodGroup: e?.bloodGroup || 'O_POSITIVE',
      departmentId: e?.department || e?.departmentId || '',
      designationId: e?.designation || e?.designationId || '',
      joiningDate: e?.joiningDate || '',
      employmentType: 'FULL_TIME',
      salary: e?.payroll?.netSalary || e?.salary || 100000,
      skills: Array.isArray(e?.skills) ? e.skills : [],
      profileImage: e?.profilePhoto || '',
      status: 'ACTIVE',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      createdBy: 'ADMIN',
      updatedBy: 'ADMIN',
    };
  },

  async getEmployeeById(id: string): Promise<EmployeeResponse> {
    const res = await apiClient.get(`/employees/${id}`);
    const e = res.data?.data;
    return {
      id: e?.employeeId || e?.id || id,
      employeeId: e?.employeeId || id,
      officialEmail: e?.officialEmail || '',
      personalEmail: e?.personalEmail || e?.officialEmail || '',
      primaryMobile: e?.mobileNumber || '',
      firstName: e?.firstName || '',
      lastName: e?.lastName || '',
      fullName: e?.fullName || `${e?.firstName || ''} ${e?.lastName || ''}`.trim(),
      gender: e?.gender || 'MALE',
      dob: e?.dateOfBirth || '1990-01-01',
      bloodGroup: e?.bloodGroup || 'O_POSITIVE',
      departmentId: e?.department || e?.departmentId || '',
      designationId: e?.designation || e?.designationId || '',
      joiningDate: e?.joiningDate || '',
      employmentType: 'FULL_TIME',
      salary: e?.payroll?.netSalary || e?.salary || 100000,
      skills: Array.isArray(e?.skills) ? e.skills : [],
      profileImage: e?.profilePhoto || '',
      status: 'ACTIVE',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      createdBy: 'ADMIN',
      updatedBy: 'ADMIN',
    };
  },

  async getEmployeeByCode(code: string): Promise<EmployeeResponse> {
    return this.getEmployeeById(code);
  },

  async getEmployeeByEmail(officialEmail: string): Promise<EmployeeResponse> {
    return this.getEmployeeById(officialEmail);
  },

  async getAllEmployees(params?: any): Promise<PagedResponse<EmployeeResponse>> {
    const res = await apiClient.get('/employees', { params });
    const list = extractList(res);
    const pagedObj = res.data?.data || res.data || {};

    const rawList = Array.isArray(list) ? list : [];
    const filteredList = rawList.filter((e: any) => {
      if (!e) return false;
      const type = (e?.employmentType || e?.role || '').toString().toUpperCase();
      const empId = (e?.employeeId || e?.id || '').toString().toUpperCase();
      return type !== 'INTERN' && type !== 'ROLE_INTERN' && !empId.startsWith('INT-');
    });

    const content: EmployeeResponse[] = filteredList.map((e: any) => ({
      id: e?.employeeId || e?.id || e?._id || '',
      employeeId: e?.employeeId || e?.id || '',
      officialEmail: e?.officialEmail || e?.email || '',
      personalEmail: e?.personalEmail || e?.officialEmail || e?.email || '',
      primaryMobile: e?.mobileNumber || e?.primaryMobile || '',
      firstName: e?.firstName || e?.fullName?.split(' ')[0] || 'Employee',
      lastName: e?.lastName || e?.fullName?.split(' ')[1] || '',
      fullName: e?.fullName || `${e?.firstName || ''} ${e?.lastName || ''}`.trim() || 'Employee',
      gender: e?.gender || 'UNSPECIFIED',
      dob: e?.dob || e?.dateOfBirth || '',
      bloodGroup: e?.bloodGroup || '',
      departmentId: e?.departmentId || e?.department || '',
      designationId: e?.designationId || e?.designation || '',
      joiningDate: e?.joiningDate || '',
      employmentType: e?.employmentType || 'FULL_TIME',
      salary: e?.payroll?.netSalary || e?.salary || 100000,
      skills: Array.isArray(e?.skills) ? e.skills : [],
      profileImage: e?.profileImage || e?.profilePhoto || '',
      status: e?.status || 'ACTIVE',
      createdAt: e?.createdAt || new Date().toISOString(),
      updatedAt: e?.updatedAt || new Date().toISOString(),
      createdBy: e?.createdBy || 'ADMIN',
      updatedBy: e?.updatedBy || 'ADMIN',
    }));

    return {
      content,
      page: typeof pagedObj.page === 'number' ? pagedObj.page : (params?.page ?? 0),
      size: typeof pagedObj.size === 'number' ? pagedObj.size : (params?.size ?? content.length),
      totalElements: typeof pagedObj.totalElements === 'number' ? pagedObj.totalElements : content.length,
      totalPages: typeof pagedObj.totalPages === 'number' ? pagedObj.totalPages : 1,
      last: typeof pagedObj.last === 'boolean' ? pagedObj.last : true,
    };
  },

  async searchEmployees(filter: EmployeeSearchFilter): Promise<PagedResponse<EmployeeSummaryResponse>> {
    const res = await apiClient.get('/employees', { params: { search: filter.searchTerm, departmentId: filter.departmentId } });
    const list = extractList(res);
    const pagedObj = res.data?.data || res.data || {};

    const rawList = Array.isArray(list) ? list : [];
    const filteredList = rawList.filter((e: any) => {
      if (!e) return false;
      const type = (e?.employmentType || e?.role || '').toString().toUpperCase();
      const empId = (e?.employeeId || e?.id || '').toString().toUpperCase();
      return type !== 'INTERN' && type !== 'ROLE_INTERN' && !empId.startsWith('INT-');
    });

    const content: EmployeeSummaryResponse[] = filteredList.map((e: any) => ({
      id: e?.employeeId || e?.id || '',
      employeeId: e?.employeeId || e?.id || '',
      fullName: e?.fullName || `${e?.firstName || ''} ${e?.lastName || ''}`.trim() || 'Employee',
      officialEmail: e?.officialEmail || e?.email || '',
      primaryMobile: e?.mobileNumber || e?.primaryMobile || '',
      departmentId: e?.departmentId || e?.department || '',
      designationId: e?.designationId || e?.designation || '',
      employmentType: e?.employmentType || 'FULL_TIME',
      status: e?.status || 'ACTIVE',
      joiningDate: e?.joiningDate || '',
    }));

    return {
      content,
      page: typeof pagedObj.page === 'number' ? pagedObj.page : 0,
      size: typeof pagedObj.size === 'number' ? pagedObj.size : content.length,
      totalElements: typeof pagedObj.totalElements === 'number' ? pagedObj.totalElements : content.length,
      totalPages: typeof pagedObj.totalPages === 'number' ? pagedObj.totalPages : 1,
      last: typeof pagedObj.last === 'boolean' ? pagedObj.last : true,
    };
  },

  async getEmployeesByDepartment(departmentId: string): Promise<EmployeeResponse[]> {
    const res = await this.getAllEmployees({ departmentId });
    return res.content;
  },

  async getDirectReports(_managerId: string): Promise<EmployeeResponse[]> {
    const res = await this.getAllEmployees();
    return res.content;
  },

  async updateEmployeeStatus(id: string, request: UpdateEmployeeStatusRequest): Promise<EmployeeResponse> {
    return this.updateEmployee(id, { status: request.status });
  },

  async deleteEmployee(id: string): Promise<boolean> {
    await apiClient.delete(`/employees/${id}`);
    return true;
  },
};
