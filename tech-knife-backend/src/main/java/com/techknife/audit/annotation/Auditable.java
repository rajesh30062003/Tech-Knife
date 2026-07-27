package com.techknife.audit.annotation;

import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to enable automatic AOP audit logging on service/controller method executions.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {

    /**
     * Action being performed (e.g., CREATE, UPDATE, DELETE, APPROVE).
     */
    AuditAction action();

    /**
     * Target application module (e.g., EMPLOYEE, PAYROLL, CRM).
     */
    AuditModule module();

    /**
     * Domain entity type associated with the operation.
     */
    String entityType() default "";

    /**
     * Optional custom description of the action.
     */
    String description() default "";
}
