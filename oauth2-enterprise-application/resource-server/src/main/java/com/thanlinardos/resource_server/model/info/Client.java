package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class Client extends OwnerDetailsInfo implements Serializable {

    private String category;
    private UUID serviceAccountId;

    @JsonCreator
    public Client(@NotBlank String name, @NotBlank String category, @NotNull UUID serviceAccountId, Set<String> roles) {
        setName(name);
        setRoles(buildRoleModels(roles));
        this.category = category;
        this.serviceAccountId = serviceAccountId;
    }
}
