package com.smartinstitute.erp.security.util;

import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.userdetails.CustomUserDetails;
import com.smartinstitute.erp.user.entity.User;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /**
     * Returns logged-in CustomUserDetails.
     */
    public CustomUserDetails getCurrentUserDetails() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {

            throw new AuthenticationCredentialsNotFoundException(
                    "User is not authenticated."
            );
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

    /**
     * Returns logged-in User entity.
     */
    public User getCurrentUser() {

        return getCurrentUserDetails().getUser();
    }

    public Long getCurrentUserId() {

        return getCurrentUser().getId();
    }

    /**
     * Returns logged-in user's institute.
     */
    public Institute getCurrentInstitute() {

        Institute institute = getCurrentUser().getInstitute();

        if (institute == null) {

            throw new IllegalStateException(
                    "Current user is not associated with any institute."
            );
        }

        return institute;
    }

    /**
     * Returns logged-in user's institute id.
     */
    public Long getCurrentInstituteId() {

        return getCurrentInstitute().getId();
    }

    public String getCurrentUserEmail() {

        return getCurrentUser().getEmail();
    }

}