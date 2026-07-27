import React from 'react';
import { Link } from 'react-router-dom';
import { Shield, Award, Users, Globe, CheckCircle2, ArrowRight, Target, Sparkles, Cpu, Code2 } from 'lucide-react';
import { Logo } from '../../components/common/Logo';

export const AboutPage: React.FC = () => {
  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      {/* Header */}
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-500/10 text-blue-400 border border-blue-500/20 text-xs font-semibold">
          <Sparkles className="w-3.5 h-3.5" />
          <span>About Tech Knife</span>
        </div>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Precision Technology Engineering for the Global Enterprise
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Founded on the principle of surgical technological precision, Tech Knife builds mission-critical software, cloud infrastructure, AI automation tools, and integrated enterprise portals.
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { label: 'Global Clients', value: '250+' },
          { label: 'Engineers & Consultants', value: '1,200+' },
          { label: 'Successful Deployments', value: '99.98%' },
          { label: 'Countries Active', value: '18' },
        ].map((stat, idx) => (
          <div key={idx} className="p-6 bg-slate-800/40 border border-slate-800 rounded-2xl text-center">
            <p className="text-3xl sm:text-4xl font-black text-blue-400">{stat.value}</p>
            <p className="text-xs text-slate-400 mt-1 font-medium">{stat.label}</p>
          </div>
        ))}
      </div>

      {/* Mission & Vision */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="p-8 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-4">
          <div className="p-3 bg-blue-500/10 text-blue-400 rounded-2xl w-fit">
            <Target className="w-6 h-6" />
          </div>
          <h2 className="text-xl font-bold text-white">Our Mission</h2>
          <p className="text-xs text-slate-300 leading-relaxed">
            To equip organizations with razor-sharp software solutions and unified management platforms that remove operational friction, amplify velocity, and unlock unprecedented growth.
          </p>
        </div>

        <div className="p-8 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-4">
          <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-2xl w-fit">
            <Award className="w-6 h-6" />
          </div>
          <h2 className="text-xl font-bold text-white">Our Vision</h2>
          <p className="text-xs text-slate-300 leading-relaxed">
            To become the premier global benchmark in software engineering excellence, recognized for combining human ingenuity with AI-driven platform capabilities.
          </p>
        </div>
      </div>

      {/* Leadership Values */}
      <div className="space-y-6">
        <h2 className="text-2xl font-bold text-white text-center">Core Engineering Values</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[
            { title: 'Surgical Precision', desc: 'Zero compromise on code quality, security standards, or architecture design.' },
            { title: 'Transparent Governance', desc: 'Real-time client portals, sprint velocity metrics, and full audit accountability.' },
            { title: 'Continuous Innovation', desc: 'Rapid adoption of cloud native paradigms, microservices, and AI models.' },
          ].map((item, idx) => (
            <div key={idx} className="p-6 bg-slate-900 border border-slate-800 rounded-2xl space-y-2">
              <div className="w-8 h-8 rounded-full bg-blue-600/20 text-blue-400 flex items-center justify-center font-bold text-xs">
                0{idx + 1}
              </div>
              <h3 className="text-base font-bold text-white">{item.title}</h3>
              <p className="text-xs text-slate-400">{item.desc}</p>
            </div>
          ))}
        </div>
      </div>

      {/* CTA */}
      <div className="p-8 bg-gradient-to-r from-blue-900 to-indigo-900 rounded-3xl text-center space-y-4">
        <h2 className="text-2xl font-bold text-white">Ready to Elevate Your Tech Infrastructure?</h2>
        <p className="text-xs text-blue-200 max-w-xl mx-auto">
          Connect with Tech Knife enterprise architects today to review your project roadmap.
        </p>
        <Link
          to="/contact"
          className="inline-flex items-center gap-2 px-6 py-3 bg-white text-slate-900 font-bold text-xs rounded-xl hover:bg-slate-100 transition-colors"
        >
          <span>Contact Us Today</span>
          <ArrowRight className="w-4 h-4" />
        </Link>
      </div>
    </div>
  );
};
