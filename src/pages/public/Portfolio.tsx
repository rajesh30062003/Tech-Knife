import React from 'react';
import { Link } from 'react-router-dom';
import { FolderKanban, ArrowRight, ExternalLink, Sparkles } from 'lucide-react';

export const PortfolioPage: React.FC = () => {
  const projects = [
    {
      title: 'Global Fintech Payment Switch Platform',
      client: 'Apex Financial Partners',
      tech: ['Spring Boot 3', 'React 19', 'PostgreSQL', 'Redis'],
      metric: 'Processed 12M+ daily transactions at 99.999% uptime.'
    },
    {
      title: 'AI-Powered Telehealth Operations Suite',
      client: 'CareGrid Health',
      tech: ['React Native', 'Gemini API', 'Kubernetes', 'WebSockets'],
      metric: 'Reduced patient wait times by 42% across 80 hospitals.'
    },
    {
      title: 'Omnichannel E-Commerce Inventory Engine',
      client: 'UrbanStyle Global',
      tech: ['Node.js', 'React 19', 'Kafka', 'MongoDB Atlas'],
      metric: 'Handled Black Friday peak load of 85,000 requests/sec.'
    },
    {
      title: 'Automated HR & Payroll Governance System',
      client: 'Tech Knife Global Internal',
      tech: ['React 19', 'TailwindCSS', 'Spring Security', 'AOP'],
      metric: 'Automated monthly payroll processing for 1,200+ employees.'
    }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Portfolio & Showcase
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Delivered Enterprise Success Stories
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Highlighting mission-critical software solutions engineered and deployed by Tech Knife.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {projects.map((p, idx) => (
          <div key={idx} className="p-8 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-4 hover:border-blue-500/50 transition-all flex flex-col justify-between">
            <div className="space-y-3">
              <span className="text-xs font-bold text-blue-400 uppercase tracking-wider">{p.client}</span>
              <h2 className="text-xl font-bold text-white">{p.title}</h2>
              <div className="p-3 bg-blue-950/40 border border-blue-800/40 rounded-xl text-blue-300 text-xs font-semibold">
                Impact: {p.metric}
              </div>
            </div>

            <div className="space-y-3 pt-4 border-t border-slate-800">
              <div className="flex flex-wrap gap-1.5">
                {p.tech.map((t, tidx) => (
                  <span key={tidx} className="px-2 py-0.5 bg-slate-900 border border-slate-700/60 text-[10px] font-semibold text-slate-300 rounded-md">
                    {t}
                  </span>
                ))}
              </div>
              <Link to="/case-studies" className="inline-flex items-center gap-1 text-xs font-bold text-blue-400 hover:underline">
                Read Full Case Study <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
