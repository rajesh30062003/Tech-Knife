import React, { useState } from 'react';
import {
  BookOpen,
  Award,
  Users,
  DollarSign,
  GraduationCap,
  ExternalLink,
  Plus,
  Tag,
  Globe,
  Sparkles,
  CheckCircle2,
  Bookmark,
  Briefcase,
  TrendingUp,
} from 'lucide-react';
import { ResearchProfile, ResearchProject, ResearchGrant, ResearchSupervision } from '../../../types/faculty';

interface ResearchSummaryCardProps {
  profile: ResearchProfile;
  onUpdateProfile?: (updated: Partial<ResearchProfile>) => Promise<void>;
}

export const ResearchSummaryCard: React.FC<ResearchSummaryCardProps> = ({
  profile,
  onUpdateProfile,
}) => {
  const [newInterest, setNewInterest] = useState('');
  const [activeTab, setActiveTab] = useState<'overview' | 'projects' | 'grants' | 'supervision' | 'collaborators'>('overview');
  const [isEditingMetrics, setIsEditingMetrics] = useState(false);
  const [metricsForm, setMetricsForm] = useState({
    orcidId: profile.orcidId || '',
    scopusId: profile.scopusId || '',
    googleScholarUrl: profile.googleScholarUrl || '',
    researchGateUrl: profile.researchGateUrl || '',
    totalCitations: profile.totalCitations || 0,
    hIndex: profile.hIndex || 0,
    i10Index: profile.i10Index || 0,
  });

  const handleAddInterest = async () => {
    if (!newInterest.trim() || !onUpdateProfile) return;
    const updatedInterests = [...profile.researchInterests, newInterest.trim()];
    await onUpdateProfile({ researchInterests: updatedInterests });
    setNewInterest('');
  };

  const handleRemoveInterest = async (index: number) => {
    if (!onUpdateProfile) return;
    const updatedInterests = profile.researchInterests.filter((_, i) => i !== index);
    await onUpdateProfile({ researchInterests: updatedInterests });
  };

  const handleSaveMetrics = async () => {
    if (!onUpdateProfile) return;
    await onUpdateProfile({
      orcidId: metricsForm.orcidId,
      scopusId: metricsForm.scopusId,
      googleScholarUrl: metricsForm.googleScholarUrl,
      researchGateUrl: metricsForm.researchGateUrl,
      totalCitations: Number(metricsForm.totalCitations),
      hIndex: Number(metricsForm.hIndex),
      i10Index: Number(metricsForm.i10Index),
    });
    setIsEditingMetrics(false);
  };

  const totalGrantsAmount = profile.grants.reduce((sum, g) => sum + g.amount, 0);

  return (
    <div className="space-y-6">
      {/* Top Academic Citation Metrics Banner */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-gradient-to-br from-indigo-900 to-slate-900 text-white rounded-2xl p-5 shadow-lg border border-indigo-800/50 relative overflow-hidden">
          <div className="absolute top-2 right-2 p-2 bg-indigo-500/20 rounded-xl text-indigo-300">
            <BookOpen className="w-5 h-5" />
          </div>
          <p className="text-xs font-semibold text-indigo-300 uppercase tracking-wider">Total Citations</p>
          <p className="text-2xl sm:text-3xl font-black mt-1 text-white">{profile.totalCitations.toLocaleString()}</p>
          <div className="flex items-center gap-1 text-[11px] text-emerald-400 mt-2 font-medium">
            <TrendingUp className="w-3.5 h-3.5" /> Google Scholar Verified
          </div>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm relative">
          <div className="absolute top-2 right-2 p-2 bg-amber-500/10 text-amber-600 rounded-xl">
            <Award className="w-5 h-5" />
          </div>
          <p className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">h-Index</p>
          <p className="text-2xl sm:text-3xl font-black mt-1 text-slate-900 dark:text-white">{profile.hIndex}</p>
          <p className="text-[11px] text-slate-500 mt-2">Impact Index Factor</p>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm relative">
          <div className="absolute top-2 right-2 p-2 bg-blue-500/10 text-blue-600 rounded-xl">
            <Sparkles className="w-5 h-5" />
          </div>
          <p className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">i10-Index</p>
          <p className="text-2xl sm:text-3xl font-black mt-1 text-slate-900 dark:text-white">{profile.i10Index}</p>
          <p className="text-[11px] text-slate-500 mt-2">Papers with &ge;10 citations</p>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm relative">
          <div className="absolute top-2 right-2 p-2 bg-emerald-500/10 text-emerald-600 rounded-xl">
            <DollarSign className="w-5 h-5" />
          </div>
          <p className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">Research Grants</p>
          <p className="text-2xl sm:text-3xl font-black mt-1 text-emerald-600 dark:text-emerald-400">
            ${(totalGrantsAmount / 1000).toFixed(0)}k
          </p>
          <p className="text-[11px] text-slate-500 mt-2">{profile.grants.length} Active Grants Sanctioned</p>
        </div>
      </div>

      {/* External Academic Identifiers & Quick Links */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm flex flex-wrap items-center justify-between gap-4">
        <div className="flex flex-wrap items-center gap-3 text-xs">
          <span className="font-bold text-slate-700 dark:text-slate-300 uppercase tracking-wider text-[11px]">
            Academic IDs:
          </span>
          {profile.orcidId && (
            <a
              href={`https://orcid.org/${profile.orcidId}`}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 rounded-xl font-mono font-bold hover:bg-emerald-500/20 transition-all"
            >
              <Globe className="w-3.5 h-3.5" /> ORCID: {profile.orcidId}
              <ExternalLink className="w-3 h-3 ml-0.5" />
            </a>
          )}
          {profile.scopusId && (
            <span className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-blue-500/10 text-blue-600 dark:text-blue-400 border border-blue-500/20 rounded-xl font-mono font-bold">
              Scopus: {profile.scopusId}
            </span>
          )}
          {profile.googleScholarUrl && (
            <a
              href={profile.googleScholarUrl}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20 rounded-xl font-bold hover:bg-amber-500/20 transition-all"
            >
              Google Scholar Profile
              <ExternalLink className="w-3 h-3" />
            </a>
          )}
          {profile.researchGateUrl && (
            <a
              href={profile.researchGateUrl}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20 rounded-xl font-bold hover:bg-indigo-500/20 transition-all"
            >
              ResearchGate
              <ExternalLink className="w-3 h-3" />
            </a>
          )}
        </div>

        <button
          onClick={() => setIsEditingMetrics(!isEditingMetrics)}
          className="text-xs font-bold text-indigo-600 dark:text-indigo-400 hover:underline"
        >
          {isEditingMetrics ? 'Cancel Edit' : 'Edit Identifiers & Indices'}
        </button>
      </div>

      {/* Edit Identifiers Form Modal Drawer */}
      {isEditingMetrics && (
        <div className="bg-slate-50 dark:bg-slate-800/60 border border-indigo-500/30 rounded-2xl p-5 space-y-4">
          <h4 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <Bookmark className="w-4 h-4 text-indigo-500" /> Update Academic Identifiers & Indices
          </h4>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 text-xs">
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-bold mb-1">ORCID ID</label>
              <input
                type="text"
                value={metricsForm.orcidId}
                onChange={(e) => setMetricsForm({ ...metricsForm, orcidId: e.target.value })}
                className="w-full bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-xl p-2 font-mono text-xs"
                placeholder="0000-0000-0000-0000"
              />
            </div>
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-bold mb-1">Scopus ID</label>
              <input
                type="text"
                value={metricsForm.scopusId}
                onChange={(e) => setMetricsForm({ ...metricsForm, scopusId: e.target.value })}
                className="w-full bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-xl p-2 font-mono text-xs"
                placeholder="57200000000"
              />
            </div>
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-bold mb-1">Total Citations</label>
              <input
                type="number"
                value={metricsForm.totalCitations}
                onChange={(e) => setMetricsForm({ ...metricsForm, totalCitations: Number(e.target.value) })}
                className="w-full bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-xl p-2 font-mono text-xs"
              />
            </div>
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-bold mb-1">h-Index / i10-Index</label>
              <div className="flex gap-2">
                <input
                  type="number"
                  value={metricsForm.hIndex}
                  onChange={(e) => setMetricsForm({ ...metricsForm, hIndex: Number(e.target.value) })}
                  className="w-1/2 bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-xl p-2 font-mono text-xs"
                  placeholder="h-Index"
                />
                <input
                  type="number"
                  value={metricsForm.i10Index}
                  onChange={(e) => setMetricsForm({ ...metricsForm, i10Index: Number(e.target.value) })}
                  className="w-1/2 bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-xl p-2 font-mono text-xs"
                  placeholder="i10-Index"
                />
              </div>
            </div>
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <button
              onClick={() => setIsEditingMetrics(false)}
              className="px-4 py-2 text-xs font-bold text-slate-500 hover:text-slate-700 dark:hover:text-slate-300"
            >
              Cancel
            </button>
            <button
              onClick={handleSaveMetrics}
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-xs font-bold shadow-md"
            >
              Save Changes
            </button>
          </div>
        </div>
      )}

      {/* Main Research Tabs Bar */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-6">
        <div className="flex border-b border-slate-200 dark:border-slate-800 gap-2 overflow-x-auto no-scrollbar pb-1">
          <button
            onClick={() => setActiveTab('overview')}
            className={`px-4 py-2 rounded-xl text-xs font-extrabold transition-all whitespace-nowrap ${
              activeTab === 'overview'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            Interests & Domains
          </button>
          <button
            onClick={() => setActiveTab('projects')}
            className={`px-4 py-2 rounded-xl text-xs font-extrabold transition-all whitespace-nowrap ${
              activeTab === 'projects'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            Research Projects ({profile.projects.length})
          </button>
          <button
            onClick={() => setActiveTab('grants')}
            className={`px-4 py-2 rounded-xl text-xs font-extrabold transition-all whitespace-nowrap ${
              activeTab === 'grants'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            Sponsored Grants ({profile.grants.length})
          </button>
          <button
            onClick={() => setActiveTab('supervision')}
            className={`px-4 py-2 rounded-xl text-xs font-extrabold transition-all whitespace-nowrap ${
              activeTab === 'supervision'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            Research Supervision ({profile.supervisions.length})
          </button>
          <button
            onClick={() => setActiveTab('collaborators')}
            className={`px-4 py-2 rounded-xl text-xs font-extrabold transition-all whitespace-nowrap ${
              activeTab === 'collaborators'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            Collaborators ({profile.collaborators.length})
          </button>
        </div>

        {/* Tab 1: Overview - Research Interests & Areas */}
        {activeTab === 'overview' && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Research Interests */}
            <div className="space-y-4">
              <h3 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <Tag className="w-4 h-4 text-indigo-500" /> Primary Research Interests
              </h3>

              <div className="flex flex-wrap gap-2">
                {profile.researchInterests.map((interest, index) => (
                  <span
                    key={index}
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20 rounded-2xl text-xs font-semibold"
                  >
                    {interest}
                    <button
                      onClick={() => handleRemoveInterest(index)}
                      className="hover:text-red-500 font-bold text-xs ml-1"
                      title="Remove"
                    >
                      &times;
                    </button>
                  </span>
                ))}
              </div>

              {onUpdateProfile && (
                <div className="flex gap-2 pt-2">
                  <input
                    type="text"
                    value={newInterest}
                    onChange={(e) => setNewInterest(e.target.value)}
                    placeholder="Add new research interest..."
                    className="flex-1 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3 py-1.5 text-xs focus:ring-2 focus:ring-indigo-500"
                  />
                  <button
                    onClick={handleAddInterest}
                    className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-xs font-bold flex items-center gap-1"
                  >
                    <Plus className="w-3.5 h-3.5" /> Add
                  </button>
                </div>
              )}
            </div>

            {/* Research Areas */}
            <div className="space-y-4">
              <h3 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <Bookmark className="w-4 h-4 text-emerald-500" /> Specialized Research Domains
              </h3>
              <ul className="space-y-2.5">
                {profile.researchAreas.map((area, idx) => (
                  <li
                    key={idx}
                    className="flex items-center gap-2.5 p-3 bg-slate-50 dark:bg-slate-800/50 rounded-xl border border-slate-200 dark:border-slate-800 text-xs font-medium text-slate-700 dark:text-slate-300"
                  >
                    <CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0" />
                    <span>{area}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}

        {/* Tab 2: Projects */}
        {activeTab === 'projects' && (
          <div className="space-y-4">
            {profile.projects.map((proj: ResearchProject) => (
              <div
                key={proj.id}
                className="p-5 bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-2"
              >
                <div className="flex flex-wrap items-start justify-between gap-2">
                  <h4 className="text-sm font-bold text-slate-900 dark:text-white">{proj.title}</h4>
                  <span
                    className={`px-2.5 py-0.5 text-[10px] font-bold uppercase rounded-full ${
                      proj.status === 'Ongoing'
                        ? 'bg-emerald-500/10 text-emerald-600 border border-emerald-500/20'
                        : proj.status === 'Completed'
                        ? 'bg-blue-500/10 text-blue-600 border border-blue-500/20'
                        : 'bg-amber-500/10 text-amber-600 border border-amber-500/20'
                    }`}
                  >
                    {proj.status}
                  </span>
                </div>
                <p className="text-xs text-slate-600 dark:text-slate-400">{proj.description}</p>
                <div className="flex flex-wrap items-center gap-4 text-xs text-slate-500 pt-2 font-medium">
                  <span>Role: <strong className="text-slate-700 dark:text-slate-300">{proj.role}</strong></span>
                  {proj.fundingAgency && (
                    <span>Sponsor: <strong className="text-slate-700 dark:text-slate-300">{proj.fundingAgency}</strong></span>
                  )}
                  {proj.grantAmount && (
                    <span className="text-emerald-600 font-bold">
                      Grant: ${proj.grantAmount.toLocaleString()}
                    </span>
                  )}
                  <span>Duration: {proj.startDate} to {proj.endDate || 'Present'}</span>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Tab 3: Grants */}
        {activeTab === 'grants' && (
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {profile.grants.map((grant: ResearchGrant) => (
                <div
                  key={grant.id}
                  className="p-4 bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-2"
                >
                  <div className="flex items-center justify-between">
                    <span className="text-[11px] font-mono text-slate-400">Ref: {grant.grantNumber}</span>
                    <span
                      className={`px-2 py-0.5 text-[10px] font-bold rounded-full ${
                        grant.status === 'In Progress'
                          ? 'bg-emerald-500/10 text-emerald-600'
                          : 'bg-slate-500/10 text-slate-500'
                      }`}
                    >
                      {grant.status}
                    </span>
                  </div>
                  <h4 className="text-xs font-extrabold text-slate-900 dark:text-white">{grant.grantName}</h4>
                  <p className="text-xs text-slate-500">{grant.sponsoringAgency}</p>
                  <div className="flex items-center justify-between text-xs pt-2 font-bold">
                    <span className="text-emerald-600 dark:text-emerald-400">${grant.amount.toLocaleString()}</span>
                    <span className="text-slate-400">{grant.startYear} &ndash; {grant.endYear}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Tab 4: Supervision */}
        {activeTab === 'supervision' && (
          <div className="space-y-3">
            {profile.supervisions.map((sup: ResearchSupervision) => (
              <div
                key={sup.id}
                className="p-4 bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 rounded-2xl flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3"
              >
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <GraduationCap className="w-4 h-4 text-indigo-500" />
                    <span className="text-xs font-bold text-slate-900 dark:text-white">{sup.studentName}</span>
                    <span className="px-2 py-0.5 text-[10px] font-mono font-bold bg-indigo-500/10 text-indigo-600 rounded-md">
                      {sup.degree}
                    </span>
                  </div>
                  <p className="text-xs italic text-slate-600 dark:text-slate-400">&ldquo;{sup.thesisTitle}&rdquo;</p>
                </div>

                <div className="flex items-center gap-3 shrink-0">
                  <span className="text-xs font-mono text-slate-400">{sup.year}</span>
                  <span
                    className={`px-2.5 py-0.5 text-[10px] font-bold rounded-full ${
                      sup.status === 'Awarded'
                        ? 'bg-emerald-500/10 text-emerald-500 border border-emerald-500/20'
                        : 'bg-amber-500/10 text-amber-500 border border-amber-500/20'
                    }`}
                  >
                    {sup.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Tab 5: Collaborators */}
        {activeTab === 'collaborators' && (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {profile.collaborators.map((c, idx) => (
              <div
                key={idx}
                className="p-4 bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 rounded-2xl flex items-center gap-3"
              >
                <div className="p-2.5 bg-indigo-500/10 text-indigo-600 rounded-xl">
                  <Users className="w-4 h-4" />
                </div>
                <div>
                  <h4 className="text-xs font-bold text-slate-900 dark:text-white">{c.name}</h4>
                  <p className="text-[11px] text-slate-500">{c.institution}</p>
                  <p className="text-[10px] text-indigo-500 font-semibold mt-0.5">{c.country}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
