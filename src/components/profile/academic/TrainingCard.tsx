import React, { useState } from 'react';
import { Presentation, Plus, Trash2, Edit2, FileText, Calendar, Users, Mic } from 'lucide-react';
import { SeminarWorkshop, SeminarCategory, SeminarRole } from '../../../types/faculty';

interface TrainingCardProps {
  seminars: SeminarWorkshop[];
  onSaveSeminar: (item: SeminarWorkshop) => Promise<SeminarWorkshop>;
  onDeleteSeminar: (id: string) => Promise<void>;
}

export const TrainingCard: React.FC<TrainingCardProps> = ({
  seminars,
  onSaveSeminar,
  onDeleteSeminar,
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<SeminarWorkshop | null>(null);

  const [form, setForm] = useState<{
    title: string;
    category: SeminarCategory;
    role: SeminarRole;
    organization: string;
    date: string;
    certificateUrl: string;
  }>({
    title: '',
    category: 'Conference',
    role: 'Speaker',
    organization: '',
    date: new Date().toISOString().split('T')[0],
    certificateUrl: '',
  });

  const handleOpenAdd = () => {
    setEditingItem(null);
    setForm({
      title: '',
      category: 'Conference',
      role: 'Speaker',
      organization: '',
      date: new Date().toISOString().split('T')[0],
      certificateUrl: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (item: SeminarWorkshop) => {
    setEditingItem(item);
    setForm({
      title: item.title,
      category: item.category,
      role: item.role,
      organization: item.organization,
      date: item.date,
      certificateUrl: item.certificateUrl || '',
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.title || !form.organization) return;

    const item: SeminarWorkshop = {
      id: editingItem ? editingItem.id : `sem-${Date.now()}`,
      title: form.title,
      category: form.category,
      role: form.role,
      organization: form.organization,
      date: form.date,
      certificateUrl: form.certificateUrl || undefined,
    };

    await onSaveSeminar(item);
    setIsModalOpen(false);
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Delete this seminar/workshop entry?')) {
      await onDeleteSeminar(id);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
        <div>
          <span className="text-[10px] font-bold text-indigo-500 uppercase tracking-wider">
            Academic Dissemination & Training
          </span>
          <h2 className="text-xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <Presentation className="w-5 h-5 text-indigo-500" /> Seminars, Workshops & FDPs
          </h2>
        </div>

        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-2xl text-xs font-bold shadow-md flex items-center gap-1.5 transition-all"
        >
          <Plus className="w-4 h-4" /> Add Event / Workshop
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {seminars.length === 0 ? (
          <div className="col-span-full py-8 text-center text-slate-500 text-xs">
            No training or seminar activity recorded yet.
          </div>
        ) : (
          seminars.map((item) => (
            <div
              key={item.id}
              className="bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 space-y-3 relative"
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-indigo-500/10 text-indigo-600 border border-indigo-500/20">
                    {item.category}
                  </span>
                  <span className="px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-emerald-500/10 text-emerald-600 border border-emerald-500/20 flex items-center gap-1">
                    <Mic className="w-3 h-3" /> {item.role}
                  </span>
                </div>

                <div className="flex items-center gap-1">
                  <button onClick={() => handleOpenEdit(item)} className="p-1 text-slate-400 hover:text-indigo-600">
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button onClick={() => handleDelete(item.id)} className="p-1 text-slate-400 hover:text-red-600">
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>

              <h3 className="text-xs font-extrabold text-slate-900 dark:text-white leading-snug">{item.title}</h3>
              <p className="text-[11px] font-medium text-slate-500">{item.organization}</p>

              <div className="flex items-center justify-between text-xs text-slate-400 pt-2 border-t border-slate-200 dark:border-slate-700 font-mono">
                <span>Date: {item.date}</span>
                {item.certificateUrl && (
                  <a
                    href={item.certificateUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="text-indigo-600 dark:text-indigo-400 font-bold hover:underline flex items-center gap-1"
                  >
                    <FileText className="w-3 h-3" /> Certificate
                  </a>
                )}
              </div>
            </div>
          ))
        )}
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 shadow-2xl space-y-4 text-xs">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">
              {editingItem ? 'Edit Event Record' : 'Add Seminar / Workshop / FDP'}
            </h3>

            <form onSubmit={handleSave} className="space-y-3">
              <div>
                <label className="block font-bold mb-1">Title / Event Name *</label>
                <input
                  type="text"
                  required
                  value={form.title}
                  onChange={(e) => setForm({ ...form, title: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  placeholder="Keynote / Hands-on Workshop title..."
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold mb-1">Category</label>
                  <select
                    value={form.category}
                    onChange={(e) => setForm({ ...form, category: e.target.value as SeminarCategory })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  >
                    <option value="Conference">Conference</option>
                    <option value="Workshop">Workshop</option>
                    <option value="FDP">FDP</option>
                    <option value="CME">CME</option>
                    <option value="Seminar">Seminar</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold mb-1">Role</label>
                  <select
                    value={form.role}
                    onChange={(e) => setForm({ ...form, role: e.target.value as SeminarRole })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  >
                    <option value="Speaker">Speaker</option>
                    <option value="Keynote">Keynote</option>
                    <option value="Organizer">Organizer</option>
                    <option value="Participant">Participant</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block font-bold mb-1">Organizing Body / Institution *</label>
                <input
                  type="text"
                  required
                  value={form.organization}
                  onChange={(e) => setForm({ ...form, organization: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  placeholder="IEEE / ACM Chapter / University"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Event Date</label>
                <input
                  type="date"
                  value={form.date}
                  onChange={(e) => setForm({ ...form, date: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5 font-mono"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Certificate URL</label>
                <input
                  type="text"
                  value={form.certificateUrl}
                  onChange={(e) => setForm({ ...form, certificateUrl: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5 font-mono"
                  placeholder="https://example.com/cert.pdf"
                />
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 font-bold text-slate-500 hover:text-slate-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl font-bold shadow-md"
                >
                  Save Event
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
