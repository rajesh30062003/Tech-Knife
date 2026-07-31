import { Router } from 'express';
import { getRoles, getDepartments, getPermissions } from '../controllers/organizationController';
import { authenticateJwt } from '../middleware/auth';

const router = Router();

router.use(authenticateJwt);

router.get('/roles', getRoles);
router.get('/departments', getDepartments);
router.get('/permissions', getPermissions);

export default router;
