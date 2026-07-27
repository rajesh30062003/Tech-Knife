package com.techknife.backend.constant;

public enum Permission {
    // User Permissions
    USER_READ("user:read", "Read user profile and roster information"),
    USER_WRITE("user:write", "Create and update user profiles"),
    USER_DELETE("user:delete", "Delete user accounts"),
    USER_MANAGE_ROLES("user:manage_roles", "Assign or revoke roles from users"),

    // Role & Permission Management
    ROLE_READ("role:read", "Read role configuration"),
    ROLE_WRITE("role:write", "Create and modify roles"),
    ROLE_DELETE("role:delete", "Remove custom roles"),
    PERMISSION_READ("permission:read", "View system permissions catalog"),

    // Audit Log Permissions
    AUDIT_READ("audit:read", "View system security audit logs"),

    // Project & Task Management
    PROJECT_READ("project:read", "View projects and sprint deliverables"),
    PROJECT_WRITE("project:write", "Create and edit project milestones"),

    // Payroll & Finance
    PAYROLL_READ("payroll:read", "View salary and compensation slips"),
    PAYROLL_WRITE("payroll:write", "Process monthly payroll disbursements"),

    // CRM & Sales
    CRM_READ("crm:read", "View client deals and CRM sales pipeline"),
    CRM_WRITE("crm:write", "Update deal stages and forecast value"),

    // Talent Acquisition & Recruitment
    RECRUITMENT_READ("recruitment:read", "View candidates and job requisitions"),
    RECRUITMENT_WRITE("recruitment:write", "Manage candidate interviews and offers"),

    // System Settings & Governance
    SYSTEM_SETTINGS("system:settings", "Modify global system properties and integrations");

    private final String code;
    private final String description;

    Permission(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
