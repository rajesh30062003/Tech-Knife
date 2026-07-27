# Changelog - Tech Knife Platform

All notable changes to the **Tech Knife** platform ("*It's Time for Technology*") will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-07-26

### Added
- **Public Corporate Web Portal**:
  - Interactive Hero section with geometric SVG Tech Knife logo emblem.
  - Trusted By marquee, Company Introduction, Services, Solutions, Industries, Products, Tech Stack, Portfolio, Case Studies, Statistics Counter, Process Timeline, Testimonials, Career Highlights, Global Technology Internship Program, FAQ, and Footer.
  - Full SEO suite with JSON-LD structured data, Open Graph, Twitter Cards, PWA Manifest, and SVG favicon.
- **Executive Admin Portal (`/admin/*`)**:
  - Executive KPI Analytics Dashboard with real-time revenue, project health, and headcount stats.
  - Organization Governance: Multi-branch office management, department hierarchies, and designations.
  - CRM & Project Governance: Customer accounts, Kanban Sprint boards, GitHub integration, and issue ticket tracking.
  - Human Resources & Monthly Mass Payroll Engine: Biometric check-in/out, leave approvals, tax deduction calculations, and PDF payslip generation.
  - Hardware Asset Inventory, Procurement Orders, Accounts Receivable Invoicing, and System Audit Logs.
- **Employee Self-Service Portal (`/employee/*`)**:
  - Daily biometric/GPS attendance check-in & check-out tracking.
  - Leave quota balances and leave application submission workflow.
  - Assigned task Kanban board, timesheet logging, payslip downloads, and training modules.
- **Customer Portal (`/customer/*`)**:
  - Project milestone delivery status, sprint progress bars, and deliverable sign-offs.
  - Invoice payment tracking and SLA support ticket helpdesk.
- **CMS Content Management System (`/cms`)**:
  - Public web page content builder with live desktop/tablet/mobile viewport previews.
  - Thought leadership tech blog authoring with interactive Google search snippet previewer.
  - Career vacancy and global internship program candidate screener.
- **DevOps & Infrastructure Suite**:
  - `tech-knife-frontend` configured for **Vercel** with SPA rewrites, security headers, and PWA manifest.
  - `tech-knife-backend` configured for **Render** with Node.js/Express, Health endpoints, and Rate Limiters.
  - **MongoDB Atlas** database configuration with connection pool management and transaction support.
  - Multi-stage Dockerfiles (`Dockerfile` for frontend, `tech-knife-backend/Dockerfile` for backend).
  - Docker Compose setups (`docker-compose.dev.yml` and `docker-compose.prod.yml`).
  - Nginx reverse proxy configuration (`/nginx/nginx.conf` and `/nginx/conf.d/techknife.conf`).
  - GitHub Actions CI/CD workflows (`.github/workflows/frontend-ci.yml` and `backend-ci.yml`).
  - Automated MongoDB backup (`/scripts/backup-mongodb.sh`) and restore (`/scripts/restore-mongodb.sh`) shell scripts.
  - Prometheus-ready metrics collector and cron background worker engine.
