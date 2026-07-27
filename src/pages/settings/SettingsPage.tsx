import React, { useState } from 'react';
import { Settings, Shield, Bell, Key, Database, Save, Check } from 'lucide-react';

export const SettingsPage: React.FC = () => {
  const [saved, setSaved] = useState(false);

  const handleSave = () => {
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  };

  return (
    <div className="space-y-8 max-w-4xl">
      {/* Header */}
      <div>
        <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
          <Settings className="w-4 h-4" />
          <span>System Environment & Configuration</span>
        </div>
        <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">System Settings & Governance</h1>
        <p className="text-xs text-slate-500">Configure global parameters, security tokens, and email gateways</p>
      </div>

      {/* Security Settings */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 space-y-6">
        <h3 className="font-bold text-base text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
          Spring Security & Token Policies
        </h3>

        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h4 className="font-semibold text-xs text-slate-900 dark:text-slate-100">JWT Token Expiration Interval</h4>
              <p className="text-[11px] text-slate-500">Stateless bearer token lifetime before refresh requirement</p>
            </div>
            <select className="text-xs p-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800">
              <option>24 Hours (Default)</option>
              <option>12 Hours</option>
              <option>8 Hours</option>
            </select>
          </div>

          <div className="flex items-center justify-between border-t border-slate-100 dark:border-slate-800 pt-4">
            <div>
              <h4 className="font-semibold text-xs text-slate-900 dark:text-slate-100">Aspect-Oriented Programming (AOP) Audit Logging</h4>
              <p className="text-[11px] text-slate-500">Log all controller method invocations into MongoDB audit_logs collection</p>
            </div>
            <input type="checkbox" defaultChecked className="w-4 h-4 text-indigo-600 rounded" />
          </div>

          <div className="flex items-center justify-between border-t border-slate-100 dark:border-slate-800 pt-4">
            <div>
              <h4 className="font-semibold text-xs text-slate-900 dark:text-slate-100">Cloudinary Media Storage Engine</h4>
              <p className="text-[11px] text-slate-500">Store employee profile avatars and PDF tax slips in cloud CDN</p>
            </div>
            <input type="checkbox" defaultChecked className="w-4 h-4 text-indigo-600 rounded" />
          </div>
        </div>

        <div className="pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
          {saved ? (
            <span className="text-xs font-semibold text-emerald-600 flex items-center gap-1">
              <Check className="w-4 h-4" /> System Parameters Persisted
            </span>
          ) : (
            <span></span>
          )}
          <button
            onClick={handleSave}
            className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-xs rounded-xl transition-all shadow-md flex items-center gap-2"
          >
            <Save className="w-3.5 h-3.5" /> Save Configuration
          </button>
        </div>
      </div>
    </div>
  );
};
