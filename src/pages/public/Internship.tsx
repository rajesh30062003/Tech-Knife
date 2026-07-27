import React from 'react';
import { Link } from 'react-router-dom';
import { GraduationCap, Award, BookOpen, CheckCircle2, ArrowRight, Sparkles, Users } from 'lucide-react';

export const InternshipPage: React.FC = () => {
  const tracks = [
    { title: 'Full-Stack Software Engineering', duration: '6 Months', stipend: 'Competitive Stipend + Full-Time Conversion Offer' },
    { title: 'Cloud DevOps & Infrastructure', duration: '6 Months', stipend: 'Competitive Stipend + Mentorship' },
    { title: 'AI & Machine Learning Engineering', duration: '6 Months', stipend: 'Competitive Stipend + Research Grant' },
    { title: 'UI/UX Design & Product Strategy', duration: '3 - 6 Months', stipend: 'Competitive Stipend' }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-cyan-500/10 text-cyan-400 border border-cyan-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Tech Knife Academy
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Global Technology Internship Program
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Accelerate your technology career with hands-on enterprise software engineering, direct senior mentorship, and full-time conversion opportunities.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {tracks.map((t, idx) => (
          <div key={idx} className="p-8 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-4 hover:border-cyan-500/50 transition-all flex flex-col justify-between">
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-[10px] font-bold text-cyan-400 uppercase tracking-widest">{t.duration}</span>
                <span className="p-2 bg-cyan-500/10 text-cyan-400 rounded-xl">
                  <GraduationCap className="w-4 h-4" />
                </span>
              </div>
              <h2 className="text-xl font-bold text-white">{t.title}</h2>
              <p className="text-xs text-slate-300">{t.stipend}</p>
            </div>

            <Link
              to="/register"
              className="inline-flex items-center gap-1.5 text-xs font-bold text-cyan-400 hover:underline pt-2"
            >
              Apply for Internship Cohort <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
};
