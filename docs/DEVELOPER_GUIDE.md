# Tech Knife - Senior Developer & Architectural Guide

## 1. Overview
**Tech Knife** (*"It's Time for Technology"*) is an enterprise technology consulting and software solutions platform engineered with React 18, TypeScript, Tailwind CSS, and Motion.

## 2. Technical Stack
- **Framework**: React 18 + Vite
- **Language**: TypeScript (Strict Mode enabled)
- **Styling**: Tailwind CSS v4 + Lucide React Icons
- **Animation**: Motion (`motion/react`)
- **State & Data**: React Context API, TanStack Query, React Hook Form + Zod
- **Data Table**: Custom `DataTable` with resizable columns, sticky headers, bulk selection, CSV export, and quick filter

## 3. Application Architecture & Portals
The application is split into four distinct security domains and a public marketing suite:
1. **Public Marketing Suite** (`/`, `/about`, `/services`, `/internship`, `/blog`)
2. **Admin Portal** (`/admin`, `/organization/*`, `/crm/*`, `/payroll`, `/assets`, `/cms`, `/audit-logs`)
3. **Employee Portal** (`/employee/dashboard`, `/attendance`, `/leave`, `/projects`, `/timesheets`)
4. **Customer Portal** (`/customer/dashboard`, `/customer/projects`, `/customer/support`, `/customer/invoices`)
5. **Authentication Suite** (`/login`, `/register`, `/forgot-password`, `/verify-otp`, `/verify-email`)

## 4. Key Architectural Rules
- **Brand Name**: Always use **Tech Knife** (never "Tech Knife Enterprise" or "ERP").
- **Logo Usage**: Use `<Logo variant="full" size="..." showTagline inverted />` in dark mode headers and login cards.
- **Data Persistence**: All state changes utilize mock API services (`/src/api/*`) and React state for client responsiveness.
