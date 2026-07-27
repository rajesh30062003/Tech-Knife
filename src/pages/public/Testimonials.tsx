import React from 'react';
import { Star, Quote, Building2 } from 'lucide-react';

export const TestimonialsPage: React.FC = () => {
  const reviews = [
    {
      name: 'Sarah Jenkins',
      role: 'VP of Engineering',
      company: 'Apex Global Financial',
      content: 'Tech Knife delivered our cloud payment gateway ahead of schedule. Their attention to SOC2 security compliance and RBAC architecture was best-in-class.',
      rating: 5
    },
    {
      name: 'Dr. Michael Vance',
      role: 'Chief Technology Officer',
      company: 'CareGrid Health System',
      content: 'The integrated customer and project management platform built by Tech Knife gave our executive team real-time visibility across 80 hospital deployments.',
      rating: 5
    },
    {
      name: 'David K. Lawson',
      role: 'Director of IT Operations',
      company: 'UrbanStyle Retail',
      content: 'Tech Knife engineers feel like a true extension of our internal team. Their Sprint velocity and code quality are unmatched.',
      rating: 5
    }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-amber-500/10 text-amber-400 border border-amber-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Client Endorsements
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Trusted by Industry Leaders Worldwide
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Read what C-level executives and engineering leaders say about partnering with Tech Knife.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {reviews.map((r, idx) => (
          <div key={idx} className="p-8 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-4 flex flex-col justify-between">
            <div className="space-y-3">
              <div className="flex items-center gap-1 text-amber-400">
                {[...Array(r.rating)].map((_, i) => (
                  <Star key={i} className="w-4 h-4 fill-amber-400" />
                ))}
              </div>
              <p className="text-xs text-slate-300 italic leading-relaxed">"{r.content}"</p>
            </div>

            <div className="pt-4 border-t border-slate-800 space-y-0.5">
              <p className="text-sm font-bold text-white">{r.name}</p>
              <p className="text-xs text-blue-400 font-medium">{r.role}</p>
              <p className="text-[10px] text-slate-500">{r.company}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
