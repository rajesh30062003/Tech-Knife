import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { motion } from 'motion/react';
import { MailCheck, Mail, KeyRound, ShieldCheck, Loader2, AlertCircle, CheckCircle2, ArrowRight } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { Logo } from '../../components/common/Logo';

const verifyEmailSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Valid email is required'),
  otpCode: z.string().min(6, '6-digit OTP is required'),
});

type VerifyEmailFormData = z.infer<typeof verifyEmailSchema>;

export const VerifyEmail: React.FC = () => {
  const navigate = useNavigate();
  const { user, sendOtp, verifyOtp, error: authError, clearError } = useAuth();

  const [isSendingOtp, setIsSendingOtp] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [otpSent, setOtpSent] = useState(false);
  const [verifiedSuccess, setVerifiedSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm<VerifyEmailFormData>({
    resolver: zodResolver(verifyEmailSchema),
    defaultValues: {
      email: user?.email || '',
      otpCode: '',
    },
  });

  const handleSendOtp = async () => {
    const email = getValues('email');
    if (!email) return;

    setIsSendingOtp(true);
    clearError();
    try {
      await sendOtp({ email, type: 'EMAIL_VERIFICATION' });
      setOtpSent(true);
    } catch (e) {
      // handled
    } finally {
      setIsSendingOtp(false);
    }
  };

  const onSubmit = async (data: VerifyEmailFormData) => {
    setIsVerifying(true);
    clearError();
    try {
      const res = await verifyOtp({
        email: data.email,
        otpCode: data.otpCode,
        type: 'EMAIL_VERIFICATION',
      });
      if (res) {
        setVerifiedSuccess(true);
        setTimeout(() => {
          navigate('/profile');
        }, 2000);
      }
    } catch (e) {
      // handled
    } finally {
      setIsVerifying(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8 relative overflow-hidden">
      <div className="absolute top-0 left-1/3 w-96 h-96 bg-teal-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="sm:mx-auto sm:w-full sm:max-w-md relative z-10 text-center">
        <Link to="/" className="inline-block hover:scale-105 transition-transform">
          <Logo variant="full" size="xl" showTagline inverted />
        </Link>
        <h2 className="mt-6 text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          Verify Official Email
        </h2>
        <p className="mt-2 text-xs sm:text-sm text-slate-400">
          Confirm ownership of your official email address to unlock all enterprise modules
        </p>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="mt-8 sm:mx-auto sm:w-full sm:max-w-md relative z-10 px-4"
      >
        <div className="bg-slate-900/90 backdrop-blur-xl border border-slate-800 py-8 px-6 shadow-2xl rounded-3xl sm:px-10">
          {verifiedSuccess ? (
            <div className="text-center space-y-4 py-4">
              <div className="w-16 h-16 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 rounded-3xl flex items-center justify-center mx-auto animate-bounce">
                <CheckCircle2 className="w-10 h-10" />
              </div>
              <h3 className="text-xl font-extrabold text-white">Email Verified!</h3>
              <p className="text-xs text-slate-300">
                Your official email address is now verified and active. Redirecting to profile...
              </p>
            </div>
          ) : (
            <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
              {authError && (
                <div className="p-3.5 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-400 flex items-center gap-2.5 text-xs">
                  <AlertCircle className="w-4 h-4 shrink-0" />
                  <span>{authError}</span>
                </div>
              )}

              {/* Email */}
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                  Official Email
                </label>
                <div className="relative">
                  <Mail className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
                  <input
                    type="email"
                    {...register('email')}
                    placeholder="name@techknife.com"
                    className="block w-full pl-10 pr-24 py-2.5 text-sm bg-slate-950/80 border border-slate-800 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-teal-500"
                  />
                  <button
                    type="button"
                    onClick={handleSendOtp}
                    disabled={isSendingOtp}
                    className="absolute right-2 top-2 px-3 py-1 bg-teal-600 hover:bg-teal-500 text-white font-semibold text-xs rounded-lg transition-colors disabled:opacity-50"
                  >
                    {isSendingOtp ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : otpSent ? 'Resend' : 'Send OTP'}
                  </button>
                </div>
                {errors.email && <p className="mt-1 text-xs text-rose-400">{errors.email.message}</p>}
                {otpSent && <p className="mt-1 text-xs text-teal-400">OTP code sent to email inbox!</p>}
              </div>

              {/* OTP Code */}
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                  Verification OTP Code
                </label>
                <div className="relative">
                  <KeyRound className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-500" />
                  <input
                    type="text"
                    maxLength={6}
                    {...register('otpCode')}
                    placeholder="Enter 6-digit OTP code"
                    className="block w-full pl-10 pr-4 py-2.5 text-sm font-mono tracking-widest bg-slate-950/80 border border-slate-800 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-teal-500"
                  />
                </div>
                {errors.otpCode && <p className="mt-1 text-xs text-rose-400">{errors.otpCode.message}</p>}
              </div>

              <button
                type="submit"
                disabled={isVerifying}
                className="w-full mt-2 py-3.5 px-4 bg-teal-600 hover:bg-teal-500 text-white font-bold rounded-xl shadow-lg shadow-teal-600/30 focus:outline-none transition-all flex items-center justify-center gap-2 text-sm disabled:opacity-50"
              >
                {isVerifying ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    <span>Verifying Code...</span>
                  </>
                ) : (
                  <>
                    <span>Verify Official Email</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </form>
          )}

          <div className="mt-6 pt-6 border-t border-slate-800 text-center text-xs text-slate-400">
            <Link to="/dashboard" className="text-teal-400 font-semibold hover:underline">
              Skip for now & Return to Dashboard
            </Link>
          </div>
        </div>
      </motion.div>
    </div>
  );
};
