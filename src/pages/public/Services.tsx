import React from 'react';
import { Link } from 'react-router-dom';
import { Code2, Server, Cpu, Shield, Database, Smartphone, Globe, Cloud, CheckCircle2, ArrowRight } from 'lucide-react';

export const ServicesPage: React.FC = () => {
  const services = [
    {
      icon: Code2,
      title: 'Custom Application Development',
      desc: 'Scalable web and enterprise applications engineered using React 19, Spring Boot, microservices, and modern REST/GraphQL APIs.',
      tags: ['React', 'Spring Boot', 'TypeScript', 'PostgreSQL', 'Microservices']
    },
    {
      icon: Cloud,
      title: 'Cloud Infrastructure & DevOps',
      desc: 'Architecting resilient multi-cloud environments on AWS, Azure, and Google Cloud with Kubernetes, Terraform, and automated CI/CD pipelines.',
      tags: ['AWS', 'Kubernetes', 'Docker', 'Terraform', 'CI/CD']
    },
    {
      icon: Cpu,
      title: 'AI & Data Analytics Solutions',
      desc: 'Leveraging Gemini LLM models, custom predictive engines, NLP automated workflows, and data pipelines for strategic decision making.',
      tags: ['Gemini API', 'Predictive AI', 'Big Data', 'Python', 'MLOps']
    },
    {
      icon: Smartphone,
      title: 'Mobile App Engineering',
      desc: 'Native iOS and Android platforms, cross-platform React Native solutions, and progressive web apps with real-time offline sync.',
      tags: ['React Native', 'iOS', 'Android', 'PWA', 'GraphQL']
    },
    {
      icon: Shield,
      title: 'Cybersecurity & RBAC Compliance',
      desc: 'Comprehensive security audits, zero-trust RBAC access control systems, SOC2 compliance, and automated vulnerability remediation.',
      tags: ['OAuth 2.0', 'JWT', 'RBAC', 'SOC2', 'Penetration Testing']
    },
    {
      icon: Database,
      title: 'Legacy Modernization & Migration',
      desc: 'Seamlessly refactoring monolithic legacy codebases into cloud-native microservices with zero downtime and guaranteed data integrity.',
      tags: ['Cloud Migration', 'Database Refactoring', 'API Gateways']
    }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Enterprise Services
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Comprehensive Software & IT Engineering Services
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          From full-stack web platforms to complex AI integrations, Tech Knife delivers end-to-end technical excellence tailored to your operational goals.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {services.map((s, idx) => {
          const IconComponent = s.icon;
          return (
            <div key={idx} className="p-6 bg-slate-800/40 border border-slate-800 rounded-2xl hover:border-blue-500/50 transition-all flex flex-col justify-between space-y-4">
              <div className="space-y-3">
                <div className="p-3 bg-blue-600/10 text-blue-400 rounded-xl w-fit">
                  <IconComponent className="w-6 h-6" />
                </div>
                <h3 className="text-lg font-bold text-white">{s.title}</h3>
                <p className="text-xs text-slate-400 leading-relaxed">{s.desc}</p>
              </div>

              <div className="space-y-3 pt-2">
                <div className="flex flex-wrap gap-1.5">
                  {s.tags.map((t, tidx) => (
                    <span key={tidx} className="px-2 py-0.5 bg-slate-900 border border-slate-700/60 text-[10px] font-semibold text-slate-300 rounded-md">
                      {t}
                    </span>
                  ))}
                </div>
                <Link
                  to="/contact"
                  className="inline-flex items-center gap-1 text-xs font-bold text-blue-400 hover:underline pt-1"
                >
                  Request Consultation <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          );
        })}
      </div>

      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-8 flex flex-col md:flex-row items-center justify-between gap-6">
        <div>
          <h2 className="text-xl font-bold text-white">Need a Custom Technical Architecture?</h2>
          <p className="text-xs text-slate-400 mt-1">Our lead solutions architects are available for technical discovery calls.</p>
        </div>
        <Link
          to="/contact"
          className="px-6 py-3 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs rounded-xl transition-all shadow-lg shadow-blue-600/30 whitespace-nowrap"
        >
          Book Discovery Session
        </Link>
      </div>
    </div>
  );
};
