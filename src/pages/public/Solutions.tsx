import React from 'react';
import { Link } from 'react-router-dom';
import { Building2, Layers, ShieldCheck, Zap, BarChart3, Users, ArrowRight } from 'lucide-react';

export const SolutionsPage: React.FC = () => {
  const solutions = [
    {
      title: 'Enterprise Management Engine (TK-EMS)',
      category: 'Operations & HR',
      desc: 'Complete operational suite combining Employee Directories, GPS Attendance, Leave Approval Workflows, and Automated Payroll Slips in a single RBAC-guarded platform.',
      link: '/dashboard'
    },
    {
      title: 'Agile Project & Sprint Hub',
      category: 'Product & Engineering',
      desc: 'Kanban boards, resource planning, sprint velocity charts, repository integrations, and automated milestone tracking for high-velocity tech teams.',
      link: '/projects'
    },
    {
      title: 'Unified Customer & CRM Portal',
      category: 'Growth & Client Care',
      desc: 'Transparent client ticket desk, deal pipelines, milestone approvals, invoice tracking, and document vaults for B2B client relationships.',
      link: '/customer'
    },
    {
      title: 'Executive Analytics & Risk Radar',
      category: 'Leadership & BI',
      desc: 'Role-based C-level dashboards (CEO, CTO, CFO, CMO) with automated risk alerts, KPI forecasting, and real-time financial metrics.',
      link: '/reports'
    }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Enterprise Solutions
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Purpose-Built Digital Platforms & Solutions
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Pre-packaged, customizable enterprise software architectures designed to solve complex operational challenges.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {solutions.map((sol, idx) => (
          <div key={idx} className="p-8 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-4 hover:border-blue-500/50 transition-all">
            <span className="text-[10px] font-bold uppercase tracking-widest text-blue-400 bg-blue-500/10 px-2.5 py-1 rounded-md border border-blue-500/20">
              {sol.category}
            </span>
            <h2 className="text-xl font-bold text-white">{sol.title}</h2>
            <p className="text-xs text-slate-300 leading-relaxed">{sol.desc}</p>
            <Link
              to={sol.link}
              className="inline-flex items-center gap-1.5 text-xs font-bold text-blue-400 hover:underline pt-2"
            >
              Explore Solution Portal <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
};
