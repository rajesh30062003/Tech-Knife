import React, { useState, useEffect } from 'react';
import {
  UploadCloud,
  FileText,
  Image as ImageIcon,
  Video,
  Award,
  DollarSign,
  User,
  Trash2,
  ExternalLink,
  Download,
  Loader2,
  CheckCircle2,
  Lock,
  Search,
  Filter
} from 'lucide-react';
import { storageApi } from '../../api/coreServices';
import { StorageFile, FileCategory } from '../../types';
import { useAuth } from '../../context/AuthContext';

interface UniversalFileUploaderProps {
  defaultCategory?: FileCategory;
  moduleName?: string;
  onFileUploaded?: (file: StorageFile) => void;
}

const CATEGORIES: FileCategory[] = [
  'Documents',
  'Images',
  'Videos',
  'Certificates',
  'Invoices',
  'Payslips',
  'Employee Photos',
  'Customer Documents',
  'Project Files',
];

export const UniversalFileUploader: React.FC<UniversalFileUploaderProps> = ({
  defaultCategory = 'Documents',
  moduleName = 'General',
  onFileUploaded,
}) => {
  const { user } = useAuth();

  const [files, setFiles] = useState<StorageFile[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<FileCategory>(defaultCategory);
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [isUploading, setIsUploading] = useState(false);
  const [isDragging, setIsDragging] = useState(false);
  const [uploadSuccess, setUploadSuccess] = useState(false);

  const loadFiles = async () => {
    const data = await storageApi.getFiles();
    setFiles(data);
  };

  useEffect(() => {
    loadFiles();
  }, []);

  const handleFileUpload = async (file: File) => {
    if (!file) return;

    setIsUploading(true);
    setUploadSuccess(false);

    try {
      const uploaded = await storageApi.uploadFile(
        file,
        selectedCategory,
        `${user?.firstName || 'Admin'} ${user?.lastName || 'User'}`,
        user?.email || 'admin@techknife.io',
        moduleName
      );

      await loadFiles();
      setUploadSuccess(true);
      if (onFileUploaded) onFileUploaded(uploaded);
      setTimeout(() => setUploadSuccess(false), 3000);
    } catch (err) {
      console.error(err);
    } finally {
      setIsUploading(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileUpload(e.dataTransfer.files[0]);
    }
  };

  const handleDelete = async (id: string) => {
    await storageApi.deleteFile(id, `${user?.firstName} ${user?.lastName}`);
    await loadFiles();
  };

  const filteredFiles = files.filter((f) => {
    const matchesCat = categoryFilter === 'ALL' || f.category === categoryFilter;
    const matchesQuery =
      f.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      f.uploadedBy.toLowerCase().includes(searchQuery.toLowerCase()) ||
      f.category.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCat && matchesQuery;
  });

  const getCategoryIcon = (category: FileCategory) => {
    switch (category) {
      case 'Images':
      case 'Employee Photos':
        return <ImageIcon className="w-4 h-4 text-purple-500" />;
      case 'Videos':
        return <Video className="w-4 h-4 text-rose-500" />;
      case 'Certificates':
        return <Award className="w-4 h-4 text-amber-500" />;
      case 'Invoices':
      case 'Payslips':
        return <DollarSign className="w-4 h-4 text-emerald-500" />;
      default:
        return <FileText className="w-4 h-4 text-blue-500" />;
    }
  };

  return (
    <div className="space-y-6">
      {/* Upload Zone */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 dark:border-slate-800 pb-3">
          <div>
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white flex items-center gap-2">
              <UploadCloud className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
              Universal Storage & Cloudinary Asset Vault
            </h3>
            <p className="text-xs text-slate-500">
              Secure document repository for certificates, invoices, payslips, photos & project deliverables.
            </p>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-slate-500">Asset Category:</span>
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value as FileCategory)}
              className="text-xs font-semibold p-2 rounded-xl bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100"
            >
              {CATEGORIES.map((cat) => (
                <option key={cat} value={cat}>
                  {cat}
                </option>
              ))}
            </select>
          </div>
        </div>

        {uploadSuccess && (
          <div className="p-3 rounded-2xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 text-xs text-emerald-800 dark:text-emerald-200 font-bold flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600" /> Asset successfully uploaded to Cloudinary Vault!
          </div>
        )}

        {/* Drag Drop Area */}
        <div
          onDragOver={(e) => {
            e.preventDefault();
            setIsDragging(true);
          }}
          onDragLeave={() => setIsDragging(false)}
          onDrop={handleDrop}
          className={`border-2 border-dashed rounded-2xl p-8 text-center transition-all ${
            isDragging
              ? 'border-indigo-500 bg-indigo-50/50 dark:bg-indigo-950/40'
              : 'border-slate-200 dark:border-slate-800 hover:border-indigo-400 bg-slate-50/50 dark:bg-slate-950/50'
          }`}
        >
          {isUploading ? (
            <div className="py-4 space-y-2">
              <Loader2 className="w-8 h-8 text-indigo-600 animate-spin mx-auto" />
              <p className="text-xs font-bold text-slate-700 dark:text-slate-300">Uploading file to Cloudinary & Indexing...</p>
            </div>
          ) : (
            <div className="space-y-3">
              <div className="w-12 h-12 rounded-2xl bg-indigo-100 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400 flex items-center justify-center mx-auto">
                <UploadCloud className="w-6 h-6" />
              </div>
              <div>
                <span className="font-extrabold text-xs text-slate-900 dark:text-white">
                  Drag and drop files here, or{' '}
                </span>
                <label className="text-xs font-extrabold text-indigo-600 dark:text-indigo-400 hover:underline cursor-pointer">
                  browse files
                  <input
                    type="file"
                    onChange={(e) => e.target.files?.[0] && handleFileUpload(e.target.files[0])}
                    className="hidden"
                  />
                </label>
              </div>
              <p className="text-[11px] text-slate-400">
                Supports PDF, DOCX, PNG, JPG, MP4, CSV (Max file size: 50MB)
              </p>
            </div>
          )}
        </div>
      </div>

      {/* Asset Manager Directory */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <h4 className="font-extrabold text-sm text-slate-900 dark:text-white">Uploaded Asset Vault Directory</h4>

          <div className="flex flex-wrap items-center gap-2">
            <div className="relative">
              <Search className="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                type="text"
                placeholder="Search assets..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-8 pr-3 py-1.5 text-xs rounded-xl bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100"
              />
            </div>

            <select
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value)}
              className="text-xs font-semibold p-1.5 rounded-xl bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100"
            >
              <option value="ALL">All Categories</option>
              {CATEGORIES.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </div>
        </div>

        {filteredFiles.length === 0 ? (
          <div className="p-8 text-center text-xs text-slate-400 font-medium">No storage assets match the criteria.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-400 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-3 px-4">Asset Name</th>
                  <th className="py-3 px-4">Category</th>
                  <th className="py-3 px-4">Uploaded By</th>
                  <th className="py-3 px-4">Size</th>
                  <th className="py-3 px-4">Date</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
                {filteredFiles.map((file) => (
                  <tr key={file.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="py-3 px-4 font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
                      {getCategoryIcon(file.category)}
                      <span className="truncate max-w-xs">{file.name}</span>
                      {file.isPrivate && <Lock className="w-3 h-3 text-amber-500 shrink-0" title="Encrypted Private File" />}
                    </td>

                    <td className="py-3 px-4">
                      <span className="px-2 py-0.5 rounded-md bg-slate-100 dark:bg-slate-800 font-semibold text-[10px] text-slate-700 dark:text-slate-300">
                        {file.category}
                      </span>
                    </td>

                    <td className="py-3 px-4 text-slate-600 dark:text-slate-400">{file.uploadedBy}</td>

                    <td className="py-3 px-4 text-slate-500 font-mono text-[11px]">
                      {(file.fileSize / 1024 / 1024).toFixed(2)} MB
                    </td>

                    <td className="py-3 px-4 text-slate-500 text-[11px]">{new Date(file.createdAt).toLocaleDateString()}</td>

                    <td className="py-3 px-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <a
                          href={file.url}
                          target="_blank"
                          rel="noreferrer"
                          className="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-indigo-600 dark:text-indigo-400"
                          title="Open Asset"
                        >
                          <ExternalLink className="w-3.5 h-3.5" />
                        </a>
                        <button
                          onClick={() => handleDelete(file.id)}
                          className="p-1.5 hover:bg-rose-50 dark:hover:bg-rose-950/40 rounded-lg text-rose-500"
                          title="Delete File"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
