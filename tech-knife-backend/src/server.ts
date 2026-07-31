import express, { Request, Response } from 'express';
import cors from 'cors';
import morgan from 'morgan';
import dotenv from 'dotenv';
import { connectDB } from './config/db';
import { seedDatabase } from './services/seedService';
import { authenticateJwt } from './middleware/auth';
import { getOrganizationChartV1 } from './controllers/employeeController';
import authRoutes from './routes/authRoutes';
import employeeRoutes from './routes/employeeRoutes';
import attendanceRoutes from './routes/attendanceRoutes';
import customerRoutes from './routes/customerRoutes';
import organizationRoutes from './routes/organizationRoutes';
import enterpriseRoutes from './routes/enterpriseRoutes';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 8080;

// Middleware
app.use(cors({ origin: '*', credentials: true }));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(morgan('dev'));

// Health Check
app.get('/api/health', (req: Request, res: Response) => {
  res.json({
    status: 'UP',
    database: 'MongoDB Atlas',
    system: 'Tech Knife Enterprise Backend',
    timestamp: new Date().toISOString(),
  });
});

// API Routes
app.use('/api/v1/auth', authRoutes);
app.use('/api/auth', authRoutes);
app.use('/auth', authRoutes);
app.use('/api/employees', employeeRoutes);
app.use('/api/attendance', attendanceRoutes);
app.use('/api/customers', customerRoutes);
app.use('/api/organization', organizationRoutes);
app.use('/api/enterprise', enterpriseRoutes);

// Compatibility & Dedicated API v1/v2 Aliases
app.get('/api/v1/organization-chart', authenticateJwt, getOrganizationChartV1);
app.use('/api/v2/employees', employeeRoutes);
app.use('/api/v2/interns', employeeRoutes);
app.use('/api/v2/attendance', attendanceRoutes);

// Startup Server
const startServer = async () => {
  await connectDB();
  await seedDatabase();

  app.listen(PORT, () => {
    console.log(`[Tech Knife Enterprise Server] Running on http://localhost:${PORT}`);
  });
};

startServer();
