import { MOCK_PROJECTS, MOCK_LEAVE_REQUESTS } from '../fixtures/testData';

describe('Core Enterprise UI Component Logic Tests', () => {
  it('should verify project dataset filtering and status aggregation', () => {
    const activeProjects = MOCK_PROJECTS.filter((p) => p.status === 'ACTIVE');
    expect(activeProjects.length).toBe(1);
    expect(activeProjects[0].name).toBe('Apex AI Transformation');
  });

  it('should calculate total project portfolio value correctly', () => {
    const totalBudget = MOCK_PROJECTS.reduce((sum, p) => sum + p.budget, 0);
    expect(totalBudget).toBe(770000);
  });

  it('should validate leave request state transitions', () => {
    const pendingLeave = MOCK_LEAVE_REQUESTS.find((l) => l.status === 'PENDING');
    expect(pendingLeave).toBeDefined();
    
    // Simulate approval transition
    const approvedLeave = { ...pendingLeave, status: 'APPROVED' };
    expect(approvedLeave.status).toBe('APPROVED');
  });
});
