package com.thanlinardos.spring_enterprise_library.model.mapped.base;

import com.thanlinardos.spring_enterprise_library.model.api.WithId;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * A basic model class that includes an ID field.
 * This class can be extended by other model classes to inherit the ID property.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BasicIdModel<E extends BasicIdJpa, M extends BasicIdModel<E, M>> implements Serializable, WithId {

    private Long id;

    /**
     * Constructs a BasicIdModel from a BasicIdJpa entity.
     *
     * @param entity the BasicIdJpa entity to copy the ID from.
     */
    protected BasicIdModel(BasicIdJpa entity) {
        setId(entity.getId());
    }

    public abstract E toEntity();

    public abstract E toEntityOnlyId();

    public abstract M fromEntity(E entity);
}
