import { Response, NextFunction } from 'express';
import { AuthRequest } from './auth';

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
 * Enterprise Authorization Matrix enforcement when creating users/employees internally.
 */
export const authorizeUserCreation = (req: AuthRequest, res: Response, next: NextFunction): void => {
  if (!req.user) {
    res.status(401).json({ success: false, message: 'Authentication required.' });
    return;
  }

  const creatorRole = req.user.role || (req.user.roles && req.user.roles[0]);
  const targetRole = req.body.role || 'ROLE_EMPLOYEE';
  const targetDepartment = req.body.department;

  // Super Admin can create any role
  if (creatorRole === 'ROLE_SUPER_ADMIN' || creatorRole === 'ROLE_ADMIN') {
    next();
    return;
  }

  // CEO / MD can create Employees, Interns, Customers
  if (creatorRole === 'ROLE_CEO' || creatorRole === 'ROLE_MD') {
    if (['ROLE_EMPLOYEE', 'ROLE_INTERN', 'ROLE_CUSTOMER'].includes(targetRole)) {
      next();
      return;
    }
  }

  // CTO can create Technical Employees, Interns, Customers
  if (creatorRole === 'ROLE_CTO') {
    if (['ROLE_EMPLOYEE', 'ROLE_INTERN', 'ROLE_CUSTOMER'].includes(targetRole)) {
      if (targetRole === 'ROLE_EMPLOYEE' && targetDepartment && targetDepartment !== 'Technology' && targetDepartment !== 'Engineering') {
        res.status(403).json({ success: false, message: 'Forbidden: CTO can only create Technical Employees.' });
        return;
      }
      next();
      return;
    }
  }

  // CMO can create Marketing Employees, Interns, Customers
  if (creatorRole === 'ROLE_CMO' || creatorRole === 'ROLE_GROWTH_HEAD') {
    if (['ROLE_EMPLOYEE', 'ROLE_INTERN', 'ROLE_CUSTOMER'].includes(targetRole)) {
      if (targetRole === 'ROLE_EMPLOYEE' && targetDepartment && targetDepartment !== 'Marketing') {
        res.status(403).json({ success: false, message: 'Forbidden: CMO/Growth Head can only create Marketing Employees.' });
        return;
      }
      next();
      return;
    }
  }

  // HR can create Employees, Interns
  if (creatorRole === 'ROLE_HR') {
    if (['ROLE_EMPLOYEE', 'ROLE_INTERN'].includes(targetRole)) {
      next();
      return;
    }
  }

  res.status(403).json({
    success: false,
    message: `Forbidden: Role ${creatorRole} is not authorized to create account with role ${targetRole}.`,
  });
};
