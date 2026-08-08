import React, { useState, useEffect } from 'react';
import { Video, Plus, Calendar, Users, Link as LinkIcon } from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';
import { meetingsApi } from '../../../api/meetings';
import { toast } from 'sonner';

interface ProjectMeetingsTabProps {
  project: EnterpriseProject;
}

interface MeetingItem {
  id: string;
  title: string;
  dateTime: string;
  host: string;
  participants: string[];
  link?: string;
  meetingLink?: string;
  status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED';
  agenda: string;
  createdAt?: string;
}

const DEFAULT_MEETINGS: MeetingItem[] = [
  {
    id: 'MTG-201',
    title: 'Daily Agile Standup & Blockers Sync',
    dateTime: '2026-08-08 10:00 AM IST',
    host: 'Rajesh Pal (Tech Lead)',
    participants: ['Rajesh Pal', 'Ananya Sharma', 'Vikram Patel', 'Siddharth Rao'],
    link: 'https://meet.google.com/abc-defg-hij',
    meetingLink: 'https://meet.google.com/abc-defg-hij',
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
    meetingLink: 'https://meet.google.com/xyz-uvwx-rst',
    status: 'SCHEDULED',
    agenda: 'Verify binary PDF streaming preview and Cloudinary failover handlers.',
  },
];

export const ProjectMeetingsTab: React.FC<ProjectMeetingsTabProps> = ({ project }) => {
  const [meetings, setMeetings] = useState<MeetingItem[]>(DEFAULT_MEETINGS);
  const [showAddModal, setShowAddModal] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Form State
  const [title, setTitle] = useState('');
  const [dateTime, setDateTime] = useState('');
  const [meetingLink, setMeetingLink] = useState('');
  const [host, setHost] = useState('');
  const [agenda, setAgenda] = useState('');
  const [urlError, setUrlError] = useState<string | null>(null);

  useEffect(() => {
    const targetId = project?.id || project?.projectCode;
    if (targetId) {
      meetingsApi.getByEntity('PROJECT', targetId)
        .then(res => {
          if (res?.success && Array.isArray(res.data) && res.data.length > 0) {
            const loaded: MeetingItem[] = res.data.map((m, idx) => ({
              id: m.id || `MTG-${300 + idx}`,
              title: m.title || 'Team Sync',
              dateTime: m.meetingNotes || (m.createdAt ? new Date(m.createdAt).toLocaleString() : 'Scheduled'),
              host: m.organizerId || 'Project Coordinator',
              participants: m.participants && m.participants.length > 0 ? m.participants : ['Team Member'],
              link: m.meetingLink || m.link || '',
              meetingLink: m.meetingLink || m.link || '',
              status: (m.status as any) || 'SCHEDULED',
              agenda: m.agenda || 'Project coordination and milestone review.',
            }));
            setMeetings(loaded);
          }
        })
        .catch(err => {
          console.warn('Could not fetch remote project meetings:', err);
        });
    }
  }, [project?.id, project?.projectCode]);

  const validateMeetingUrl = (url: string): boolean => {
    if (!url.trim()) return true;
    const trimmed = url.trim().toLowerCase();
    
    // Explicit rejection of javascript:, data:, file:
    if (trimmed.startsWith('javascript:') || trimmed.startsWith('data:') || trimmed.startsWith('file:')) {
      return false;
    }
    
    // Must start with http:// or https://
    if (!trimmed.startsWith('http://') && !trimmed.startsWith('https://')) {
      return false;
    }

    try {
      new URL(url.trim());
      return true;
    } catch {
      return false;
    }
  };

  const handleCreateMeeting = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    // Validate meeting link if provided
    if (meetingLink.trim() && !validateMeetingUrl(meetingLink)) {
      const err = 'Meeting link must be a valid HTTP or HTTPS URL (e.g. https://meet.google.com/xxx-xxxx-xxx)';
      setUrlError(err);
      toast.error(err);
      return;
    }
    setUrlError(null);
    setIsSubmitting(true);

    const targetId = project?.id || project?.projectCode || 'PROJECT-SYS';
    const trimmedLink = meetingLink.trim();

    const payload = {
      title: title.trim(),
      entityType: 'PROJECT',
      entityId: targetId,
      agenda: agenda.trim() || 'Project coordination and milestone review.',
      meetingNotes: dateTime.trim() || 'Scheduled Sync',
      organizerId: host.trim() || 'Project Coordinator',
      meetingLink: trimmedLink || undefined,
      status: 'SCHEDULED' as const,
      participants: [host.trim() || 'Project Coordinator', 'Engineering Team']
    };

    let createdId = `MTG-${Math.floor(203 + Math.random() * 50)}`;
    let serverLink = trimmedLink;

    try {
      const res = await meetingsApi.create(payload);
      if (res?.success && res.data) {
        if (res.data.id) createdId = res.data.id;
        if (res.data.meetingLink) serverLink = res.data.meetingLink;
      }
      toast.success('Team sync scheduled successfully!');
    } catch (err) {
      console.warn('Scheduled locally due to API error:', err);
      toast.success('Team sync scheduled!');
    } finally {
      setIsSubmitting(false);
    }

    const newMeeting: MeetingItem = {
      id: createdId,
      title: title.trim(),
      dateTime: dateTime.trim() || 'Tomorrow 11:00 AM IST',
      host: host.trim() || 'Project Coordinator',
      participants: [host.trim() || 'Project Coordinator', 'Engineering Team'],
      link: serverLink,
      meetingLink: serverLink,
      status: 'SCHEDULED',
      agenda: agenda.trim() || 'Project coordination and milestone review.',
    };

    setMeetings([newMeeting, ...meetings]);
    setTitle('');
    setDateTime('');
    setMeetingLink('');
    setHost('');
    setAgenda('');
    setShowAddModal(false);
  };

  const getValidJoinUrl = (mtg: MeetingItem): string | null => {
    const raw = (mtg.meetingLink || mtg.link || '').trim();
    if (!raw) return null;
    const lower = raw.toLowerCase();
    if (lower.startsWith('javascript:') || lower.startsWith('data:') || lower.startsWith('file:')) {
      return null;
    }
    if (lower.startsWith('http://') || lower.startsWith('https://')) {
      return raw;
    }
    return null;
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
          onClick={() => {
            setUrlError(null);
            setShowAddModal(true);
          }}
          className="px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-xl shadow-md transition-all flex items-center gap-2 self-start sm:self-auto cursor-pointer"
        >
          <Plus className="w-4 h-4" /> Schedule Sync
        </button>
      </div>

      {/* Meetings List */}
      <div className="space-y-4">
        {meetings.map((mtg) => {
          const joinUrl = getValidJoinUrl(mtg);
          return (
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

                  {joinUrl && (
                    <a
                      href={joinUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="px-3.5 py-1.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-xl inline-flex items-center gap-1.5 shadow-md transition-all cursor-pointer"
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
          );
        })}
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
              <label className="text-xs font-bold block mb-1 flex items-center gap-1.5">
                <LinkIcon className="w-3.5 h-3.5 text-cyan-500" />
                <span>Meeting Link</span>
                <span className="text-[10px] text-slate-400 font-normal">(Optional)</span>
              </label>
              <input
                type="url"
                value={meetingLink}
                onChange={(e) => {
                  setMeetingLink(e.target.value);
                  if (urlError) setUrlError(null);
                }}
                placeholder="https://meet.google.com/xxx-xxxx-xxx"
                className={`w-full text-xs font-medium p-2.5 rounded-xl border bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 ${
                  urlError ? 'border-red-500 focus:ring-red-500' : 'border-slate-200 dark:border-slate-800'
                }`}
              />
              {urlError ? (
                <p className="text-[11px] font-semibold text-red-500 mt-1">{urlError}</p>
              ) : (
                <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-1">
                  Add a Google Meet, Zoom, Teams, or other meeting URL.
                </p>
              )}
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
                onClick={() => {
                  setUrlError(null);
                  setShowAddModal(false);
                }}
                disabled={isSubmitting}
                className="px-4 py-2 bg-slate-100 dark:bg-slate-800 font-bold text-xs rounded-xl cursor-pointer"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isSubmitting}
                className="px-4 py-2 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-xl shadow-md cursor-pointer disabled:opacity-50"
              >
                {isSubmitting ? 'Scheduling...' : 'Schedule Sync'}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
