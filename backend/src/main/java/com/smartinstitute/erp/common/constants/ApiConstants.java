package com.smartinstitute.erp.common.constants;

/**
 * API endpoint constants.
 */
public final class ApiConstants {

    private ApiConstants() {
    }

    public static final String API_BASE = "/api/v1";

    public static final String HEALTH = API_BASE + "/health";
    public static final String AUTH = API_BASE + "/auth";
    public static final String USERS = API_BASE + "/users";
    public static final String ROLES = API_BASE + "/roles";
    public static final String INSTITUTES = API_BASE + "/institutes";
}