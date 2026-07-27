import React, { useState } from 'react';
import { ShieldCheck, Plus, Trash2, Edit2, Calendar, FileText, ExternalLink, Award } from 'lucide-react';
import { ProfessionalMembership, MembershipType } from '../../../types/faculty';

interface MembershipCardProps {
  memberships: ProfessionalMembership[];
  onSaveMembership: (item: ProfessionalMembership) => Promise<ProfessionalMembership>;
  onDeleteMembership: (id: string) => Promise<void>;
}

export const MembershipCard: React.FC<MembershipCardProps> = ({
  memberships,
  onSaveMembership,
  onDeleteMembership,
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<ProfessionalMembership | null>(null);

  const [form, setForm] = useState<{
    organization: string;
    membershipNumber: string;
    membershipType: MembershipType;
    validUntil: string;
    certificateUrl: string;
  }>({
    organization: '',
    membershipNumber: '',
    membershipType: 'Senior Member',
    validUntil: '',
    certificateUrl: '',
  });

  const handleOpenAdd = () => {
    setEditingItem(null);
    setForm({
      organization: '',
      membershipNumber: '',
      membershipType: 'Senior Member',
      validUntil: '',
      certificateUrl: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (item: ProfessionalMembership) => {
    setEditingItem(item);
    setForm({
      organization: item.organization,
      membershipNumber: item.membershipNumber,
      membershipType: item.membershipType,
      validUntil: item.validUntil || '',
      certificateUrl: item.certificateUrl || '',
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.organization || !form.membershipNumber) return;

    const item: ProfessionalMembership = {
      id: editingItem ? editingItem.id : `mem-${Date.now()}`,
      organization: form.organization,
      membershipNumber: form.membershipNumber,
      membershipType: form.membershipType,
      validUntil: form.validUntil || undefined,
      certificateUrl: form.certificateUrl || undefined,
    };

    await onSaveMembership(item);
    setIsModalOpen(false);
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Delete this professional membership record?')) {
      await onDeleteMembership(id);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
        <div>
          <span className="text-[10px] font-bold text-indigo-500 uppercase tracking-wider">
            Affiliations & Credential Bodies
          </span>
          <h2 className="text-xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-indigo-500" /> Professional Memberships
          </h2>
        </div>

        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-2xl text-xs font-bold shadow-md flex items-center gap-1.5 transition-all"
        >
          <Plus className="w-4 h-4" /> Add Membership
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {memberships.length === 0 ? (
          <div className="col-span-full py-8 text-center text-slate-500 text-xs">
            No professional memberships recorded yet.
          </div>
        ) : (
          memberships.map((mem) => (
            <div
              key={mem.id}
              className="bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 space-y-3 relative group"
            >
              <div className="flex items-start justify-between gap-2">
                <span className="px-2.5 py-0.5 text-[10px] font-bold font-mono rounded-full bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20">
                  {mem.membershipType}
                </span>
                <div className="flex items-center gap-1">
                  <button
                    onClick={() => handleOpenEdit(mem)}
                    className="p-1 text-slate-400 hover:text-indigo-600"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => handleDelete(mem.id)}
                    className="p-1 text-slate-400 hover:text-red-600"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>

              <h3 className="text-xs font-extrabold text-slate-900 dark:text-white leading-snug">
                {mem.organization}
              </h3>

              <div className="space-y-1 text-xs text-slate-500">
                <p className="font-mono">
                  Member ID: <strong className="text-slate-700 dark:text-slate-300">{mem.membershipNumber}</strong>
                </p>
                {mem.validUntil && (
                  <p className="flex items-center gap-1">
                    <Calendar className="w-3 h-3 text-slate-400" />
                    Valid Until: <strong className="text-slate-700 dark:text-slate-300">{mem.validUntil}</strong>
                  </p>
                )}
              </div>

              {mem.certificateUrl && (
                <div className="pt-2 border-t border-slate-200 dark:border-slate-700">
                  <a
                    href={mem.certificateUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="inline-flex items-center gap-1 text-[11px] font-bold text-indigo-600 dark:text-indigo-400 hover:underline"
                  >
                    <FileText className="w-3.5 h-3.5" /> View Membership Certificate
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
              {editingItem ? 'Edit Membership' : 'Add Professional Membership'}
            </h3>

            <form onSubmit={handleSave} className="space-y-3">
              <div>
                <label className="block font-bold mb-1">Organization Name *</label>
                <input
                  type="text"
                  required
                  value={form.organization}
                  onChange={(e) => setForm({ ...form, organization: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  placeholder="e.g. IEEE, ACM, ISCB"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Membership Number *</label>
                <input
                  type="text"
                  required
                  value={form.membershipNumber}
                  onChange={(e) => setForm({ ...form, membershipNumber: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5 font-mono"
                  placeholder="e.g. IEEE-92184029"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold mb-1">Membership Type</label>
                  <select
                    value={form.membershipType}
                    onChange={(e) => setForm({ ...form, membershipType: e.target.value as MembershipType })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  >
                    <option value="Senior Member">Senior Member</option>
                    <option value="Life Member">Life Member</option>
                    <option value="Fellow">Fellow</option>
                    <option value="Annual Member">Annual Member</option>
                    <option value="Student Member">Student Member</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold mb-1">Valid Until</label>
                  <input
                    type="date"
                    value={form.validUntil}
                    onChange={(e) => setForm({ ...form, validUntil: e.target.value })}
                    className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold mb-1">Certificate URL / Attachment</label>
                <input
                  type="text"
                  value={form.certificateUrl}
                  onChange={(e) => setForm({ ...form, certificateUrl: e.target.value })}
                  className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl p-2.5 font-mono"
                  placeholder="https://example.com/certificate.pdf"
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
                  Save Membership
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
