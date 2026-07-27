import { apiClient } from '../../api/client';
import { notificationsApi, logActivityAction } from '../../api/coreServices';
import {
  ApiResponse,
  CreateEmployeeRequest,
  EmployeeResponse,
  EmployeeSearchFilter,
  EmployeeSummaryResponse,
  PagedResponse,
  UpdateEmployeeRequest,
  UpdateEmployeeStatusRequest,
  EmployeeStatus,
} from '../types/employeeV2';

// Helper to append state diff to local audit entries ledger
const appendAuditDiff = (
  entityName: string,
  entityId: string,
  action: 'CREATE' | 'UPDATE' | 'DELETE' | 'STATUS_CHANGE',
  oldValue: Record<string, any> | null,
  newValue: Record<string, any> | null,
  userName: string = 'Corporate Admin'
) => {
  try {
    const existing = JSON.parse(localStorage.getItem('techknife_audit_entries') || '[]');
    const entry = {
      id: `aud-emp-${Date.now()}`,
      userId: 'usr-admin-01',
      userName,
      entityName,
      entityId,
      action,
      oldValue,
      newValue,
      ipAddress: '192.168.1.104',
      browser: 'Chrome 126.0 (macOS)',
      timestamp: new Date().toISOString(),
    };
    localStorage.setItem('techknife_audit_entries', JSON.stringify([entry, ...existing].slice(0, 100)));
  } catch (err) {
    console.error('Failed to append audit diff', err);
  }
};

// Fallback Mock Data for local client resilience
let mockEmployeesV2: EmployeeResponse[] = [
  {
    id: 'emp-doc-1001',
    employeeId: 'EMP-1001',
    officialEmail: 'a.vance@techknife.com',
    personalEmail: 'alexander.vance@gmail.com',
    primaryMobile: '+1 (555) 019-2834',
    alternateMobile: '+1 (555) 019-2835',
    firstName: 'Alexander',
    lastName: 'Vance',
    fullName: 'Alexander Vance',
    gender: 'MALE',
    dob: '1985-04-12',
    bloodGroup: 'O_POSITIVE',
    departmentId: 'Executive Leadership',
    designationId: 'Managing Director',
    managerId: '',
    joiningDate: '2021-03-15',
    employmentType: 'FULL_TIME',
    salary: 240000,
    skills: ['Executive Leadership', 'Corporate Strategy', 'Enterprise Sales', 'Governance'],
    githubUsername: 'alexvance-tk',
    profileImage: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300',
    status: 'ACTIVE',
    createdAt: '2021-03-15T09:00:00Z',
    updatedAt: '2026-01-10T11:20:00Z',
    createdBy: 'SYSTEM_ADMIN',
    updatedBy: 'SYSTEM_ADMIN',
  },
  {
    id: 'emp-doc-1002',
    employeeId: 'EMP-1002',
    officialEmail: 's.connor@techknife.com',
    personalEmail: 'sarah.connor@gmail.com',
    primaryMobile: '+1 (555) 018-9921',
    alternateMobile: '+1 (555) 018-9922',
    firstName: 'Sarah',
    lastName: 'Connor',
    fullName: 'Sarah Connor',
    gender: 'FEMALE',
    dob: '1988-08-23',
    bloodGroup: 'A_POSITIVE',
    departmentId: 'Engineering & DevOps',
    designationId: 'Chief Technology Officer',
    managerId: 'emp-doc-1001',
    joiningDate: '2021-06-01',
    employmentType: 'FULL_TIME',
    salary: 210000,
    skills: ['System Architecture', 'Spring Security', 'Kubernetes', 'Java/TypeScript'],
    githubUsername: 'sconnor-tech',
    profileImage: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=300',
    status: 'ACTIVE',
    createdAt: '2021-06-01T09:00:00Z',
    updatedAt: '2026-02-14T14:10:00Z',
    createdBy: 'emp-doc-1001',
    updatedBy: 'emp-doc-1001',
  },
  {
    id: 'emp-doc-1003',
    employeeId: 'EMP-1003',
    officialEmail: 'm.brody@techknife.com',
    personalEmail: 'marcus.brody@gmail.com',
    primaryMobile: '+1 (555) 017-3342',
    alternateMobile: '',
    firstName: 'Marcus',
    lastName: 'Brody',
    fullName: 'Marcus Brody',
    gender: 'MALE',
    dob: '1990-11-05',
    bloodGroup: 'B_POSITIVE',
    departmentId: 'Product Management',
    designationId: 'Senior Engineering Manager',
    managerId: 'emp-doc-1002',
    joiningDate: '2022-01-10',
    employmentType: 'FULL_TIME',
    salary: 165000,
    skills: ['Agile Scrum', 'Roadmap Planning', 'Technical Leadership', 'React / Node'],
    githubUsername: 'mbrody-pm',
    profileImage: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300',
    status: 'ACTIVE',
    createdAt: '2022-01-10T09:00:00Z',
    updatedAt: '2026-03-01T10:00:00Z',
    createdBy: 'emp-doc-1002',
    updatedBy: 'emp-doc-1002',
  },
  {
    id: 'emp-doc-1004',
    employeeId: 'EMP-1004',
    officialEmail: 'e.rostova@techknife.com',
    personalEmail: 'elena.rostova@gmail.com',
    primaryMobile: '+1 (555) 016-8812',
    alternateMobile: '',
    firstName: 'Elena',
    lastName: 'Rostova',
    fullName: 'Elena Rostova',
    gender: 'FEMALE',
    dob: '1994-02-18',
    bloodGroup: 'AB_POSITIVE',
    departmentId: 'Engineering & DevOps',
    designationId: 'Senior Frontend Lead',
    managerId: 'emp-doc-1003',
    joiningDate: '2022-09-18',
    employmentType: 'FULL_TIME',
    salary: 140000,
    skills: ['React', 'TypeScript', 'Tailwind CSS', 'Performance Optimization'],
    githubUsername: 'erostova-dev',
    profileImage: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=300',
    status: 'ACTIVE',
    createdAt: '2022-09-18T09:00:00Z',
    updatedAt: '2026-03-12T16:30:00Z',
    createdBy: 'emp-doc-1003',
    updatedBy: 'emp-doc-1003',
  },
  {
    id: 'emp-doc-1005',
    employeeId: 'EMP-1005',
    officialEmail: 'd.miller@techknife.com',
    personalEmail: 'david.miller@gmail.com',
    primaryMobile: '+1 (555) 014-7723',
    alternateMobile: '',
    firstName: 'David',
    lastName: 'Miller',
    fullName: 'David Miller',
    gender: 'MALE',
    dob: '1992-07-30',
    bloodGroup: 'O_NEGATIVE',
    departmentId: 'Client Growth & CRM',
    designationId: 'Growth Lead',
    managerId: 'emp-doc-1001',
    joiningDate: '2023-02-01',
    employmentType: 'CONTRACT',
    salary: 125000,
    skills: ['HubSpot CRM', 'Key Account Management', 'Enterprise Sales'],
    githubUsername: 'dmiller-growth',
    profileImage: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=300',
    status: 'INACTIVE',
    createdAt: '2023-02-01T09:00:00Z',
    updatedAt: '2026-01-15T08:00:00Z',
    createdBy: 'emp-doc-1001',
    updatedBy: 'emp-doc-1001',
  },
];

export const employeeV2Api = {
  // 1. Create Employee
  async createEmployee(request: CreateEmployeeRequest): Promise<EmployeeResponse> {
    let created: EmployeeResponse | null = null;
    try {
      const response = await apiClient.post<ApiResponse<EmployeeResponse>>('/v2/employees', request);
      if (response.data?.data) {
        created = response.data.data;
      }
    } catch {
      // Fallback
    }

    if (!created) {
      const newDocId = `emp-doc-${Date.now()}`;
      created = {
        id: newDocId,
        employeeId: request.employeeId,
        officialEmail: request.officialEmail,
        personalEmail: request.personalEmail || '',
        primaryMobile: request.primaryMobile,
        alternateMobile: request.alternateMobile || '',
        firstName: request.firstName,
        lastName: request.lastName,
        fullName: `${request.firstName} ${request.lastName}`.trim(),
        gender: request.gender,
        dob: request.dob,
        bloodGroup: request.bloodGroup,
        departmentId: request.departmentId,
        designationId: request.designationId,
        managerId: request.managerId || '',
        joiningDate: request.joiningDate,
        employmentType: request.employmentType,
        salary: Number(request.salary) || 0,
        skills: request.skills || [],
        githubUsername: request.githubUsername || '',
        profileImage:
          request.profileImage ||
          `https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=300`,
        status: request.status || 'ACTIVE',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        createdBy: 'CURRENT_USER',
        updatedBy: 'CURRENT_USER',
      };
      mockEmployeesV2 = [created, ...mockEmployeesV2];
    }

    // Trigger Notification & Audit Logging
    logActivityAction(
      'Corporate Admin',
      'ROLE_ADMIN',
      'Employee Directory V2',
      'ONBOARD_EMPLOYEE',
      `Onboarded new staff member ${created.fullName} (${created.employeeId}) into ${created.departmentId}`
    );

    notificationsApi.sendNotification(
      'Employee Onboarded',
      `Staff member ${created.fullName} (${created.employeeId}) has been registered in ${created.departmentId} as ${created.designationId}.`,
      created.officialEmail,
      'success',
      ['DATABASE', 'BROWSER', 'EMAIL']
    );

    appendAuditDiff(
      'Employee',
      created.employeeId,
      'CREATE',
      null,
      {
        employeeId: created.employeeId,
        fullName: created.fullName,
        officialEmail: created.officialEmail,
        departmentId: created.departmentId,
        designationId: created.designationId,
        salary: created.salary,
        status: created.status,
      }
    );

    return created;
  },

  // 2. Update Employee
  async updateEmployee(id: string, request: UpdateEmployeeRequest): Promise<EmployeeResponse> {
    let updated: EmployeeResponse | null = null;
    let existingOldState: EmployeeResponse | undefined;

    const index = mockEmployeesV2.findIndex((e) => e.id === id || e.employeeId === id);
    if (index !== -1) {
      existingOldState = { ...mockEmployeesV2[index] };
    }

    try {
      const response = await apiClient.put<ApiResponse<EmployeeResponse>>(`/v2/employees/${id}`, request);
      if (response.data?.data) {
        updated = response.data.data;
      }
    } catch {
      // Fallback
    }

    if (!updated) {
      if (index !== -1 && existingOldState) {
        updated = {
          ...existingOldState,
          ...request,
          fullName: `${request.firstName || existingOldState.firstName} ${request.lastName || existingOldState.lastName}`.trim(),
          updatedAt: new Date().toISOString(),
        };
        mockEmployeesV2[index] = updated;
      } else {
        throw new Error(`Employee with ID ${id} not found.`);
      }
    }

    // Audit Logging & Notifications
    logActivityAction(
      'Corporate Admin',
      'ROLE_ADMIN',
      'Employee Directory V2',
      'UPDATE_EMPLOYEE',
      `Updated profile parameters for ${updated.fullName} (${updated.employeeId})`
    );

    notificationsApi.sendNotification(
      'Employee Profile Updated',
      `Profile details for ${updated.fullName} (${updated.employeeId}) were updated by Corporate HR.`,
      updated.officialEmail,
      'info',
      ['DATABASE', 'BROWSER']
    );

    appendAuditDiff(
      'Employee',
      updated.employeeId,
      'UPDATE',
      existingOldState
        ? {
            designationId: existingOldState.designationId,
            salary: existingOldState.salary,
            departmentId: existingOldState.departmentId,
            status: existingOldState.status,
          }
        : null,
      {
        designationId: updated.designationId,
        salary: updated.salary,
        departmentId: updated.departmentId,
        status: updated.status,
      }
    );

    return updated;
  },

  // 3. Get Employee By Document ID
  async getEmployeeById(id: string): Promise<EmployeeResponse> {
    try {
      const response = await apiClient.get<ApiResponse<EmployeeResponse>>(`/v2/employees/${id}`);
      if (response.data?.data) {
        return response.data.data;
      }
    } catch {
      // Fallback
    }

    const found = mockEmployeesV2.find((e) => e.id === id || e.employeeId === id);
    if (found) return found;
    throw new Error(`Employee with ID ${id} not found.`);
  },

  // 4. Get Employee By Employee Code
  async getEmployeeByCode(code: string): Promise<EmployeeResponse> {
    try {
      const response = await apiClient.get<ApiResponse<EmployeeResponse>>(`/v2/employees/code/${code}`);
      if (response.data?.data) {
        return response.data.data;
      }
    } catch {
      // Fallback
    }

    const found = mockEmployeesV2.find((e) => e.employeeId.toLowerCase() === code.toLowerCase());
    if (found) return found;
    throw new Error(`Employee with code ${code} not found.`);
  },

  // 5. Get Employee By Official Email
  async getEmployeeByEmail(officialEmail: string): Promise<EmployeeResponse> {
    try {
      const response = await apiClient.get<ApiResponse<EmployeeResponse>>(`/v2/employees/email/${officialEmail}`);
      if (response.data?.data) {
        return response.data.data;
      }
    } catch {
      // Fallback
    }

    const found = mockEmployeesV2.find((e) => e.officialEmail.toLowerCase() === officialEmail.toLowerCase());
    if (found) return found;
    throw new Error(`Employee with email ${officialEmail} not found.`);
  },

  // 6. Get All Employees with basic filters & pagination
  async getAllEmployees(params?: {
    page?: number;
    size?: number;
    search?: string;
    departmentId?: string;
    managerId?: string;
    status?: string;
  }): Promise<PagedResponse<EmployeeResponse>> {
    try {
      const response = await apiClient.get<ApiResponse<PagedResponse<EmployeeResponse>>>('/v2/employees', {
        params,
      });
      if (response.data?.data) {
        return response.data.data;
      }
    } catch {
      // Fallback
    }

    let filtered = [...mockEmployeesV2];

    if (params?.search) {
      const term = params.search.toLowerCase();
      filtered = filtered.filter(
        (e) =>
          e.fullName.toLowerCase().includes(term) ||
          e.employeeId.toLowerCase().includes(term) ||
          e.officialEmail.toLowerCase().includes(term) ||
          e.designationId.toLowerCase().includes(term)
      );
    }

    if (params?.departmentId && params.departmentId !== 'ALL') {
      filtered = filtered.filter((e) => e.departmentId === params.departmentId);
    }

    if (params?.managerId && params.managerId !== 'ALL') {
      filtered = filtered.filter((e) => e.managerId === params.managerId);
    }

    if (params?.status && params.status !== 'ALL') {
      filtered = filtered.filter((e) => e.status === params.status);
    }

    const page = params?.page ?? 0;
    const size = params?.size ?? 10;
    const start = page * size;
    const paginated = filtered.slice(start, start + size);
    const totalPages = Math.ceil(filtered.length / size) || 1;

    return {
      content: paginated,
      page,
      size,
      totalElements: filtered.length,
      totalPages,
      last: page >= totalPages - 1,
    };
  },

  // 7. Advanced Multi-Criteria Employee Search
  async searchEmployees(filter: EmployeeSearchFilter): Promise<PagedResponse<EmployeeSummaryResponse>> {
    try {
      const response = await apiClient.post<ApiResponse<PagedResponse<EmployeeSummaryResponse>>>(
        '/v2/employees/search',
        filter
      );
      if (response.data?.data) {
        return response.data.data;
      }
    } catch {
      // Fallback
    }

    let filtered = [...mockEmployeesV2];

    if (filter.searchTerm) {
      const q = filter.searchTerm.toLowerCase();
      filtered = filtered.filter(
        (e) =>
          e.fullName.toLowerCase().includes(q) ||
          e.officialEmail.toLowerCase().includes(q) ||
          e.employeeId.toLowerCase().includes(q)
      );
    }

    if (filter.departmentId && filter.departmentId !== 'ALL') {
      filtered = filtered.filter((e) => e.departmentId === filter.departmentId);
    }

    if (filter.designationId && filter.designationId !== 'ALL') {
      filtered = filtered.filter((e) => e.designationId === filter.designationId);
    }

    if (filter.managerId && filter.managerId !== 'ALL') {
      filtered = filtered.filter((e) => e.managerId === filter.managerId);
    }

    if (filter.status) {
      filtered = filtered.filter((e) => e.status === filter.status);
    }

    if (filter.employmentType) {
      filtered = filtered.filter((e) => e.employmentType === filter.employmentType);
    }

    if (filter.bloodGroup) {
      filtered = filtered.filter((e) => e.bloodGroup === filter.bloodGroup);
    }

    if (filter.skills && filter.skills.length > 0) {
      filtered = filtered.filter((e) =>
        filter.skills?.some((skill) => e.skills.map((s) => s.toLowerCase()).includes(skill.toLowerCase()))
      );
    }

    const page = filter.page ?? 0;
    const size = filter.size ?? 10;
    const start = page * size;
    const paginated = filtered.slice(start, start + size);
    const totalPages = Math.ceil(filtered.length / size) || 1;

    const summaries: EmployeeSummaryResponse[] = paginated.map((e) => ({
      id: e.id,
      employeeId: e.employeeId,
      fullName: e.fullName,
      officialEmail: e.officialEmail,
      primaryMobile: e.primaryMobile,
      departmentId: e.departmentId,
      designationId: e.designationId,
      employmentType: e.employmentType,
      status: e.status,
      profileImage: e.profileImage,
      joiningDate: e.joiningDate,
    }));

    return {
      content: summaries,
      page,
      size,
      totalElements: filtered.length,
      totalPages,
      last: page >= totalPages - 1,
    };
  },

  // 8. Get Employees by Department
  async getEmployeesByDepartment(departmentId: string): Promise<EmployeeResponse[]> {
    try {
      const response = await apiClient.get<ApiResponse<EmployeeResponse[]>>(`/v2/employees/department/${departmentId}`);
      if (response.data?.data) {
        return response.data.data;
      }
    } catch {
      // Fallback
    }

    return mockEmployeesV2.filter((e) => e.departmentId === departmentId);
  },

  // 9. Get Direct Reports for Manager
  async getDirectReports(managerId: string): Promise<EmployeeResponse[]> {
    try {
      const response = await apiClient.get<ApiResponse<EmployeeResponse[]>>(`/v2/employees/manager/${managerId}/reports`);
      if (response.data?.data) {
        return response.data.data;
      }
    } catch {
      // Fallback
    }

    return mockEmployeesV2.filter((e) => e.managerId === managerId);
  },

  // 10. Update Employee Status
  async updateEmployeeStatus(id: string, request: UpdateEmployeeStatusRequest): Promise<EmployeeResponse> {
    let updated: EmployeeResponse | null = null;
    let oldStatus: EmployeeStatus = 'ACTIVE';

    const index = mockEmployeesV2.findIndex((e) => e.id === id || e.employeeId === id);
    if (index !== -1) {
      oldStatus = mockEmployeesV2[index].status;
    }

    try {
      const response = await apiClient.patch<ApiResponse<EmployeeResponse>>(`/v2/employees/${id}/status`, request);
      if (response.data?.data) {
        updated = response.data.data;
      }
    } catch {
      // Fallback
    }

    if (!updated) {
      if (index !== -1) {
        mockEmployeesV2[index].status = request.status;
        mockEmployeesV2[index].updatedAt = new Date().toISOString();
        updated = mockEmployeesV2[index];
      } else {
        throw new Error(`Employee with ID ${id} not found.`);
      }
    }

    logActivityAction(
      'Corporate Admin',
      'ROLE_ADMIN',
      'Employee Directory V2',
      'STATUS_CHANGE',
      `Changed status for ${updated.fullName} (${updated.employeeId}) from ${oldStatus} to ${request.status}`
    );

    notificationsApi.sendNotification(
      'Employee Status Changed',
      `Employment status for ${updated.fullName} (${updated.employeeId}) was transitioned to ${request.status}. Reason: ${request.statusReason || 'Administrative decision'}.`,
      updated.officialEmail,
      request.status === 'TERMINATED' || request.status === 'SUSPENDED' ? 'warning' : 'info',
      ['DATABASE', 'BROWSER', 'EMAIL']
    );

    appendAuditDiff(
      'Employee',
      updated.employeeId,
      'STATUS_CHANGE',
      { status: oldStatus },
      { status: request.status, statusReason: request.statusReason }
    );

    return updated;
  },

  // 11. Delete Employee Record
  async deleteEmployee(id: string): Promise<boolean> {
    const found = mockEmployeesV2.find((e) => e.id === id || e.employeeId === id);

    try {
      await apiClient.delete(`/v2/employees/${id}`);
    } catch {
      // Fallback
    }

    mockEmployeesV2 = mockEmployeesV2.filter((e) => e.id !== id && e.employeeId !== id);

    if (found) {
      logActivityAction(
        'Corporate Admin',
        'ROLE_SUPER_ADMIN',
        'Employee Directory V2',
        'DELETE_EMPLOYEE',
        `Permanently deleted employee record for ${found.fullName} (${found.employeeId})`
      );

      notificationsApi.sendNotification(
        'Employee Record Deleted',
        `Employee document for ${found.fullName} (${found.employeeId}) was deleted from corporate database.`,
        'hr@techknife.com',
        'warning',
        ['DATABASE', 'BROWSER']
      );

      appendAuditDiff('Employee', found.employeeId, 'DELETE', { fullName: found.fullName, status: found.status }, null);
    }

    return true;
  },
};

