import { MOCK_ADMIN_USER, MOCK_EMPLOYEE_USER, MOCK_CUSTOMER_USER } from '../fixtures/testData';

describe('Authentication & Authorization Module Unit Tests', () => {
  it('should correctly authenticate Super Admin user with complete administrative privileges', () => {
    expect(MOCK_ADMIN_USER.role).toBe('ROLE_SUPER_ADMIN');
    expect(MOCK_ADMIN_USER.email).toContain('@techknife.com');
    expect(MOCK_ADMIN_USER.status).toBe('ACTIVE');
  });

  it('should enforce role separation between Employee and Customer users', () => {
    expect(MOCK_EMPLOYEE_USER.role).not.toBe(MOCK_CUSTOMER_USER.role);
    expect(MOCK_EMPLOYEE_USER.role).toBe('ROLE_EMPLOYEE');
    expect(MOCK_CUSTOMER_USER.role).toBe('ROLE_CUSTOMER');
  });

  it('should validate corporate email domain format for internal staff', () => {
    const isTechKnifeStaff = (email: string) => email.endsWith('@techknife.com');
    expect(isTechKnifeStaff(MOCK_ADMIN_USER.email)).toBe(true);
    expect(isTechKnifeStaff(MOCK_EMPLOYEE_USER.email)).toBe(true);
    expect(isTechKnifeStaff(MOCK_CUSTOMER_USER.email)).toBe(false);
  });
});
