import React from 'react';
import { BarChart3, PieChart, TrendingUp, DollarSign } from 'lucide-react';
import { Publication, ResearchProfile } from '../../../types/faculty';

interface ResearchChartProps {
  publications: Publication[];
  profile: ResearchProfile;
}

export const ResearchChart: React.FC<ResearchChartProps> = ({ publications, profile }) => {
  // Aggregate publications by type
  const typeCounts: Record<string, number> = {};
  publications.forEach((p) => {
    typeCounts[p.type] = (typeCounts[p.type] || 0) + 1;
  });

  // Citations trend data (mocked historical curve leading up to current citations)
  const citationTrends = [
    { year: 2020, citations: 120 },
    { year: 2021, citations: 280 },
    { year: 2022, citations: 540 },
    { year: 2023, citations: 980 },
    { year: 2024, citations: profile.totalCitations },
  ];

  const maxCitations = Math.max(...citationTrends.map((t) => t.citations), 1);
  const totalPublications = publications.length;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      {/* Citation Growth Trend Chart */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <span className="text-[10px] font-bold text-indigo-500 uppercase tracking-wider">Citation Analytics</span>
            <h3 className="text-base font-black text-slate-900 dark:text-white flex items-center gap-2">
              <TrendingUp className="w-4 h-4 text-emerald-500" /> Citation Growth Curve
            </h3>
          </div>
          <span className="px-2.5 py-1 text-xs font-mono font-extrabold bg-emerald-500/10 text-emerald-600 rounded-xl">
            +{profile.totalCitations} Total
          </span>
        </div>

        {/* Visual Bar Graph for Citations */}
        <div className="h-44 flex items-end justify-between gap-3 pt-6 pb-2 px-2 border-b border-slate-100 dark:border-slate-800">
          {citationTrends.map((item) => {
            const heightPercent = Math.round((item.citations / maxCitations) * 100);
            return (
              <div key={item.year} className="flex-1 flex flex-col items-center gap-2 h-full justify-end group relative">
                {/* Tooltip */}
                <div className="opacity-0 group-hover:opacity-100 transition-opacity absolute -top-8 px-2 py-1 bg-slate-900 text-white text-[10px] font-bold rounded-lg pointer-events-none z-10 whitespace-nowrap">
                  {item.citations} citations in {item.year}
                </div>

                <div
                  style={{ height: `${Math.max(heightPercent, 8)}%` }}
                  className="w-full bg-gradient-to-t from-indigo-600 to-indigo-400 dark:from-indigo-500 dark:to-indigo-300 rounded-t-xl group-hover:brightness-110 transition-all"
                />
                <span className="text-[11px] font-mono font-bold text-slate-500">{item.year}</span>
              </div>
            );
          })}
        </div>

        <div className="flex items-center justify-between text-xs text-slate-500 pt-1">
          <span>h-index: <strong className="text-slate-700 dark:text-slate-300">{profile.hIndex}</strong></span>
          <span>i10-index: <strong className="text-slate-700 dark:text-slate-300">{profile.i10Index}</strong></span>
        </div>
      </div>

      {/* Publication Types Distribution */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <span className="text-[10px] font-bold text-indigo-500 uppercase tracking-wider">Output Distribution</span>
            <h3 className="text-base font-black text-slate-900 dark:text-white flex items-center gap-2">
              <PieChart className="w-4 h-4 text-indigo-500" /> Publications by Type
            </h3>
          </div>
          <span className="px-2.5 py-1 text-xs font-mono font-extrabold bg-indigo-500/10 text-indigo-600 rounded-xl">
            {totalPublications} Publications
          </span>
        </div>

        <div className="space-y-3 pt-2">
          {Object.entries(typeCounts).map(([type, count]) => {
            const percent = totalPublications > 0 ? Math.round((count / totalPublications) * 100) : 0;
            return (
              <div key={type} className="space-y-1">
                <div className="flex items-center justify-between text-xs">
                  <span className="font-bold text-slate-700 dark:text-slate-300">{type}</span>
                  <span className="font-mono text-slate-500">{count} ({percent}%)</span>
                </div>
                <div className="w-full bg-slate-100 dark:bg-slate-800 h-2 rounded-full overflow-hidden">
                  <div
                    style={{ width: `${percent}%` }}
                    className="bg-indigo-600 dark:bg-indigo-500 h-full rounded-full"
                  />
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
