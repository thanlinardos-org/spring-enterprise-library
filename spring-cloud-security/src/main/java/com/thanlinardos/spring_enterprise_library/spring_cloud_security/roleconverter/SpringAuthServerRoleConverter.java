package com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.RoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.RoleGrantedAuthority;
import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

/**
 * A converter that extracts roles from a JWT token's "roles" claim
 * and converts them into Spring Security {@link GrantedAuthority} objects.
 * <p>
 * When constructed with a {@link RoleService}, each role is stored as a {@link RoleGrantedAuthority}
 * carrying the role's {@code privilegeLevel}, allowing privilege-aware checks directly from the
 * security context. When no {@code RoleService} is provided, an unknown privilege level
 * ({@link Integer#MAX_VALUE}) is used as a safe fallback.
 *
 * @param <T> the type of Role
 */
public class SpringAuthServerRoleConverter<T extends Role> implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final RoleService<T> roleService;

    /**
     * Constructs a converter with a {@link RoleService} for privilege level lookup.
     * Each role found in the JWT "roles" claim will be looked up via the service;
     * if not found, {@link Integer#MAX_VALUE} is used as a safe fallback.
     *
     * @param roleService the role service used to resolve privilege levels
     */
    public SpringAuthServerRoleConverter(RoleService<T> roleService) {
        this.roleService = roleService;
    }

    /**
     * Converts the JWT token into a collection of {@link RoleGrantedAuthority} objects
     * based on the roles found in the "roles" claim.
     *
     * @param jwt the JWT token
     * @return a collection of {@link RoleGrantedAuthority} objects representing the roles,
     *         or an empty list if no roles claim is present
     */
    @Override
    public Collection<GrantedAuthority> convert(@Nonnull Jwt jwt) { //NOSONAR (S2638)
        if (jwt.getClaims().get("roles") instanceof ArrayList<?> scopes && !scopes.isEmpty() && scopes.getFirst() instanceof String) {
            return getGrantedAuthoritiesFromScopes(scopes);
        } else {
            return new ArrayList<>();
        }
    }

    private List<GrantedAuthority> getGrantedAuthoritiesFromScopes(ArrayList<?> scopes) {
        return scopes.stream()
                .map(role -> ROLE_PREFIX + role)
                .map(roleName -> new RoleGrantedAuthority(roleName, resolvePrivilegeLevel(roleName)))
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    private int resolvePrivilegeLevel(String roleName) {
        return Optional.ofNullable(roleService.findRole(roleName))
                .map(Role::getPrivilegeLvl)
                .orElse(Integer.MAX_VALUE);
    }
}
