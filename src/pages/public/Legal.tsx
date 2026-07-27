import React from 'react';
import { useLocation } from 'react-router-dom';
import { Shield, FileText, CheckCircle2 } from 'lucide-react';

export const LegalPage: React.FC = () => {
  const location = useLocation();
  const path = location.pathname;

  let title = 'Privacy Policy';
  if (path.includes('terms')) title = 'Terms of Service';
  if (path.includes('refund')) title = 'Refund Policy';

  return (
    <div className="py-12 px-6 max-w-4xl mx-auto space-y-8">
      <div className="space-y-2 border-b border-slate-800 pb-6">
        <div className="flex items-center gap-2 text-blue-400 text-xs font-bold uppercase tracking-wider">
          <Shield className="w-4 h-4" />
          <span>Legal & Compliance</span>
        </div>
        <h1 className="text-3xl font-black text-white">{title}</h1>
        <p className="text-xs text-slate-400">Last updated: July 2026 | Tech Knife Legal Operations</p>
      </div>

      <div className="space-y-6 text-xs text-slate-300 leading-relaxed">
        <section className="space-y-2">
          <h2 className="text-sm font-bold text-white">1. Governance & Overview</h2>
          <p>
            Tech Knife is committed to operating with complete transparency and compliance. This document outlines the legal terms, data handling policies, and service obligations governing use of the Tech Knife platform, website, and API services.
          </p>
        </section>

        <section className="space-y-2">
          <h2 className="text-sm font-bold text-white">2. Data Security & Confidentiality</h2>
          <p>
            All data transmitted through Tech Knife platforms is encrypted in transit via TLS 1.3 and at rest using AES-256 standards. We strictly adhere to SOC2 Type II, ISO 27001, and GDPR guidelines. No customer data is sold or shared with third parties without explicit authorization.
          </p>
        </section>

        <section className="space-y-2">
          <h2 className="text-sm font-bold text-white">3. Platform SLAs & Service Obligations</h2>
          <p>
            Tech Knife guarantees 99.9% uptime for production cloud workspaces. Maintenance windows are scheduled in advance during off-peak hours with mandatory advance notice to organization administrators.
          </p>
        </section>

        <section className="space-y-2">
          <h2 className="text-sm font-bold text-white">4. Contact Legal Department</h2>
          <p>
            For any compliance inquiries, DPA requests, or security audit documentation, please contact legal@techknife.com.
          </p>
        </section>
      </div>
    </div>
  );
};
