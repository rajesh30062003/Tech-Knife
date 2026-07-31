import { Router } from 'express';
import { login, register, getCurrentUser, refreshToken, logout } from '../controllers/authController';
import { authenticateJwt } from '../middleware/auth';
import { validatePublicRegistration } from '../middleware/rbac';

const router = Router();

router.post('/login', login);
router.post('/register', validatePublicRegistration, register);
router.get('/me', authenticateJwt, getCurrentUser);
router.get('/current-user', authenticateJwt, getCurrentUser);
router.post('/refresh', refreshToken);
router.post('/refresh-token', refreshToken);
router.post('/logout', logout);

export default router;
