import React from 'react';
import { Link } from 'react-router-dom';
import { 
  Shield, Users, ArrowRight, CheckCircle2, 
  BarChart3, Zap, Globe2, Building2, Lock, Sparkles,
  Code2, Cpu, Server, Layers, Award, Star, ArrowUpRight, Check
} from 'lucide-react';
import { Logo } from '../../components/common/Logo';

export const PublicHome: React.FC = () => {
  return (
    <div className="space-y-24 pb-20 pt-8">
      
      {/* Hero Section */}
      <section className="relative px-6 max-w-7xl mx-auto text-center space-y-8">
        
        {/* Glow backdrop */}
        <div className="absolute -top-24 left-1/2 -translate-x-1/2 w-[600px] h-[300px] bg-blue-600/15 blur-[120px] rounded-full pointer-events-none" />

        <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-blue-500/10 text-blue-400 border border-blue-500/20 text-xs font-semibold animate-pulse">
          <Sparkles className="w-3.5 h-3.5 text-blue-400" />
          <span>Next-Generation Enterprise IT Services & Solutions Platform</span>
        </div>

        <h1 className="text-4xl sm:text-6xl font-black text-white tracking-tight leading-tight max-w-4xl mx-auto">
          Cutting-Edge Software Engineering & <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-indigo-400 to-cyan-300">Digital Transformation</span>
        </h1>

        <p className="text-slate-400 text-base sm:text-lg max-w-2xl mx-auto leading-relaxed">
          Tech Knife empowers global leaders with custom cloud architectures, AI automation, high-performance software engineering, and unified operational portals.
        </p>

        <div className="flex flex-wrap items-center justify-center gap-4 pt-2">
          <Link
            to="/dashboard"
            className="inline-flex items-center gap-2 px-6 py-3.5 text-sm font-bold text-white bg-blue-600 hover:bg-blue-500 rounded-xl transition-all shadow-xl shadow-blue-600/30"
          >
            <span>Launch Workspace Portal</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
          <Link
            to="/contact"
            className="inline-flex items-center gap-2 px-6 py-3.5 text-sm font-bold text-slate-300 bg-slate-800/80 hover:bg-slate-800 rounded-xl transition-all border border-slate-700/80"
          >
            <span>Schedule Executive Consultation</span>
          </Link>
        </div>

        {/* Hero Interactive Live Platform Mockup */}
        <div className="pt-8 max-w-5xl mx-auto">
          <div className="p-3 bg-slate-800/60 rounded-3xl border border-slate-700/80 shadow-2xl backdrop-blur-xl">
            <div className="bg-slate-950 rounded-2xl p-6 border border-slate-800 text-left space-y-6">
              <div className="flex items-center justify-between border-b border-slate-800 pb-4">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 rounded-full bg-rose-500" />
                  <div className="w-3 h-3 rounded-full bg-amber-500" />
                  <div className="w-3 h-3 rounded-full bg-emerald-500" />
                  <span className="text-xs text-slate-500 ml-2 font-mono">portal.techknife.com/dashboard</span>
                </div>
                <div className="flex items-center gap-2 text-xs text-blue-400 font-semibold bg-blue-500/10 px-3 py-1 rounded-lg border border-blue-500/20">
                  <Shield className="w-3.5 h-3.5" />
                  <span>Enterprise Security Token Guard</span>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-slate-900/90 p-4 rounded-xl border border-slate-800">
                  <span className="text-xs text-slate-400">Quarterly Tech Revenue</span>
                  <p className="text-2xl font-bold text-white mt-1">$4.28M</p>
                  <span className="text-[11px] text-emerald-400 font-semibold">↑ +24.8% Year-over-Year</span>
                </div>
                <div className="bg-slate-900/90 p-4 rounded-xl border border-slate-800">
                  <span className="text-xs text-slate-400">Sprint Delivery Rate</span>
                  <p className="text-2xl font-bold text-white mt-1">98.6%</p>
                  <span className="text-[11px] text-blue-400 font-semibold">42 Active Client Sprints</span>
                </div>
                <div className="bg-slate-900/90 p-4 rounded-xl border border-slate-800">
                  <span className="text-xs text-slate-400">Client CSAT Score</span>
                  <p className="text-2xl font-bold text-white mt-1">4.96 / 5.0</p>
                  <span className="text-[11px] text-emerald-400 font-semibold">1,240 Verified Reviews</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Corporate Capabilities Grid */}
      <section className="px-6 max-w-7xl mx-auto space-y-12">
        <div className="text-center space-y-3">
          <h2 className="text-3xl font-black text-white">
            Core Technological Pillars
          </h2>
          <p className="text-slate-400 text-sm max-w-xl mx-auto">
            Architected to serve Fortune 500 enterprises, high-growth tech companies, and public institutions.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="p-6 bg-slate-800/40 border border-slate-800 rounded-2xl space-y-3 hover:border-blue-500/50 transition-all group">
            <div className="p-3 bg-blue-500/10 text-blue-400 rounded-xl w-fit group-hover:bg-blue-600 group-hover:text-white transition-colors">
              <Code2 className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-bold text-white">Custom Engineering</h3>
            <p className="text-xs text-slate-400 leading-relaxed">
              Full-stack cloud applications, high-throughput microservices, and specialized web/mobile products built on modern React and Spring Boot architectures.
            </p>
            <Link to="/services" className="inline-flex items-center gap-1 text-xs text-blue-400 font-semibold hover:underline pt-2">
              Explore Engineering <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          <div className="p-6 bg-slate-800/40 border border-slate-800 rounded-2xl space-y-3 hover:border-blue-500/50 transition-all group">
            <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-xl w-fit group-hover:bg-indigo-600 group-hover:text-white transition-colors">
              <Cpu className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-bold text-white">AI & Automation Engine</h3>
            <p className="text-xs text-slate-400 leading-relaxed">
              Predictive analytics, intelligent OCR processing, workflow automations, and LLM agent integration tailored to enterprise data workflows.
            </p>
            <Link to="/solutions" className="inline-flex items-center gap-1 text-xs text-indigo-400 font-semibold hover:underline pt-2">
              Discover AI Solutions <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          <div className="p-6 bg-slate-800/40 border border-slate-800 rounded-2xl space-y-3 hover:border-blue-500/50 transition-all group">
            <div className="p-3 bg-cyan-500/10 text-cyan-400 rounded-xl w-fit group-hover:bg-cyan-600 group-hover:text-white transition-colors">
              <Server className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-bold text-white">Cloud Infrastructure & DevOps</h3>
            <p className="text-xs text-slate-400 leading-relaxed">
              Multi-cloud AWS, Azure, GCP infrastructure management, CI/CD automated deployment pipelines, Kubernetes orchestration, and zero-trust security.
            </p>
            <Link to="/services" className="inline-flex items-center gap-1 text-xs text-cyan-400 font-semibold hover:underline pt-2">
              View Cloud Services <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>
      </section>

      {/* Public Pages Quick Directory Grid */}
      <section className="px-6 max-w-7xl mx-auto space-y-8 bg-slate-900/60 p-8 rounded-3xl border border-slate-800">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 border-b border-slate-800 pb-6">
          <div>
            <span className="text-xs font-bold text-blue-400 uppercase tracking-widest">Tech Knife Directory</span>
            <h2 className="text-2xl font-bold text-white mt-1">Explore Public Portals & Offerings</h2>
          </div>
          <Link to="/about" className="text-xs font-bold text-blue-400 hover:underline flex items-center gap-1">
            Learn About Tech Knife <ArrowUpRight className="w-4 h-4" />
          </Link>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
          {[
            { label: 'About Us', path: '/about' },
            { label: 'IT Services', path: '/services' },
            { label: 'Solutions', path: '/solutions' },
            { label: 'Products', path: '/products' },
            { label: 'Industries', path: '/industries' },
            { label: 'Client Portfolio', path: '/portfolio' },
            { label: 'Case Studies', path: '/case-studies' },
            { label: 'Testimonials', path: '/testimonials' },
            { label: 'Internships', path: '/internship' },
            { label: 'Careers', path: '/careers' },
            { label: 'Tech Blog', path: '/blog' },
            { label: 'Pricing Plans', path: '/pricing' },
          ].map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className="p-3 bg-slate-800/50 hover:bg-slate-800 border border-slate-800 hover:border-blue-500/40 rounded-xl text-center text-xs font-semibold text-slate-200 hover:text-white transition-all flex items-center justify-between"
            >
              <span>{item.label}</span>
              <ArrowUpRight className="w-3.5 h-3.5 text-slate-500" />
            </Link>
          ))}
        </div>
      </section>

      {/* Security & Compliance Banner */}
      <section className="px-6 max-w-7xl mx-auto">
        <div className="bg-gradient-to-r from-blue-950/80 via-slate-900 to-slate-950 p-8 sm:p-12 rounded-3xl border border-blue-800/40 flex flex-col md:flex-row items-center justify-between gap-8">
          <div className="space-y-3 max-w-xl">
            <div className="flex items-center gap-2 text-blue-400 font-bold text-xs uppercase tracking-wider">
              <Lock className="w-4 h-4" />
              <span>SOC2 Type II & ISO 27001 Certified Security</span>
            </div>
            <h3 className="text-2xl sm:text-3xl font-black text-white">
              Enterprise Role-Based Access & Audit Logging
            </h3>
            <p className="text-xs text-slate-300 leading-relaxed">
              Every system operation is tracked via Aspect-Oriented Audit Logging. Real-time JWT token verification, Spring Security RBAC controls, and encrypted data vaults.
            </p>
          </div>
          <Link
            to="/login"
            className="px-6 py-3.5 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs rounded-xl transition-all shadow-lg shadow-blue-600/30 whitespace-nowrap"
          >
            Access Security Sandbox
          </Link>
        </div>
      </section>

    </div>
  );
};
