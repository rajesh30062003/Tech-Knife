import React from 'react';
import { motion } from 'motion/react';
import { 
  Briefcase, Calendar, DollarSign, Clock, Layers, Cpu, Database, Cloud, 
  ExternalLink, Github, Terminal, CheckCircle2, ShieldCheck, UserCheck, 
  Sparkles, Users, FileText, ArrowUpRight, CheckSquare, Activity, Globe, FolderGit2
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';

interface ProjectOverviewTabProps {
  project: EnterpriseProject;
}

export const ProjectOverviewTab: React.FC<ProjectOverviewTabProps> = ({ project }) => {
  const links = project.links || {};
  const progress = project.overallProgressPercentage ?? project.progressPercentage ?? 68;
  const budget = project.budget || project.estimatedCost || 85000;
  const estimatedHours = project.estimatedHours || 480;
  const estimatedWeeks = project.estimatedDuration || 12;
  const totalTeamMembers = (project.assignedEmployees?.length || 0) + (project.assignedInterns?.length || 0);

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
      {/* Top 4 KPI Dashboard Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 lg:gap-5">
        
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

        {/* Card 2: Budget Valuation */}
        <motion.div 
          variants={itemVariants}
          className="group relative overflow-hidden p-5 rounded-2xl bg-white dark:bg-slate-900/90 border border-slate-200/80 dark:border-slate-800/80 shadow-xs hover:shadow-md hover:border-emerald-500/30 transition-all duration-200 flex flex-col justify-between gap-4"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500 flex items-center gap-1.5">
              Budget Valuation
            </span>
            <div className="p-2.5 rounded-xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 group-hover:scale-105 transition-transform">
              <DollarSign className="w-4 h-4" />
            </div>
          </div>

          <div>
            <div className="flex items-baseline gap-1.5">
              <span className="text-2xl lg:text-3xl font-extrabold text-emerald-600 dark:text-emerald-400 font-mono tracking-tight">
                ${budget.toLocaleString('en-US')}
              </span>
              <span className="text-xs font-bold text-slate-400">USD</span>
            </div>
            <p className="text-xs text-slate-500 dark:text-slate-400 font-medium mt-1">
              Cap Limit • Approved Allocation
            </p>
          </div>

          <div className="flex items-center gap-2 pt-1 border-t border-slate-100 dark:border-slate-800 text-[11px] text-slate-500 dark:text-slate-400 font-medium">
            <span className="w-2 h-2 rounded-full bg-emerald-500" />
            <span>Financial Governance Active</span>
          </div>
        </motion.div>

        {/* Card 3: Estimated Duration */}
        <motion.div 
          variants={itemVariants}
          className="group relative overflow-hidden p-5 rounded-2xl bg-white dark:bg-slate-900/90 border border-slate-200/80 dark:border-slate-800/80 shadow-xs hover:shadow-md hover:border-indigo-500/30 transition-all duration-200 flex flex-col justify-between gap-4"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500 flex items-center gap-1.5">
              Duration Target
            </span>
            <div className="p-2.5 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20 group-hover:scale-105 transition-transform">
              <Clock className="w-4 h-4" />
            </div>
          </div>

          <div>
            <div className="flex items-baseline gap-1.5">
              <span className="text-2xl lg:text-3xl font-extrabold text-indigo-600 dark:text-indigo-400 font-mono tracking-tight">
                {estimatedHours}
              </span>
              <span className="text-xs font-bold text-indigo-500">Hours</span>
            </div>
            <p className="text-xs text-slate-500 dark:text-slate-400 font-medium mt-1">
              {estimatedWeeks} Weeks Estimated Sprint Cycle
            </p>
          </div>

          <div className="flex items-center gap-2 pt-1 border-t border-slate-100 dark:border-slate-800 text-[11px] text-slate-500 dark:text-slate-400 font-medium truncate">
            <Calendar className="w-3.5 h-3.5 text-indigo-500 shrink-0" />
            <span className="truncate">{project.startDate || '2026-01-01'} to {project.endDate || project.targetEndDate || '2026-12-31'}</span>
          </div>
        </motion.div>

        {/* Card 4: Active Team Roster */}
        <motion.div 
          variants={itemVariants}
          className="group relative overflow-hidden p-5 rounded-2xl bg-white dark:bg-slate-900/90 border border-slate-200/80 dark:border-slate-800/80 shadow-xs hover:shadow-md hover:border-amber-500/30 transition-all duration-200 flex flex-col justify-between gap-4"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500 flex items-center gap-1.5">
              Team Roster
            </span>
            <div className="p-2.5 rounded-xl bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20 group-hover:scale-105 transition-transform">
              <Users className="w-4 h-4" />
            </div>
          </div>

          <div>
            <div className="flex items-baseline gap-1.5">
              <span className="text-2xl lg:text-3xl font-extrabold text-slate-900 dark:text-white font-mono tracking-tight">
                {totalTeamMembers > 0 ? totalTeamMembers : 1}
              </span>
              <span className="text-xs font-bold text-amber-500">Allocated</span>
            </div>
            <p className="text-xs text-slate-500 dark:text-slate-400 font-medium mt-1 truncate">
              Manager: {project.projectManagerName || 'Unassigned'}
            </p>
          </div>

          <div className="flex items-center justify-between pt-1 border-t border-slate-100 dark:border-slate-800 text-[11px] text-slate-500 dark:text-slate-400 font-medium">
            <span className="truncate">Lead: {project.projectLeadName || 'Unassigned'}</span>
            <UserCheck className="w-3.5 h-3.5 text-amber-500 shrink-0" />
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
            No detailed description provided for this enterprise deliverable yet.
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
            Technology Stack & Cloud Infrastructure
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
                {project.deploymentType || 'Kubernetes / Containerized'}
              </span>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Repositories & System Links Grid */}
      <motion.div 
        variants={itemVariants}
        className="p-6 rounded-2xl bg-white dark:bg-slate-900/90 border border-slate-200/80 dark:border-slate-800/80 shadow-xs space-y-4"
      >
        <div className="flex items-center gap-2.5 border-b border-slate-100 dark:border-slate-800 pb-3">
          <div className="p-2 rounded-xl bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20">
            <FolderGit2 className="w-4 h-4" />
          </div>
          <h3 className="text-lg font-bold text-slate-900 dark:text-white tracking-tight">
            Repositories & System Environments
          </h3>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3.5">
          {[
            { label: 'GitHub Primary Repo', url: links.githubUrl || project.repositoryUrl, icon: Github },
            { label: 'Frontend Repository', url: links.frontendRepoUrl, icon: FolderGit2 },
            { label: 'Backend Repository', url: links.backendRepoUrl, icon: Terminal },
            { label: 'CI/CD Pipeline', url: links.cicdPipelineUrl, icon: Activity },
            { label: 'Staging Environment', url: links.stagingUrl, icon: Globe },
            { label: 'Production URL', url: links.productionUrl || links.deploymentUrl, icon: ExternalLink },
            { label: 'Swagger API Specs', url: links.swaggerUrl || links.apiDocUrl, icon: FileText },
            { label: 'Google Drive Asset Storage', url: links.googleDriveUrl || links.driveUrl, icon: Layers },
          ].map((item, idx) => (
            <div 
              key={idx} 
              className="p-3.5 rounded-xl bg-slate-50 dark:bg-slate-950/60 border border-slate-200/80 dark:border-slate-800/80 flex items-center justify-between gap-3 group hover:border-cyan-500/30 transition-all"
            >
              <div className="flex items-center gap-3 min-w-0">
                <item.icon className="w-4 h-4 text-slate-400 group-hover:text-cyan-500 shrink-0 transition-colors" />
                <div className="truncate">
                  <span className="text-[10px] text-slate-400 font-bold block uppercase tracking-wider">{item.label}</span>
                  <span className="text-xs font-mono font-semibold text-indigo-600 dark:text-indigo-400 truncate block">
                    {item.url || 'Not configured'}
                  </span>
                </div>
              </div>

              {item.url && (
                <a
                  href={item.url.startsWith('http') ? item.url : `https://${item.url}`}
                  target="_blank"
                  rel="noreferrer"
                  className="p-1.5 text-slate-400 hover:text-cyan-500 dark:hover:text-cyan-400 rounded-lg hover:bg-slate-200 dark:hover:bg-slate-800 transition-colors shrink-0"
                  title={`Open ${item.label}`}
                >
                  <ArrowUpRight className="w-4 h-4" />
                </a>
              )}
            </div>
          ))}
        </div>
      </motion.div>
    </motion.div>
  );
};

