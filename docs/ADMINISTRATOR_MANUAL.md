# Tech Knife - Executive Administrator Manual

## 1. Introduction
Welcome to the Executive Administrator Manual for **Tech Knife** (*"It's Time for Technology"*). This guide details administrative procedures for governing organization hierarchies, role-based access control (RBAC), multi-branch settings, employee lifecycles, payroll execution, and security audit monitoring.

---

## 2. Accessing the Executive Admin Portal
- **URL**: `https://www.techknife.com/admin` (or `/admin/dashboard` in development)
- **Role Requirement**: `ROLE_SUPER_ADMIN` or `ROLE_ADMIN`
- **Multi-Factor Authentication (MFA)**: Enforced via 6-digit TOTP / email OTP verification upon sign-in.

---

## 3. Core Administrative Modules

### 3.1 Organization & Multi-Branch Governance (`/organization/*`)
- **Branches**: Manage global enterprise branch offices (e.g., San Francisco, London, Singapore, Dhaka). Configure location timezone, primary currency, tax identification numbers, and localized compliance rules.
- **Departments & Teams**: Create department hierarchies (Engineering, AI & Cloud Solutions, DevOps, Cybersecurity, Finance, HR) and assign department heads.
- **Designations & Job Bands**: Define career matrices (Junior Engineer to Principal Software Architect) with associated salary scale bands.

### 3.2 Human Capital & Payroll Management (`/payroll`, `/employee/*`)
- **Employee Onboarding**: Provision staff accounts with automatic corporate email generation (`@techknife.com`), department tagging, and initial role assignments.
- **Attendance & Biometric Overrides**: Review GPS/Biometric check-in logs, approve manual attendance regularization requests, and export monthly attendance reports.
- **Leave Quota Management**: Set annual leave balances (Annual, Sick, Casual, Maternity, Paternity) and manage approval delegation chains.
- **Monthly Mass Payroll Engine**: Calculate base salary, tax withholdings, allowances, bonuses, and generate downloadable digital payslips in PDF format.

### 3.3 CRM & Enterprise Project Portfolio (`/crm/*`)
- **Client Account Governance**: Manage enterprise customer accounts, assign dedicated account managers, and set contract credit lines.
- **Project Governance & Sprint Tracking**: View project health metrics, task completion rates, burn-up charts, budget utilization, and pull request activity via GitHub integration.

### 3.4 Hardware Assets, Procurement & Finance (`/assets`, `/finance/*`)
- **Hardware Asset Inventory**: Track enterprise laptops, mobile testing devices, and server racks by serial number, assignment status, warranty expiration date, and depreciation schedule.
- **Procurement Orders**: Approve hardware and software purchase orders with multi-tier authorization limits.
- **Invoicing & Ledger**: Issue tax-compliant invoices to client accounts and track accounts receivable.

### 3.5 System Services & Security Governance (`/cms`, `/audit-logs`)
- **System Audit Logs**: Real-time immutable record of user logins, role modifications, document access, and high-value financial actions.
- **CMS Management**: Control public corporate website content, news press releases, and career job listings.

---

## 4. Emergency & Security Protocols
- **Immediate Account Revocation**: Navigate to User Management → Select User → Click "Deactivate Account" to instantly revoke all active JWT tokens and session keys.
- **Security Audit Export**: Export CSV/JSON logs filtered by severity level (`INFO`, `WARN`, `CRITICAL`, `SECURITY_BREACH`).
