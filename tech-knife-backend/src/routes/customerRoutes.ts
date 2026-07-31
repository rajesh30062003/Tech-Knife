import { Router } from 'express';
import { getCustomers, getCustomerById } from '../controllers/customerController';
import { authenticateJwt } from '../middleware/auth';

const router = Router();

router.use(authenticateJwt);

router.get('/', getCustomers);
router.get('/:id', getCustomerById);

export default router;
