import React, { useState } from 'react';
import { GitCommit, GitPullRequest, GitBranch, ExternalLink, ChevronRight, Code, Terminal, CheckCircle2, RefreshCw } from 'lucide-react';

interface GitCommitItem {
  id: string;
  hash: string;
  message: string;
  repo: string;
  branch: string;
  author: string;
  timeAgo: string;
  additions: number;
  deletions: number;
  status: 'merged' | 'committed' | 'review';
}

const GITHUB_COMMITS: GitCommitItem[] = [
  {
    id: 'commit-1',
    hash: '8f2a1e9',
    message: 'feat(auth): implement JWT refresh token rotation with Redis store',
    repo: 'tech-knife-backend',
    branch: 'feature/jwt-rotation',
    author: 'You',
    timeAgo: '18 mins ago',
    additions: 142,
    deletions: 18,
    status: 'committed',
  },
  {
    id: 'commit-2',
    hash: 'c4e3d10',
    message: 'fix(ui): adjust dark mode contrast for employee dashboard widgets',
    repo: 'tech-knife-frontend',
    branch: 'main',
    author: 'You',
    timeAgo: '2 hours ago',
    additions: 86,
    deletions: 12,
    status: 'merged',
  },
  {
    id: 'commit-3',
    hash: 'a9b8c7d',
    message: 'test(payroll): add DTO schema validation for payslip generation',
    repo: 'tech-knife-backend',
    branch: 'feature/payroll-tests',
    author: 'You',
    timeAgo: 'Yesterday',
    additions: 210,
    deletions: 45,
    status: 'review',
  },
];

export const GithubActivityWidget: React.FC = () => {
  const [activeRepo, setActiveRepo] = useState<string>('all');
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleSyncGit = () => {
    setIsRefreshing(true);
    setTimeout(() => setIsRefreshing(false), 800);
  };

  const filteredCommits = GITHUB_COMMITS.filter((c) => {
    if (activeRepo !== 'all') return c.repo === activeRepo;
    return true;
  });

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-4 flex flex-col justify-between h-full transition-all hover:border-indigo-500/30">
      {/* Widget Header */}
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-slate-900 dark:bg-slate-800 text-white">
            <GitCommit className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <span>GitHub Developer Activity</span>
              <span className="px-2 py-0.5 bg-emerald-500/10 text-emerald-500 font-mono text-[10px] font-bold rounded-full border border-emerald-500/20">
                Connected
              </span>
            </h3>
            <p className="text-[11px] text-slate-500">Live commits, pull requests & code review logs</p>
          </div>
        </div>

        <button
          onClick={handleSyncGit}
          className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-700 dark:text-slate-300 rounded-xl transition-colors"
          title="Sync Git Activity"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${isRefreshing ? 'animate-spin' : ''}`} />
        </button>
      </div>

      {/* GitHub Quick Metrics Bar */}
      <div className="grid grid-cols-4 gap-2 text-center text-xs">
        <div className="p-2.5 bg-slate-50 dark:bg-slate-950/60 rounded-xl border border-slate-200/80 dark:border-slate-800">
          <span className="text-[10px] font-bold text-slate-400 uppercase block">Commits</span>
          <span className="text-sm font-black font-mono text-slate-900 dark:text-white">14 Today</span>
        </div>

        <div className="p-2.5 bg-slate-50 dark:bg-slate-950/60 rounded-xl border border-slate-200/80 dark:border-slate-800">
          <span className="text-[10px] font-bold text-slate-400 uppercase block">Open PRs</span>
          <span className="text-sm font-black font-mono text-indigo-500">3 Open</span>
        </div>

        <div className="p-2.5 bg-slate-50 dark:bg-slate-950/60 rounded-xl border border-slate-200/80 dark:border-slate-800">
          <span className="text-[10px] font-bold text-slate-400 uppercase block">Merged</span>
          <span className="text-sm font-black font-mono text-emerald-500">12 PRs</span>
        </div>

        <div className="p-2.5 bg-slate-50 dark:bg-slate-950/60 rounded-xl border border-slate-200/80 dark:border-slate-800">
          <span className="text-[10px] font-bold text-slate-400 uppercase block">Reviews</span>
          <span className="text-sm font-black font-mono text-amber-500">2 Pending</span>
        </div>
      </div>

      {/* Commits Stream */}
      <div className="space-y-2.5 flex-1 overflow-y-auto max-h-[260px] pr-1">
        {filteredCommits.map((c) => (
          <div
            key={c.id}
            className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-950/60 border border-slate-200/80 dark:border-slate-800 space-y-2"
          >
            <div className="flex items-center justify-between gap-2">
              <div className="flex items-center gap-1.5 font-mono text-[11px] font-bold text-slate-900 dark:text-white">
                <GitBranch className="w-3.5 h-3.5 text-indigo-500 shrink-0" />
                <span className="px-1.5 py-0.5 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 rounded text-[10px]">
                  {c.hash}
                </span>
                <span className="text-slate-400 text-[10px]">{c.branch}</span>
              </div>

              <span className="text-[10px] text-slate-400 font-mono shrink-0">{c.timeAgo}</span>
            </div>

            <p className="text-xs font-semibold text-slate-800 dark:text-slate-200 line-clamp-2 leading-relaxed">
              {c.message}
            </p>

            <div className="flex items-center justify-between text-[10px] text-slate-500 font-mono pt-1 border-t border-slate-200/50 dark:border-slate-800/80">
              <span className="text-slate-400">{c.repo}</span>
              <div className="flex items-center gap-2">
                <span className="text-emerald-500 font-bold">+{c.additions}</span>
                <span className="text-rose-500 font-bold">-{c.deletions}</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* External Repository Link CTA */}
      <a
        href="https://github.com/orgs/tech-knife/repositories"
        target="_blank"
        rel="noopener noreferrer"
        className="w-full py-2.5 bg-slate-900 dark:bg-slate-800 hover:bg-slate-800 text-white font-bold text-xs rounded-xl transition-colors flex items-center justify-center gap-2 shadow"
      >
        <Code className="w-4 h-4" /> View GitHub Repository <ExternalLink className="w-3.5 h-3.5" />
      </a>
    </div>
  );
};
