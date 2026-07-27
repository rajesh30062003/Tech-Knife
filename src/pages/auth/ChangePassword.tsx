import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { motion } from 'motion/react';
import { Lock, Eye, EyeOff, Loader2, AlertCircle, CheckCircle2, Shield } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&._\-#^()+={}\[\]|:;"'<>,/]).{12,}$/;

const changePasswordSchema = z.object({
  currentPassword: z.string().min(1, 'Current password is required'),
  newPassword: z
    .string()
    .min(12, 'New password must be at least 12 characters long')
    .regex(passwordPattern, 'Must contain uppercase, lowercase, digit, and special character'),
});

type ChangePasswordFormData = z.infer<typeof changePasswordSchema>;

export const ChangePassword: React.FC = () => {
  const { changePassword, error: authError, clearError } = useAuth();

  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<ChangePasswordFormData>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: {
      currentPassword: '',
      newPassword: '',
    },
  });

  const watchNewPassword = watch('newPassword', '');

  const hasMinLength = watchNewPassword.length >= 12;
  const hasUpper = /[A-Z]/.test(watchNewPassword);
  const hasLower = /[a-z]/.test(watchNewPassword);
  const hasDigit = /\d/.test(watchNewPassword);
  const hasSpecial = /[@$!%*?&._\-#^()+={}\[\]|:;"'<>,/]/.test(watchNewPassword);
  const passedCount = [hasMinLength, hasUpper, hasLower, hasDigit, hasSpecial].filter(Boolean).length;

  const onSubmit = async (data: ChangePasswordFormData) => {
    setIsSubmitting(true);
    setSuccess(false);
    clearError();

    try {
      await changePassword({
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
      });
      setSuccess(true);
      reset();
    } catch (e) {
      // handled
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 max-w-xl shadow-xl">
      <div className="flex items-center gap-3 mb-6 pb-4 border-b border-slate-800">
        <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-2xl ring-1 ring-indigo-500/20">
          <Shield className="w-6 h-6" />
        </div>
        <div>
          <h3 className="text-lg font-bold text-white">Change Account Password</h3>
          <p className="text-xs text-slate-400">Update your official password in compliance with enterprise governance policy</p>
        </div>
      </div>

      {success && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-6 p-4 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 flex items-center gap-3 text-xs"
        >
          <CheckCircle2 className="w-5 h-5 shrink-0" />
          <span>Your account password has been successfully updated and saved.</span>
        </motion.div>
      )}

      {authError && (
        <div className="mb-6 p-4 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-400 flex items-center gap-3 text-xs">
          <AlertCircle className="w-5 h-5 shrink-0" />
          <span>{authError}</span>
        </div>
      )}

      <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>
        {/* Current Password */}
        <div>
          <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
            Current Password
          </label>
          <div className="relative">
            <Lock className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
            <input
              type={showCurrentPassword ? 'text' : 'password'}
              {...register('currentPassword')}
              placeholder="••••••••••••"
              className="block w-full pl-10 pr-10 py-3 text-sm bg-slate-950/80 border border-slate-800 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <button
              type="button"
              onClick={() => setShowCurrentPassword(!showCurrentPassword)}
              className="absolute right-3.5 top-3.5 text-slate-500 hover:text-slate-300"
            >
              {showCurrentPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>
          {errors.currentPassword && <p className="mt-1 text-xs text-rose-400">{errors.currentPassword.message}</p>}
        </div>

        {/* New Password */}
        <div>
          <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
            New Compliant Password
          </label>
          <div className="relative">
            <Lock className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
            <input
              type={showNewPassword ? 'text' : 'password'}
              {...register('newPassword')}
              placeholder="Min 12 chars (Upper, Lower, Digit, Special)"
              className="block w-full pl-10 pr-10 py-3 text-sm bg-slate-950/80 border border-slate-800 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <button
              type="button"
              onClick={() => setShowNewPassword(!showNewPassword)}
              className="absolute right-3.5 top-3.5 text-slate-500 hover:text-slate-300"
            >
              {showNewPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>

          {watchNewPassword && (
            <div className="mt-3 p-3 rounded-xl bg-slate-950/60 border border-slate-800 text-[11px] space-y-1">
              <div className="flex items-center justify-between mb-1">
                <span className="font-bold text-slate-400">Policy Compliance Bar</span>
                <span className="font-mono font-bold text-indigo-400">{passedCount}/5 Met</span>
              </div>
              <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden">
                <div
                  className={`h-full transition-all duration-300 ${
                    passedCount <= 2 ? 'bg-rose-500' : passedCount <= 4 ? 'bg-amber-500' : 'bg-emerald-500'
                  }`}
                  style={{ width: `${(passedCount / 5) * 100}%` }}
                />
              </div>
            </div>
          )}
          {errors.newPassword && <p className="mt-1 text-xs text-rose-400">{errors.newPassword.message}</p>}
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full py-3.5 px-4 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl shadow-lg shadow-indigo-600/30 focus:outline-none transition-all flex items-center justify-center gap-2 text-sm disabled:opacity-50"
        >
          {isSubmitting ? (
            <>
              <Loader2 className="w-4 h-4 animate-spin" />
              <span>Updating Password...</span>
            </>
          ) : (
            <span>Update Account Password</span>
          )}
        </button>
      </form>
    </div>
  );
};
