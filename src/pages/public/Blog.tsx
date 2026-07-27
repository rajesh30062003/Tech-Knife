import React from 'react';
import { Link } from 'react-router-dom';
import { BookOpen, Calendar, User, ArrowRight } from 'lucide-react';

export const BlogPage: React.FC = () => {
  const posts = [
    {
      title: 'Architecting High-Throughput Microservices with Spring Boot 3 & React 19',
      date: 'July 24, 2026',
      author: 'Lead Architect, Tech Knife Labs',
      snippet: 'Key strategies for state management, token rotation, and low-latency API proxying in enterprise environments.'
    },
    {
      title: 'Integrating Gemini LLM Agents into B2B Customer Portals Safely',
      date: 'July 18, 2026',
      author: 'AI Operations Director',
      snippet: 'A step-by-step guide to server-side API proxying, PII redaction, and grounding for automated client ticket support.'
    },
    {
      title: 'The Shift to Aspect-Oriented Audit Telemetry in SOC2 Compliance',
      date: 'July 10, 2026',
      author: 'Head of Cybersecurity',
      snippet: 'How Spring AOP annotations streamline compliance auditing without polluting core business logic.'
    }
  ];

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Engineering Insights
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Tech Knife Engineering Blog
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Deep technical articles, architectural patterns, and industry trends written by our senior engineers.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {posts.map((p, idx) => (
          <div key={idx} className="p-6 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-4 flex flex-col justify-between hover:border-blue-500/50 transition-all">
            <div className="space-y-3">
              <div className="flex items-center gap-3 text-[10px] text-slate-400">
                <span className="flex items-center gap-1"><Calendar className="w-3 h-3 text-blue-400" /> {p.date}</span>
              </div>
              <h2 className="text-base font-bold text-white hover:text-blue-400 transition-colors leading-snug">{p.title}</h2>
              <p className="text-xs text-slate-400 leading-relaxed">{p.snippet}</p>
            </div>

            <div className="pt-4 border-t border-slate-800 flex items-center justify-between">
              <span className="text-[10px] font-semibold text-slate-400">{p.author}</span>
              <span className="text-xs font-bold text-blue-400 flex items-center gap-1">Read <ArrowRight className="w-3.5 h-3.5" /></span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
