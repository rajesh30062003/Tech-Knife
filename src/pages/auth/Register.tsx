import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { motion } from 'motion/react';
import { Mail, Lock, User, Briefcase, Building, Phone, Eye, EyeOff, AlertCircle, Loader2, CheckCircle2, XCircle } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { Role } from '../../types';
import { Logo } from '../../components/common/Logo';

const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&._\-#^()+={}\[\]|:;"'<>,/]).{12,}$/;

const registerSchema = z.object({
  email: z
    .string()
    .min(1, { message: 'Official email address is required' })
    .email({ message: 'Must be a valid official email address' })
    .refine((val) => !/^\+?[0-9]{7,15}$/.test(val), {
      message: 'Mobile numbers cannot be used as primary email logins.',
    }),
  password: z
    .string()
    .min(12, { message: 'Password must be at least 12 characters long' })
    .regex(passwordPattern, {
      message: 'Password must contain uppercase, lowercase, digit, and special character',
    }),
  firstName: z.string().min(1, { message: 'First name is required' }),
  lastName: z.string().min(1, { message: 'Last name is required' }),
  designation: z.string().min(1, { message: 'Designation / Title is required' }),
  department: z.string().min(1, { message: 'Department is required' }),
  phoneNumber: z.string().optional(),
  primaryRole: z.string().min(1, { message: 'Please select a primary role' }),
});

type RegisterFormData = z.infer<typeof registerSchema>;

const ROLES_OPTIONS: { role: Role; label: string; group: string }[] = [
  { role: 'ROLE_CEO', label: 'CEO - Chief Executive Officer', group: 'Executive' },
  { role: 'ROLE_CTO', label: 'CTO - Chief Technology Officer', group: 'Executive' },
  { role: 'ROLE_CMO', label: 'CMO - Chief Marketing Officer', group: 'Executive' },
  { role: 'ROLE_MD', label: 'MD - Managing Director', group: 'Executive' },
  { role: 'ROLE_DIRECTOR', label: 'Director - Department Lead', group: 'Management' },
  { role: 'ROLE_MANAGER', label: 'Manager - Engineering / Operations', group: 'Management' },
  { role: 'ROLE_EMPLOYEE', label: 'Employee - Staff Specialist', group: 'Staff' },
  { role: 'ROLE_INTERN', label: 'Intern - Associate', group: 'Staff' },
  { role: 'ROLE_CUSTOMER', label: 'Customer - Client Representative', group: 'External' },
  { role: 'ROLE_ADMIN', label: 'System Admin', group: 'System' },
];

export const Register: React.FC = () => {
  const navigate = useNavigate();
  const { register: registerAccount, error: authError, clearError } = useAuth();

  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      designation: 'Software Specialist',
      department: 'Engineering',
      phoneNumber: '',
      primaryRole: 'ROLE_EMPLOYEE',
    },
  });

  const watchPassword = watch('password', '');

  // Password Strength Calculation
  const hasMinLength = watchPassword.length >= 12;
  const hasUpper = /[A-Z]/.test(watchPassword);
  const hasLower = /[a-z]/.test(watchPassword);
  const hasDigit = /\d/.test(watchPassword);
  const hasSpecial = /[@$!%*?&._\-#^()+={}\[\]|:;"'<>,/]/.test(watchPassword);

  const passedCount = [hasMinLength, hasUpper, hasLower, hasDigit, hasSpecial].filter(Boolean).length;

  const onSubmit = async (data: RegisterFormData) => {
    setIsSubmitting(true);
    setErrorMessage(null);
    clearError();

    try {
      await registerAccount({
        email: data.email,
        password: data.password,
        firstName: data.firstName,
        lastName: data.lastName,
        designation: data.designation,
        department: data.department,
        phoneNumber: data.phoneNumber,
        roles: [data.primaryRole as Role],
      });
      navigate('/dashboard', { replace: true });
    } catch (err: any) {
      setErrorMessage(err.message || 'Registration failed. Please check input requirements.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8 relative overflow-hidden">
      <div className="absolute top-0 right-1/4 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 left-1/4 w-96 h-96 bg-indigo-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="sm:mx-auto sm:w-full sm:max-w-xl relative z-10 text-center space-y-3">
        <Link to="/" className="inline-block hover:scale-105 transition-transform">
          <Logo variant="full" size="xl" showTagline inverted />
        </Link>
        <h2 className="text-xl sm:text-2xl font-extrabold text-white tracking-tight">
          Create Your Enterprise Account
        </h2>
        <p className="text-xs text-slate-400">
          Provision your corporate identity with role-based access control
        </p>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="mt-8 sm:mx-auto sm:w-full sm:max-w-xl relative z-10 px-4"
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
                <span className="font-bold block mb-0.5">Registration Failed</span>
                {errorMessage || authError}
              </div>
            </motion.div>
          )}

          <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
            {/* Name Fields */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                  First Name
                </label>
                <div className="relative">
                  <User className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
                  <input
                    type="text"
                    {...register('firstName')}
                    placeholder="Alexander"
                    className="w-full pl-10 pr-3 py-2.5 bg-slate-950/80 border border-slate-800 rounded-xl text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                {errors.firstName && <p className="mt-1 text-xs text-rose-400">{errors.firstName.message}</p>}
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                  Last Name
                </label>
                <div className="relative">
                  <User className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
                  <input
                    type="text"
                    {...register('lastName')}
                    placeholder="Vance"
                    className="w-full pl-10 pr-3 py-2.5 bg-slate-950/80 border border-slate-800 rounded-xl text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                {errors.lastName && <p className="mt-1 text-xs text-rose-400">{errors.lastName.message}</p>}
              </div>
            </div>

            {/* Official Email */}
            <div>
              <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                Official Corporate Email
              </label>
              <div className="relative">
                <Mail className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
                <input
                  type="email"
                  {...register('email')}
                  placeholder="a.vance@techknife.com"
                  className="w-full pl-10 pr-3 py-2.5 bg-slate-950/80 border border-slate-800 rounded-xl text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              {errors.email && <p className="mt-1 text-xs text-rose-400">{errors.email.message}</p>}
            </div>

            {/* Designation & Department */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                  Designation / Title
                </label>
                <div className="relative">
                  <Briefcase className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
                  <input
                    type="text"
                    {...register('designation')}
                    placeholder="VP of Engineering"
                    className="w-full pl-10 pr-3 py-2.5 bg-slate-950/80 border border-slate-800 rounded-xl text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                {errors.designation && <p className="mt-1 text-xs text-rose-400">{errors.designation.message}</p>}
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                  Department
                </label>
                <div className="relative">
                  <Building className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
                  <input
                    type="text"
                    {...register('department')}
                    placeholder="Engineering"
                    className="w-full pl-10 pr-3 py-2.5 bg-slate-950/80 border border-slate-800 rounded-xl text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                {errors.department && <p className="mt-1 text-xs text-rose-400">{errors.department.message}</p>}
              </div>
            </div>

            {/* Phone & Role */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                  Phone Number
                </label>
                <div className="relative">
                  <Phone className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
                  <input
                    type="text"
                    {...register('phoneNumber')}
                    placeholder="+1 (555) 019-2834"
                    className="w-full pl-10 pr-3 py-2.5 bg-slate-950/80 border border-slate-800 rounded-xl text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                  Primary Enterprise Role
                </label>
                <select
                  {...register('primaryRole')}
                  className="w-full px-3 py-2.5 bg-slate-950/80 border border-slate-800 rounded-xl text-sm text-slate-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  {ROLES_OPTIONS.map((opt) => (
                    <option key={opt.role} value={opt.role}>
                      {opt.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Password Field */}
            <div>
              <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                Password
              </label>
              <div className="relative">
                <Lock className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  {...register('password')}
                  placeholder="Min 12 chars (Upper, Lower, Digit, Special)"
                  className="w-full pl-10 pr-10 py-2.5 bg-slate-950/80 border border-slate-800 rounded-xl text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3.5 top-3.5 text-slate-500 hover:text-slate-300"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>

              {/* Live Strength Checklist */}
              {watchPassword && (
                <div className="mt-2.5 p-3 rounded-xl bg-slate-950/60 border border-slate-800 text-[11px] space-y-1">
                  <div className="flex items-center justify-between mb-1">
                    <span className="font-bold text-slate-400">Password Policy Checklist</span>
                    <span className="font-mono font-bold text-blue-400">{passedCount}/5 Met</span>
                  </div>
                  <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden mb-2">
                    <div
                      className={`h-full transition-all duration-300 ${
                        passedCount <= 2 ? 'bg-rose-500' : passedCount <= 4 ? 'bg-amber-500' : 'bg-emerald-500'
                      }`}
                      style={{ width: `${(passedCount / 5) * 100}%` }}
                    />
                  </div>
                  <div className="grid grid-cols-2 gap-1 text-slate-400">
                    <span className={`flex items-center gap-1 ${hasMinLength ? 'text-emerald-400' : ''}`}>
                      {hasMinLength ? <CheckCircle2 className="w-3 h-3" /> : <XCircle className="w-3 h-3" />} Min 12 Characters
                    </span>
                    <span className={`flex items-center gap-1 ${hasUpper ? 'text-emerald-400' : ''}`}>
                      {hasUpper ? <CheckCircle2 className="w-3 h-3" /> : <XCircle className="w-3 h-3" />} Uppercase Letter
                    </span>
                    <span className={`flex items-center gap-1 ${hasLower ? 'text-emerald-400' : ''}`}>
                      {hasLower ? <CheckCircle2 className="w-3 h-3" /> : <XCircle className="w-3 h-3" />} Lowercase Letter
                    </span>
                    <span className={`flex items-center gap-1 ${hasDigit ? 'text-emerald-400' : ''}`}>
                      {hasDigit ? <CheckCircle2 className="w-3 h-3" /> : <XCircle className="w-3 h-3" />} At Least 1 Digit
                    </span>
                    <span className={`flex items-center gap-1 ${hasSpecial ? 'text-emerald-400' : ''}`}>
                      {hasSpecial ? <CheckCircle2 className="w-3 h-3" /> : <XCircle className="w-3 h-3" />} Special Character
                    </span>
                  </div>
                </div>
              )}
              {errors.password && <p className="mt-1 text-xs text-rose-400">{errors.password.message}</p>}
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full mt-4 py-3.5 px-4 bg-blue-600 hover:bg-blue-500 text-white font-bold rounded-xl shadow-lg shadow-blue-600/30 focus:outline-none transition-all flex items-center justify-center gap-2 text-sm disabled:opacity-50"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Provisioning Account...</span>
                </>
              ) : (
                <span>Complete Registration</span>
              )}
            </button>
          </form>

          <div className="mt-6 text-center text-xs text-slate-400">
            Already registered?{' '}
            <Link to="/login" className="font-semibold text-blue-400 hover:text-blue-300">
              Sign In to Account
            </Link>
          </div>
        </div>
      </motion.div>
    </div>
  );
};
