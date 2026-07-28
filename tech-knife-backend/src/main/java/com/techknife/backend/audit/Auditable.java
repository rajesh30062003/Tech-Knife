package com.techknife.backend.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String module() default "";
    String action() default "";
    String resourceType() default "";
    boolean logParameters() default false;
}
