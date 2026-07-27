# Tech Knife - Enterprise Engineering & Coding Standards

## 1. Code Quality Principles
- **TypeScript Strictness**: `noImplicitAny`, strict null checks, and explicit interface definitions for props and model shapes.
- **Component Architecture**: Extract subcomponents out of huge pages. Use functional React components with hooks.
- **Lucide Icons Only**: All SVG icons must be imported from `lucide-react`.
- **Motion Animations**: Use `motion` imported from `motion/react` for route transitions and hover states.

## 2. Branding & Styling Guidelines
- **Brand Colors**: Deep Navy (`#0f172a`), Royal Blue (`#2563eb`), Bright Cyan (`#38bdf8`), Dark Slate.
- **Logo Component**: Standardized `<Logo />` component with `inverted` prop for dark backgrounds.
- **Accessibility**: WCAG 2.2 AA compliant contrast, aria labels on interactive controls, keyboard navigation support.
