# Tech Knife Enterprise Management System (TKEMS) - Backend Engine

Enterprise-grade Spring Boot 3.5 & Java 21 backend application powering the Tech Knife Enterprise Platform.

## 🚀 Technology Stack

- **Framework**: Spring Boot 3.5.0
- **Runtime**: Java 21 LTS
- **Database**: MongoDB Atlas (Spring Data MongoDB)
- **Security**: Spring Security + JWT (JSON Web Token) with Role-Based Access Control (RBAC)
- **Validation**: Jakarta Bean Validation + Custom Annotations
- **DTO Mapping**: MapStruct & Lombok
- **Object Storage**: Cloudinary SDK
- **Email Service**: Spring Boot Starter Mail (SMTP)
- **API Documentation**: Springdoc OpenAPI / Swagger UI
- **Auditing**: Aspect-Oriented Programming (AOP) + Custom Audit Logger

## 📁 Package Architecture

```
com.techknife.backend/
├── audit/         # AOP Audit Logger & Aspect Interceptors
├── config/        # Mongo, Security, Cors, Swagger & Async Configurations
├── constant/      # Enums (Roles, Statuses) & Security Constants
├── controller/    # REST Endpoints (Auth, Employee, Intern, Health)
├── dto/           # Request/Response Wrappers & Payload Contracts
├── entity/        # Mongo DB Collections & Base Audit Entities (Employee, Intern, Task)
├── event/         # Spring Application Events
├── exception/     # Custom Exceptions & Global Exception Handler
├── github/        # GitHub API Integration Services
├── listener/      # Event Listeners
├── mail/          # SMTP Email Delivery Services & Templates
├── mapper/        # MapStruct Bean Mappers
├── repository/    # Mongo Repositories (EmployeeRepository, InternRepository)
├── scheduler/     # Cron Schedulers & System Tasks
├── security/      # JWT Filter, Authentication Provider & Token Utilities
├── service/       # Service Interfaces (EmployeeService, InternService)
├── serviceImpl/   # Service Implementations
├── storage/       # Cloudinary Attachment Service
├── util/          # Core Helpers & Security Context Utilities
└── validator/     # Custom Validation Annotations
```

## 🎓 Intern Management Module Features

- **Intern CRUD**: Complete lifecycle tracking with auto-generated Intern ID (`INT-2026-XXX`).
- **Academic & Mentor Tracking**: Institution records, assigned mentors, department placement, monthly stipend, and CGPA tracking.
- **Task & Milestone Management**: Assign daily deliverables and weekly sprint tasks with scoring and mentor feedback.
- **Performance Evaluation**: Comprehensive rating framework (Technical, Soft Skills, Code Quality) with PPO (Pre-Placement Offer) recommendation engine.
- **Certificate Metadata Generation**: Automated creation of cryptographically signed completion certificate metadata.
- **Intern to Full-Time Conversion**: Seamless one-click conversion from intern status to full-time Employee directory entry.
- **RBAC Enforcement**: Restricted creation/updates to Admins, MD, CEO, CTO, and Managers; self-service view for interns using official corporate email.

## 📡 Intern Management REST APIs

| Method | Endpoint | Access Role | Description |
|---|---|---|---|
| `GET` | `/api/interns` | All Authenticated | Search, filter, and paginate intern cohort |
| `POST` | `/api/interns` | Admin, Manager | Register new intern cohort member |
| `GET` | `/api/interns/{id}` | All Authenticated | Fetch intern detail profile |
| `PUT` | `/api/interns/{id}` | Admin, Manager | Update intern details & assigned mentor |
| `PATCH` | `/api/interns/{id}/status` | Admin, Manager | Update intern status (`Active`, `On Review`, `Suspended`, etc.) |
| `DELETE` | `/api/interns/{id}` | Super Admin, MD, CTO | Delete intern record |
| `POST` | `/api/interns/{id}/tasks` | Admin, Mentor | Assign daily/weekly task milestone |
| `POST` | `/api/interns/{id}/evaluate` | Admin, Mentor | Submit final performance evaluation & PPO recommendation |
| `POST` | `/api/interns/{id}/generate-certificate` | Admin | Issue completion certificate metadata |
| `POST` | `/api/interns/{id}/convert-to-employee` | Admin, HR | Convert intern to full-time Employee record |
| `GET` | `/api/interns/statistics` | All Authenticated | Fetch intern cohort analytics & PPO rates |

## 🗄️ MongoDB Indexes (Intern Collection)

```json
[
  { "key": { "internId": 1 }, "name": "idx_intern_id", "unique": true },
  { "key": { "officialEmail": 1 }, "name": "idx_official_email", "unique": true },
  { "key": { "department": 1, "status": 1 }, "name": "idx_dept_status" },
  { "key": { "mentorId": 1 }, "name": "idx_mentor_id" },
  { "key": { "performanceScore": -1 }, "name": "idx_perf_score" }
]
```

## 🎓 Attendance Management Module Features

- **Daily Punch Engine**: Check-In, Check-Out, and Break timer toggling with geo-coordinates and IP logging.
- **Automated Time Calculations**: Dynamic net work duration, total break duration, overtime calculation (>8 hours/day), late flag (>09:15 AM), and half-day classification (<4 hours).
- **Admin Correction & Manual Entry**: Administrative audit correction of punches with mandatory reason logging and audit trail event emission.
- **Bulk CSV Import**: Import historical attendance registers in bulk via CSV parsing.
- **Analytics & Reporting**: Monthly departmental attendance matrix, yearly employee summary, and interactive visual calendar view.
- **RBAC Security Enforcement**: Self-service check-in for all employees; admin/manager restricted correction, manual entries, and bulk import.

## 📡 Attendance Management REST APIs

| Method | Endpoint | Access Role | Description |
|---|---|---|---|
| `GET` | `/api/v1/attendance/today` | All Authenticated | Fetch current user's today attendance record |
| `POST` | `/api/v1/attendance/check-in` | All Authenticated | Record daily check-in with location & WFH status |
| `POST` | `/api/v1/attendance/{id}/check-out` | All Authenticated | Record check-out & calculate work duration |
| `POST` | `/api/v1/attendance/{id}/break` | All Authenticated | Start or end break time punch |
| `GET` | `/api/v1/attendance/history` | All Authenticated | Fetch attendance history ledger with search & date filtering |
| `GET` | `/api/v1/attendance/calendar` | All Authenticated | Retrieve monthly calendar attendance records |
| `PUT` | `/api/v1/attendance/{id}/correct` | Admin, Manager | Correct employee attendance record with audit reason |
| `POST` | `/api/v1/attendance/manual` | Admin, Manager | Create manual attendance record for staff |
| `POST` | `/api/v1/attendance/bulk-import` | Admin, Manager | Bulk import attendance records via CSV |
| `GET` | `/api/v1/attendance/monthly-summary` | Admin, Manager | Get monthly departmental attendance matrix & rates |
| `GET` | `/api/v1/attendance/yearly-summary` | All Authenticated | Get annual attendance summary for employee |

## 👥 Employee Management V2 Module Features

- **Enterprise Staff Directory & Onboarding**: Comprehensive employee lifecycle management with full name, contact details, blood group, gender, department, designation, manager hierarchy, and joining dates.
- **Advanced Dynamic Search & Filtering**: Multi-criteria search by keyword, department, designation, employment type (`FULL_TIME`, `PART_TIME`, `CONTRACT`, `INTERN`), status, and joining date range.
- **Status Transition Workflow**: Dynamic status updates (`ACTIVE`, `PROBATION`, `ON_LEAVE`, `SUSPENDED`, `TERMINATED`, `RESIGNED`) with mandatory reason logging and automated employee notifications.
- **Audit Logging & Event Interception**: Built-in `@Auditable(module = "Employee Directory V2")` AOP aspect interception logging state changes and diffs.
- **RBAC Security Enforcement**: Role-based endpoint guards ensuring proper authorization across Admin, Super Admin, HR, Manager, and Employee roles.

## 📡 Employee Management V2 REST APIs

| Method | Endpoint | Access Role | Description |
|---|---|---|---|
| `POST` | `/api/v2/employees` | Admin, HR | Onboard new staff member into corporate directory |
| `GET` | `/api/v2/employees` | All Authenticated | List employees with pagination & quick filter parameters |
| `POST` | `/api/v2/employees/search` | All Authenticated | Dynamic multi-criteria search and filter payload query |
| `GET` | `/api/v2/employees/{id}` | All Authenticated | Fetch full employee profile by document ID |
| `GET` | `/api/v2/employees/code/{employeeId}` | All Authenticated | Fetch employee profile by official employee code (e.g. `EMP-1001`) |
| `PUT` | `/api/v2/employees/{id}` | Admin, HR | Update employee details, compensation, & manager hierarchy |
| `PATCH` | `/api/v2/employees/{id}/status` | Admin, HR | Update employment status with mandatory decision reason |
| `DELETE` | `/api/v2/employees/{id}` | Super Admin | Delete employee document record from database |
| `GET` | `/api/v2/employees/department/{deptId}` | All Authenticated | Fetch direct list of staff in specific department |
| `GET` | `/api/v2/employees/manager/{managerId}` | All Authenticated | Fetch direct reporting team for manager |

## 🗄️ MongoDB Indexes (Employee V2 Collection)

```json
[
  { "key": { "employeeId": 1 }, "name": "idx_employee_code", "unique": true },
  { "key": { "officialEmail": 1 }, "name": "idx_emp_official_email", "unique": true },
  { "key": { "departmentId": 1, "status": 1 }, "name": "idx_emp_dept_status" },
  { "key": { "managerId": 1 }, "name": "idx_emp_manager_id" },
  { "key": { "employmentType": 1 }, "name": "idx_emp_type" }
]
```

## 🗄️ MongoDB Indexes (Attendance Collection)

```json
[
  { "key": { "userId": 1, "date": 1 }, "name": "idx_user_date", "unique": true },
  { "key": { "date": 1 }, "name": "idx_attendance_date" },
  { "key": { "department": 1, "date": 1 }, "name": "idx_dept_date" },
  { "key": { "status": 1 }, "name": "idx_status" }
]
```

## ⚙️ Environment Variables

Set the following environment variables prior to launching:

| Variable | Default Value | Description |
|---|---|---|
| `PORT` | `3000` / `8080` | HTTP Server Port |
| `MONGODB_URI` | `mongodb+srv://...` | MongoDB Atlas Connection String |
| `JWT_SECRET` | 256-bit Hex Key | Secret key for JWT signature verification |
| `JWT_EXPIRATION_MS` | `86400000` | Access Token Lifetime (24 hours) |
| `CLOUDINARY_CLOUD_NAME` | `tech-knife-cloud` | Cloudinary Cloud Name |
| `CLOUDINARY_API_KEY` | `123456789012345` | Cloudinary API Key |
| `CLOUDINARY_API_SECRET` | `secret` | Cloudinary API Secret |
| `SMTP_HOST` | `smtp.gmail.com` | SMTP Host |
| `SMTP_PORT` | `587` | SMTP Port |
| `SMTP_USERNAME` | `noreply@techknife.com` | Email Sender Credentials |
| `SMTP_PASSWORD` | `secret` | Email Sender Password |

## 🛠️ Build and Execution

```bash
# Build the project
mvn clean package

# Run unit & integration tests
mvn test

# Run locally
mvn spring-boot:run

# Docker build & run
docker build -t tech-knife-backend .
docker run -p 8080:8080 --env-file .env tech-knife-backend
```

