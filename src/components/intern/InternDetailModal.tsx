import React, { useState } from 'react';
import {
  X,
  GraduationCap,
  Building2,
  Mail,
  Phone,
  Calendar,
  Award,
  BookOpen,
  FileText,
  UserCheck,
  CheckCircle2,
  Clock,
  Briefcase,
  ExternalLink,
  ShieldCheck,
  Zap,
  Plus,
  Send,
  Star,
  UserPlus
} from 'lucide-react';
import { Intern, InternTask } from '../../types';
import { StatusBadge } from '../common/StatusBadge';

interface InternDetailModalProps {
  intern: Intern | null;
  isOpen: boolean;
  onClose: () => void;
  onOpenTasksModal: (intern: Intern) => void;
  onOpenEvaluationModal: (intern: Intern) => void;
  onGenerateCertificate: (intern: Intern) => void;
  onOpenConvertModal: (intern: Intern) => void;
  onStatusChange: (intern: Intern, status: Intern['status']) => void;
}

export const InternDetailModal: React.FC<InternDetailModalProps> = ({
  intern,
  isOpen,
  onClose,
  onOpenTasksModal,
  onOpenEvaluationModal,
  onGenerateCertificate,
  onOpenConvertModal,
  onStatusChange,
}) => {
  const [activeTab, setActiveTab] = useState<'overview' | 'academic' | 'tasks' | 'evaluation'>('overview');

  if (!isOpen || !intern) return null;

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4 overflow-y-auto">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-4xl p-6 space-y-6 shadow-2xl relative my-8">
        
        {/* Header Profile Banner */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-5">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-cyan-500 to-blue-600 text-white font-extrabold text-xl flex items-center justify-center shadow-md">
              {intern.firstName[0]}
              {intern.lastName[0]}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
                  {intern.firstName} {intern.lastName}
                </h3>
                <StatusBadge status={intern.status === 'Graduated' ? 'Completed' : intern.status} />
              </div>
              <div className="flex items-center gap-3 text-xs text-slate-500 mt-1">
                <span className="font-mono font-bold text-cyan-600 dark:text-cyan-400">{intern.internId}</span>
                <span>•</span>
                <span>{intern.department}</span>
                <span>•</span>
                <span>{intern.university}</span>
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => onOpenConvertModal(intern)}
              className="px-3.5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-bold rounded-xl shadow-md transition-colors flex items-center gap-1.5"
            >
              <UserPlus className="w-3.5 h-3.5" /> Convert to Employee
            </button>

            <button
              onClick={onClose}
              className="p-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl bg-slate-100 dark:bg-slate-800 transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Modal Navigation Tabs */}
        <div className="flex items-center gap-2 border-b border-slate-100 dark:border-slate-800 pb-2 text-xs font-bold">
          <button
            onClick={() => setActiveTab('overview')}
            className={`px-4 py-2 rounded-xl transition-colors ${
              activeTab === 'overview'
                ? 'bg-cyan-50 dark:bg-cyan-950/50 text-cyan-600 dark:text-cyan-400 border border-cyan-200 dark:border-cyan-800'
                : 'text-slate-500 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            Overview & Placement
          </button>
          <button
            onClick={() => setActiveTab('academic')}
            className={`px-4 py-2 rounded-xl transition-colors ${
              activeTab === 'academic'
                ? 'bg-cyan-50 dark:bg-cyan-950/50 text-cyan-600 dark:text-cyan-400 border border-cyan-200 dark:border-cyan-800'
                : 'text-slate-500 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            Academic & Credentials
          </button>
          <button
            onClick={() => setActiveTab('tasks')}
            className={`px-4 py-2 rounded-xl transition-colors ${
              activeTab === 'tasks'
                ? 'bg-cyan-50 dark:bg-cyan-950/50 text-cyan-600 dark:text-cyan-400 border border-cyan-200 dark:border-cyan-800'
                : 'text-slate-500 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            Tasks & Milestones
          </button>
          <button
            onClick={() => setActiveTab('evaluation')}
            className={`px-4 py-2 rounded-xl transition-colors ${
              activeTab === 'evaluation'
                ? 'bg-cyan-50 dark:bg-cyan-950/50 text-cyan-600 dark:text-cyan-400 border border-cyan-200 dark:border-cyan-800'
                : 'text-slate-500 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            Evaluation & Certificate
          </button>
        </div>

        {/* Tab 1: Overview & Placement */}
        {activeTab === 'overview' && (
          <div className="space-y-6 text-xs">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-950 border border-slate-200/80 dark:border-slate-800 space-y-1">
                <span className="text-[10px] uppercase font-bold text-slate-400">Assigned Mentor</span>
                <div className="font-bold text-slate-900 dark:text-white text-sm">{intern.mentor}</div>
                <p className="text-[11px] text-slate-500">Weekly 1-on-1 Code Reviews</p>
              </div>

              <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-950 border border-slate-200/80 dark:border-slate-800 space-y-1">
                <span className="text-[10px] uppercase font-bold text-slate-400">Performance Score</span>
                <div className="font-extrabold text-emerald-600 dark:text-emerald-400 text-xl">{intern.performanceScore}%</div>
                <p className="text-[11px] text-slate-500">Based on submitted milestones</p>
              </div>

              <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-950 border border-slate-200/80 dark:border-slate-800 space-y-1">
                <span className="text-[10px] uppercase font-bold text-slate-400">Monthly Stipend</span>
                <div className="font-extrabold text-indigo-600 dark:text-indigo-400 text-xl">{intern.stipend || '$3,800/mo'}</div>
                <p className="text-[11px] text-slate-500">Term: {intern.joiningDate} to {intern.endDate}</p>
              </div>
            </div>

            {/* Details Table */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 bg-slate-50 dark:bg-slate-950 p-5 rounded-2xl border border-slate-200/80 dark:border-slate-800">
              <div className="space-y-2">
                <div className="flex justify-between border-b border-slate-200/60 dark:border-slate-800 pb-1.5">
                  <span className="text-slate-500 font-medium">Official Email:</span>
                  <span className="font-bold text-slate-900 dark:text-white">{intern.officialEmail}</span>
                </div>
                <div className="flex justify-between border-b border-slate-200/60 dark:border-slate-800 pb-1.5">
                  <span className="text-slate-500 font-medium">Personal Email:</span>
                  <span className="font-bold text-slate-900 dark:text-white">{intern.personalEmail}</span>
                </div>
                <div className="flex justify-between border-b border-slate-200/60 dark:border-slate-800 pb-1.5">
                  <span className="text-slate-500 font-medium">Primary Mobile:</span>
                  <span className="font-bold text-slate-900 dark:text-white">{intern.primaryMobile}</span>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex justify-between border-b border-slate-200/60 dark:border-slate-800 pb-1.5">
                  <span className="text-slate-500 font-medium">Department:</span>
                  <span className="font-bold text-slate-900 dark:text-white">{intern.department}</span>
                </div>
                <div className="flex justify-between border-b border-slate-200/60 dark:border-slate-800 pb-1.5">
                  <span className="text-slate-500 font-medium">GitHub Account:</span>
                  <span className="font-bold text-cyan-600 dark:text-cyan-400">{intern.githubUsername || 'N/A'}</span>
                </div>
                <div className="flex justify-between border-b border-slate-200/60 dark:border-slate-800 pb-1.5">
                  <span className="text-slate-500 font-medium">Attendance Record:</span>
                  <span className="font-bold text-emerald-600 dark:text-emerald-400">{intern.attendance}%</span>
                </div>
              </div>
            </div>

            {/* Assigned Projects */}
            <div>
              <span className="text-[10px] font-bold uppercase text-slate-400 block mb-2">Active Enterprise Projects</span>
              <div className="flex flex-wrap gap-1.5">
                {intern.assignedProjects && intern.assignedProjects.length > 0 ? (
                  intern.assignedProjects.map((p: any, idx: number) => {
                    const pName = typeof p === 'object' && p !== null ? (p.name || p.projectName || p.id) : String(p);
                    if (!pName || pName === 'Not Assigned' || pName === 'Unassigned') return null;
                    return (
                      <span
                        key={idx}
                        className="px-3 py-1 rounded-xl bg-indigo-100 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 font-bold text-[11px] border border-indigo-200 dark:border-indigo-800"
                      >
                        {pName}
                      </span>
                    );
                  })
                ) : (
                  <span className="px-3 py-1 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-500 font-bold text-[11px] border border-slate-200 dark:border-slate-700">
                    Not Assigned
                  </span>
                )}
              </div>
            </div>

            {/* Skills */}
            <div>
              <span className="text-[10px] font-bold uppercase text-slate-400 block mb-2">Technical Skill Matrix</span>
              <div className="flex flex-wrap gap-2">
                {intern.skills.map((s, idx) => (
                  <span
                    key={idx}
                    className="px-3 py-1 rounded-xl bg-cyan-100 dark:bg-cyan-950/60 text-cyan-700 dark:text-cyan-300 font-bold text-[11px] border border-cyan-200 dark:border-cyan-800"
                  >
                    {s}
                  </span>
                ))}
              </div>
            </div>

            {/* Status Quick Actions */}
            <div className="pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
              <span className="font-bold text-slate-500">Update Cohort Status:</span>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => onStatusChange(intern, 'Active')}
                  className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-bold"
                >
                  Activate
                </button>
                <button
                  onClick={() => onStatusChange(intern, 'On Review')}
                  className="px-3 py-1.5 bg-amber-600 hover:bg-amber-500 text-white rounded-xl text-xs font-bold"
                >
                  Place On Review
                </button>
                <button
                  onClick={() => onStatusChange(intern, 'Suspended')}
                  className="px-3 py-1.5 bg-rose-600 hover:bg-rose-500 text-white rounded-xl text-xs font-bold"
                >
                  Suspend
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Tab 2: Academic & Documents */}
        {activeTab === 'academic' && (
          <div className="space-y-6 text-xs">
            <div className="p-5 bg-slate-50 dark:bg-slate-950 rounded-2xl border border-slate-200/80 dark:border-slate-800 space-y-3">
              <h4 className="font-bold text-slate-900 dark:text-white text-sm flex items-center gap-2">
                <BookOpen className="w-4 h-4 text-cyan-500" /> Academic Institution Details
              </h4>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <span className="text-slate-400 block text-[10px]">University / Institution</span>
                  <span className="font-bold text-slate-800 dark:text-slate-200">{intern.university}</span>
                </div>
                <div>
                  <span className="text-slate-400 block text-[10px]">College / School</span>
                  <span className="font-bold text-slate-800 dark:text-slate-200">{intern.college}</span>
                </div>
                <div>
                  <span className="text-slate-400 block text-[10px]">Degree & Specialization</span>
                  <span className="font-bold text-slate-800 dark:text-slate-200">{intern.degree} - {intern.branch}</span>
                </div>
                <div>
                  <span className="text-slate-400 block text-[10px]">Semester / Cumulative GPA</span>
                  <span className="font-bold text-emerald-600 dark:text-emerald-400">{intern.semester} • CGPA: {intern.cgpa} / 4.0</span>
                </div>
              </div>
            </div>

            {/* Documents */}
            <div className="space-y-3">
              <h4 className="font-bold text-slate-900 dark:text-white text-sm flex items-center gap-2">
                <FileText className="w-4 h-4 text-cyan-500" /> Onboarding & Internship Documents
              </h4>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200/80 dark:border-slate-800 rounded-2xl flex items-center justify-between">
                  <div>
                    <span className="font-bold text-slate-800 dark:text-slate-200 block">Academic Resume / CV</span>
                    <span className="text-[10px] text-slate-400">PDF Document</span>
                  </div>
                  <a
                    href={intern.resumeUrl || '#'}
                    target="_blank"
                    rel="noreferrer"
                    className="p-2 bg-cyan-600 hover:bg-cyan-500 text-white rounded-xl transition-colors"
                  >
                    <ExternalLink className="w-4 h-4" />
                  </a>
                </div>

                <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200/80 dark:border-slate-800 rounded-2xl flex items-center justify-between">
                  <div>
                    <span className="font-bold text-slate-800 dark:text-slate-200 block">Signed Internship Offer Letter</span>
                    <span className="text-[10px] text-slate-400">Signed Corporate Contract</span>
                  </div>
                  <a
                    href={intern.offerLetterUrl || '#'}
                    target="_blank"
                    rel="noreferrer"
                    className="p-2 bg-cyan-600 hover:bg-cyan-500 text-white rounded-xl transition-colors"
                  >
                    <ExternalLink className="w-4 h-4" />
                  </a>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Tab 3: Tasks & Milestones */}
        {activeTab === 'tasks' && (
          <div className="space-y-6 text-xs">
            <div className="flex items-center justify-between">
              <h4 className="font-bold text-slate-900 dark:text-white text-sm">Assigned Tasks & Sprint Milestones</h4>
              <button
                onClick={() => onOpenTasksModal(intern)}
                className="px-3 py-1.5 bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-bold rounded-xl shadow transition-colors flex items-center gap-1.5"
              >
                <Plus className="w-3.5 h-3.5" /> Assign New Task
              </button>
            </div>

            {/* Daily Tasks */}
            <div className="space-y-3">
              <span className="text-[10px] uppercase font-bold text-slate-400">Daily Deliverables</span>
              {intern.dailyTasks && intern.dailyTasks.length > 0 ? (
                intern.dailyTasks.map((t) => (
                  <div key={t.id} className="p-3.5 bg-slate-50 dark:bg-slate-950 border border-slate-200/80 dark:border-slate-800 rounded-2xl flex items-center justify-between">
                    <div>
                      <div className="font-bold text-slate-900 dark:text-white">{t.title}</div>
                      <div className="text-[10px] text-slate-500 mt-0.5">Due: {t.dueDate} • Status: {t.status}</div>
                    </div>
                    {t.score && (
                      <span className="px-2.5 py-1 bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 font-extrabold rounded-lg">
                        {t.score}/100
                      </span>
                    )}
                  </div>
                ))
              ) : (
                <p className="text-slate-400 italic">No daily tasks currently assigned.</p>
              )}
            </div>

            {/* Weekly Tasks */}
            <div className="space-y-3">
              <span className="text-[10px] uppercase font-bold text-slate-400">Weekly Milestones</span>
              {intern.weeklyTasks && intern.weeklyTasks.length > 0 ? (
                intern.weeklyTasks.map((t) => (
                  <div key={t.id} className="p-3.5 bg-slate-50 dark:bg-slate-950 border border-slate-200/80 dark:border-slate-800 rounded-2xl flex items-center justify-between">
                    <div>
                      <div className="font-bold text-slate-900 dark:text-white">{t.title}</div>
                      <div className="text-[10px] text-slate-500 mt-0.5">Due: {t.dueDate} • Status: {t.status}</div>
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-slate-400 italic">No weekly milestones assigned.</p>
              )}
            </div>
          </div>
        )}

        {/* Tab 4: Evaluation & Certificate */}
        {activeTab === 'evaluation' && (
          <div className="space-y-6 text-xs">
            <div className="p-5 bg-slate-50 dark:bg-slate-950 rounded-2xl border border-slate-200/80 dark:border-slate-800 space-y-4">
              <div className="flex items-center justify-between">
                <h4 className="font-bold text-slate-900 dark:text-white text-sm flex items-center gap-2">
                  <Star className="w-4 h-4 text-amber-500" /> Mentor Performance Review & Evaluation
                </h4>
                <button
                  onClick={() => onOpenEvaluationModal(intern)}
                  className="px-3 py-1.5 bg-amber-600 hover:bg-amber-500 text-white font-bold rounded-xl text-xs"
                >
                  Submit Performance Evaluation
                </button>
              </div>

              {intern.finalEvaluation ? (
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 pt-2">
                  <div className="p-3 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 text-center">
                    <span className="text-[10px] text-slate-400 font-bold block">Technical Rating</span>
                    <span className="text-lg font-extrabold text-cyan-600">{intern.finalEvaluation.technicalRating} / 5</span>
                  </div>
                  <div className="p-3 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 text-center">
                    <span className="text-[10px] text-slate-400 font-bold block">Soft Skills Rating</span>
                    <span className="text-lg font-extrabold text-cyan-600">{intern.finalEvaluation.softSkillsRating} / 5</span>
                  </div>
                  <div className="p-3 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 text-center">
                    <span className="text-[10px] text-slate-400 font-bold block">Code Quality Rating</span>
                    <span className="text-lg font-extrabold text-cyan-600">{intern.finalEvaluation.codeQualityRating} / 5</span>
                  </div>
                </div>
              ) : (
                <p className="text-slate-400 italic">No final performance review submitted yet.</p>
              )}
            </div>

            {/* Certificate Generation */}
            <div className="p-5 bg-gradient-to-r from-slate-900 to-cyan-950 text-white rounded-2xl space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-bold text-sm text-cyan-300 flex items-center gap-2">
                    <Award className="w-5 h-5 text-amber-400" /> Completion Certificate Generation
                  </h4>
                  <p className="text-[11px] text-slate-300 mt-0.5">
                    Generate official cryptographically signed Tech Knife Internship Certificate metadata
                  </p>
                </div>
                {intern.certificateGenerated ? (
                  <span className="px-3 py-1 bg-emerald-500/20 text-emerald-300 font-bold border border-emerald-500/30 rounded-xl text-xs">
                    Certificate Issued
                  </span>
                ) : (
                  <button
                    onClick={() => onGenerateCertificate(intern)}
                    className="px-4 py-2 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs rounded-xl shadow-md transition-colors"
                  >
                    Generate Certificate
                  </button>
                )}
              </div>
            </div>
          </div>
        )}

      </div>
    </div>
  );
};
