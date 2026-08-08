import { EmployeeData } from '../api/employees';
import { EnterpriseProject, ProjectMember } from '../api/projects';

export interface ResolvedEmployee {
  employeeId: string;
  fullName: string;
  firstName: string;
  lastName: string;
  designation: string;
  department: string;
  email: string;
  role: string;
}

/**
 * Resolves any employee reference (canonical employeeId, Mongo ObjectId, email, or full name)
 * against the employee directory. ALWAYS returns the canonical employee object with canonical employeeId.
 */
export function resolveEmployee(
  reference?: string | null | Record<string, any>,
  employeeDirectory: (EmployeeData | any)[] = []
): ResolvedEmployee | null {
  if (!reference) return null;

  let searchStr = '';
  if (typeof reference === 'string') {
    searchStr = reference.trim();
  } else if (typeof reference === 'object') {
    searchStr = String(
      reference.employeeId ||
      reference.id ||
      reference._id ||
      reference.employeeCode ||
      reference.officialEmail ||
      reference.email ||
      reference.employeeName ||
      reference.name ||
      ''
    ).trim();
  }

  if (!searchStr || searchStr.toLowerCase().includes('unassigned')) {
    return null;
  }

  // Extract clean ID code if formatted as "Name (EMP-xxx)" or "(EMP-xxx)"
  const codeMatch = searchStr.match(/\((EMP-[^)]+|INT-[^)]+|TK-[^)]+)\)/i) || searchStr.match(/^(EMP-[^\s]+|INT-[^\s]+|TK-[^\s]+)$/i);
  const searchCode = (codeMatch ? codeMatch[1] || codeMatch[0] : searchStr).toLowerCase();

  // Search directory by 1. canonical employeeId/employeeCode, 2. Mongo _id/userId, 3. officialEmail/email, 4. Full Name
  const found = employeeDirectory.find((e) => {
    if (!e) return false;
    const empId = String(e.employeeId || e.id || e._id || e.employeeCode || '').toLowerCase();
    const mongoId = String(e._id || e.id || e.userId || '').toLowerCase();
    const email = String(e.officialEmail || e.email || e.personalEmail || '').toLowerCase();
    const fullName = String(e.fullName || `${e.firstName || ''} ${e.lastName || ''}`).trim().toLowerCase();

    return (
      (empId && empId === searchCode) ||
      (mongoId && mongoId === searchCode) ||
      (email && email === searchCode) ||
      (fullName && fullName === searchCode)
    );
  });

  if (found) {
    const firstName = found.firstName || found.fullName?.split(' ')[0] || 'Employee';
    const lastName = found.lastName || found.fullName?.split(' ').slice(1).join(' ') || '';
    const fullName = found.fullName || `${firstName} ${lastName}`.trim();
    const canonicalId = found.employeeId || found.employeeCode || found.id || 'EMP-UNKNOWN';

    return {
      employeeId: canonicalId,
      fullName,
      firstName,
      lastName,
      designation: found.designation || 'Staff',
      department: found.department || 'Engineering',
      email: found.officialEmail || found.email || '',
      role: found.role || 'ROLE_EMPLOYEE',
    };
  }

  return null;
}

/**
 * Resolves Project Manager strictly by Authoritative Order:
 * 1. projectManagerId resolved against employeeDirectory
 * 2. members[] where role === 'PROJECT_MANAGER' || 'MANAGER'
 * 3. projectManagerName fallback string (only if clean name without stale code)
 * 4. otherwise null
 */
export function resolveProjectManager(
  project: EnterpriseProject,
  employeeDirectory: (EmployeeData | any)[] = []
): ResolvedEmployee | null {
  if (!project) return null;

  // 1. Primary: projectManagerId
  if (project.projectManagerId) {
    const mgr = resolveEmployee(project.projectManagerId, employeeDirectory);
    if (mgr) return mgr;
  }

  // 2. Secondary: members[] by role
  const members = Array.isArray(project.members) ? project.members : [];
  const pmMember = members.find(m => m && (m.role === 'PROJECT_MANAGER' || m.role === 'MANAGER'));
  if (pmMember) {
    const mgr = resolveEmployee(pmMember.employeeId || pmMember.employeeName, employeeDirectory);
    if (mgr) return mgr;
  }

  // 3. Tertiary: projectManagerName
  if (project.projectManagerName) {
    const mgr = resolveEmployee(project.projectManagerName, employeeDirectory);
    if (mgr) return mgr;
  }

  return null;
}

/**
 * Resolves Project Lead strictly by Authoritative Order:
 * 1. projectLeadId resolved against employeeDirectory
 * 2. members[] where role === 'TECH_LEAD' || 'LEAD'
 * 3. projectLeadName fallback string (only if clean name without stale code)
 * 4. otherwise null
 */
export function resolveProjectLead(
  project: EnterpriseProject,
  employeeDirectory: (EmployeeData | any)[] = []
): ResolvedEmployee | null {
  if (!project) return null;

  // 1. Primary: projectLeadId
  if (project.projectLeadId) {
    const lead = resolveEmployee(project.projectLeadId, employeeDirectory);
    if (lead) return lead;
  }

  // 2. Secondary: members[] by role
  const members = Array.isArray(project.members) ? project.members : [];
  const leadMember = members.find(m => m && (m.role === 'TECH_LEAD' || m.role === 'LEAD' || m.role === 'PROJECT_LEAD'));
  if (leadMember) {
    const lead = resolveEmployee(leadMember.employeeId || leadMember.employeeName, employeeDirectory);
    if (lead) return lead;
  }

  // 3. Tertiary: projectLeadName
  if (project.projectLeadName) {
    const lead = resolveEmployee(project.projectLeadName, employeeDirectory);
    if (lead) return lead;
  }

  return null;
}

/**
 * Resolves a manager or lead display name cleanly.
 * Returns clean Full Name or 'Unassigned' (NEVER 'Unassigned (EMP-006)').
 */
export function resolveEmployeeName(
  reference?: string | null,
  employeeDirectory: (EmployeeData | any)[] = [],
  fallbackDefault = 'Unassigned'
): string {
  const resolved = resolveEmployee(reference, employeeDirectory);
  if (resolved) {
    return resolved.fullName;
  }

  if (!reference) return fallbackDefault;
  const str = String(reference).trim();
  if (!str || str.toLowerCase().includes('unassigned')) return fallbackDefault;

  // Clean trailing parenthetical ID string e.g. "Rahul Garai (EMP-005)" -> "Rahul Garai"
  const clean = str.replace(/\s*\([^)]*\)/g, '').trim();
  if (clean.startsWith('EMP-') || clean.startsWith('INT-') || clean.startsWith('TK-') || clean.startsWith('EXEC-') || clean.length > 20) {
    return fallbackDefault;
  }

  return clean || fallbackDefault;
}

/**
 * Normalizes a project object's Manager, Lead, and Roster members using canonical employee IDs.
 */
export function normalizeProjectRoster(
  project: EnterpriseProject,
  employeeDirectory: (EmployeeData | any)[] = []
): EnterpriseProject {
  const manager = resolveProjectManager(project, employeeDirectory);
  const lead = resolveProjectLead(project, employeeDirectory);

  const normalizedManagerId = manager ? manager.employeeId : '';
  const normalizedManagerName = manager ? manager.fullName : 'Unassigned';

  const normalizedLeadId = lead ? lead.employeeId : '';
  const normalizedLeadName = lead ? lead.fullName : 'Unassigned';

  const rawMembers = Array.isArray(project.members) ? project.members : [];
  const normalizedMembers: ProjectMember[] = rawMembers.map((m: any) => {
    const resolved = resolveEmployee(m.employeeId || m.employeeName, employeeDirectory);
    return {
      ...m,
      employeeId: resolved ? resolved.employeeId : (m.employeeId && !m.employeeId.includes('6a7') ? m.employeeId : 'EMP-UNKNOWN'),
      employeeName: resolved ? resolved.fullName : (m.employeeName ? m.employeeName.replace(/\s*\([^)]*\)/g, '').trim() : 'Team Member'),
      role: m.role || resolved?.role || 'MEMBER',
      designation: resolved?.designation || m.designation,
      department: resolved?.department || m.department,
    };
  });

  return {
    ...project,
    projectManagerId: normalizedManagerId,
    projectManagerName: normalizedManagerName,
    projectLeadId: normalizedLeadId,
    projectLeadName: normalizedLeadName,
    members: normalizedMembers,
  };
}
