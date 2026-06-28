package com.smartinstitute.erp.security.jwt;

/**
 * Standard JWT claim names used across the application.
 *
 * Centralizing claim keys prevents hard-coded strings
 * throughout the project.
 */
public final class JwtClaims {

    private JwtClaims() {
        // Prevent instantiation
    }

    /**
     * User ID
     */
    public static final String USER_ID = "userId";

    /**
     * User email (username)
     */
    public static final String EMAIL = "email";

    /**
     * User role
     */
    public static final String ROLE = "role";

    /**
     * Token type (ACCESS / REFRESH)
     */
    public static final String TOKEN_TYPE = "tokenType";

    /**
     * User status
     */
    public static final String STATUS = "status";

    /**
     * Institute ID
     */
    public static final String INSTITUTE_ID = "instituteId";
}