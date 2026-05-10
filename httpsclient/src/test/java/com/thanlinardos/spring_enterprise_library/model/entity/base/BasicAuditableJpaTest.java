package com.thanlinardos.spring_enterprise_library.model.entity.base;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicAuditableModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CoreTest
class BasicAuditableJpaTest {

    @Test
    void trackedPropertyMethods_shouldCopyAuditFieldsAndIdWhenRequested() {
        BasicAuditableJpa entity = new BasicAuditableJpa();
        BasicAuditableJpa source = new BasicAuditableJpa();
        source.setId(11L);
        source.setCreatedAt(LocalDateTime.of(2025, 1, 1, 10, 0));
        source.setCreatedBy("creator");
        source.setUpdatedAt(LocalDateTime.of(2025, 1, 2, 11, 0));
        source.setUpdatedBy("updater");

        TestAuditableModel model = new TestAuditableModel(source);

        entity.setTrackedProperties(model);
        assertEquals(source.getCreatedAt(), entity.getCreatedAt());
        assertEquals("creator", entity.getCreatedBy());
        assertEquals(source.getUpdatedAt(), entity.getUpdatedAt());
        assertEquals("updater", entity.getUpdatedBy());

        entity.setTrackedPropertiesWithLink(model);
        assertEquals(11L, entity.getId());
    }

    private static final class TestAuditableModel extends BasicAuditableModel<BasicAuditableJpa> {

        private TestAuditableModel(BasicAuditableJpa entity) {
            super(entity);
        }

        @Override
        public BasicAuditableJpa toEntity() {
            BasicAuditableJpa entity = new BasicAuditableJpa();
            entity.setId(getId());
            entity.setCreatedAt(getCreatedAt());
            entity.setCreatedBy(getCreatedBy());
            entity.setUpdatedAt(getUpdatedAt());
            entity.setUpdatedBy(getUpdatedBy());
            return entity;
        }

        @Override
        public BasicAuditableJpa toEntityOnlyId() {
            return new BasicAuditableJpa();
        }

        @Override
        public TestAuditableModel fromEntity(BasicAuditableJpa entity) {
            return new TestAuditableModel(entity);
        }
    }
}

