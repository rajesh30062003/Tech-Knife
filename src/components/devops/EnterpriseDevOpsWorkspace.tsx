import React, { useState } from 'react';
import { 
  GitBranch, Server, Cpu, Layers, Terminal, Activity, CheckCircle2, 
  AlertTriangle, RefreshCw, ExternalLink, ShieldCheck, Box, Cloud
} from 'lucide-react';

interface PipelineJob {
  id: string;
  repoName: string;
  branch: string;
  provider: 'GitHub Actions' | 'GitLab CI' | 'Jenkins' | 'Docker Hub';
  status: 'SUCCESS' | 'RUNNING' | 'FAILED';
  commitMsg: string;
  duration: string;
  timestamp: string;
}

const MOCK_PIPELINES: PipelineJob[] = [
  {
    id: 'pipe-101',
    repoName: 'techknife-enterprise-backend',
    branch: 'main',
    provider: 'GitHub Actions',
    status: 'SUCCESS',
    commitMsg: 'feat(oauth2): Migrate Google Drive authentication to OAuth 2.0 offline flow',
    duration: '2m 14s',
    timestamp: '12 mins ago',
  },
  {
    id: 'pipe-102',
    repoName: 'techknife-enterprise-frontend',
    branch: 'main',
    provider: 'GitLab CI',
    status: 'SUCCESS',
    commitMsg: 'feat(workspace): Expand Enterprise Project Workspace with AI Copilot & Command Palette',
    duration: '1m 45s',
    timestamp: '28 mins ago',
  },
  {
    id: 'pipe-103',
    repoName: 'techknife-k8s-cluster',
    branch: 'production',
    provider: 'Jenkins',
    status: 'RUNNING',
    commitMsg: 'deploy(helm): Upgrade Spring Boot 3.5 replica set to 4 pods',
    duration: '45s',
    timestamp: 'Just now',
  },
];

export const EnterpriseDevOpsWorkspace: React.FC = () => {
  const [pipelines, setPipelines] = useState<PipelineJob[]>(MOCK_PIPELINES);
  const [isSyncing, setIsSyncing] = useState(false);

  const handleSyncTelemetry = () => {
    setIsSyncing(true);
    setTimeout(() => {
      setIsSyncing(false);
    }, 1000);
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-slate-950 via-slate-900 to-slate-950 border border-slate-800 text-white shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-3 py-1 bg-cyan-500/20 text-cyan-400 font-mono text-xs font-bold rounded-full border border-cyan-500/30 flex items-center gap-1.5">
              <Activity className="w-3.5 h-3.5" /> DevOps & Telemetry Dashboard
            </span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black tracking-tight">GitHub, GitLab, Jenkins & Kubernetes Runner Telemetry</h2>
          <p className="text-xs text-slate-400 font-medium">Real-time CI/CD Build Monitoring • Docker Image Registry • Kubernetes Pod Cluster Health</p>
        </div>

        <button
          onClick={handleSyncTelemetry}
          disabled={isSyncing}
          className="px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-2xl shadow-md transition-all flex items-center gap-2 disabled:opacity-50 shrink-0"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin' : ''}`} />
          <span>Sync Runner Webhooks</span>
        </button>
      </div>

      {/* Cluster Metrics Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-2">
          <span className="text-[10px] uppercase font-bold text-slate-400 block flex items-center gap-1">
            <Server className="w-3.5 h-3.5 text-cyan-500" /> K8s Pod Cluster Health
          </span>
          <div className="flex items-baseline justify-between">
            <span className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 font-mono">100% HEALTH</span>
            <span className="text-xs text-slate-400 font-mono">4/4 Pods</span>
          </div>
          <p className="text-xs text-slate-500 font-medium">Spring Boot 3.5 Replica Set</p>
        </div>

        <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-2">
          <span className="text-[10px] uppercase font-bold text-slate-400 block flex items-center gap-1">
            <Cpu className="w-3.5 h-3.5 text-indigo-500" /> CPU & Memory Usage
          </span>
          <div className="flex items-baseline justify-between">
            <span className="text-xl font-extrabold text-slate-900 dark:text-white font-mono">1.2 GB / 4.0 GB</span>
            <span className="text-xs text-slate-400 font-mono">30% CPU</span>
          </div>
          <p className="text-xs text-slate-500 font-medium">Nominal Operating Load</p>
        </div>

        <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-2">
          <span className="text-[10px] uppercase font-bold text-slate-400 block flex items-center gap-1">
            <Box className="w-3.5 h-3.5 text-amber-500" /> Docker Registry Image
          </span>
          <div className="flex items-baseline justify-between">
            <span className="text-xl font-extrabold text-slate-900 dark:text-white font-mono">v1.0.0-SNAPSHOT</span>
            <span className="text-xs text-emerald-500 font-bold font-mono">Verified</span>
          </div>
          <p className="text-xs text-slate-500 font-medium">SHA-256 Digest Validated</p>
        </div>

        <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-2">
          <span className="text-[10px] uppercase font-bold text-slate-400 block flex items-center gap-1">
            <Cloud className="w-3.5 h-3.5 text-cyan-500" /> GCP Cloud Availability
          </span>
          <div className="flex items-baseline justify-between">
            <span className="text-xl font-extrabold text-cyan-600 dark:text-cyan-400 font-mono">99.99% Uptime</span>
            <span className="text-xs text-slate-400 font-mono">asia-south1</span>
          </div>
          <p className="text-xs text-slate-500 font-medium">Multi-AZ Failover Ready</p>
        </div>
      </div>

      {/* CI/CD Pipeline Stream Table */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
        <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
          <GitBranch className="w-4 h-4 text-cyan-500" /> CI/CD Build Pipeline Activity Stream
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-400 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200 dark:border-slate-800">
              <tr>
                <th className="py-3 px-4">Repository</th>
                <th className="py-3 px-4">Provider</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4">Commit Message</th>
                <th className="py-3 px-4">Duration</th>
                <th className="py-3 px-4">Triggered</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
              {pipelines.map((pipe) => (
                <tr key={pipe.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition-colors">
                  <td className="py-3 px-4 font-mono font-bold text-cyan-600 dark:text-cyan-400">{pipe.repoName} ({pipe.branch})</td>
                  <td className="py-3 px-4 font-bold text-slate-700 dark:text-slate-300">{pipe.provider}</td>
                  <td className="py-3 px-4">
                    <span className={`px-2.5 py-0.5 rounded-md font-mono text-[10px] font-bold ${
                      pipe.status === 'SUCCESS' ? 'bg-emerald-500/10 text-emerald-600' : 'bg-cyan-500/10 text-cyan-600'
                    }`}>
                      {pipe.status}
                    </span>
                  </td>
                  <td className="py-3 px-4 font-medium text-slate-600 dark:text-slate-300 truncate max-w-md">{pipe.commitMsg}</td>
                  <td className="py-3 px-4 font-mono text-slate-400">{pipe.duration}</td>
                  <td className="py-3 px-4 text-slate-400 font-mono">{pipe.timestamp}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

    </div>
  );
};
