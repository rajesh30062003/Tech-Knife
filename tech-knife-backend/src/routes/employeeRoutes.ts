import { Router } from 'express';
import {
  getAllEmployees,
  getEmployeeById,
  createEmployee,
  updateEmployee,
  deleteEmployee,
  getInterns,
  getOrgChart,
  getOrganizationChartV1,
} from '../controllers/employeeController';
import { authenticateJwt } from '../middleware/auth';
import { authorizeUserCreation, requirePermission } from '../middleware/rbac';

const router = Router();

router.use(authenticateJwt);

router.get('/', requirePermission('EMPLOYEE_READ'), getAllEmployees);
router.get('/interns', requirePermission('INTERN_READ'), getInterns);
router.get('/org-chart', getOrgChart);
router.get('/organization-chart', getOrganizationChartV1);
router.get('/:id', getEmployeeById);
router.post('/', requirePermission('EMPLOYEE_CREATE'), authorizeUserCreation, createEmployee);
router.put('/:id', requirePermission('EMPLOYEE_WRITE'), updateEmployee);
router.delete('/:id', requirePermission('EMPLOYEE_DELETE'), deleteEmployee);

export default router;
