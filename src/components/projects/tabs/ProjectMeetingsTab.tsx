import React, { useState } from 'react';
import { Video, Plus, Calendar, Clock, Users, ExternalLink, CheckCircle2 } from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';

interface ProjectMeetingsTabProps {
  project: EnterpriseProject;
}

interface MeetingItem {
  id: string;
  title: string;
  dateTime: string;
  host: string;
  participants: string[];
  link: string;
  status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED';
  agenda: string;
}

const DEFAULT_MEETINGS: MeetingItem[] = [
  {
    id: 'MTG-201',
    title: 'Daily Agile Standup & Blockers Sync',
    dateTime: '2026-08-08 10:00 AM IST',
    host: 'Rajesh Pal (Tech Lead)',
    participants: ['Rajesh Pal', 'Ananya Sharma', 'Vikram Patel', 'Siddharth Rao'],
    link: 'https://meet.google.com/abc-defg-hij',
    status: 'COMPLETED',
    agenda: 'Daily progress check on Google OAuth token refresh & Task status API updates.',
  },
  {
    id: 'MTG-202',
    title: 'Architecture & Drive Stream Review',
    dateTime: '2026-08-09 02:30 PM IST',
    host: 'Security Governance',
    participants: ['Rajesh Pal', 'DevOps Lead', 'Infra Team'],
    link: 'https://meet.google.com/xyz-uvwx-rst',
    status: 'SCHEDULED',
    agenda: 'Verify binary PDF streaming preview and Cloudinary failover handlers.',
  },
];

export const ProjectMeetingsTab: React.FC<ProjectMeetingsTabProps> = ({ project }) => {
  const [meetings, setMeetings] = useState<MeetingItem[]>(DEFAULT_MEETINGS);
  const [showAddModal, setShowAddModal] = useState(false);

  // Form State
  const [title, setTitle] = useState('');
  const [dateTime, setDateTime] = useState('');
  const [host, setHost] = useState('');
  const [agenda, setAgenda] = useState('');

  const handleCreateMeeting = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    const newMeeting: MeetingItem = {
      id: `MTG-${Math.floor(203 + Math.random() * 50)}`,
      title: title.trim(),
      dateTime: dateTime || 'Tomorrow 11:00 AM IST',
      host: host.trim() || 'Project Coordinator',
      participants: [host.trim() || 'Project Coordinator', 'Engineering Team'],
      link: 'https://meet.google.com/tech-knife-sync',
      status: 'SCHEDULED',
      agenda: agenda.trim() || 'Project coordination and milestone review.',
    };

    setMeetings([newMeeting, ...meetings]);
    setTitle('');
    setDateTime('');
    setHost('');
    setAgenda('');
    setShowAddModal(false);
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-cyan-600 dark:text-cyan-400 mb-1">
            <Video className="w-4 h-4" />
            <span>Team Collaboration & Meetings</span>
          </div>
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
            Meetings & Syncs ({meetings.length})
          </h3>
          <p className="text-xs text-slate-500">Schedule standups, design reviews, and client demo sessions</p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-xl shadow-md transition-all flex items-center gap-2 self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" /> Schedule Sync
        </button>
      </div>

      {/* Meetings List */}
      <div className="space-y-4">
        {meetings.map((mtg) => (
          <div
            key={mtg.id}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4"
          >
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 font-mono text-[10px] font-bold rounded-md">
                  {mtg.id}
                </span>
                <h4 className="text-base font-extrabold text-slate-900 dark:text-white">{mtg.title}</h4>
              </div>

              <div className="flex items-center gap-2">
                <span
                  className={`px-3 py-1 rounded-full text-xs font-bold ${
                    mtg.status === 'COMPLETED'
                      ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300'
                      : 'bg-cyan-100 text-cyan-800 dark:bg-cyan-950 dark:text-cyan-300'
                  }`}
                >
                  {mtg.status}
                </span>

                {mtg.link && (
                  <a
                    href={mtg.link}
                    target="_blank"
                    rel="noreferrer"
                    className="px-3 py-1 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl inline-flex items-center gap-1.5 shadow-xs"
                  >
                    <Video className="w-3.5 h-3.5" /> Join Call
                  </a>
                )}
              </div>
            </div>

            <div className="flex flex-wrap items-center gap-4 text-xs font-medium text-slate-500">
              <span className="flex items-center gap-1.5"><Calendar className="w-3.5 h-3.5 text-cyan-500" /> <strong className="text-slate-800 dark:text-slate-200">{mtg.dateTime}</strong></span>
              <span className="flex items-center gap-1.5"><Users className="w-3.5 h-3.5 text-slate-400" /> Host: <strong className="text-slate-800 dark:text-slate-200">{mtg.host}</strong></span>
            </div>

            {mtg.agenda && (
              <p className="text-xs text-slate-600 dark:text-slate-300 font-medium bg-slate-50 dark:bg-slate-800/50 p-3 rounded-2xl border border-slate-200/60 dark:border-slate-800">
                <strong className="text-slate-900 dark:text-white">Agenda:</strong> {mtg.agenda}
              </p>
            )}

            {mtg.participants && (
              <div className="flex items-center gap-1.5 pt-1">
                <span className="text-[10px] font-bold uppercase text-slate-400 mr-1">Attendees:</span>
                {mtg.participants.map((p, idx) => (
                  <span key={idx} className="px-2.5 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 text-[11px] font-bold rounded-lg">
                    {p}
                  </span>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>

      {/* Schedule Meeting Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
          <form onSubmit={handleCreateMeeting} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4 shadow-xl">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Schedule Team Sync</h3>

            <div>
              <label className="text-xs font-bold block mb-1">Meeting Title *</label>
              <input
                type="text"
                required
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g. Sprint Demo & Retrospective"
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold block mb-1">Date & Time</label>
              <input
                type="text"
                value={dateTime}
                onChange={(e) => setDateTime(e.target.value)}
                placeholder="e.g. Tomorrow 03:00 PM IST"
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold block mb-1">Host / Organizer</label>
              <input
                type="text"
                value={host}
                onChange={(e) => setHost(e.target.value)}
                placeholder="e.g. Tech Lead"
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div>
              <label className="text-xs font-bold block mb-1">Meeting Agenda</label>
              <textarea
                rows={2}
                value={agenda}
                onChange={(e) => setAgenda(e.target.value)}
                placeholder="Key topics to discuss..."
                className="w-full text-xs font-medium p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setShowAddModal(false)}
                className="px-4 py-2 bg-slate-100 dark:bg-slate-800 font-bold text-xs rounded-xl"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-2 bg-cyan-500 text-slate-950 font-black text-xs rounded-xl shadow-md"
              >
                Schedule Sync
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
