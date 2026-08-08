import React, { useState, useEffect } from 'react';
import { motion } from 'motion/react';
import { 
  Calendar, Cpu, Database, Cloud, Terminal, CheckCircle2, ShieldCheck, 
  Sparkles, Users, FileText
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';
import { employeesApi, EmployeeData } from '../../../api/employees';
import { 
  resolveEmployee, 
  resolveEmployeeName, 
  resolveProjectManager, 
  resolveProjectLead 
} from '../../../utils/employeeResolver';

interface ProjectOverviewTabProps {
  project: EnterpriseProject;
}

interface RosterMember {
  role: string;
  name: string;
  code?: string;
}

const formatSavedCompletionDate = (dateStr?: string | null) => {
  if (!dateStr || !dateStr.trim()) return 'Not set';
  try {
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return dateStr;
    const day = String(d.getDate()).padStart(2, '0');
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const month = monthNames[d.getMonth()];
    const year = d.getFullYear();
    return `${day} ${month} ${year}`;
  } catch {
    return dateStr;
  }
};

export const ProjectOverviewTab: React.FC<ProjectOverviewTabProps> = ({ project }) => {
  const [employeesList, setEmployeesList] = useState<EmployeeData[]>([]);

  useEffect(() => {
    employeesApi.getEmployees().then(res => {
      if (res?.employees && Array.isArray(res.employees)) {
        setEmployeesList(res.employees);
      }
    }).catch(() => {});
  }, []);

  const progress = project.overallProgressPercentage ?? project.progressPercentage ?? 68;
  const completionDateRaw = project.endDate || project.targetEndDate || project.estimatedCompletion;
  const formattedCompletionDate = formatSavedCompletionDate(completionDateRaw);

  const findEmployee = (idOrName?: string | null) => {
    if (!idOrName) return null;
    const target = idOrName.trim().toLowerCase();
    return employeesList.find(e => 
      (e.id && e.id.toLowerCase() === target) ||
      (e.employeeId && e.employeeId.toLowerCase() === target) ||
      (e.employeeCode && e.employeeCode.toLowerCase() === target) ||
      (`${e.firstName} ${e.lastName}`.toLowerCase() === target)
    );
  };

  const getRosterMembers = (): RosterMember[] => {
    const roster: RosterMember[] = [];

    // Project Manager
    const m = resolveProjectManager(project, employeesList);
    if (m) {
      roster.push({ role: 'Project Manager', name: m.fullName, code: m.employeeId });
    } else {
      const cleanName = resolveEmployeeName(project.projectManagerName || project.projectManagerId, employeesList);
      if (cleanName !== 'Unassigned') {
        roster.push({ role: 'Project Manager', name: cleanName });
      }
    }

    // Technical Lead
    const l = resolveProjectLead(project, employeesList);
    if (l) {
      roster.push({ role: 'Technical Lead', name: l.fullName, code: l.employeeId });
    } else {
      const cleanName = resolveEmployeeName(project.projectLeadName || project.projectLeadId, employeesList);
      if (cleanName !== 'Unassigned') {
        roster.push({ role: 'Technical Lead', name: cleanName });
      }
    }

    // Engineers & Interns
    if (project.assignedEmployees && project.assignedEmployees.length > 0) {
      project.assignedEmployees.forEach((item) => {
        if (item !== project.projectManagerId && item !== project.projectLeadId) {
          const emp = resolveEmployee(item, employeesList);
          if (emp) {
            roster.push({ role: 'Engineer', name: emp.fullName, code: emp.employeeId });
          }
        }
      });
    }

    // Interns
    if (project.assignedInterns && project.assignedInterns.length > 0) {
      project.assignedInterns.forEach((item) => {
        const emp = findEmployee(item);
        const name = emp ? `${emp.firstName} ${emp.lastName}` : item;
        const code = emp?.employeeId || emp?.employeeCode;
        roster.push({ role: 'Intern', name, code });
      });
    }

    return roster;
  };

  const rosterList = getRosterMembers();

  const containerVariants = {
    hidden: { opacity: 0, y: 12 },
    visible: { 
      opacity: 1, 
      y: 0,
      transition: { duration: 0.25, staggerChildren: 0.05 } 
    }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 10 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.2 } }
  };

  return (
    <motion.div 
      className="space-y-6 text-slate-800 dark:text-slate-200"
      variants={containerVariants}
      initial="hidden"
      animate="visible"
    >
      {/* Top Simplified KPI Metric Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 lg:gap-5">
        
        {/* Card 1: Overall Progress */}
        <motion.div 
          variants={itemVariants}
          className="group relative overflow-hidden p-5 rounded-2xl bg-white dark:bg-slate-900/90 border border-slate-200/80 dark:border-slate-800/80 shadow-xs hover:shadow-md hover:border-cyan-500/30 transition-all duration-200 flex flex-col justify-between gap-4"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500 flex items-center gap-1.5">
              Overall Progress
            </span>
            <div className="p-2.5 rounded-xl bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 border border-cyan-500/20 group-hover:scale-105 transition-transform">
              <Sparkles className="w-4 h-4" />
            </div>
          </div>

          <div className="space-y-2">
            <div className="flex items-baseline justify-between">
              <span className="text-3xl lg:text-4xl font-extrabold text-cyan-600 dark:text-cyan-400 font-mono tracking-tight">
                {progress}%
              </span>
              <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-cyan-500/10 text-cyan-600 dark:text-cyan-300 border border-cyan-500/20">
                On Track
              </span>
            </div>

            <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
              <motion.div 
                className="h-full bg-gradient-to-r from-cyan-500 to-indigo-500 rounded-full" 
                initial={{ width: 0 }}
                animate={{ width: `${progress}%` }}
                transition={{ duration: 0.6, ease: "easeOut" }}
              />
            </div>
          </div>

          <p className="text-xs text-slate-500 dark:text-slate-400 font-medium">
            Milestone progress target tracked automatically
          </p>
        </motion.div>

        {/* Card 2: Completion Date */}
        <motion.div 
          variants={itemVariants}
          className="group relative overflow-hidden p-5 rounded-2xl bg-white dark:bg-slate-900/90 border border-slate-200/80 dark:border-slate-800/80 shadow-xs hover:shadow-md hover:border-indigo-500/30 transition-all duration-200 flex flex-col justify-between gap-4"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500 flex items-center gap-1.5">
              Completion Date
            </span>
            <div className="p-2.5 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20 group-hover:scale-105 transition-transform">
              <Calendar className="w-4 h-4" />
            </div>
          </div>

          <div>
            <div className="flex items-baseline gap-1.5">
              <span className="text-2xl lg:text-3xl font-extrabold text-indigo-600 dark:text-indigo-400 font-mono tracking-tight">
                {formattedCompletionDate}
              </span>
            </div>
            <p className="text-xs text-slate-500 dark:text-slate-400 font-medium mt-1">
              Saved Target Completion Date
            </p>
          </div>

          <div className="flex items-center gap-2 pt-1 border-t border-slate-100 dark:border-slate-800 text-[11px] text-slate-500 dark:text-slate-400 font-medium">
            <span className="w-2 h-2 rounded-full bg-indigo-500" />
            <span>Project Deadline Target</span>
          </div>
        </motion.div>

        {/* Card 3: Team Roster (All Members) */}
        <motion.div 
          variants={itemVariants}
          className="group relative overflow-hidden p-5 rounded-2xl bg-white dark:bg-slate-900/90 border border-slate-200/80 dark:border-slate-800/80 shadow-xs hover:shadow-md hover:border-amber-500/30 transition-all duration-200 flex flex-col justify-between gap-3"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500 flex items-center gap-1.5">
              Team Roster
            </span>
            <div className="flex items-center gap-2">
              <span className="px-2 py-0.5 rounded-full bg-amber-500/10 text-amber-600 dark:text-amber-400 font-mono text-xs font-extrabold border border-amber-500/20">
                {rosterList.length} {rosterList.length === 1 ? 'Member' : 'Members'}
              </span>
              <div className="p-2 rounded-xl bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20">
                <Users className="w-4 h-4" />
              </div>
            </div>
          </div>

          <div className="max-h-44 overflow-y-auto pr-1 space-y-2">
            {rosterList.length > 0 ? (
              rosterList.map((m, idx) => (
                <div key={idx} className="p-2 rounded-xl bg-slate-50 dark:bg-slate-950/60 border border-slate-200/60 dark:border-slate-800/80 flex items-center justify-between text-xs">
                  <div>
                    <span className="text-[10px] uppercase font-extrabold text-slate-400 block">{m.role}</span>
                    <span className="font-extrabold text-slate-900 dark:text-slate-100">
                      {m.name} {m.code ? <span className="text-slate-400 font-mono font-normal">({m.code})</span> : ''}
                    </span>
                  </div>
                </div>
              ))
            ) : (
              <span className="text-xs text-slate-400 font-medium">No team members assigned</span>
            )}
          </div>
        </motion.div>

      </div>

      {/* Project Description & Executive Summary Card */}
      <motion.div 
        variants={itemVariants}
        className="p-6 rounded-2xl bg-white dark:bg-slate-900/90 border border-slate-200/80 dark:border-slate-800/80 shadow-xs space-y-4"
      >
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20">
              <ShieldCheck className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-lg font-bold text-slate-900 dark:text-white tracking-tight">
                Project Overview & Objectives
              </h3>
              <p className="text-xs text-slate-500 dark:text-slate-400 font-medium">
                {project.client || project.clientOrganization || 'Internal Enterprise'} • {project.department || 'Engineering'} ({project.businessUnit || 'Core Platform'})
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <span className="px-3 py-1 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 text-xs font-bold border border-slate-200 dark:border-slate-700">
              {project.projectType || 'Full-Stack Enterprise'}
            </span>
          </div>
        </div>

        {project.description ? (
          <p className="text-sm text-slate-700 dark:text-slate-300 leading-relaxed font-medium whitespace-pre-wrap break-words">
            {project.description}
          </p>
        ) : (
          <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-950/60 border border-dashed border-slate-300 dark:border-slate-800 text-center text-xs text-slate-400 font-medium">
            No detailed description provided for this deliverable yet.
          </div>
        )}

        {project.objectives && (
          <div className="pt-4 border-t border-slate-100 dark:border-slate-800 space-y-2">
            <span className="text-xs uppercase font-bold text-indigo-600 dark:text-indigo-400 tracking-wider block">
              Key Strategic Deliverable Objectives
            </span>
            <p className="text-xs text-slate-600 dark:text-slate-400 font-medium leading-relaxed bg-indigo-50/50 dark:bg-indigo-950/20 p-3.5 rounded-xl border border-indigo-100 dark:border-indigo-900/30">
              {project.objectives}
            </p>
          </div>
        )}
      </motion.div>

      {/* Technology Architecture & Stack Card */}
      <motion.div 
        variants={itemVariants}
        className="p-6 rounded-2xl bg-white dark:bg-slate-900/90 border border-slate-200/80 dark:border-slate-800/80 shadow-xs space-y-4"
      >
        <div className="flex items-center gap-2.5 border-b border-slate-100 dark:border-slate-800 pb-3">
          <div className="p-2 rounded-xl bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 border border-cyan-500/20">
            <Cpu className="w-4 h-4" />
          </div>
          <h3 className="text-lg font-bold text-slate-900 dark:text-white tracking-tight">
            Technology Stack & Infrastructure
          </h3>
        </div>

        <div className="flex flex-wrap gap-2.5">
          {(project.technologyStack || ['Java 21', 'Spring Boot 3.5', 'React 18', 'TypeScript', 'Tailwind CSS', 'MongoDB Atlas', 'Docker', 'REST API']).map((tech, idx) => (
            <span 
              key={idx} 
              className="px-3.5 py-1.5 bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/80 text-slate-800 dark:text-slate-200 rounded-xl text-xs font-bold font-mono shadow-2xs hover:border-cyan-500/40 transition-colors"
            >
              {tech}
            </span>
          ))}
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3.5 pt-2">
          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-slate-950/60 border border-slate-200/80 dark:border-slate-800/80 flex items-center gap-3">
            <div className="p-2 rounded-lg bg-emerald-500/10 text-emerald-500 border border-emerald-500/20 shrink-0">
              <Database className="w-4 h-4" />
            </div>
            <div className="min-w-0">
              <span className="text-[10px] text-slate-400 uppercase font-bold block">Database Layer</span>
              <span className="font-bold text-xs text-slate-900 dark:text-slate-100 truncate block">
                {project.databaseTech || 'MongoDB Atlas Cluster'}
              </span>
            </div>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-slate-950/60 border border-slate-200/80 dark:border-slate-800/80 flex items-center gap-3">
            <div className="p-2 rounded-lg bg-indigo-500/10 text-indigo-500 border border-indigo-500/20 shrink-0">
              <Cloud className="w-4 h-4" />
            </div>
            <div className="min-w-0">
              <span className="text-[10px] text-slate-400 uppercase font-bold block">Cloud Platform</span>
              <span className="font-bold text-xs text-slate-900 dark:text-slate-100 truncate block">
                {project.cloudProvider || 'Google Cloud Platform'}
              </span>
            </div>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-slate-950/60 border border-slate-200/80 dark:border-slate-800/80 flex items-center gap-3">
            <div className="p-2 rounded-lg bg-cyan-500/10 text-cyan-500 border border-cyan-500/20 shrink-0">
              <Terminal className="w-4 h-4" />
            </div>
            <div className="min-w-0">
              <span className="text-[10px] text-slate-400 uppercase font-bold block">Deployment</span>
              <span className="font-bold text-xs text-slate-900 dark:text-slate-100 truncate block">
                {project.deploymentType || 'Containerized Deployment'}
              </span>
            </div>
          </div>
        </div>
      </motion.div>
    </motion.div>
  );
};
