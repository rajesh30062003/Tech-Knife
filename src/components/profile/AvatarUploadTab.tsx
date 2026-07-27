import React, { useState, useRef } from 'react';
import {
  Camera,
  Upload,
  CheckCircle2,
  Sparkles,
  Loader2,
  Image as ImageIcon,
  Check,
  RefreshCw,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

const PRESET_AVATARS = [
  'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=250',
  'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=250',
  'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=250',
  'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=250',
  'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=250',
  'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=250',
  'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&q=80&w=250',
  'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=250',
];

export const AvatarUploadTab: React.FC = () => {
  const { user, updateProfilePicture } = useAuth();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [selectedAvatar, setSelectedAvatar] = useState<string>(
    user?.avatarUrl || PRESET_AVATARS[0]
  );
  const [customUrl, setCustomUrl] = useState('');
  const [isUpdating, setIsUpdating] = useState(false);
  const [success, setSuccess] = useState(false);
  const [dragActive, setDragActive] = useState(false);

  const handleApplyAvatar = async (urlToApply: string) => {
    setIsUpdating(true);
    setSuccess(false);

    try {
      await updateProfilePicture({ avatarUrl: urlToApply });
      setSelectedAvatar(urlToApply);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      console.error('Failed to update avatar', err);
    } finally {
      setIsUpdating(false);
    }
  };

  const handleFileSelect = (file: File) => {
    if (!file.type.startsWith('image/')) return;
    const reader = new FileReader();
    reader.onload = (e) => {
      if (e.target?.result) {
        const dataUrl = e.target.result as string;
        setSelectedAvatar(dataUrl);
        handleApplyAvatar(dataUrl);
      }
    };
    reader.readAsDataURL(file);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileSelect(e.dataTransfer.files[0]);
    }
  };

  return (
    <div className="max-w-3xl mx-auto space-y-8">
      {/* Header Banner */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 sm:p-8 shadow-lg flex flex-col md:flex-row items-center gap-6">
        <div className="relative group shrink-0">
          <img
            src={selectedAvatar}
            alt="Current Avatar"
            className="w-28 h-28 rounded-2xl object-cover border-4 border-indigo-600 shadow-xl ring-4 ring-indigo-500/20"
          />
          <span className="absolute -bottom-2 -right-2 p-2 bg-indigo-600 text-white rounded-xl shadow-md">
            <Camera className="w-4 h-4" />
          </span>
        </div>

        <div className="space-y-2 text-center md:text-left flex-1">
          <h3 className="text-xl font-extrabold text-slate-900 dark:text-white flex items-center justify-center md:justify-start gap-2">
            <span>Profile Photo & Display Avatar</span>
            <Sparkles className="w-4 h-4 text-amber-500" />
          </h3>
          <p className="text-xs text-slate-500">
            Upload a custom photo, select from enterprise presets, or link a CDN image URL to update your user icon across all dashboards and comments.
          </p>

          {success && (
            <div className="p-3 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-500 text-xs font-semibold flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" /> Avatar picture updated successfully!
            </div>
          )}
        </div>
      </div>

      {/* Grid: Upload & Preset Gallery */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        {/* Option 1: Drag & Drop File Upload */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-4">
          <h4 className="text-sm font-bold text-slate-900 dark:text-white flex items-center gap-2">
            <Upload className="w-4 h-4 text-indigo-500" />
            Upload Image File
          </h4>

          <div
            onDragOver={(e) => {
              e.preventDefault();
              setDragActive(true);
            }}
            onDragLeave={() => setDragActive(false)}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
            className={`border-2 border-dashed rounded-2xl p-6 text-center cursor-pointer transition-all flex flex-col items-center justify-center gap-3 ${
              dragActive
                ? 'border-indigo-500 bg-indigo-500/10'
                : 'border-slate-200 dark:border-slate-800 hover:border-indigo-400 bg-slate-50/50 dark:bg-slate-950/50'
            }`}
          >
            <input
              type="file"
              ref={fileInputRef}
              accept="image/*"
              className="hidden"
              onChange={(e) => {
                if (e.target.files && e.target.files[0]) {
                  handleFileSelect(e.target.files[0]);
                }
              }}
            />
            <div className="p-3 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 rounded-2xl">
              <ImageIcon className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xs font-bold text-slate-800 dark:text-slate-200 block">
                Drag & drop your photo here
              </span>
              <span className="text-[11px] text-slate-400 block mt-0.5">
                PNG, JPG or WebP up to 5MB
              </span>
            </div>
            <button
              type="button"
              className="px-4 py-1.5 bg-indigo-600 text-white font-bold text-xs rounded-xl shadow hover:bg-indigo-500 transition-colors"
            >
              Browse Computer
            </button>
          </div>

          {/* Custom URL Form */}
          <div className="pt-2 border-t border-slate-100 dark:border-slate-800">
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">
              Or Link CDN / Unsplash URL
            </label>
            <div className="flex gap-2">
              <input
                type="url"
                value={customUrl}
                onChange={(e) => setCustomUrl(e.target.value)}
                placeholder="https://images.unsplash.com/photo-..."
                className="flex-1 px-3.5 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-xs font-medium text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <button
                type="button"
                disabled={!customUrl || isUpdating}
                onClick={() => handleApplyAvatar(customUrl)}
                className="px-4 py-2 bg-slate-900 dark:bg-slate-800 hover:bg-slate-800 text-white font-bold text-xs rounded-xl shadow transition-colors disabled:opacity-50 flex items-center gap-1.5"
              >
                {isUpdating ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <RefreshCw className="w-3.5 h-3.5" />}
                Apply
              </button>
            </div>
          </div>
        </div>

        {/* Option 2: Curated Enterprise Avatar Presets */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-lg space-y-4">
          <h4 className="text-sm font-bold text-slate-900 dark:text-white flex items-center gap-2">
            <Sparkles className="w-4 h-4 text-amber-500" />
            Curated Professional Presets
          </h4>
          <p className="text-xs text-slate-500">
            Select one of the pre-screened HD professional avatars below for immediate platform-wide sync.
          </p>

          <div className="grid grid-cols-4 gap-3 pt-2">
            {PRESET_AVATARS.map((url, idx) => {
              const isSelected = selectedAvatar === url;
              return (
                <button
                  key={idx}
                  type="button"
                  onClick={() => handleApplyAvatar(url)}
                  disabled={isUpdating}
                  className={`relative rounded-2xl overflow-hidden border-2 transition-all group aspect-square ${
                    isSelected
                      ? 'border-indigo-600 ring-2 ring-indigo-500/40 scale-105 shadow-md'
                      : 'border-transparent hover:border-indigo-300 dark:hover:border-slate-700 opacity-80 hover:opacity-100'
                  }`}
                >
                  <img src={url} alt={`Preset ${idx + 1}`} className="w-full h-full object-cover" />
                  {isSelected && (
                    <div className="absolute inset-0 bg-indigo-600/30 flex items-center justify-center">
                      <div className="p-1 bg-indigo-600 text-white rounded-full">
                        <Check className="w-3.5 h-3.5" />
                      </div>
                    </div>
                  )}
                </button>
              );
            })}
          </div>
        </div>

      </div>
    </div>
  );
};
