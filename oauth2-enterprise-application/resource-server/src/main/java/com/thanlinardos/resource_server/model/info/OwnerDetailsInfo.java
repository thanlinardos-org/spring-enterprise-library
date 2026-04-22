package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thanlinardos.resource_server.misc.utils.RoleUtils;
import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.spring_enterprise_library.parse.utils.ParserUtil;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OwnerDetailsInfo implements Serializable, PrivilegedResource {

    private UUID uuid;
    @NotBlank
    private String name;
    private Set<RoleModel> roles;
    private LocalDate createDt;

    @Override
    public int getPrivilegeLevel() {
        return PrivilegedResource.calcPrivilegeLvlFromRoles(getRoles());
    }

    @Override
    public String getPrincipalName() {
        return ParserUtil.safeParseString(name);
    }

    /**
     * Get the names of the roles assigned to the owner, without the _ROLE prefix.
     *
     * @return A set of role names.
     */
    @JsonIgnore
    public Set<String> getRoleNamesNoPrefix() {
        return getRoles().stream()
                .map(RoleModel::getNameNoPrefix)
                .collect(Collectors.toSet());
    }

    protected Set<RoleModel> buildRoleModels(Set<String> roleNames) {
        return roleNames.stream()
                .map(role -> RoleModel.builder().role(role).privilegeLvl(RoleUtils.getPrivilegeLevelFromContextForRole(role)).build()) //NOSONAR (S3252)
                .collect(Collectors.toSet());
    }
}
