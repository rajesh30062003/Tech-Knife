import React, { useState } from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import { 
  ArrowRight, Shield, Menu, X, Globe, Phone, Mail, MapPin, 
  ChevronDown, ExternalLink, Sparkles
} from 'lucide-react';
import { useTheme } from '../../context/ThemeContext';
import { useAuth } from '../../context/AuthContext';
import { Logo } from '../common/Logo';

export const PublicLayout: React.FC = () => {
  const { theme, themeMode, setThemeMode } = useTheme();
  const { user } = useAuth();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [servicesDropdown, setServicesDropdown] = useState(false);

  const mainNav = [
    { label: 'Home', path: '/' },
    { label: 'About', path: '/about' },
    { label: 'Services', path: '/services' },
    { label: 'Solutions', path: '/solutions' },
    { label: 'Products', path: '/products' },
    { label: 'Industries', path: '/industries' },
    { label: 'Portfolio', path: '/portfolio' },
    { label: 'Case Studies', path: '/case-studies' },
    { label: 'Internship', path: '/internship' },
    { label: 'Careers', path: '/careers' },
    { label: 'Blog', path: '/blog' },
    { label: 'Pricing', path: '/pricing' },
    { label: 'Contact', path: '/contact' },
  ];

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col font-sans selection:bg-blue-600 selection:text-white">
      
      {/* Top Banner Bar */}
      <div className="bg-gradient-to-r from-blue-900 via-slate-900 to-indigo-900 border-b border-slate-800/80 px-6 py-2 text-xs text-slate-300">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-2">
          <div className="flex items-center gap-4">
            <span className="flex items-center gap-1.5 text-blue-400 font-semibold">
              <Sparkles className="w-3.5 h-3.5" /> Next-Gen IT Services & Engineering
            </span>
            <span className="hidden md:inline text-slate-600">|</span>
            <span className="hidden md:inline text-slate-400">HQ: Silicon Valley & Global Delivery Centers</span>
          </div>
          <div className="flex items-center gap-4 text-slate-400">
            <a href="mailto:contact@techknife.com" className="hover:text-blue-400 flex items-center gap-1 transition-colors">
              <Mail className="w-3 h-3" /> contact@techknife.com
            </a>
            <a href="tel:+18005550199" className="hover:text-blue-400 flex items-center gap-1 transition-colors">
              <Phone className="w-3 h-3" /> +1 (800) 555-0199
            </a>
          </div>
        </div>
      </div>

      {/* Main Public Header Navbar */}
      <header className="sticky top-0 z-50 bg-slate-900/95 backdrop-blur-md border-b border-slate-800/80">
        <div className="max-w-7xl mx-auto px-6 h-18 flex items-center justify-between">
          
          <Link to="/" className="flex items-center">
            <Logo variant="full" size="md" showTagline inverted />
          </Link>

          {/* Desktop Navigation Links */}
          <nav className="hidden xl:flex items-center gap-6 text-xs font-semibold text-slate-300">
            {mainNav.slice(0, 8).map((item) => {
              const isActive = location.pathname === item.path;
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`transition-colors py-1 relative ${
                    isActive ? 'text-blue-400 font-bold' : 'hover:text-white'
                  }`}
                >
                  {item.label}
                  {isActive && (
                    <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-500 rounded-full" />
                  )}
                </Link>
              );
            })}

            {/* Dropdown for More Links */}
            <div className="relative group">
              <button 
                className="flex items-center gap-1 hover:text-white py-1 transition-colors"
                onClick={() => setServicesDropdown(!servicesDropdown)}
              >
                More <ChevronDown className="w-3.5 h-3.5 text-slate-400 group-hover:rotate-180 transition-transform" />
              </button>
              <div className="absolute right-0 top-full pt-2 w-48 opacity-0 group-hover:opacity-100 pointer-events-none group-hover:pointer-events-auto transition-all duration-200">
                <div className="bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl p-2 space-y-1">
                  {mainNav.slice(8).map((item) => (
                    <Link
                      key={item.path}
                      to={item.path}
                      className="block px-3 py-2 text-xs text-slate-300 hover:text-white hover:bg-slate-800/80 rounded-xl transition-colors"
                    >
                      {item.label}
                    </Link>
                  ))}
                  <Link
                    to="/testimonials"
                    className="block px-3 py-2 text-xs text-slate-300 hover:text-white hover:bg-slate-800/80 rounded-xl transition-colors"
                  >
                    Testimonials & Reviews
                  </Link>
                  <Link
                    to="/faq"
                    className="block px-3 py-2 text-xs text-slate-300 hover:text-white hover:bg-slate-800/80 rounded-xl transition-colors"
                  >
                    FAQ
                  </Link>
                </div>
              </div>
            </div>
          </nav>

          {/* Action CTAs */}
          <div className="hidden sm:flex items-center gap-3">
            {user ? (
              <Link
                to="/dashboard"
                className="inline-flex items-center gap-2 px-4 py-2 text-xs font-semibold text-white bg-blue-600 hover:bg-blue-500 rounded-xl transition-all shadow-lg shadow-blue-600/25"
              >
                <span>Launch App Platform</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            ) : (
              <>
                <Link
                  to="/login"
                  className="px-4 py-2 text-xs font-semibold text-slate-300 hover:text-white transition-colors"
                >
                  Client Sign In
                </Link>
                <Link
                  to="/contact"
                  className="inline-flex items-center gap-2 px-4 py-2 text-xs font-semibold text-white bg-blue-600 hover:bg-blue-500 rounded-xl transition-all shadow-lg shadow-blue-600/25"
                >
                  <span>Get Started</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              </>
            )}
          </div>

          {/* Mobile Menu Button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="xl:hidden p-2 text-slate-400 hover:text-white focus:outline-none"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>

        {/* Mobile Flyout Menu */}
        {mobileMenuOpen && (
          <div className="xl:hidden bg-slate-900 border-b border-slate-800 px-6 py-4 space-y-3 animate-in fade-in slide-in-from-top-2">
            <div className="grid grid-cols-2 gap-2">
              {mainNav.map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  onClick={() => setMobileMenuOpen(false)}
                  className="px-3 py-2 text-xs font-medium text-slate-300 hover:text-white hover:bg-slate-800 rounded-lg"
                >
                  {item.label}
                </Link>
              ))}
            </div>
            <div className="pt-3 border-t border-slate-800 flex flex-col gap-2">
              <Link
                to="/login"
                onClick={() => setMobileMenuOpen(false)}
                className="w-full text-center px-4 py-2 text-xs font-semibold text-slate-200 bg-slate-800 rounded-xl"
              >
                Client Sign In
              </Link>
              <Link
                to="/dashboard"
                onClick={() => setMobileMenuOpen(false)}
                className="w-full text-center px-4 py-2 text-xs font-semibold text-white bg-blue-600 rounded-xl"
              >
                Access Portal
              </Link>
            </div>
          </div>
        )}
      </header>

      {/* Main Outlet */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Public Footer */}
      <footer className="border-t border-slate-800 bg-slate-950 pt-16 pb-12 px-6 text-sm text-slate-400">
        <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-5 gap-10 mb-12">
          
          <div className="md:col-span-2 space-y-4">
            <Logo variant="full" size="lg" showTagline inverted />
            <p className="text-xs text-slate-400 leading-relaxed max-w-sm">
              Tech Knife is a premier global technology consulting and software solutions company. We engineer scalable cloud systems, AI workflows, enterprise platforms, and digital transformation for Fortune 500 enterprises and hyper-growth startups.
            </p>
            <div className="flex items-center gap-3 pt-2 text-slate-400">
              <div className="p-2 bg-slate-900 border border-slate-800 rounded-lg hover:border-blue-500 transition-colors cursor-pointer">
                <Globe className="w-4 h-4" />
              </div>
              <div className="p-2 bg-slate-900 border border-slate-800 rounded-lg hover:border-blue-500 transition-colors cursor-pointer">
                <Mail className="w-4 h-4" />
              </div>
              <div className="p-2 bg-slate-900 border border-slate-800 rounded-lg hover:border-blue-500 transition-colors cursor-pointer">
                <Phone className="w-4 h-4" />
              </div>
            </div>
          </div>

          <div>
            <h4 className="font-bold text-white text-xs uppercase tracking-wider mb-4 border-b border-slate-800 pb-2">Solutions & Services</h4>
            <ul className="space-y-2.5 text-xs">
              <li><Link to="/services" className="hover:text-blue-400 transition-colors">Custom Software Development</Link></li>
              <li><Link to="/services" className="hover:text-blue-400 transition-colors">Cloud & Infrastructure DevOps</Link></li>
              <li><Link to="/services" className="hover:text-blue-400 transition-colors">AI & Data Analytics Engine</Link></li>
              <li><Link to="/solutions" className="hover:text-blue-400 transition-colors">Enterprise Systems Modernization</Link></li>
              <li><Link to="/solutions" className="hover:text-blue-400 transition-colors">Cybersecurity & RBAC Compliance</Link></li>
            </ul>
          </div>

          <div>
            <h4 className="font-bold text-white text-xs uppercase tracking-wider mb-4 border-b border-slate-800 pb-2">Company & Ecosystem</h4>
            <ul className="space-y-2.5 text-xs">
              <li><Link to="/about" className="hover:text-blue-400 transition-colors">About Tech Knife</Link></li>
              <li><Link to="/products" className="hover:text-blue-400 transition-colors">Product Ecosystem</Link></li>
              <li><Link to="/portfolio" className="hover:text-blue-400 transition-colors">Client Portfolio</Link></li>
              <li><Link to="/case-studies" className="hover:text-blue-400 transition-colors">Case Studies</Link></li>
              <li><Link to="/careers" className="hover:text-blue-400 transition-colors">Careers & Hiring</Link></li>
              <li><Link to="/internship" className="hover:text-blue-400 transition-colors">Global Internship Program</Link></li>
            </ul>
          </div>

          <div>
            <h4 className="font-bold text-white text-xs uppercase tracking-wider mb-4 border-b border-slate-800 pb-2">Portals & Trust</h4>
            <ul className="space-y-2.5 text-xs mb-4">
              <li><Link to="/login" className="hover:text-blue-400 transition-colors">Employee Portal</Link></li>
              <li><Link to="/login" className="hover:text-blue-400 transition-colors">Customer Portal</Link></li>
              <li><Link to="/login" className="hover:text-blue-400 transition-colors">Executive Governance Desk</Link></li>
              <li><Link to="/privacy-policy" className="hover:text-blue-400 transition-colors">Privacy Policy</Link></li>
              <li><Link to="/terms" className="hover:text-blue-400 transition-colors">Terms of Service</Link></li>
              <li><Link to="/refund-policy" className="hover:text-blue-400 transition-colors">Refund Policy</Link></li>
            </ul>
            <div className="flex items-center gap-2 p-2.5 bg-blue-950/40 border border-blue-800/40 rounded-xl text-blue-300 text-[11px]">
              <Shield className="w-4 h-4 text-blue-400 shrink-0" />
              <span>SOC2 Type II & ISO 27001 Security Standard</span>
            </div>
          </div>

        </div>

        <div className="max-w-7xl mx-auto border-t border-slate-800/80 pt-8 flex flex-col md:flex-row items-center justify-between text-xs text-slate-500 gap-4">
          <p>&copy; {new Date().getFullYear()} Tech Knife Inc. All rights reserved.</p>
          <div className="flex flex-wrap gap-6 text-slate-400">
            <Link to="/privacy-policy" className="hover:text-white transition-colors">Privacy Policy</Link>
            <Link to="/terms" className="hover:text-white transition-colors">Terms of Service</Link>
            <Link to="/refund-policy" className="hover:text-white transition-colors">Refund Policy</Link>
            <Link to="/support" className="hover:text-white transition-colors">Support Center</Link>
          </div>
        </div>
      </footer>

    </div>
  );
};
