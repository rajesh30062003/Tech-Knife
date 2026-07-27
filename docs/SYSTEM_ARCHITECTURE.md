# Tech Knife - High-Level System Architecture & Security Specification

## 1. Executive Summary
**Tech Knife** (*"It's Time for Technology"*) is built on a modern full-stack web architecture engineered for multi-tenant enterprise scalability, zero-trust security, high availability, and rapid performance.

---

## 2. High-Level Architecture Diagram (Logical Flow)

```
[ Client Browsers / PWA Mobile ]
               │
               ▼
   [ Cloud Run Ingress / Nginx ] (Port 3000)
               │
               ▼
     [ Node.js + Express API ] <───> [ React 18 + Vite SPA Frontend ]
               │
   ┌───────────┼───────────┐
   ▼           ▼           ▼
[ MongoDB ] [ Gemini AI ] [ Cloud Storage / CDN ]
```

---

## 3. Technology Stack Breakdown
- **Frontend Layer**: React 18, TypeScript (Strict Mode), Vite, Tailwind CSS v4, Motion, Lucide React.
- **Backend API Layer**: Node.js, Express, JWT Bearer Auth, Rate Limiting, Helmet Security Middleware.
- **Data Persistence**: MongoDB / Firestore Document Store with strict schema validation.
- **AI Engine**: Google GenAI SDK (`@google/genai`) for server-side natural language processing and document parsing.
- **PWA Capabilities**: Service workers, web app manifest, offline fallback UI, and local storage state caching.

---

## 4. Security Architecture & Threat Mitigation
- **XSS Mitigation**: React automatic JSX escaping + DOMPurify for dynamic rich text content.
- **CSRF Safeguards**: SameSite=Strict HTTP-only cookies and CSRF token header validation.
- **RBAC Matrix**: Enforced at both route guard level (`ProtectedRoute`) and backend API endpoint level.
- **Content Security Policy (CSP)**: Strict script-src and frame-ancestors restrictions.
