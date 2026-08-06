import React, { useState } from 'react';
import { 
  ShieldCheck, Lock, Key, Users, CheckCircle2, AlertTriangle, 
  RefreshCw, Globe, FileText, Cpu, Eye, ShieldAlert
} from 'lucide-react';

export const EnterpriseSecuritySuite: React.FC = () => {
  const [ssoEnabled, setSsoEnabled] = useState(true);
  const [mfaEnforced, setMfaEnforced] = useState(true);
  const [abacActive, setAbacActive] = useState(true);
  const [gdprMasking, setGdprMasking] = useState(true);

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-slate-950 via-slate-900 to-slate-950 border border-slate-800 text-white shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-3 py-1 bg-emerald-500/20 text-emerald-400 font-mono text-xs font-bold rounded-full border border-emerald-500/30 flex items-center gap-1.5">
              <ShieldCheck className="w-3.5 h-3.5" /> Security & Governance Suite
            </span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black tracking-tight">SAML 2.0 / Okta SSO, MFA, ABAC & SOC 2 Compliance</h2>
          <p className="text-xs text-slate-400 font-medium">Enterprise Identity Management • Data Loss Prevention • Immutable Audit Logs</p>
        </div>
      </div>

      {/* Security Controls Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        {/* SSO & SAML Card */}
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-2xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
                <Key className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm font-extrabold text-slate-900 dark:text-white">SAML 2.0 / Okta / Azure AD SSO</h3>
                <p className="text-xs text-slate-500 font-medium">Single Sign-On Identity Provider Integration</p>
              </div>
            </div>
            <button
              onClick={() => setSsoEnabled(!ssoEnabled)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-extrabold transition-all ${
                ssoEnabled ? 'bg-emerald-600 text-white shadow-xs' : 'bg-slate-200 text-slate-600'
              }`}
            >
              {ssoEnabled ? 'ENFORCED' : 'DISABLED'}
            </button>
          </div>

          <div className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 text-xs font-mono space-y-1">
            <div className="flex justify-between text-slate-500">
              <span>Metadata URL:</span>
              <span className="font-bold text-cyan-600">https://auth.techknife.io/saml/metadata</span>
            </div>
            <div className="flex justify-between text-slate-500">
              <span>IdP Provider:</span>
              <span className="font-bold text-slate-900 dark:text-white">Okta Enterprise / Google Workspace</span>
            </div>
          </div>
        </div>

        {/* Multi-Factor Authentication (MFA) Card */}
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-2xl bg-cyan-500/10 text-cyan-600 dark:text-cyan-400">
                <Lock className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm font-extrabold text-slate-900 dark:text-white">Multi-Factor Authentication (MFA)</h3>
                <p className="text-xs text-slate-500 font-medium">TOTP Authenticator & WebAuthn Security Keys</p>
              </div>
            </div>
            <button
              onClick={() => setMfaEnforced(!mfaEnforced)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-extrabold transition-all ${
                mfaEnforced ? 'bg-emerald-600 text-white shadow-xs' : 'bg-slate-200 text-slate-600'
              }`}
            >
              {mfaEnforced ? 'ENFORCED' : 'OPTIONAL'}
            </button>
          </div>

          <div className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 text-xs font-mono space-y-1">
            <div className="flex justify-between text-slate-500">
              <span>TOTP Standard:</span>
              <span className="font-bold text-emerald-600">RFC 6238 Compliant</span>
            </div>
            <div className="flex justify-between text-slate-500">
              <span>Hardware Key:</span>
              <span className="font-bold text-slate-900 dark:text-white">YubiKey / WebAuthn Active</span>
            </div>
          </div>
        </div>

        {/* Attribute-Based Access Control (ABAC) Card */}
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-2xl bg-amber-500/10 text-amber-600 dark:text-amber-400">
                <Users className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm font-extrabold text-slate-900 dark:text-white">Attribute-Based Access Control (ABAC)</h3>
                <p className="text-xs text-slate-500 font-medium">Granular IP Range & Environmental Policy Checks</p>
              </div>
            </div>
            <button
              onClick={() => setAbacActive(!abacActive)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-extrabold transition-all ${
                abacActive ? 'bg-emerald-600 text-white shadow-xs' : 'bg-slate-200 text-slate-600'
              }`}
            >
              {abacActive ? 'ACTIVE' : 'INACTIVE'}
            </button>
          </div>
        </div>

        {/* SOC 2 & GDPR PII Masking Card */}
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-2xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400">
                <ShieldAlert className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm font-extrabold text-slate-900 dark:text-white">SOC 2 Type II & GDPR Compliance</h3>
                <p className="text-xs text-slate-500 font-medium">PII Data Masking & Automatic Audit Logging</p>
              </div>
            </div>
            <button
              onClick={() => setGdprMasking(!gdprMasking)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-extrabold transition-all ${
                gdprMasking ? 'bg-emerald-600 text-white shadow-xs' : 'bg-slate-200 text-slate-600'
              }`}
            >
              {gdprMasking ? 'COMPLIANT' : 'WARNING'}
            </button>
          </div>
        </div>

      </div>

    </div>
  );
};
