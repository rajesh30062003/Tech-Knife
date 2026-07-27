import React, { useState, useMemo } from 'react';
import {
  ChevronDown, ChevronUp, Search, Download, Printer, Filter,
  Trash2, CheckSquare, Square, Eye, MoreHorizontal, ArrowUpDown, RefreshCw, Columns
} from 'lucide-react';

export interface Column<T> {
  key: string;
  header: string;
  accessor: (item: T) => React.ReactNode;
  sortable?: boolean;
  sortKey?: keyof T;
  width?: string;
}

interface DataTableProps<T extends { id: string }> {
  data: T[];
  columns: Column<T>[];
  title?: string;
  subtitle?: string;
  searchPlaceholder?: string;
  onBulkDelete?: (selectedIds: string[]) => void;
  onBulkStatusUpdate?: (selectedIds: string[], status: string) => void;
  onRowClick?: (item: T) => void;
  isLoading?: boolean;
}

export function DataTable<T extends { id: string }>({
  data,
  columns,
  title,
  subtitle,
  searchPlaceholder = 'Quick filter records...',
  onBulkDelete,
  onBulkStatusUpdate,
  onRowClick,
  isLoading = false,
}: DataTableProps<T>) {
  const [searchTerm, setSearchTerm] = useState('');
  const [sortField, setSortField] = useState<string | null>(null);
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [visibleColumns, setVisibleColumns] = useState<string[]>(
    columns.map((c) => c.key)
  );
  const [showColumnMenu, setShowColumnMenu] = useState(false);
  const [pageSize, setPageSize] = useState(10);
  const [currentPage, setCurrentPage] = useState(1);

  // Quick filter logic
  const filteredData = useMemo(() => {
    if (!searchTerm.trim()) return data;
    const lower = searchTerm.toLowerCase();
    return data.filter((item) =>
      Object.values(item as Record<string, any>).some((val) =>
        String(val).toLowerCase().includes(lower)
      )
    );
  }, [data, searchTerm]);

  // Sorting logic
  const sortedData = useMemo(() => {
    if (!sortField) return filteredData;
    return [...filteredData].sort((a: any, b: any) => {
      const aVal = a[sortField];
      const bVal = b[sortField];
      if (aVal < bVal) return sortDirection === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }, [filteredData, sortField, sortDirection]);

  // Pagination logic
  const totalPages = Math.ceil(sortedData.length / pageSize) || 1;
  const paginatedData = sortedData.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  const handleSelectAll = () => {
    if (selectedIds.length === paginatedData.length) {
      setSelectedIds([]);
    } else {
      setSelectedIds(paginatedData.map((d) => d.id));
    }
  };

  const handleSelectRow = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    if (selectedIds.includes(id)) {
      setSelectedIds(selectedIds.filter((i) => i !== id));
    } else {
      setSelectedIds([...selectedIds, id]);
    }
  };

  const handleSort = (colKey: string, isSortable?: boolean) => {
    if (!isSortable) return;
    if (sortField === colKey) {
      if (sortDirection === 'asc') setSortDirection('desc');
      else setSortField(null);
    } else {
      setSortField(colKey);
      setSortDirection('asc');
    }
  };

  const handleExportCSV = () => {
    if (!data.length) return;
    const activeCols = columns.filter((c) => visibleColumns.includes(c.key));
    const headers = activeCols.map((c) => `"${c.header}"`).join(',');
    const rows = sortedData.map((item) =>
      activeCols
        .map((c) => {
          const val = (item as any)[c.key] ?? '';
          return `"${String(val).replace(/"/g, '""')}"`;
        })
        .join(',')
    );
    const csvContent = 'data:text/csv;charset=utf-8,' + [headers, ...rows].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `${title || 'export'}_${Date.now()}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handlePrint = () => {
    window.print();
  };

  const activeColumnsList = columns.filter((c) => visibleColumns.includes(c.key));

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-sm overflow-hidden space-y-3">
      {/* Table Header Controls */}
      <div className="p-4 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-100 dark:border-slate-800">
        <div>
          {title && <h3 className="text-base font-extrabold text-slate-900 dark:text-white">{title}</h3>}
          {subtitle && <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">{subtitle}</p>}
        </div>

        <div className="flex flex-wrap items-center gap-2.5 w-full sm:w-auto">
          {/* Quick Search */}
          <div className="relative flex-1 sm:w-64">
            <Search className="w-3.5 h-3.5 absolute left-3 top-2.5 text-slate-400" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder={searchPlaceholder}
              className="w-full pl-8 pr-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-800 dark:text-slate-200"
            />
          </div>

          {/* Column Visibility */}
          <div className="relative">
            <button
              onClick={() => setShowColumnMenu(!showColumnMenu)}
              className="px-2.5 py-1.5 text-xs font-semibold rounded-xl border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 flex items-center gap-1.5"
            >
              <Columns className="w-3.5 h-3.5" />
              <span>Columns</span>
            </button>
            {showColumnMenu && (
              <div className="absolute right-0 mt-2 w-48 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl p-2 z-30 space-y-1 text-xs">
                <div className="px-2 py-1 font-bold text-slate-400 uppercase text-[10px]">Toggle Columns</div>
                {columns.map((c) => (
                  <label key={c.key} className="flex items-center gap-2 px-2 py-1 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-lg cursor-pointer">
                    <input
                      type="checkbox"
                      checked={visibleColumns.includes(c.key)}
                      onChange={(e) => {
                        if (e.target.checked) setVisibleColumns([...visibleColumns, c.key]);
                        else setVisibleColumns(visibleColumns.filter((k) => k !== c.key));
                      }}
                      className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                    />
                    <span className="text-slate-800 dark:text-slate-200 font-medium">{c.header}</span>
                  </label>
                ))}
              </div>
            )}
          </div>

          {/* Export & Print */}
          <button
            onClick={handleExportCSV}
            className="px-2.5 py-1.5 text-xs font-semibold rounded-xl border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 flex items-center gap-1.5"
            title="Export CSV"
          >
            <Download className="w-3.5 h-3.5" />
            <span className="hidden md:inline">CSV</span>
          </button>
          <button
            onClick={handlePrint}
            className="px-2.5 py-1.5 text-xs font-semibold rounded-xl border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 flex items-center gap-1.5"
            title="Print Table"
          >
            <Printer className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>

      {/* Bulk Action Bar */}
      {selectedIds.length > 0 && (
        <div className="mx-4 p-2.5 bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 rounded-xl flex items-center justify-between text-xs font-semibold text-blue-900 dark:text-blue-200">
          <div className="flex items-center gap-2">
            <CheckSquare className="w-4 h-4 text-blue-600" />
            <span>{selectedIds.length} items selected across page</span>
          </div>
          <div className="flex items-center gap-2">
            {onBulkDelete && (
              <button
                onClick={() => onBulkDelete(selectedIds)}
                className="px-3 py-1 bg-rose-600 hover:bg-rose-500 text-white rounded-lg font-bold flex items-center gap-1 shadow-sm"
              >
                <Trash2 className="w-3.5 h-3.5" />
                <span>Bulk Delete</span>
              </button>
            )}
            <button
              onClick={() => setSelectedIds([])}
              className="text-xs text-slate-500 hover:underline px-2"
            >
              Clear Selection
            </button>
          </div>
        </div>
      )}

      {/* Table Element */}
      <div className="overflow-x-auto max-h-[600px] overflow-y-auto">
        <table className="w-full text-left border-collapse text-xs">
          <thead className="sticky top-0 bg-slate-100 dark:bg-slate-800/90 backdrop-blur-md z-10 text-slate-600 dark:text-slate-300 uppercase font-bold text-[11px] border-b border-slate-200 dark:border-slate-700">
            <tr>
              <th className="p-3 w-10 text-center">
                <input
                  type="checkbox"
                  checked={selectedIds.length > 0 && selectedIds.length === paginatedData.length}
                  onChange={handleSelectAll}
                  className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                />
              </th>
              {activeColumnsList.map((col) => (
                <th
                  key={col.key}
                  onClick={() => handleSort(col.key, col.sortable)}
                  className={`p-3 select-none ${col.sortable ? 'cursor-pointer hover:text-blue-600 dark:hover:text-blue-400' : ''}`}
                  style={{ width: col.width }}
                >
                  <div className="flex items-center gap-1">
                    <span>{col.header}</span>
                    {col.sortable && (
                      <ArrowUpDown className="w-3 h-3 text-slate-400 shrink-0" />
                    )}
                  </div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
            {isLoading ? (
              <tr>
                <td colSpan={activeColumnsList.length + 1} className="p-8 text-center text-slate-400">
                  <RefreshCw className="w-6 h-6 animate-spin mx-auto mb-2 text-blue-500" />
                  <span>Loading enterprise dataset...</span>
                </td>
              </tr>
            ) : paginatedData.length === 0 ? (
              <tr>
                <td colSpan={activeColumnsList.length + 1} className="p-8 text-center text-slate-400 font-medium">
                  No matching records found.
                </td>
              </tr>
            ) : (
              paginatedData.map((item) => {
                const isSelected = selectedIds.includes(item.id);
                return (
                  <tr
                    key={item.id}
                    onClick={() => onRowClick && onRowClick(item)}
                    className={`hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors ${
                      isSelected ? 'bg-blue-50/50 dark:bg-blue-950/30' : ''
                    } ${onRowClick ? 'cursor-pointer' : ''}`}
                  >
                    <td className="p-3 text-center" onClick={(e) => e.stopPropagation()}>
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={(e) => handleSelectRow(item.id, e as any)}
                        className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                      />
                    </td>
                    {activeColumnsList.map((col) => (
                      <td key={col.key} className="p-3">
                        {col.accessor(item)}
                      </td>
                    ))}
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Controls */}
      <div className="p-4 flex flex-col sm:flex-row items-center justify-between gap-3 border-t border-slate-100 dark:border-slate-800 text-xs font-medium text-slate-500 dark:text-slate-400">
        <div className="flex items-center gap-2">
          <span>Show</span>
          <select
            value={pageSize}
            onChange={(e) => {
              setPageSize(Number(e.target.value));
              setCurrentPage(1);
            }}
            className="p-1 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg text-slate-800 dark:text-slate-200"
          >
            <option value={5}>5</option>
            <option value={10}>10</option>
            <option value={25}>25</option>
            <option value={50}>50</option>
          </select>
          <span>entries per page (Total {sortedData.length})</span>
        </div>

        <div className="flex items-center gap-2">
          <button
            disabled={currentPage === 1}
            onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
            className="px-3 py-1 rounded-lg border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40"
          >
            Previous
          </button>
          <span className="font-bold text-slate-800 dark:text-slate-200">
            Page {currentPage} of {totalPages}
          </span>
          <button
            disabled={currentPage === totalPages}
            onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
            className="px-3 py-1 rounded-lg border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40"
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
}
