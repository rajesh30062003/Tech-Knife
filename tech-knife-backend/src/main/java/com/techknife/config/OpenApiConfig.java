package com.techknife.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 / Swagger configuration for Tech Knife Enterprise Platform API documentation.
 * Configures JWT Bearer authentication, server environments, and API feature tags.
 */
@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "BearerToken";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tech Knife Enterprise Platform API")
                        .description("Enterprise Management System Backend APIs")
                        .version("v1")
                        .contact(new Contact()
                                .name("Tech Knife")
                                .email("support@techknife.com")
                                .url("https://techknife.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://your-render-domain.onrender.com")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .description("Enter JWT Bearer Token to authorize REST endpoints")))
                .tags(List.of(
                        new Tag().name("Authentication").description("Auth, JWT tokens, OTP, & Password Management"),
                        new Tag().name("Employee").description("Staff Directory, Lifecycle, & Profile Management"),
                        new Tag().name("Department").description("Organizational Department Hierarchy"),
                        new Tag().name("Designation").description("Job Roles, Titles, & Pay Grade Levels"),
                        new Tag().name("Intern").description("Intern Onboarding & Program Management"),
                        new Tag().name("Attendance").description("Punch-in/out, Shifts, & Attendance Records"),
                        new Tag().name("Leave").description("Leave Allocation, Requests, & Approvals"),
                        new Tag().name("Project").description("Project Portfolio & Sprint Tracking"),
                        new Tag().name("Task").description("Task Assignment, Workflows, & Kanbans"),
                        new Tag().name("Payroll").description("Salary Structures, Payslips, & Compensation"),
                        new Tag().name("CRM").description("Leads, Sales Pipeline, & Deal Tracking"),
                        new Tag().name("Customer").description("Client Accounts & Contact Directory"),
                        new Tag().name("Recruitment").description("Job Openings, Applicants, & Interviews"),
                        new Tag().name("Reports").description("Analytics, Export Dashboards, & Metrics"),
                        new Tag().name("Notifications").description("Real-time Alerting & In-app System Notices"),
                        new Tag().name("System").description("Health, Audit Logs, & Platform Status")
                ));
    }
}
