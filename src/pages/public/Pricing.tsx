import React from 'react';
import { Link } from 'react-router-dom';
import { Check, Shield, Zap, Building2, ArrowRight } from 'lucide-react';

export const PricingPage: React.FC = () => {
  const plans = [
    {
      name: 'Starter Team',
      price: '$999',
      period: '/ month',
      desc: 'Ideal for growing tech teams needing core HR, project sprint management, and basic CRM.',
      features: [
        'Up to 50 Employee Seats',
        'Kanban Project Sprint Boards',
        'GPS Attendance & Leave Approvals',
        'Standard Email & Ticket Support'
      ],
      cta: 'Start 14-Day Trial',
      highlighted: false
    },
    {
      name: 'Enterprise Platform',
      price: '$2,499',
      period: '/ month',
      desc: 'Complete unified engine with Automated Payroll, Customer Portal, and RBAC Audit Logging.',
      features: [
        'Up to 250 Employee Seats',
        'Full Payroll & Auto Tax Slips',
        'Customer Portal & Ticket Desk',
        'AOP Security & Audit Telemetry',
        'Gemini AI Assistant Integration',
        '24/7 Dedicated Support SLA'
      ],
      cta: 'Launch Enterprise Workspace',
      highlighted: true
    },
    {
      name: 'Custom Cloud / On-Prem',
      price: 'Custom',
      period: 'Quote',
      desc: 'Dedicated private cloud deployments, air-gapped instances, and custom SLA agreements.',
      features: [
        'Unlimited Employee Seats',
        'Air-Gapped Private Cloud Hosting',
        'Custom SSO / SAML / OAuth 2.0',
        'Custom Database Connectors',
        'Dedicated Solutions Architect'
      ],
      cta: 'Contact Sales',
      highlighted: false
    }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Transparent Pricing
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Flexible Plans Tailored to Your Growth
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Clear, predictable enterprise pricing with no hidden fees or unexpected bandwidth surcharges.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {plans.map((p, idx) => (
          <div
            key={idx}
            className={`p-8 rounded-3xl border flex flex-col justify-between space-y-6 relative transition-all ${
              p.highlighted
                ? 'bg-gradient-to-b from-blue-950/60 to-slate-900 border-blue-500 shadow-2xl shadow-blue-600/20 scale-105'
                : 'bg-slate-800/40 border-slate-800'
            }`}
          >
            {p.highlighted && (
              <span className="absolute -top-3 left-1/2 -translate-x-1/2 px-3 py-1 bg-blue-600 text-white text-[10px] font-bold uppercase rounded-full shadow-md">
                Most Popular
              </span>
            )}

            <div className="space-y-4">
              <h3 className="text-xl font-bold text-white">{p.name}</h3>
              <div className="flex items-baseline gap-1">
                <span className="text-4xl font-black text-white">{p.price}</span>
                <span className="text-xs text-slate-400 font-medium">{p.period}</span>
              </div>
              <p className="text-xs text-slate-400 leading-relaxed">{p.desc}</p>

              <div className="pt-4 border-t border-slate-800 space-y-2.5">
                {p.features.map((f, fidx) => (
                  <div key={fidx} className="flex items-center gap-2 text-xs text-slate-300">
                    <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                    <span>{f}</span>
                  </div>
                ))}
              </div>
            </div>

            <Link
              to="/contact"
              className={`w-full py-3 text-center text-xs font-bold rounded-xl transition-all shadow-md ${
                p.highlighted
                  ? 'bg-blue-600 hover:bg-blue-500 text-white shadow-blue-600/30'
                  : 'bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700'
              }`}
            >
              {p.cta}
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
};
