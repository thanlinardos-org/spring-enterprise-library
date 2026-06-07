package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;

import java.util.Optional;

/**
 * Utility class for processing spring security authentication related information.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtils {

    /**
     * Extracts the principal name from the given Authentication object.
     * It first attempts to retrieve the 'sub' (subject) claim from the JWT.
     * If 'sub' is not available, it falls back to the 'email' claim,
     * and if 'email' is also not available, it uses the 'client_id' claim.
     *
     * @param authentication the Authentication object containing the JWT
     * @return the principal name, or null if none of the claims are present
     */
    public static String getPrincipalNameFromAuthentication(Authentication authentication) {
        Jwt principal = (Jwt) authentication.getPrincipal();
        return Optional.ofNullable(principal)
                .map(JwtClaimAccessor::getSubject)
                .or(() -> Optional.ofNullable(principal != null ? principal.getClaimAsString("email") : null))
                .or(() -> Optional.ofNullable(principal != null ? principal.getClaimAsString("client_id") : null))
                .orElse(null);
    }
}
