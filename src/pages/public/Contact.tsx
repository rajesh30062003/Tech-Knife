import React, { useState } from 'react';
import { Mail, Phone, MapPin, Send, CheckCircle2, Globe, Sparkles } from 'lucide-react';

export const ContactPage: React.FC = () => {
  const [submitted, setSubmitted] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    company: '',
    service: 'General Inquiry',
    message: ''
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
  };

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      <div className="text-center space-y-4 max-w-3xl mx-auto">
        <span className="px-3 py-1 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-full text-xs font-bold uppercase tracking-wider">
          Get In Touch
        </span>
        <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight">
          Connect with Tech Knife Experts
        </h1>
        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
          Whether you need a custom enterprise solution, platform demo, or technical consultation, our team is ready to assist.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
        {/* Contact Information Cards */}
        <div className="space-y-6">
          <h2 className="text-xl font-bold text-white">Global Headquarters & Support</h2>
          
          <div className="space-y-4">
            <div className="p-5 bg-slate-800/40 border border-slate-800 rounded-2xl flex items-start gap-4">
              <div className="p-3 bg-blue-500/10 text-blue-400 rounded-xl shrink-0">
                <MapPin className="w-5 h-5" />
              </div>
              <div>
                <h4 className="text-sm font-bold text-white">Silicon Valley HQ</h4>
                <p className="text-xs text-slate-400 mt-0.5">100 Tech Knife Way, Suite 500, San Jose, CA 95110, USA</p>
              </div>
            </div>

            <div className="p-5 bg-slate-800/40 border border-slate-800 rounded-2xl flex items-start gap-4">
              <div className="p-3 bg-blue-500/10 text-blue-400 rounded-xl shrink-0">
                <Mail className="w-5 h-5" />
              </div>
              <div>
                <h4 className="text-sm font-bold text-white">Direct Email</h4>
                <p className="text-xs text-slate-400 mt-0.5">contact@techknife.com | support@techknife.com</p>
              </div>
            </div>

            <div className="p-5 bg-slate-800/40 border border-slate-800 rounded-2xl flex items-start gap-4">
              <div className="p-3 bg-blue-500/10 text-blue-400 rounded-xl shrink-0">
                <Phone className="w-5 h-5" />
              </div>
              <div>
                <h4 className="text-sm font-bold text-white">Global Phone Support</h4>
                <p className="text-xs text-slate-400 mt-0.5">+1 (800) 555-0199 | +44 (20) 7946-0912</p>
              </div>
            </div>
          </div>
        </div>

        {/* Contact Form */}
        <div className="p-8 bg-slate-800/40 border border-slate-800 rounded-3xl space-y-6">
          <h2 className="text-xl font-bold text-white">Send Us a Message</h2>

          {submitted ? (
            <div className="p-6 bg-emerald-950/40 border border-emerald-800/40 rounded-2xl text-center space-y-3 animate-in fade-in">
              <CheckCircle2 className="w-10 h-10 text-emerald-400 mx-auto" />
              <h3 className="text-base font-bold text-white">Message Received!</h3>
              <p className="text-xs text-emerald-300">
                Thank you for reaching out. A Senior Solutions Architect from Tech Knife will get back to you within 2 business hours.
              </p>
              <button
                onClick={() => setSubmitted(false)}
                className="mt-2 text-xs font-bold text-blue-400 underline hover:text-blue-300"
              >
                Send Another Message
              </button>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Your Full Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Sarah Jenkins"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full px-4 py-2.5 bg-slate-900 border border-slate-700/80 rounded-xl text-xs text-white focus:outline-none focus:border-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Work Email</label>
                <input
                  type="email"
                  required
                  placeholder="s.jenkins@company.com"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="w-full px-4 py-2.5 bg-slate-900 border border-slate-700/80 rounded-xl text-xs text-white focus:outline-none focus:border-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Company / Organization</label>
                <input
                  type="text"
                  placeholder="Apex Global"
                  value={formData.company}
                  onChange={(e) => setFormData({ ...formData, company: e.target.value })}
                  className="w-full px-4 py-2.5 bg-slate-900 border border-slate-700/80 rounded-xl text-xs text-white focus:outline-none focus:border-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">How Can We Help You?</label>
                <textarea
                  rows={4}
                  required
                  placeholder="Tell us about your project requirements or questions..."
                  value={formData.message}
                  onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                  className="w-full px-4 py-2.5 bg-slate-900 border border-slate-700/80 rounded-xl text-xs text-white focus:outline-none focus:border-blue-500 resize-none"
                />
              </div>

              <button
                type="submit"
                className="w-full py-3.5 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs rounded-xl transition-all shadow-lg shadow-blue-600/30 flex items-center justify-center gap-2"
              >
                <Send className="w-4 h-4" />
                <span>Submit Inquiry</span>
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};
