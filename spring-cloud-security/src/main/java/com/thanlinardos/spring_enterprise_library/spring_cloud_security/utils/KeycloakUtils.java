package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.RoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.thanlinardos.spring_enterprise_library.objects.utils.FunctionUtils.stream;
import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

/**
 * Utility class for processing Keycloak-related information.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeycloakUtils {

    /**
     * Extracts GrantedAuthority objects from the given JWT token
     * based on the roles found in the "realm_access" claim.
     *
     * @param jwt         the JWT token.
     * @param roleService the service to fetch roles and authorities.
     * @param <T>         the type of Role.
     * @return a collection of GrantedAuthority objects representing the roles, or an empty list if none found.
     */
    public static <T extends Role> Collection<GrantedAuthority> getGrantedAuthoritiesFromJwt(Jwt jwt, RoleService<T> roleService) {
        if (jwt.getClaim("realm_access") instanceof Map<?, ?> realmAccess && !realmAccess.isEmpty()) {
            return getSimpleGrantedAuthoritiesFromRealmAccess(realmAccess, roleService);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Extracts GrantedAuthority objects from the given realm access map
     * based on the roles found in the "roles" key.
     *
     * @param realmAccess the map containing realm access information.
     * @param roleService the service to fetch roles and authorities.
     * @param <T>         the type of Role.
     * @return a collection of GrantedAuthority objects representing the roles, or an empty list if none found.
     */
    public static <T extends Role> Collection<GrantedAuthority> getSimpleGrantedAuthoritiesFromRealmAccess(Map<?, ?> realmAccess, RoleService<T> roleService) {
        return parseRoleNamesStream(realmAccess, roleService).stream()
                .flatMap(stream(T::getGrantedAuthorities))
                .distinct()
                .toList();
    }

    private static <T extends Role> Collection<T> parseRoleNamesStream(Map<?, ?> realmAccess, RoleService<T> roleService) {
        return switch (realmAccess.get("roles")) {
            case List<?> roleList -> roleService.findRolesWithoutValidation(roleList.stream()
                    .map(roleName -> ROLE_PREFIX + roleName)
                    .toList());
            case null, default -> Collections.emptyList();
        };
    }
}
