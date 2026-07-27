# TECH KNIFE ENTERPRISE PLATFORM
## FEATURE DEVELOPMENT CONTRACT

> **Version:** 1.0.0  
> **Status:** Mandatory Architectural Standard  
> **Target System:** Tech Knife Enterprise Management System  
> **Scope:** Backend (`tech-knife-backend`), Frontend (`tech-knife-frontend`), Infrastructure, and Engineering Governance.

---

### 1. Folder Structure (Vertical Slice Architecture)

To maximize modularity, maintainability, and domain isolation, all new modules must follow **Domain-Driven Vertical Slice Architecture**. Cross-cutting concerns remain shared, while domain business logic is encapsulated within feature bounded contexts.

#### Backend Directory Structure (`tech-knife-backend`)
```text
tech-knife-backend/
├── src/main/java/com/techknife/
│   ├── common/                       # Global shared infrastructure
│   │   ├── audit/                    # Cross-cutting audit aspects & listeners
│   │   ├── config/                   # Security, Mongo, Swagger, CORS configs
│   │   ├── constant/                 # App-wide global constants
│   │   ├── dto/                      # Global wrappers (ApiResponse, PagedResponse, ErrorDetails)
│   │   ├── exception/                # Global exception handlers & base exceptions
│   │   ├── security/                 # JWT filters, UserDetails, Auth entry points
│   │   ├── storage/                  # Object storage interfaces & implementations
│   │   └── util/                     # Pure helper functions & date formatters
│   │
│   └── <feature_domain>/             # Bounded context vertical slice (e.g., employee, attendance, payroll)
│       ├── controller/               # REST API Endpoints for feature
│       ├── dto/                      # Request / Response payloads for feature
│       ├── entity/                   # MongoDB Document models & Enums
│       ├── event/                    # Feature domain events & listeners
│       ├── mapper/                   # Entity <-> DTO mappers
│       ├── repository/               # MongoRepository & custom MongoTemplate search repositories
│       └── service/                  # Business interfaces and impl implementations
│           └── impl/                 # Service implementation classes
```

#### Frontend Directory Structure (`tech-knife-frontend`)
```text
tech-knife-frontend/
├── src/
│   ├── assets/                       # Static media, icons, branding assets
│   ├── components/                   # Shared UI primitives (Buttons, Modals, Inputs, DataTables)
│   ├── context/                      # Global React Contexts (AuthContext, ThemeContext)
│   ├── hooks/                        # Custom reusable React Hooks
│   ├── layouts/                      # Top-level shell layouts (DashboardLayout, AuthLayout)
│   ├── services/                     # Axios API Clients & endpoint configurations
│   ├── types/                        # Global TypeScript definitions
│   ├── utils/                        # Formatting, validation schemas, math helpers
│   └── features/                     # Feature vertical slices
│       └── <feature_name>/           # e.g., employee-management, attendance-tracker
│           ├── components/           # Feature-specific subcomponents
│           ├── hooks/                # Feature-specific React Query/data hooks
│           ├── pages/                # Route level entry components
│           ├── services/             # Feature API calls
│           └── types/                # Feature TypeScript interfaces & enums
```

---

### 2. Naming Conventions

* **Java Classes:** PascalCase (e.g., `EmployeeService`, `AttendanceCorrectionRequestDto`).
* **Java Variables & Methods:** camelCase (e.g., `findByOfficialEmail`, `primaryMobile`).
* **Java Constants:** UPPER_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`, `DEFAULT_PAGE_SIZE`).
* **TypeScript Interfaces & Types:** PascalCase prefixed with domain context where applicable (e.g., `EmployeeProfile`, `UpdateEmployeeRequest`).
* **TypeScript Variables & Functions:** camelCase (e.g., `fetchEmployeeById`, `formatCurrency`).
* **React Components & Files:** PascalCase (e.g., `EmployeeDirectoryTable.tsx`, `OnboardingModal.tsx`).
* **CSS & Tailwind Classes:** Kebab-case or standard utility compositions (`bg-slate-50`, `text-indigo-600`).
* **Database Collections:** Plural lowercase snake_case or kebab-case (e.g., `employees`, `attendance_logs`).

---

### 3. Java Package Conventions

* Package names must be **all lowercase** with words concatenated without underscores or hyphens.
* Base package prefix: `com.techknife.<feature_domain>`
* Package hierarchy for feature slices:
  - Entities & Enums: `com.techknife.<feature_domain>.entity`
  - Repositories: `com.techknife.<feature_domain>.repository`
  - Services & Interfaces: `com.techknife.<feature_domain>.service`
  - Service Implementations: `com.techknife.<feature_domain>.service.impl` (or `com.techknife.<feature_domain>.serviceImpl`)
  - Controllers: `com.techknife.<feature_domain>.controller`
  - DTOs: `com.techknife.<feature_domain>.dto`
  - Mappers: `com.techknife.<feature_domain>.mapper`

---

### 4. React Component Conventions

1. **Functional Components Only:** Class components are strictly prohibited.
2. **Explicit Props Interface:** Every component accepting props must declare a companion TypeScript interface named `<ComponentName>Props`.
3. **Single Responsibility:** A single component file should not exceed 250 lines of code. Sub-elements must be decomposed into `components/` subdirectories within the feature slice.
4. **Export Style:** Named exports preferred for subcomponents, default exports allowed for page-level routes.
5. **Icon Usage:** All visual icons must be imported exclusively from `lucide-react`. Custom SVG strings are prohibited.
6. **Animation:** Page and state transitions must utilize `motion` (imported from `motion/react`).

---

### 5. REST API Conventions

1. **Resource URI Pluralization:** Use plural nouns for resource endpoints (e.g., `/api/v1/employees`, `/api/v1/attendances`).
2. **HTTP Method Semantics:**
   - `GET`: Read resource(s). Must be idempotent and side-effect free.
   - `POST`: Create a new resource or initiate a workflow.
   - `PUT`: Full update / replacement of an existing resource.
   - `PATCH`: Partial update of specific fields in a resource.
   - `DELETE`: Remove a resource.
3. **API Versioning:** All public endpoints must include API versioning in the path: `/api/v1/<resource>`.
4. **Hierarchical Sub-resources:** Use clean URI relationships (e.g., `/api/v1/employees/{employeeId}/attendance-logs`).

---

### 6. MongoDB Collection Naming

* **Collection Names:** Lowercase plural form (e.g., `employees`, `attendance_records`, `audit_logs`).
* **Primary Key:** `@Id private String id` maps to MongoDB default `_id`.
* **Indexing Strategy:**
  - Unique fields must explicitly declare `@Indexed(unique = true)`.
  - Compound query filters must declare `@CompoundIndex`.
  - Foreign reference fields (`departmentId`, `managerId`) must be indexed.

---

### 7. DTO Conventions

1. **Isolation:** Domain Entities must **NEVER** be returned directly in REST API responses or accepted in request parameters.
2. **Request DTOs:**
   - Creation DTOs: `Create<Entity>Request.java`
   - Update DTOs: `Update<Entity>Request.java`
   - Search Filter DTOs: `<Entity>SearchFilter.java`
3. **Response DTOs:**
   - Single item detailed payload: `<Entity>Response.java`
   - Lightweight list item payload: `<Entity>DTO.java` or `<Entity>SummaryResponse.java`
4. **Lombok Usage:** All DTOs must use `@Data`, `@Builder`, `@NoArgsConstructor`, and `@AllArgsConstructor`.

---

### 8. Validation Rules

1. **Request Payload Validation:** Controllers must annotate request bodies with `@Valid`.
2. **Jakarta Validation Annotations:**
   - Mandatory Strings: `@NotBlank(message = "...")`
   - Mandatory Objects/Enums: `@NotNull(message = "...")`
   - Email format: `@Email(message = "...")`
   - Numbers & Money: `@Positive`, `@PositiveOrZero`, `@Min`, `@Max`
   - Collections: `@NotEmpty` or `@Size(min = ...)`
3. **Fail-Fast:** Validation failures must yield `400 Bad Request` with field-level error breakdowns.

---

### 9. Error Handling Format

All exceptions intercepted by `@RestControllerAdvice` (`GlobalExceptionHandler`) must return a standard `ErrorDetails` structure:

```json
{
  "timestamp": "2026-07-23T08:26:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for request",
  "path": "/api/v1/employees",
  "fieldErrors": {
    "officialEmail": "Invalid official email format",
    "primaryMobile": "Primary mobile number must be between 10 and 15 digits"
  }
}
```

---

### 10. API Response Format

All successful REST API operations must be wrapped in a standardized `ApiResponse<T>` container:

```json
{
  "success": true,
  "message": "Employee record retrieved successfully",
  "data": { ... },
  "timestamp": "2026-07-23T08:26:00Z"
}
```

For paginated collections, `data` contains `PagedResponse<T>`:
```json
{
  "success": true,
  "message": "Employees list retrieved successfully",
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 10,
    "totalElements": 142,
    "totalPages": 15,
    "last": false
  },
  "timestamp": "2026-07-23T08:26:00Z"
}
```

---

### 11. Logging Standards

1. **Framework:** Use `org.slf4j.Logger` via Lombok `@Slf4j`.
2. **Log Levels:**
   - `TRACE`: High-frequency internal loop execution details.
   - `DEBUG`: Technical context useful during active development and troubleshooting.
   - `INFO`: Business milestone events (e.g., `Employee created: EMP-1092 by admin@techknife.com`).
   - `WARN`: Recoverable unexpected events, degraded fallback executions.
   - `ERROR`: System failures, unhandled exceptions, database connectivity errors (must include stack trace).
3. **No Sensitive Data:** Never log credentials, JWT tokens, salaries, or PII (Personally Identifiable Information).

---

### 12. Audit Logging Rules

1. **Entity Auditing:** All persistent domain entities extending `BaseEntity` automatically capture:
   - `createdAt` (Instant)
   - `updatedAt` (Instant)
   - `createdBy` (String - User ID / Email)
   - `updatedBy` (String - User ID / Email)
2. **Business Action Audit Logs:** Critical administrative mutations (Create, Delete, Role Change, Salary Update) must publish an `AuditLog` record containing:
   - `principal`: The identity executing the action.
   - `action`: E.g., `EMPLOYEE_STATUS_CHANGE`.
   - `resource`: E.g., `employees/65d8f1e9c2b1a8001a123456`.
   - `ipAddress`: Client remote address.
   - `payloadDelta`: Previous state vs. new state diff.

---

### 13. Security Rules

1. **Stateless JWT Authentication:** Authentication tokens passed via `Authorization: Bearer <token>` header.
2. **Role-Based Access Control (RBAC):** All controller endpoints must specify `@PreAuthorize`:
   - E.g., `@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")`
3. **Password & Secret Handling:**
   - Raw passwords must be encoded with `BCryptPasswordEncoder` (strength 12+).
   - Secret API keys must **NEVER** be committed to source repositories or sent to the browser client.
   - API keys must reside in environment variables and be declared in `.env.example`.

---

### 14. Testing Standards

1. **Backend Unit Tests:** Every Service class must have a JUnit 5 + Mockito unit test covering:
   - Happy path execution.
   - Resource not found scenarios (`ResourceNotFoundException`).
   - Validation & duplicate constraint failures (`BadRequestException`).
2. **Integration Tests:** Repository queries and MongoTemplate searches must be verified using `@DataMongoTest` or Testcontainers.
3. **Frontend Component Tests:** React components must be tested using React Testing Library to verify component mounting and user interaction handlers.

---

### 15. Documentation Standards

1. **OpenAPI / Swagger:** All Controller classes must be annotated with `@Tag`, and all endpoints must declare `@Operation(summary = "...")`.
2. **Code Comments:** Javadoc comments required for non-obvious complex business logic, calculation engines, and custom repository implementations.
3. **Frontend Component Stories / Docs:** Complex UI widgets must include clear inline TypeScript documentation detailing required prop types and side effects.

---

### 16. Git Branch Naming

All branches must adhere to the standard naming syntax: `<type>/<feature-or-ticket-name>`

* `feature/employee-onboarding`
* `feature/attendance-punch-clock`
* `bugfix/jwt-token-expiration`
* `hotfix/cors-allowed-origins`
* `refactor/mongo-template-employee-search`
* `chore/dependency-upgrade-spring-boot-3`

---

### 17. Commit Message Conventions

Follow the **Conventional Commits** specification:

```text
<type>(<scope>): <short summary>

[optional body]

[optional footer]
```

#### Allowed Types:
* `feat`: A new feature implementation for the user.
* `fix`: A bug fix.
* `docs`: Documentation changes only.
* `style`: Changes that do not affect code logic (formatting, white-space).
* `refactor`: Code change that neither fixes a bug nor adds a feature.
* `test`: Adding missing tests or correcting existing tests.
* `chore`: Build tasks, package manager updates, configuration changes.

#### Example:
```text
feat(employee): add MongoDB custom search repository for multi-criteria filtering

Implemented EmployeeSearchRepositoryImpl using MongoTemplate to support
dynamic pagination, text search regex, department filtering, and skill matching.
```

---

### 18. Pull Request Template

Every Pull Request submitted to the Tech Knife repositories must fill out the following template:

```markdown
## Description
Brief summary of the changes introduced by this PR.

## Type of Change
- [ ] New feature (non-breaking change adding functionality)
- [ ] Bug fix (non-breaking change fixing an issue)
- [ ] Refactoring / Architecture optimization
- [ ] Documentation update

## Related Issues
Closes #<issue_number>

## Checklist
- [ ] Code complies with the Feature Development Contract standards.
- [ ] Unit tests added and passing.
- [ ] API endpoints verified with Swagger / OpenAPI.
- [ ] No hardcoded secrets or environment credentials added.
- [ ] Tested locally on full-stack dev server.
```

---

### 19. Code Review Checklist

Reviewers must verify that the proposed pull request satisfies:

* [ ] **Architecture:** Respects Vertical Slice domain boundary; no cross-domain tight coupling.
* [ ] **Security:** `@PreAuthorize` present on every new REST endpoint; inputs sanitized; no raw passwords logged.
* [ ] **Performance:** Indexes declared for all MongoDB query filters; pagination enforced on all list endpoints.
* [ ] **Code Hygiene:** No dead code, debug `console.log` statements, or commented-out code blocks left behind.
* [ ] **Error Handling:** Proper custom exceptions thrown instead of raw `RuntimeException`.

---

### 20. Performance Checklist

1. **Database Indexing:** Ensure all fields present in `.orOperator()` or `.andOperator()` filters have corresponding Mongo indexes.
2. **Pagination Enforcement:** Never execute unbounded `findAll()` queries on production entities. Default page size capped at 50 records max.
3. **Projection:** Retrieve only required fields when performing high-volume summary or directory listings.
4. **Frontend Asset Optimization:** Lazy-load feature routes using React `React.lazy` and `Suspense`.
5. **Debounced Search:** User typing inputs for live search filters must be debounced by at least 300ms before firing API calls.

---

### 21. Accessibility Checklist

1. **Semantic HTML:** Use proper tags (`<main>`, `<nav>`, `<header>`, `<footer>`, `<article>`, `<section>`, `<button>`).
2. **Keyboard Navigation:** All interactive elements must be focusable and operable via `Tab`, `Enter`, and `Space`.
3. **Contrast Ratio:** Text-to-background contrast must achieve WCAG 2.1 AA standard (minimum 4.5:1 ratio for standard text).
4. **Touch Targets:** Interactive controls on responsive viewports must have a touch target area of at least 44px x 44px.
5. **Form Inputs:** Every input must be associated with an explicit `<label>` or `aria-label`.

---

### 22. Deployment Checklist

1. **Pre-build Linting:** `npm run lint` (or `./mvnw spotless:check`) completes with zero errors.
2. **Compilation:** `npm run build` / `./mvnw clean package` compiles cleanly without warnings or type errors.
3. **Environment Sync:** All new backend/frontend environment variables are documented in `.env.example`.
4. **Database Migrations / Index Generation:** Verify MongoDB auto-index creation or index creation scripts are applied to production clusters.
5. **Port Binding:** Ensure the application container binds strictly to port `3000` on host `0.0.0.0` for ingress proxy compatibility.
6. **Health Check Verification:** `/api/health` endpoint returns HTTP 200 `{"status": "ok"}` after deployment startup.

---

*End of Feature Development Contract — Tech Knife Enterprise Management System*
