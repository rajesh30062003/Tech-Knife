import React, { useState, useEffect } from 'react';
import { 
  FolderKanban, GitBranch, Server, Globe, Terminal, Code2, 
  ExternalLink, Edit3, X, Github, Link2
} from 'lucide-react';
import { toast } from 'sonner';
import { useAuth } from '../../context/AuthContext';
import { projectsApi, EnterpriseProject, ProjectLinksData } from '../../api/projects';
import { canApproveProjectStatus } from '../../constants/projectStatus';

interface EnterpriseDevOpsWorkspaceProps {
  project?: EnterpriseProject;
  onProjectUpdated?: () => void;
  isApprover?: boolean;
}

export const EnterpriseDevOpsWorkspace: React.FC<EnterpriseDevOpsWorkspaceProps> = ({
  project,
  onProjectUpdated,
  isApprover,
}) => {
  const { user } = useAuth();
  
  // Permission calculation: Only authorized roles can edit project repository links
  const canEdit = isApprover ?? canApproveProjectStatus(user, project);

  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const [formLinks, setFormLinks] = useState<ProjectLinksData>({
    githubUrl: '',
    frontendRepoUrl: '',
    backendRepoUrl: '',
    deploymentUrl: '',
    serverUrl: '',
    cicdPipelineUrl: '',
  });

  // Sync formLinks with project.links prop
  useEffect(() => {
    if (project?.links) {
      setFormLinks({
        githubUrl: project.links.githubUrl || '',
        frontendRepoUrl: project.links.frontendRepoUrl || '',
        backendRepoUrl: project.links.backendRepoUrl || '',
        deploymentUrl: project.links.deploymentUrl || project.links.productionUrl || '',
        serverUrl: project.links.serverUrl || '',
        cicdPipelineUrl: project.links.cicdPipelineUrl || '',
      });
    }
  }, [project]);

  const handleOpenEditModal = () => {
    setFormLinks({
      githubUrl: project?.links?.githubUrl || '',
      frontendRepoUrl: project?.links?.frontendRepoUrl || '',
      backendRepoUrl: project?.links?.backendRepoUrl || '',
      deploymentUrl: project?.links?.deploymentUrl || project?.links?.productionUrl || '',
      serverUrl: project?.links?.serverUrl || '',
      cicdPipelineUrl: project?.links?.cicdPipelineUrl || '',
    });
    setIsEditing(true);
  };

  const isValidUrl = (url: string): boolean => {
    if (!url.trim()) return true;
    try {
      const testUrl = url.startsWith('http://') || url.startsWith('https://') ? url : `https://${url}`;
      new URL(testUrl);
      return true;
    } catch {
      return false;
    }
  };

  const handleSaveLinks = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isSaving) return;
    if (!project?.id) {
      toast.error('Project ID missing.');
      return;
    }

    // Validate URLs
    const entries = [
      { name: 'GitHub Project Link', value: formLinks.githubUrl },
      { name: 'Frontend Repository', value: formLinks.frontendRepoUrl },
      { name: 'Backend Repository', value: formLinks.backendRepoUrl },
      { name: 'Frontend Deployment URL', value: formLinks.deploymentUrl },
      { name: 'Backend Deployment URL', value: formLinks.serverUrl },
      { name: 'CI/CD Pipeline URL', value: formLinks.cicdPipelineUrl },
    ];

    for (const item of entries) {
      if (item.value && !isValidUrl(item.value)) {
        toast.error(`Invalid URL format for ${item.name}`);
        return;
      }
    }

    setIsSaving(true);
    try {
      const updatedLinks: ProjectLinksData = {
        ...(project.links || {}),
        githubUrl: formLinks.githubUrl?.trim() || null,
        frontendRepoUrl: formLinks.frontendRepoUrl?.trim() || null,
        backendRepoUrl: formLinks.backendRepoUrl?.trim() || null,
        deploymentUrl: formLinks.deploymentUrl?.trim() || null,
        productionUrl: formLinks.deploymentUrl?.trim() || null,
        serverUrl: formLinks.serverUrl?.trim() || null,
        cicdPipelineUrl: formLinks.cicdPipelineUrl?.trim() || null,
      };

      const res = await projectsApi.update(project.id, { links: updatedLinks });
      const updatedProject = res?.data || { ...project, links: updatedLinks };

      toast.success('Project repository and deployment details updated successfully.');
      setIsEditing(false);
      if (onProjectUpdated) {
        await onProjectUpdated(updatedProject);
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || err.message || 'Failed to update project links.');
    } finally {
      setIsSaving(false);
    }
  };

  const LINK_CARDS = [
    {
      title: 'GitHub Project',
      value: project?.links?.githubUrl,
      icon: Github,
      iconColor: 'text-purple-500 dark:text-purple-400',
      buttonLabel: 'Open Link',
    },
    {
      title: 'Frontend Repository',
      value: project?.links?.frontendRepoUrl,
      icon: Code2,
      iconColor: 'text-cyan-500 dark:text-cyan-400',
      buttonLabel: 'Open Repository',
    },
    {
      title: 'Backend Repository',
      value: project?.links?.backendRepoUrl,
      icon: Terminal,
      iconColor: 'text-indigo-500 dark:text-indigo-400',
      buttonLabel: 'Open Repository',
    },
    {
      title: 'Frontend Deployment',
      value: project?.links?.deploymentUrl || project?.links?.productionUrl,
      icon: Globe,
      iconColor: 'text-emerald-500 dark:text-emerald-400',
      buttonLabel: 'Open Frontend',
    },
    {
      title: 'Backend Deployment',
      value: project?.links?.serverUrl,
      icon: Server,
      iconColor: 'text-blue-500 dark:text-blue-400',
      buttonLabel: 'Open Backend',
    },
    {
      title: 'CI/CD Workflow',
      value: project?.links?.cicdPipelineUrl,
      icon: GitBranch,
      iconColor: 'text-amber-500 dark:text-amber-400',
      buttonLabel: 'Open Workflow',
    },
  ];

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 text-white shadow-xl flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-3 py-1 bg-cyan-500/20 text-cyan-400 font-mono text-xs font-bold rounded-full border border-cyan-500/30 flex items-center gap-1.5">
              <FolderKanban className="w-3.5 h-3.5" /> Project Infrastructure
            </span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black tracking-tight text-white">
            Project Repositories & Deployment
          </h2>
          <p className="text-xs text-slate-400 font-medium">
            Repository and deployment links for this project.
          </p>
        </div>

        {canEdit && (
          <button
            type="button"
            onClick={handleOpenEditModal}
            className="px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-2xl shadow-md transition-all flex items-center gap-2 shrink-0 cursor-pointer"
          >
            <Edit3 className="w-4 h-4" />
            <span>Edit Links</span>
          </button>
        )}
      </div>

      {/* Grid of Repository & Deployment Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {LINK_CARDS.map((item, idx) => {
          const Icon = item.icon;
          const rawVal = item.value?.trim();
          const isConfigured = Boolean(rawVal);
          const safeUrl = isConfigured 
            ? (rawVal!.startsWith('http://') || rawVal!.startsWith('https://') ? rawVal! : `https://${rawVal}`) 
            : '';

          return (
            <div 
              key={idx} 
              className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs flex flex-col justify-between space-y-4"
            >
              <div className="space-y-2">
                <div className="flex items-center gap-2">
                  <div className="p-2 rounded-xl bg-slate-100 dark:bg-slate-800 shrink-0">
                    <Icon className={`w-4 h-4 ${item.iconColor}`} />
                  </div>
                  <h4 className="text-xs font-extrabold text-slate-900 dark:text-white uppercase tracking-wider">
                    {item.title}
                  </h4>
                </div>

                <div className="pt-1 min-h-[38px] flex items-center">
                  {isConfigured ? (
                    <p className="text-xs font-mono font-medium text-slate-700 dark:text-slate-300 break-all line-clamp-2">
                      {rawVal}
                    </p>
                  ) : (
                    <span className="text-xs font-medium text-slate-400 dark:text-slate-500 italic">
                      Not configured
                    </span>
                  )}
                </div>
              </div>

              <div>
                {isConfigured ? (
                  <a
                    href={safeUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="w-full px-3.5 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 text-xs font-bold rounded-xl transition-all inline-flex items-center justify-center gap-1.5 cursor-pointer"
                  >
                    <span>{item.buttonLabel}</span>
                    <ExternalLink className="w-3.5 h-3.5 text-slate-400" />
                  </a>
                ) : (
                  <button
                    disabled
                    className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 text-slate-300 dark:text-slate-700 text-xs font-bold rounded-xl cursor-not-allowed border border-slate-100 dark:border-slate-800"
                  >
                    Not Configured
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Edit Form Modal */}
      {isEditing && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <form
            onSubmit={handleSaveLinks}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-lg w-full p-6 space-y-4 shadow-xl text-slate-800 dark:text-slate-200"
          >
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <Link2 className="w-5 h-5 text-cyan-500" />
                <h3 className="text-base font-extrabold text-slate-900 dark:text-white">
                  Edit Project Repositories & Deployment
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setIsEditing(false)}
                className="p-1 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-3 text-xs">
              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  GitHub Project Link
                </label>
                <input
                  type="text"
                  value={formLinks.githubUrl || ''}
                  onChange={(e) => setFormLinks(prev => ({ ...prev, githubUrl: e.target.value }))}
                  placeholder="https://github.com/org/project"
                  className="w-full p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono text-xs"
                />
              </div>

              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  Frontend Repository
                </label>
                <input
                  type="text"
                  value={formLinks.frontendRepoUrl || ''}
                  onChange={(e) => setFormLinks(prev => ({ ...prev, frontendRepoUrl: e.target.value }))}
                  placeholder="https://github.com/org/frontend-repo"
                  className="w-full p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono text-xs"
                />
              </div>

              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  Backend Repository
                </label>
                <input
                  type="text"
                  value={formLinks.backendRepoUrl || ''}
                  onChange={(e) => setFormLinks(prev => ({ ...prev, backendRepoUrl: e.target.value }))}
                  placeholder="https://github.com/org/backend-repo"
                  className="w-full p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono text-xs"
                />
              </div>

              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  Frontend Deployment URL
                </label>
                <input
                  type="text"
                  value={formLinks.deploymentUrl || ''}
                  onChange={(e) => setFormLinks(prev => ({ ...prev, deploymentUrl: e.target.value }))}
                  placeholder="https://app.example.com"
                  className="w-full p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono text-xs"
                />
              </div>

              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  Backend Deployment URL
                </label>
                <input
                  type="text"
                  value={formLinks.serverUrl || ''}
                  onChange={(e) => setFormLinks(prev => ({ ...prev, serverUrl: e.target.value }))}
                  placeholder="https://api.example.com"
                  className="w-full p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono text-xs"
                />
              </div>

              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  CI/CD Pipeline URL (Optional)
                </label>
                <input
                  type="text"
                  value={formLinks.cicdPipelineUrl || ''}
                  onChange={(e) => setFormLinks(prev => ({ ...prev, cicdPipelineUrl: e.target.value }))}
                  placeholder="https://github.com/org/repo/actions"
                  className="w-full p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono text-xs"
                />
              </div>
            </div>

            <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100 dark:border-slate-800">
              <button
                type="button"
                onClick={() => setIsEditing(false)}
                className="px-4 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-300 font-bold text-xs rounded-xl"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isSaving}
                className="px-5 py-2 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-xl shadow-md disabled:opacity-50 flex items-center gap-1.5 cursor-pointer"
              >
                {isSaving ? 'Saving...' : 'Save Changes'}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
