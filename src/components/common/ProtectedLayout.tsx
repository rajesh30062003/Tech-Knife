import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Role, Permission } from '../../types';
import { checkUserHasRole, checkUserPermission } from '../../utils/rbac';

interface ProtectedLayoutProps {
  allowedRoles?: Role[];
  requiredPermissions?: Permission[];
}

export const ProtectedLayout: React.FC<ProtectedLayoutProps> = ({
  allowedRoles,
  requiredPermissions,
}) => {
  const { user, isAuthenticated, canAccessRoute } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Dynamic Route Access Check from DB Role matrix
  if (location.pathname && !canAccessRoute(location.pathname)) {
    return <Navigate to="/403" replace />;
  }

  // Check role eligibility
  if (allowedRoles && allowedRoles.length > 0) {
    const hasAllowedRole = checkUserHasRole(user, allowedRoles);
    if (!hasAllowedRole) {
      return <Navigate to="/403" replace />;
    }
  }

  // Check permission eligibility
  if (requiredPermissions && requiredPermissions.length > 0) {
    const hasAllPermissions = requiredPermissions.every((perm) =>
      checkUserPermission(user, perm)
    );
    if (!hasAllPermissions) {
      return <Navigate to="/403" replace />;
    }
  }

  return <Outlet />;
};
