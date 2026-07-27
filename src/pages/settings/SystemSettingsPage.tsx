import React, { useState, useEffect } from 'react';
import {
  Settings,
  Building,
  Mail,
  UploadCloud,
  Github,
  Palette,
  Clock,
  Calendar,
  CheckCircle2,
  Save,
  Loader2,
  ShieldAlert,
  Sparkles
} from 'lucide-react';
import { settingsApi } from '../../api/coreServices';
import { SystemSettings } from '../../types';
import { useAuth } from '../../context/AuthContext';

export const SystemSettingsPage: React.FC = () => {
  const { user } = useAuth();

  const [settings, setSettings] = useState<SystemSettings | null>(null);
  const [activeTab, setActiveTab] = useState<'company' | 'smtp' | 'cloudinary' | 'github' | 'hours'>('company');
  const [isSaving, setIsSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);
  const [smtpTestResult, setSmtpTestResult] = useState<string | null>(null);
  const [cloudinaryTestResult, setCloudinaryTestResult] = useState<string | null>(null);

  useEffect(() => {
    const loadSettings = async () => {
      const data = await settingsApi.getSettings();
      setSettings(data);
    };
    loadSettings();
  }, []);

  if (!settings) {
    return <div className="p-12 text-center text-xs text-slate-400">Loading system configuration...</div>;
  }

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    setSaveSuccess(false);

    try {
      await settingsApi.updateSettings(settings, `${user?.firstName} ${user?.lastName}`);
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 3000);
    } finally {
      setIsSaving(false);
    }
  };

  const testSmtp = async () => {
    setSmtpTestResult('Testing connection...');
    const res = await settingsApi.testSmtpConnection();
    setSmtpTestResult(res.message);
  };

  const testCloudinary = async () => {
    setCloudinaryTestResult('Verifying API Key...');
    const res = await settingsApi.testCloudinaryConnection();
    setCloudinaryTestResult(res.message);
  };

  return (
    <div className="space-y-8 pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 font-semibold text-xs uppercase tracking-wider mb-1">
            <Settings className="w-4 h-4" />
            <span>Universal Configuration Engine</span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">Enterprise Platform Settings</h1>
          <p className="text-xs text-slate-500">Configure corporate information, SMTP mailer, Cloudinary asset storage, GitHub webhooks & working schedules</p>
        </div>

        <button
          onClick={handleSave}
          disabled={isSaving}
          className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl transition-all shadow-md flex items-center gap-2 self-start sm:self-auto disabled:opacity-50"
        >
          {isSaving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />} Save System Configuration
        </button>
      </div>

      {saveSuccess && (
        <div className="p-4 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-2xl text-xs text-emerald-800 dark:text-emerald-200 font-bold flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-600" /> Platform settings updated successfully!
        </div>
      )}

      {/* Tabs */}
      <div className="flex items-center gap-2 p-1.5 bg-slate-100 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl overflow-x-auto custom-scrollbar">
        {[
          { id: 'company', label: 'Company Profile', icon: <Building className="w-3.5 h-3.5" /> },
          { id: 'smtp', label: 'SMTP Mailer', icon: <Mail className="w-3.5 h-3.5" /> },
          { id: 'cloudinary', label: 'Cloudinary Vault', icon: <UploadCloud className="w-3.5 h-3.5" /> },
          { id: 'github', label: 'GitHub Webhooks', icon: <Github className="w-3.5 h-3.5" /> },
          { id: 'hours', label: 'Working Hours & Holidays', icon: <Clock className="w-3.5 h-3.5" /> },
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id as any)}
            className={`px-4 py-2 rounded-xl text-xs font-bold flex items-center gap-2 whitespace-nowrap transition-all ${
              activeTab === tab.id
                ? 'bg-indigo-600 text-white shadow-xs'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200/60 dark:hover:bg-slate-800'
            }`}
          >
            {tab.icon}
            <span>{tab.label}</span>
          </button>
        ))}
      </div>

      {/* Form Body */}
      <form onSubmit={handleSave} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-6">
        {/* 1. Company Information */}
        {activeTab === 'company' && (
          <div className="space-y-4">
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
              Corporate & Business Identity
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Company Legal Name</label>
                <input
                  type="text"
                  value={settings.companyInfo.name}
                  onChange={(e) =>
                    setSettings({ ...settings, companyInfo: { ...settings.companyInfo, name: e.target.value } })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-medium"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Federal Tax Identification (EIN / Tax ID)</label>
                <input
                  type="text"
                  value={settings.companyInfo.taxId}
                  onChange={(e) =>
                    setSettings({ ...settings, companyInfo: { ...settings.companyInfo, taxId: e.target.value } })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-medium"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Official Contact Email</label>
                <input
                  type="email"
                  value={settings.companyInfo.contactEmail}
                  onChange={(e) =>
                    setSettings({ ...settings, companyInfo: { ...settings.companyInfo, contactEmail: e.target.value } })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-medium"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Support Phone Number</label>
                <input
                  type="text"
                  value={settings.companyInfo.supportPhone}
                  onChange={(e) =>
                    setSettings({ ...settings, companyInfo: { ...settings.companyInfo, supportPhone: e.target.value } })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-medium"
                />
              </div>

              <div className="md:col-span-2">
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Headquarters Physical Address</label>
                <input
                  type="text"
                  value={settings.companyInfo.address}
                  onChange={(e) =>
                    setSettings({ ...settings, companyInfo: { ...settings.companyInfo, address: e.target.value } })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-medium"
                />
              </div>
            </div>
          </div>
        )}

        {/* 2. SMTP Config */}
        {activeTab === 'smtp' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-extrabold text-base text-slate-900 dark:text-white">SMTP Email Infrastructure</h3>
              <button
                type="button"
                onClick={testSmtp}
                className="px-3.5 py-1.5 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-indigo-600 dark:text-indigo-400 font-extrabold text-xs rounded-xl transition-all"
              >
                Test SMTP Handshake
              </button>
            </div>

            {smtpTestResult && (
              <div className="p-3 bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-200 dark:border-indigo-800 rounded-xl text-xs text-indigo-800 dark:text-indigo-200 font-semibold">
                {smtpTestResult}
              </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">SMTP Server Host</label>
                <input
                  type="text"
                  value={settings.smtpConfig.host}
                  onChange={(e) =>
                    setSettings({ ...settings, smtpConfig: { ...settings.smtpConfig, host: e.target.value } })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">SMTP Port (SSL/TLS)</label>
                <input
                  type="number"
                  value={settings.smtpConfig.port}
                  onChange={(e) =>
                    setSettings({ ...settings, smtpConfig: { ...settings.smtpConfig, port: Number(e.target.value) } })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Sender Email Address</label>
                <input
                  type="email"
                  value={settings.smtpConfig.senderEmail}
                  onChange={(e) =>
                    setSettings({ ...settings, smtpConfig: { ...settings.smtpConfig, senderEmail: e.target.value } })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-medium"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Sender Display Name</label>
                <input
                  type="text"
                  value={settings.smtpConfig.senderName}
                  onChange={(e) =>
                    setSettings({ ...settings, smtpConfig: { ...settings.smtpConfig, senderName: e.target.value } })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-medium"
                />
              </div>
            </div>
          </div>
        )}

        {/* 3. Cloudinary Config */}
        {activeTab === 'cloudinary' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="font-extrabold text-base text-slate-900 dark:text-white">Cloudinary Asset Storage Vault</h3>
              <button
                type="button"
                onClick={testCloudinary}
                className="px-3.5 py-1.5 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-indigo-600 dark:text-indigo-400 font-extrabold text-xs rounded-xl transition-all"
              >
                Verify Cloudinary Bucket
              </button>
            </div>

            {cloudinaryTestResult && (
              <div className="p-3 bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-200 dark:border-indigo-800 rounded-xl text-xs text-indigo-800 dark:text-indigo-200 font-semibold">
                {cloudinaryTestResult}
              </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Cloud Name</label>
                <input
                  type="text"
                  value={settings.cloudinaryConfig.cloudName}
                  onChange={(e) =>
                    setSettings({
                      ...settings,
                      cloudinaryConfig: { ...settings.cloudinaryConfig, cloudName: e.target.value },
                    })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">API Key</label>
                <input
                  type="text"
                  value={settings.cloudinaryConfig.apiKey}
                  onChange={(e) =>
                    setSettings({
                      ...settings,
                      cloudinaryConfig: { ...settings.cloudinaryConfig, apiKey: e.target.value },
                    })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Default Storage Folder</label>
                <input
                  type="text"
                  value={settings.cloudinaryConfig.defaultFolder}
                  onChange={(e) =>
                    setSettings({
                      ...settings,
                      cloudinaryConfig: { ...settings.cloudinaryConfig, defaultFolder: e.target.value },
                    })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono"
                />
              </div>
            </div>
          </div>
        )}

        {/* 4. GitHub Integration */}
        {activeTab === 'github' && (
          <div className="space-y-4">
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
              GitHub Enterprise Integration & Webhooks
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Organization Name</label>
                <input
                  type="text"
                  value={settings.githubConfig.organization}
                  onChange={(e) =>
                    setSettings({
                      ...settings,
                      githubConfig: { ...settings.githubConfig, organization: e.target.value },
                    })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Webhook Endpoint URL</label>
                <input
                  type="text"
                  value={settings.githubConfig.webhookUrl}
                  onChange={(e) =>
                    setSettings({
                      ...settings,
                      githubConfig: { ...settings.githubConfig, webhookUrl: e.target.value },
                    })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-mono"
                />
              </div>
            </div>
          </div>
        )}

        {/* 5. Working Hours */}
        {activeTab === 'hours' && (
          <div className="space-y-4">
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-3">
              Corporate Working Hours & Holiday Calendar
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Shift Start Time</label>
                <input
                  type="time"
                  value={settings.workingHours.startTime}
                  onChange={(e) =>
                    setSettings({
                      ...settings,
                      workingHours: { ...settings.workingHours, startTime: e.target.value },
                    })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-semibold"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Shift End Time</label>
                <input
                  type="time"
                  value={settings.workingHours.endTime}
                  onChange={(e) =>
                    setSettings({
                      ...settings,
                      workingHours: { ...settings.workingHours, endTime: e.target.value },
                    })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-semibold"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Corporate Timezone</label>
                <input
                  type="text"
                  value={settings.workingHours.timezone}
                  onChange={(e) =>
                    setSettings({
                      ...settings,
                      workingHours: { ...settings.workingHours, timezone: e.target.value },
                    })
                  }
                  className="w-full text-xs p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-semibold"
                />
              </div>
            </div>

            {/* Holiday Calendar List */}
            <div className="pt-4 space-y-2">
              <h4 className="text-xs font-extrabold uppercase text-slate-400">Configured Corporate Holidays</h4>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                {settings.holidayCalendar.map((h) => (
                  <div
                    key={h.id}
                    className="p-3 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 flex items-center justify-between text-xs"
                  >
                    <div>
                      <div className="font-extrabold text-slate-900 dark:text-white">{h.name}</div>
                      <div className="text-[11px] text-slate-500 font-semibold">{h.date}</div>
                    </div>
                    <span className="px-2 py-0.5 rounded-full bg-indigo-100 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300 text-[10px] font-bold">
                      {h.type}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </form>
    </div>
  );
};
