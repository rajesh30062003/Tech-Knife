import React, { useState, useEffect } from 'react';
import { 
  FileText, UploadCloud, Download, Trash2, ExternalLink, RefreshCw, 
  Loader2, CheckCircle2, AlertCircle, File, FolderKanban 
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';
import { projectWorkspaceApi, DriveFileRecord } from '../../../api/projectWorkspaceApi';
import { useAuth } from '../../../context/AuthContext';

interface ProjectDocumentsTabProps {
  project: EnterpriseProject;
}

export const ProjectDocumentsTab: React.FC<ProjectDocumentsTabProps> = ({ project }) => {
  const { user } = useAuth();
  const projectCode = project.projectCode || project.projectId || project.id;

  const [documents, setDocuments] = useState<DriveFileRecord[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isUploading, setIsUploading] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState('Project Files');
  const [uploadSuccess, setUploadSuccess] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const loadDocuments = async () => {
    setIsLoading(true);
    setErrorMsg(null);
    try {
      const res = await projectWorkspaceApi.getDriveDocuments(projectCode);
      if (res.data) {
        setDocuments(res.data);
      }
    } catch (err: any) {
      console.error('Failed to load project documents:', err);
      // Fallback empty list gracefully
      setDocuments([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadDocuments();
  }, [projectCode]);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setIsUploading(true);
    setUploadSuccess(false);
    setErrorMsg(null);

    try {
      const userName = `${user?.firstName || 'Admin'} ${user?.lastName || 'User'}`;
      await projectWorkspaceApi.uploadDriveDocument(file, projectCode, selectedCategory, userName);
      await loadDocuments();
      setUploadSuccess(true);
      setTimeout(() => setUploadSuccess(false), 3000);
    } catch (err: any) {
      console.error('Upload failed:', err);
      setErrorMsg(err?.response?.data?.message || 'File upload failed. Check Google Drive connectivity.');
    } finally {
      setIsUploading(false);
    }
  };

  const handleDeleteDocument = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this document from Google Drive?')) return;
    try {
      await projectWorkspaceApi.deleteDriveDocument(id);
      await loadDocuments();
    } catch (err: any) {
      console.error('Delete failed:', err);
      alert('Failed to delete file from Google Drive');
    }
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Upload Banner Section */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-indigo-950 via-slate-900 to-indigo-950 text-white shadow-xl border border-indigo-900/50 space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 text-indigo-400 font-bold text-xs uppercase tracking-wider mb-1">
              <FolderKanban className="w-4 h-4" />
              <span>Google Drive Cloud Storage Storage Pipeline</span>
            </div>
            <h3 className="text-lg font-extrabold text-white">
              Project Documents & Artifacts ({documents.length})
            </h3>
            <p className="text-xs text-slate-300">
              Synchronized Google Drive file repository for project code: <span className="font-mono text-cyan-400 font-bold">{projectCode}</span>
            </p>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={loadDocuments}
              className="p-2.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl transition-all border border-slate-700 text-xs font-bold flex items-center gap-1.5"
              title="Refresh Google Drive files"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isLoading ? 'animate-spin' : ''}`} /> Refresh
            </button>

            <label className="px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 text-xs font-extrabold rounded-xl transition-all shadow-md cursor-pointer flex items-center gap-2">
              {isUploading ? <Loader2 className="w-4 h-4 animate-spin" /> : <UploadCloud className="w-4 h-4" />}
              <span>Upload to Drive</span>
              <input type="file" onChange={handleFileUpload} disabled={isUploading} className="hidden" />
            </label>
          </div>
        </div>

        {uploadSuccess && (
          <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-2xl text-xs text-emerald-300 font-bold flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400" /> Document uploaded and indexed in Google Drive!
          </div>
        )}

        {errorMsg && (
          <div className="p-3 bg-rose-500/10 border border-rose-500/30 rounded-2xl text-xs text-rose-300 font-bold flex items-center gap-2">
            <AlertCircle className="w-4 h-4 text-rose-400" /> {errorMsg}
          </div>
        )}
      </div>

      {/* Documents List */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-xs">
        {isLoading ? (
          <div className="py-12 text-center text-xs text-slate-400 flex items-center justify-center gap-2">
            <Loader2 className="w-4 h-4 animate-spin text-indigo-500" /> Fetching project documents from Google Drive...
          </div>
        ) : documents.length === 0 ? (
          <div className="py-12 text-center space-y-3">
            <FileText className="w-10 h-10 text-slate-300 dark:text-slate-700 mx-auto" />
            <h4 className="text-sm font-bold text-slate-700 dark:text-slate-300">No Documents Uploaded Yet</h4>
            <p className="text-xs text-slate-400 max-w-sm mx-auto">
              Upload project SRS documents, technical architecture guides, design assets, or final deliverables.
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-400 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-3 px-4">Document Name</th>
                  <th className="py-3 px-4">Category</th>
                  <th className="py-3 px-4">Uploaded By</th>
                  <th className="py-3 px-4">Date</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
                {documents.map((doc) => (
                  <tr key={doc.id || doc.fileId} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="py-3.5 px-4 font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
                      <div className="p-2 bg-indigo-50 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400 rounded-xl">
                        <FileText className="w-4 h-4" />
                      </div>
                      <div>
                        <div className="truncate max-w-xs">{doc.name || doc.originalFileName}</div>
                        <span className="text-[10px] font-mono text-slate-400">ID: {doc.fileId || doc.id}</span>
                      </div>
                    </td>

                    <td className="py-3.5 px-4">
                      <span className="px-2.5 py-0.5 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-bold text-[10px] border border-slate-200 dark:border-slate-700">
                        {doc.category || 'Project File'}
                      </span>
                    </td>

                    <td className="py-3.5 px-4 text-slate-700 dark:text-slate-300 font-medium">
                      {doc.uploadedBy || 'Executive Admin'}
                    </td>

                    <td className="py-3.5 px-4 text-slate-400 text-[11px] font-mono">
                      {doc.uploadedAt ? new Date(doc.uploadedAt).toLocaleDateString() : 'Recent'}
                    </td>

                    <td className="py-3.5 px-4 text-right space-x-2">
                      {doc.webViewLink && (
                        <a
                          href={doc.webViewLink}
                          target="_blank"
                          rel="noreferrer"
                          className="px-2.5 py-1 bg-indigo-50 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400 font-bold rounded-lg text-[10px] hover:bg-indigo-100 transition-colors inline-flex items-center gap-1"
                        >
                          <ExternalLink className="w-3 h-3" /> Preview
                        </a>
                      )}

                      <button
                        onClick={() => handleDeleteDocument(doc.id || doc.fileId)}
                        className="p-1 text-slate-400 hover:text-rose-600 transition-colors"
                        title="Delete file"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
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
