import { UserProfile } from '../types';
import { EnterpriseProject } from '../api/projects';

export interface ProjectStatusConfig {
  value: string;
  label: string;
  progress: number;
}

export interface ProjectPendingStatusRequest {
  requestedStatus: string;
  reason?: string;
  requestedBy: string;
  requestedByRole?: string;
  requestedAt: string;
}

export const PROJECT_STATUS_LIST: ProjectStatusConfig[] = [
  { value: 'PLANNED', label: 'PLANNED', progress: 0 },
  { value: 'REQ_GATHERING', label: 'REQ GATHERING', progress: 10 },
  { value: 'DESIGN', label: 'DESIGN', progress: 20 },
  { value: 'BACKEND_DEV', label: 'BACKEND DEV', progress: 35 },
  { value: 'FRONTEND_DEV', label: 'FRONTEND DEV', progress: 45 },
  { value: 'FULLSTACK_DEV', label: 'FULLSTACK DEV', progress: 55 },
  { value: 'API_INTEGRATION', label: 'API INTEGRATION', progress: 65 },
  { value: 'TESTING', label: 'TESTING', progress: 75 },
  { value: 'QA', label: 'QA', progress: 82 },
  { value: 'UAT', label: 'UAT', progress: 90 },
  { value: 'DEPLOYMENT', label: 'DEPLOYMENT', progress: 95 },
  { value: 'LIVE', label: 'LIVE', progress: 100 },
  { value: 'MAINTENANCE', label: 'MAINTENANCE', progress: 100 },
  { value: 'COMPLETED', label: 'COMPLETED', progress: 100 },
  { value: 'ON_HOLD', label: 'ON HOLD', progress: -1 }, // Preserve last valid progress
  { value: 'CANCELLED', label: 'CANCELLED', progress: 0 },
];

export const normalizeProjectStatus = (raw?: string | null): string => {
  if (!raw || !raw.trim()) return 'PLANNED';
  const clean = raw.trim().replace(/[-\s]+/g, '_').toUpperCase();

  if (clean === 'IN_PROGRESS' || clean === 'DEVELOPMENT') return 'FULLSTACK_DEV';
  if (clean === 'REVIEW' || clean === 'CODE_REVIEW') return 'QA';
  if (clean === 'REQUIREMENT_GATHERING') return 'REQ_GATHERING';
  if (clean === 'BACKEND_DEVELOPMENT') return 'BACKEND_DEV';
  if (clean === 'FRONTEND_DEVELOPMENT') return 'FRONTEND_DEV';
  if (clean === 'FULLSTACK_DEVELOPMENT') return 'FULLSTACK_DEV';

  const match = PROJECT_STATUS_LIST.find(s => s.value === clean);
  if (match) return match.value;

  return clean;
};

export const getStatusProgress = (status?: string | null, lastValidProgress = 0): number => {
  if (!status) return 0;
  const clean = normalizeProjectStatus(status);

  if (clean === 'ON_HOLD') {
    return lastValidProgress > 0 ? Math.round(lastValidProgress) : 0;
  }

  const match = PROJECT_STATUS_LIST.find(s => s.value === clean);
  if (match && match.progress >= 0) {
    return match.progress;
  }

  return 0;
};

export const getStatusLabel = (status?: string | null): string => {
  const clean = normalizeProjectStatus(status);
  const match = PROJECT_STATUS_LIST.find(s => s.value === clean);
  if (match) return match.label;
  return clean.replace(/_/g, ' ');
};

export const canApproveProjectStatus = (user: UserProfile | null, project?: EnterpriseProject | null): boolean => {
  if (!user) return false;

  const role = (user.role || '').toUpperCase();
  const roles = (user.roles || []).map(r => String(r).toUpperCase());
  const designation = (user.designation || '').toUpperCase();
  const userId = (user.id || '').toLowerCase();
  const empId = ((user as any).employeeId || user.id || '').toLowerCase();

  // 1. Check direct leadership roles (CEO, MD, CTO, Super Admin)
  const isCLevel = 
    role === 'ROLE_SUPER_ADMIN' || role === 'SUPER_ADMIN' ||
    role === 'ROLE_CEO' || role === 'CEO' || designation.includes('CEO') ||
    role === 'ROLE_MD' || role === 'MD' || designation.includes('MD') || designation.includes('MANAGING DIRECTOR') ||
    role === 'ROLE_CTO' || role === 'CTO' || designation.includes('CTO') || designation.includes('CHIEF TECHNOLOGY OFFICER') ||
    roles.some(r => r.includes('ADMIN') || r.includes('CEO') || r.includes('MD') || r.includes('CTO'));

  if (isCLevel) return true;

  // 2. Check general Manager / Team Lead roles
  const isManagerOrLeadRole = 
    role === 'ROLE_PROJECT_MANAGER' || role === 'PROJECT_MANAGER' || role === 'ROLE_MANAGER' || designation.includes('PROJECT MANAGER') ||
    role === 'ROLE_TEAM_LEAD' || role === 'TEAM_LEAD' || role === 'ROLE_PROJECT_LEAD' || designation.includes('TEAM LEAD') || designation.includes('TECHNICAL LEAD');

  if (isManagerOrLeadRole) return true;

  // 3. Check specific Project Manager or Technical Lead assignment on the current project
  if (project) {
    const pmId = (project.projectManagerId || '').toLowerCase();
    const leadId = (project.projectLeadId || '').toLowerCase();

    if ((userId && (userId === pmId || userId === leadId)) || (empId && (empId === pmId || empId === leadId))) {
      return true;
    }
  }

  return false;
};
