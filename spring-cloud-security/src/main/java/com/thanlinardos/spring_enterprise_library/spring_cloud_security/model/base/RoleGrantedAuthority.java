package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base;

import jakarta.annotation.Nonnull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Objects;

/**
 * A custom {@link GrantedAuthority} implementation for role-based authorities (i.e. those prefixed with {@code ROLE_}).
 * <p>
 * Unlike {@link SimpleGrantedAuthority}, this class also carries
 * the {@code privilegeLevel} of the role, allowing privilege-aware access checks directly from the
 * {@link Authentication} stored in the security context.
 * <p>
 * Lower privilege level values indicate higher privilege (e.g. level 1 is more privileged than level 3).
 */
public record RoleGrantedAuthority(String authority, int privilegeLevel) implements GrantedAuthority {

    /**
     * Creates a new {@code RoleGrantedAuthority}.
     *
     * @param authority      the role name, expected to start with {@code ROLE_}
     * @param privilegeLevel the privilege level associated with this role;
     *                       lower values indicate higher privilege
     */
    public RoleGrantedAuthority {
        Objects.requireNonNull(authority, "authority must not be null");
    }

    /**
     * Returns the role name (e.g. {@code ROLE_ADMIN}).
     *
     * @return the authority string
     */
    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoleGrantedAuthority(String authority1, int privilegeLevel1))) {
            return false;
        }
        return authority.equals(authority1) && privilegeLevel == privilegeLevel1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority, privilegeLevel);
    }

    @Nonnull
    @Override
    public String toString() {
        return "RoleGrantedAuthority[authority=" + authority + ", privilegeLevel=" + privilegeLevel + "]";
    }
}

