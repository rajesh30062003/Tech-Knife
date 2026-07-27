import React, { useState } from 'react';
import {
  User,
  Mail,
  Phone,
  MapPin,
  AlertCircle,
  Building2,
  Briefcase,
  Calendar,
  DollarSign,
  Lock,
  CheckCircle2,
  UserCheck,
  Save,
  Loader2,
  Plus,
  X,
  Shield,
  Clock,
  Sparkles,
} from 'lucide-react';
import { UserProfile } from '../../types';
import { useAuth } from '../../context/AuthContext';

interface ProfileOverviewTabProps {
  user: UserProfile;
}

export const ProfileOverviewTab: React.FC<ProfileOverviewTabProps> = ({ user }) => {
  const { updateUserProfile } = useAuth();

  // Editable fields state
  const [phoneNumber, setPhoneNumber] = useState(user.phoneNumber || '');
  const [address, setAddress] = useState(user.address || '742 Evergreen Terrace, San Jose, CA 95112');
  const [emergencyContact, setEmergencyContact] = useState(user.emergencyContact || 'Nikolai Rostova (+1 555-900-3344)');
  const [bio, setBio] = useState(
    user.bio || 'Senior full-stack software engineer specializing in high-performance React application architecture, micro-frontends, and cloud services.'
  );
  const [skills, setSkills] = useState<string[]>(
    user.skills && user.skills.length > 0
      ? user.skills
      : ['TypeScript', 'React 18', 'Tailwind CSS', 'Node.js', 'REST APIs', 'GraphQL', 'Docker']
  );
  const [newSkill, setNewSkill] = useState('');

  const [isSaving, setIsSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);

  const handleAddSkill = () => {
    if (newSkill.trim() && !skills.includes(newSkill.trim())) {
      setSkills([...skills, newSkill.trim()]);
      setNewSkill('');
    }
  };

  const handleRemoveSkill = (skillToRemove: string) => {
    setSkills(skills.filter((s) => s !== skillToRemove));
  };

  const handleSaveContactDetails = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    setSaveSuccess(false);

    try {
      await updateUserProfile({
        phoneNumber,
        address,
        emergencyContact,
        bio,
        skills,
      });
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 4000);
    } catch (err) {
      console.error('Failed to update contact profile details', err);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="space-y-8">
      {/* Save Success Banner */}
      {saveSuccess && (
        <div className="p-4 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-600 dark:text-emerald-400 text-xs font-semibold flex items-center justify-between shadow-sm animate-fadeIn">
          <div className="flex items-center gap-2.5">
            <CheckCircle2 className="w-5 h-5 text-emerald-500 shrink-0" />
            <span>Profile details updated successfully! Emergency contact, address, and skills saved.</span>
          </div>
        </div>
      )}

      {/* Main Grid Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Left Column: Editable Personal & Contact Information */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 sm:p-7 shadow-lg space-y-6">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
              <div>
                <h3 className="text-base font-bold text-slate-900 dark:text-white flex items-center gap-2">
                  <User className="w-4 h-4 text-indigo-500" />
                  Personal & Contact Details
                </h3>
                <p className="text-xs text-slate-500">
                  Update your contact phone, address, emergency contact, and personal bio
                </p>
              </div>
              <span className="px-3 py-1 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 text-[11px] font-bold rounded-full border border-indigo-500/20">
                Editable by You
              </span>
            </div>

            <form onSubmit={handleSaveContactDetails} className="space-y-5">
              {/* Phone & Emergency Contact */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 uppercase tracking-wider mb-2">
                    Phone Contact Number *
                  </label>
                  <div className="relative">
                    <Phone className="w-4 h-4 absolute left-3.5 top-3 text-slate-400" />
                    <input
                      type="text"
                      required
                      value={phoneNumber}
                      onChange={(e) => setPhoneNumber(e.target.value)}
                      placeholder="+1 (555) 018-7712"
                      className="w-full pl-10 pr-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-medium text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500/50"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 uppercase tracking-wider mb-2">
                    Emergency Contact Person & Phone *
                  </label>
                  <div className="relative">
                    <AlertCircle className="w-4 h-4 absolute left-3.5 top-3 text-amber-500" />
                    <input
                      type="text"
                      required
                      value={emergencyContact}
                      onChange={(e) => setEmergencyContact(e.target.value)}
                      placeholder="Jane Doe (+1 555-900-1122)"
                      className="w-full pl-10 pr-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-medium text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500/50"
                    />
                  </div>
                  <p className="text-[10px] text-slate-400 mt-1">Used by HR solely in case of medical or facility emergencies</p>
                </div>
              </div>

              {/* Address */}
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 uppercase tracking-wider mb-2">
                  Residential Address *
                </label>
                <div className="relative">
                  <MapPin className="w-4 h-4 absolute left-3.5 top-3 text-slate-400" />
                  <input
                    type="text"
                    required
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    placeholder="742 Evergreen Terrace, San Jose, CA 95112"
                    className="w-full pl-10 pr-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-medium text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500/50"
                  />
                </div>
              </div>

              {/* Professional Bio */}
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 uppercase tracking-wider mb-2">
                  Professional Bio / Summary
                </label>
                <textarea
                  rows={3}
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                  placeholder="Short overview of your engineering background and enterprise contributions..."
                  className="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-medium text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500/50 resize-none"
                />
              </div>

              {/* Skills Tags */}
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 uppercase tracking-wider mb-2">
                  Technical Skills & Competencies
                </label>
                <div className="flex flex-wrap gap-2 mb-3">
                  {skills.map((skill) => (
                    <span
                      key={skill}
                      className="inline-flex items-center gap-1.5 px-3 py-1 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 text-xs font-semibold rounded-lg border border-slate-200 dark:border-slate-700"
                    >
                      {skill}
                      <button
                        type="button"
                        onClick={() => handleRemoveSkill(skill)}
                        className="text-slate-400 hover:text-rose-500 transition-colors"
                      >
                        <X className="w-3 h-3" />
                      </button>
                    </span>
                  ))}
                </div>

                <div className="flex gap-2">
                  <input
                    type="text"
                    value={newSkill}
                    onChange={(e) => setNewSkill(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                        handleAddSkill();
                      }
                    }}
                    placeholder="Add a skill (e.g. Kubernetes, React Native) & press Enter"
                    className="flex-1 px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-medium text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500/50"
                  />
                  <button
                    type="button"
                    onClick={handleAddSkill}
                    className="px-4 py-2 bg-slate-200 dark:bg-slate-800 hover:bg-slate-300 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs rounded-xl transition-colors flex items-center gap-1"
                  >
                    <Plus className="w-3.5 h-3.5" /> Add
                  </button>
                </div>
              </div>

              {/* Action Button */}
              <div className="pt-2 flex justify-end">
                <button
                  type="submit"
                  disabled={isSaving}
                  className="px-6 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 disabled:opacity-50"
                >
                  {isSaving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
                  <span>Save Personal Details</span>
                </button>
              </div>
            </form>
          </div>
        </div>

        {/* Right Column: READ-ONLY Corporate Restrictions */}
        <div className="space-y-6">
          <div className="bg-slate-50/80 dark:bg-slate-900/90 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-5 relative">
            <div className="flex items-center justify-between border-b border-slate-200/80 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <Shield className="w-4 h-4 text-amber-500" />
                <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-900 dark:text-white">
                  Corporate Data Safeguards
                </h3>
              </div>
              <span className="px-2.5 py-0.5 bg-amber-500/10 text-amber-600 dark:text-amber-400 text-[10px] font-mono font-bold rounded-full border border-amber-500/20 flex items-center gap-1">
                <Lock className="w-3 h-3" /> Read-Only Fields
              </span>
            </div>

            <p className="text-[11px] text-slate-500 leading-relaxed">
              The following fields are managed centrally by HR, IT, and Finance Administration. Employees cannot modify these attributes directly.
            </p>

            <div className="space-y-3 text-xs">
              {/* Official Email */}
              <div className="p-3.5 bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl flex items-start gap-3">
                <Mail className="w-4 h-4 text-slate-400 shrink-0 mt-0.5" />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] uppercase font-bold text-slate-400">Official Email</span>
                    <span className="text-[10px] font-mono text-amber-600 dark:text-amber-400 font-bold flex items-center gap-1">
                      <Lock className="w-2.5 h-2.5" /> Managed by IT
                    </span>
                  </div>
                  <span className="font-mono font-bold text-slate-800 dark:text-slate-200 truncate block mt-0.5">
                    {user.email}
                  </span>
                </div>
              </div>

              {/* Department */}
              <div className="p-3.5 bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl flex items-start gap-3">
                <Building2 className="w-4 h-4 text-slate-400 shrink-0 mt-0.5" />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] uppercase font-bold text-slate-400">Department</span>
                    <span className="text-[10px] font-mono text-amber-600 dark:text-amber-400 font-bold flex items-center gap-1">
                      <Lock className="w-2.5 h-2.5" /> Managed by Mgmt
                    </span>
                  </div>
                  <span className="font-bold text-slate-800 dark:text-slate-200 truncate block mt-0.5">
                    {user.department || 'Engineering'}
                  </span>
                </div>
              </div>

              {/* Designation */}
              <div className="p-3.5 bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl flex items-start gap-3">
                <Briefcase className="w-4 h-4 text-slate-400 shrink-0 mt-0.5" />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] uppercase font-bold text-slate-400">Designation</span>
                    <span className="text-[10px] font-mono text-amber-600 dark:text-amber-400 font-bold flex items-center gap-1">
                      <Lock className="w-2.5 h-2.5" /> Managed by HR
                    </span>
                  </div>
                  <span className="font-bold text-slate-800 dark:text-slate-200 truncate block mt-0.5">
                    {user.designation || 'Senior Full Stack Engineer'}
                  </span>
                </div>
              </div>

              {/* Joining Date */}
              <div className="p-3.5 bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl flex items-start gap-3">
                <Calendar className="w-4 h-4 text-slate-400 shrink-0 mt-0.5" />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] uppercase font-bold text-slate-400">Joining Date</span>
                    <span className="text-[10px] font-mono text-amber-600 dark:text-amber-400 font-bold flex items-center gap-1">
                      <Lock className="w-2.5 h-2.5" /> System Record
                    </span>
                  </div>
                  <span className="font-mono font-bold text-slate-800 dark:text-slate-200 truncate block mt-0.5">
                    {user.joinDate || '2022-04-12'}
                  </span>
                </div>
              </div>

              {/* Base Salary */}
              <div className="p-3.5 bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl flex items-start gap-3">
                <DollarSign className="w-4 h-4 text-emerald-500 shrink-0 mt-0.5" />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] uppercase font-bold text-slate-400">Annual Base Salary</span>
                    <span className="text-[10px] font-mono text-amber-600 dark:text-amber-400 font-bold flex items-center gap-1">
                      <Lock className="w-2.5 h-2.5" /> Managed by Payroll
                    </span>
                  </div>
                  <span className="font-mono font-extrabold text-emerald-600 dark:text-emerald-400 text-sm truncate block mt-0.5">
                    ${(user.salary || 135000).toLocaleString('en-US')} USD / yr
                  </span>
                </div>
              </div>

              {/* Reporting Manager */}
              <div className="p-3.5 bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl flex items-start gap-3">
                <UserCheck className="w-4 h-4 text-indigo-500 shrink-0 mt-0.5" />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] uppercase font-bold text-slate-400">Reporting Manager</span>
                    <span className="text-[10px] font-mono text-amber-600 dark:text-amber-400 font-bold flex items-center gap-1">
                      <Lock className="w-2.5 h-2.5" /> Hierarchy Record
                    </span>
                  </div>
                  <div className="mt-1 flex items-center gap-2">
                    <div className="w-6 h-6 rounded-full bg-indigo-600 text-white font-bold text-[10px] flex items-center justify-center">
                      MB
                    </div>
                    <div>
                      <span className="font-bold text-slate-900 dark:text-white block text-xs">
                        {user.managerName || 'Marcus Brody'}
                      </span>
                      <span className="text-[10px] text-slate-500 block">
                        {user.managerDesignation || 'Engineering Manager'}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};
