package com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

/**
 * A converter that extracts roles from a JWT token's "roles" claim
 * and converts them into Spring Security GrantedAuthority objects.
 */
public class SpringAuthServerRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * Default constructor.
     */
    public SpringAuthServerRoleConverter() {
        // Default constructor
    }

    /**
     * Converts the JWT token into a collection of GrantedAuthority objects
     * based on the roles found in the "roles" claim.
     *
     * @param jwt the JWT token.
     * @return a collection of GrantedAuthority objects representing the roles.
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        if (jwt.getClaims().get("roles") instanceof ArrayList<?> scopes && !scopes.isEmpty() && scopes.getFirst() instanceof String) {
            return scopes.stream()
                    .map(role -> ROLE_PREFIX + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
