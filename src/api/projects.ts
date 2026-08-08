import { apiClient } from './client';
import { ApiResponse } from '../types';

export interface ProjectLinksData {
  githubUrl?: string | null;
  frontendRepoUrl?: string | null;
  backendRepoUrl?: string | null;
  dockerRepoUrl?: string | null;
  cicdPipelineUrl?: string | null;
  deploymentUrl?: string | null;
  stagingUrl?: string | null;
  productionUrl?: string | null;
  testingUrl?: string | null;
  kubernetesDashboardUrl?: string | null;
  serverUrl?: string | null;
  domainName?: string | null;
  documentationUrl?: string | null;
  apiDocUrl?: string | null;
  swaggerUrl?: string | null;
  figmaUrl?: string | null;
  googleDriveUrl?: string | null;
  driveUrl?: string | null;
  jiraUrl?: string | null;
  notionUrl?: string | null;
  confluenceUrl?: string | null;
  monitoringUrl?: string | null;
  envRef?: string | null;
  serverDetails?: string | null;
}

export interface ProjectMember {
  employeeId?: string;
  employeeName?: string;
  role?: string;
  allocationPercentage?: number;
  joinedDate?: string;
  designation?: string;
  department?: string;
}

export interface EnterpriseProject {
  id?: string;
  projectId?: string;
  projectCode?: string;
  projectName?: string;
  shortName?: string;
  description?: string;
  objectives?: string;
  client?: string;
  clientId?: string | null;
  clientOrganization?: string | null;
  department?: string | null;
  category?: string | null;
  businessUnit?: string | null;
  projectType?: string | null;
  status?: string | null;
  priority?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  targetEndDate?: string | null;
  estimatedCompletion?: string | null;
  estimatedHours?: number | null;
  estimatedDuration?: number | null;
  budget?: number | null;
  estimatedCost?: number | null;
  progressPercentage?: number | null;
  technologyStack?: string[] | null;
  programmingLanguages?: string[] | null;
  frameworks?: string[] | null;
  databaseTech?: string | null;
  cloudProvider?: string | null;
  repositoryUrl?: string | null;
  repositoryType?: string | null;
  repositoryVisibility?: string | null;
  projectVisibility?: string | null;
  deploymentType?: string | null;
  projectManagerId?: string | null;
  projectManagerName?: string | null;
  projectLeadId?: string | null;
  projectLeadName?: string | null;
  projectSponsor?: string | null;
  customerRepresentative?: string | null;
  assignedEmployees?: string[] | null;
  assignedInterns?: string[] | null;
  members?: ProjectMember[] | null;
  links?: ProjectLinksData | null;
  remarks?: string | null;
  tags?: string[] | null;
  logoUrl?: string | null;
  overallProgressPercentage?: number | null;
  pendingStatusRequest?: {
    requestedStatus: string;
    reason?: string;
    requestedBy: string;
    requestedByRole?: string;
    requestedAt: string;
  } | null;
  totalTasks?: number | null;
  completedTasks?: number | null;
  createdAt?: string | null;
  updatedAt?: string | null;
  createdBy?: string | null;
  updatedBy?: string | null;
}

export interface ProjectActivity {
  id?: string;
  projectId?: string;
  projectCode?: string;
  action?: string;
  activityType?: string;
  description?: string;
  user?: string;
  performedBy?: string;
  performedByEmail?: string;
  userRole?: string;
  timestamp?: string;
  details?: string;
  field?: string;
  fieldModified?: string;
  oldValue?: string;
  newValue?: string;
}

export const projectsApi = {
  getAll: async (params?: { status?: string; category?: string }): Promise<ApiResponse<EnterpriseProject[]>> => {
    const res = await apiClient.get<ApiResponse<EnterpriseProject[]>>('/projects', { params });
    return res.data;
  },

  getProjects: async (): Promise<ApiResponse<EnterpriseProject[]>> => {
    const res = await apiClient.get<ApiResponse<EnterpriseProject[]>>('/projects');
    return res.data;
  },

  getById: async (id: string): Promise<ApiResponse<EnterpriseProject>> => {
    const res = await apiClient.get<ApiResponse<EnterpriseProject>>(`/projects/${id}`);
    return res.data;
  },

  getProjectById: async (id: string): Promise<ApiResponse<EnterpriseProject>> => {
    const res = await apiClient.get<ApiResponse<EnterpriseProject>>(`/projects/${id}`);
    return res.data;
  },

  create: async (data: Partial<EnterpriseProject>): Promise<ApiResponse<EnterpriseProject>> => {
    const res = await apiClient.post<ApiResponse<EnterpriseProject>>('/projects', data);
    return res.data;
  },

  update: async (id: string, data: Partial<EnterpriseProject>): Promise<ApiResponse<EnterpriseProject>> => {
    const res = await apiClient.put<ApiResponse<EnterpriseProject>>(`/projects/${id}`, data);
    return res.data;
  },

  updateStatus: async (id: string, status: string, reason?: string, progressPercentage?: number): Promise<ApiResponse<EnterpriseProject>> => {
    const res = await apiClient.patch<ApiResponse<EnterpriseProject>>(`/projects/${id}/status`, { 
      status, 
      reason: reason || 'Status update',
      progressPercentage 
    });
    return res.data;
  },

  requestStatusChange: async (id: string, requestData: {
    requestedStatus: string;
    reason?: string;
    requestedBy?: string;
    requestedByRole?: string;
  }): Promise<ApiResponse<EnterpriseProject>> => {
    const res = await apiClient.post<ApiResponse<EnterpriseProject>>(`/projects/${id}/status-request`, requestData);
    return res.data;
  },

  assignMembers: async (id: string, assignData: {
    projectManagerId?: string;
    projectLeadId?: string;
    assignedEmployees?: string[];
    assignedInterns?: string[];
    employeeIds?: string[];
    internIds?: string[];
  }): Promise<ApiResponse<EnterpriseProject>> => {
    const empIds = assignData.employeeIds || assignData.assignedEmployees || [];
    const intIds = assignData.internIds || assignData.assignedInterns || [];

    const payload = {
      projectManagerId: assignData.projectManagerId || '',
      projectLeadId: assignData.projectLeadId || '',
      employeeIds: empIds,
      assignedEmployees: empIds,
      internIds: intIds,
      assignedInterns: intIds,
    };

    const res = await apiClient.put<ApiResponse<EnterpriseProject>>(`/projects/${id}/members`, payload);
    return res.data;
  },

  updateLinks: async (id: string, linksData: {
    links: ProjectLinksData;
    repositoryVisibility?: string;
    deploymentType?: string;
  }): Promise<ApiResponse<EnterpriseProject>> => {
    const res = await apiClient.put<ApiResponse<EnterpriseProject>>(`/projects/${id}/links`, linksData);
    return res.data;
  },

  delete: async (id: string): Promise<ApiResponse<void>> => {
    const res = await apiClient.delete<ApiResponse<void>>(`/projects/${id}`);
    return res.data;
  },

  getActivities: async (id: string): Promise<ApiResponse<ProjectActivity[]>> => {
    const res = await apiClient.get<ApiResponse<ProjectActivity[]>>(`/projects/${id}/activities`);
    return res.data;
  }
};
