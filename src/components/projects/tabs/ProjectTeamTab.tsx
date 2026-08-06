import React, { useState } from 'react';
import { 
  Users, UserCheck, ShieldCheck, UserPlus, Save, Loader2, CheckCircle2 
} from 'lucide-react';
import { EnterpriseProject, projectsApi } from '../../../api/projects';
import { EmployeeSelect } from '../../common/EmployeeSelect';
import { InternSelect } from '../../common/InternSelect';

interface ProjectTeamTabProps {
  project: EnterpriseProject;
  onProjectUpdated?: (updated: EnterpriseProject) => void;
}

export const ProjectTeamTab: React.FC<ProjectTeamTabProps> = ({ project, onProjectUpdated }) => {
  const projectId = project.id || project.projectId || '';

  const [managerId, setManagerId] = useState<string>(project.projectManagerId || '');
  const [leadId, setLeadId] = useState<string>(project.projectLeadId || '');
  const [assignedEmployees, setAssignedEmployees] = useState<string[]>(project.assignedEmployees || []);
  const [assignedInterns, setAssignedInterns] = useState<string[]>(project.assignedInterns || []);

  const [isSaving, setIsSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);

  const handleSaveTeam = async () => {
    if (!projectId) return;
    setIsSaving(true);
    setSaveSuccess(false);

    try {
      const res = await projectsApi.assignMembers(projectId, {
        projectManagerId: managerId,
        projectLeadId: leadId,
        assignedEmployees,
        assignedInterns,
      });

      if (res.data && onProjectUpdated) {
        onProjectUpdated(res.data);
      }
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 3000);
    } catch (err) {
      console.error('Failed to update project team assignments:', err);
      alert('Failed to save team member assignments');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400 mb-1">
              <Users className="w-4 h-4" />
              <span>Enterprise Resource & Team Allocation</span>
            </div>
            <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
              Project Team & Stakeholders Governance
            </h3>
            <p className="text-xs text-slate-500">Assign project leadership, full-stack engineers, and intern cohort members</p>
          </div>

          <button
            onClick={handleSaveTeam}
            disabled={isSaving}
            className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 disabled:opacity-50 self-start sm:self-auto"
          >
            {isSaving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
            <span>Save Team Allocation</span>
          </button>
        </div>

        {saveSuccess && (
          <div className="p-3 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-2xl text-xs text-emerald-800 dark:text-emerald-200 font-bold flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600" /> Team member allocations successfully updated!
          </div>
        )}
      </div>

      {/* Leadership Form Section */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        {/* Project Manager Selection */}
        <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-3">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-700 dark:text-slate-300">
            <UserCheck className="w-4 h-4 text-indigo-500" />
            <span>Project Manager Assignment</span>
          </div>
          <EmployeeSelect
            value={managerId}
            onChange={(val) => setManagerId(val as string)}
            multiple={false}
            placeholder="Select Project Manager..."
          />
          {project.projectManagerName && (
            <p className="text-xs text-slate-500 font-medium">
              Current Manager: <span className="font-bold text-slate-800 dark:text-slate-200">{project.projectManagerName}</span>
            </p>
          )}
        </div>

        {/* Project Lead Selection */}
        <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-3">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-700 dark:text-slate-300">
            <ShieldCheck className="w-4 h-4 text-cyan-500" />
            <span>Project Technical Lead Assignment</span>
          </div>
          <EmployeeSelect
            value={leadId}
            onChange={(val) => setLeadId(val as string)}
            multiple={false}
            placeholder="Select Project Technical Lead..."
          />
          {project.projectLeadName && (
            <p className="text-xs text-slate-500 font-medium">
              Current Lead: <span className="font-bold text-slate-800 dark:text-slate-200">{project.projectLeadName}</span>
            </p>
          )}
        </div>
      </div>

      {/* Assigned Engineers Selection */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
        <div className="flex items-center gap-2 text-xs font-bold text-slate-700 dark:text-slate-300">
          <Users className="w-4 h-4 text-emerald-500" />
          <span>Assigned Full-Stack Engineers & Team Members ({assignedEmployees.length})</span>
        </div>
        <EmployeeSelect
          value={assignedEmployees}
          onChange={(val) => setAssignedEmployees(val as string[])}
          multiple={true}
          placeholder="Select assigned engineers..."
        />
      </div>

      {/* Assigned Intern Cohort Selection */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
        <div className="flex items-center gap-2 text-xs font-bold text-slate-700 dark:text-slate-300">
          <UserPlus className="w-4 h-4 text-amber-500" />
          <span>Assigned Intern Cohort Members ({assignedInterns.length})</span>
        </div>
        <InternSelect
          value={assignedInterns}
          onChange={(val) => setAssignedInterns(val)}
          multiple={true}
          placeholder="Select assigned intern team members..."
        />
      </div>
    </div>
  );
};
