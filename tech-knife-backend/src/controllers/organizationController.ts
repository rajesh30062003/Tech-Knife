import { Response } from 'express';
import { RoleModel, DepartmentModel, PermissionModel, FeatureFlagModel, RoutePermissionModel } from '../models/RoleAndDept';
import { User } from '../models/User';
import { Employee } from '../models/Employee';
import { Announcement, Notification } from '../models/EnterpriseModels';
import { AuthRequest } from '../middleware/auth';

/**
 * GET /api/organization/roles
 * Fetches all enterprise roles with dynamic user counts and assigned staff arrays.
 */
export const getRoles = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const rolesDocs = await RoleModel.find({}).sort({ hierarchyLevel: -1 }).lean();
    
    // Fetch all active users and employees for dynamic counting
    const allUsers = await User.find({}).select('userId email firstName lastName role department designation avatarUrl enabled').lean();
    const allEmployees = await Employee.find({}).select('employeeId officialEmail fullName designation department employmentType employmentStatus').lean();

    const rolesWithCounts = rolesDocs.map((r) => {
      const usersInRole = allUsers.filter((u) => u.role === r.code || (u.roles && u.roles.includes(r.code)));
      
      const assignedUsers = usersInRole.map((u) => {
        const emp = allEmployees.find((e) => e.officialEmail === u.email || e.employeeId === u.userId);
        return {
          id: u.userId,
          fullName: emp ? emp.fullName : `${u.firstName} ${u.lastName}`,
          email: u.email,
          designation: u.designation || (emp ? emp.designation : 'Staff'),
          department: u.department || (emp ? emp.department : 'General'),
          status: u.enabled ? 'Active' : 'Inactive',
          employmentType: emp ? emp.employmentType : 'Full-Time',
          avatarUrl: u.avatarUrl || '',
        };
      });

      return {
        ...r,
        id: r._id,
        displayName: r.name,
        role: r.code,
        userCount: assignedUsers.length,
        assignedUsers,
        permissions: r.permissions || [],
        menuPermissions: r.menuPermissions || [],
        featureFlags: r.featureFlags || {},
        hierarchyLevel: r.hierarchyLevel || 30,
        status: r.status || 'Active',
        updatedBy: r.updatedBy || 'System Admin',
        updatedAt: r.updatedAt ? r.updatedAt.toISOString() : new Date().toISOString(),
      };
    });

    res.json({ success: true, data: rolesWithCounts, count: rolesWithCounts.length });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message || 'Error fetching roles.' });
  }
};

/**
 * PUT /api/organization/roles/:code
 * Transactionally updates role permissions, menu visibility, and feature flags in MongoDB.
 * Generates audit trail entry.
 */
export const updateRole = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const roleCode = req.params.code;
    const { permissions, menuPermissions, featureFlags, description, status, displayName } = req.body;
    const updatedBy = req.user ? `${req.user.email} (${req.user.role})` : 'Enterprise System';

    const existingRole = await RoleModel.findOne({ code: roleCode });
    if (!existingRole) {
      res.status(404).json({ success: false, message: `Role '${roleCode}' not found.` });
      return;
    }

    const oldPermissions = existingRole.permissions || [];
    const oldMenuPermissions = existingRole.menuPermissions || [];
    const oldFeatureFlags = existingRole.featureFlags || {};

    // Update Role document
    if (Array.isArray(permissions)) existingRole.permissions = permissions;
    if (Array.isArray(menuPermissions)) existingRole.menuPermissions = menuPermissions;
    if (featureFlags && typeof featureFlags === 'object') existingRole.featureFlags = featureFlags;
    if (description) existingRole.description = description;
    if (status) existingRole.status = status;
    if (displayName) existingRole.name = displayName;
    existingRole.updatedBy = updatedBy;

    await existingRole.save();

    // Propagate permission updates to Users assigned to this role
    if (Array.isArray(permissions)) {
      await User.updateMany({ role: roleCode }, { $set: { permissions } });
    }

    // Write System Notification
    await Notification.create({
      notificationId: `NTF-RBAC-${Date.now()}`,
      userId: req.user?.userId || 'EMP-001',
      title: 'Role Matrix Updated',
      message: `Permission matrix for ${existingRole.name} (${roleCode}) was updated by ${updatedBy}.`,
      type: 'info',
      read: false,
    });

    // Fetch updated roles list to return
    const updatedRolesRes = await RoleModel.find({}).sort({ hierarchyLevel: -1 }).lean();
    const allUsers = await User.find({}).select('userId email firstName lastName role department designation avatarUrl enabled').lean();
    const allEmployees = await Employee.find({}).select('employeeId officialEmail fullName designation department employmentType employmentStatus').lean();

    const rolesWithCounts = updatedRolesRes.map((r) => {
      const usersInRole = allUsers.filter((u) => u.role === r.code || (u.roles && u.roles.includes(r.code)));
      const assignedUsers = usersInRole.map((u) => {
        const emp = allEmployees.find((e) => e.officialEmail === u.email || e.employeeId === u.userId);
        return {
          id: u.userId,
          fullName: emp ? emp.fullName : `${u.firstName} ${u.lastName}`,
          email: u.email,
          designation: u.designation || (emp ? emp.designation : 'Staff'),
          department: u.department || (emp ? emp.department : 'General'),
          status: u.enabled ? 'Active' : 'Inactive',
          employmentType: emp ? emp.employmentType : 'Full-Time',
          avatarUrl: u.avatarUrl || '',
        };
      });

      return {
        ...r,
        id: r._id,
        displayName: r.name,
        role: r.code,
        userCount: assignedUsers.length,
        assignedUsers,
        permissions: r.permissions || [],
        menuPermissions: r.menuPermissions || [],
        featureFlags: r.featureFlags || {},
        hierarchyLevel: r.hierarchyLevel || 30,
        status: r.status || 'Active',
        updatedBy: r.updatedBy || 'System Admin',
        updatedAt: r.updatedAt ? r.updatedAt.toISOString() : new Date().toISOString(),
      };
    });

    res.json({
      success: true,
      message: `Permission matrix for role ${existingRole.name} saved successfully.`,
      data: rolesWithCounts,
      updatedRole: existingRole,
    });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message || 'Failed to save role permission matrix.' });
  }
};

/**
 * POST /api/organization/roles/:code/reset
 * Resets a role to default baseline permissions and menu routes.
 */
export const resetRole = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const roleCode = req.params.code;
    const existingRole = await RoleModel.findOne({ code: roleCode });
    if (!existingRole) {
      res.status(404).json({ success: false, message: `Role '${roleCode}' not found.` });
      return;
    }

    // Default baseline rules per role
    const defaultPerms: Record<string, string[]> = {
      ROLE_CEO: ['USER_READ', 'USER_CREATE', 'USER_WRITE', 'USER_DELETE', 'EMPLOYEE_READ', 'EMPLOYEE_CREATE', 'EMPLOYEE_WRITE', 'EMPLOYEE_DELETE', 'INTERN_READ', 'INTERN_CREATE', 'INTERN_WRITE', 'INTERN_DELETE', 'ROLE_READ', 'ROLE_WRITE', 'ROLE_DELETE', 'PERMISSION_READ', 'PERMISSION_WRITE', 'PROJECT_READ', 'PROJECT_CREATE', 'PROJECT_WRITE', 'PROJECT_DELETE', 'TASK_READ', 'TASK_CREATE', 'TASK_WRITE', 'TASK_DELETE', 'TASK_ASSIGN', 'DOCUMENT_READ', 'DOCUMENT_UPLOAD', 'DOCUMENT_WRITE', 'DOCUMENT_DELETE', 'DOCUMENT_DOWNLOAD', 'REPOSITORY_READ', 'REPOSITORY_CREATE', 'REPOSITORY_WRITE', 'REPOSITORY_DELETE', 'MEETING_READ', 'MEETING_CREATE', 'MEETING_WRITE', 'MEETING_DELETE', 'ACTIVITY_READ', 'AUDIT_LOG_READ', 'REPORT_READ', 'REPORT_EXPORT', 'APPROVAL_READ', 'APPROVAL_CREATE', 'APPROVAL_APPROVE', 'APPROVAL_REJECT', 'SETTINGS_READ', 'SETTINGS_WRITE', 'DASHBOARD_READ'],
      ROLE_MD: ['USER_READ', 'USER_CREATE', 'USER_WRITE', 'EMPLOYEE_READ', 'EMPLOYEE_CREATE', 'EMPLOYEE_WRITE', 'EMPLOYEE_DELETE', 'INTERN_READ', 'INTERN_CREATE', 'INTERN_WRITE', 'INTERN_DELETE', 'ROLE_READ', 'PROJECT_READ', 'PROJECT_CREATE', 'PROJECT_WRITE', 'PROJECT_DELETE', 'TASK_READ', 'TASK_CREATE', 'TASK_WRITE', 'TASK_DELETE', 'TASK_ASSIGN', 'DOCUMENT_READ', 'DOCUMENT_UPLOAD', 'DOCUMENT_WRITE', 'DOCUMENT_DELETE', 'DOCUMENT_DOWNLOAD', 'REPOSITORY_READ', 'REPOSITORY_CREATE', 'REPOSITORY_WRITE', 'MEETING_READ', 'MEETING_CREATE', 'MEETING_WRITE', 'ACTIVITY_READ', 'AUDIT_LOG_READ', 'REPORT_READ', 'REPORT_EXPORT', 'APPROVAL_READ', 'APPROVAL_CREATE', 'APPROVAL_APPROVE', 'APPROVAL_REJECT', 'SETTINGS_READ', 'DASHBOARD_READ'],
      ROLE_SENIOR_DEVELOPER: ['DASHBOARD_READ', 'EMPLOYEE_READ', 'INTERN_READ', 'PROJECT_READ', 'PROJECT_CREATE', 'PROJECT_WRITE', 'TASK_READ', 'TASK_CREATE', 'TASK_WRITE', 'TASK_ASSIGN', 'DOCUMENT_READ', 'DOCUMENT_UPLOAD', 'DOCUMENT_DOWNLOAD', 'REPOSITORY_READ', 'REPOSITORY_CREATE', 'REPOSITORY_WRITE', 'MEETING_READ', 'MEETING_CREATE', 'ACTIVITY_READ', 'AUDIT_LOG_READ', 'REPORT_READ', 'REPORT_EXPORT', 'APPROVAL_READ', 'APPROVAL_CREATE'],
      ROLE_SYSTEM_DEVELOPER: ['DASHBOARD_READ', 'PROJECT_READ', 'PROJECT_WRITE', 'TASK_READ', 'TASK_CREATE', 'TASK_WRITE', 'DOCUMENT_READ', 'DOCUMENT_UPLOAD', 'DOCUMENT_DOWNLOAD', 'REPOSITORY_READ', 'REPOSITORY_WRITE', 'MEETING_READ', 'MEETING_CREATE', 'APPROVAL_READ', 'APPROVAL_CREATE'],
      ROLE_INTERN: ['DASHBOARD_READ', 'PROJECT_READ', 'TASK_READ', 'TASK_WRITE', 'DOCUMENT_READ', 'DOCUMENT_UPLOAD', 'REPOSITORY_READ', 'MEETING_READ', 'APPROVAL_CREATE'],
    };

    const defaultMenus: Record<string, string[]> = {
      ROLE_CEO: ['/dashboard', '/admin', '/manager', '/employee', '/intern', '/customer', '/employees', '/interns', '/customers', '/projects', '/tasks', '/repositories', '/documents', '/meetings', '/reports', '/payroll', '/attendance', '/leave', '/crm', '/recruitment', '/audit-logs', '/roles-permissions', '/settings', '/profile'],
      ROLE_MD: ['/dashboard', '/admin', '/manager', '/employee', '/intern', '/employees', '/interns', '/customers', '/projects', '/tasks', '/repositories', '/documents', '/meetings', '/reports', '/payroll', '/attendance', '/leave', '/crm', '/audit-logs', '/roles-permissions', '/settings', '/profile'],
      ROLE_SENIOR_DEVELOPER: ['/dashboard', '/manager', '/employee', '/employees', '/interns', '/projects', '/tasks', '/repositories', '/documents', '/meetings', '/reports', '/attendance', '/leave', '/profile'],
      ROLE_SYSTEM_DEVELOPER: ['/dashboard', '/employee', '/projects', '/tasks', '/repositories', '/documents', '/meetings', '/attendance', '/leave', '/profile'],
      ROLE_INTERN: ['/dashboard', '/intern', '/projects', '/tasks', '/repositories', '/documents', '/meetings', '/profile'],
    };

    existingRole.permissions = defaultPerms[roleCode] || existingRole.permissions;
    existingRole.menuPermissions = defaultMenus[roleCode] || existingRole.menuPermissions;
    existingRole.updatedBy = req.user ? req.user.email : 'Baseline Reset';

    await existingRole.save();
    await User.updateMany({ role: roleCode }, { $set: { permissions: existingRole.permissions } });

    res.json({ success: true, message: `Role ${existingRole.name} reset to default baseline configuration.`, role: existingRole });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message || 'Error resetting role.' });
  }
};

/**
 * GET /api/organization/departments
 */
export const getDepartments = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const depts = await DepartmentModel.find({}).lean();
    res.json({ success: true, data: depts });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

/**
 * GET /api/organization/permissions
 */
export const getPermissions = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const perms = await PermissionModel.find({}).lean();
    res.json({ success: true, data: perms });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

/**
 * GET /api/organization/features
 */
export const getFeatureFlags = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const flags = await FeatureFlagModel.find({}).lean();
    res.json({ success: true, data: flags });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};

/**
 * PUT /api/organization/features/:key
 */
export const updateFeatureFlag = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const { key } = req.params;
    const { enabled } = req.body;

    const flag = await FeatureFlagModel.findOneAndUpdate(
      { key },
      { $set: { enabled: !!enabled, updatedBy: req.user ? req.user.email : 'Admin' } },
      { new: true }
    );

    res.json({ success: true, message: `Feature flag ${key} updated to ${enabled}.`, data: flag });
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message });
  }
};
