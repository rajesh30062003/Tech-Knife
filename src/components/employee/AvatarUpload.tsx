import React, { useState } from 'react';
import { Upload, Camera, Link as LinkIcon, User } from 'lucide-react';

interface AvatarUploadProps {
  currentUrl?: string;
  onChange: (url: string) => void;
  name?: string;
}

export const AvatarUpload: React.FC<AvatarUploadProps> = ({ currentUrl, onChange, name }) => {
  const [activeTab, setActiveTab] = useState<'url' | 'upload'>('url');
  const [urlInput, setUrlInput] = useState(currentUrl || '');

  const initials = name
    ? name
        .split(' ')
        .map(n => n[0])
        .join('')
        .slice(0, 2)
        .toUpperCase()
    : 'EMP';

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        if (typeof reader.result === 'string') {
          onChange(reader.result);
        }
      };
      reader.readAsDataURL(file);
    }
  };

  const handleApplyUrl = () => {
    if (urlInput) {
      onChange(urlInput);
    }
  };

  return (
    <div className="space-y-3">
      <label className="block text-xs font-bold text-slate-700 dark:text-slate-300">
        Profile Photo & Avatar
      </label>

      <div className="flex items-center gap-4">
        {/* Avatar Preview */}
        <div className="relative group shrink-0">
          <div className="w-16 h-16 rounded-2xl bg-indigo-600 text-white font-extrabold flex items-center justify-center text-lg overflow-hidden border-2 border-indigo-500/30 shadow-md">
            {currentUrl ? (
              <img
                src={currentUrl}
                alt={name || 'Avatar'}
                className="w-full h-full object-cover"
                onError={(e) => {
                  // Fallback on image load error
                  (e.target as HTMLImageElement).style.display = 'none';
                }}
              />
            ) : (
              <span>{initials}</span>
            )}
          </div>
          <div className="absolute inset-0 bg-slate-900/40 rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center text-white">
            <Camera className="w-4 h-4" />
          </div>
        </div>

        {/* Upload Controls */}
        <div className="flex-1 space-y-2">
          <div className="flex items-center gap-2 border-b border-slate-200 dark:border-slate-800 pb-1 text-xs">
            <button
              type="button"
              onClick={() => setActiveTab('url')}
              className={`pb-1 font-semibold transition-colors flex items-center gap-1 ${
                activeTab === 'url'
                  ? 'text-indigo-600 dark:text-indigo-400 border-b-2 border-indigo-600'
                  : 'text-slate-400 hover:text-slate-600'
              }`}
            >
              <LinkIcon className="w-3 h-3" /> Image URL
            </button>
            <button
              type="button"
              onClick={() => setActiveTab('upload')}
              className={`pb-1 font-semibold transition-colors flex items-center gap-1 ${
                activeTab === 'upload'
                  ? 'text-indigo-600 dark:text-indigo-400 border-b-2 border-indigo-600'
                  : 'text-slate-400 hover:text-slate-600'
              }`}
            >
              <Upload className="w-3 h-3" /> Upload File
            </button>
          </div>

          {activeTab === 'url' ? (
            <div className="flex items-center gap-2">
              <input
                type="url"
                value={urlInput}
                onChange={(e) => setUrlInput(e.target.value)}
                placeholder="https://images.unsplash.com/photo-..."
                className="flex-1 px-3 py-1.5 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
              />
              <button
                type="button"
                onClick={handleApplyUrl}
                className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs rounded-xl transition-colors shadow-xs"
              >
                Apply
              </button>
            </div>
          ) : (
            <div className="relative">
              <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10"
              />
              <div className="px-3 py-2 border-2 border-dashed border-slate-300 dark:border-slate-700 rounded-xl bg-slate-50 dark:bg-slate-800/50 text-center text-xs text-slate-500 hover:border-indigo-500 transition-colors">
                <span className="font-semibold text-indigo-600 dark:text-indigo-400">Click to choose image</span> or drag and drop
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
