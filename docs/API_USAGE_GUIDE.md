# Tech Knife - API Service & Integration Specification

## API Architecture
The frontend communicates with backend services via Axios instances with JWT Bearer interceptors defined in `/src/api/client.ts`.

## Core Service Endpoints

### 1. Authentication Service (`/api/v1/auth`)
- `POST /login`: Authenticates official email and password. Returns JWT payload.
- `POST /verify-otp`: Validates 6-digit MFA OTP code.
- `POST /verify-email`: Validates email token.

### 2. CRM & Customer Service (`/api/v1/crm`)
- `GET /customers`: Returns list of enterprise client accounts.
- `POST /customers`: Registers new client account.
- `GET /projects`: Retrieves project status and sprint progress.

### 3. Employee & Attendance Service (`/api/v1/hr`)
- `POST /attendance/check-in`: Registers daily check-in timestamp with geolocation/biometrics.
- `POST /attendance/check-out`: Registers daily check-out timestamp.
- `GET /leave/balance`: Retrieves employee leave quota and history.

### 4. CMS Content Service (`/api/v1/cms`)
- `GET /pages`: Lists web pages for public corporate portal.
- `POST /articles`: Publishes blog post or press release.
