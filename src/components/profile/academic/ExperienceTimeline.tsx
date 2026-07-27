import React, { useState } from 'react';
import {
  Clock,
  GraduationCap,
  Briefcase,
  TrendingUp,
  Award,
  BookOpen,
  Presentation,
  CheckCircle2,
  Filter,
} from 'lucide-react';
import { ProfileTimelineItem, TimelineCategory } from '../../../types/faculty';

interface ExperienceTimelineProps {
  timeline: ProfileTimelineItem[];
}

export const ExperienceTimeline: React.FC<ExperienceTimelineProps> = ({ timeline }) => {
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');

  const filteredTimeline = selectedCategory === 'ALL'
    ? timeline
    : timeline.filter((item) => item.category === selectedCategory);

  const getCategoryIcon = (category: TimelineCategory) => {
    switch (category) {
      case 'Education':
        return <GraduationCap className="w-4 h-4 text-emerald-500" />;
      case 'Appointments':
        return <Briefcase className="w-4 h-4 text-indigo-500" />;
      case 'Promotions':
        return <TrendingUp className="w-4 h-4 text-blue-500" />;
      case 'Awards':
        return <Award className="w-4 h-4 text-amber-500" />;
      case 'Research':
        return <Clock className="w-4 h-4 text-purple-500" />;
      case 'Publications':
        return <BookOpen className="w-4 h-4 text-rose-500" />;
      case 'Training':
        return <Presentation className="w-4 h-4 text-cyan-500" />;
      default:
        return <CheckCircle2 className="w-4 h-4 text-slate-500" />;
    }
  };

  const getCategoryBadgeClass = (category: TimelineCategory) => {
    switch (category) {
      case 'Education':
        return 'bg-emerald-500/10 text-emerald-600 border-emerald-500/20';
      case 'Appointments':
        return 'bg-indigo-500/10 text-indigo-600 border-indigo-500/20';
      case 'Promotions':
        return 'bg-blue-500/10 text-blue-600 border-blue-500/20';
      case 'Awards':
        return 'bg-amber-500/10 text-amber-600 border-amber-500/20';
      case 'Research':
        return 'bg-purple-500/10 text-purple-600 border-purple-500/20';
      case 'Publications':
        return 'bg-rose-500/10 text-rose-600 border-rose-500/20';
      case 'Training':
        return 'bg-cyan-500/10 text-cyan-600 border-cyan-500/20';
      default:
        return 'bg-slate-500/10 text-slate-600 border-slate-500/20';
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
        <div>
          <span className="text-[10px] font-bold text-indigo-500 uppercase tracking-wider">
            Chronological Academic Milestones
          </span>
          <h2 className="text-xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <Clock className="w-5 h-5 text-indigo-500" /> Faculty Profile Timeline
          </h2>
        </div>

        {/* Category Filters */}
        <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar max-w-full">
          {['ALL', 'Education', 'Appointments', 'Promotions', 'Awards', 'Research', 'Publications', 'Training'].map(
            (cat) => (
              <button
                key={cat}
                onClick={() => setSelectedCategory(cat)}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap ${
                  selectedCategory === cat
                    ? 'bg-indigo-600 text-white shadow-sm'
                    : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:bg-slate-200'
                }`}
              >
                {cat}
              </button>
            )
          )}
        </div>
      </div>

      {/* Timeline Stream */}
      <div className="relative pl-6 sm:pl-8 space-y-8 before:absolute before:left-2.5 sm:before:left-3.5 before:top-3 before:bottom-3 before:w-0.5 before:bg-slate-200 dark:before:bg-slate-800">
        {filteredTimeline.length === 0 ? (
          <p className="text-xs text-slate-500 py-4">No milestone items matching the selected filter.</p>
        ) : (
          filteredTimeline.map((item) => (
            <div key={item.id} className="relative group">
              {/* Connector Circle Icon */}
              <div className="absolute -left-6 sm:-left-8 top-1 p-1.5 bg-white dark:bg-slate-900 border-2 border-indigo-600 rounded-full shadow-md z-10 group-hover:scale-110 transition-transform">
                {getCategoryIcon(item.category)}
              </div>

              {/* Card Container */}
              <div className="bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800/80 rounded-2xl p-4 sm:p-5 space-y-2 shadow-xs group-hover:border-indigo-500/40 transition-colors">
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <div className="flex items-center gap-2">
                    <span
                      className={`px-2.5 py-0.5 text-[10px] font-bold uppercase rounded-full border ${getCategoryBadgeClass(
                        item.category
                      )}`}
                    >
                      {item.category}
                    </span>
                    <span className="text-xs font-mono font-extrabold text-slate-900 dark:text-white">
                      {item.date}
                    </span>
                  </div>
                  <span className="text-[11px] font-mono text-slate-400 font-bold">Year {item.year}</span>
                </div>

                <h3 className="text-sm font-extrabold text-slate-900 dark:text-white">{item.title}</h3>
                <p className="text-xs font-bold text-slate-600 dark:text-slate-300">{item.organization}</p>
                {item.description && (
                  <p className="text-xs text-slate-500 dark:text-slate-400 pt-1 leading-relaxed">
                    {item.description}
                  </p>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
