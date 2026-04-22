package com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.RoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.KeycloakUtils.getGrantedAuthoritiesFromJwt;

/**
 * A converter that extracts roles from a Keycloak JWT token
 * and converts them into Spring Security GrantedAuthority objects.
 *
 * @param <T> the type of Role
 */
public class KeycloakRoleConverter<T extends Role> implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final RoleService<T> roleService;

    /**
     * Constructor to initialize the KeycloakRoleConverter with a RoleService.
     *
     * @param roleService the RoleService to manage roles.
     */
    public KeycloakRoleConverter(RoleService<T> roleService) {
        this.roleService = roleService;
    }

    /**
     * Converts the JWT token into a collection of GrantedAuthority objects
     * based on the roles found in the token.
     *
     * @param jwt the JWT token.
     * @return a collection of GrantedAuthority objects representing the roles.
     */
    @Override
    public Collection<GrantedAuthority> convert(@Nonnull Jwt jwt) { //NOSONAR (S2648)
        return getGrantedAuthoritiesFromJwt(jwt, roleService);
    }
}
