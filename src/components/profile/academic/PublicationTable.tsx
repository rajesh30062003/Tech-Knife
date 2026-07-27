import React, { useState, useMemo } from 'react';
import {
  Search,
  Filter,
  Plus,
  BookOpen,
  ExternalLink,
  Trash2,
  Edit2,
  FileText,
  Paperclip,
  ChevronLeft,
  ChevronRight,
  ArrowUpDown,
  CheckCircle,
  Eye,
  Calendar,
  Award,
} from 'lucide-react';
import { Publication, PublicationType, PublicationStatus } from '../../../types/faculty';
import { PublicationForm } from './PublicationForm';

interface PublicationTableProps {
  publications: Publication[];
  onCreate: (data: Omit<Publication, 'id' | 'createdAt'>) => Promise<Publication>;
  onUpdate: (id: string, data: Partial<Publication>) => Promise<Publication>;
  onDelete: (id: string) => Promise<void>;
}

export const PublicationTable: React.FC<PublicationTableProps> = ({
  publications,
  onCreate,
  onUpdate,
  onDelete,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedType, setSelectedType] = useState<string>('ALL');
  const [selectedYear, setSelectedYear] = useState<string>('ALL');
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');
  const [sortBy, setSortBy] = useState<'date' | 'citations' | 'title'>('date');
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('desc');

  // Modal states
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingPub, setEditingPub] = useState<Publication | null>(null);
  const [viewingAbstract, setViewingAbstract] = useState<Publication | null>(null);

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 5;

  // Extract available years for filtering dropdown
  const availableYears = useMemo(() => {
    const years = new Set<string>();
    publications.forEach((p) => {
      if (p.publicationDate) {
        const y = new Date(p.publicationDate).getFullYear();
        if (!isNaN(y)) years.add(y.toString());
      }
    });
    return Array.from(years).sort((a, b) => Number(b) - Number(a));
  }, [publications]);

  // Search, Filter & Sort
  const filteredPublications = useMemo(() => {
    return publications
      .filter((p) => {
        // Search query check
        const q = searchQuery.toLowerCase().trim();
        const matchesSearch =
          !q ||
          p.title.toLowerCase().includes(q) ||
          p.journal.toLowerCase().includes(q) ||
          p.authors.some((a) => a.toLowerCase().includes(q)) ||
          p.keywords?.some((k) => k.toLowerCase().includes(q));

        // Type filter
        const matchesType = selectedType === 'ALL' || p.type === selectedType;

        // Year filter
        const pubYear = new Date(p.publicationDate).getFullYear().toString();
        const matchesYear = selectedYear === 'ALL' || pubYear === selectedYear;

        // Status filter
        const matchesStatus = selectedStatus === 'ALL' || p.status === selectedStatus;

        return matchesSearch && matchesType && matchesYear && matchesStatus;
      })
      .sort((a, b) => {
        let comp = 0;
        if (sortBy === 'date') {
          comp = new Date(a.publicationDate).getTime() - new Date(b.publicationDate).getTime();
        } else if (sortBy === 'citations') {
          comp = (a.citationsCount || 0) - (b.citationsCount || 0);
        } else if (sortBy === 'title') {
          comp = a.title.localeCompare(b.title);
        }
        return sortOrder === 'desc' ? -comp : comp;
      });
  }, [publications, searchQuery, selectedType, selectedYear, selectedStatus, sortBy, sortOrder]);

  // Paginated dataset
  const totalPages = Math.ceil(filteredPublications.length / pageSize) || 1;
  const paginatedPublications = useMemo(() => {
    const start = (currentPage - 1) * pageSize;
    return filteredPublications.slice(start, start + pageSize);
  }, [filteredPublications, currentPage]);

  const existingTitles = useMemo(() => publications.map((p) => p.title), [publications]);

  const handleCreateOrUpdate = async (data: Omit<Publication, 'id' | 'createdAt'>) => {
    if (editingPub) {
      await onUpdate(editingPub.id, data);
    } else {
      await onCreate(data);
    }
  };

  const handleDeletePublication = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this publication from your portfolio?')) {
      await onDelete(id);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-6">
      {/* Header & Launcher */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
        <div>
          <span className="text-[10px] font-bold text-indigo-500 uppercase tracking-wider">Publications & Academic Papers</span>
          <h2 className="text-xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-indigo-500" /> Research Publications
          </h2>
        </div>

        <button
          onClick={() => {
            setEditingPub(null);
            setIsFormOpen(true);
          }}
          className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-2xl text-xs font-bold shadow-lg transition-all flex items-center gap-2"
        >
          <Plus className="w-4 h-4" /> Add Publication
        </button>
      </div>

      {/* Search & Filter Controls Toolbar */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-5 gap-3">
        {/* Search Input */}
        <div className="md:col-span-2 relative">
          <Search className="w-4 h-4 absolute left-3.5 top-3 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
              setCurrentPage(1);
            }}
            placeholder="Search by title, author, journal, or keyword..."
            className="w-full pl-10 pr-4 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* Filter by Type */}
        <div>
          <select
            value={selectedType}
            onChange={(e) => {
              setSelectedType(e.target.value);
              setCurrentPage(1);
            }}
            className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs text-slate-700 dark:text-slate-300 focus:ring-2 focus:ring-indigo-500 font-medium"
          >
            <option value="ALL">All Publication Types</option>
            <option value="Journal">Journal</option>
            <option value="Conference">Conference</option>
            <option value="Book">Book</option>
            <option value="Book Chapter">Book Chapter</option>
            <option value="Case Study">Case Study</option>
            <option value="Magazine Article">Magazine Article</option>
            <option value="Editorial Work">Editorial Work</option>
          </select>
        </div>

        {/* Filter by Year */}
        <div>
          <select
            value={selectedYear}
            onChange={(e) => {
              setSelectedYear(e.target.value);
              setCurrentPage(1);
            }}
            className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs text-slate-700 dark:text-slate-300 focus:ring-2 focus:ring-indigo-500 font-medium"
          >
            <option value="ALL">All Publication Years</option>
            {availableYears.map((y) => (
              <option key={y} value={y}>
                Year {y}
              </option>
            ))}
          </select>
        </div>

        {/* Sort selector */}
        <div>
          <select
            value={`${sortBy}-${sortOrder}`}
            onChange={(e) => {
              const [sb, so] = e.target.value.split('-') as ['date' | 'citations' | 'title', 'asc' | 'desc'];
              setSortBy(sb);
              setSortOrder(so);
            }}
            className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs text-slate-700 dark:text-slate-300 focus:ring-2 focus:ring-indigo-500 font-medium"
          >
            <option value="date-desc">Newest First</option>
            <option value="date-asc">Oldest First</option>
            <option value="citations-desc">Most Cited First</option>
            <option value="citations-asc">Least Cited First</option>
            <option value="title-asc">Title (A-Z)</option>
          </select>
        </div>
      </div>

      {/* Publications List Table */}
      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs border-collapse">
          <thead>
            <tr className="border-b border-slate-200 dark:border-slate-800 text-slate-400 font-extrabold uppercase tracking-wider text-[10px]">
              <th className="py-3 px-3">Publication Details</th>
              <th className="py-3 px-3">Journal / Publisher</th>
              <th className="py-3 px-3">Type & Status</th>
              <th className="py-3 px-3 text-center">Citations</th>
              <th className="py-3 px-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
            {paginatedPublications.length === 0 ? (
              <tr>
                <td colSpan={5} className="py-8 text-center text-slate-500">
                  No matching publications found in records.
                </td>
              </tr>
            ) : (
              paginatedPublications.map((pub) => (
                <tr key={pub.id} className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors">
                  {/* Title & Authors */}
                  <td className="py-4 px-3 space-y-1 max-w-md">
                    <h4 className="font-extrabold text-slate-900 dark:text-white leading-snug">{pub.title}</h4>
                    <p className="text-[11px] text-slate-500 font-medium">
                      Authors: <span className="text-slate-700 dark:text-slate-300">{pub.authors.join(', ')}</span>
                    </p>
                    {pub.doi && (
                      <span className="inline-block text-[10px] font-mono text-indigo-500">
                        DOI: {pub.doi}
                      </span>
                    )}
                  </td>

                  {/* Journal & Metadata */}
                  <td className="py-4 px-3 space-y-1">
                    <p className="font-bold text-slate-800 dark:text-slate-200">{pub.journal}</p>
                    <div className="flex flex-wrap gap-2 text-[11px] text-slate-400 font-mono">
                      <span>Date: {pub.publicationDate}</span>
                      {pub.volume && <span>Vol: {pub.volume}</span>}
                      {pub.pages && <span>pp: {pub.pages}</span>}
                    </div>
                  </td>

                  {/* Type & Status badge */}
                  <td className="py-4 px-3 space-y-1.5">
                    <span className="inline-block px-2.5 py-0.5 text-[10px] font-bold rounded-full bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20">
                      {pub.type}
                    </span>
                    <div>
                      <span
                        className={`inline-block px-2.5 py-0.5 text-[10px] font-bold rounded-full ${
                          pub.status === 'Published'
                            ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400'
                            : pub.status === 'Accepted'
                            ? 'bg-blue-500/10 text-blue-600'
                            : 'bg-amber-500/10 text-amber-600'
                        }`}
                      >
                        {pub.status}
                      </span>
                    </div>
                  </td>

                  {/* Citations Count */}
                  <td className="py-4 px-3 text-center">
                    <span className="font-mono font-extrabold text-sm text-slate-900 dark:text-white">
                      {pub.citationsCount || 0}
                    </span>
                  </td>

                  {/* Action Buttons */}
                  <td className="py-4 px-3 text-right space-x-1 whitespace-nowrap">
                    {pub.abstract && (
                      <button
                        onClick={() => setViewingAbstract(pub)}
                        className="p-1.5 text-slate-400 hover:text-indigo-600 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800"
                        title="View Abstract"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                    )}
                    {pub.externalUrl && (
                      <a
                        href={pub.externalUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-block p-1.5 text-slate-400 hover:text-emerald-600 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800"
                        title="Open External URL"
                      >
                        <ExternalLink className="w-4 h-4" />
                      </a>
                    )}
                    <button
                      onClick={() => {
                        setEditingPub(pub);
                        setIsFormOpen(true);
                      }}
                      className="p-1.5 text-slate-400 hover:text-blue-600 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800"
                      title="Edit Publication"
                    >
                      <Edit2 className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDeletePublication(pub.id)}
                      className="p-1.5 text-slate-400 hover:text-red-600 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800"
                      title="Delete Publication"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Footer */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-500 pt-2 border-t border-slate-100 dark:border-slate-800">
        <div>
          Showing {filteredPublications.length > 0 ? (currentPage - 1) * pageSize + 1 : 0} to{' '}
          {Math.min(currentPage * pageSize, filteredPublications.length)} of {filteredPublications.length} publications
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
            disabled={currentPage === 1}
            className="p-1.5 border border-slate-200 dark:border-slate-700 rounded-lg disabled:opacity-40 hover:bg-slate-100 dark:hover:bg-slate-800"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
          <span className="font-mono font-bold">
            Page {currentPage} of {totalPages}
          </span>
          <button
            onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
            disabled={currentPage === totalPages}
            className="p-1.5 border border-slate-200 dark:border-slate-700 rounded-lg disabled:opacity-40 hover:bg-slate-100 dark:hover:bg-slate-800"
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Publication Form Modal */}
      {isFormOpen && (
        <PublicationForm
          initialData={editingPub}
          onSubmit={handleCreateOrUpdate}
          onClose={() => {
            setIsFormOpen(false);
            setEditingPub(null);
          }}
          existingTitles={existingTitles}
        />
      )}

      {/* Abstract Preview Modal */}
      {viewingAbstract && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl max-w-lg w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="text-sm font-extrabold text-slate-900 dark:text-white">Publication Abstract</h3>
              <button
                onClick={() => setViewingAbstract(null)}
                className="text-slate-400 hover:text-slate-600"
              >
                &times;
              </button>
            </div>
            <h4 className="text-xs font-bold text-indigo-600 dark:text-indigo-400">{viewingAbstract.title}</h4>
            <p className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed bg-slate-50 dark:bg-slate-800 p-4 rounded-2xl border border-slate-200 dark:border-slate-700">
              {viewingAbstract.abstract}
            </p>
            {viewingAbstract.keywords && viewingAbstract.keywords.length > 0 && (
              <div className="flex flex-wrap gap-1.5">
                {viewingAbstract.keywords.map((kw, i) => (
                  <span
                    key={i}
                    className="px-2 py-0.5 text-[10px] font-mono bg-indigo-500/10 text-indigo-500 rounded-md"
                  >
                    #{kw}
                  </span>
                ))}
              </div>
            )}
            <div className="flex justify-end pt-2">
              <button
                onClick={() => setViewingAbstract(null)}
                className="px-4 py-2 bg-indigo-600 text-white text-xs font-bold rounded-xl"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
