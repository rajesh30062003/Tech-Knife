import { Role } from '../../src/types';

export interface TestUser {
  id: string;
  email: string;
  name: string;
  role: Role;
  department: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export const MOCK_ADMIN_USER: TestUser = {
  id: 'usr-admin-01',
  email: 'admin@techknife.com',
  name: 'Alex Thorne',
  role: 'ROLE_SUPER_ADMIN',
  department: 'Executive Board',
  status: 'ACTIVE',
};

export const MOCK_EMPLOYEE_USER: TestUser = {
  id: 'usr-emp-01',
  email: 'sarah.jenkins@techknife.com',
  name: 'Sarah Jenkins',
  role: 'ROLE_EMPLOYEE',
  department: 'Engineering',
  status: 'ACTIVE',
};

export const MOCK_CUSTOMER_USER: TestUser = {
  id: 'usr-cust-01',
  email: 'client.contact@apexcorp.com',
  name: 'David Vance',
  role: 'ROLE_CUSTOMER',
  department: 'External Enterprise',
  status: 'ACTIVE',
};

export const MOCK_PROJECTS = [
  {
    id: 'prj-101',
    name: 'Apex AI Transformation',
    client: 'Apex Global Corp',
    budget: 450000,
    status: 'ACTIVE',
    completionRate: 78,
  },
  {
    id: 'prj-102',
    name: 'Cloud Core Microservices Migration',
    client: 'FinTech Nexus',
    budget: 320000,
    status: 'IN_PROGRESS',
    completionRate: 60,
  },
];

export const MOCK_LEAVE_REQUESTS = [
  {
    id: 'lve-001',
    employeeId: 'usr-emp-01',
    employeeName: 'Sarah Jenkins',
    leaveType: 'ANNUAL',
    startDate: '2026-08-01',
    endDate: '2026-08-05',
    reason: 'Family Vacation',
    status: 'PENDING',
  },
];
