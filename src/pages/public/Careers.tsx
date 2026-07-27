import React from 'react';
import { Link } from 'react-router-dom';
import { Briefcase, MapPin, Clock, ArrowRight, Building2, CheckCircle2 } from 'lucide-react';

export const CareersPage: React.FC = () => {
  const jobs = [
    { title: 'Senior Staff Full-Stack Architect', dept: 'Engineering', location: 'San Francisco, CA / Remote', type: 'Full-Time' },
    { title: 'Lead Cloud DevOps Engineer (AWS/K8s)', dept: 'Infrastructure', location: 'Austin, TX / Remote', type: 'Full-Time' },
    { title: 'Senior Gemini AI Integration Engineer', dept: 'AI Labs', location: 'New York, NY / Remote', type: 'Full-Time' },
    { title: 'Enterprise Customer Success Manager', dept: 'Client Operations', location: 'London, UK / Hybrid', type: 'Full-Time' }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Join Tech Knife
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Build the Future of Enterprise Technology
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          We are seeking world-class software engineers, architects, and product managers to build high-impact global software.
        </p>
      </div>

      <div className="space-y-4">
        {jobs.map((j, idx) => (
          <div key={idx} className="p-6 bg-slate-800/40 border border-slate-800 rounded-2xl flex flex-col md:flex-row items-start md:items-center justify-between gap-4 hover:border-blue-500/50 transition-all">
            <div className="space-y-1">
              <span className="text-[10px] font-bold text-blue-400 uppercase tracking-widest">{j.dept}</span>
              <h3 className="text-lg font-bold text-white">{j.title}</h3>
              <div className="flex items-center gap-4 text-xs text-slate-400 pt-1">
                <span className="flex items-center gap-1"><MapPin className="w-3.5 h-3.5" /> {j.location}</span>
                <span className="flex items-center gap-1"><Clock className="w-3.5 h-3.5" /> {j.type}</span>
              </div>
            </div>
            <Link
              to="/contact"
              className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs rounded-xl transition-all shadow-md shadow-blue-600/30 whitespace-nowrap"
            >
              Apply Position
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
};
