import React, { useState } from 'react';
import { ChevronDown, HelpCircle, Shield, Sparkles } from 'lucide-react';

export const FaqPage: React.FC = () => {
  const faqs = [
    {
      q: 'What is Tech Knife and what services do you provide?',
      a: 'Tech Knife is a premier global technology company specializing in custom software engineering, cloud infrastructure DevOps, AI automation engines, and unified enterprise management solutions.'
    },
    {
      q: 'How does the Tech Knife platform enforce RBAC security and role isolation?',
      a: 'Our platform uses Spring Security 6 with JWT token rotation, role guards across all REST endpoints, and Aspect-Oriented Programming (AOP) audit logging for full SOC2 compliance.'
    },
    {
      q: 'Can Tech Knife integrate with our existing GitHub repositories or cloud infrastructure?',
      a: 'Yes, Tech Knife provides native integrations with GitHub, GitLab, AWS, Azure, Google Cloud, and major identity providers via OAuth 2.0 / SAML.'
    },
    {
      q: 'What support options and SLAs are available for enterprise customers?',
      a: 'We offer 24/7 dedicated support with 15-minute response time SLAs for critical production incidents, assigned Solutions Architects, and custom training sessions.'
    }
  ];

  const [openIdx, setOpenIdx] = useState<number | null>(0);

  return (
    <div className="py-12 px-6 max-w-4xl mx-auto space-y-12">
      <div className="text-center space-y-3">
        <span className="px-3 py-1 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Frequently Asked Questions
        </span>
        <h1 className="text-3xl sm:text-4xl font-black text-white">
          Everything You Need to Know About Tech Knife
        </h1>
        <p className="text-slate-400 text-xs sm:text-sm">
          Have questions? We have answers.
        </p>
      </div>

      <div className="space-y-4">
        {faqs.map((faq, idx) => {
          const isOpen = openIdx === idx;
          return (
            <div key={idx} className="bg-slate-800/40 border border-slate-800 rounded-2xl overflow-hidden transition-all">
              <button
                onClick={() => setOpenIdx(isOpen ? null : idx)}
                className="w-full px-6 py-4 flex items-center justify-between text-left text-sm font-bold text-white hover:bg-slate-800/60"
              >
                <span>{faq.q}</span>
                <ChevronDown className={`w-4 h-4 text-slate-400 transition-transform ${isOpen ? 'rotate-180 text-blue-400' : ''}`} />
              </button>
              {isOpen && (
                <div className="px-6 pb-5 pt-1 text-xs text-slate-300 leading-relaxed border-t border-slate-800/60 bg-slate-900/50">
                  {faq.a}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
