import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from './context/ThemeContext';
import { AuthProvider } from './context/AuthContext';

// Layouts
import { PublicLayout } from './components/layout/PublicLayout';
import { DashboardLayout } from './components/layout/DashboardLayout';
import { ProtectedLayout } from './components/common/ProtectedLayout';

// Public Pages
import { PublicHome } from './pages/public/PublicHome';
import { AboutPage } from './pages/public/About';
import { ServicesPage } from './pages/public/Services';
import { SolutionsPage } from './pages/public/Solutions';
import { ProductsPage } from './pages/public/Products';
import { IndustriesPage } from './pages/public/Industries';
import { PortfolioPage } from './pages/public/Portfolio';
import { CaseStudiesPage } from './pages/public/CaseStudies';
import { TestimonialsPage } from './pages/public/Testimonials';
import { InternshipPage } from './pages/public/Internship';
import { CareersPage } from './pages/public/Careers';
import { BlogPage } from './pages/public/Blog';
import { PricingPage } from './pages/public/Pricing';
import { FaqPage } from './pages/public/Faq';
import { LegalPage } from './pages/public/Legal';
import { ContactPage } from './pages/public/Contact';

// Auth Pages
import { Login } from './pages/auth/Login';
import { Register } from './pages/auth/Register';
import { ForgotPassword } from './pages/auth/ForgotPassword';
import { ResetPassword } from './pages/auth/ResetPassword';
import { VerifyEmail } from './pages/auth/VerifyEmail';
import { VerifyOtp } from './pages/auth/VerifyOtp';
import { ChangePassword } from './pages/auth/ChangePassword';

// Enterprise Pages
import { UnifiedDashboard } from './pages/dashboard/UnifiedDashboard';
import { AdminDashboard } from './pages/admin/AdminDashboard';
import { ManagerDashboard } from './pages/manager/ManagerDashboard';
import { EmployeePortal } from './pages/employee/EmployeePortal';
import { InternPortal } from './pages/intern/InternPortal';
import { CustomerPortal } from './pages/customer/CustomerPortal';
import { EmployeesPage } from './pages/employees/EmployeesPage';
import { InternsPage } from './pages/interns/InternsPage';
import { CustomersPage } from './pages/customers/CustomersPage';
import { ProjectsPage } from './pages/projects/ProjectsPage';
import { PayrollPage } from './pages/payroll/PayrollPage';
import { AttendancePage } from './pages/attendance/AttendancePage';
import { LeavePage } from './pages/leave/LeavePage';
import { CrmPage } from './pages/crm/CrmPage';
import { RecruitmentPage } from './pages/recruitment/RecruitmentPage';
import { NotificationsPage } from './pages/notifications/NotificationsPage';
import { ProfilePage } from './pages/profile/ProfilePage';
import { SupportPage } from './pages/support/SupportPage';
import { CalendarPage } from './pages/calendar/CalendarPage';
import { ReportsPage } from './pages/reports/ReportsPage';
import { DepartmentsPage } from './pages/organization/DepartmentsPage';
import { DesignationsPage } from './pages/organization/DesignationsPage';
import { OrgStructurePage } from './pages/organization/OrgStructurePage';

// Enterprise Core Services Pages
import { WorkflowsPage } from './pages/admin/WorkflowsPage';
import { StorageManagerPage } from './pages/admin/StorageManagerPage';
import { AuditLogsPage } from './pages/admin/AuditLogsPage';
import { RolesPermissionsPage } from './pages/admin/RolesPermissionsPage';
import { SystemSettingsPage } from './pages/settings/SystemSettingsPage';
import { CmsAdminPage } from './pages/admin/CmsAdminPage';
import { AssetsInventoryPage } from './pages/admin/AssetsInventoryPage';

// Error Pages
import { NotFound404 } from './pages/errors/NotFound404';
import { Forbidden403 } from './pages/errors/Forbidden403';
import { Unauthorized401 } from './pages/errors/Unauthorized401';
import { ServerError500 } from './pages/errors/ServerError500';

export default function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            
            {/* Public Website Routes */}
            <Route element={<PublicLayout />}>
              <Route path="/" element={<PublicHome />} />
              <Route path="/about" element={<AboutPage />} />
              <Route path="/services" element={<ServicesPage />} />
              <Route path="/solutions" element={<SolutionsPage />} />
              <Route path="/products" element={<ProductsPage />} />
              <Route path="/industries" element={<IndustriesPage />} />
              <Route path="/portfolio" element={<PortfolioPage />} />
              <Route path="/projects-showcase" element={<PortfolioPage />} />
              <Route path="/case-studies" element={<CaseStudiesPage />} />
              <Route path="/testimonials" element={<TestimonialsPage />} />
              <Route path="/clients" element={<TestimonialsPage />} />
              <Route path="/internship" element={<InternshipPage />} />
              <Route path="/careers" element={<CareersPage />} />
              <Route path="/blog" element={<BlogPage />} />
              <Route path="/news" element={<BlogPage />} />
              <Route path="/events" element={<BlogPage />} />
              <Route path="/pricing" element={<PricingPage />} />
              <Route path="/faq" element={<FaqPage />} />
              <Route path="/privacy-policy" element={<LegalPage />} />
              <Route path="/terms" element={<LegalPage />} />
              <Route path="/refund-policy" element={<LegalPage />} />
              <Route path="/support-public" element={<ContactPage />} />
              <Route path="/contact" element={<ContactPage />} />
            </Route>

            {/* Standalone Auth Pages */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />
            <Route path="/verify-email" element={<VerifyEmail />} />
            <Route path="/verify-otp" element={<VerifyOtp />} />

            {/* Error Pages */}
            <Route path="/401" element={<Unauthorized401 />} />
            <Route path="/403" element={<Forbidden403 />} />
            <Route path="/500" element={<ServerError500 />} />

            {/* Authenticated & Role Protected Application Shell */}
            <Route element={<DashboardLayout />}>
              
              {/* Common Authenticated Routes */}
              <Route element={<ProtectedLayout />}>
                <Route path="/dashboard" element={<UnifiedDashboard />} />
                <Route path="/departments" element={<DepartmentsPage />} />
                <Route path="/designations" element={<DesignationsPage />} />
                <Route path="/org-structure" element={<OrgStructurePage />} />
                <Route path="/projects" element={<ProjectsPage />} />
                <Route path="/payroll" element={<PayrollPage />} />
                <Route path="/attendance" element={<AttendancePage />} />
                <Route path="/leave" element={<LeavePage />} />
                <Route path="/workflows" element={<WorkflowsPage />} />
                <Route path="/storage" element={<StorageManagerPage />} />
                <Route path="/assets" element={<AssetsInventoryPage />} />
                <Route path="/cms" element={<CmsAdminPage />} />
                <Route path="/notifications" element={<NotificationsPage />} />
                <Route path="/profile" element={<ProfilePage />} />
                <Route path="/change-password" element={<ChangePassword />} />
                <Route path="/support" element={<SupportPage />} />
                <Route path="/calendar" element={<CalendarPage />} />
              </Route>

              {/* Admin & Executive Guarded Routes */}
              <Route element={<ProtectedLayout allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO', 'ROLE_CTO', 'ROLE_CMO', 'ROLE_MD', 'ROLE_DIRECTOR']} />}>
                <Route path="/admin" element={<AdminDashboard />} />
                <Route path="/settings" element={<SystemSettingsPage />} />
                <Route path="/audit-logs" element={<AuditLogsPage />} />
                <Route path="/roles-permissions" element={<RolesPermissionsPage />} />
              </Route>

              {/* Manager Guarded Routes */}
              <Route element={<ProtectedLayout allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO', 'ROLE_CTO', 'ROLE_CMO', 'ROLE_MD', 'ROLE_DIRECTOR', 'ROLE_MANAGER']} />}>
                <Route path="/manager" element={<ManagerDashboard />} />
                <Route path="/crm" element={<CrmPage />} />
                <Route path="/recruitment" element={<RecruitmentPage />} />
                <Route path="/reports" element={<ReportsPage />} />
              </Route>

              {/* Employee Desk & Directory */}
              <Route element={<ProtectedLayout allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO', 'ROLE_CTO', 'ROLE_CMO', 'ROLE_MD', 'ROLE_DIRECTOR', 'ROLE_MANAGER', 'ROLE_EMPLOYEE']} />}>
                <Route path="/employee" element={<EmployeePortal />} />
                <Route path="/employee-dashboard" element={<EmployeePortal />} />
                <Route path="/employees" element={<EmployeesPage />} />
              </Route>

              {/* Intern Desk & Cohort */}
              <Route element={<ProtectedLayout allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO', 'ROLE_CTO', 'ROLE_CMO', 'ROLE_MD', 'ROLE_DIRECTOR', 'ROLE_MANAGER', 'ROLE_INTERN']} />}>
                <Route path="/intern" element={<InternPortal />} />
                <Route path="/interns" element={<InternsPage />} />
              </Route>

              {/* Customer Desk & Directory */}
              <Route element={<ProtectedLayout allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO', 'ROLE_CTO', 'ROLE_CMO', 'ROLE_MD', 'ROLE_DIRECTOR', 'ROLE_CUSTOMER']} />}>
                <Route path="/customer" element={<CustomerPortal />} />
                <Route path="/customers" element={<CustomersPage />} />
              </Route>

            </Route>

            {/* Catch-all 404 */}
            <Route path="*" element={<NotFound404 />} />

          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  );
}
