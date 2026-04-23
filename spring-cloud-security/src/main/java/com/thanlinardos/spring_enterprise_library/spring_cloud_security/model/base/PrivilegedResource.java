package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Interface representing a resource that requires a certain privilege level for access.
 */
public interface PrivilegedResource {

    /**
     * The privilege level required to access the resource.
     * The lower the level, the higher the privilege.
     *
     * @return Integer value of the privilege level required to access the resource.
     */
    int getPrivilegeLevel();

    /**
     * The principal name that owns the resource.
     *
     * @return String value of the principal name.
     */
    String getPrincipalName();

    /**
     * Checks if the principal has the same privilege level as the resource owner.
     *
     * @param principal the JWT principal to check against.
     * @return true if the principal has the same privilege level, false otherwise.
     */
    @JsonIgnore
    @SuppressWarnings("unchecked")
    default Boolean samePrivilegeLevelCheck(Jwt principal) {
        Map<String, Object> claims = principal.getClaims();
        Map<String, Object> resourceAccess = Optional.ofNullable(claims.get("resource_access"))
                .map(Map.class::cast)
                .orElse(Collections.emptyMap());
        return matchesPrincipalNameToEmail(resourceAccess, claims) || matchesPrincipalNameToClientId(principal, claims, resourceAccess);
    }

    private Boolean matchesPrincipalNameToClientId(Jwt principal, Map<String, Object> claims, Map<String, Object> resourceAccess) {
        return Optional.ofNullable(claims.get("client_id"))
                .map(Object::toString)
                .filter(resourceAccess::containsKey)
                .map(clientID -> clientID.equals(this.getPrincipalName()))
                .orElse(principal.getSubject().equals(this.getPrincipalName()));
    }

    private boolean matchesPrincipalNameToEmail(Map<String, Object> resourceAccess, Map<String, Object> claims) {
        return resourceAccess.get("account") != null && claims.get("email") != null && claims.get("email").equals(this.getPrincipalName());
    }

    /**
     * The maximum privilege level that can access the resource.
     *
     * @return Integer value of the maximum privilege level that can access the resource.
     */
    @JsonIgnore
    default Integer getMaxPrivilegeLevel() {
        return Integer.MAX_VALUE;
    }

    /**
     * Calculates the privilege level from a list of roles.
     * The privilege level is determined by the minimum privilege level among the roles.
     *
     * @param roles List of roles to calculate the privilege level from.
     * @param <T>   Type of the role, must extend Role.
     * @return Integer value of the calculated privilege level,
     * or Integer.MAX_VALUE if the list is empty, representing the lowest privilege level.
     */
    static <T extends Role> int calcPrivilegeLvlFromRoles(Collection<T> roles) {
        return roles.stream()
                .map(Role::getPrivilegeLvl)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }

    static int getPrivilegeLevelFromGrantedAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .filter(RoleGrantedAuthority.class::isInstance)
                .map(RoleGrantedAuthority.class::cast)
                .map(RoleGrantedAuthority::privilegeLevel)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }
}
