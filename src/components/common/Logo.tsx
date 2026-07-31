import React, { useId } from 'react';

interface LogoProps {
  variant?: 'full' | 'mark' | 'text';
  size?: 'sm' | 'md' | 'lg' | 'xl' | '2xl';
  className?: string;
  showTagline?: boolean;
  inverted?: boolean;
}

export const Logo: React.FC<LogoProps> = ({
  variant = 'full',
  size = 'md',
  className = '',
  showTagline = false,
  inverted = false,
}) => {
  const rawId = useId();
  const id = rawId.replace(/:/g, '');

  // Size mapping for the SVG mark height/width
  const dimensions = {
    sm: { height: 28, markSize: 26, fontSize: 'text-sm', taglineSize: 'text-[8px]' },
    md: { height: 36, markSize: 34, fontSize: 'text-base', taglineSize: 'text-[9px]' },
    lg: { height: 48, markSize: 44, fontSize: 'text-xl', taglineSize: 'text-[10px]' },
    xl: { height: 64, markSize: 58, fontSize: 'text-2xl', taglineSize: 'text-[11px]' },
    '2xl': { height: 80, markSize: 74, fontSize: 'text-3xl', taglineSize: 'text-[12px]' }
  }[size];

  // SVG Mark of the Tech Knife official geometric emblem
  const LogoMark = (
    <svg
      width={dimensions.markSize}
      height={dimensions.markSize}
      viewBox="0 0 200 200"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className="shrink-0 transition-transform duration-300 hover:scale-105 drop-shadow-md"
    >
      <defs>
        <linearGradient id={`tkGradLeft-${id}`} x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stopColor="#1d4ed8" />
          <stop offset="100%" stopColor="#3b82f6" />
        </linearGradient>
        <linearGradient id={`tkGradMid-${id}`} x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stopColor="#2563eb" />
          <stop offset="100%" stopColor="#06b6d4" />
        </linearGradient>
        <linearGradient id={`tkGradSharp-${id}`} x1="50%" y1="0%" x2="50%" y2="100%">
          <stop offset="0%" stopColor="#60a5fa" />
          <stop offset="100%" stopColor="#1e3a8a" />
        </linearGradient>
      </defs>

      {/* Main geometric knife blade polygon left wing */}
      <path
        d="M 55,35 L 115,25 L 75,135 L 55,140 Z"
        fill={`url(#tkGradLeft-${id})`}
      />
      {/* Knife facet fold centerpiece */}
      <path
        d="M 115,25 L 110,65 L 85,75 L 105,100 L 75,175 Z"
        fill={`url(#tkGradSharp-${id})`}
      />
      {/* Front sharp blade angle */}
      <path
        d="M 115,25 L 130,50 L 110,65 Z"
        fill="#38bdf8"
      />
      {/* Lower inner geometric shard */}
      <path
        d="M 85,75 L 110,78 L 105,100 L 90,105 Z"
        fill="#2563eb"
      />
      {/* Right stepped block nodes (as in official Tech Knife logo) */}
      <path
        d="M 128,50 L 145,54 L 143,63 L 126,59 Z"
        fill="#38bdf8"
      />
      <path
        d="M 122,70 L 139,74 L 136,84 L 119,80 Z"
        fill="#2563eb"
      />
      <path
        d="M 116,90 L 133,94 L 130,105 L 113,101 Z"
        fill="#1d4ed8"
      />
      {/* Outer framing angular shroud */}
      <path
        d="M 115,25 L 140,48 L 136,68 L 118,175 C 105,135 90,115 75,175 Z"
        fill={`url(#tkGradMid-${id})`}
        opacity="0.95"
      />
    </svg>
  );

  if (variant === 'mark') {
    return <div className={`inline-flex items-center ${className}`}>{LogoMark}</div>;
  }

  const techTextColor = inverted
    ? 'text-white'
    : 'text-slate-900 dark:text-white';

  const knifeTextColor = inverted
    ? 'text-cyan-400'
    : 'text-blue-600 dark:text-cyan-400';

  const taglineColor = inverted
    ? 'text-slate-300'
    : 'text-slate-500 dark:text-slate-400';

  return (
    <div className={`inline-flex items-center gap-2.5 ${className}`}>
      {LogoMark}
      {variant !== 'mark' && (
        <div className="flex flex-col leading-none">
          <div className={`font-black tracking-tight ${techTextColor} ${dimensions.fontSize}`}>
            TECH<span className={`${knifeTextColor} ml-1`}>KNIFE</span>
          </div>
          {showTagline && (
            <div className={`mt-0.5 tracking-[0.18em] font-extrabold ${taglineColor} uppercase ${dimensions.taglineSize}`}>
              INFRASTRUCTURE TO INTELLIGENCE
            </div>
          )}
        </div>
      )}
    </div>
  );
};
