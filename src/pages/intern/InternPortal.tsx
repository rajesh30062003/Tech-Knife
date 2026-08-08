import React, { useState, useEffect } from 'react';
import { GraduationCap, BookOpen, Award, CheckCircle2, FileText, Send, Loader2, Sparkles, Code } from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useAuth } from '../../context/AuthContext';
import { internsApi } from '../../api/interns';
import { Intern } from '../../types';

export const InternPortal: React.FC = () => {
  const { user } = useAuth();
  const [intern, setIntern] = useState<Intern | null>(null);
  const [submissionUrl, setSubmissionUrl] = useState('');
  const [selectedMilestone, setSelectedMilestone] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submittedSuccess, setSubmittedSuccess] = useState(false);

  useEffect(() => {
    // Fetch logged in intern data or default to mock intern
    const loadIntern = async () => {
      const res = await internsApi.getInterns();
      if (res.interns.length > 0) {
        const current = res.interns.find(i => i.officialEmail.toLowerCase() === user?.email.toLowerCase()) || res.interns[0];
        setIntern(current);
        if (current.dailyTasks && current.dailyTasks.length > 0) {
          setSelectedMilestone(current.dailyTasks[0].title);
        }
      }
    };
    loadIntern();
  }, [user]);

  const handleSubmitMilestone = (e: React.FormEvent) => {
    e.preventDefault();
    if (!submissionUrl) return;

    setIsSubmitting(true);
    setTimeout(() => {
      setIsSubmitting(false);
      setSubmittedSuccess(true);
      setSubmissionUrl('');
      setTimeout(() => setSubmittedSuccess(false), 4000);
    }, 800);
  };

  return (
    <div className="space-y-8 pb-12">
      {/* Header */}
      <div>
        <div className="flex items-center gap-2 text-cyan-600 dark:text-cyan-400 font-semibold text-xs uppercase tracking-wider mb-1">
          <GraduationCap className="w-4 h-4" />
          <span>Intern Learning & Mentorship Portal</span>
        </div>
        <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Learning Curriculum & Submissions</h1>
        <p className="text-xs text-slate-500">Track assigned learning modules, submit weekly milestone code, and view mentor feedback</p>
      </div>

      {/* Profile Bar */}
      {intern && (
        <div className="p-6 bg-gradient-to-r from-slate-900 via-slate-800 to-cyan-950 text-white rounded-3xl shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-cyan-500 text-slate-950 font-extrabold text-xl flex items-center justify-center">
              {intern.firstName[0]}
              {intern.lastName[0]}
            </div>
            <div>
              <div className="text-lg font-extrabold">{intern.firstName} {intern.lastName}</div>
              <p className="text-xs text-slate-300">{intern.university} • {intern.department}</p>
              <div className="text-[11px] text-cyan-300 mt-1">Assigned Mentor: <strong>{intern.mentor}</strong></div>
            </div>
          </div>

          <div className="flex items-center gap-4 text-xs">
            <div className="bg-white/10 backdrop-blur-xs px-4 py-2.5 rounded-2xl border border-white/10">
              <span className="text-slate-400 block text-[10px] font-bold uppercase">Milestone Progress</span>
              <span className="font-extrabold text-cyan-400 text-base">{intern.performanceScore}% Score</span>
            </div>
            <div className="bg-white/10 backdrop-blur-xs px-4 py-2.5 rounded-2xl border border-white/10">
              <span className="text-slate-400 block text-[10px] font-bold uppercase">Cohort Term</span>
              <span className="font-bold text-white text-xs">{intern.joiningDate} to {intern.endDate}</span>
            </div>
          </div>
        </div>
      )}

      {/* Curriculum & Submissions */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        
        {/* Mentor Modules & Tasks */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-xs">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white">Assigned Learning Milestones</h3>
            <span className="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-cyan-100 dark:bg-cyan-950 text-cyan-700 dark:text-cyan-300">
              Mentor: {intern?.mentor || 'Ganesh Pal'}
            </span>
          </div>

          <div className="space-y-3">
            {intern?.dailyTasks && intern.dailyTasks.length > 0 ? (
              intern.dailyTasks.map((t) => (
                <div key={t.id} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{t.title}</span>
                    <StatusBadge status={t.status === 'Approved' ? 'Completed' : t.status} />
                  </div>
                  <div className="flex items-center justify-between text-[11px] text-slate-500">
                    <span>Score: {t.score ? `${t.score}/100` : 'Pending Review'}</span>
                    <span>{t.dueDate}</span>
                  </div>
                  {t.feedback && (
                    <div className="text-[11px] text-emerald-600 dark:text-emerald-400 font-semibold pt-1 border-t border-slate-200/60 dark:border-slate-800">
                      Mentor Note: "{t.feedback}"
                    </div>
                  )}
                </div>
              ))
            ) : (
              [
                { title: 'MongoDB Indexing & Aggregation Pipelines', status: 'Completed', score: '98/100', due: 'Submitted Oct 10' },
                { title: 'Spring Security Filter Chains & JWT Interceptors', status: 'In Progress', score: 'Pending Review', due: 'Due Oct 18' },
                { title: 'React 19 Server Components & Tailwind Architecture', status: 'Planning', score: '-', due: 'Due Oct 25' },
              ].map((m, idx) => (
                <div key={idx} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-800 space-y-1.5">
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-xs text-slate-900 dark:text-slate-100">{m.title}</span>
                    <StatusBadge status={m.status} />
                  </div>
                  <div className="flex items-center justify-between text-[11px] text-slate-500">
                    <span>Score: {m.score}</span>
                    <span>{m.due}</span>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Submit Work */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 space-y-4 shadow-xs">
          <div className="border-b border-slate-100 dark:border-slate-800 pb-3">
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white">Submit Milestone Assignment</h3>
            <p className="text-xs text-slate-500">Upload code repository links or pull request URLs for mentor evaluation</p>
          </div>

          {submittedSuccess && (
            <div className="p-3 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-2xl text-xs text-emerald-800 dark:text-emerald-200 font-bold flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-600" /> Milestone code successfully submitted to assigned mentor!
            </div>
          )}

          <form onSubmit={handleSubmitMilestone} className="space-y-4">
            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Select Milestone *</label>
              <select
                value={selectedMilestone}
                onChange={(e) => setSelectedMilestone(e.target.value)}
                className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-medium"
              >
                {intern?.dailyTasks?.map((t) => (
                  <option key={t.id} value={t.title}>
                    {t.title}
                  </option>
                )) || <option>Spring Security Filter Chains & JWT Interceptors</option>}
              </select>
            </div>

            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">GitHub PR or Repository URL *</label>
              <input
                type="url"
                required
                value={submissionUrl}
                onChange={(e) => setSubmissionUrl(e.target.value)}
                placeholder="https://github.com/techknife/backend/pull/42"
                className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-medium"
              />
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full py-3 bg-cyan-600 hover:bg-cyan-500 text-white font-bold text-xs rounded-xl transition-all shadow-md flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {isSubmitting ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                <>
                  <Send className="w-4 h-4" /> Submit to Mentor
                </>
              )}
            </button>
          </form>
        </div>

      </div>
    </div>
  );
};
