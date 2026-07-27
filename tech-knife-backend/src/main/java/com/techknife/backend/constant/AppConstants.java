package com.techknife.backend.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Global application constants containing system configuration defaults, API routes, security roles, and status constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    // API Versioning Constants
    public static final String API_V1_PREFIX = "/api/v1";
    public static final String API_V2_PREFIX = "/api/v2";
    public static final String CURRENT_API_VERSION = "v2";

    // Default Pagination & Sorting Constants
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";
    public static final int MAX_PAGE_SIZE = 100;

    // Security Roles
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_HR = "HR";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";

    // Common Entity / Employment Status Values
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PROBATION = "PROBATION";
    public static final String STATUS_ON_LEAVE = "ON_LEAVE";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_TERMINATED = "TERMINATED";
    public static final String STATUS_RESIGNED = "RESIGNED";

    // System Utilities & Header Constants
    public static final String SYSTEM_USER = "SYSTEM";
    public static final String LOG_TRACE_ID = "traceId";
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    public static final String DEFAULT_TIMEZONE = "UTC";
    public static final String DATE_FORMAT_ISO = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
}
