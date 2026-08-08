import { Response, NextFunction } from 'express';
import { AuthRequest } from './auth';
import { RoleModel, FeatureFlagModel } from '../models/RoleAndDept';
import { User } from '../models/User';

/**
 * Public registration restriction: Only ROLE_CUSTOMER can be registered publicly.
 */
export const validatePublicRegistration = (req: AuthRequest, res: Response, next: NextFunction): void => {
  const requestedRole = req.body.role || (req.body.roles && req.body.roles[0]) || 'ROLE_CUSTOMER';

  if (requestedRole !== 'ROLE_CUSTOMER') {
    res.status(403).json({
      success: false,
      message: `Forbidden: Public registration is restricted to Customers only. Accounts for ${requestedRole} can only be created by Authorized Enterprise Leadership/Admin.`,
    });
    return;
  }
  next();
};

/**
 * Dynamic Permission Guard Middleware: Checks MongoDB Role permissions array for the authenticated user.
 */
export const requirePermission = (permissionCode: string) => {
  return async (req: AuthRequest, res: Response, next: NextFunction): Promise<void> => {
    try {
      if (!req.user) {
        res.status(401).json({ success: false, message: 'Authentication required. No token provided.' });
        return;
      }

      const userRoleCode = req.user.role || (req.user.roles && req.user.roles[0]) || 'ROLE_CUSTOMER';

      // Super Admin and CEO always bypass permission checks
      if (userRoleCode === 'ROLE_SUPER_ADMIN' || userRoleCode === 'ROLE_CEO') {
        next();
        return;
      }

      const roleDoc = await RoleModel.findOne({ code: userRoleCode });
      if (!roleDoc) {
        res.status(403).json({ success: false, message: `Access denied. Role ${userRoleCode} not registered in system.` });
        return;
      }

      if (!roleDoc.permissions || !roleDoc.permissions.includes(permissionCode)) {
        res.status(403).json({
          success: false,
          message: `Access Denied: Your role (${userRoleCode}) lacks the required '${permissionCode}' permission.`,
        });
        return;
      }

      next();
    } catch (error: any) {
      res.status(500).json({ success: false, message: error.message || 'Internal authorization error.' });
    }
  };
};

/**
 * Dynamic Route Guard Middleware: Checks if user's role has menu route access.
 */
export const requireRouteAccess = (routePath: string) => {
  return async (req: AuthRequest, res: Response, next: NextFunction): Promise<void> => {
    try {
      if (!req.user) {
        res.status(401).json({ success: false, message: 'Authentication required.' });
        return;
      }

      const userRoleCode = req.user.role || (req.user.roles && req.user.roles[0]);

      if (userRoleCode === 'ROLE_SUPER_ADMIN' || userRoleCode === 'ROLE_CEO') {
        next();
        return;
      }

      const roleDoc = await RoleModel.findOne({ code: userRoleCode });
      if (!roleDoc || !roleDoc.menuPermissions || !roleDoc.menuPermissions.includes(routePath)) {
        res.status(403).json({
          success: false,
          message: `Access Denied: Route '${routePath}' is disabled for role ${userRoleCode}.`,
        });
        return;
      }

      next();
    } catch (error: any) {
      res.status(500).json({ success: false, message: error.message || 'Route authorization error.' });
    }
  };
};

/**
 * Feature Flag Guard Middleware: Checks if a global feature flag is enabled.
 */
export const requireFeatureFlag = (featureKey: string) => {
  return async (req: AuthRequest, res: Response, next: NextFunction): Promise<void> => {
    try {
      const flagDoc = await FeatureFlagModel.findOne({ key: featureKey });
      if (flagDoc && flagDoc.enabled === false) {
        res.status(403).json({
          success: false,
          message: `Module Disabled: Feature flag '${flagDoc.title || featureKey}' is currently deactivated by system policy.`,
        });
        return;
      }
      next();
    } catch (error: any) {
      res.status(500).json({ success: false, message: error.message || 'Feature flag error.' });
    }
  };
};

/**
 * Server-Side Privilege Escalation Prevention Guard:
 * Prevents non-CEO users from modifying ROLE_CEO permissions or granting permissions beyond their own hierarchy.
 */
export const preventPrivilegeEscalation = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({ success: false, message: 'Authentication required.' });
      return;
    }

    const callerRoleCode = req.user.role || (req.user.roles && req.user.roles[0]);
    const targetRoleCode = req.params.code || req.body.code || req.body.role;

    // CEO and Super Admin can edit any role
    if (callerRoleCode === 'ROLE_CEO' || callerRoleCode === 'ROLE_SUPER_ADMIN') {
      next();
      return;
    }

    // Non-CEO cannot modify ROLE_CEO or ROLE_SUPER_ADMIN
    if (targetRoleCode === 'ROLE_CEO' || targetRoleCode === 'ROLE_SUPER_ADMIN') {
      res.status(403).json({
        success: false,
        message: 'Security Violation: You are not authorized to modify Executive Leadership (ROLE_CEO / ROLE_SUPER_ADMIN) configuration.',
      });
      return;
    }

    const callerRoleDoc = await RoleModel.findOne({ code: callerRoleCode });
    const targetRoleDoc = await RoleModel.findOne({ code: targetRoleCode });

    const callerHierarchy = callerRoleDoc ? callerRoleDoc.hierarchyLevel : 30;
    const targetHierarchy = targetRoleDoc ? targetRoleDoc.hierarchyLevel : 30;

    // Caller cannot modify a role with equal or higher hierarchy rank
    if (callerHierarchy <= targetHierarchy) {
      res.status(403).json({
        success: false,
        message: `Security Violation: Role ${callerRoleCode} (rank ${callerHierarchy}) cannot modify role ${targetRoleCode} (rank ${targetHierarchy}).`,
      });
      return;
    }

    next();
  } catch (error: any) {
    res.status(500).json({ success: false, message: error.message || 'Privilege escalation check error.' });
  }
};

/**
 * Enterprise Authorization Matrix enforcement when creating users/employees internally.
 */
export const authorizeUserCreation = (req: AuthRequest, res: Response, next: NextFunction): void => {
  if (!req.user) {
    res.status(401).json({ success: false, message: 'Authentication required.' });
    return;
  }

  const creatorRole = req.user.role || (req.user.roles && req.user.roles[0]);
  const targetRole = req.body.role || 'ROLE_EMPLOYEE';

  // Super Admin & CEO can create any role
  if (['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO'].includes(creatorRole)) {
    next();
    return;
  }

  // MD can create Senior Dev, System Dev, Intern, Customer
  if (creatorRole === 'ROLE_MD') {
    if (['ROLE_SENIOR_DEVELOPER', 'ROLE_SYSTEM_DEVELOPER', 'ROLE_EMPLOYEE', 'ROLE_INTERN', 'ROLE_CUSTOMER'].includes(targetRole)) {
      next();
      return;
    }
  }

  // Senior Dev can create System Dev, Intern
  if (creatorRole === 'ROLE_SENIOR_DEVELOPER') {
    if (['ROLE_SYSTEM_DEVELOPER', 'ROLE_EMPLOYEE', 'ROLE_INTERN'].includes(targetRole)) {
      next();
      return;
    }
  }

  res.status(403).json({
    success: false,
    message: `Forbidden: Role ${creatorRole} is not authorized to create account with role ${targetRole}.`,
  });
};
