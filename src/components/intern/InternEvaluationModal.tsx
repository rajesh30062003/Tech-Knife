import React, { useState } from 'react';
import { X, Star, Award, Loader2 } from 'lucide-react';
import { Intern } from '../../types';

interface InternEvaluationModalProps {
  intern: Intern | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (internId: string, evaluation: NonNullable<Intern['finalEvaluation']>) => Promise<void>;
}

export const InternEvaluationModal: React.FC<InternEvaluationModalProps> = ({
  intern,
  isOpen,
  onClose,
  onSubmit,
}) => {
  const [technicalRating, setTechnicalRating] = useState(5);
  const [softSkillsRating, setSoftSkillsRating] = useState(5);
  const [codeQualityRating, setCodeQualityRating] = useState(5);
  const [overallFeedback, setOverallFeedback] = useState('Demonstrated high problem-solving capability and code ownership.');
  const [ppoRecommendation, setPpoRecommendation] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!isOpen || !intern) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      await onSubmit(intern.id, {
        technicalRating,
        softSkillsRating,
        codeQualityRating,
        overallFeedback,
        ppoRecommendation,
      });
      onClose();
    } catch (err) {
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-lg p-6 space-y-4 shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
          <div className="flex items-center gap-2">
            <Award className="w-5 h-5 text-amber-500" />
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white">
              Final Performance Review for {intern.firstName}
            </h3>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600">
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4 text-xs">
          <div className="space-y-3">
            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                Technical Proficiency Rating (1 - 5 Stars)
              </label>
              <input
                type="range"
                min="1"
                max="5"
                value={technicalRating}
                onChange={(e) => setTechnicalRating(Number(e.target.value))}
                className="w-full accent-amber-500"
              />
              <div className="flex justify-between text-[11px] font-bold text-amber-600">
                <span>Rating: {technicalRating} / 5</span>
              </div>
            </div>

            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                Communication & Soft Skills (1 - 5 Stars)
              </label>
              <input
                type="range"
                min="1"
                max="5"
                value={softSkillsRating}
                onChange={(e) => setSoftSkillsRating(Number(e.target.value))}
                className="w-full accent-amber-500"
              />
              <div className="flex justify-between text-[11px] font-bold text-amber-600">
                <span>Rating: {softSkillsRating} / 5</span>
              </div>
            </div>

            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                Code Quality & Testing Standards (1 - 5 Stars)
              </label>
              <input
                type="range"
                min="1"
                max="5"
                value={codeQualityRating}
                onChange={(e) => setCodeQualityRating(Number(e.target.value))}
                className="w-full accent-amber-500"
              />
              <div className="flex justify-between text-[11px] font-bold text-amber-600">
                <span>Rating: {codeQualityRating} / 5</span>
              </div>
            </div>

            <div>
              <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                Mentor Feedback & Qualitative Assessment
              </label>
              <textarea
                rows={3}
                required
                value={overallFeedback}
                onChange={(e) => setOverallFeedback(e.target.value)}
                className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-900 dark:text-white"
              />
            </div>

            <div className="flex items-center gap-3 p-3 bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 rounded-xl">
              <input
                type="checkbox"
                id="ppoRec"
                checked={ppoRecommendation}
                onChange={(e) => setPpoRecommendation(e.target.checked)}
                className="w-4 h-4 text-amber-600 rounded"
              />
              <label htmlFor="ppoRec" className="font-bold text-amber-900 dark:text-amber-200 text-xs">
                Recommend for Pre-Placement Offer (PPO) Full-time hiring
              </label>
            </div>
          </div>

          <div className="flex items-center justify-end gap-2 pt-3 border-t border-slate-100 dark:border-slate-800">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl font-bold"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-5 py-2 bg-amber-600 hover:bg-amber-500 text-white font-bold rounded-xl shadow flex items-center gap-2"
            >
              {isSubmitting && <Loader2 className="w-3.5 h-3.5 animate-spin" />}
              <span>Save Evaluation</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
