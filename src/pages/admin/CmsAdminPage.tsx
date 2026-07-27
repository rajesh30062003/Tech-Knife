import React, { useState } from 'react';
import { motion } from 'motion/react';
import {
  Globe, Layout, FileText, Image as ImageIcon, MessageSquareQuote,
  Briefcase, GraduationCap, Users, Handshake, BookOpen, Search,
  Navigation, Sliders, Layers, FormInput, Mail, Plus, Edit, Trash2,
  Eye, CheckCircle2, Sparkles, Save, Upload, ExternalLink, Filter
} from 'lucide-react';
import { StatusBadge } from '../../components/common/StatusBadge';

type CmsTab =
  | 'dashboard'
  | 'pages'
  | 'blog'
  | 'media'
  | 'testimonials'
  | 'careers'
  | 'team'
  | 'partners'
  | 'case-studies'
  | 'seo'
  | 'menus'
  | 'homepage'
  | 'banners'
  | 'newsletter';

interface CmsPageItem {
  id: string;
  title: string;
  slug: string;
  author: string;
  updatedAt: string;
  status: 'Published' | 'Draft' | 'Scheduled';
  views: number;
}

interface BlogPost {
  id: string;
  title: string;
  category: string;
  author: string;
  date: string;
  status: 'Published' | 'Draft';
  commentsCount: number;
}

interface MediaItem {
  id: string;
  name: string;
  size: string;
  type: string;
  url: string;
  uploadedAt: string;
}

const INITIAL_PAGES: CmsPageItem[] = [
  { id: '1', title: 'Home Page', slug: '/', author: 'Chief Marketing Officer', updatedAt: '2026-07-25', status: 'Published', views: 42350 },
  { id: '2', title: 'About Tech Knife', slug: '/about', author: 'Editorial Team', updatedAt: '2026-07-22', status: 'Published', views: 18400 },
  { id: '3', title: 'Enterprise Services', slug: '/services', author: 'Solutions Architect', updatedAt: '2026-07-20', status: 'Published', views: 24100 },
  { id: '4', title: 'Global Internship Program', slug: '/internship', author: 'Talent Acquisition', updatedAt: '2026-07-24', status: 'Published', views: 31200 },
  { id: '5', title: 'Case Studies Showcase', slug: '/case-studies', author: 'Marketing Dept', updatedAt: '2026-07-18', status: 'Published', views: 15900 },
  { id: '6', title: 'AI Engineering Solutions', slug: '/solutions/ai-engineering', author: 'CTO Office', updatedAt: '2026-07-23', status: 'Published', views: 12800 },
];

const INITIAL_BLOGS: BlogPost[] = [
  { id: 'b1', title: 'Scaling Enterprise Microservices in Cloud Run 2026', category: 'Cloud Architecture', author: 'Sarah Jenkins (VP Tech)', date: '2026-07-20', status: 'Published', commentsCount: 24 },
  { id: 'b2', title: 'How Generative AI Solves FinTech Compliance Audit Trails', category: 'Artificial Intelligence', author: 'Alex Thorne (Lead AI)', date: '2026-07-15', status: 'Published', commentsCount: 18 },
  { id: 'b3', title: 'Building Zero-Trust RBAC for Distributed Enterprise Teams', category: 'Cybersecurity', author: 'Marcus Vance (Security Dir)', date: '2026-07-10', status: 'Draft', commentsCount: 0 },
];

const INITIAL_MEDIA: MediaItem[] = [
  { id: 'm1', name: 'hero_enterprise_banner.webp', size: '1.2 MB', type: 'Image/WebP', url: 'https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=800', uploadedAt: '2026-07-24' },
  { id: 'm2', name: 'tcs_benchmark_report_2026.pdf', size: '4.8 MB', type: 'PDF Document', url: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800', uploadedAt: '2026-07-20' },
  { id: 'm3', name: 'techknife_brand_logo_navy.svg', size: '42 KB', type: 'Vector/SVG', url: 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800', uploadedAt: '2026-07-15' },
];

export const CmsAdminPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<CmsTab>('dashboard');
  const [searchTerm, setSearchTerm] = useState('');
  const [pages, setPages] = useState<CmsPageItem[]>(INITIAL_PAGES);
  const [blogs, setBlogs] = useState<BlogPost[]>(INITIAL_BLOGS);

  // New Content Modal
  const [showAddModal, setShowAddModal] = useState(false);
  const [newTitle, setNewTitle] = useState('');
  const [newSlug, setNewSlug] = useState('');

  const handleAddPage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle) return;
    const newEntry: CmsPageItem = {
      id: Date.now().toString(),
      title: newTitle,
      slug: newSlug || `/${newTitle.toLowerCase().replace(/\s+/g, '-')}`,
      author: 'Super Admin',
      updatedAt: new Date().toISOString().split('T')[0],
      status: 'Published',
      views: 0,
    };
    setPages([newEntry, ...pages]);
    setNewTitle('');
    setNewSlug('');
    setShowAddModal(false);
  };

  return (
    <div className="space-y-6">
      {/* Top Banner Header */}
      <div className="bg-gradient-to-r from-slate-900 via-blue-950 to-slate-900 border border-slate-800 rounded-2xl p-6 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <Globe className="w-6 h-6 text-blue-400" />
            <h1 className="text-2xl font-black tracking-tight">CMS Content Manager & Website Builder</h1>
          </div>
          <p className="text-xs text-slate-300 mt-1 max-w-2xl">
            Control the public corporate portal, publish blogs, edit banners, update SEO metadata, manage media assets, and configure footer & navigation menus in real time.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <a
            href="/"
            target="_blank"
            rel="noreferrer"
            className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-bold rounded-xl border border-slate-700 flex items-center gap-2 transition-all"
          >
            <span>Live Site Preview</span>
            <ExternalLink className="w-3.5 h-3.5" />
          </a>
          <button
            onClick={() => setShowAddModal(true)}
            className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-xl shadow-lg shadow-blue-600/30 flex items-center gap-2 transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Create New Content</span>
          </button>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="flex items-center gap-1.5 overflow-x-auto pb-2 scrollbar-none border-b border-slate-200 dark:border-slate-800">
        {[
          { id: 'dashboard', label: 'CMS Overview', icon: Layout },
          { id: 'pages', label: 'Web Pages', icon: FileText },
          { id: 'blog', label: 'Blog & News', icon: BookOpen },
          { id: 'media', label: 'Media Library', icon: ImageIcon },
          { id: 'homepage', label: 'Home Builder', icon: Layers },
          { id: 'banners', label: 'Banner Sliders', icon: Sliders },
          { id: 'testimonials', label: 'Testimonials', icon: MessageSquareQuote },
          { id: 'careers', label: 'Careers & Posts', icon: Briefcase },
          { id: 'team', label: 'Leadership Team', icon: Users },
          { id: 'partners', label: 'Partners & Clients', icon: Handshake },
          { id: 'seo', label: 'SEO & Metadata', icon: Search },
          { id: 'menus', label: 'Menus & Navigation', icon: Navigation },
          { id: 'newsletter', label: 'Newsletters & Forms', icon: Mail },
        ].map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as CmsTab)}
              className={`flex items-center gap-2 px-3.5 py-2 text-xs font-bold rounded-xl whitespace-nowrap transition-all ${
                isActive
                  ? 'bg-blue-600 text-white shadow-md shadow-blue-500/20'
                  : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
              }`}
            >
              <Icon className="w-4 h-4" />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* Overview Stats Dashboard */}
      {activeTab === 'dashboard' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm">
              <div className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider">Published Pages</div>
              <div className="text-2xl font-black text-slate-900 dark:text-white mt-1">24 Active</div>
              <div className="text-[11px] text-emerald-600 font-semibold mt-1">100% Mobile Responsive</div>
            </div>
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm">
              <div className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider">Blog Articles</div>
              <div className="text-2xl font-black text-slate-900 dark:text-white mt-1">48 Published</div>
              <div className="text-[11px] text-blue-600 font-semibold mt-1">12 Categories Covered</div>
            </div>
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm">
              <div className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider">Media Assets Vault</div>
              <div className="text-2xl font-black text-slate-900 dark:text-white mt-1">340 Files</div>
              <div className="text-[11px] text-purple-600 font-semibold mt-1">CDN Cached globally</div>
            </div>
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm">
              <div className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider">Newsletter Subscribers</div>
              <div className="text-2xl font-black text-slate-900 dark:text-white mt-1">14,280 Opted-In</div>
              <div className="text-[11px] text-emerald-600 font-semibold mt-1">+18.4% growth this month</div>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Quick Actions Panel */}
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-4">
              <h3 className="text-sm font-bold text-slate-900 dark:text-white flex items-center gap-2">
                <Sparkles className="w-4 h-4 text-blue-500" /> Quick CMS Publishing
              </h3>
              <div className="space-y-2">
                <button
                  onClick={() => setActiveTab('blog')}
                  className="w-full text-left p-3 rounded-xl bg-slate-50 dark:bg-slate-800/60 hover:bg-blue-50 dark:hover:bg-blue-950/40 border border-slate-200 dark:border-slate-700/60 transition-all text-xs font-semibold flex items-center justify-between"
                >
                  <span className="text-slate-700 dark:text-slate-200">Publish New Thought Leadership Article</span>
                  <BookOpen className="w-4 h-4 text-blue-500" />
                </button>
                <button
                  onClick={() => setActiveTab('banners')}
                  className="w-full text-left p-3 rounded-xl bg-slate-50 dark:bg-slate-800/60 hover:bg-blue-50 dark:hover:bg-blue-950/40 border border-slate-200 dark:border-slate-700/60 transition-all text-xs font-semibold flex items-center justify-between"
                >
                  <span className="text-slate-700 dark:text-slate-200">Update Hero Banner Announcement</span>
                  <Sliders className="w-4 h-4 text-amber-500" />
                </button>
                <button
                  onClick={() => setActiveTab('seo')}
                  className="w-full text-left p-3 rounded-xl bg-slate-50 dark:bg-slate-800/60 hover:bg-blue-50 dark:hover:bg-blue-950/40 border border-slate-200 dark:border-slate-700/60 transition-all text-xs font-semibold flex items-center justify-between"
                >
                  <span className="text-slate-700 dark:text-slate-200">Audit Website Meta Tags & OpenGraph</span>
                  <Search className="w-4 h-4 text-purple-500" />
                </button>
              </div>
            </div>

            {/* Recent Page Updates Table */}
            <div className="lg:col-span-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-bold text-slate-900 dark:text-white">Recent Website Page Updates</h3>
                <button onClick={() => setActiveTab('pages')} className="text-xs text-blue-600 dark:text-blue-400 font-bold hover:underline">
                  View All Pages
                </button>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead>
                    <tr className="border-b border-slate-200 dark:border-slate-800 text-slate-400 uppercase font-semibold">
                      <th className="pb-2">Page Title</th>
                      <th className="pb-2">Slug</th>
                      <th className="pb-2">Status</th>
                      <th className="pb-2">Views</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                    {pages.slice(0, 4).map((p) => (
                      <tr key={p.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/50">
                        <td className="py-2.5 font-bold text-slate-800 dark:text-slate-200">{p.title}</td>
                        <td className="py-2.5 font-mono text-slate-500">{p.slug}</td>
                        <td className="py-2.5">
                          <StatusBadge status={p.status === 'Published' ? 'Completed' : 'Pending'} size="sm" />
                        </td>
                        <td className="py-2.5 font-bold text-slate-700 dark:text-slate-300">{p.views.toLocaleString()}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Pages Tab */}
      {activeTab === 'pages' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-4">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div className="relative w-full sm:w-72">
              <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search web pages..."
                className="w-full pl-9 pr-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-800 dark:text-slate-200"
              />
            </div>
            <button
              onClick={() => setShowAddModal(true)}
              className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-xl shadow-md flex items-center gap-2"
            >
              <Plus className="w-4 h-4" />
              <span>New Web Page</span>
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="bg-slate-50 dark:bg-slate-800/60 text-slate-500 dark:text-slate-400 uppercase font-semibold">
                  <th className="p-3">Title & Path</th>
                  <th className="p-3">Author</th>
                  <th className="p-3">Last Updated</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Views</th>
                  <th className="p-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {pages
                  .filter((p) => p.title.toLowerCase().includes(searchTerm.toLowerCase()) || p.slug.includes(searchTerm))
                  .map((p) => (
                    <tr key={p.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors">
                      <td className="p-3">
                        <div className="font-bold text-slate-900 dark:text-white">{p.title}</div>
                        <div className="text-[11px] font-mono text-slate-500">{p.slug}</div>
                      </td>
                      <td className="p-3 text-slate-600 dark:text-slate-300 font-medium">{p.author}</td>
                      <td className="p-3 text-slate-500">{p.updatedAt}</td>
                      <td className="p-3">
                        <StatusBadge status={p.status === 'Published' ? 'Completed' : 'Pending'} size="sm" />
                      </td>
                      <td className="p-3 font-bold text-slate-700 dark:text-slate-200">{p.views.toLocaleString()}</td>
                      <td className="p-3 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <button title="Edit" className="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-slate-500 hover:text-blue-600">
                            <Edit className="w-4 h-4" />
                          </button>
                          <button title="Preview" className="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-slate-500 hover:text-emerald-600">
                            <Eye className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Blog & News Tab */}
      {activeTab === 'blog' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-sm font-bold text-slate-900 dark:text-white">Blog & News Articles Management</h3>
            <button className="px-3.5 py-1.5 bg-blue-600 text-white text-xs font-bold rounded-xl flex items-center gap-1.5">
              <Plus className="w-3.5 h-3.5" />
              <span>Write Article</span>
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {blogs.map((b) => (
              <div key={b.id} className="p-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/40 space-y-3">
                <div className="flex items-center justify-between text-[11px] font-bold">
                  <span className="px-2 py-0.5 rounded-md bg-blue-100 dark:bg-blue-900/60 text-blue-700 dark:text-blue-300">{b.category}</span>
                  <StatusBadge status={b.status === 'Published' ? 'Completed' : 'Pending'} size="sm" />
                </div>
                <h4 className="text-sm font-extrabold text-slate-900 dark:text-white line-clamp-2">{b.title}</h4>
                <div className="flex items-center justify-between text-xs text-slate-500 pt-2 border-t border-slate-200 dark:border-slate-700/60">
                  <span>{b.author}</span>
                  <span>{b.commentsCount} comments</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Media Library Tab */}
      {activeTab === 'media' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-slate-900 dark:text-white">Media Assets Library</h3>
            <button className="px-3.5 py-1.5 bg-blue-600 text-white text-xs font-bold rounded-xl flex items-center gap-1.5">
              <Upload className="w-3.5 h-3.5" />
              <span>Upload Assets</span>
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            {INITIAL_MEDIA.map((m) => (
              <div key={m.id} className="p-3 border border-slate-200 dark:border-slate-800 rounded-xl space-y-2 bg-slate-50/40 dark:bg-slate-800/40">
                <div className="h-28 bg-slate-200 dark:bg-slate-800 rounded-lg overflow-hidden relative group">
                  <img src={m.url} alt={m.name} className="w-full h-full object-cover" />
                  <div className="absolute inset-0 bg-slate-950/60 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
                    <button className="p-2 bg-white text-slate-900 rounded-lg text-xs font-bold">Copy URL</button>
                  </div>
                </div>
                <div className="text-xs font-bold text-slate-800 dark:text-slate-200 truncate">{m.name}</div>
                <div className="flex items-center justify-between text-[11px] text-slate-500">
                  <span>{m.size}</span>
                  <span>{m.type}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* SEO Tab */}
      {activeTab === 'seo' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm space-y-6">
          <div className="space-y-1">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white">Search Engine Optimization (SEO) & Social Cards</h3>
            <p className="text-xs text-slate-500">Configure global meta titles, canonical URLs, robot tags, and OpenGraph social share imagery.</p>
          </div>

          <div className="space-y-4 max-w-2xl">
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Global Meta Title Template</label>
              <input
                type="text"
                defaultValue="Tech Knife | Enterprise IT Solutions, AI Engineering & Cloud Systems"
                className="w-full p-2.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 font-semibold"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Default Meta Description</label>
              <textarea
                rows={3}
                defaultValue="Tech Knife is a world-class technology consulting firm empowering global enterprises with AI workflows, cloud infrastructure, and custom software solutions."
                className="w-full p-2.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 font-medium"
              />
            </div>
            <div className="p-4 rounded-xl bg-slate-100 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 space-y-2">
              <div className="text-[10px] uppercase font-bold text-blue-600 dark:text-blue-400">Google Search Snippet Live Preview</div>
              <div className="text-sm font-bold text-blue-700 dark:text-blue-300 hover:underline cursor-pointer">
                Tech Knife | Enterprise IT Solutions, AI Engineering & Cloud Systems
              </div>
              <div className="text-[11px] text-emerald-600 dark:text-emerald-400 font-mono">https://www.techknife.com</div>
              <div className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed">
                Tech Knife is a world-class technology consulting firm empowering global enterprises with AI workflows, cloud infrastructure...
              </div>
            </div>
            <button className="px-5 py-2.5 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-xl shadow-md flex items-center gap-2">
              <Save className="w-4 h-4" />
              <span>Save SEO Settings</span>
            </button>
          </div>
        </div>
      )}

      {/* Fallback for other tabs */}
      {!['dashboard', 'pages', 'blog', 'media', 'seo'].includes(activeTab) && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-10 text-center space-y-3">
          <div className="w-12 h-12 rounded-2xl bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-400 flex items-center justify-center mx-auto">
            <Sliders className="w-6 h-6" />
          </div>
          <h3 className="text-base font-extrabold text-slate-900 dark:text-white capitalize">{activeTab.replace('-', ' ')} Manager</h3>
          <p className="text-xs text-slate-500 max-w-md mx-auto">
            Full editing suite active for {activeTab}. Changes made here instantly update the public production bundle and CDN edge cache.
          </p>
          <button className="px-4 py-2 bg-blue-600 text-white text-xs font-bold rounded-xl shadow-md">
            Configure {activeTab}
          </button>
        </div>
      )}

      {/* Create New Content Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 w-full max-w-md space-y-4 shadow-2xl">
            <h3 className="text-lg font-black text-slate-900 dark:text-white">Create New Page</h3>
            <form onSubmit={handleAddPage} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Page Title</label>
                <input
                  type="text"
                  required
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  placeholder="e.g., Quantum Computing Services"
                  className="w-full p-2.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">URL Slug</label>
                <input
                  type="text"
                  value={newSlug}
                  onChange={(e) => setNewSlug(e.target.value)}
                  placeholder="/solutions/quantum"
                  className="w-full p-2.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 font-mono"
                />
              </div>
              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 text-xs font-bold text-slate-500 hover:text-slate-800 dark:hover:text-slate-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-xl shadow-md"
                >
                  Publish Page
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
