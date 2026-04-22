package com.thanlinardos.resource_server.misc.utils;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.RoleGrantedAuthority;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

public class RoleUtils {

    private RoleUtils() {
    }

    public static Set<String> getRoleNamesFromRoleRepresentations(List<RoleRepresentation> roleRepresentations) {
        return roleRepresentations.stream()
                .map(RoleRepresentation::getName)
                .map(roleName -> ROLE_PREFIX + roleName)
                .collect(Collectors.toSet());
    }

    public static int getPrivilegeLevelFromGrantedAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .filter(RoleGrantedAuthority.class::isInstance)
                .map(RoleGrantedAuthority.class::cast)
                .map(RoleGrantedAuthority::privilegeLevel)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }

    public static int getPrivilegeLevelFromContextForRoles(Set<String> roleNames) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities().stream()
                .filter(RoleGrantedAuthority.class::isInstance)
                .map(RoleGrantedAuthority.class::cast)
                .filter(roleGrantedAuthority -> roleNames.contains(roleGrantedAuthority.getAuthority()))
                .map(RoleGrantedAuthority::privilegeLevel)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }

    public static int getPrivilegeLevelFromContextForRole(String roleName) {
        return getPrivilegeLevelFromContextForRoles(Set.of(roleName));
    }
}
