import React, { useState, useEffect } from 'react';
import { 
  FileText, UploadCloud, Download, Trash2, ExternalLink, RefreshCw, 
  Loader2, CheckCircle2, AlertCircle, Eye, Search, Filter, ZoomIn, ZoomOut, RotateCcw, FolderKanban, ShieldCheck
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
  const [selectedDocument, setSelectedDocument] = useState<DriveFileRecord | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isUploading, setIsUploading] = useState(false);
  const [isPdfLoading, setIsPdfLoading] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [zoomLevel, setZoomLevel] = useState(100);
  const [uploadSuccess, setUploadSuccess] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const loadDocuments = async (autoSelectId?: string) => {
    setIsLoading(true);
    setErrorMsg(null);
    try {
      const res = await projectWorkspaceApi.getDriveDocuments(projectCode);
      if (res.data) {
        setDocuments(res.data);
        if (res.data.length > 0) {
          if (autoSelectId) {
            const found = res.data.find(d => (d.fileId || d.id) === autoSelectId);
            setSelectedDocument(found || res.data[0]);
          } else if (!selectedDocument || !res.data.some(d => (d.fileId || d.id) === (selectedDocument.fileId || selectedDocument.id))) {
            setSelectedDocument(res.data[0]);
          }
        } else {
          setSelectedDocument(null);
        }
      }
    } catch (err: any) {
      console.error('Failed to load project documents:', err);
      setDocuments([]);
      setSelectedDocument(null);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadDocuments();
  }, [projectCode]);

  useEffect(() => {
    if (selectedDocument) {
      setIsPdfLoading(true);
    }
  }, [selectedDocument]);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setIsUploading(true);
    setUploadSuccess(false);
    setErrorMsg(null);

    try {
      const userName = `${user?.firstName || 'Admin'} ${user?.lastName || 'User'}`;
      const category = selectedCategory === 'All' ? 'Project File' : selectedCategory;
      const res = await projectWorkspaceApi.uploadDriveDocument(file, projectCode, category, userName);
      const uploadedRecord = res.data;
      setUploadSuccess(true);
      await loadDocuments(uploadedRecord?.fileId || uploadedRecord?.id);
      setTimeout(() => setUploadSuccess(false), 3000);
    } catch (err: any) {
      console.error('Upload failed:', err);
      setErrorMsg(err?.response?.data?.message || 'File upload failed. Check document server connectivity.');
    } finally {
      setIsUploading(false);
    }
  };

  const handleDeleteDocument = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this document?')) return;
    try {
      await projectWorkspaceApi.deleteDriveDocument(id);
      if (selectedDocument && (selectedDocument.id === id || selectedDocument.fileId === id)) {
        setSelectedDocument(null);
      }
      await loadDocuments();
    } catch (err: any) {
      console.error('Delete failed:', err);
      alert('Failed to delete file from document repository');
    }
  };

  const filteredDocuments = documents.filter(doc => {
    const name = doc.name || doc.originalFileName || '';
    const cat = doc.category || 'Project File';
    const matchesCategory = selectedCategory === 'All' || cat === selectedCategory;
    const matchesSearch = name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          cat.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  const getPreviewUrl = (doc: DriveFileRecord | null) => {
    if (!doc) return null;
    const fId = doc.fileId || doc.id;
    if (doc.webViewLink && doc.webViewLink.startsWith('http')) {
      return doc.webViewLink;
    }
    return `/api/v1/drive/preview/${fId}`;
  };

  const getDownloadUrl = (doc: DriveFileRecord | null) => {
    if (!doc) return null;
    const fId = doc.fileId || doc.id;
    if (doc.webContentLink && doc.webContentLink.startsWith('http')) {
      return doc.webContentLink;
    }
    return `/api/v1/drive/download/${fId}`;
  };

  const currentPreviewUrl = getPreviewUrl(selectedDocument);
  const currentDownloadUrl = getDownloadUrl(selectedDocument);

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Upload Banner Section */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-indigo-950 via-slate-900 to-indigo-950 text-white shadow-xl border border-indigo-900/50 space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 text-indigo-400 font-bold text-xs uppercase tracking-wider mb-1">
              <FolderKanban className="w-4 h-4" />
              <span>Enterprise SRS & Project Document Repository</span>
            </div>
            <h3 className="text-xl font-black text-white">
              SRS & System Architecture Documents ({documents.length})
            </h3>
            <p className="text-xs text-slate-300">
              Project Code: <span className="font-mono text-cyan-400 font-bold">{projectCode}</span>
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <button
              onClick={() => loadDocuments()}
              className="p-2.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl transition-all border border-slate-700 text-xs font-bold flex items-center gap-1.5"
              title="Refresh files"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isLoading ? 'animate-spin' : ''}`} /> Refresh
            </button>

            <label className="px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 text-xs font-extrabold rounded-xl transition-all shadow-md cursor-pointer flex items-center gap-2">
              {isUploading ? <Loader2 className="w-4 h-4 animate-spin" /> : <UploadCloud className="w-4 h-4" />}
              <span>Upload PDF / Document</span>
              <input type="file" accept=".pdf,.doc,.docx,.png,.jpg" onChange={handleFileUpload} disabled={isUploading} className="hidden" />
            </label>
          </div>
        </div>

        {uploadSuccess && (
          <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-2xl text-xs text-emerald-300 font-bold flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400" /> Document uploaded and available for instant PDF preview!
          </div>
        )}

        {errorMsg && (
          <div className="p-3 bg-rose-500/10 border border-rose-500/30 rounded-2xl text-xs text-rose-300 font-bold flex items-center gap-2">
            <AlertCircle className="w-4 h-4 text-rose-400" /> {errorMsg}
          </div>
        )}
      </div>

      {/* Main 2-Column Workspace: Left = Document List, Right = PDF Viewer */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 min-h-[600px]">
        
        {/* LEFT COLUMN: Document List & Controls (5 Cols) */}
        <div className="lg:col-span-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs flex flex-col space-y-4">
          
          <div className="flex items-center justify-between gap-2 pb-3 border-b border-slate-100 dark:border-slate-800">
            <h4 className="font-extrabold text-sm text-slate-900 dark:text-white flex items-center gap-2">
              <FileText className="w-4 h-4 text-indigo-500" /> Asset Directory
            </h4>
            <span className="text-[10px] font-mono font-bold text-slate-400">
              {filteredDocuments.length} File{filteredDocuments.length === 1 ? '' : 's'}
            </span>
          </div>

          {/* Search & Category Filters */}
          <div className="space-y-2">
            <div className="relative">
              <Search className="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                type="text"
                placeholder="Filter documents..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-8 pr-3 py-2 text-xs rounded-xl bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 text-slate-900 dark:text-slate-100 focus:outline-none focus:border-indigo-500"
              />
            </div>

            <div className="flex items-center gap-1.5 overflow-x-auto pb-1 scrollbar-none text-[11px]">
              {['All', 'Project Files', 'SRS & Architecture', 'Design Specs', 'Reports'].map(cat => (
                <button
                  key={cat}
                  onClick={() => setSelectedCategory(cat)}
                  className={`px-2.5 py-1 rounded-lg font-bold transition-all shrink-0 ${
                    selectedCategory === cat
                      ? 'bg-indigo-600 text-white'
                      : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:text-slate-900'
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>

          {/* Documents List Scroll Container */}
          <div className="flex-1 overflow-y-auto max-h-[500px] space-y-2 pr-1">
            {isLoading ? (
              <div className="py-12 text-center text-xs text-slate-400 flex items-center justify-center gap-2">
                <Loader2 className="w-4 h-4 animate-spin text-indigo-500" /> Fetching project documents...
              </div>
            ) : filteredDocuments.length === 0 ? (
              <div className="py-12 text-center space-y-2">
                <FileText className="w-8 h-8 text-slate-300 dark:text-slate-700 mx-auto" />
                <p className="text-xs font-bold text-slate-500">No documents found</p>
                <p className="text-[11px] text-slate-400">Upload a PDF to view it in the preview panel.</p>
              </div>
            ) : (
              filteredDocuments.map(doc => {
                const docId = doc.fileId || doc.id;
                const isSelected = selectedDocument && (selectedDocument.fileId === docId || selectedDocument.id === docId);
                const docName = doc.name || doc.originalFileName || 'Untitled Document';
                const isPdf = docName.toLowerCase().endsWith('.pdf') || (doc.mimeType && doc.mimeType.includes('pdf'));

                return (
                  <div
                    key={docId}
                    onClick={() => setSelectedDocument(doc)}
                    className={`p-3.5 rounded-2xl border transition-all cursor-pointer flex items-center justify-between gap-3 ${
                      isSelected
                        ? 'bg-indigo-50/80 dark:bg-indigo-950/60 border-indigo-500 shadow-xs'
                        : 'bg-slate-50/50 dark:bg-slate-950/50 border-slate-200/80 dark:border-slate-800/80 hover:border-indigo-300 dark:hover:border-indigo-700'
                    }`}
                  >
                    <div className="flex items-center gap-3 min-w-0">
                      <div className={`p-2 rounded-xl shrink-0 ${isSelected ? 'bg-indigo-600 text-white' : 'bg-slate-200 dark:bg-slate-800 text-indigo-600 dark:text-indigo-400'}`}>
                        <FileText className="w-4 h-4" />
                      </div>
                      <div className="min-w-0">
                        <h5 className="text-xs font-extrabold text-slate-900 dark:text-white truncate">
                          {docName}
                        </h5>
                        <div className="flex items-center gap-2 text-[10px] text-slate-400 font-mono">
                          <span>{doc.category || 'Project File'}</span>
                          <span>•</span>
                          <span>{doc.uploadedAt ? new Date(doc.uploadedAt).toLocaleDateString() : 'Recent'}</span>
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center gap-1 shrink-0">
                      {isPdf && (
                        <span className="px-1.5 py-0.5 bg-rose-500/10 text-rose-500 text-[9px] font-black rounded-md uppercase">
                          PDF
                        </span>
                      )}
                      <button
                        onClick={(e) => { e.stopPropagation(); handleDeleteDocument(docId); }}
                        className="p-1.5 text-slate-400 hover:text-rose-500 rounded-lg hover:bg-rose-50 dark:hover:bg-rose-950/30 transition-colors"
                        title="Delete file"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* RIGHT COLUMN: PDF / Document Viewer Container (7 Cols) */}
        <div className="lg:col-span-7 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-xs flex flex-col space-y-3">
          
          {selectedDocument ? (
            <>
              {/* Document Header & Viewer Toolbar */}
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-slate-100 dark:border-slate-800">
                <div className="min-w-0">
                  <div className="flex items-center gap-2 text-[10px] font-mono font-bold text-indigo-500">
                    <ShieldCheck className="w-3.5 h-3.5" />
                    <span>PREVIEWING DOCUMENT</span>
                  </div>
                  <h4 className="text-sm font-extrabold text-slate-900 dark:text-white truncate">
                    {selectedDocument.name || selectedDocument.originalFileName}
                  </h4>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <div className="flex items-center gap-1 bg-slate-100 dark:bg-slate-800 p-1 rounded-xl">
                    <button
                      onClick={() => setZoomLevel(Math.max(50, zoomLevel - 15))}
                      className="p-1 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-slate-700 dark:text-slate-300"
                      title="Zoom Out"
                    >
                      <ZoomOut className="w-3.5 h-3.5" />
                    </button>
                    <span className="text-[10px] font-mono font-bold px-1 text-slate-600 dark:text-slate-400">
                      {zoomLevel}%
                    </span>
                    <button
                      onClick={() => setZoomLevel(Math.min(200, zoomLevel + 15))}
                      className="p-1 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-slate-700 dark:text-slate-300"
                      title="Zoom In"
                    >
                      <ZoomIn className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => setZoomLevel(100)}
                      className="p-1 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-slate-700 dark:text-slate-300"
                      title="Reset Zoom"
                    >
                      <RotateCcw className="w-3.5 h-3.5" />
                    </button>
                  </div>

                  {currentDownloadUrl && (
                    <a
                      href={currentDownloadUrl}
                      download={selectedDocument.name || 'document.pdf'}
                      className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-xs inline-flex items-center gap-1.5"
                    >
                      <Download className="w-3.5 h-3.5" /> Download
                    </a>
                  )}

                  {currentPreviewUrl && (
                    <a
                      href={currentPreviewUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-700 dark:text-slate-300 rounded-xl"
                      title="Open in new tab"
                    >
                      <ExternalLink className="w-3.5 h-3.5" />
                    </a>
                  )}
                </div>
              </div>

              {/* PDF Viewing Container with iframe */}
              <div className="flex-1 bg-slate-950 rounded-2xl overflow-hidden relative min-h-[480px] flex flex-col">
                {isPdfLoading && (
                  <div className="absolute inset-0 z-10 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center gap-2 text-xs font-bold text-cyan-400">
                    <Loader2 className="w-5 h-5 animate-spin" /> Rendering PDF Document...
                  </div>
                )}

                {currentPreviewUrl ? (
                  <div
                    className="w-full h-full flex-1 overflow-auto transition-transform duration-200 origin-top-left"
                    style={{ transform: `scale(${zoomLevel / 100})`, width: zoomLevel > 100 ? `${100 * (100 / zoomLevel)}%` : '100%' }}
                  >
                    <iframe
                      src={currentPreviewUrl}
                      title="PDF Document Preview"
                      onLoad={() => setIsPdfLoading(false)}
                      className="w-full h-full min-h-[500px] border-none rounded-2xl"
                    />
                  </div>
                ) : (
                  <div className="flex-1 flex flex-col items-center justify-center p-8 text-center text-slate-400 space-y-3">
                    <AlertCircle className="w-8 h-8 text-amber-500" />
                    <p className="text-xs font-bold text-slate-300">Preview Stream Unavailable</p>
                    <p className="text-[11px] text-slate-500">The file preview URL is not available from storage.</p>
                  </div>
                )}
              </div>
            </>
          ) : (
            <div className="flex-1 flex flex-col items-center justify-center p-12 text-center space-y-4">
              <div className="w-16 h-16 rounded-3xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-500 flex items-center justify-center mx-auto">
                <FileText className="w-8 h-8" />
              </div>
              <div>
                <h4 className="text-base font-extrabold text-slate-900 dark:text-white">Select a Document to Preview</h4>
                <p className="text-xs text-slate-500 max-w-xs mx-auto mt-1">
                  Choose a PDF or file from the left directory to view its full content in this interactive viewer.
                </p>
              </div>
            </div>
          )}
        </div>

      </div>
    </div>
  );
};
