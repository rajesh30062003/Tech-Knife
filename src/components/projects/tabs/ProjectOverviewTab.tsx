import React from 'react';
import { 
  Briefcase, Calendar, DollarSign, Clock, Layers, Cpu, Database, Cloud, 
  ExternalLink, Github, Terminal, CheckCircle2, ShieldCheck, UserCheck 
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';

interface ProjectOverviewTabProps {
  project: EnterpriseProject;
}

export const ProjectOverviewTab: React.FC<ProjectOverviewTabProps> = ({ project }) => {
  const links = project.links || {};

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Top Metadata Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-800 space-y-1">
          <span className="text-[10px] uppercase font-bold text-slate-400 block flex items-center gap-1">
            <Briefcase className="w-3.5 h-3.5 text-indigo-500" /> Client & Business Unit
          </span>
          <p className="font-extrabold text-sm text-slate-900 dark:text-white truncate">
            {project.client || project.clientOrganization || 'Internal Enterprise'}
          </p>
          <p className="text-xs text-slate-500 font-medium">
            {project.department || 'Engineering'} • {project.businessUnit || 'Core Platform'}
          </p>
        </div>

        <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-800 space-y-1">
          <span className="text-[10px] uppercase font-bold text-slate-400 block flex items-center gap-1">
            <DollarSign className="w-3.5 h-3.5 text-emerald-500" /> Budget & SLA Valuation
          </span>
          <p className="font-extrabold text-sm text-emerald-600 dark:text-emerald-400 font-mono">
            ${(project.budget || project.estimatedCost || 120000).toLocaleString('en-US')} USD
          </p>
          <p className="text-xs text-slate-500 font-medium flex items-center gap-1">
            <Clock className="w-3 h-3 text-slate-400" /> {project.estimatedHours || 480} Est. Hours ({project.estimatedDuration || 12} Weeks)
          </p>
        </div>

        <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-800 space-y-1">
          <span className="text-[10px] uppercase font-bold text-slate-400 block flex items-center gap-1">
            <UserCheck className="w-3.5 h-3.5 text-cyan-500" /> Project Leadership
          </span>
          <p className="font-extrabold text-xs text-slate-900 dark:text-white truncate">
            Manager: {project.projectManagerName || 'Unassigned'}
          </p>
          <p className="text-xs text-slate-500 font-medium truncate">
            Lead: {project.projectLeadName || 'Unassigned'}
          </p>
        </div>

        <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-800 space-y-1">
          <span className="text-[10px] uppercase font-bold text-slate-400 block flex items-center gap-1">
            <Calendar className="w-3.5 h-3.5 text-amber-500" /> Target Timeline
          </span>
          <p className="font-extrabold text-xs text-slate-900 dark:text-white font-mono">
            {project.startDate || '2026-01-01'} → {project.endDate || project.targetEndDate || '2026-12-31'}
          </p>
          <p className="text-xs text-slate-500 font-medium">
            Type: {project.projectType || 'Full-Stack Enterprise'}
          </p>
        </div>
      </div>

      {/* Description & Objectives */}
      <div className="p-5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 space-y-3">
        <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
          <ShieldCheck className="w-4 h-4 text-indigo-500" /> Executive Description & Objectives
        </h4>
        <p className="text-xs text-slate-700 dark:text-slate-300 leading-relaxed font-medium">
          {project.description || 'Enterprise project management system deliverable designed for high-availability cloud deployment with integrated security governance, microservices architecture, and automated CI/CD deployment pipelines.'}
        </p>
        {project.objectives && (
          <div className="pt-2 border-t border-slate-200/60 dark:border-slate-800">
            <span className="text-[10px] uppercase font-bold text-indigo-600 dark:text-indigo-400 block mb-1">Key Deliverable Objectives</span>
            <p className="text-xs text-slate-600 dark:text-slate-400 font-medium">{project.objectives}</p>
          </div>
        )}
      </div>

      {/* Technology Stack & Environment Details */}
      <div className="p-5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 space-y-4">
        <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
          <Cpu className="w-4 h-4 text-cyan-500" /> Technology Architecture & Stack
        </h4>

        <div className="flex flex-wrap gap-2">
          {(project.technologyStack || ['Java 21', 'Spring Boot 3.5', 'React 18', 'TypeScript', 'Tailwind CSS', 'MongoDB Atlas', 'Docker']).map((tech, idx) => (
            <span key={idx} className="px-3 py-1 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 text-slate-800 dark:text-slate-200 rounded-xl text-xs font-bold font-mono shadow-2xs">
              {tech}
            </span>
          ))}
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 pt-2 text-xs">
          <div className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200/60 dark:border-slate-800 flex items-center gap-2">
            <Database className="w-4 h-4 text-emerald-500 shrink-0" />
            <div>
              <span className="text-[10px] text-slate-400 uppercase font-bold block">Database</span>
              <span className="font-extrabold text-slate-800 dark:text-slate-200">{project.databaseTech || 'MongoDB Atlas Cluster'}</span>
            </div>
          </div>

          <div className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200/60 dark:border-slate-800 flex items-center gap-2">
            <Cloud className="w-4 h-4 text-indigo-500 shrink-0" />
            <div>
              <span className="text-[10px] text-slate-400 uppercase font-bold block">Cloud Infrastructure</span>
              <span className="font-extrabold text-slate-800 dark:text-slate-200">{project.cloudProvider || 'Google Cloud Platform (GCP)'}</span>
            </div>
          </div>

          <div className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200/60 dark:border-slate-800 flex items-center gap-2">
            <Terminal className="w-4 h-4 text-cyan-500 shrink-0" />
            <div>
              <span className="text-[10px] text-slate-400 uppercase font-bold block">Deployment Type</span>
              <span className="font-extrabold text-slate-800 dark:text-slate-200">{project.deploymentType || 'Kubernetes / Containerized'}</span>
            </div>
          </div>
        </div>
      </div>

      {/* External Repositories & System Links */}
      <div className="p-5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 space-y-3">
        <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
          <ExternalLink className="w-4 h-4 text-amber-500" /> Repositories & Environments
        </h4>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          {[
            { label: 'GitHub Repository', url: links.githubUrl || project.repositoryUrl, icon: Github },
            { label: 'Frontend Repo', url: links.frontendRepoUrl, icon: ExternalLink },
            { label: 'Backend Repo', url: links.backendRepoUrl, icon: ExternalLink },
            { label: 'CI/CD Pipeline', url: links.cicdPipelineUrl, icon: Terminal },
            { label: 'Staging Environment', url: links.stagingUrl, icon: ExternalLink },
            { label: 'Production URL', url: links.productionUrl || links.deploymentUrl, icon: ExternalLink },
            { label: 'Swagger API Docs', url: links.swaggerUrl || links.apiDocUrl, icon: ExternalLink },
            { label: 'Google Drive Folder', url: links.googleDriveUrl || links.driveUrl, icon: ExternalLink },
          ].map((item, idx) => (
            <div key={idx} className="p-3 rounded-xl bg-white dark:bg-slate-900 border border-slate-200/60 dark:border-slate-800 flex items-center justify-between gap-2">
              <div className="flex items-center gap-2 min-w-0">
                <item.icon className="w-4 h-4 text-slate-400 shrink-0" />
                <div className="truncate">
                  <span className="text-[10px] text-slate-400 font-bold block">{item.label}</span>
                  <span className="text-xs font-mono font-medium text-indigo-600 dark:text-indigo-400 truncate block">
                    {item.url || 'Not configured'}
                  </span>
                </div>
              </div>
              {item.url && (
                <a
                  href={item.url.startsWith('http') ? item.url : `https://${item.url}`}
                  target="_blank"
                  rel="noreferrer"
                  className="p-1.5 text-slate-400 hover:text-indigo-600 dark:hover:text-indigo-400 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                >
                  <ExternalLink className="w-3.5 h-3.5" />
                </a>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
