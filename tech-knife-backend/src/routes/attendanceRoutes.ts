import { Router } from 'express';
import { getAttendance, clockIn, clockOut } from '../controllers/attendanceController';
import { authenticateJwt } from '../middleware/auth';

const router = Router();

router.use(authenticateJwt);

router.get('/', getAttendance);
router.post('/clock-in', clockIn);
router.post('/clock-out', clockOut);

export default router;
