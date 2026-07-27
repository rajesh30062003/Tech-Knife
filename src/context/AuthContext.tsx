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
  hasPermission: (permission: Permission) => boolean;
  clearError: () => void;
}

const MOCK_PROFILES: Record<Role, UserProfile> = {
  ROLE_SUPER_ADMIN: {
    id: 'usr-001',
    email: 'admin@techknife.com',
    firstName: 'Alexander',
    lastName: 'Vance',
    role: 'ROLE_SUPER_ADMIN',
    roles: ['ROLE_SUPER_ADMIN'],
    department: 'Executive Governance',
    designation: 'Chief Information Security Officer',
    avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 019-2834',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
  },
  ROLE_ADMIN: {
    id: 'usr-002',
    email: 's.connor@techknife.com',
    firstName: 'Sarah',
    lastName: 'Connor',
    role: 'ROLE_ADMIN',
    roles: ['ROLE_ADMIN'],
    department: 'System Operations',
    designation: 'Global System Administrator',
    avatarUrl: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 012-9988',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
  },
  ROLE_CEO: {
    id: 'usr-003',
    email: 'ceo@techknife.com',
    firstName: 'Victoria',
    lastName: 'Sterling',
    role: 'ROLE_CEO',
    roles: ['ROLE_CEO'],
    department: 'Executive Suite',
    designation: 'Chief Executive Officer',
    avatarUrl: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 010-1000',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
  },
  ROLE_CTO: {
    id: 'usr-004',
    email: 'cto@techknife.com',
    firstName: 'Marcus',
    lastName: 'Vance',
    role: 'ROLE_CTO',
    roles: ['ROLE_CTO'],
    department: 'Technology & Architecture',
    designation: 'Chief Technology Officer',
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 010-2000',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
  },
  ROLE_CMO: {
    id: 'usr-005',
    email: 'cmo@techknife.com',
    firstName: 'Eleanor',
    lastName: 'Rigby',
    role: 'ROLE_CMO',
    roles: ['ROLE_CMO'],
    department: 'Global Growth & Marketing',
    designation: 'Chief Marketing Officer',
    avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 010-3000',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
  },
  ROLE_MD: {
    id: 'usr-006',
    email: 'md@techknife.com',
    firstName: 'Arthur',
    lastName: 'Pendelton',
    role: 'ROLE_MD',
    roles: ['ROLE_MD'],
    department: 'Managing Directorate',
    designation: 'Managing Director',
    avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 010-4000',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
  },
  ROLE_DIRECTOR: {
    id: 'usr-007',
    email: 'director@techknife.com',
    firstName: 'Rachel',
    lastName: 'Green',
    role: 'ROLE_DIRECTOR',
    roles: ['ROLE_DIRECTOR'],
    department: 'Enterprise Solutions',
    designation: 'Director of Engineering',
    avatarUrl: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 010-5000',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
  },
  ROLE_MANAGER: {
    id: 'usr-008',
    email: 'm.brody@techknife.com',
    firstName: 'Marcus',
    lastName: 'Brody',
    role: 'ROLE_MANAGER',
    roles: ['ROLE_MANAGER'],
    department: 'Engineering',
    designation: 'Engineering Manager',
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 014-4321',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
  },
  ROLE_EMPLOYEE: {
    id: 'usr-009',
    email: 'e.rostova@techknife.com',
    firstName: 'Elena',
    lastName: 'Rostova',
    role: 'ROLE_EMPLOYEE',
    roles: ['ROLE_EMPLOYEE'],
    department: 'Frontend Engineering',
    designation: 'Senior Full Stack Engineer',
    avatarUrl: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 018-7712',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
    emergencyContact: 'Nikolai Rostova (+1 555-900-3344)',
    address: '742 Evergreen Terrace, San Jose, CA 95112',
    joinDate: '2022-04-12',
    salary: 135000,
    managerId: 'usr-008',
    managerName: 'Marcus Brody',
    managerDesignation: 'Engineering Manager',
    bio: 'Senior full-stack software engineer specializing in high-performance React application architecture, micro-frontends, and cloud services.',
    skills: ['TypeScript', 'React 18', 'Tailwind CSS', 'Node.js', 'REST APIs', 'GraphQL', 'Docker'],
  },
  ROLE_INTERN: {
    id: 'usr-010',
    email: 'l.chen@techknife.com',
    firstName: 'Lucas',
    lastName: 'Chen',
    role: 'ROLE_INTERN',
    roles: ['ROLE_INTERN'],
    department: 'Cloud Solutions',
    designation: 'DevOps Intern',
    avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 016-3390',
    enabled: true,
    accountNonLocked: true,
    emailVerified: false,
  },
  ROLE_CUSTOMER: {
    id: 'usr-011',
    email: 'david@apexenterprises.io',
    firstName: 'David',
    lastName: 'Miller',
    role: 'ROLE_CUSTOMER',
    roles: ['ROLE_CUSTOMER'],
    department: 'Apex Enterprises',
    designation: 'Client Technology Sponsor',
    avatarUrl: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&q=80&w=250',
    phoneNumber: '+1 (555) 011-5544',
    enabled: true,
    accountNonLocked: true,
    emailVerified: true,
  },
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserProfile | null>(() => {
    const savedDemoUser = localStorage.getItem('techknife_demo_user');
    if (savedDemoUser) {
      try {
        return JSON.parse(savedDemoUser);
      } catch (e) {
        // ignore
      }
    }
    return MOCK_PROFILES.ROLE_SUPER_ADMIN;
  });

  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchCurrentUser = useCallback(async () => {
    const token = localStorage.getItem('techknife_access_token');
    if (!token) {
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true);
      const response = await authApi.getCurrentUser();
      if (response.success && response.data) {
        const u = response.data;
        const primaryRole = u.roles && u.roles.length > 0 ? u.roles[0] : 'ROLE_EMPLOYEE';
        const profile: UserProfile = {
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
          lastLoginAt: u.lastLoginAt,
          createdAt: u.createdAt,
          updatedAt: u.updatedAt,
        };
        setUser(profile);
      }
    } catch (err: unknown) {
      console.warn('Backend connection failed, maintaining demo session:', err);
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
        localStorage.removeItem('techknife_demo_user');

        await fetchCurrentUser();
        return authData;
      } else {
        throw new Error(response.message || 'Authentication failed');
      }
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Failed to authenticate';
      setError(msg);
      setIsLoading(false);
      throw new Error(msg);
    }
  };

  const register = async (data: RegisterRequest): Promise<AuthResponse> => {
    setError(null);
    setIsLoading(true);
    try {
      const response = await authApi.register(data);
      if (response.success && response.data) {
        const authData = response.data;
        localStorage.setItem('techknife_access_token', authData.accessToken);
        localStorage.setItem('techknife_refresh_token', authData.refreshToken);
        localStorage.removeItem('techknife_demo_user');

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
      // ignore logout network errors
    } finally {
      localStorage.removeItem('techknife_access_token');
      localStorage.removeItem('techknife_refresh_token');
      localStorage.removeItem('techknife_demo_user');
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
      const primaryRole = u.roles && u.roles.length > 0 ? u.roles[0] : 'ROLE_EMPLOYEE';
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

    // Filter out restricted fields so employees cannot modify salary, email, department, designation, joinDate, manager
    const { salary, email, department, designation, joinDate, managerId, managerName, managerDesignation, ...allowedUpdates } = updates;

    const updatedProfile: UserProfile = {
      ...user,
      ...allowedUpdates,
      updatedAt: new Date().toISOString(),
    };

    setUser(updatedProfile);
    localStorage.setItem('techknife_demo_user', JSON.stringify(updatedProfile));
    return updatedProfile;
  };

  const switchRole = (role: Role) => {
    const profile = MOCK_PROFILES[role] || MOCK_PROFILES.ROLE_SUPER_ADMIN;
    setUser(profile);
    localStorage.setItem('techknife_demo_user', JSON.stringify(profile));
  };

  const hasRole = (allowedRoles: Role[]) => {
    if (!user) return false;
    if (user.roles && user.roles.length > 0) {
      return user.roles.some((r) => allowedRoles.includes(r));
    }
    return allowedRoles.includes(user.role);
  };

  const hasPermission = (permission: Permission) => {
    if (!user) return false;
    if (user.permissions && user.permissions.length > 0) {
      return user.permissions.includes(permission);
    }
    // Executive roles have full access
    const executiveRoles: Role[] = ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO', 'ROLE_CTO', 'ROLE_CMO', 'ROLE_MD', 'ROLE_DIRECTOR'];
    return user.roles.some((r) => executiveRoles.includes(r));
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
