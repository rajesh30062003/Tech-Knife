import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import {
  AuthRequest,
  AuthResponse,
  ChangePasswordRequest,
  ForgotPasswordRequest,
  Permission,
  ProfilePictureRequest,
  RegisterRequest,
  ResetPasswordRequest,
  Role,
  SendOtpRequest,
  UserProfile,
  VerifyOtpRequest,
} from '../types';
import { authApi } from '../api/auth';

interface AuthContextType {
  user: UserProfile | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (data: AuthRequest) => Promise<AuthResponse>;
  register: (data: RegisterRequest) => Promise<AuthResponse>;
  logout: () => Promise<void>;
  forgotPassword: (data: ForgotPasswordRequest) => Promise<void>;
  resetPassword: (data: ResetPasswordRequest) => Promise<void>;
  sendOtp: (data: SendOtpRequest) => Promise<void>;
  verifyOtp: (data: VerifyOtpRequest) => Promise<boolean>;
  changePassword: (data: ChangePasswordRequest) => Promise<void>;
  updateProfilePicture: (data: ProfilePictureRequest) => Promise<UserProfile>;
  updateUserProfile: (data: Partial<UserProfile>) => Promise<UserProfile>;
  switchRole: (role: Role) => void;
  hasRole: (allowedRoles: Role[]) => boolean;
  hasPermission: (permission: string) => boolean;
  canAccessRoute: (routePath: string) => boolean;
  isFeatureEnabled: (featureKey: string) => boolean;
  refetchPermissions: () => Promise<void>;
  clearError: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchCurrentUser = useCallback(async () => {
    const token = localStorage.getItem('techknife_access_token');
    if (!token) {
      setUser(null);
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true);
      const response = await authApi.getCurrentUser();
      if (response.success && response.data) {
        const u = response.data;
        const primaryRole = u.roles && u.roles.length > 0 ? u.roles[0] : (u.role || 'ROLE_EMPLOYEE');
        const profile: UserProfile = {
        id: u.id,
          email: u.email,
          firstName: u.firstName,
          lastName: u.lastName,
          designation: u.designation || 'Team Member',
          department: u.department || 'General',
          phoneNumber: u.phoneNumber || '',
          avatarUrl: u.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(u.firstName + ' ' + u.lastName)}`,
          enabled: u.enabled ?? true,
          accountNonLocked: u.accountNonLocked ?? true,
          emailVerified: u.emailVerified ?? true,
          roles: u.roles || [primaryRole],
          role: primaryRole,
          permissions: u.permissions || [],
          lastLoginAt: u.lastLoginAt,
          createdAt: u.createdAt,
          updatedAt: u.updatedAt,
        };
        setUser(profile);
      } else {
        setUser(null);
      }
    } catch (err: unknown) {
      console.error('Failed to fetch user session from MongoDB Atlas:', err);
      setUser(null);
      localStorage.removeItem('techknife_access_token');
      localStorage.removeItem('techknife_refresh_token');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchCurrentUser();
  }, [fetchCurrentUser]);

  const login = async (data: AuthRequest): Promise<AuthResponse> => {
    setError(null);
    setIsLoading(true);
    try {
      const response = await authApi.login(data);
      if (response.success && response.data) {
        const authData = response.data;
        localStorage.setItem('techknife_access_token', authData.accessToken);
        localStorage.setItem('techknife_refresh_token', authData.refreshToken);
        await fetchCurrentUser();
        return authData;
      } else {
        throw new Error(response.message || 'Authentication failed');
      }
    } catch (err: any) {
      const serverDetails = err.response?.data?.error?.details;
      const serverMsg = err.response?.data?.message;
      let msg = 'Failed to authenticate with MongoDB Atlas';
      if (serverDetails && typeof serverDetails === 'string') {
        msg = serverDetails;
      } else if (serverMsg && typeof serverMsg === 'string' && serverMsg !== 'Application Error' && serverMsg !== 'Internal Server Error') {
        msg = serverMsg;
      } else if (err.response?.status === 401) {
        msg = 'Invalid email or password';
      } else if (err.response?.status === 403) {
        msg = 'Account disabled or locked';
      } else if (err.response?.status === 500) {
        msg = 'Unexpected server error (500). Please try again later.';
      } else if (err.message) {
        msg = err.message;
      }
      setError(msg);
      setIsLoading(false);
      throw new Error(msg);
    }
  };

  const register = async (data: RegisterRequest): Promise<AuthResponse> => {
    setError(null);
    setIsLoading(true);
    try {
      const response = await authApi.register({ ...data, role: 'ROLE_CUSTOMER' });
      if (response.success && response.data) {
        const authData = response.data;
        localStorage.setItem('techknife_access_token', authData.accessToken);
        localStorage.setItem('techknife_refresh_token', authData.refreshToken);
        await fetchCurrentUser();
        return authData;
      } else {
        throw new Error(response.message || 'Registration failed');
      }
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Failed to register account';
      setError(msg);
      setIsLoading(false);
      throw new Error(msg);
    }
  };

  const logout = async () => {
    const refreshToken = localStorage.getItem('techknife_refresh_token');
    try {
      if (refreshToken) {
        await authApi.logout(refreshToken);
      }
    } catch (e) {
      // ignore
    } finally {
      localStorage.removeItem('techknife_access_token');
      localStorage.removeItem('techknife_refresh_token');
      setUser(null);
    }
  };

  const forgotPassword = async (data: ForgotPasswordRequest): Promise<void> => {
    setError(null);
    try {
      await authApi.forgotPassword(data);
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Failed to send reset code';
      setError(msg);
      throw new Error(msg);
    }
  };

  const resetPassword = async (data: ResetPasswordRequest): Promise<void> => {
    setError(null);
    try {
      await authApi.resetPassword(data);
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Failed to reset password';
      setError(msg);
      throw new Error(msg);
    }
  };

  const sendOtp = async (data: SendOtpRequest): Promise<void> => {
    setError(null);
    try {
      await authApi.sendOtp(data);
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Failed to send OTP';
      setError(msg);
      throw new Error(msg);
    }
  };

  const verifyOtp = async (data: VerifyOtpRequest): Promise<boolean> => {
    setError(null);
    try {
      const response = await authApi.verifyOtp(data);
      if (user && data.type === 'EMAIL_VERIFICATION') {
        setUser({ ...user, emailVerified: true });
      }
      return response.data;
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'OTP verification failed';
      setError(msg);
      throw new Error(msg);
    }
  };

  const changePassword = async (data: ChangePasswordRequest): Promise<void> => {
    setError(null);
    try {
      await authApi.changePassword(data);
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Failed to update password';
      setError(msg);
      throw new Error(msg);
    }
  };

  const updateProfilePicture = async (data: ProfilePictureRequest): Promise<UserProfile> => {
    setError(null);
    try {
      const response = await authApi.updateProfilePicture(data);
      const u = response.data;
      const primaryRole = u.roles && u.roles.length > 0 ? u.roles[0] : (u.role || 'ROLE_EMPLOYEE');
      const updatedProfile: UserProfile = {
        id: u.id,
        email: u.email,
        firstName: u.firstName,
        lastName: u.lastName,
        designation: u.designation,
        department: u.department,
        phoneNumber: u.phoneNumber,
        avatarUrl: u.avatarUrl,
        enabled: u.enabled,
        accountNonLocked: u.accountNonLocked,
        emailVerified: u.emailVerified,
        roles: u.roles || [primaryRole],
        role: primaryRole,
        permissions: u.permissions,
      };
      setUser(updatedProfile);
      return updatedProfile;
    } catch (err: any) {
      if (user) {
        const updated = { ...user, avatarUrl: data.avatarUrl };
        setUser(updated);
        return updated;
      }
      const msg = err.response?.data?.message || err.message || 'Failed to update avatar';
      setError(msg);
      throw new Error(msg);
    }
  };

  const updateUserProfile = async (updates: Partial<UserProfile>): Promise<UserProfile> => {
    setError(null);
    if (!user) throw new Error('No active authenticated user session');

    const { salary, email, department, designation, joinDate, managerId, managerName, managerDesignation, ...allowedUpdates } = updates;

    const updatedProfile: UserProfile = {
      ...user,
      ...allowedUpdates,
      updatedAt: new Date().toISOString(),
    };

    setUser(updatedProfile);
    return updatedProfile;
  };

  const [userRoleConfig, setUserRoleConfig] = useState<DynamicRole | null>(null);

  const refetchPermissions = useCallback(async () => {
    if (!user) return;
    try {
      const roles = await permissionsApi.getRoles();
      const myRole = roles.find((r) => r.role === user.role);
      if (myRole) {
        setUserRoleConfig(myRole);
      }
    } catch {
      // fallback
    }
  }, [user]);

  useEffect(() => {
    if (user) {
      refetchPermissions();
    } else {
      setUserRoleConfig(null);
    }
  }, [user, refetchPermissions]);

  const switchRole = (_role: Role) => {
    // Role switching is strictly governed by MongoDB Atlas user roles.
    fetchCurrentUser();
  };

  const hasRole = (allowedRoles: Role[]) => {
    if (!user) return false;
    if (user.roles && user.roles.length > 0) {
      return user.roles.some((r) => allowedRoles.includes(r));
    }
    return allowedRoles.includes(user.role);
  };

  const hasPermission = (permission: string): boolean => {
    if (!user) return false;
    if (user.role === 'ROLE_CEO' || user.role === 'ROLE_SUPER_ADMIN') return true;
    if (userRoleConfig && userRoleConfig.permissions) {
      return userRoleConfig.permissions.includes(permission);
    }
    if (user.permissions && user.permissions.length > 0) {
      return user.permissions.includes(permission as any);
    }
    return false;
  };

  const canAccessRoute = (routePath: string): boolean => {
    if (!user) return false;
    if (user.role === 'ROLE_CEO' || user.role === 'ROLE_SUPER_ADMIN') return true;
    if (userRoleConfig && Array.isArray(userRoleConfig.menuPermissions)) {
      return userRoleConfig.menuPermissions.includes(routePath);
    }
    return true;
  };

  const isFeatureEnabled = (featureKey: string): boolean => {
    if (!user) return false;
    if (user.role === 'ROLE_CEO' || user.role === 'ROLE_SUPER_ADMIN') return true;
    if (userRoleConfig && userRoleConfig.featureFlags && userRoleConfig.featureFlags[featureKey] !== undefined) {
      return !!userRoleConfig.featureFlags[featureKey];
    }
    return true;
  };

  const clearError = () => setError(null);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        error,
        login,
        register,
        logout,
        forgotPassword,
        resetPassword,
        sendOtp,
        verifyOtp,
        changePassword,
        updateProfilePicture,
        updateUserProfile,
        switchRole,
        hasRole,
        hasPermission,
        canAccessRoute,
        isFeatureEnabled,
        refetchPermissions,
        clearError,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
