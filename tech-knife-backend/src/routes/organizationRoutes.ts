import { Router } from 'express';
import {
  getRoles,
  updateRole,
  resetRole,
  getDepartments,
  getPermissions,
  getFeatureFlags,
  updateFeatureFlag,
} from '../controllers/organizationController';
import { authenticateJwt } from '../middleware/auth';
import { requirePermission, preventPrivilegeEscalation } from '../middleware/rbac';

const router = Router();

router.use(authenticateJwt);

router.get('/roles', requirePermission('ROLE_READ'), getRoles);
router.put('/roles/:code', requirePermission('ROLE_WRITE'), preventPrivilegeEscalation, updateRole);
router.post('/roles/:code/reset', requirePermission('ROLE_WRITE'), preventPrivilegeEscalation, resetRole);
router.get('/departments', getDepartments);
router.get('/permissions', getPermissions);
router.get('/features', getFeatureFlags);
router.put('/features/:key', requirePermission('ROLE_WRITE'), updateFeatureFlag);

export default router;
