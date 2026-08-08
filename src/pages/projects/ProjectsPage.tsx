import React, { useState, useEffect } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { 
  FolderKanban, Plus, GitBranch, Calendar, Search, Filter, 
  ExternalLink, Github, Globe, Shield, Users, Layers, DollarSign, 
  Clock, Activity, Edit3, Trash2, Link2, CheckCircle2, ChevronRight, 
  FileText, ArrowUpRight, Cpu, Sparkles, UserPlus, AlertCircle, X, ShieldAlert,
  Server, Database, Cloud, Terminal, Code2, Lock, Tag, MessageSquare
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { projectsApi, EnterpriseProject, ProjectActivity, ProjectLinksData } from '../../api/projects';
import { employeesApi, EmployeeData } from '../../api/employees';
import { StatusBadge } from '../../components/common/StatusBadge';
import { EmployeeSelect } from '../../components/common/EmployeeSelect';
import { InternSelect } from '../../components/common/InternSelect';
import { EnterpriseProjectWorkspace } from '../../components/projects/EnterpriseProjectWorkspace';
import { 
  PROJECT_STATUS_LIST, 
  getStatusProgress, 
  normalizeProjectStatus, 
  canApproveProjectStatus, 
  getStatusLabel 
} from '../../constants/projectStatus';
import { 
  resolveEmployeeName, 
  resolveProjectManager, 
  resolveProjectLead, 
  normalizeProjectRoster 
} from '../../utils/employeeResolver';

export const ProjectsPage: React.FC = () => {
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const [projects, setProjects] = useState<EnterpriseProject[]>([]);
  const [employeesList, setEmployeesList] = useState<EmployeeData[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    employeesApi.getEmployees().then((res) => {
      if (res?.employees && Array.isArray(res.employees)) {
        setEmployeesList(res.employees);
      }
    }).catch(() => {});
  }, []);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');

  // Modal / Drawer States
  const [selectedProject, setSelectedProject] = useState<EnterpriseProject | null>(null);
  const [workspaceProject, setWorkspaceProject] = useState<EnterpriseProject | null>(null);
  const [isWorkspaceOpen, setIsWorkspaceOpen] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);
  const [isLinksModalOpen, setIsLinksModalOpen] = useState(false);
  const [isActivityModalOpen, setIsActivityModalOpen] = useState(false);
  
  // Status Request Modal State
  const [requestStatusProject, setRequestStatusProject] = useState<EnterpriseProject | null>(null);
  const [requestedStatus, setRequestedStatus] = useState<string>('FRONTEND_DEV');
  const [requestReason, setRequestReason] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const openProjectWorkspace = (project: EnterpriseProject) => {
    setWorkspaceProject(project);
    setIsWorkspaceOpen(true);
  };

  // Form Section State in Create Modal
  const [activeTab, setActiveTab] = useState<'basic' | 'tech' | 'schedule' | 'team' | 'urls'>('basic');

  // Form Data State
  const [formData, setFormData] = useState<Partial<EnterpriseProject>>({
    projectCode: '',
    projectName: '',
    shortName: '',
    description: '',
    objectives: '',
    client: '',
    clientOrganization: '',
    department: 'Engineering',
    category: 'Technical',
    businessUnit: 'Enterprise Solutions',
    projectType: 'FIXED_BID',
    priority: 'MEDIUM',
    status: 'PLANNED',
    estimatedCost: 75000,
    budget: 75000,
    estimatedDuration: 90,
    startDate: new Date().toISOString().split('T')[0],
    targetEndDate: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    technologyStack: ['React', 'Spring Boot', 'MongoDB Atlas', 'Docker'],
    programmingLanguages: ['Java', 'TypeScript', 'SQL'],
    frameworks: ['Spring Boot 3', 'React 18', 'Tailwind CSS'],
    databaseTech: 'MongoDB Atlas',
    cloudProvider: 'AWS / GCP Cloud',
    repositoryType: 'GIT',
    repositoryVisibility: 'PRIVATE',
    projectVisibility: 'PRIVATE',
    deploymentType: 'CLOUD',
    projectManagerId: '',
    projectLeadId: '',
    projectSponsor: '',
    customerRepresentative: '',
    assignedEmployees: [],
    assignedInterns: [],
    links: {
      githubUrl: '',
      frontendRepoUrl: '',
      backendRepoUrl: '',
      dockerRepoUrl: '',
      cicdPipelineUrl: '',
      productionUrl: '',
      stagingUrl: '',
      swaggerUrl: '',
      figmaUrl: '',
      jiraUrl: '',
      confluenceUrl: '',
    },
    remarks: '',
    tags: ['Enterprise', 'Production-Ready'],
  });

  const [assignData, setAssignData] = useState<{
    projectManagerId: string;
    projectLeadId: string;
    assignedEmployees: string[];
    assignedInterns: string[];
  }>({
    projectManagerId: '',
    projectLeadId: '',
    assignedEmployees: [],
    assignedInterns: [],
  });

  const [linksData, setLinksData] = useState<ProjectLinksData>({});
  const [activities, setActivities] = useState<ProjectActivity[]>([]);

  const userRole = user?.role || 'ROLE_EMPLOYEE';
  const userRoles = user?.roles || [userRole];

  // RBAC Permission Checks (null-safe)
  const isExecutive = (userRoles ?? []).some(r => 
    Boolean(r) && ['ROLE_SUPER_ADMIN', 'SUPER_ADMIN', 'ROLE_ADMIN', 'ADMIN', 'ROLE_CEO', 'CEO', 'ROLE_MD', 'MD', 'ROLE_CTO', 'CTO', 'ROLE_CMO', 'CMO', 'ROLE_COO', 'COO', 'ROLE_VP', 'VP', 'ROLE_DIRECTOR', 'DIRECTOR'].includes(r)
  );

  const canCreate = isExecutive || (userRoles ?? []).some(r => Boolean(r) && ['ROLE_MANAGER', 'MANAGER'].includes(r));
  const canAssign = isExecutive || (userRoles ?? []).some(r => Boolean(r) && ['ROLE_MANAGER', 'MANAGER', 'ROLE_PROJECT_LEAD', 'PROJECT_LEAD'].includes(r));
  const canEditLinks = isExecutive || (userRoles ?? []).some(r => Boolean(r) && ['ROLE_MANAGER', 'MANAGER', 'ROLE_PROJECT_LEAD', 'PROJECT_LEAD'].includes(r));
  const canDelete = isExecutive; // Executive authority required to delete projects

  const loadProjects = async () => {
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const res = await projectsApi.getAll();
      if (res?.success && res?.data && Array.isArray(res.data)) {
        setProjects(res.data);
      } else {
        setProjects([]);
      }
    } catch (err: any) {
      console.error('Failed to fetch projects from MongoDB Atlas:', err);
      setErrorMessage(err?.response?.data?.message || err?.message || 'Failed to load projects from MongoDB');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    setIsSubmitting(true);
    try {
      const res = await projectsApi.create(formData);
      if (res?.success) {
        setIsCreateModalOpen(false);
        loadProjects();
      }
    } catch (err: any) {
      console.error('Create project failed:', err);
      const detail = err?.response?.data?.error?.details || err?.response?.data?.message || err?.message;
      setErrorMessage(`Failed to create project: ${detail}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleAssignSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProject || !selectedProject.id) return;

    // Duplicate selections validation
    const empDuplicates = assignData.assignedEmployees.filter((item, index) => assignData.assignedEmployees.indexOf(item) !== index);
    if (empDuplicates.length > 0) {
      setErrorMessage('Employees cannot appear twice in assigned team members.');
      return;
    }

    const intDuplicates = assignData.assignedInterns.filter((item, index) => assignData.assignedInterns.indexOf(item) !== index);
    if (intDuplicates.length > 0) {
      setErrorMessage('Interns cannot appear twice in assigned interns.');
      return;
    }

    setIsSubmitting(true);
    setErrorMessage(null);
    try {
      const res = await projectsApi.assignMembers(selectedProject.id, {
        projectManagerId: assignData.projectManagerId,
        projectLeadId: assignData.projectLeadId,
        assignedEmployees: assignData.assignedEmployees,
        assignedInterns: assignData.assignedInterns,
        employeeIds: assignData.assignedEmployees,
        internIds: assignData.assignedInterns,
      });

      if (res?.success) {
        setIsAssignModalOpen(false);
        queryClient.invalidateQueries();
        loadProjects();
      }
    } catch (err: any) {
      console.error('Assign members failed:', err);
      setErrorMessage(err?.response?.data?.message || err?.message || 'Failed to assign members');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLinksSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProject || !selectedProject.id) return;
    setIsSubmitting(true);
    try {
      const res = await projectsApi.updateLinks(selectedProject.id, {
        links: linksData,
      });
      if (res?.success) {
        setIsLinksModalOpen(false);
        loadProjects();
      }
    } catch (err: any) {
      console.error('Update links failed:', err);
      setErrorMessage(err?.response?.data?.message || err?.message || 'Failed to update links');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!id) return;
    if (!window.confirm('Are you sure you want to delete this project?')) return;
    try {
      await projectsApi.delete(id);
      loadProjects();
    } catch (err: any) {
      console.error('Delete project failed:', err);
      setErrorMessage(err?.response?.data?.message || err?.message || 'Failed to delete project');
    }
  };

  const openActivityTrail = async (project: EnterpriseProject) => {
    if (!project || !project.id) return;
    setSelectedProject(project);
    try {
      const res = await projectsApi.getActivities(project.id);
      if (res?.success && res?.data && Array.isArray(res.data)) {
        setActivities(res.data);
      } else {
        setActivities([]);
      }
    } catch (err) {
      console.error('Failed to fetch project activities:', err);
      setActivities([]);
    }
    setIsActivityModalOpen(true);
  };

  // ---------------------------------------------------------------------------
  // Null-safe Filter & Search Callback (Fixes crash on line 232)
  // Ensures p.projectName, p.projectCode, p.client, and all other fields are null-safe
  // ---------------------------------------------------------------------------
  const keyword = String(searchTerm ?? '').toLowerCase().trim();

  const filteredProjects = (projects || []).filter((p) => {
    if (!p) return false;

    // Search filter matching against all project searchable fields (null-safe with optional chaining)
    const matchesSearch =
      !keyword ||
      [
        p.projectName,
        p.projectCode,
        p.shortName,
        p.client,
        p.clientOrganization,
        p.customerRepresentative,
        p.department,
        p.businessUnit,
        p.category,
        p.status,
        p.priority,
        p.description,
        p.objectives,
        p.projectManagerName,
        p.projectLeadName,
        p.projectSponsor,
        p.createdBy,
        p.databaseTech,
        p.cloudProvider,
        p.technologyStack,
        p.frameworks,
        p.programmingLanguages,
        p.links?.githubUrl,
        p.links?.productionUrl,
        p.links?.stagingUrl,
      ]
        .flatMap((value) => (Array.isArray(value) ? value : [value]))
        .map((value) => String(value ?? '').toLowerCase())
        .some((value) => value.includes(keyword));

    // Status filter (null-safe)
    const matchesStatus =
      statusFilter === 'ALL' ||
      String(p.status ?? '').toUpperCase() === String(statusFilter ?? '').toUpperCase();

    // Category filter (null-safe)
    const matchesCategory =
      categoryFilter === 'ALL' ||
      String(p.category ?? '').toLowerCase() === String(categoryFilter ?? '').toLowerCase();

    return matchesSearch && matchesStatus && matchesCategory;
  });

  return (
    <div className="space-y-6">
      
      {/* Global Error Alert Banner */}
      {errorMessage && (
        <div className="p-4 bg-rose-500/10 border border-rose-500/30 rounded-2xl flex items-center justify-between text-rose-600 dark:text-rose-400 text-xs font-bold animate-in fade-in">
          <div className="flex items-center gap-2">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{errorMessage}</span>
          </div>
          <button onClick={() => setErrorMessage(null)} className="p-1 hover:bg-rose-500/20 rounded-lg">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Enterprise Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 p-6 rounded-3xl shadow-xs">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 text-xs font-extrabold mb-2 border border-cyan-500/20">
            <Cpu className="w-3.5 h-3.5" />
            <span>MongoDB Atlas Production Core</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            Enterprise Projects & Delivery Management
          </h1>
          <p className="text-xs text-slate-500">
            Lifecycle governance, GitHub/CI-CD integrations, and SLA compliance tracking
          </p>
        </div>

        {canCreate && (
          <button
            onClick={() => {
              setFormData({
                projectCode: `TK-PRJ-${Math.floor(1000 + Math.random() * 9000)}`,
                projectName: '',
                shortName: '',
                description: '',
                objectives: '',
                client: '',
                clientOrganization: '',
                department: 'Engineering',
                category: 'Technical',
                businessUnit: 'Enterprise Solutions',
                projectType: 'FIXED_BID',
                priority: 'MEDIUM',
                status: 'PLANNED',
                estimatedCost: 85000,
                budget: 85000,
                estimatedDuration: 90,
                startDate: new Date().toISOString().split('T')[0],
                targetEndDate: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
                technologyStack: ['React 18', 'Spring Boot 3', 'MongoDB Atlas', 'Docker'],
                programmingLanguages: ['Java 21', 'TypeScript'],
                frameworks: ['Spring Boot', 'React', 'Tailwind CSS'],
                databaseTech: 'MongoDB Atlas',
                cloudProvider: 'AWS Cloud Services',
                repositoryType: 'GIT',
                repositoryVisibility: 'PRIVATE',
                projectVisibility: 'PRIVATE',
                deploymentType: 'CLOUD',
                projectManagerId: '',
                projectLeadId: '',
                assignedEmployees: [],
                assignedInterns: [],
                links: {
                  githubUrl: '',
                  frontendRepoUrl: '',
                  backendRepoUrl: '',
                  productionUrl: '',
                  stagingUrl: '',
                  swaggerUrl: '',
                },
                remarks: '',
                tags: ['Production', 'Enterprise'],
              });
              setActiveTab('basic');
              setIsCreateModalOpen(true);
            }}
            className="px-5 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 text-xs font-extrabold rounded-xl transition-all shadow-md flex items-center gap-2 shrink-0"
          >
            <Plus className="w-4 h-4" />
            <span>Create Enterprise Project</span>
          </button>
        )}
      </div>

      {/* Filter & Search Bar */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 p-4 rounded-2xl flex flex-col md:flex-row items-center justify-between gap-4 shadow-xs">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-3 text-slate-400" />
          <input
            type="text"
            placeholder="Search by project name, code, or client..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs text-slate-900 dark:text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-cyan-500"
          />
        </div>

        <div className="flex flex-wrap items-center gap-3 w-full md:w-auto">
          <div className="flex items-center gap-2">
            <Filter className="w-3.5 h-3.5 text-slate-400" />
            <span className="text-xs font-bold text-slate-500">Status:</span>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3 py-1.5 text-xs font-bold text-slate-700 dark:text-slate-300 focus:outline-none"
            >
              <option value="ALL">All Statuses</option>
              {PROJECT_STATUS_LIST.map((s) => (
                <option key={s.value} value={s.value}>{s.label}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Projects Grid Container */}
      {isLoading ? (
        <div className="p-12 text-center text-slate-400 text-xs font-bold animate-pulse">
          Loading enterprise projects from MongoDB Atlas...
        </div>
      ) : filteredProjects.length === 0 ? (
        <div className="p-12 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl text-center space-y-3">
          <FolderKanban className="w-10 h-10 text-slate-300 mx-auto" />
          <h3 className="text-sm font-bold text-slate-700 dark:text-slate-300">No Projects Found</h3>
          <p className="text-xs text-slate-400">No projects match your current role visibility or search filter.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredProjects.map((prj) => {
            const projectCode = prj.projectCode ?? '-';
            const projectName = prj.projectName ?? '-';
            const description = prj.description || 'Enterprise project delivery module';
            const currentStatus = normalizeProjectStatus(prj.status);
            const progress = getStatusProgress(currentStatus, Math.round(Number(prj.overallProgressPercentage ?? prj.progressPercentage ?? 0)));
            const managerObj = resolveProjectManager(prj, employeesList);
            const leadObj = resolveProjectLead(prj, employeesList);
            const managerName = managerObj ? managerObj.fullName : resolveEmployeeName(prj.projectManagerName || prj.projectManagerId, employeesList);
            const leadName = leadObj ? leadObj.fullName : resolveEmployeeName(prj.projectLeadName || prj.projectLeadId, employeesList);
            const githubUrl = prj.links?.githubUrl;
            const techStack = (prj.technologyStack ?? []).filter(Boolean);
            const isApprover = canApproveProjectStatus(user, prj);

            return (
              <div
                key={prj.id || Math.random().toString()}
                className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-4 shadow-xs hover:border-cyan-500/50 transition-all flex flex-col justify-between group"
              >
                <div className="space-y-3">
                  <div className="flex items-center justify-between gap-2">
                    <span className="px-2.5 py-0.5 rounded-md bg-slate-900 text-cyan-400 text-[10px] font-mono font-bold tracking-wide">
                      {projectCode}
                    </span>
                    
                    {isApprover ? (
                      <select
                        value={currentStatus}
                        onChange={async (e) => {
                          const newStatus = e.target.value;
                          const newProgress = getStatusProgress(newStatus, progress);
                          try {
                            await projectsApi.updateStatus(prj.id, newStatus, 'Status updated via project dashboard', newProgress);
                            await loadProjects();
                          } catch (err) {
                            console.error('Failed to update status:', err);
                          }
                        }}
                        className="text-[11px] font-bold px-2.5 py-1 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 border border-slate-200 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-cyan-500 cursor-pointer hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
                      >
                        {PROJECT_STATUS_LIST.map((s) => (
                          <option key={s.value} value={s.value}>{s.label}</option>
                        ))}
                      </select>
                    ) : (
                      <div className="flex items-center gap-2">
                        <StatusBadge status={currentStatus} />
                        <button
                          type="button"
                          onClick={() => {
                            setRequestStatusProject(prj);
                            setRequestedStatus(currentStatus);
                          }}
                          className="text-[10px] font-bold px-2 py-1 rounded-lg bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 border border-cyan-500/20 hover:bg-cyan-500/20 transition-colors"
                        >
                          Request Status Change
                        </button>
                      </div>
                    )}
                  </div>

                  {/* Pending Status Request Alert Banner */}
                  {prj.pendingStatusRequest && (
                    <div className="p-3 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-700 dark:text-amber-300 text-xs space-y-2">
                      <div className="flex items-center justify-between">
                        <span className="text-[10px] uppercase font-mono font-bold">Pending Status Change Request</span>
                        <span className="text-[10px] text-slate-400 font-mono">{prj.pendingStatusRequest.requestedAt}</span>
                      </div>
                      <p className="text-xs font-bold">
                        {getStatusLabel(prj.status)} → <span className="text-cyan-400 font-black">{getStatusLabel(prj.pendingStatusRequest.requestedStatus)}</span>
                      </p>
                      <p className="text-[11px] text-slate-500 dark:text-slate-400">
                        Requested by: <strong className="text-slate-700 dark:text-slate-200">{prj.pendingStatusRequest.requestedBy}</strong>
                        {prj.pendingStatusRequest.reason && <span> — "{prj.pendingStatusRequest.reason}"</span>}
                      </p>
                      {isApprover && (
                        <div className="flex items-center justify-end gap-2 pt-1">
                          <button
                            type="button"
                            onClick={async () => {
                              const targetStatus = prj.pendingStatusRequest!.requestedStatus;
                              const targetProgress = getStatusProgress(targetStatus, progress);
                              await projectsApi.update(prj.id, { pendingStatusRequest: null });
                              await projectsApi.updateStatus(prj.id, targetStatus, 'Approved pending status request', targetProgress);
                              await loadProjects();
                            }}
                            className="px-3 py-1 bg-emerald-600 hover:bg-emerald-500 text-white font-extrabold text-[10px] rounded-lg shadow-xs"
                          >
                            Approve Change
                          </button>
                          <button
                            type="button"
                            onClick={async () => {
                              await projectsApi.update(prj.id, { pendingStatusRequest: null });
                              await loadProjects();
                            }}
                            className="px-3 py-1 bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-extrabold text-[10px] rounded-lg"
                          >
                            Reject
                          </button>
                        </div>
                      )}
                    </div>
                  )}

                  <div>
                    <h3
                      onClick={() => openProjectWorkspace(prj)}
                      className="font-extrabold text-base text-slate-900 dark:text-white group-hover:text-cyan-600 dark:group-hover:text-cyan-400 transition-colors cursor-pointer hover:underline"
                    >
                      {projectName}
                    </h3>
                    <p className="text-xs text-slate-500 line-clamp-2 mt-1">
                      {description}
                    </p>
                  </div>

                  {/* Progress Bar */}
                  <div className="space-y-1.5 pt-1">
                    <div className="flex justify-between text-xs font-bold">
                      <span className="text-slate-500">Progress</span>
                      <span className="text-cyan-600 dark:text-cyan-400">
                        {progress}%
                      </span>
                    </div>
                    <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-cyan-500 rounded-full transition-all"
                        style={{ width: `${Math.min(100, Math.max(0, progress))}%` }}
                      />
                    </div>
                  </div>

                  {/* Key Roles & URLs Info */}
                  <div className="text-xs space-y-1 text-slate-500 border-t border-slate-100 dark:border-slate-800/80 pt-2">
                    <p><strong className="text-slate-700 dark:text-slate-300">Manager:</strong> {managerName}</p>
                    <p><strong className="text-slate-700 dark:text-slate-300">Lead:</strong> {leadName}</p>
                    {githubUrl && (
                      <p className="truncate text-[11px] text-cyan-600 dark:text-cyan-400">
                        <a href={githubUrl} target="_blank" rel="noreferrer" className="hover:underline inline-flex items-center gap-1">
                          <Github className="w-3 h-3" /> Repository Link
                        </a>
                      </p>
                    )}
                  </div>

                  {/* Stack Pills */}
                  <div className="flex flex-wrap gap-1.5 pt-1">
                    {techStack.slice(0, 4).map((tech, idx) => (
                      <span
                        key={idx}
                        className="px-2 py-0.5 text-[10px] font-bold rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400"
                      >
                        {tech ?? '-'}
                      </span>
                    ))}
                  </div>
                </div>

                {/* Action Buttons Toolbar */}
                <div className="pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between gap-2">
                  <div className="flex items-center gap-1.5">
                    <button
                      onClick={() => openActivityTrail(prj)}
                      className="p-2 text-slate-500 hover:text-cyan-500 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors"
                      title="Audit Activity History"
                    >
                      <Activity className="w-4 h-4" />
                    </button>

                    {canEditLinks && (
                      <button
                        onClick={() => {
                          setSelectedProject(prj);
                          setLinksData(prj.links || {});
                          setIsLinksModalOpen(true);
                        }}
                        className="p-2 text-slate-500 hover:text-cyan-500 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors"
                        title="Manage URLs & Repositories"
                      >
                        <Link2 className="w-4 h-4" />
                      </button>
                    )}

                    {canAssign && (
                      <button
                        onClick={() => {
                          setSelectedProject(prj);
                          setAssignData({
                            projectManagerId: prj.projectManagerId || '',
                            projectLeadId: prj.projectLeadId || '',
                            assignedEmployees: prj.assignedEmployees || [],
                            assignedInterns: prj.assignedInterns || [],
                          });
                          setIsAssignModalOpen(true);
                        }}
                        className="p-2 text-slate-500 hover:text-cyan-500 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors"
                        title="Assign Members & Roles"
                      >
                        <UserPlus className="w-4 h-4" />
                      </button>
                    )}
                  </div>

                  <div className="flex items-center gap-1.5">
                    {canDelete && (
                      <button
                        onClick={() => prj.id && handleDelete(prj.id)}
                        className="p-2 text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950/50 rounded-lg transition-colors"
                        title="Delete Project"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                </div>

              </div>
            );
          })}
        </div>
      )}

      {/* CREATE ENTERPRISE PROJECT MODAL (Redesigned with Sticky Footer & Tabbed Navigation) */}
      {isCreateModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-3 sm:p-6 overflow-y-auto">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-4xl max-h-[90vh] flex flex-col shadow-2xl overflow-hidden animate-in zoom-in-95">
            
            {/* Modal Header */}
            <div className="p-5 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between bg-slate-50/50 dark:bg-slate-900/50 shrink-0">
              <div>
                <h2 className="text-lg font-extrabold text-slate-900 dark:text-white">Create Enterprise Project</h2>
                <p className="text-xs text-slate-500">Configure architecture, stakeholders, repositories, and budget SLA</p>
              </div>
              <button
                onClick={() => setIsCreateModalOpen(false)}
                className="p-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Navigation Tabs */}
            <div className="flex border-b border-slate-200 dark:border-slate-800 px-5 bg-slate-100/50 dark:bg-slate-800/50 text-xs font-bold overflow-x-auto shrink-0">
              {[
                { id: 'basic', label: 'Basic Info', icon: FileText },
                { id: 'tech', label: 'Tech Stack & Cloud', icon: Cpu },
                { id: 'schedule', label: 'Cost & Schedule', icon: DollarSign },
                { id: 'team', label: 'Team Leadership', icon: Users },
                { id: 'urls', label: 'Repositories & URLs', icon: GitBranch },
              ].map((tab) => {
                const IconComp = tab.icon;
                return (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id as any)}
                    className={`px-4 py-3 border-b-2 flex items-center gap-2 transition-all whitespace-nowrap ${
                      activeTab === tab.id
                        ? 'border-cyan-500 text-cyan-600 dark:text-cyan-400 bg-white dark:bg-slate-900'
                        : 'border-transparent text-slate-500 hover:text-slate-800 dark:hover:text-slate-200'
                    }`}
                  >
                    <IconComp className="w-4 h-4" />
                    <span>{tab.label}</span>
                  </button>
                );
              })}
            </div>

            {/* Scrollable Form Body */}
            <form id="create-project-form" onSubmit={handleCreateSubmit} className="flex-1 overflow-y-auto p-6 space-y-4 custom-scrollbar">
              
              {/* TAB 1: BASIC INFO */}
              {activeTab === 'basic' && (
                <div className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Project Code *</label>
                      <input
                        type="text"
                        required
                        value={formData.projectCode || ''}
                        onChange={(e) => setFormData({ ...formData, projectCode: e.target.value })}
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-mono font-bold"
                      />
                    </div>
                    <div className="md:col-span-2">
                      <label className="text-xs font-bold text-slate-500 block mb-1">Project Name *</label>
                      <input
                        type="text"
                        required
                        value={formData.projectName || ''}
                        onChange={(e) => setFormData({ ...formData, projectName: e.target.value })}
                        placeholder="e.g. Next-Gen Enterprise AI Portal"
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold"
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Client Name *</label>
                      <input
                        type="text"
                        required
                        value={formData.client || ''}
                        onChange={(e) => setFormData({ ...formData, client: e.target.value })}
                        placeholder="e.g. Apex Global Logistics"
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold"
                      />
                    </div>
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Status Lifecycle *</label>
                      <select
                        value={formData.status}
                        onChange={(e) => setFormData({ ...formData, status: e.target.value as any })}
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold"
                      >
                        <option value="PLANNED">PLANNED</option>
                        <option value="REQUIREMENT_GATHERING">REQUIREMENT_GATHERING</option>
                        <option value="DESIGN">DESIGN</option>
                        <option value="BACKEND_DEVELOPMENT">BACKEND_DEVELOPMENT</option>
                        <option value="FRONTEND_DEVELOPMENT">FRONTEND_DEVELOPMENT</option>
                        <option value="FULLSTACK_DEVELOPMENT">FULLSTACK_DEVELOPMENT</option>
                        <option value="API_INTEGRATION">API_INTEGRATION</option>
                        <option value="TESTING">TESTING</option>
                        <option value="QA">QA</option>
                        <option value="UAT">UAT</option>
                        <option value="DEPLOYMENT">DEPLOYMENT</option>
                        <option value="LIVE">LIVE</option>
                        <option value="MAINTENANCE">MAINTENANCE</option>
                        <option value="COMPLETED">COMPLETED</option>
                      </select>
                    </div>
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Priority</label>
                      <select
                        value={formData.priority}
                        onChange={(e) => setFormData({ ...formData, priority: e.target.value as any })}
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold"
                      >
                        <option value="LOW">LOW</option>
                        <option value="MEDIUM">MEDIUM</option>
                        <option value="HIGH">HIGH</option>
                        <option value="CRITICAL">CRITICAL</option>
                      </select>
                    </div>
                  </div>

                  <div>
                    <label className="text-xs font-bold text-slate-500 block mb-1">Project Description</label>
                    <textarea
                      rows={3}
                      value={formData.description || ''}
                      onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                      placeholder="Detailed scope of deliverable..."
                      className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
                    />
                  </div>
                </div>
              )}

              {/* TAB 2: TECH STACK & CLOUD */}
              {activeTab === 'tech' && (
                <div className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Technology Stack (Comma-separated)</label>
                      <input
                        type="text"
                        value={(formData.technologyStack || []).join(', ')}
                        onChange={(e) => setFormData({ ...formData, technologyStack: e.target.value.split(',').map(s => s.trim()) })}
                        placeholder="React, Spring Boot, MongoDB, Docker"
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
                      />
                    </div>
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Database Technology</label>
                      <input
                        type="text"
                        value={formData.databaseTech || ''}
                        onChange={(e) => setFormData({ ...formData, databaseTech: e.target.value })}
                        placeholder="MongoDB Atlas"
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Cloud Provider</label>
                      <input
                        type="text"
                        value={formData.cloudProvider || ''}
                        onChange={(e) => setFormData({ ...formData, cloudProvider: e.target.value })}
                        placeholder="AWS / GCP Cloud"
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
                      />
                    </div>
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Deployment Architecture</label>
                      <select
                        value={formData.deploymentType}
                        onChange={(e) => setFormData({ ...formData, deploymentType: e.target.value })}
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold"
                      >
                        <option value="CLOUD">CLOUD</option>
                        <option value="ON_PREMISE">ON_PREMISE</option>
                        <option value="HYBRID">HYBRID</option>
                        <option value="SERVERLESS">SERVERLESS</option>
                      </select>
                    </div>
                  </div>
                </div>
              )}

              {/* TAB 3: COST & SCHEDULE */}
              {activeTab === 'schedule' && (
                <div className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Estimated Budget ($)</label>
                      <input
                        type="number"
                        value={formData.budget || 0}
                        onChange={(e) => setFormData({ ...formData, budget: parseFloat(e.target.value) || 0, estimatedCost: parseFloat(e.target.value) || 0 })}
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold"
                      />
                    </div>
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Start Date</label>
                      <input
                        type="date"
                        value={formData.startDate || ''}
                        onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold"
                      />
                    </div>
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Target End Date</label>
                      <input
                        type="date"
                        value={formData.targetEndDate || ''}
                        onChange={(e) => setFormData({ ...formData, targetEndDate: e.target.value, endDate: e.target.value })}
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold"
                      />
                    </div>
                  </div>
                </div>
              )}

              {/* TAB 4: TEAM LEADERSHIP */}
              {activeTab === 'team' && (
                <div className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <EmployeeSelect
                      label="Project Manager"
                      placeholder="Search & select Project Manager..."
                      isMulti={false}
                      value={formData.projectManagerId || ''}
                      onChange={(val) => setFormData({ ...formData, projectManagerId: val })}
                    />
                    <EmployeeSelect
                      label="Project Lead"
                      placeholder="Search & select Project Lead..."
                      isMulti={false}
                      value={formData.projectLeadId || ''}
                      onChange={(val) => setFormData({ ...formData, projectLeadId: val })}
                    />
                  </div>

                  <div>
                    <EmployeeSelect
                      label="Assigned Team Members / Employees"
                      placeholder="Search & select team members..."
                      isMulti={true}
                      value={formData.assignedEmployees || []}
                      onChange={(val) => setFormData({ ...formData, assignedEmployees: val })}
                    />
                  </div>
                </div>
              )}

              {/* TAB 5: REPOSITORIES & URLS */}
              {activeTab === 'urls' && (
                <div className="space-y-4">
                  <div>
                    <label className="text-xs font-bold text-slate-500 block mb-1">GitHub Repository URL</label>
                    <input
                      type="url"
                      value={formData.links?.githubUrl || ''}
                      onChange={(e) => setFormData({ ...formData, links: { ...formData.links, githubUrl: e.target.value } })}
                      placeholder="https://github.com/techknife/enterprise-repo"
                      className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
                    />
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Production URL</label>
                      <input
                        type="url"
                        value={formData.links?.productionUrl || ''}
                        onChange={(e) => setFormData({ ...formData, links: { ...formData.links, productionUrl: e.target.value } })}
                        placeholder="https://app.techknife.com"
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
                      />
                    </div>
                    <div>
                      <label className="text-xs font-bold text-slate-500 block mb-1">Staging URL</label>
                      <input
                        type="url"
                        value={formData.links?.stagingUrl || ''}
                        onChange={(e) => setFormData({ ...formData, links: { ...formData.links, stagingUrl: e.target.value } })}
                        placeholder="https://staging.techknife.com"
                        className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
                      />
                    </div>
                  </div>
                </div>
              )}

            </form>

            {/* Sticky Modal Footer */}
            <div className="p-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between bg-slate-50/50 dark:bg-slate-900/50 shrink-0">
              <span className="text-[11px] font-bold text-slate-400">All fields saved directly to MongoDB Atlas</span>
              <div className="flex items-center gap-3">
                <button
                  type="button"
                  onClick={() => setIsCreateModalOpen(false)}
                  className="px-5 py-2.5 bg-slate-100 dark:bg-slate-800 text-xs font-bold rounded-xl hover:bg-slate-200 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  form="create-project-form"
                  disabled={isSubmitting}
                  className="px-6 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 disabled:opacity-50"
                >
                  {isSubmitting ? (
                    <span>Saving to MongoDB Atlas...</span>
                  ) : (
                    <>
                      <Plus className="w-4 h-4" />
                      <span>Create Project</span>
                    </>
                  )}
                </button>
              </div>
            </div>

          </div>
        </div>
      )}

      {/* Activity Trail Modal */}
      {isActivityModalOpen && selectedProject && (
        <div className="fixed inset-0 z-50 bg-slate-950/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 max-w-2xl w-full space-y-4 max-h-[85vh] flex flex-col">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div>
                <h3 className="font-extrabold text-base text-slate-900 dark:text-white">
                  Audit Activity Trail — {selectedProject?.projectName ?? '-'}
                </h3>
                <p className="text-xs text-slate-500">Recorded enterprise audit events</p>
              </div>
              <button onClick={() => setIsActivityModalOpen(false)} className="p-1 text-slate-400 hover:text-slate-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto space-y-3 custom-scrollbar pr-1">
              {(activities ?? []).length === 0 ? (
                <div className="text-xs text-slate-400 text-center py-8">No recorded activity logs yet.</div>
              ) : (
                (activities ?? []).map((act) => (
                  <div key={act.id || Math.random().toString()} className="p-3.5 bg-slate-50 dark:bg-slate-800/50 rounded-xl border border-slate-200/60 dark:border-slate-800 text-xs space-y-1">
                    <div className="flex items-center justify-between font-bold">
                      <span className="text-cyan-600 dark:text-cyan-400">{act.action ?? '-'}</span>
                      <span className="text-slate-400 text-[10px] font-mono">
                        {act.timestamp ? new Date(act.timestamp).toLocaleString() : '-'}
                      </span>
                    </div>
                    <p className="text-slate-600 dark:text-slate-400 text-[11px]">
                      By: <strong className="text-slate-800 dark:text-slate-200">{act.performedBy ?? '-'}</strong> ({act.userRole ?? '-'})
                    </p>
                    {act.newValue && (
                      <p className="text-slate-500 text-[10px]">
                        Details: {act.oldValue ? `${act.oldValue} ➔ ` : ''}{act.newValue}
                      </p>
                    )}
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      )}

      {/* Links & Repository Modal */}
      {isLinksModalOpen && selectedProject && (
        <div className="fixed inset-0 z-50 bg-slate-950/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 max-w-xl w-full space-y-4 max-h-[85vh] flex flex-col">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div>
                <h3 className="font-extrabold text-base text-slate-900 dark:text-white">
                  Update Links & Repositories
                </h3>
                <p className="text-xs text-slate-500">{selectedProject?.projectName ?? '-'}</p>
              </div>
              <button onClick={() => setIsLinksModalOpen(false)} className="p-1 text-slate-400 hover:text-slate-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleLinksSubmit} className="flex-1 overflow-y-auto space-y-3 custom-scrollbar pr-1">
              <div>
                <label className="text-xs font-bold text-slate-500 block mb-1">GitHub Repository URL</label>
                <input
                  type="url"
                  value={linksData.githubUrl || ''}
                  onChange={(e) => setLinksData({ ...linksData, githubUrl: e.target.value })}
                  placeholder="https://github.com/techknife/project-repo"
                  className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-500 block mb-1">Production Deployment URL</label>
                <input
                  type="url"
                  value={linksData.productionUrl || ''}
                  onChange={(e) => setLinksData({ ...linksData, productionUrl: e.target.value })}
                  placeholder="https://app.techknife.com"
                  className="w-full p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
                />
              </div>

              <div className="pt-3 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsLinksModalOpen(false)}
                  className="px-4 py-2 bg-slate-100 dark:bg-slate-800 text-xs font-bold rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="px-4 py-2 bg-cyan-500 text-slate-950 font-extrabold text-xs rounded-xl disabled:opacity-50"
                >
                  {isSubmitting ? 'Saving...' : 'Save Links'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Member Assignment Modal */}
      {isAssignModalOpen && selectedProject && (
        <div className="fixed inset-0 z-50 bg-slate-950/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 max-w-xl w-full space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div>
                <h3 className="font-extrabold text-base text-slate-900 dark:text-white">
                  Assign Members — {selectedProject?.projectName ?? '-'}
                </h3>
                <p className="text-xs text-slate-500">Configure Project Manager, Lead, Employees & Interns</p>
              </div>
              <button onClick={() => setIsAssignModalOpen(false)} className="p-1 text-slate-400 hover:text-slate-600">
                <X className="w-5 h-5" />
              </button>
            </div>
            <form onSubmit={handleAssignSubmit} className="space-y-4">
              <EmployeeSelect
                label="Project Manager"
                placeholder="Search & select Project Manager..."
                isMulti={false}
                value={assignData.projectManagerId}
                onChange={(val) => setAssignData({ ...assignData, projectManagerId: val })}
              />

              <EmployeeSelect
                label="Project Lead"
                placeholder="Search & select Project Lead..."
                isMulti={false}
                value={assignData.projectLeadId}
                onChange={(val) => setAssignData({ ...assignData, projectLeadId: val })}
              />

              <EmployeeSelect
                label="Assigned Team Members / Employees"
                placeholder="Search & select team members..."
                isMulti={true}
                value={assignData.assignedEmployees}
                onChange={(val) => setAssignData({ ...assignData, assignedEmployees: val })}
              />

              <InternSelect
                label="Assigned Interns"
                placeholder="Search & select interns..."
                isMulti={true}
                value={assignData.assignedInterns}
                onChange={(val) => setAssignData({ ...assignData, assignedInterns: val })}
              />

              <div className="pt-3 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsAssignModalOpen(false)}
                  className="px-4 py-2 bg-slate-100 dark:bg-slate-800 text-xs font-bold rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="px-4 py-2 bg-cyan-500 text-slate-950 font-extrabold text-xs rounded-xl disabled:opacity-50"
                >
                  {isSubmitting ? 'Saving...' : 'Save Assignments'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Full-Screen Enterprise Project Workspace */}
      <EnterpriseProjectWorkspace
        project={workspaceProject}
        isOpen={isWorkspaceOpen}
        onClose={() => setIsWorkspaceOpen(false)}
        onProjectUpdated={() => loadProjects()}
      />

      {/* Request Status Change Modal for Unauthorized Users */}
      {requestStatusProject && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <form
            onSubmit={async (e) => {
              e.preventDefault();
              const pendingReq = {
                requestedStatus,
                reason: requestReason.trim(),
                requestedBy: user ? `${user.firstName} ${user.lastName}` : 'Team Member',
                requestedByRole: user?.role || 'EMPLOYEE',
                requestedAt: new Date().toISOString().split('T')[0],
              };
              await projectsApi.update(requestStatusProject.id, { pendingStatusRequest: pendingReq });
              setRequestStatusProject(null);
              setRequestReason('');
              await loadProjects();
            }}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4 shadow-xl text-slate-800 dark:text-slate-200"
          >
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="text-base font-extrabold text-slate-900 dark:text-white">
                Request Project Status Change
              </h3>
              <button type="button" onClick={() => setRequestStatusProject(null)} className="p-1 text-slate-400 hover:text-slate-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <p className="text-xs text-slate-500 font-medium">
              Current Official Status: <strong className="text-slate-900 dark:text-white font-bold">{getStatusLabel(requestStatusProject.status)}</strong>
            </p>

            <div>
              <label className="text-xs font-bold block mb-1">Requested Status *</label>
              <select
                value={requestedStatus}
                onChange={(e) => setRequestedStatus(e.target.value)}
                className="w-full text-xs font-bold p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              >
                {PROJECT_STATUS_LIST.map((s) => (
                  <option key={s.value} value={s.value}>
                    {s.label} ({s.progress >= 0 ? `${s.progress}% Derived Progress` : 'Preserves Progress'})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="text-xs font-bold block mb-1">Reason / Justification (Optional)</label>
              <textarea
                rows={3}
                value={requestReason}
                onChange={(e) => setRequestReason(e.target.value)}
                placeholder="Explain deliverable milestone status..."
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setRequestStatusProject(null)}
                className="px-4 py-2 bg-slate-100 dark:bg-slate-800 font-bold text-xs text-slate-700 dark:text-slate-300 rounded-xl"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-2 bg-cyan-500 text-slate-950 font-black text-xs rounded-xl shadow-md"
              >
                Submit Status Request
              </button>
            </div>
          </form>
        </div>
      )}

    </div>
  );
};
