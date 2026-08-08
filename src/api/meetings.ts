import { apiClient } from './client';
import { ApiResponse } from '../types';

export interface MeetingItem {
  id?: string;
  title: string;
  entityType?: string;
  entityId?: string;
  agenda?: string;
  participants?: string[];
  dateTime?: string;
  meetingTime?: string;
  meetingNotes?: string;
  host?: string;
  organizerId?: string;
  status?: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED';
  link?: string;
  meetingLink?: string;
  createdAt?: string;
  updatedAt?: string;
}

export const meetingsApi = {
  getByEntity: async (entityType: string, entityId: string): Promise<ApiResponse<MeetingItem[]>> => {
    const res = await apiClient.get<ApiResponse<MeetingItem[]>>(`/crm/meetings/entity?entityType=${entityType}&entityId=${entityId}`);
    return res.data;
  },

  create: async (data: Partial<MeetingItem>): Promise<ApiResponse<MeetingItem>> => {
    const res = await apiClient.post<ApiResponse<MeetingItem>>('/crm/meetings', data);
    return res.data;
  },

  update: async (id: string, data: Partial<MeetingItem>): Promise<ApiResponse<MeetingItem>> => {
    const res = await apiClient.put<ApiResponse<MeetingItem>>(`/crm/meetings/${id}`, data);
    return res.data;
  }
};
