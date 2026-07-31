import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { motion } from 'motion/react';
import { Lock, Mail, Eye, EyeOff, AlertCircle, Loader2, ArrowRight } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { Logo } from '../../components/common/Logo';

const loginSchema = z.object({
  email: z
    .string()
    .min(1, { message: 'Email address is required' })
    .email({ message: 'Must be a valid email address' }),
  password: z.string().min(1, { message: 'Password is required' }),
});

type LoginFormData = z.infer<typeof loginSchema>;

const SEEDED_ACCOUNTS = [
  { label: 'CEO', email: 'ranadhir.pal@techknife.com', desc: 'Ranadhir Pal (ROLE_CEO)' },
  { label: 'MD', email: 'sourav.roy@techknife.com', desc: 'Sourav Roy (ROLE_MD)' },
  { label: 'CTO', email: 'subrata.pal@techknife.com', desc: 'Subrata Pal (ROLE_CTO)' },
  { label: 'Growth Head', email: 'anindita.c@techknife.com', desc: 'Anindita Chakraborty (ROLE_GROWTH_HEAD)' },
  { label: 'Sr. Eng Manager', email: 'rahul.garai@techknife.com', desc: 'Rahul Garai (ROLE_SENIOR_ENGINEERING_MANAGER)' },
  { label: 'Intern (Eng)', email: 'rahul.pal@techknife.com', desc: 'Rahul Pal (ROLE_INTERN)' },
  { label: 'Intern (Marketing)', email: 'sangita.k@techknife.com', desc: 'Sangita Koner (ROLE_INTERN)' },
  { label: 'Intern (Tech)', email: 'salman.k@techknife.com', desc: 'Salman Kaji (ROLE_INTERN)' },
  { label: 'Customer', email: 'amit.sharma@example.com', desc: 'Amit Sharma (ROLE_CUSTOMER)' },
];

export const Login: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, error: authError, clearError } = useAuth();

  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const from = (location.state as any)?.from?.pathname || '/dashboard';

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: 'ranadhir.pal@techknife.com',
      password: 'TechKnife@2026',
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
      setErrorMessage(err.message || 'Invalid email or password');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleAccountSelect = (email: string) => {
    setValue('email', email);
    setValue('password', 'TechKnife@2026');
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8 relative overflow-hidden">
      <div className="absolute top-0 left-1/4 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-indigo-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="sm:mx-auto sm:w-full sm:max-w-md relative z-10 text-center space-y-3">
        <Link to="/" className="inline-block hover:scale-105 transition-transform">
          <Logo variant="full" size="xl" showTagline inverted />
        </Link>
        <h2 className="text-xl sm:text-2xl font-extrabold text-white tracking-tight">
          Enterprise MongoDB Atlas Sign-In
        </h2>
        <p className="text-xs text-slate-400">
          Enter your official corporate email credentials stored in MongoDB Atlas
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
                <span className="font-bold block mb-0.5">Sign-In Alert</span>
                <p className="text-xs text-rose-300 font-medium">{errorMessage || authError}</p>
              </div>
            </motion.div>
          )}

          <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>
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

            <div>
              <div className="flex items-center justify-between mb-2">
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
                  Password
                </label>
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

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full py-3.5 px-4 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-extrabold rounded-xl shadow-lg shadow-cyan-500/20 focus:outline-none focus:ring-2 focus:ring-cyan-400 transition-all flex items-center justify-center gap-2 text-sm disabled:opacity-50"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Verifying with MongoDB Atlas...</span>
                </>
              ) : (
                <>
                  <span>Sign In to Dashboard</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          {/* Seeded Accounts Quick Select */}
          <div className="mt-8 pt-6 border-t border-slate-800/80">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-bold uppercase tracking-wider text-slate-400">
                Seeded Leadership & Users (MongoDB)
              </span>
              <span className="text-[10px] bg-blue-500/20 text-blue-300 font-mono px-2 py-0.5 rounded-full">
                Password: TechKnife@2026
              </span>
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 max-h-48 overflow-y-auto pr-1 custom-scrollbar">
              {SEEDED_ACCOUNTS.map((acc) => (
                <button
                  key={acc.email}
                  type="button"
                  onClick={() => handleAccountSelect(acc.email)}
                  className="p-2 rounded-xl border border-slate-800 bg-slate-950/60 hover:bg-slate-800/80 text-left transition-all hover:scale-[1.02]"
                >
                  <span className="block text-xs font-bold text-blue-400">{acc.label}</span>
                  <span className="block text-[10px] text-slate-400 truncate">{acc.desc}</span>
                </button>
              ))}
            </div>
          </div>

          <div className="mt-6 text-center text-xs text-slate-400">
            Need a Customer Account?{' '}
            <Link to="/register" className="font-semibold text-blue-400 hover:text-blue-300 transition-colors">
              Public Customer Registration
            </Link>
          </div>
        </div>
      </motion.div>
    </div>
  );
};
