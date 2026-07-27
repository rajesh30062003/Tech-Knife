import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { X, FileText, Upload, Plus, Trash2, AlertCircle, Link as LinkIcon, CheckCircle } from 'lucide-react';
import { Publication, PublicationType, PublicationStatus, PublicationAttachment } from '../../../types/faculty';

const publicationSchema = z.object({
  title: z.string().min(5, 'Title must be at least 5 characters long'),
  authorsString: z.string().min(2, 'At least one author is required'),
  journal: z.string().min(2, 'Journal / Conference / Publisher name is required'),
  publisher: z.string().optional(),
  issnIsbn: z.string().optional(),
  volume: z.string().optional(),
  issue: z.string().optional(),
  pages: z.string().optional(),
  doi: z.string().optional(),
  publicationDate: z.string().refine((val) => !isNaN(Date.parse(val)), {
    message: 'Valid publication date is required',
  }),
  abstract: z.string().optional(),
  keywordsString: z.string().optional(),
  externalUrl: z.string().url('Invalid URL format').or(z.literal('')).optional(),
  type: z.enum([
    'Journal',
    'Conference',
    'Book',
    'Book Chapter',
    'Case Study',
    'Magazine Article',
    'Editorial Work',
  ] as const),
  status: z.enum(['Published', 'Accepted', 'Under Review', 'Draft'] as const),
  citationsCount: z.coerce.number().min(0).optional(),
});

type PublicationFormData = z.infer<typeof publicationSchema>;

interface PublicationFormProps {
  initialData?: Publication | null;
  onSubmit: (data: Omit<Publication, 'id' | 'createdAt'>) => Promise<void>;
  onClose: () => void;
  existingTitles?: string[];
}

export const PublicationForm: React.FC<PublicationFormProps> = ({
  initialData,
  onSubmit,
  onClose,
  existingTitles = [],
}) => {
  const [serverError, setServerError] = useState<string | null>(null);
  const [attachments, setAttachments] = useState<PublicationAttachment[]>(
    initialData?.attachments || []
  );
  const [isUploading, setIsUploading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<PublicationFormData>({
    resolver: zodResolver(publicationSchema) as any,
    defaultValues: {
      title: initialData?.title || '',
      authorsString: initialData?.authors.join(', ') || '',
      journal: initialData?.journal || '',
      publisher: initialData?.publisher || '',
      issnIsbn: initialData?.issnIsbn || '',
      volume: initialData?.volume || '',
      issue: initialData?.issue || '',
      pages: initialData?.pages || '',
      doi: initialData?.doi || '',
      publicationDate: initialData?.publicationDate || new Date().toISOString().split('T')[0],
      abstract: initialData?.abstract || '',
      keywordsString: initialData?.keywords?.join(', ') || '',
      externalUrl: initialData?.externalUrl || '',
      type: initialData?.type || 'Journal',
      status: initialData?.status || 'Published',
      citationsCount: initialData?.citationsCount || 0,
    },
  });

  const handleSimulatedFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    setIsUploading(true);
    setTimeout(() => {
      const fileArray: File[] = Array.from(files);
      const newItems: PublicationAttachment[] = fileArray.map((f: File) => ({
        name: f.name,
        url: URL.createObjectURL(f),
        size: `${(f.size / (1024 * 1024)).toFixed(1)} MB`,
      }));
      setAttachments((prev) => [...prev, ...newItems]);
      setIsUploading(false);
    }, 400);
  };

  const removeAttachment = (index: number) => {
    setAttachments((prev) => prev.filter((_, i) => i !== index));
  };

  const onFormSubmit = async (data: PublicationFormData) => {
    setServerError(null);

    // Duplicate check prevention (client check)
    if (
      !initialData &&
      existingTitles.some(
        (t) => t.trim().toLowerCase() === data.title.trim().toLowerCase()
      )
    ) {
      setServerError('A publication with this exact title already exists in your records.');
      return;
    }

    const authors = data.authorsString
      .split(',')
      .map((a) => a.trim())
      .filter(Boolean);

    const keywords = data.keywordsString
      ? data.keywordsString
          .split(',')
          .map((k) => k.trim())
          .filter(Boolean)
      : [];

    try {
      await onSubmit({
        title: data.title,
        authors,
        journal: data.journal,
        publisher: data.publisher,
        issnIsbn: data.issnIsbn,
        volume: data.volume,
        issue: data.issue,
        pages: data.pages,
        doi: data.doi,
        publicationDate: data.publicationDate,
        abstract: data.abstract,
        keywords,
        attachments,
        externalUrl: data.externalUrl,
        type: data.type as PublicationType,
        status: data.status as PublicationStatus,
        citationsCount: data.citationsCount || 0,
      });
      onClose();
    } catch (err: any) {
      setServerError(err.message || 'An error occurred while saving the publication.');
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-2xl w-full p-6 sm:p-8 shadow-2xl relative space-y-6 my-8">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
          <div className="flex items-center gap-2">
            <div className="p-2 bg-indigo-500/10 text-indigo-600 rounded-xl">
              <FileText className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-lg font-extrabold text-slate-900 dark:text-white">
                {initialData ? 'Edit Publication Record' : 'Add New Publication'}
              </h3>
              <p className="text-xs text-slate-500">Record journal articles, conference papers, books, or chapters.</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-full"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {serverError && (
          <div className="p-4 bg-red-500/10 border border-red-500/20 text-red-600 dark:text-red-400 rounded-2xl text-xs flex items-center gap-2">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{serverError}</span>
          </div>
        )}

        <form onSubmit={handleSubmit(onFormSubmit as any)} className="space-y-4">
          {/* Publication Title */}
          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
              Publication Title <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              {...register('title')}
              placeholder="e.g. Attention-Guided Vision Transformers for High-Resolution Medical Image Segmentation"
              className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3.5 py-2 text-xs focus:ring-2 focus:ring-indigo-500 text-slate-900 dark:text-white"
            />
            {errors.title && (
              <p className="text-[11px] text-red-500 mt-1">{errors.title.message}</p>
            )}
          </div>

          {/* Authors & Type/Status row */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Authors (comma separated) <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                {...register('authorsString')}
                placeholder="Dr. Alexander Vance, Elena Rostova, Aarav Mehta"
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3.5 py-2 text-xs focus:ring-2 focus:ring-indigo-500 text-slate-900 dark:text-white"
              />
              {errors.authorsString && (
                <p className="text-[11px] text-red-500 mt-1">{errors.authorsString.message}</p>
              )}
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Journal / Conference / Publisher <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                {...register('journal')}
                placeholder="IEEE Transactions on Pattern Analysis and Machine Intelligence"
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3.5 py-2 text-xs focus:ring-2 focus:ring-indigo-500 text-slate-900 dark:text-white"
              />
              {errors.journal && (
                <p className="text-[11px] text-red-500 mt-1">{errors.journal.message}</p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Publication Type
              </label>
              <select
                {...register('type')}
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-indigo-500 text-slate-900 dark:text-white"
              >
                <option value="Journal">Journal</option>
                <option value="Conference">Conference</option>
                <option value="Book">Book</option>
                <option value="Book Chapter">Book Chapter</option>
                <option value="Case Study">Case Study</option>
                <option value="Magazine Article">Magazine Article</option>
                <option value="Editorial Work">Editorial Work</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Status
              </label>
              <select
                {...register('status')}
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-indigo-500 text-slate-900 dark:text-white"
              >
                <option value="Published">Published</option>
                <option value="Accepted">Accepted</option>
                <option value="Under Review">Under Review</option>
                <option value="Draft">Draft</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Publication Date <span className="text-red-500">*</span>
              </label>
              <input
                type="date"
                {...register('publicationDate')}
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-indigo-500 text-slate-900 dark:text-white"
              />
              {errors.publicationDate && (
                <p className="text-[11px] text-red-500 mt-1">{errors.publicationDate.message}</p>
              )}
            </div>
          </div>

          {/* Volume, Issue, Pages, DOI */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-bold mb-1">Volume</label>
              <input
                type="text"
                {...register('volume')}
                placeholder="e.g. 45"
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3 py-1.5 text-xs"
              />
            </div>
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-bold mb-1">Issue</label>
              <input
                type="text"
                {...register('issue')}
                placeholder="e.g. 8"
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3 py-1.5 text-xs"
              />
            </div>
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-bold mb-1">Pages</label>
              <input
                type="text"
                {...register('pages')}
                placeholder="10214-10228"
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3 py-1.5 text-xs"
              />
            </div>
            <div>
              <label className="block text-slate-600 dark:text-slate-400 font-bold mb-1">ISSN / ISBN</label>
              <input
                type="text"
                {...register('issnIsbn')}
                placeholder="0162-8828"
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3 py-1.5 text-xs"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                DOI String
              </label>
              <input
                type="text"
                {...register('doi')}
                placeholder="10.1109/TPAMI.2023.3289012"
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3.5 py-2 text-xs font-mono"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                External Publication URL
              </label>
              <input
                type="text"
                {...register('externalUrl')}
                placeholder="https://doi.org/10.1109/..."
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3.5 py-2 text-xs font-mono"
              />
              {errors.externalUrl && (
                <p className="text-[11px] text-red-500 mt-1">{errors.externalUrl.message}</p>
              )}
            </div>
          </div>

          {/* Abstract */}
          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
              Abstract
            </label>
            <textarea
              rows={3}
              {...register('abstract')}
              placeholder="Brief summary or executive abstract of the publication..."
              className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3.5 py-2 text-xs text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          {/* Keywords & Citations */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Keywords (comma separated)
              </label>
              <input
                type="text"
                {...register('keywordsString')}
                placeholder="Vision Transformer, Medical AI, Deep Learning"
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3.5 py-2 text-xs"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Citations Count
              </label>
              <input
                type="number"
                {...register('citationsCount')}
                placeholder="0"
                className="w-full bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-3.5 py-2 text-xs font-mono"
              />
            </div>
          </div>

          {/* Attachments Section */}
          <div className="space-y-2 pt-2 border-t border-slate-100 dark:border-slate-800">
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300">
              Attach PDF / Pre-prints / Certificates
            </label>
            <div className="flex flex-wrap items-center gap-2">
              <label className="cursor-pointer inline-flex items-center gap-1.5 px-3 py-1.5 bg-indigo-500/10 hover:bg-indigo-500/20 text-indigo-600 dark:text-indigo-400 rounded-xl text-xs font-bold transition-all">
                <Upload className="w-3.5 h-3.5" />
                <span>{isUploading ? 'Uploading...' : 'Choose PDF File'}</span>
                <input
                  type="file"
                  accept=".pdf,.doc,.docx"
                  onChange={handleSimulatedFileUpload}
                  className="hidden"
                  disabled={isUploading}
                />
              </label>
            </div>

            {attachments.length > 0 && (
              <div className="space-y-1.5 pt-2">
                {attachments.map((att, idx) => (
                  <div
                    key={idx}
                    className="flex items-center justify-between p-2 bg-slate-50 dark:bg-slate-800 rounded-xl text-xs border border-slate-200 dark:border-slate-700"
                  >
                    <span className="font-mono text-slate-700 dark:text-slate-300 truncate max-w-xs">{att.name}</span>
                    <button
                      type="button"
                      onClick={() => removeAttachment(idx)}
                      className="p-1 text-red-500 hover:text-red-700"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Form Controls */}
          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100 dark:border-slate-800">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-xs font-bold text-slate-500 hover:text-slate-700 dark:hover:text-slate-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-xs font-bold shadow-lg transition-all flex items-center gap-1.5"
            >
              <CheckCircle className="w-4 h-4" />
              <span>{isSubmitting ? 'Saving...' : initialData ? 'Update Record' : 'Save Publication'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
