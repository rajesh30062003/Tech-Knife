import React, { useState } from 'react';
import {
  GraduationCap,
  BookOpen,
  Briefcase,
  Award,
  ShieldCheck,
  Presentation,
  Clock,
  BarChart2,
  RefreshCw,
} from 'lucide-react';
import {
  useResearchProfile,
  useFacultyPublications,
  useTeachingExperience,
  useFacultyMemberships,
  useFacultyAwards,
  useFacultySeminars,
  useProfileTimeline,
} from '../../../hooks/useFacultyPortfolio';

import { ResearchSummaryCard } from './ResearchSummaryCard';
import { ResearchChart } from './ResearchChart';
import { PublicationTable } from './PublicationTable';
import { TeachingExperienceSection } from './TeachingExperienceSection';
import { MembershipCard } from './MembershipCard';
import { AwardCard } from './AwardCard';
import { TrainingCard } from './TrainingCard';
import { ExperienceTimeline } from './ExperienceTimeline';

type AcademicSectionTab =
  | 'research'
  | 'publications'
  | 'teaching'
  | 'memberships'
  | 'awards'
  | 'seminars'
  | 'timeline';

export const AcademicPortfolio: React.FC = () => {
  const [activeSubTab, setActiveSubTab] = useState<AcademicSectionTab>('research');

  // Hooks
  const { profile, loading: loadingProfile, saveProfile } = useResearchProfile();
  const {
    publications,
    loading: loadingPubs,
    createPublication,
    updatePublication,
    deletePublication,
  } = useFacultyPublications();
  const { experiences, saveTeaching } = useTeachingExperience();
  const { memberships, saveMembership, deleteMembership } = useFacultyMemberships();
  const { awards, saveAward, deleteAward } = useFacultyAwards();
  const { seminars, saveSeminar, deleteSeminar } = useFacultySeminars();
  const { timeline } = useProfileTimeline();

  if (loadingProfile || loadingPubs) {
    return (
      <div className="p-12 text-center space-y-3">
        <RefreshCw className="w-8 h-8 text-indigo-600 animate-spin mx-auto" />
        <p className="text-xs font-bold text-slate-500 uppercase tracking-wider">
          Loading Faculty Academic Portfolio...
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Portfolio Sub Navigation Header Pills */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-3 shadow-sm flex items-center justify-between gap-2 overflow-x-auto no-scrollbar">
        <div className="flex items-center gap-1 sm:gap-2">
          <button
            onClick={() => setActiveSubTab('research')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-extrabold transition-all whitespace-nowrap flex items-center gap-2 ${
              activeSubTab === 'research'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <GraduationCap className="w-4 h-4" /> Research Profile
          </button>

          <button
            onClick={() => setActiveSubTab('publications')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-extrabold transition-all whitespace-nowrap flex items-center gap-2 ${
              activeSubTab === 'publications'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <BookOpen className="w-4 h-4" /> Publications ({publications.length})
          </button>

          <button
            onClick={() => setActiveSubTab('teaching')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-extrabold transition-all whitespace-nowrap flex items-center gap-2 ${
              activeSubTab === 'teaching'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <Briefcase className="w-4 h-4" /> Teaching ({experiences.length})
          </button>

          <button
            onClick={() => setActiveSubTab('memberships')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-extrabold transition-all whitespace-nowrap flex items-center gap-2 ${
              activeSubTab === 'memberships'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <ShieldCheck className="w-4 h-4" /> Memberships ({memberships.length})
          </button>

          <button
            onClick={() => setActiveSubTab('awards')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-extrabold transition-all whitespace-nowrap flex items-center gap-2 ${
              activeSubTab === 'awards'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <Award className="w-4 h-4" /> Awards ({awards.length})
          </button>

          <button
            onClick={() => setActiveSubTab('seminars')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-extrabold transition-all whitespace-nowrap flex items-center gap-2 ${
              activeSubTab === 'seminars'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <Presentation className="w-4 h-4" /> Seminars/Workshops ({seminars.length})
          </button>

          <button
            onClick={() => setActiveSubTab('timeline')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-extrabold transition-all whitespace-nowrap flex items-center gap-2 ${
              activeSubTab === 'timeline'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            <Clock className="w-4 h-4" /> Timeline
          </button>
        </div>
      </div>

      {/* Sub Tab Content */}
      {activeSubTab === 'research' && profile && (
        <div className="space-y-6">
          <ResearchSummaryCard profile={profile} onUpdateProfile={saveProfile} />
          <ResearchChart publications={publications} profile={profile} />
        </div>
      )}

      {activeSubTab === 'publications' && (
        <PublicationTable
          publications={publications}
          onCreate={createPublication}
          onUpdate={updatePublication}
          onDelete={deletePublication}
        />
      )}

      {activeSubTab === 'teaching' && (
        <TeachingExperienceSection
          experiences={experiences}
          onSaveExperiences={saveTeaching}
        />
      )}

      {activeSubTab === 'memberships' && (
        <MembershipCard
          memberships={memberships}
          onSaveMembership={saveMembership}
          onDeleteMembership={deleteMembership}
        />
      )}

      {activeSubTab === 'awards' && (
        <AwardCard
          awards={awards}
          onSaveAward={saveAward}
          onDeleteAward={deleteAward}
        />
      )}

      {activeSubTab === 'seminars' && (
        <TrainingCard
          seminars={seminars}
          onSaveSeminar={saveSeminar}
          onDeleteSeminar={deleteSeminar}
        />
      )}

      {activeSubTab === 'timeline' && (
        <ExperienceTimeline timeline={timeline} />
      )}
    </div>
  );
};
