package com.smartinstitute.erp.security.userdetails;

import com.smartinstitute.erp.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    public Long getUserId() {
        return user.getId();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getRole() {
        return user.getRole().getName().name();
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Username = Email
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Can later use email verification here.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * ACTIVE users can login.
     */
    @Override
    public boolean isEnabled() {

        return user.getStatus().name().equals("ACTIVE");
    }

}