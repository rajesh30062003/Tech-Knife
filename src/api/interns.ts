import { apiClient } from './client';
import { Intern, InternStats, InternTask, InternStatus } from '../types';

export let mockInterns: Intern[] = [
  {
    id: 'int-101',
    internId: 'INT-2026-001',
    firstName: 'Lucas',
    lastName: 'Chen',
    officialEmail: 'l.chen@techknife.com',
    personalEmail: 'lucas.chen.dev@gmail.com',
    primaryMobile: '+1 (555) 234-5678',
    alternateMobile: '+1 (555) 876-5432',
    college: 'UC Berkeley College of Engineering',
    university: 'University of California, Berkeley',
    degree: 'Bachelor of Science',
    branch: 'Computer Science',
    semester: '7th Semester',
    cgpa: 3.92,
    resumeUrl: 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?auto=format&fit=crop&q=80&w=300',
    offerLetterUrl: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80&w=300',
    joiningDate: '2026-05-01',
    endDate: '2026-11-01',
    mentor: 'Sarah Connor (CTO)',
    mentorId: 'EMP-102',
    department: 'Engineering & DevOps',
    skills: ['Spring Boot 3', 'Kubernetes', 'Docker', 'MongoDB', 'Redis'],
    githubUsername: 'lucaschen-tech',
    performanceScore: 92,
    attendance: 98,
    status: 'Active',
    certificateGenerated: false,
    stipend: '$3,800/mo',
    assignedProjects: ['PRJ-101: Cloud Migration', 'PRJ-104: Kubernetes Infra'],
    dailyTasks: [
      { id: 'dt-1', title: 'Implement Redis Session Cache Interceptor', type: 'daily', dueDate: 'Today', status: 'Approved', score: 95, feedback: 'Great error handling!' },
      { id: 'dt-2', title: 'Write DTO Validation for User Service', type: 'daily', dueDate: 'Tomorrow', status: 'Submitted' }
    ],
    weeklyTasks: [
      { id: 'wt-1', title: 'Weekly Kubernetes Cluster Load Test Benchmark', type: 'weekly', dueDate: 'End of Week', status: 'Pending' }
    ]
  },
  {
    id: 'int-102',
    internId: 'INT-2026-002',
    firstName: 'Maya',
    lastName: 'Patel',
    officialEmail: 'm.patel@techknife.com',
    personalEmail: 'maya.patel.code@gmail.com',
    primaryMobile: '+1 (555) 345-6789',
    college: 'Stanford School of Engineering',
    university: 'Stanford University',
    degree: 'Master of Science',
    branch: 'Software Systems',
    semester: '3rd Semester',
    cgpa: 3.98,
    resumeUrl: 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?auto=format&fit=crop&q=80&w=300',
    offerLetterUrl: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80&w=300',
    joiningDate: '2026-06-01',
    endDate: '2026-12-01',
    mentor: 'Marcus Brody (Engineering Manager)',
    mentorId: 'EMP-103',
    department: 'Frontend Engineering',
    skills: ['React 19', 'TypeScript', 'Tailwind CSS', 'Framer Motion', 'State Management'],
    githubUsername: 'mayapatel-ui',
    performanceScore: 96,
    attendance: 100,
    status: 'Active',
    certificateGenerated: false,
    stipend: '$4,000/mo',
    assignedProjects: ['PRJ-102: Enterprise Customer Portal V2'],
    dailyTasks: [
      { id: 'dt-3', title: 'Build Responsive Employee Dashboard Grid', type: 'daily', dueDate: 'Today', status: 'Approved', score: 98, feedback: 'Pixel perfect layout.' }
    ],
    weeklyTasks: [
      { id: 'wt-2', title: 'Audit Accessibility (WCAG 2.1 AA) across portal', type: 'weekly', dueDate: 'End of Week', status: 'Submitted' }
    ]
  },
  {
    id: 'int-103',
    internId: 'INT-2026-003',
    firstName: 'James',
    lastName: 'Wilson',
    officialEmail: 'j.wilson@techknife.com',
    personalEmail: 'j.wilson.mit@gmail.com',
    primaryMobile: '+1 (555) 456-7890',
    college: 'MIT School of Engineering',
    university: 'Massachusetts Institute of Technology',
    degree: 'Bachelor of Science',
    branch: 'Electrical Engineering & Computer Science',
    semester: '5th Semester',
    cgpa: 3.85,
    resumeUrl: 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?auto=format&fit=crop&q=80&w=300',
    joiningDate: '2026-07-01',
    endDate: '2027-01-01',
    mentor: 'Elena Rostova (Frontend Lead)',
    mentorId: 'EMP-104',
    department: 'Engineering & DevOps',
    skills: ['Java 21', 'Spring Security', 'GraphQL', 'MongoDB'],
    githubUsername: 'jwilson-mit',
    performanceScore: 84,
    attendance: 94,
    status: 'Active',
    certificateGenerated: false,
    stipend: '$3,800/mo',
    assignedProjects: ['PRJ-103: Microservices Auth Engine']
  },
  {
    id: 'int-104',
    internId: 'INT-2026-004',
    firstName: 'Sofia',
    lastName: 'Rodriguez',
    officialEmail: 's.rodriguez@techknife.com',
    personalEmail: 'sofia.rod.cmu@gmail.com',
    primaryMobile: '+1 (555) 567-8901',
    college: 'Carnegie Mellon School of Computer Science',
    university: 'Carnegie Mellon University',
    degree: 'Master of Science',
    branch: 'Artificial Intelligence',
    semester: 'Graduated',
    cgpa: 4.0,
    resumeUrl: 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?auto=format&fit=crop&q=80&w=300',
    offerLetterUrl: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80&w=300',
    joiningDate: '2026-01-10',
    endDate: '2026-07-10',
    mentor: 'Alexander Vance (MD)',
    mentorId: 'EMP-101',
    department: 'AI Systems & Analytics',
    skills: ['Python', 'Gemini API', 'PyTorch', 'FastAPI', 'LangChain'],
    githubUsername: 'sofia-ai-cmu',
    performanceScore: 99,
    attendance: 100,
    status: 'Graduated',
    certificateGenerated: true,
    certificateUrl: 'https://techknife.com/certificates/INT-2026-004.pdf',
    stipend: '$4,200/mo',
    finalEvaluation: {
      technicalRating: 5,
      softSkillsRating: 5,
      codeQualityRating: 5,
      overallFeedback: 'Exceptional performance. Recommended for full-time Senior AI Engineer PPO role.',
      ppoRecommendation: true
    }
  }
];

export const internsApi = {
  // GET /api/interns
  async getInterns(params?: {
    search?: string;
    department?: string;
    status?: string;
    mentor?: string;
    page?: number;
    limit?: number;
  }): Promise<{ interns: Intern[]; total: number; totalPages: number }> {
    try {
      const res = await apiClient.get('/interns', { params });
      if (res.data?.data) {
        return {
          interns: res.data.data,
          total: res.data.total || res.data.data.length,
          totalPages: res.data.totalPages || 1
        };
      }
    } catch {
      // Fallback to local store
    }

    let filtered = [...mockInterns];

    if (params?.search) {
      const q = params.search.toLowerCase();
      filtered = filtered.filter(
        i =>
          `${i.firstName} ${i.lastName}`.toLowerCase().includes(q) ||
          i.officialEmail.toLowerCase().includes(q) ||
          i.internId.toLowerCase().includes(q) ||
          i.university.toLowerCase().includes(q) ||
          i.department.toLowerCase().includes(q)
      );
    }

    if (params?.department && params.department !== 'ALL') {
      filtered = filtered.filter(i => i.department === params.department);
    }

    if (params?.status && params.status !== 'ALL') {
      filtered = filtered.filter(i => i.status === params.status);
    }

    if (params?.mentor && params.mentor !== 'ALL') {
      filtered = filtered.filter(i => i.mentor.includes(params.mentor!));
    }

    const page = params?.page || 1;
    const limit = params?.limit || 10;
    const startIndex = (page - 1) * limit;
    const paginated = filtered.slice(startIndex, startIndex + limit);
    const totalPages = Math.ceil(filtered.length / limit) || 1;

    return {
      interns: paginated,
      total: filtered.length,
      totalPages
    };
  },

  // GET /api/interns/:id
  async getInternById(id: string): Promise<Intern | null> {
    try {
      const res = await apiClient.get(`/interns/${id}`);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }
    return mockInterns.find(i => i.id === id || i.internId === id) || null;
  },

  // POST /api/interns
  async createIntern(data: Partial<Intern>): Promise<Intern> {
    try {
      const res = await apiClient.post('/interns', data);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const count = mockInterns.length + 1;
    const newInternId = `INT-2026-${count < 10 ? '00' : count < 100 ? '0' : ''}${count}`;

    const created: Intern = {
      id: `int-${Date.now()}`,
      internId: newInternId,
      firstName: data.firstName || 'New',
      lastName: data.lastName || 'Intern',
      officialEmail: data.officialEmail || `${data.firstName?.toLowerCase() || 'intern'}.${data.lastName?.toLowerCase() || 'dev'}@techknife.com`,
      personalEmail: data.personalEmail || 'personal@gmail.com',
      primaryMobile: data.primaryMobile || '+1 (555) 000-1111',
      college: data.college || 'State Engineering College',
      university: data.university || 'State University',
      degree: data.degree || 'Bachelor of Technology',
      branch: data.branch || 'Computer Science & Engineering',
      semester: data.semester || '6th Semester',
      cgpa: Number(data.cgpa) || 3.8,
      joiningDate: data.joiningDate || new Date().toISOString().split('T')[0],
      endDate: data.endDate || '2027-01-01',
      mentor: data.mentor || 'Sarah Connor (CTO)',
      department: data.department || 'Engineering & DevOps',
      skills: data.skills || ['Java', 'Spring Boot', 'React'],
      githubUsername: data.githubUsername || 'techknife-intern',
      performanceScore: 85,
      attendance: 100,
      status: 'Active',
      certificateGenerated: false,
      stipend: data.stipend || '$3,800/mo'
    };

    mockInterns = [created, ...mockInterns];
    return created;
  },

  // PUT /api/interns/:id
  async updateIntern(id: string, updates: Partial<Intern>): Promise<Intern> {
    try {
      const res = await apiClient.put(`/interns/${id}`, updates);
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const idx = mockInterns.findIndex(i => i.id === id || i.internId === id);
    if (idx !== -1) {
      mockInterns[idx] = { ...mockInterns[idx], ...updates };
      return mockInterns[idx];
    }
    throw new Error('Intern not found');
  },

  // PATCH /api/interns/:id/status
  async updateInternStatus(id: string, status: InternStatus): Promise<Intern> {
    return this.updateIntern(id, { status });
  },

  // DELETE /api/interns/:id
  async deleteIntern(id: string): Promise<boolean> {
    try {
      await apiClient.delete(`/interns/${id}`);
    } catch {
      // Fallback
    }
    mockInterns = mockInterns.filter(i => i.id !== id && i.internId !== id);
    return true;
  },

  // POST /api/interns/:id/tasks
  async assignTask(id: string, task: Partial<InternTask>): Promise<InternTask> {
    const newTask: InternTask = {
      id: `task-${Date.now()}`,
      title: task.title || 'New Assigned Milestone',
      type: task.type || 'daily',
      dueDate: task.dueDate || 'Tomorrow',
      status: 'Pending'
    };

    const intern = mockInterns.find(i => i.id === id || i.internId === id);
    if (intern) {
      if (task.type === 'weekly') {
        intern.weeklyTasks = [...(intern.weeklyTasks || []), newTask];
      } else {
        intern.dailyTasks = [...(intern.dailyTasks || []), newTask];
      }
    }
    return newTask;
  },

  // POST /api/interns/:id/generate-certificate
  async generateCertificate(id: string): Promise<{ certificateUrl: string }> {
    const certificateUrl = `https://techknife.com/certificates/${id}-2026-SEAL.pdf`;
    await this.updateIntern(id, {
      certificateGenerated: true,
      certificateUrl
    });
    return { certificateUrl };
  },

  // POST /api/interns/:id/evaluate
  async evaluateIntern(id: string, evaluation: NonNullable<Intern['finalEvaluation']>): Promise<Intern> {
    const avgScore = Math.round(
      ((evaluation.technicalRating + evaluation.softSkillsRating + evaluation.codeQualityRating) / 15) * 100
    );
    return this.updateIntern(id, {
      finalEvaluation: evaluation,
      performanceScore: avgScore
    });
  },

  // POST /api/interns/:id/convert-to-employee
  async convertToEmployee(id: string, designation: string, salary: number): Promise<{ employeeId: string }> {
    const intern = mockInterns.find(i => i.id === id || i.internId === id);
    if (!intern) throw new Error('Intern not found');

    const newEmpId = `EMP-${Math.floor(200 + Math.random() * 800)}`;
    
    await this.updateIntern(id, {
      status: 'Converted to Employee'
    });

    return { employeeId: newEmpId };
  },

  // GET /api/interns/statistics
  async getStatistics(): Promise<InternStats> {
    try {
      const res = await apiClient.get('/interns/statistics');
      if (res.data?.data) return res.data.data;
    } catch {
      // Fallback
    }

    const totalInterns = mockInterns.length;
    const activeCount = mockInterns.filter(i => i.status === 'Active').length;
    const graduatedCount = mockInterns.filter(i => i.status === 'Graduated' || i.status === 'Converted to Employee').length;
    const suspendedCount = mockInterns.filter(i => i.status === 'Suspended').length;
    const totalScoreSum = mockInterns.reduce((acc, curr) => acc + curr.performanceScore, 0);
    const avgScore = totalInterns ? Math.round(totalScoreSum / totalInterns) : 0;
    const certsCount = mockInterns.filter(i => i.certificateGenerated).length;

    return {
      totalInterns,
      activeCount,
      graduatedCount,
      suspendedCount,
      averagePerformanceScore: avgScore,
      ppoConversionRate: 92.5,
      certificatesIssuedCount: certsCount
    };
  }
};
