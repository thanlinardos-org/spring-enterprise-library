package com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Service interface for managing roles and their associated authorities.
 *
 * @param <T> the type of Role used in the application
 */
public interface RoleService<T extends Role> {

    /**
     * Finds a role by its name.
     *
     * @param name the name of the role to find, with the ROLE_ prefix.
     * @return the role that matches the given name, or null if not found.
     */
    T findRole(String name);

    /**
     * Finds roles by their names.
     *
     * @param names the names of the roles to find, with the ROLE_ prefix.
     * @return a collection of roles that match the given names.
     */
    Collection<T> findRoles(Collection<String> names);

    /**
     * Finds roles by their names, by filtering non-existent roles without throwing an exception.
     * Used for cases like Keycloak realm roles, where some of the granted authorities may not exist as roles in the system.
     *
     * @param names the names of the roles to find, with the ROLE_ prefix.
     * @return a collection of roles that match the given names.
     */
    Collection<T> findRolesWithoutValidation(Collection<String> names);

    /**
     * Finds granted authorities associated with a collection of role names.
     *
     * @param roleNames a collection of role names, each prefixed with {@link SecurityCommonConstants#ROLE_PREFIX}.
     * @return a collection of granted authorities associated with the given role names.
     */
    Collection<GrantedAuthority> findGrantedAuthoritiesWithRoles(Collection<String> roleNames);

    /**
     * Retrieves all available authorities in the system.
     *
     * @return a collection of all authorities.
     */
    Collection<Authority> getAllAuthorities();

    /**
     * Determines the highest privilege level from a collection of role names.
     *
     * @param roleNames a collection of role names, each prefixed with "ROLE_".
     * @return the highest privilege level among the provided roles.
     */
    int getPrivilegeLevelFromRoleNames(Collection<String> roleNames);

    /**
     * Retrieves all available roles in the system.
     *
     * @return a collection of all roles.
     */
    Collection<T> getAllRoles();
}
