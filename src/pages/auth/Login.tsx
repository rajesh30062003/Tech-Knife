import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { motion } from 'motion/react';
import { Lock, Mail, Eye, EyeOff, ShieldCheck, AlertCircle, Loader2, ArrowRight } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { Role } from '../../types';
import { Logo } from '../../components/common/Logo';

const loginSchema = z.object({
  email: z
    .string()
    .min(1, { message: 'Official email address is required' })
    .email({ message: 'Must be a valid official email address' })
    .refine((val) => !/^\+?[0-9]{7,15}$/.test(val), {
      message: 'Login using mobile numbers is strictly prohibited. Use official email.',
    }),
  password: z.string().min(1, { message: 'Password is required' }),
});

type LoginFormData = z.infer<typeof loginSchema>;

const ROLES_LIST: { role: Role; label: string; desc: string; color: string }[] = [
  { role: 'ROLE_SUPER_ADMIN', label: 'Super Admin', desc: 'Full System Control', color: 'bg-rose-500/10 text-rose-400 border-rose-500/30' },
  { role: 'ROLE_CEO', label: 'CEO', desc: 'Executive Analytics', color: 'bg-purple-500/10 text-purple-400 border-purple-500/30' },
  { role: 'ROLE_CTO', label: 'CTO', desc: 'Tech & Architecture', color: 'bg-indigo-500/10 text-indigo-400 border-indigo-500/30' },
  { role: 'ROLE_CMO', label: 'CMO', desc: 'Growth & Campaigns', color: 'bg-pink-500/10 text-pink-400 border-pink-500/30' },
  { role: 'ROLE_MD', label: 'MD', desc: 'Managing Directorate', color: 'bg-amber-500/10 text-amber-400 border-amber-500/30' },
  { role: 'ROLE_DIRECTOR', label: 'Director', desc: 'Department Head', color: 'bg-cyan-500/10 text-cyan-400 border-cyan-500/30' },
  { role: 'ROLE_ADMIN', label: 'Admin', desc: 'User & System Ops', color: 'bg-blue-500/10 text-blue-400 border-blue-500/30' },
  { role: 'ROLE_MANAGER', label: 'Manager', desc: 'Projects & CRM', color: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30' },
  { role: 'ROLE_EMPLOYEE', label: 'Employee', desc: 'Personal Workspace', color: 'bg-teal-500/10 text-teal-400 border-teal-500/30' },
  { role: 'ROLE_INTERN', label: 'Intern', desc: 'Learning Desk', color: 'bg-slate-500/10 text-slate-400 border-slate-500/30' },
  { role: 'ROLE_CUSTOMER', label: 'Customer', desc: 'Client Portal', color: 'bg-orange-500/10 text-orange-400 border-orange-500/30' },
];

export const Login: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, switchRole, error: authError, clearError } = useAuth();

  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const from = (location.state as any)?.from?.pathname || '/dashboard';

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: 'a.vance@techknife.com',
      password: 'SuperSecret123!',
    },
  });

  const onSubmit = async (data: LoginFormData) => {
    setIsSubmitting(true);
    setErrorMessage(null);
    clearError();

    try {
      await login({ email: data.email, password: data.password });
      navigate(from, { replace: true });
    } catch (err: any) {
      setErrorMessage(err.message || 'Invalid official email or password');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleRoleQuickSelect = (role: Role) => {
    switchRole(role);
    navigate(from, { replace: true });
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8 relative overflow-hidden">
      {/* Background Glows */}
      <div className="absolute top-0 left-1/4 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-indigo-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="sm:mx-auto sm:w-full sm:max-w-md relative z-10 text-center space-y-3">
        <Link to="/" className="inline-block hover:scale-105 transition-transform">
          <Logo variant="full" size="xl" showTagline inverted />
        </Link>
        <h2 className="text-xl sm:text-2xl font-extrabold text-white tracking-tight">
          Official Email Authentication
        </h2>
        <p className="text-xs text-slate-400">
          Enter your official corporate email credentials to access your designated workspace
        </p>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="mt-8 sm:mx-auto sm:w-full sm:max-w-md relative z-10 px-4"
      >
        <div className="bg-slate-900/90 backdrop-blur-xl border border-slate-800 py-8 px-6 shadow-2xl rounded-3xl sm:px-10">
          {(errorMessage || authError) && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              className="mb-6 p-4 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-400 flex items-start gap-3 text-xs leading-relaxed"
            >
              <AlertCircle className="w-5 h-5 shrink-0 mt-0.5" />
              <div>
                <span className="font-bold block mb-0.5">Authentication Policy Violation</span>
                {errorMessage || authError}
              </div>
            </motion.div>
          )}

          <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>
            {/* Official Email Field */}
            <div>
              <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
                Official Email Address
              </label>
              <div className="relative rounded-xl shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                  <Mail className="w-4 h-4" />
                </div>
                <input
                  type="email"
                  {...register('email')}
                  placeholder="name@techknife.com"
                  className="block w-full pl-10 pr-4 py-3 text-sm bg-slate-950/80 border border-slate-800 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                />
              </div>
              {errors.email && (
                <p className="mt-1.5 text-xs text-rose-400 font-medium flex items-center gap-1">
                  <AlertCircle className="w-3.5 h-3.5" /> {errors.email.message}
                </p>
              )}
            </div>

            {/* Password Field */}
            <div>
              <div className="flex items-center justify-between mb-2">
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
                  Password
                </label>
                <Link
                  to="/forgot-password"
                  className="text-xs font-semibold text-blue-400 hover:text-blue-300 transition-colors"
                >
                  Forgot Password?
                </Link>
              </div>
              <div className="relative rounded-xl shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type={showPassword ? 'text' : 'password'}
                  {...register('password')}
                  placeholder="••••••••••••"
                  className="block w-full pl-10 pr-10 py-3 text-sm bg-slate-950/80 border border-slate-800 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-slate-500 hover:text-slate-300 transition-colors"
                  aria-label="Toggle password visibility"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
              {errors.password && (
                <p className="mt-1.5 text-xs text-rose-400 font-medium flex items-center gap-1">
                  <AlertCircle className="w-3.5 h-3.5" /> {errors.password.message}
                </p>
              )}
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full py-3.5 px-4 bg-blue-600 hover:bg-blue-500 text-white font-bold rounded-xl shadow-lg shadow-blue-600/30 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all flex items-center justify-center gap-2 text-sm disabled:opacity-50"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Verifying Credentials...</span>
                </>
              ) : (
                <>
                  <span>Sign In to Dashboard</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          {/* Quick Role Switcher for Fast Evaluation */}
          <div className="mt-8 pt-6 border-t border-slate-800/80">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-bold uppercase tracking-wider text-slate-400">
                Instant Role Fast-Login (Demo)
              </span>
              <span className="text-[10px] bg-blue-500/20 text-blue-300 font-mono px-2 py-0.5 rounded-full">
                11 Roles Supported
              </span>
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 max-h-48 overflow-y-auto pr-1 custom-scrollbar">
              {ROLES_LIST.map((r) => (
                <button
                  key={r.role}
                  type="button"
                  onClick={() => handleRoleQuickSelect(r.role)}
                  className={`p-2 rounded-xl border text-left transition-all hover:scale-[1.02] ${r.color}`}
                >
                  <span className="block text-xs font-bold leading-tight">{r.label}</span>
                  <span className="block text-[10px] opacity-80 font-normal leading-tight truncate">{r.desc}</span>
                </button>
              ))}
            </div>
          </div>

          <div className="mt-6 text-center text-xs text-slate-400">
            Don't have an enterprise account?{' '}
            <Link to="/register" className="font-semibold text-blue-400 hover:text-blue-300 transition-colors">
              Register New Account
            </Link>
          </div>
        </div>
      </motion.div>
    </div>
  );
};
