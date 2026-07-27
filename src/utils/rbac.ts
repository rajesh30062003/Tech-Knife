import { Role, Permission, UserProfile } from '../types';

/**
 * Numerical hierarchy rank for enterprise roles.
 * Higher rank indicates greater administrative scope and authority.
 */
export const ROLE_HIERARCHY: Record<Role, number> = {
  ROLE_SUPER_ADMIN: 100,
  ROLE_MD: 90,
  ROLE_CEO: 85,
  ROLE_CTO: 80,
  ROLE_CMO: 80,
  ROLE_DIRECTOR: 70,
  ROLE_MANAGER: 60,
  ROLE_EMPLOYEE: 30,
  ROLE_INTERN: 20,
  ROLE_CUSTOMER: 10,
  ROLE_ADMIN: 95,
};

/**
 * Administrative Executive & Manager Role Sets
 */
export const EXECUTIVE_ROLES: Role[] = [
  'ROLE_SUPER_ADMIN',
  'ROLE_ADMIN',
  'ROLE_MD',
  'ROLE_CEO',
  'ROLE_CTO',
  'ROLE_CMO',
  'ROLE_DIRECTOR',
];

export const MANAGEMENT_ROLES: Role[] = [
  ...EXECUTIVE_ROLES,
  'ROLE_MANAGER',
];

export const STAFF_ROLES: Role[] = [
  'ROLE_EMPLOYEE',
  'ROLE_INTERN',
];

export const EXTERNAL_ROLES: Role[] = [
  'ROLE_CUSTOMER',
];

/**
 * Role Permission Matrix defining explicit capability mappings
 */
export const ROLE_PERMISSIONS: Record<Role, Permission[]> = {
  ROLE_SUPER_ADMIN: [
    'USER_READ', 'USER_WRITE', 'USER_DELETE',
    'PROJECT_READ', 'PROJECT_WRITE', 'PROJECT_DELETE',
    'PAYROLL_READ', 'PAYROLL_WRITE',
    'CRM_READ', 'CRM_WRITE',
    'RECRUITMENT_READ', 'RECRUITMENT_WRITE',
    'SYSTEM_ADMIN',
  ],
  ROLE_ADMIN: [
    'USER_READ', 'USER_WRITE', 'USER_DELETE',
    'PROJECT_READ', 'PROJECT_WRITE', 'PROJECT_DELETE',
    'PAYROLL_READ', 'PAYROLL_WRITE',
    'CRM_READ', 'CRM_WRITE',
    'RECRUITMENT_READ', 'RECRUITMENT_WRITE',
    'SYSTEM_ADMIN',
  ],
  ROLE_MD: [
    'USER_READ', 'USER_WRITE', 'USER_DELETE',
    'PROJECT_READ', 'PROJECT_WRITE', 'PROJECT_DELETE',
    'PAYROLL_READ', 'PAYROLL_WRITE',
    'CRM_READ', 'CRM_WRITE',
    'RECRUITMENT_READ', 'RECRUITMENT_WRITE',
    'SYSTEM_ADMIN',
  ],
  ROLE_CEO: [
    'USER_READ', 'USER_WRITE', 'USER_DELETE',
    'PROJECT_READ', 'PROJECT_WRITE', 'PROJECT_DELETE',
    'PAYROLL_READ', 'PAYROLL_WRITE',
    'CRM_READ', 'CRM_WRITE',
    'RECRUITMENT_READ', 'RECRUITMENT_WRITE',
    'SYSTEM_ADMIN',
  ],
  ROLE_CTO: [
    'USER_READ', 'USER_WRITE', 'USER_DELETE',
    'PROJECT_READ', 'PROJECT_WRITE', 'PROJECT_DELETE',
    'PAYROLL_READ', 'PAYROLL_WRITE',
    'CRM_READ', 'CRM_WRITE',
    'RECRUITMENT_READ', 'RECRUITMENT_WRITE',
    'SYSTEM_ADMIN',
  ],
  ROLE_CMO: [
    'USER_READ', 'USER_WRITE',
    'PROJECT_READ', 'PROJECT_WRITE',
    'CRM_READ', 'CRM_WRITE',
    'RECRUITMENT_READ', 'RECRUITMENT_WRITE',
  ],
  ROLE_DIRECTOR: [
    'USER_READ', 'USER_WRITE', 'USER_DELETE',
    'PROJECT_READ', 'PROJECT_WRITE', 'PROJECT_DELETE',
    'PAYROLL_READ', 'PAYROLL_WRITE',
    'CRM_READ', 'CRM_WRITE',
    'RECRUITMENT_READ', 'RECRUITMENT_WRITE',
  ],
  ROLE_MANAGER: [
    'USER_READ', 'USER_WRITE',
    'PROJECT_READ', 'PROJECT_WRITE',
    'PAYROLL_READ', 'PAYROLL_WRITE',
    'CRM_READ', 'CRM_WRITE',
    'RECRUITMENT_READ', 'RECRUITMENT_WRITE',
  ],
  ROLE_EMPLOYEE: [
    'PROJECT_READ',
    'PAYROLL_READ',
  ],
  ROLE_INTERN: [
    'PROJECT_READ',
  ],
  ROLE_CUSTOMER: [
    'PROJECT_READ',
  ],
};

/**
 * Evaluates whether a user has a required permission
 */
export const checkUserPermission = (user: UserProfile | null, permission: Permission): boolean => {
  if (!user) return false;
  if (user.permissions && user.permissions.includes(permission)) {
    return true;
  }
  const userRoles = user.roles && user.roles.length > 0 ? user.roles : [user.role];
  return userRoles.some((r) => ROLE_PERMISSIONS[r]?.includes(permission));
};

/**
 * Checks if user holds any of the given roles
 */
export const checkUserHasRole = (user: UserProfile | null, allowedRoles: Role[]): boolean => {
  if (!user) return false;
  const userRoles = user.roles && user.roles.length > 0 ? user.roles : [user.role];
  return userRoles.some((r) => allowedRoles.includes(r));
};

/**
 * Specific Granular Action Security Checks according to system requirements
 */

// Admins, Owner, MD, CEO, CTO, CMO, Director, Manager permissions
export const canCreateEmployee = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canCreateIntern = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canDeleteEmployee = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canSuspendEmployee = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canAssignProjects = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canApprovePayroll = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canManageAttendance = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canManageLeave = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canManageDepartments = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canManageRecruitment = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, MANAGEMENT_ROLES);

export const canChangeSalary = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, EXECUTIVE_ROLES);

// Employee constraints
export const canUpdateProjectStatus = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, [...MANAGEMENT_ROLES, 'ROLE_EMPLOYEE', 'ROLE_INTERN']);

export const canViewSalary = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, [...MANAGEMENT_ROLES, 'ROLE_EMPLOYEE']);

export const canViewTasks = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, [...MANAGEMENT_ROLES, 'ROLE_EMPLOYEE', 'ROLE_INTERN', 'ROLE_CUSTOMER']);

// Customer capabilities
export const canTrackProject = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, [...MANAGEMENT_ROLES, 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER']);

export const canRaiseTicket = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, [...MANAGEMENT_ROLES, 'ROLE_EMPLOYEE', 'ROLE_INTERN', 'ROLE_CUSTOMER']);

export const canViewInvoice = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, [...MANAGEMENT_ROLES, 'ROLE_CUSTOMER']);

export const canDownloadDocuments = (user: UserProfile | null): boolean =>
  checkUserHasRole(user, [...MANAGEMENT_ROLES, 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER']);
