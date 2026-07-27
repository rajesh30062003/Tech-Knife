import React, { useState } from 'react';
import { Award, Plus, Trash2, Edit2, FileText, Image as ImageIcon, ExternalLink } from 'lucide-react';
import { AwardAchievement } from '../../../types/faculty';

interface AwardCardProps {
  awards: AwardAchievement[];
  onSaveAward: (item: AwardAchievement) => Promise<AwardAchievement>;
  onDeleteAward: (id: string) => Promise<void>;
}

export const AwardCard: React.FC<AwardCardProps> = ({ awards, onSaveAward, onDeleteAward }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<AwardAchievement | null>(null);

  const [form, setForm] = useState<{
    awardTitle: string;
    awardingOrganization: string;
    year: number;
    description: string;
    certificateUrl: string;
    imageUrl: string;
  }>({
    awardTitle: '',
    awardingOrganization: '',
    year: new Date().getFullYear(),
    description: '',
    certificateUrl: '',
    imageUrl: '',
  });

  const handleOpenAdd = () => {
    setEditingItem(null);
    setForm({
      awardTitle: '',
      awardingOrganization: '',
      year: new Date().getFullYear(),
      description: '',
      certificateUrl: '',
      imageUrl: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (item: AwardAchievement) => {
    setEditingItem(item);
    setForm({
      awardTitle: item.awardTitle,
      awardingOrganization: item.awardingOrganization,
      year: item.year,
      description: item.description || '',
      certificateUrl: item.certificateUrl || '',
      imageUrl: item.imageUrl || '',
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.awardTitle || !form.awardingOrganization) return;

    const item: AwardAchievement = {
      id: editingItem ? editingItem.id : `award-${Date.now()}`,
      awardTitle: form.awardTitle,
      awardingOrganization: form.awardingOrganization,
      year: Number(form.year),
      description: form.description || undefined,
      certificateUrl: form.certificateUrl || undefined,
      imageUrl: form.imageUrl || undefined,
    };

    await onSaveAward(item);
    setIsModalOpen(false);
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Delete this award or achievement?')) {
      await onDeleteAward(id);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
        <div>
          <span className="text-[10px] font-bold text-indigo-500 uppercase tracking-wider">
            Honors & Medals
          </span>
          <h2 className="text-xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <Award className="w-5 h-5 text-indigo-500" /> Awards & Achievements
          </h2>
        </div>

        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-2xl text-xs font-bold shadow-md flex items-center gap-1.5 transition-all"
        >
          <Plus className="w-4 h-4" /> Add Honor
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {awards.length === 0 ? (
          <div className="col-span-full py-8 text-center text-slate-500 text-xs">
            No awards recorded yet.
          </div>
        ) : (
          awards.map((award) => (
            <div
              key={award.id}
              className="bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 space-y-3 relative overflow-hidden flex flex-col justify-between"
            >
              {award.imageUrl && (
                <img
                  src={award.imageUrl}
                  alt={award.awardTitle}
                  className="w-full h-28 object-cover rounded-xl mb-2"
                />
              )}

              <div className="space-y-1.5">
                <div className="flex items-center justify-between">
                  <span className="px-2.5 py-0.5 text-[10px] font-mono font-bold rounded-full bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20">
                    Year {award.year}
                  </span>
                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => handleOpenEdit(award)}
                      className="p-1 text-slate-400 hover:text-indigo-600"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => handleDelete(award.id)}
                      className="p-1 text-slate-400 hover:text-red-600"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                <h3 className="text-xs font-extrabold text-slate-900 dark:text-white leading-snug">
                  {award.awardTitle}
                </h3>
                <p className="text-[11px] font-medium text-slate-500">{award.awardingOrganization}</p>
                {award.description && (
                  <p className="text-xs text-slate-600 dark:text-slate-400 pt-1 line-clamp-3">
                    {award.description}
                  </p>
                )}
              </div>

              {award.certificateUrl && (
                <div className="pt-3 border-t border-slate-200 dark:border-slate-700">
                  <a
                    href={award.certificateUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="inline-flex items-center gap-1 text-[11px] font-bold text-indigo-600 dark:text-indigo-400 hover:underline"
                  >
                    <FileText className="w-3.5 h-3.5" /> Award Citation / Certificate
                  </a>
                </div>
              )}
            </div>
          ))
        )}
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-md w-full p-6 shadow-2xl space-y-4 text-xs">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">
              {editingItem ? 'Edit Award' : 'Add Award / Achievement'}
            </h3>

            <form onSubmit={handleSave} className="space-y-3">
              <div>
                <label className="block font-bold mb-1">Award Title *</label>
                <input
                  type="text"
                  required
                  value={form.awardTitle}
                  onChange={(e) => setForm({ ...form, awardTitle: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  placeholder="Excellence in AI Innovation Gold Medal"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Awarding Organization *</label>
                <input
                  type="text"
                  required
                  value={form.awardingOrganization}
                  onChange={(e) => setForm({ ...form, awardingOrganization: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  placeholder="National Academy of Science"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Award Year</label>
                <input
                  type="number"
                  value={form.year}
                  onChange={(e) => setForm({ ...form, year: Number(e.target.value) })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5 font-mono"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Description / Citation</label>
                <textarea
                  rows={2}
                  value={form.description}
                  onChange={(e) => setForm({ ...form, description: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  placeholder="Citation details..."
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Certificate URL</label>
                <input
                  type="text"
                  value={form.certificateUrl}
                  onChange={(e) => setForm({ ...form, certificateUrl: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5 font-mono"
                  placeholder="https://example.com/award.pdf"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Award Banner Image URL</label>
                <input
                  type="text"
                  value={form.imageUrl}
                  onChange={(e) => setForm({ ...form, imageUrl: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5 font-mono"
                  placeholder="https://images.unsplash.com/..."
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
                  Save Honor
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
