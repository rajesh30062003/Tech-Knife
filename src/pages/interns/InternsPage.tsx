import React, { useState, useEffect, useMemo } from 'react';
import {
  GraduationCap,
  Plus,
  Award,
  BookOpen,
  UserCheck,
  Calendar,
  CheckCircle,
  Clock,
  Download,
  Upload,
  RefreshCw,
  MoreVertical,
  Eye,
  Edit2,
  Trash2,
  UserPlus,
  Star,
  CheckSquare,
  FileSpreadsheet,
  ChevronLeft,
  ChevronRight,
  Sparkles
} from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useAuth } from '../../context/AuthContext';
import { canCreateIntern } from '../../utils/rbac';
import { Intern, InternStats, InternStatus, InternTask } from '../../types';
import { internsApi } from '../../api/interns';
import { InternFilters } from '../../components/intern/InternFilters';
import { InternFormModal } from '../../components/intern/InternFormModal';
import { InternDetailModal } from '../../components/intern/InternDetailModal';
import { InternTasksModal } from '../../components/intern/InternTasksModal';
import { InternEvaluationModal } from '../../components/intern/InternEvaluationModal';
import { InternConvertModal } from '../../components/intern/InternConvertModal';

export const InternsPage: React.FC = () => {
  const { user } = useAuth();

  // State
  const [interns, setInterns] = useState<Intern[]>([]);
  const [stats, setStats] = useState<InternStats>({
    totalInterns: 0,
    activeCount: 0,
    graduatedCount: 0,
    suspendedCount: 0,
    averagePerformanceScore: 0,
    ppoConversionRate: 92.5,
    certificatesIssuedCount: 0,
  });

  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [departmentFilter, setDepartmentFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [mentorFilter, setMentorFilter] = useState('ALL');
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize] = useState(6);

  // Modals
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [editingIntern, setEditingIntern] = useState<Intern | null>(null);

  const [selectedDetailIntern, setSelectedDetailIntern] = useState<Intern | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);

  const [taskIntern, setTaskIntern] = useState<Intern | null>(null);
  const [isTasksModalOpen, setIsTasksModalOpen] = useState(false);

  const [evaluationIntern, setEvaluationIntern] = useState<Intern | null>(null);
  const [isEvaluationModalOpen, setIsEvaluationModalOpen] = useState(false);

  const [convertIntern, setConvertIntern] = useState<Intern | null>(null);
  const [isConvertModalOpen, setIsConvertModalOpen] = useState(false);

  const [deleteConfirmId, setDeleteConfirmId] = useState<string | null>(null);

  // Fetch Data
  const loadData = async () => {
    setIsLoading(true);
    try {
      const [res, statsData] = await Promise.all([
        internsApi.getInterns({
          search: searchQuery,
          department: departmentFilter,
          status: statusFilter,
          mentor: mentorFilter,
          page: currentPage,
          limit: pageSize,
        }),
        internsApi.getStatistics(),
      ]);

      setInterns(res.interns);
      setStats(statsData);
    } catch (err) {
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [searchQuery, departmentFilter, statusFilter, mentorFilter, currentPage]);

  const departments = useMemo(() => {
    return Array.from(new Set(interns.map((i) => i.department).filter(Boolean)));
  }, [interns]);

  const mentors = useMemo(() => {
    return Array.from(new Set(interns.map((i) => i.mentor).filter(Boolean)));
  }, [interns]);

  // Actions
  const handleCreateOrUpdate = async (data: Partial<Intern>) => {
    if (editingIntern) {
      await internsApi.updateIntern(editingIntern.id, data);
    } else {
      await internsApi.createIntern(data);
    }
    await loadData();
  };

  const handleDelete = async (id: string) => {
    await internsApi.deleteIntern(id);
    setDeleteConfirmId(null);
    await loadData();
  };

  const handleStatusChange = async (intern: Intern, newStatus: InternStatus) => {
    await internsApi.updateInternStatus(intern.id, newStatus);
    await loadData();
    if (selectedDetailIntern?.id === intern.id) {
      setSelectedDetailIntern({ ...selectedDetailIntern, status: newStatus });
    }
  };

  const handleAssignTask = async (internId: string, task: Partial<InternTask>) => {
    await internsApi.assignTask(internId, task);
    await loadData();
  };

  const handleEvaluation = async (
    internId: string,
    evaluation: NonNullable<Intern['finalEvaluation']>
  ) => {
    await internsApi.evaluateIntern(internId, evaluation);
    await loadData();
  };

  const handleGenerateCertificate = async (intern: Intern) => {
    await internsApi.generateCertificate(intern.id);
    await loadData();
    if (selectedDetailIntern?.id === intern.id) {
      setSelectedDetailIntern({ ...selectedDetailIntern, certificateGenerated: true });
    }
  };

  const handleConvert = async (internId: string) => {
    await internsApi.convertToEmployee(internId);
    await loadData();
  };

  const handleExportCSV = () => {
    const headers = ['ID', 'Name', 'Official Email', 'University', 'Mentor', 'Department', 'Status', 'Score', 'Stipend'];
    const rows = interns.map((i) => [
      i.internId,
      `"${i.firstName} ${i.lastName}"`,
      i.officialEmail,
      `"${i.university}"`,
      `"${i.mentor}"`,
      `"${i.department}"`,
      i.status,
      i.performanceScore,
      i.stipend || '$3,800/mo',
    ]);

    const csvContent = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows.map((e) => e.join(','))].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `TechKnife_Interns_Export_${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="space-y-8 pb-12">
      {/* Top Banner Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-cyan-600 dark:text-cyan-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <GraduationCap className="w-4 h-4" />
            <span>Talent Pipeline & Internship Learning Desk</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Internship Cohort Directory</h1>
          <p className="text-xs text-slate-500">
            Track learning milestones, mentor assignments, stipends, certificate generation & PPO conversions
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <button
            onClick={handleExportCSV}
            className="px-3.5 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-all shadow-xs flex items-center gap-1.5"
          >
            <Download className="w-3.5 h-3.5" /> Export CSV
          </button>

          {canCreateIntern(user) && (
            <button
              onClick={() => {
                setEditingIntern(null);
                setIsFormModalOpen(true);
              }}
              className="inline-flex items-center gap-2 px-4 py-2 bg-cyan-600 hover:bg-cyan-500 text-white font-extrabold text-xs rounded-xl transition-all shadow-md"
            >
              <Plus className="w-3.5 h-3.5" /> Register New Intern
            </button>
          )}
        </div>
      </div>

      {/* Analytics & Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-xs">
          <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider block">Total Active Cohort</span>
          <div className="text-2xl font-extrabold text-slate-900 dark:text-white">{stats.activeCount} Interns</div>
          <p className="text-[11px] text-cyan-600 dark:text-cyan-400 font-semibold">Active Learning Tracks</p>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-xs">
          <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider block">Avg Performance Score</span>
          <div className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400">{stats.averagePerformanceScore}%</div>
          <p className="text-[11px] text-slate-500">Milestones Evaluated</p>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-xs">
          <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider block">PPO Full-Time Conversion</span>
          <div className="text-2xl font-extrabold text-indigo-600 dark:text-indigo-400">{stats.ppoConversionRate}%</div>
          <p className="text-[11px] text-slate-500">Pre-placement offers rate</p>
        </div>

        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1 shadow-xs">
          <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider block">Certificates Issued</span>
          <div className="text-2xl font-extrabold text-amber-600 dark:text-amber-400">{stats.certificatesIssuedCount} Certificates</div>
          <p className="text-[11px] text-slate-500">Graduated Cohorts</p>
        </div>
      </div>

      {/* Advanced Filter Toolbar */}
      <InternFilters
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        departmentFilter={departmentFilter}
        setDepartmentFilter={setDepartmentFilter}
        statusFilter={statusFilter}
        setStatusFilter={setStatusFilter}
        mentorFilter={mentorFilter}
        setMentorFilter={setMentorFilter}
        departments={departments.length ? departments : ['Engineering & DevOps', 'Frontend Engineering', 'AI Systems & Analytics']}
        mentors={mentors.length ? mentors : ['Ganesh Pal (Sr. Developer)', 'Rahul Garai (System Developer)', 'Ranadhir Pal (CEO)']}
        onReset={() => {
          setSearchQuery('');
          setDepartmentFilter('ALL');
          setStatusFilter('ALL');
          setMentorFilter('ALL');
          setCurrentPage(1);
        }}
      />

      {/* Intern Cards Grid */}
      {isLoading ? (
        <div className="p-12 text-center text-slate-400">Loading intern profiles...</div>
      ) : interns.length === 0 ? (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-12 text-center space-y-3">
          <GraduationCap className="w-10 h-10 text-slate-400 mx-auto" />
          <h3 className="font-bold text-slate-800 dark:text-slate-200">No Interns Found</h3>
          <p className="text-xs text-slate-500 max-w-sm mx-auto">
            No active intern matches the specified search queries or filters.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {interns.map((intern) => (
            <div
              key={intern.id}
              className="bg-white dark:bg-slate-900 border border-slate-200/90 dark:border-slate-800 rounded-3xl p-5 space-y-4 hover:border-cyan-500/50 transition-all shadow-sm flex flex-col justify-between"
            >
              {/* Header */}
              <div className="space-y-3">
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-center gap-3">
                    <div className="w-11 h-11 rounded-2xl bg-gradient-to-br from-cyan-500 to-blue-600 text-white font-extrabold flex items-center justify-center text-sm shadow-xs">
                      {intern.firstName[0]}
                      {intern.lastName[0]}
                    </div>
                    <div>
                      <h3 className="font-extrabold text-sm text-slate-900 dark:text-white leading-tight">
                        {intern.firstName} {intern.lastName}
                      </h3>
                      <div className="text-[11px] font-semibold text-slate-500 mt-0.5">{intern.university}</div>
                    </div>
                  </div>
                  <StatusBadge status={intern.status === 'Graduated' ? 'Completed' : intern.status} />
                </div>

                {/* Details */}
                <div className="space-y-2 pt-2 border-t border-slate-100 dark:border-slate-800 text-xs">
                  <div className="flex justify-between text-[11px]">
                    <span className="text-slate-400 font-bold uppercase text-[10px]">Intern ID:</span>
                    <span className="font-mono font-bold text-slate-700 dark:text-slate-300">{intern.internId}</span>
                  </div>

                  <div className="flex justify-between text-[11px]">
                    <span className="text-slate-400 font-bold uppercase text-[10px]">Department:</span>
                    <span className="font-semibold text-slate-800 dark:text-slate-200">{intern.department}</span>
                  </div>

                  <div className="flex justify-between text-[11px]">
                    <span className="text-slate-400 font-bold uppercase text-[10px]">Mentor:</span>
                    <span className="font-semibold text-cyan-600 dark:text-cyan-400">{intern.mentor || 'Not Assigned'}</span>
                  </div>

                  <div className="flex justify-between items-center text-[11px]">
                    <span className="text-slate-400 font-bold uppercase text-[10px]">Current Projects:</span>
                    {intern.assignedProjects && intern.assignedProjects.length > 0 ? (
                      <div className="flex flex-wrap gap-1 justify-end max-w-[170px]">
                        {intern.assignedProjects.map((p: any, idx: number) => {
                          const pName = typeof p === 'object' && p !== null ? (p.name || p.projectName || p.id) : String(p);
                          if (!pName || pName === 'Not Assigned' || pName === 'Unassigned') return null;
                          return (
                            <span key={idx} className="px-1.5 py-0.5 rounded-md bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 font-bold text-[10px] border border-indigo-200/50">
                              {pName}
                            </span>
                          );
                        })}
                      </div>
                    ) : (
                      <span className="font-semibold text-slate-400">Not Assigned</span>
                    )}
                  </div>

                  <div className="flex justify-between text-[11px]">
                    <span className="text-slate-400 font-bold uppercase text-[10px]">Email:</span>
                    <span className="font-medium text-slate-700 dark:text-slate-300 truncate max-w-[140px]">{intern.officialEmail || intern.personalEmail || '-'}</span>
                  </div>

                  <div className="flex justify-between text-[11px]">
                    <span className="text-slate-400 font-bold uppercase text-[10px]">Phone:</span>
                    <span className="font-medium text-slate-700 dark:text-slate-300">{intern.mobileNumber || intern.phoneNumber || '-'}</span>
                  </div>

                  <div className="flex justify-between text-[11px]">
                    <span className="text-slate-400 font-bold uppercase text-[10px]">Tenure:</span>
                    <span className="font-medium text-slate-700 dark:text-slate-300">{intern.startDate || intern.joiningDate || '-'} ➔ {intern.endDate || intern.internshipEndDate || '-'}</span>
                  </div>

                  {/* Progress Bar */}
                  <div className="space-y-1 pt-1">
                    <div className="flex justify-between text-[11px] font-bold">
                      <span className="text-slate-500">Performance Rating</span>
                      <span className="text-emerald-600 dark:text-emerald-400">{intern.performanceScore}%</span>
                    </div>
                    <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-cyan-500 rounded-full transition-all duration-500"
                        style={{ width: `${intern.performanceScore}%` }}
                      />
                    </div>
                  </div>

                  {/* Skills tags */}
                  <div className="flex flex-wrap gap-1 pt-1">
                    {intern.skills.slice(0, 3).map((s, idx) => (
                      <span
                        key={idx}
                        className="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 rounded-lg text-[10px] font-semibold"
                      >
                        {s}
                      </span>
                    ))}
                    {intern.skills.length > 3 && (
                      <span className="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-500 rounded-lg text-[10px] font-semibold">
                        +{intern.skills.length - 3}
                      </span>
                    )}
                  </div>
                </div>
              </div>

              {/* Action Toolbar */}
              <div className="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between text-xs">
                <button
                  onClick={() => {
                    setSelectedDetailIntern(intern);
                    setIsDetailModalOpen(true);
                  }}
                  className="px-3 py-1.5 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-800 dark:text-slate-200 font-bold rounded-xl transition-colors flex items-center gap-1 text-[11px]"
                >
                  <Eye className="w-3.5 h-3.5" /> View Profile
                </button>

                <div className="flex items-center gap-1">
                  <button
                    onClick={() => {
                      setEditingIntern(intern);
                      setIsFormModalOpen(true);
                    }}
                    className="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-slate-500 hover:text-slate-900"
                    title="Edit Intern"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>

                  <button
                    onClick={() => setDeleteConfirmId(intern.id)}
                    className="p-1.5 hover:bg-rose-50 dark:hover:bg-rose-950/40 rounded-lg text-rose-500"
                    title="Delete Intern Record"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Delete Confirmation Dialog */}
      {deleteConfirmId && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-sm w-full p-6 space-y-4 shadow-xl">
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white">Delete Intern Record?</h3>
            <p className="text-xs text-slate-500">
              Are you sure you want to purge this intern from the Tech Knife Enterprise Database? This action is irreversible.
            </p>
            <div className="flex items-center justify-end gap-2 pt-2">
              <button
                onClick={() => setDeleteConfirmId(null)}
                className="px-4 py-2 text-xs font-bold text-slate-600 dark:text-slate-400 hover:bg-slate-100 rounded-xl"
              >
                Cancel
              </button>
              <button
                onClick={() => handleDelete(deleteConfirmId)}
                className="px-4 py-2 text-xs font-bold bg-rose-600 hover:bg-rose-500 text-white rounded-xl shadow"
              >
                Delete Intern
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Form Modal */}
      <InternFormModal
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
        onSubmit={handleCreateOrUpdate}
        initialData={editingIntern}
        departments={departments.length ? departments : ['Engineering & DevOps', 'Frontend Engineering', 'AI Systems & Analytics']}
        mentors={mentors.length ? mentors : ['Ganesh Pal (Sr. Developer)', 'Rahul Garai (System Developer)', 'Ranadhir Pal (CEO)']}
      />

      {/* Detail Profile Modal */}
      <InternDetailModal
        intern={selectedDetailIntern}
        isOpen={isDetailModalOpen}
        onClose={() => setIsDetailModalOpen(false)}
        onOpenTasksModal={(intern) => {
          setTaskIntern(intern);
          setIsTasksModalOpen(true);
        }}
        onOpenEvaluationModal={(intern) => {
          setEvaluationIntern(intern);
          setIsEvaluationModalOpen(true);
        }}
        onGenerateCertificate={handleGenerateCertificate}
        onOpenConvertModal={(intern) => {
          setConvertIntern(intern);
          setIsConvertModalOpen(true);
        }}
        onStatusChange={handleStatusChange}
      />

      {/* Tasks Modal */}
      <InternTasksModal
        intern={taskIntern}
        isOpen={isTasksModalOpen}
        onClose={() => setIsTasksModalOpen(false)}
        onSubmit={handleAssignTask}
      />

      {/* Evaluation Modal */}
      <InternEvaluationModal
        intern={evaluationIntern}
        isOpen={isEvaluationModalOpen}
        onClose={() => setIsEvaluationModalOpen(false)}
        onSubmit={handleEvaluation}
      />

      {/* Convert Modal */}
      <InternConvertModal
        intern={convertIntern}
        isOpen={isConvertModalOpen}
        onClose={() => setIsConvertModalOpen(false)}
        onSubmit={handleConvert}
      />
    </div>
  );
};
