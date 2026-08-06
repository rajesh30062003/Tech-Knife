import { apiClient } from './client';
import { ApiResponse } from '../types';

export interface DriveFileRecord {
  id: string;
  fileId: string;
  name: string;
  originalFileName?: string;
  projectCode?: string;
  category?: string;
  uploadedBy?: string;
  uploadedAt?: string;
  fileSize?: number;
  mimeType?: string;
  webViewLink?: string;
  webContentLink?: string;
  secureUrl?: string;
}

export interface ProjectTask {
  id: string;
  taskCode?: string;
  title: string;
  description?: string;
  status: 'Backlog' | 'In Progress' | 'Code Review' | 'Completed' | string;
  priority: 'Urgent' | 'High' | 'Medium' | 'Low' | string;
  assigneeName?: string;
  assigneeId?: string;
  dueDate?: string;
  createdAt?: string;
}

export interface ProjectRisk {
  id: string;
  riskCode?: string;
  title: string;
  description?: string;
  severity: 'HIGH' | 'MEDIUM' | 'LOW' | string;
  impact?: string;
  likelihood?: string;
  mitigationPlan?: string;
  status: 'IDENTIFIED' | 'MITIGATED' | 'CLOSED' | string;
  reportedBy?: string;
  createdAt?: string;
}

export const projectWorkspaceApi = {
  // Google Drive Documents
  getDriveDocuments: async (projectCode: string): Promise<ApiResponse<DriveFileRecord[]>> => {
    const res = await apiClient.get<ApiResponse<DriveFileRecord[]>>(`/drive/project/${projectCode}`);
    return res.data;
  },

  uploadDriveDocument: async (file: File, projectCode: string, category: string, uploadedBy: string): Promise<ApiResponse<DriveFileRecord>> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('projectCode', projectCode);
    formData.append('category', category);
    formData.append('uploadedBy', uploadedBy);

    const res = await apiClient.post<ApiResponse<DriveFileRecord>>('/drive/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },

  deleteDriveDocument: async (fileId: string): Promise<ApiResponse<void>> => {
    const res = await apiClient.delete<ApiResponse<void>>(`/drive/${fileId}`);
    return res.data;
  },

  // Project Tasks
  getTasks: async (projectId: string): Promise<ApiResponse<ProjectTask[]>> => {
    const res = await apiClient.get<ApiResponse<ProjectTask[]>>(`/projects/${projectId}/tasks`);
    return res.data;
  },

  createTask: async (projectId: string, task: Partial<ProjectTask>): Promise<ApiResponse<ProjectTask>> => {
    const res = await apiClient.post<ApiResponse<ProjectTask>>(`/projects/${projectId}/tasks`, task);
    return res.data;
  },

  updateTaskStatus: async (projectId: string, taskId: string, status: string): Promise<ApiResponse<ProjectTask>> => {
    const res = await apiClient.patch<ApiResponse<ProjectTask>>(`/projects/${projectId}/tasks/${taskId}/status`, { status });
    return res.data;
  },

  // Project Risks
  getRisks: async (projectId: string): Promise<ApiResponse<ProjectRisk[]>> => {
    const res = await apiClient.get<ApiResponse<ProjectRisk[]>>(`/projects/${projectId}/risks`);
    return res.data;
  },

  createRisk: async (projectId: string, risk: Partial<ProjectRisk>): Promise<ApiResponse<ProjectRisk>> => {
    const res = await apiClient.post<ApiResponse<ProjectRisk>>(`/projects/${projectId}/risks`, risk);
    return res.data;
  },
};
