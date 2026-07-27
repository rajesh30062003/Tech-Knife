import { employeeV2Api } from '../api/employeeV2Api';
import { CreateEmployeeRequest, UpdateEmployeeRequest, UpdateEmployeeStatusRequest } from '../types/employeeV2';

// Lightweight test framework helpers for TypeScript compilation compatibility
const assert = (condition: boolean, message: string) => {
  if (!condition) {
    throw new Error(`Assertion Failed: ${message}`);
  }
};

const expect = <T,>(actual: T) => ({
  toBe: (expected: T) => assert(actual === expected, `Expected ${expected}, got ${actual}`),
  toBeDefined: () => assert(actual !== undefined && actual !== null, 'Expected value to be defined'),
  toBeGreaterThan: (expected: number) =>
    assert((actual as unknown as number) > expected, `Expected ${actual} > ${expected}`),
  toContain: (expected: string) =>
    assert(String(actual).includes(expected), `Expected "${actual}" to contain "${expected}"`),
});

export const runEmployeeApiTestSuite = async () => {
  const sampleRequest: CreateEmployeeRequest = {
    employeeId: 'EMP-9999',
    officialEmail: 'test.user@techknife.com',
    personalEmail: 'test.user@gmail.com',
    primaryMobile: '+1 (555) 999-0000',
    firstName: 'Test',
    lastName: 'User',
    gender: 'OTHER',
    dob: '1995-05-15',
    bloodGroup: 'O_POSITIVE',
    departmentId: 'Engineering & DevOps',
    designationId: 'Software Engineer',
    managerId: 'emp-doc-1002',
    joiningDate: '2026-01-01',
    employmentType: 'FULL_TIME',
    salary: 120000,
    skills: ['React', 'TypeScript', 'Spring Boot'],
    status: 'ACTIVE',
  };

  // 1. Onboard Employee Test
  const created = await employeeV2Api.createEmployee(sampleRequest);
  expect(created).toBeDefined();
  expect(created.employeeId).toBe('EMP-9999');
  expect(created.officialEmail).toBe('test.user@techknife.com');
  expect(created.fullName).toBe('Test User');
  expect(created.status).toBe('ACTIVE');

  // Verify localStorage audit log entry created
  const auditEntries = JSON.parse(localStorage.getItem('techknife_audit_entries') || '[]');
  expect(auditEntries.length).toBeGreaterThan(0);
  expect(auditEntries[0].entityId).toBe('EMP-9999');
  expect(auditEntries[0].action).toBe('CREATE');

  // 2. Fetch Employees Test
  const res = await employeeV2Api.getAllEmployees({ search: 'Alexander' });
  expect(res).toBeDefined();
  expect(res.content.length).toBeGreaterThan(0);
  expect(res.content[0].fullName).toContain('Alexander');

  // 3. Update Profile Test
  const updateReq: UpdateEmployeeRequest = {
    firstName: 'Test',
    lastName: 'User-Updated',
    salary: 135000,
    designationId: 'Senior Software Engineer',
  };

  const updated = await employeeV2Api.updateEmployee('EMP-9999', updateReq);
  expect(updated).toBeDefined();
  expect(updated.salary).toBe(135000);
  expect(updated.fullName).toBe('Test User-Updated');

  // 4. Status Transition Test
  const statusReq: UpdateEmployeeStatusRequest = {
    status: 'SUSPENDED',
    statusReason: 'Security audit review',
  };

  const statusUpdated = await employeeV2Api.updateEmployeeStatus('EMP-9999', statusReq);
  expect(statusUpdated.status).toBe('SUSPENDED');

  // 5. Delete Employee Test
  const deleted = await employeeV2Api.deleteEmployee('EMP-9999');
  expect(deleted).toBe(true);

  // Verify removed from search
  const resAfterDelete = await employeeV2Api.getAllEmployees({ search: 'EMP-9999' });
  expect(resAfterDelete.content.length).toBe(0);

  return { success: true, testsPassed: 5 };
};
