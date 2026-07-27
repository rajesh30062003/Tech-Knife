# Tech Knife - Global Technology Consulting & Software Solutions Platform

Welcome to the official frontend application for **Tech Knife** (*"It's Time for Technology"*). Tech Knife is an enterprise-grade technology consulting, software engineering, and digital transformation platform engineered with React 18, TypeScript, Tailwind CSS, and Framer Motion.

---

## 🚀 Key Modules & Capabilities

### 1. 🌐 Public Corporate Web Portal
- **Landing Page**: Interactive hero banner, client logos marquee, live statistics counters, tech stack showcase, case studies, client testimonials, and newsletter capture.
- **Services & Solutions**: Detailed pages for Cloud Architecture, AI Engineering, DevOps Automation, Cybersecurity, and Custom Enterprise Software.
- **Career & Global Internship**: Interactive portal for career applications and student technology internships.

### 2. 🏛️ Admin Portal
- **Executive Dashboard**: Real-time organizational analytics, revenue tracking, headcount stats, and project health indicators.
- **Organization Management**: Multi-branch management, department hierarchy, team assignments, designations, and role-based permissions.
- **CRM & Project Governance**: Customer accounts, project lifecycles, interactive Kanban Sprint boards, GitHub repository sync, and ticket tracking.
- **Payroll, Assets & Finance**: Invoice generation, hardware asset inventory (serial tracking & warranty coverage), procurement, and expense tracking.
- **System Services**: Approval workflow engine, cloud storage vault, system audit logs, and security policy management.

### 3. 👥 Employee Self-Service Portal
- **Daily Attendance**: Biometric/GPS check-in and check-out tracking, timesheets, and leave balance calculations.
- **Task & Sprint Center**: Assigned project tasks, Kanban board, and pull request activity.
- **Payroll & Documents**: Downloadable payslips, tax certificates, and NDA compliance documents.
- **Performance & Training**: KPI reviews, skill certifications, and internal training modules.

### 4. 🏢 Customer Portal
- **Client Workspace**: Live project milestones, interactive delivery timelines, and budget burn-up charts.
- **Support & Communication**: Helpdesk ticket submission, SLA tracking, invoice payment history, and shared project deliverables.

### 5. 📰 CMS Content Manager
- **Page & Article Builder**: Live publish workflow for corporate web pages, thought leadership blogs, and press releases.
- **Media Vault**: Global CDN asset manager for images, vector graphics, and downloadable PDF reports.
- **SEO Engine**: Real-time Google search snippet previews, Meta title templates, OpenGraph cards, and schema generator.

---

## 📁 Directory Architecture

```
/
├── index.html                  # HTML entry point with JSON-LD, PWA manifest, and inline SVG favicon
├── metadata.json               # Platform configuration metadata
├── manifest.json               # Progressive Web App (PWA) manifest
├── src/
│   ├── api/                    # API client layer & Axios interceptors
│   ├── components/
│   │   ├── common/             # Reusable UI components (DataTable, StatusBadge, Logo, etc.)
│   │   ├── core/               # Core engine components (AuditTrailViewer, UniversalSearchModal, etc.)
│   │   ├── layout/             # Application layouts (Navbar, Sidebar, PublicLayout, Footer)
│   │   └── dashboard/          # Specialized chart and visualizer cards
│   ├── context/                # React Context (AuthContext, ThemeContext)
│   ├── pages/                  # Page routes grouped by feature domain
│   │   ├── admin/              # Executive Admin & Governance pages
│   │   ├── auth/               # Authentication & OTP pages
│   │   ├── crm/                # CRM & Client Management
│   │   ├── customer/           # Customer Portal pages
│   │   ├── employee/           # Employee Self-Service pages
│   │   ├── public/             # Marketing & Public Web pages
│   │   └── settings/           # System & User Settings
│   ├── types/                  # Shared TypeScript interfaces & types
│   └── utils/                  # Helper utilities and formatting functions
└── vite.config.ts              # Vite build configuration
```

---

## 🛠️ Environment Variables (.env.example)

Create a `.env` file in the project root:

```env
# API Gateway Endpoint
VITE_API_BASE_URL=https://api.techknife.com/v1

# AI Studio Server-Side Gemini API Key (Server Only)
GEMINI_API_KEY=your_gemini_api_key_here

# Application Environment
VITE_APP_ENV=production
```

---

## 💻 Local Development Guide

### 1. Install Dependencies
```bash
npm install
```

### 2. Start Local Development Server
```bash
npm run dev
```
The application will launch on `http://localhost:3000`.

### 3. Lint Code
```bash
npm run lint
```

### 4. Production Build
```bash
npm run build
```
Outputs bundled production static assets into `dist/`.

---

## 🎨 Tech Knife Design System

- **Primary Colors**: Deep Navy (`#0f172a`), Royal Blue (`#2563eb`), Bright Cyan (`#06b6d4`, `#38bdf8`)
- **Typography**: Inter / Plus Jakarta Sans for UI controls, Playfair Display for display headers
- **Brand Slogan**: *"It's Time for Technology"*
- **Design Principles**: WCAG 2.2 AA accessibility, high-contrast dark/light mode, non-overlapping SVG geometric emblems, and dense enterprise data density.

---

## ⚡ Performance Optimizations

1. **Route Splitting**: Dynamic React code splitting via `React.lazy` for fast page loads.
2. **DataTable Virtualization**: Sticky headers, column toggles, quick client-side filtering, and CSV data export.
3. **Optimized Animations**: Framer Motion transitions with reduced motion preference support.
4. **Offline First (PWA)**: Web App Manifest and offline splash screens.

---

© 2026 Tech Knife. All Rights Reserved. *"It's Time for Technology"*.
