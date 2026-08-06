import { apiClient } from './client';
import {
  ApiResponse,
  AuthRequest,
  AuthResponse,
  RegisterRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  SendOtpRequest,
  VerifyOtpRequest,
  ChangePasswordRequest,
  ProfilePictureRequest,
  UserProfile,
} from '../types';

export const authApi = {
  login: async (data: AuthRequest): Promise<ApiResponse<AuthResponse>> => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/login', data);
    return res.data;
  },

  register: async (data: RegisterRequest): Promise<ApiResponse<AuthResponse>> => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/register', data);
    return res.data;
  },

  refreshToken: async (refreshToken: string): Promise<ApiResponse<AuthResponse>> => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/refresh-token', { refreshToken });
    return res.data;
  },

  logout: async (refreshToken?: string): Promise<ApiResponse<void>> => {
    const res = await apiClient.post<ApiResponse<void>>('/auth/logout', { refreshToken });
    return res.data;
  },

  forgotPassword: async (data: ForgotPasswordRequest): Promise<ApiResponse<void>> => {
    const res = await apiClient.post<ApiResponse<void>>('/auth/forgot-password', data);
    return res.data;
  },

  resetPassword: async (data: ResetPasswordRequest): Promise<ApiResponse<void>> => {
    const res = await apiClient.post<ApiResponse<void>>('/auth/reset-password', data);
    return res.data;
  },

  sendOtp: async (data: SendOtpRequest): Promise<ApiResponse<void>> => {
    const res = await apiClient.post<ApiResponse<void>>('/auth/send-otp', data);
    return res.data;
  },

  verifyOtp: async (data: VerifyOtpRequest): Promise<ApiResponse<boolean>> => {
    const res = await apiClient.post<ApiResponse<boolean>>('/auth/verify-otp', data);
    return res.data;
  },

  changePassword: async (data: ChangePasswordRequest): Promise<ApiResponse<void>> => {
    const res = await apiClient.post<ApiResponse<void>>('/auth/change-password', data);
    return res.data;
  },

  getCurrentUser: async (): Promise<ApiResponse<UserProfile>> => {
    const res = await apiClient.get<ApiResponse<UserProfile>>('/auth/me');
    return res.data;
  },

  updateProfilePicture: async (data: ProfilePictureRequest): Promise<ApiResponse<UserProfile>> => {
    const res = await apiClient.patch<ApiResponse<UserProfile>>('/auth/profile-picture', data);
    return res.data;
  },
};
