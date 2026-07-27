import React from 'react';
import { Link } from 'react-router-dom';
import { Building2, Stethoscope, Landmark, ShoppingCart, ShieldCheck, Factory, ArrowRight } from 'lucide-react';

export const IndustriesPage: React.FC = () => {
  const industries = [
    {
      icon: Landmark,
      title: 'Banking & Financial Services (BFSI)',
      desc: 'High-security transaction platforms, real-time fraud monitoring, SOC2 compliance, and automated auditing for global financial institutions.'
    },
    {
      icon: Stethoscope,
      title: 'Healthcare & Life Sciences',
      desc: 'HIPAA-compliant patient portals, telemetry data pipelines, secure medical record storage, and clinical workflow management.'
    },
    {
      icon: ShoppingCart,
      title: 'E-Commerce & Retail Tech',
      desc: 'High-concurrency microservice backends, real-time inventory management engines, payment gateways, and omnichannel customer portals.'
    },
    {
      icon: Factory,
      title: 'Manufacturing & Logistics',
      desc: 'Supply chain tracking systems, IoT telemetry dashboards, automated asset maintenance schedules, and ERP integrations.'
    },
    {
      icon: Building2,
      title: 'SaaS & High-Growth Tech',
      desc: 'Multi-tenant cloud architectures, usage-based billing integration, developer APIs, and automated CI/CD DevOps infrastructure.'
    },
    {
      icon: ShieldCheck,
      title: 'Public Sector & Defense',
      desc: 'Air-gapped deployment readiness, FedRAMP compliant security standards, zero-trust RBAC access controls, and immutable audit logs.'
    }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Industry Vertical Expertise
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Tailored Engineering for Industry Leaders
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Deep domain experience and specialized security compliance frameworks across major global sectors.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {industries.map((ind, idx) => {
          const IconComponent = ind.icon;
          return (
            <div key={idx} className="p-6 bg-slate-800/40 border border-slate-800 rounded-2xl hover:border-blue-500/50 transition-all space-y-3">
              <div className="p-3 bg-blue-500/10 text-blue-400 rounded-xl w-fit">
                <IconComponent className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-white">{ind.title}</h3>
              <p className="text-xs text-slate-400 leading-relaxed">{ind.desc}</p>
              <Link to="/contact" className="inline-flex items-center gap-1 text-xs font-bold text-blue-400 hover:underline pt-2">
                View Industry Solutions <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>
          );
        })}
      </div>
    </div>
  );
};
