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
import { authorizeUserCreation } from '../middleware/rbac';

const router = Router();

router.use(authenticateJwt);

router.get('/', getAllEmployees);
router.get('/interns', getInterns);
router.get('/org-chart', getOrgChart);
router.get('/organization-chart', getOrganizationChartV1);
router.get('/:id', getEmployeeById);
router.post('/', authorizeUserCreation, createEmployee);
router.put('/:id', updateEmployee);
router.delete('/:id', deleteEmployee);

export default router;
