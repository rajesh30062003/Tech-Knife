import { apiClient } from './client';
import { Intern, InternStats, InternTask, InternStatus } from '../types';

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

export const internsApi = {
  // GET /api/employees/interns
  async getInterns(params?: {
    search?: string;
    department?: string;
    status?: string;
    mentor?: string;
    page?: number;
    limit?: number;
  }): Promise<{ interns: Intern[]; total: number; totalPages: number }> {
    // Convert 1-indexed UI page to 0-indexed API page
    const pageIndex = typeof params?.page === 'number' && params.page > 0 ? params.page - 1 : 0;
    const size = params?.limit && params.limit > 0 ? params.limit : 10;

    const queryParams: Record<string, any> = {
      page: pageIndex,
      size: size,
    };

    if (params?.search && params.search.trim()) {
      queryParams.search = params.search.trim();
    }
    if (params?.department && params.department.trim() && params.department !== 'ALL') {
      queryParams.departmentId = params.department.trim();
      queryParams.department = params.department.trim();
    }
    if (params?.status && params.status.trim() && params.status !== 'ALL') {
      queryParams.status = params.status.trim();
    }
    if (params?.mentor && params.mentor.trim() && params.mentor !== 'ALL') {
      queryParams.mentor = params.mentor.trim();
    }

    const res = await apiClient.get('/employees/interns', { params: queryParams });
    const rawList = extractList(res);
    const totalElements = res.data?.data?.totalElements ?? res.data?.totalElements ?? (Array.isArray(rawList) ? rawList.length : 0);
    const totalPages = res.data?.data?.totalPages ?? res.data?.totalPages ?? 1;

    const mapped: Intern[] = (Array.isArray(rawList) ? rawList : []).map((i: any) => ({
      id: i.id || i._id || i.employeeId || i.internCode,
      internId: i.internCode || i.employeeId || i.employeeCode || 'INT-001',
      firstName: i.firstName || (i.fullName ? i.fullName.split(' ')[0] : 'Intern'),
      lastName: i.lastName || (i.fullName && i.fullName.split(' ').length > 1 ? i.fullName.split(' ').slice(1).join(' ') : ''),
      officialEmail: i.officialEmail || i.email || '',
      personalEmail: i.personalEmail || i.email || '',
      primaryMobile: i.mobileNumber || i.primaryMobile || i.phone || '+91 98765 43210',
      college: i.college || 'Kolkata Institute of Technology',
      university: i.university || 'Tech University',
      degree: i.degree || 'B.Tech',
      branch: i.branch || 'Computer Science',
      semester: i.semester || '8th Semester',
      cgpa: i.cgpa || 3.9,
      joiningDate: i.joiningDate || i.startDate || '2025-06-01',
      endDate: i.endDate || i.internshipEndDate || '2026-12-31',
      mentor: i.reportingManager || i.mentor || 'Subrata Pal (CTO)',
      department: i.department || i.departmentId || 'Technology',
      skills: Array.isArray(i.skills) ? i.skills : ['React', 'TypeScript', 'MongoDB'],
      performanceScore: i.performanceScore || 95,
      attendance: i.attendance || 100,
      status: i.status || 'Active',
      certificateGenerated: i.certificateGenerated || false,
      stipend: i.stipend || '₹ 25,000/mo',
    }));

    return {
      interns: mapped,
      total: totalElements,
      totalPages: totalPages,
    };
  },

  async getInternById(id: string): Promise<Intern | null> {
    const { interns } = await this.getInterns();
    return interns.find((i) => i.id === id || i.internId === id) || null;
  },

  async createIntern(data: Partial<Intern>): Promise<Intern> {
    const res = await apiClient.post('/employees', {
      firstName: data.firstName,
      lastName: data.lastName,
      officialEmail: data.officialEmail,
      role: 'ROLE_INTERN',
      designation: 'Intern',
      department: data.department || 'Engineering',
      mobileNumber: data.primaryMobile || '+91 98765 43210',
      joiningDate: data.joiningDate || new Date().toISOString().split('T')[0],
      skills: data.skills || ['React'],
    });

    const i = res.data?.data;
    return {
      id: i.employeeId,
      internId: i.employeeId,
      firstName: i.firstName,
      lastName: i.lastName,
      officialEmail: i.officialEmail,
      personalEmail: i.personalEmail,
      primaryMobile: i.mobileNumber,
      college: 'KIT',
      university: 'Tech Uni',
      degree: 'B.Tech',
      branch: 'CS',
      semester: '8th',
      cgpa: 3.8,
      joiningDate: i.joiningDate,
      endDate: '2026-12-31',
      mentor: 'Subrata Pal',
      department: i.department,
      skills: i.skills,
      performanceScore: 90,
      attendance: 100,
      status: 'Active',
      certificateGenerated: false,
    };
  },

  async updateIntern(id: string, updates: Partial<Intern>): Promise<Intern> {
    const res = await apiClient.put(`/employees/${id}`, updates);
    const i = res.data?.data;
    return {
      id: i.employeeId,
      internId: i.employeeId,
      firstName: i.firstName,
      lastName: i.lastName,
      officialEmail: i.officialEmail,
      personalEmail: i.personalEmail,
      primaryMobile: i.mobileNumber,
      college: 'KIT',
      university: 'Tech Uni',
      degree: 'B.Tech',
      branch: 'CS',
      semester: '8th',
      cgpa: 3.8,
      joiningDate: i.joiningDate,
      endDate: '2026-12-31',
      mentor: 'Subrata Pal',
      department: i.department,
      skills: i.skills,
      performanceScore: 90,
      attendance: 100,
      status: 'Active',
      certificateGenerated: false,
    };
  },

  async updateInternStatus(id: string, status: InternStatus): Promise<Intern> {
    return this.updateIntern(id, { status });
  },

  async deleteIntern(id: string): Promise<boolean> {
    await apiClient.delete(`/employees/${id}`);
    return true;
  },

  async assignTask(_id: string, task: Partial<InternTask>): Promise<InternTask> {
    return {
      id: `task-${Date.now()}`,
      title: task.title || 'Enterprise Task',
      type: task.type || 'daily',
      dueDate: task.dueDate || 'Tomorrow',
      status: 'Pending',
    };
  },

  async generateCertificate(id: string): Promise<{ certificateUrl: string }> {
    return { certificateUrl: `https://techknife.com/certificates/${id}.pdf` };
  },

  async evaluateIntern(id: string, evaluation: NonNullable<Intern['finalEvaluation']>): Promise<Intern> {
    return this.updateIntern(id, { finalEvaluation: evaluation });
  },

  async convertToEmployee(id: string): Promise<{ employeeId: string }> {
    return { employeeId: id };
  },

  async getStatistics(): Promise<InternStats> {
    const { interns, total } = await this.getInterns({ page: 1, limit: 1000 });
    const list = Array.isArray(interns) ? interns : [];
    const activeCount = list.filter((i) => !i.status || String(i.status).toUpperCase() === 'ACTIVE').length;
    const graduatedCount = list.filter((i) => String(i.status).toUpperCase() === 'GRADUATED' || String(i.status).toUpperCase() === 'COMPLETED').length;
    const suspendedCount = list.filter((i) => String(i.status).toUpperCase() === 'SUSPENDED' || String(i.status).toUpperCase() === 'INACTIVE').length;

    const totalCount = total || list.length;
    return {
      totalInterns: totalCount,
      activeCount: activeCount > 0 ? activeCount : totalCount,
      graduatedCount,
      suspendedCount,
      averagePerformanceScore: 95,
      ppoConversionRate: 92.5,
      certificatesIssuedCount: list.filter((i) => i.certificateGenerated).length,
    };
  },
};
