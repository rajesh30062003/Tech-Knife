import React, { useState } from 'react';
import {
  Briefcase,
  Building2,
  Plus,
  Calendar,
  Clock,
  BookOpen,
  Users,
  Shield,
  Trash2,
  Edit3,
  CheckCircle2,
  Award,
} from 'lucide-react';
import { TeachingExperience, EmploymentType } from '../../../types/faculty';

interface TeachingExperienceProps {
  experiences: TeachingExperience[];
  onSaveExperiences: (experiences: TeachingExperience[]) => Promise<TeachingExperience[]>;
}

export const TeachingExperienceSection: React.FC<TeachingExperienceProps> = ({
  experiences,
  onSaveExperiences,
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [activeItem, setActiveItem] = useState<TeachingExperience | null>(null);

  // Form state
  const [form, setForm] = useState<{
    institution: string;
    department: string;
    designation: string;
    joiningDate: string;
    endDate: string;
    isCurrent: boolean;
    employmentType: EmploymentType;
    subjectsTaughtString: string;
    classesHandledString: string;
    teachingHoursPerWeek: number;
    clinicalPosting: string;
    administrativeResponsibility: string;
  }>({
    institution: '',
    department: '',
    designation: '',
    joiningDate: new Date().toISOString().split('T')[0],
    endDate: '',
    isCurrent: true,
    employmentType: 'Full-Time',
    subjectsTaughtString: '',
    classesHandledString: '',
    teachingHoursPerWeek: 12,
    clinicalPosting: '',
    administrativeResponsibility: '',
  });

  const handleOpenAdd = () => {
    setActiveItem(null);
    setForm({
      institution: '',
      department: '',
      designation: '',
      joiningDate: new Date().toISOString().split('T')[0],
      endDate: '',
      isCurrent: true,
      employmentType: 'Full-Time',
      subjectsTaughtString: '',
      classesHandledString: '',
      teachingHoursPerWeek: 12,
      clinicalPosting: '',
      administrativeResponsibility: '',
    });
    setIsEditing(true);
  };

  const handleOpenEdit = (item: TeachingExperience) => {
    setActiveItem(item);
    setForm({
      institution: item.institution,
      department: item.department,
      designation: item.designation,
      joiningDate: item.joiningDate,
      endDate: item.endDate || '',
      isCurrent: item.isCurrent,
      employmentType: item.employmentType,
      subjectsTaughtString: item.subjectsTaught.join(', '),
      classesHandledString: item.classesHandled.join(', '),
      teachingHoursPerWeek: item.teachingHoursPerWeek,
      clinicalPosting: item.clinicalPosting || '',
      administrativeResponsibility: item.administrativeResponsibility || '',
    });
    setIsEditing(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.institution || !form.department || !form.designation) return;

    const subjectsTaught = form.subjectsTaughtString
      .split(',')
      .map((s) => s.trim())
      .filter(Boolean);

    const classesHandled = form.classesHandledString
      .split(',')
      .map((c) => c.trim())
      .filter(Boolean);

    const newItem: TeachingExperience = {
      id: activeItem ? activeItem.id : `teach-${Date.now()}`,
      institution: form.institution,
      department: form.department,
      designation: form.designation,
      joiningDate: form.joiningDate,
      endDate: form.isCurrent ? undefined : form.endDate,
      isCurrent: form.isCurrent,
      employmentType: form.employmentType,
      subjectsTaught,
      classesHandled,
      teachingHoursPerWeek: Number(form.teachingHoursPerWeek),
      clinicalPosting: form.clinicalPosting,
      administrativeResponsibility: form.administrativeResponsibility,
    };

    let updatedList: TeachingExperience[];
    if (activeItem) {
      updatedList = experiences.map((exp) => (exp.id === activeItem.id ? newItem : exp));
    } else {
      updatedList = [newItem, ...experiences];
    }

    await onSaveExperiences(updatedList);
    setIsEditing(false);
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this teaching experience entry?')) {
      const updated = experiences.filter((exp) => exp.id !== id);
      await onSaveExperiences(updated);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
        <div>
          <span className="text-[10px] font-bold text-indigo-500 uppercase tracking-wider">
            Academic & Clinical Track
          </span>
          <h2 className="text-xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <Briefcase className="w-5 h-5 text-indigo-500" /> Teaching Experience & Clinical Postings
          </h2>
        </div>

        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-2xl text-xs font-bold shadow-md flex items-center gap-1.5 transition-all"
        >
          <Plus className="w-4 h-4" /> Add Academic Posting
        </button>
      </div>

      {/* Experience Cards */}
      <div className="space-y-4">
        {experiences.length === 0 ? (
          <div className="p-8 text-center text-slate-500 text-xs">
            No teaching experience recorded. Click &ldquo;Add Academic Posting&rdquo; above.
          </div>
        ) : (
          experiences.map((exp) => (
            <div
              key={exp.id}
              className="p-5 bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-4 relative"
            >
              <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2 border-b border-slate-200 dark:border-slate-700/60 pb-3">
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="text-sm font-extrabold text-slate-900 dark:text-white">{exp.designation}</h3>
                    <span className="px-2.5 py-0.5 text-[10px] font-bold uppercase rounded-full bg-indigo-500/10 text-indigo-600 border border-indigo-500/20">
                      {exp.employmentType}
                    </span>
                    {exp.isCurrent && (
                      <span className="px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-emerald-500/10 text-emerald-500 border border-emerald-500/20">
                        Current Position
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-slate-600 dark:text-slate-300 font-bold flex items-center gap-1 mt-1">
                    <Building2 className="w-3.5 h-3.5 text-slate-400" />
                    {exp.institution} &ndash; <span className="font-medium">{exp.department}</span>
                  </p>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-xs font-mono text-slate-500 flex items-center gap-1">
                    <Calendar className="w-3.5 h-3.5" />
                    {exp.joiningDate} to {exp.isCurrent ? 'Present' : exp.endDate}
                  </span>
                  <div className="flex items-center gap-1 ml-2">
                    <button
                      onClick={() => handleOpenEdit(exp)}
                      className="p-1.5 text-slate-400 hover:text-indigo-600 rounded-lg hover:bg-slate-200 dark:hover:bg-slate-700"
                    >
                      <Edit3 className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDelete(exp.id)}
                      className="p-1.5 text-slate-400 hover:text-red-600 rounded-lg hover:bg-slate-200 dark:hover:bg-slate-700"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>

              {/* Sub Details Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
                <div>
                  <span className="text-[10px] text-slate-400 font-bold uppercase block mb-1">
                    Subjects & Courses Taught
                  </span>
                  <div className="flex flex-wrap gap-1.5">
                    {exp.subjectsTaught.map((sub, idx) => (
                      <span
                        key={idx}
                        className="px-2.5 py-1 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-700 dark:text-slate-300 font-medium"
                      >
                        {sub}
                      </span>
                    ))}
                  </div>
                </div>

                <div>
                  <span className="text-[10px] text-slate-400 font-bold uppercase block mb-1">
                    Student Batches / Classes Handled
                  </span>
                  <div className="flex flex-wrap gap-1.5">
                    {exp.classesHandled.map((cls, idx) => (
                      <span
                        key={idx}
                        className="px-2.5 py-1 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-700 dark:text-slate-300 font-medium"
                      >
                        {cls}
                      </span>
                    ))}
                  </div>
                </div>
              </div>

              <div className="pt-2 border-t border-slate-200/60 dark:border-slate-800 flex flex-wrap items-center gap-6 text-xs text-slate-500 font-medium">
                <span className="flex items-center gap-1">
                  <Clock className="w-3.5 h-3.5 text-indigo-500" />
                  Teaching Hours: <strong className="text-slate-800 dark:text-slate-200">{exp.teachingHoursPerWeek} hrs/week</strong>
                </span>

                {exp.clinicalPosting && (
                  <span className="flex items-center gap-1">
                    <Shield className="w-3.5 h-3.5 text-emerald-500" />
                    Clinical Posting: <strong className="text-slate-800 dark:text-slate-200">{exp.clinicalPosting}</strong>
                  </span>
                )}

                {exp.administrativeResponsibility && (
                  <span className="flex items-center gap-1">
                    <Award className="w-3.5 h-3.5 text-amber-500" />
                    Admin Responsibility: <strong className="text-slate-800 dark:text-slate-200">{exp.administrativeResponsibility}</strong>
                  </span>
                )}
              </div>
            </div>
          ))
        )}
      </div>

      {/* Edit / Add Modal */}
      {isEditing && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-xl w-full p-6 shadow-2xl space-y-4">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">
              {activeItem ? 'Edit Academic Experience' : 'Add Academic Experience'}
            </h3>

            <form onSubmit={handleSave} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold mb-1">Institution Name *</label>
                <input
                  type="text"
                  required
                  value={form.institution}
                  onChange={(e) => setForm({ ...form, institution: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  placeholder="e.g. Stanford University / Tech Knife Institute"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold mb-1">Department *</label>
                  <input
                    type="text"
                    required
                    value={form.department}
                    onChange={(e) => setForm({ ...form, department: e.target.value })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                    placeholder="Computer Science & AI"
                  />
                </div>
                <div>
                  <label className="block font-bold mb-1">Designation *</label>
                  <input
                    type="text"
                    required
                    value={form.designation}
                    onChange={(e) => setForm({ ...form, designation: e.target.value })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                    placeholder="Professor & Head"
                  />
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block font-bold mb-1">Employment Type</label>
                  <select
                    value={form.employmentType}
                    onChange={(e) => setForm({ ...form, employmentType: e.target.value as EmploymentType })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  >
                    <option value="Full-Time">Full-Time</option>
                    <option value="Part-Time">Part-Time</option>
                    <option value="Visiting">Visiting</option>
                    <option value="Adjunct">Adjunct</option>
                    <option value="Clinical">Clinical</option>
                  </select>
                </div>
                <div>
                  <label className="block font-bold mb-1">Joining Date</label>
                  <input
                    type="date"
                    value={form.joiningDate}
                    onChange={(e) => setForm({ ...form, joiningDate: e.target.value })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  />
                </div>
                <div>
                  <label className="block font-bold mb-1">End Date</label>
                  <input
                    type="date"
                    disabled={form.isCurrent}
                    value={form.endDate}
                    onChange={(e) => setForm({ ...form, endDate: e.target.value })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5 disabled:opacity-40"
                  />
                </div>
              </div>

              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  id="isCurrent"
                  checked={form.isCurrent}
                  onChange={(e) => setForm({ ...form, isCurrent: e.target.checked })}
                  className="rounded text-indigo-600"
                />
                <label htmlFor="isCurrent" className="font-bold cursor-pointer">
                  Current Position (Ongoing)
                </label>
              </div>

              <div>
                <label className="block font-bold mb-1">Subjects Taught (comma separated)</label>
                <input
                  type="text"
                  value={form.subjectsTaughtString}
                  onChange={(e) => setForm({ ...form, subjectsTaughtString: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  placeholder="Deep Learning, Distributed AI, MLOps"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold mb-1">Classes Handled</label>
                  <input
                    type="text"
                    value={form.classesHandledString}
                    onChange={(e) => setForm({ ...form, classesHandledString: e.target.value })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                    placeholder="Ph.D. Cohort 2024, M.Tech Year 2"
                  />
                </div>
                <div>
                  <label className="block font-bold mb-1">Teaching Hours / Week</label>
                  <input
                    type="number"
                    value={form.teachingHoursPerWeek}
                    onChange={(e) => setForm({ ...form, teachingHoursPerWeek: Number(e.target.value) })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold mb-1">Clinical Posting</label>
                  <input
                    type="text"
                    value={form.clinicalPosting}
                    onChange={(e) => setForm({ ...form, clinicalPosting: e.target.value })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                    placeholder="Radiology AI Lab / N/A"
                  />
                </div>
                <div>
                  <label className="block font-bold mb-1">Administrative Responsibility</label>
                  <input
                    type="text"
                    value={form.administrativeResponsibility}
                    onChange={(e) => setForm({ ...form, administrativeResponsibility: e.target.value })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                    placeholder="Research Committee Chair"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setIsEditing(false)}
                  className="px-4 py-2 font-bold text-slate-500 hover:text-slate-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl font-bold shadow-md"
                >
                  Save Entry
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
