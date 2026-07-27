# Tech Knife - Incident Response & Escalation Guide

## 1. Overview
This guide defines the incident response framework for the **Tech Knife** production platform.

---

## 2. Severity Classification

| Severity Level | Definition | Target SLA (Response / Resolution) | Escalation Contact |
|---|---|---|---|
| **SEV-1 (Critical)** | Core API offline, complete system outage, data corruption, or active data breach | **15 minutes** / **1 hour** | Lead SRE + CTO |
| **SEV-2 (Major)** | Degradation of key module (e.g., Payroll Engine offline or Customer Portal inaccessible) | **30 minutes** / **4 hours** | Principal DevOps Lead |
| **SEV-3 (Minor)** | Non-blocking feature bug or UI glitch with available workaround | **2 hours** / **24 hours** | On-call Engineer |
| **SEV-4 (Low)** | Minor cosmetic issue or documentation typo | **8 hours** / **Next Sprint** | Engineering Team |

---

## 3. Incident Execution Workflow

### Step 1: Triage & Identification
1. Alert triggered via Render Health check, Vercel status alert, or SRE monitoring.
2. Confirm severity level (SEV-1 through SEV-4).
3. Create Incident Channel in Slack/Teams: `#incident-YYYYMMDD-[scope]`.

### Step 2: Containment & Mitigation
- **API Server Down**: Trigger container restart on Render or roll back to last known healthy git commit.
- **Database Connection Saturated**: Increase MongoDB Atlas connection pool size or scale Atlas cluster tier.
- **DDoS / High Traffic Spike**: Enable aggressive rate-limiting rules in Nginx reverse proxy or Vercel Web Application Firewall (WAF).

### Step 3: Root Cause Analysis (RCA) & Post-Mortem
1. Export Nginx access logs and application logs.
2. Document incident timeline, root cause, impact duration, and permanent preventative action items.
3. Publish RCA report in `/docs/incidents/` within 48 hours of incident resolution.
