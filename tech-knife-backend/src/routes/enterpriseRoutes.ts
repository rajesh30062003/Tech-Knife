import { Router } from 'express';
import {
  getProjects,
  getTasks,
  getAnnouncements,
  getNotifications,
  getLeaves,
  getSalarySlips,
} from '../controllers/enterpriseController';
import { authenticateJwt } from '../middleware/auth';

const router = Router();

router.use(authenticateJwt);

router.get('/projects', getProjects);
router.get('/tasks', getTasks);
router.get('/announcements', getAnnouncements);
router.get('/notifications', getNotifications);
router.get('/leaves', getLeaves);
router.get('/salary-slips', getSalarySlips);

export default router;
