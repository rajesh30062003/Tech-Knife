import React, { useState } from 'react';
import { Video, Clock, Users, ExternalLink, Copy, Check, ChevronRight, Calendar } from 'lucide-react';
import { Link } from 'react-router-dom';

interface MeetingItem {
  id: string;
  title: string;
  time: string;
  duration: string;
  host: string;
  hostAvatar: string;
  link: string;
  attendeesCount: number;
  status: 'Starting Soon' | 'Scheduled' | 'Completed';
}

const MEETINGS_LIST: MeetingItem[] = [
  {
    id: 'mtg-1',
    title: 'Daily Engineering & Sprint Standup',
    time: '10:00 AM - 10:30 AM',
    duration: '30m',
    host: 'Rahul Garai (System Developer)',
    hostAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=150',
    link: 'https://meet.google.com/abc-defg-hij',
    attendeesCount: 8,
    status: 'Starting Soon',
  },
  {
    id: 'mtg-2',
    title: 'Cloud Architecture & DB Security Review',
    time: '02:00 PM - 03:00 PM',
    duration: '1h',
    host: 'Ganesh Pal (Sr. Developer)',
    hostAvatar: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=150',
    link: 'https://meet.google.com/xyz-uvwx-rst',
    attendeesCount: 5,
    status: 'Scheduled',
  },
  {
    id: 'mtg-3',
    title: 'Bi-weekly 1-on-1 Sync with Manager',
    time: '04:30 PM - 05:00 PM',
    duration: '30m',
    host: 'Sourav Roy',
    hostAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=150',
    link: 'https://meet.google.com/one-on-one',
    attendeesCount: 2,
    status: 'Scheduled',
  },
];

export const MeetingsWidget: React.FC = () => {
  const [copiedId, setCopiedId] = useState<string | null>(null);

  const handleCopyLink = (id: string, link: string) => {
    navigator.clipboard.writeText(link);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-4 flex flex-col justify-between h-full transition-all hover:border-indigo-500/30">
      {/* Widget Header */}
      <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-cyan-500/10 text-cyan-600 dark:text-cyan-400">
            <Video className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Today's Meetings Agenda</h3>
            <p className="text-[11px] text-slate-500">Syncs, Architecture Reviews & Standups</p>
          </div>
        </div>

        <Link
          to="/calendar"
          className="text-xs font-bold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center gap-1"
        >
          Calendar <ChevronRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      {/* Meetings List */}
      <div className="space-y-3 flex-1 overflow-y-auto max-h-[300px] pr-1">
        {MEETINGS_LIST.map((m) => (
          <div
            key={m.id}
            className={`p-4 rounded-2xl border transition-all space-y-3 ${
              m.status === 'Starting Soon'
                ? 'bg-cyan-50/40 dark:bg-cyan-950/20 border-cyan-500/40 shadow-sm'
                : 'bg-slate-50 dark:bg-slate-950/60 border-slate-200/80 dark:border-slate-800'
            }`}
          >
            <div className="flex items-start justify-between gap-3">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <h4 className="text-xs font-extrabold text-slate-900 dark:text-white leading-tight">
                    {m.title}
                  </h4>
                  {m.status === 'Starting Soon' && (
                    <span className="px-2 py-0.5 bg-rose-500 text-white font-mono font-bold text-[9px] rounded-full animate-pulse shrink-0">
                      In 25 Mins
                    </span>
                  )}
                </div>

                <div className="flex items-center gap-3 text-[11px] font-mono text-slate-500">
                  <span className="flex items-center gap-1 text-slate-700 dark:text-slate-300 font-bold">
                    <Clock className="w-3 h-3 text-cyan-500" /> {m.time}
                  </span>
                  <span>•</span>
                  <span>{m.duration}</span>
                </div>
              </div>
            </div>

            {/* Host & Actions */}
            <div className="flex items-center justify-between text-xs pt-1 border-t border-slate-200/50 dark:border-slate-800/80">
              <div className="flex items-center gap-2">
                <img src={m.hostAvatar} alt={m.host} className="w-5 h-5 rounded-full object-cover" />
                <span className="text-[11px] font-medium text-slate-600 dark:text-slate-400 truncate max-w-[140px]">
                  {m.host}
                </span>
              </div>

              <div className="flex items-center gap-1.5 shrink-0">
                <button
                  onClick={() => handleCopyLink(m.id, m.link)}
                  className="p-1.5 bg-slate-200/60 dark:bg-slate-800 text-slate-600 dark:text-slate-300 rounded-lg hover:bg-slate-300 transition-colors"
                  title="Copy Video Link"
                >
                  {copiedId === m.id ? <Check className="w-3 h-3 text-emerald-500" /> : <Copy className="w-3 h-3" />}
                </button>

                <a
                  href={m.link}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="px-3 py-1 bg-cyan-600 hover:bg-cyan-500 text-white font-bold text-[11px] rounded-lg shadow flex items-center gap-1 transition-colors"
                >
                  Join <ExternalLink className="w-3 h-3" />
                </a>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
