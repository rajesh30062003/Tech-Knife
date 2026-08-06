import React, { useState } from 'react';
import { 
  BookOpen, FileText, Search, Plus, Folder, Edit3, Shield, 
  ExternalLink, Sparkles, ChevronRight, Bookmark, Tag
} from 'lucide-react';

interface WikiArticle {
  id: string;
  title: string;
  category: 'Architecture' | 'SOP' | 'Security' | 'API Spec';
  author: string;
  updatedAt: string;
  content: string;
}

const MOCK_WIKI_ARTICLES: WikiArticle[] = [
  {
    id: 'wiki-1',
    title: 'Google Drive OAuth 2.0 Offline Token Refresh Protocol',
    category: 'Architecture',
    author: 'Vikramaditya Sharma',
    updatedAt: '2026-08-05',
    content: 'This document specifies the OAuth 2.0 Authorization Code flow for Google Drive integrations under storageQuotaExceeded constraints. Refresh tokens are stored encrypted in MongoDB Atlas and auto-rotated prior to API call execution.',
  },
  {
    id: 'wiki-2',
    title: 'Standard Operating Procedure (SOP): Production Incident Response',
    category: 'SOP',
    author: 'Security Governance',
    updatedAt: '2026-07-20',
    content: 'Step 1: Check K8s pod restart counters. Step 2: Extract un-truncated Spring Boot 3.5 logs via log viewer. Step 3: Trigger P2 incident alert to CTO on duty if SLA boundary is breached.',
  },
  {
    id: 'wiki-3',
    title: 'REST API & STOMP WebSocket Schema Reference',
    category: 'API Spec',
    author: 'Engineering Lead',
    updatedAt: '2026-08-01',
    content: 'Comprehensive reference of all /api/v1 endpoints, MongoDB collection schemas, and STOMP topic destinations (/topic/project.{code}).',
  },
];

export const EnterpriseWikiWorkspace: React.FC = () => {
  const [articles, setArticles] = useState<WikiArticle[]>(MOCK_WIKI_ARTICLES);
  const [selectedArticle, setSelectedArticle] = useState<WikiArticle>(MOCK_WIKI_ARTICLES[0]);
  const [searchTerm, setSearchTerm] = useState('');

  const filtered = articles.filter(a => a.title.toLowerCase().includes(searchTerm.toLowerCase()) || a.category.toLowerCase().includes(searchTerm.toLowerCase()));

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* Header Banner */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-slate-950 via-indigo-950 to-slate-950 border border-indigo-900/40 text-white shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-3 py-1 bg-indigo-500/20 text-indigo-300 font-mono text-xs font-bold rounded-full border border-indigo-500/30 flex items-center gap-1.5">
              <BookOpen className="w-3.5 h-3.5" /> Enterprise Knowledge Base & Wiki
            </span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black tracking-tight">Architecture Specs, RFCs, SOPs & Knowledge Documents</h2>
          <p className="text-xs text-slate-400 font-medium">Confluence-style Knowledge Repository • Markdown Versioning • Vector Indexable</p>
        </div>

        <button className="px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-2xl shadow-md transition-all flex items-center gap-2 shrink-0">
          <Plus className="w-3.5 h-3.5" /> Create Article
        </button>
      </div>

      {/* Main Layout Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        
        {/* Left Article Navigation Tree */}
        <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
          <div className="relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search wiki & SOPs..."
              className="w-full text-xs pl-9 pr-3 py-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 font-medium"
            />
          </div>

          <div className="space-y-2">
            {filtered.map((art) => (
              <div
                key={art.id}
                onClick={() => setSelectedArticle(art)}
                className={`p-3 rounded-2xl cursor-pointer transition-all ${
                  selectedArticle.id === art.id
                    ? 'bg-indigo-600 text-white shadow-md'
                    : 'bg-slate-50 dark:bg-slate-800/40 text-slate-700 dark:text-slate-300 hover:bg-slate-100'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className={`px-2 py-0.5 rounded-md font-mono text-[9px] font-bold uppercase ${
                    selectedArticle.id === art.id ? 'bg-indigo-500 text-white' : 'bg-slate-200 dark:bg-slate-700 text-slate-600 dark:text-slate-300'
                  }`}>
                    {art.category}
                  </span>
                  <span className="text-[10px] opacity-75 font-mono">{art.updatedAt}</span>
                </div>
                <h4 className="text-xs font-extrabold mt-1.5 line-clamp-1">{art.title}</h4>
              </div>
            ))}
          </div>
        </div>

        {/* Right Selected Article Viewer */}
        <div className="md:col-span-2 p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
          <div className="border-b border-slate-100 dark:border-slate-800 pb-4 space-y-2">
            <div className="flex items-center gap-2">
              <span className="px-2.5 py-0.5 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 font-mono text-xs font-bold rounded-md">
                {selectedArticle.category}
              </span>
              <span className="text-xs text-slate-400 font-medium">Author: {selectedArticle.author}</span>
            </div>
            <h1 className="text-xl font-extrabold text-slate-900 dark:text-white">{selectedArticle.title}</h1>
          </div>

          <div className="text-xs text-slate-700 dark:text-slate-300 leading-relaxed font-medium space-y-4">
            <p>{selectedArticle.content}</p>
          </div>
        </div>

      </div>

    </div>
  );
};
