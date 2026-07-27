import React from 'react';
import { Link } from 'react-router-dom';
import { FileText, ArrowRight, CheckCircle2, TrendingUp } from 'lucide-react';

export const CaseStudiesPage: React.FC = () => {
  const caseStudies = [
    {
      title: 'Modernizing Core Banking Workflows with Microservices',
      industry: 'BFSI',
      summary: 'How Tech Knife refactored a legacy 15-year-old mainframe banking system into modern containerized REST APIs with zero transaction downtime.',
      results: [
        '99.999% SLA Uptime maintained during migration',
        '65% reduction in infrastructure compute costs',
        'Deployment frequency increased from monthly to 12x daily'
      ]
    },
    {
      title: 'Scaling AI-Powered Supply Chain Telemetry for Global Logistics',
      industry: 'Supply Chain & Manufacturing',
      summary: 'Integrating IoT stream processing with Gemini predictive AI to forecast container delay risks across 45 international shipping routes.',
      results: [
        'Saved $3.2M in annual container demurrage fees',
        '88% accuracy in 72-hour arrival window prediction',
        'Real-time dashboard accessible by 4,000+ logistics personnel'
      ]
    }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Technical Deep Dives
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          In-Depth Engineering Case Studies
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Comprehensive analysis of complex technical challenges, architecture decisions, and measurable enterprise results.
        </p>
      </div>

      <div className="space-y-8">
        {caseStudies.map((cs, idx) => (
          <div key={idx} className="p-8 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-6">
            <div className="flex items-center justify-between">
              <span className="px-3 py-1 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-full text-xs font-bold uppercase">
                {cs.industry}
              </span>
            </div>
            <h2 className="text-2xl font-bold text-white">{cs.title}</h2>
            <p className="text-xs text-slate-300 leading-relaxed max-w-3xl">{cs.summary}</p>

            <div className="space-y-2 pt-2">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">Measurable Outcomes</h4>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                {cs.results.map((r, ridx) => (
                  <div key={ridx} className="p-3 bg-slate-900 border border-slate-800 rounded-xl flex items-center gap-2 text-xs text-slate-200">
                    <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                    <span>{r}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
