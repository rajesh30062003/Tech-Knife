import React, { useState, useEffect } from 'react';
import { X, GraduationCap, Building2, User, Mail, Phone, Calendar, BookOpen, DollarSign, Code, Award, Loader2 } from 'lucide-react';
import { Intern } from '../../types';

interface InternFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: Partial<Intern>) => Promise<void>;
  initialData?: Intern | null;
  departments: string[];
  mentors: string[];
}

export const InternFormModal: React.FC<InternFormModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  departments,
  mentors,
}) => {
  const [formData, setFormData] = useState<Partial<Intern>>({
    firstName: '',
    lastName: '',
    officialEmail: '',
    personalEmail: '',
    primaryMobile: '',
    alternateMobile: '',
    college: '',
    university: '',
    degree: 'Bachelor of Technology',
    branch: 'Computer Science',
    semester: '6th Semester',
    cgpa: 3.8,
    resumeUrl: '',
    offerLetterUrl: '',
    joiningDate: '2026-06-01',
    endDate: '2026-12-01',
    mentor: 'Sarah Connor (CTO)',
    department: 'Engineering & DevOps',
    skills: ['Java', 'Spring Boot 3', 'React'],
    githubUsername: '',
    stipend: '$3,800/mo',
  });

  const [skillsInput, setSkillsInput] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (initialData) {
      setFormData(initialData);
      setSkillsInput(initialData.skills?.join(', ') || '');
    } else {
      setFormData({
        firstName: '',
        lastName: '',
        officialEmail: '',
        personalEmail: '',
        primaryMobile: '',
        alternateMobile: '',
        college: 'Stanford School of Engineering',
        university: 'Stanford University',
        degree: 'Master of Science',
        branch: 'Software Systems',
        semester: '3rd Semester',
        cgpa: 3.9,
        resumeUrl: '',
        offerLetterUrl: '',
        joiningDate: new Date().toISOString().split('T')[0],
        endDate: '2026-12-31',
        mentor: mentors[0] || 'Sarah Connor (CTO)',
        department: departments[0] || 'Engineering & DevOps',
        skills: ['Spring Boot 3', 'React 19', 'TypeScript'],
        githubUsername: '',
        stipend: '$3,800/mo',
      });
      setSkillsInput('Spring Boot 3, React 19, TypeScript');
    }
  }, [initialData, isOpen, departments, mentors]);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    const parsedSkills = skillsInput
      .split(',')
      .map((s) => s.trim())
      .filter(Boolean);

    try {
      await onSubmit({
        ...formData,
        skills: parsedSkills,
      });
      onClose();
    } catch (err) {
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4 overflow-y-auto">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-3xl p-6 space-y-6 shadow-2xl relative my-8">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
          <div className="flex items-center gap-3">
            <div className="p-2.5 bg-cyan-500/10 text-cyan-600 dark:text-cyan-400 rounded-2xl">
              <GraduationCap className="w-6 h-6" />
            </div>
            <div>
              <h3 className="text-lg font-extrabold text-slate-900 dark:text-white">
                {initialData ? 'Update Intern Profile' : 'Register New Intern Cohort Member'}
              </h3>
              <p className="text-xs text-slate-500">
                Configure academic record, assigned mentor, skills & corporate credentials
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl bg-slate-100 dark:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="space-y-6 text-xs">
          {/* Section 1: Basic & Official Details */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
              <User className="w-3.5 h-3.5 text-cyan-500" /> Basic & Corporate Contact Information
            </h4>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">First Name *</label>
                <input
                  type="text"
                  required
                  value={formData.firstName || ''}
                  onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  placeholder="e.g. Maya"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Last Name *</label>
                <input
                  type="text"
                  required
                  value={formData.lastName || ''}
                  onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                  placeholder="e.g. Patel"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Official Corporate Email *</label>
                <input
                  type="email"
                  required
                  value={formData.officialEmail || ''}
                  onChange={(e) => setFormData({ ...formData, officialEmail: e.target.value })}
                  placeholder="m.patel@techknife.com"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Personal Email *</label>
                <input
                  type="email"
                  required
                  value={formData.personalEmail || ''}
                  onChange={(e) => setFormData({ ...formData, personalEmail: e.target.value })}
                  placeholder="maya.patel@gmail.com"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Primary Mobile *</label>
                <input
                  type="text"
                  required
                  value={formData.primaryMobile || ''}
                  onChange={(e) => setFormData({ ...formData, primaryMobile: e.target.value })}
                  placeholder="+1 (555) 345-6789"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">GitHub Username</label>
                <input
                  type="text"
                  value={formData.githubUsername || ''}
                  onChange={(e) => setFormData({ ...formData, githubUsername: e.target.value })}
                  placeholder="e.g. mayapatel-ui"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>
            </div>
          </div>

          {/* Section 2: Academic & College Records */}
          <div className="space-y-3 pt-3 border-t border-slate-100 dark:border-slate-800">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
              <BookOpen className="w-3.5 h-3.5 text-cyan-500" /> Academic & Institution Background
            </h4>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">University / Institute *</label>
                <input
                  type="text"
                  required
                  value={formData.university || ''}
                  onChange={(e) => setFormData({ ...formData, university: e.target.value })}
                  placeholder="Stanford University"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">College / School</label>
                <input
                  type="text"
                  value={formData.college || ''}
                  onChange={(e) => setFormData({ ...formData, college: e.target.value })}
                  placeholder="School of Engineering"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Degree</label>
                <input
                  type="text"
                  value={formData.degree || ''}
                  onChange={(e) => setFormData({ ...formData, degree: e.target.value })}
                  placeholder="Master of Science"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Branch / Specialization</label>
                <input
                  type="text"
                  value={formData.branch || ''}
                  onChange={(e) => setFormData({ ...formData, branch: e.target.value })}
                  placeholder="Software Systems"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Semester / Year</label>
                <input
                  type="text"
                  value={formData.semester || ''}
                  onChange={(e) => setFormData({ ...formData, semester: e.target.value })}
                  placeholder="3rd Semester"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">CGPA Score</label>
                <input
                  type="number"
                  step="0.01"
                  max="4.0"
                  min="0"
                  value={formData.cgpa || 3.8}
                  onChange={(e) => setFormData({ ...formData, cgpa: parseFloat(e.target.value) })}
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>
            </div>
          </div>

          {/* Section 3: Corporate Placement & Mentor Assignment */}
          <div className="space-y-3 pt-3 border-t border-slate-100 dark:border-slate-800">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
              <Building2 className="w-3.5 h-3.5 text-cyan-500" /> Department, Mentor & Duration
            </h4>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Assigned Department</label>
                <select
                  value={formData.department || ''}
                  onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                >
                  {departments.map((d) => (
                    <option key={d} value={d}>
                      {d}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Assigned Senior Mentor *</label>
                <select
                  value={formData.mentor || ''}
                  onChange={(e) => setFormData({ ...formData, mentor: e.target.value })}
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                >
                  {mentors.map((m) => (
                    <option key={m} value={m}>
                      {m}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Joining Date *</label>
                <input
                  type="date"
                  required
                  value={formData.joiningDate || ''}
                  onChange={(e) => setFormData({ ...formData, joiningDate: e.target.value })}
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">End Date *</label>
                <input
                  type="date"
                  required
                  value={formData.endDate || ''}
                  onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Monthly Stipend</label>
                <input
                  type="text"
                  value={formData.stipend || ''}
                  onChange={(e) => setFormData({ ...formData, stipend: e.target.value })}
                  placeholder="$3,800/mo"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">Technical Skills (comma separated)</label>
                <input
                  type="text"
                  value={skillsInput}
                  onChange={(e) => setSkillsInput(e.target.value)}
                  placeholder="Java, Spring Boot, React, MongoDB"
                  className="w-full px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white font-medium"
                />
              </div>
            </div>
          </div>

          {/* Buttons */}
          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100 dark:border-slate-800">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-xs font-bold text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-6 py-2 bg-cyan-600 hover:bg-cyan-500 text-white font-bold text-xs rounded-xl shadow transition-colors flex items-center gap-2 disabled:opacity-50"
            >
              {isSubmitting && <Loader2 className="w-4 h-4 animate-spin" />}
              <span>{initialData ? 'Update Profile' : 'Save Intern Record'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
