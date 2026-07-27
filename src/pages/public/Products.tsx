import React from 'react';
import { Link } from 'react-router-dom';
import { Package, Shield, Sparkles, ArrowRight, CheckCircle2 } from 'lucide-react';

export const ProductsPage: React.FC = () => {
  const products = [
    {
      name: 'Tech Knife Platform Engine',
      tagline: 'Core Enterprise Infrastructure',
      desc: 'An all-in-one software ecosystem managing company organization, payroll, CRM, projects, and security governance.',
      status: 'Active Release v2.4'
    },
    {
      name: 'TK AI Intelligence Gateway',
      tagline: 'Enterprise Gemini Integration',
      desc: 'Secure API middleware proxying LLM prompts, agent reasoning, and doc synthesis while masking sensitive PII data.',
      status: 'GA Enterprise'
    },
    {
      name: 'VaultSecure Storage Manager',
      tagline: 'Cloud Document Vault',
      desc: 'Encrypted document store with granular RBAC permissions, version control, and automated compliance retention rules.',
      status: 'GA Enterprise'
    },
    {
      name: 'AOP Audit Log Engine',
      tagline: 'Compliance & Telemetry',
      desc: 'Aspect-oriented security event logger capturing immutable audit logs for enterprise SOC2 compliance.',
      status: 'GA Enterprise'
    }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-cyan-500/10 text-cyan-400 border border-cyan-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Product Ecosystem
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Proprietary Tech Knife Software Products
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Battle-tested software products created by Tech Knife engineering labs for enterprise speed and security.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {products.map((p, idx) => (
          <div key={idx} className="p-8 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-4 hover:border-cyan-500/50 transition-all">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-cyan-400">{p.tagline}</span>
              <span className="px-2 py-0.5 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-[10px] font-bold rounded-full">
                {p.status}
              </span>
            </div>
            <h2 className="text-xl font-bold text-white">{p.name}</h2>
            <p className="text-xs text-slate-300 leading-relaxed">{p.desc}</p>
            <Link
              to="/dashboard"
              className="inline-flex items-center gap-1.5 text-xs font-bold text-cyan-400 hover:underline pt-2"
            >
              Access Product Instance <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
};
