package com.thanlinardos.spring_enterprise_library.service;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.annotations.TimeFactoryExtension;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import com.thanlinardos.spring_enterprise_library.repository.base.BasicIdJpaRepository;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@CoreTest
@ExtendWith({MockitoExtension.class, TimeFactoryExtension.class})
class ModelServiceHelperTest {

    @Mock
    private BasicIdJpaRepository<ParentEntity> repository;

    @Mock
    private BasicIdJpaRepository<SubEntity> subRepository;

    private ModelServiceHelper<ParentEntity, ParentModel, SubEntity, SubModel> helper;

    @BeforeEach
    void setUp() {
        helper = new ModelServiceHelper<>(repository, subRepository);
    }

    @Test
    void saveOrUpdateMethods_shouldSetIdsFromSavedEntities() {
        ParentModel parentModel = new ParentModel(1L);
        SubModel subModel = new SubModel(2L);
        long parentId = 10L;
        long subId = 20L;

        doAnswer(i -> setParentEntityIdOnFirstArgument(i, parentId)).when(repository).saveFoundByProperty(any(), any());
        doAnswer(i -> setSubEntityIdOnFirstArgument(i, subId)).when(subRepository).saveFoundByProperty(any(), any());

        ParentModel savedParent = helper.saveOrUpdateEntityFoundBy(parentModel, Optional::empty);
        SubModel savedSub = helper.saveOrUpdateSubEntityFoundBy(subModel, Optional::empty);

        assertEquals(parentId, savedParent.getId());
        assertEquals(subId, savedSub.getId());
    }

    @Nullable
    private Object setParentEntityIdOnFirstArgument(InvocationOnMock invocation, long id) {
        ParentEntity entity = invocation.getArgument(0);
        entity.setId(id);
        return null;
    }

    @Nullable
    private Object setSubEntityIdOnFirstArgument(InvocationOnMock invocation, long id) {
        SubEntity entity = invocation.getArgument(0);
        entity.setId(id);
        return null;
    }

    @Test
    void linkAndUnlink_shouldUpdateRelationsAndReturnMappedModel() {
        ParentEntity parent = new ParentEntity(1L);
        SubEntity sub = new SubEntity(5L);
        ParentModel model = new ParentModel(1L);

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ParentModel linked = helper.linkToModel(model, () -> Optional.of(parent), ParentEntity::getRelations, sub);
        assertTrue(parent.getRelations().contains(sub));
        assertEquals(1L, linked.getId());

        ParentModel unlinked = helper.unlinkFromModel(model, () -> Optional.of(parent), ParentEntity::getRelations, sub);
        verify(repository).removeRelation(eq(parent), any(), eq(sub));
        assertEquals(1L, unlinked.getId());
    }

    @Test
    void createWithLinks_shouldUpdateRelationsAndRefetchModel() {
        ParentModel model = new ParentModel(7L);
        SubModel existing = new SubModel(3L);
        SubModel next = new SubModel(4L);

        ParentEntity refreshed = new ParentEntity(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(refreshed));

        ParentModel result = helper.createWithLinks(
                model,
                List.of(existing),
                Set.of(next),
                true,
                m -> Optional.of(new ParentEntity(m.getId())),
                ParentEntity::getRelations
        );

        verify(repository).updateRelations(any(), any(), anyCollection());
        assertEquals(7L, result.getId());
    }

    @Test
    void createSubWithLinks_shouldLinkUnlinkAndRefetchSubModel() {
        SubModel model = new SubModel(8L);
        ParentModel keep = new ParentModel(1L);
        ParentModel remove = new ParentModel(2L);
        ParentModel add = new ParentModel(3L);

        when(subRepository.findById(8L)).thenReturn(Optional.of(new SubEntity(8L)));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SubModel result = helper.createSubWithLinks(
                model,
                List.of(keep, remove),
                Set.of(keep, add),
                true,
                m -> Optional.of(new ParentEntity(m.getId())),
                ParentEntity::getRelations
        );

        verify(repository, atLeastOnce()).removeRelation(any(), any(), any());
        verify(repository, atLeastOnce()).save(any());
        assertEquals(8L, result.getId());
    }

    @Test
    void createWithLinks_whenRefetchFails_shouldThrowCoreException() {
        ParentModel model = new ParentModel(77L);
        when(repository.findById(77L)).thenReturn(Optional.empty());

        List<SubModel> emptyList = Collections.emptyList();
        Set<SubModel> emptySet = Collections.emptySet();
        assertThrows(CoreException.class, () -> helper.createWithLinks(
                model,
                emptyList,
                emptySet,
                true,
                m -> Optional.of(new ParentEntity(m.getId())),
                ParentEntity::getRelations
        ));
    }

    private static final class ParentEntity extends BasicIdJpa {
        private final Collection<SubEntity> relations = new HashSet<>();

        private ParentEntity(Long id) {
            super(id);
        }

        private Collection<SubEntity> getRelations() {
            return relations;
        }
    }

    private static final class SubEntity extends BasicIdJpa {
        private SubEntity(Long id) {
            super(id);
        }
    }

    private static final class ParentModel extends BasicIdModel<ParentEntity, ParentModel> {
        private ParentModel(Long id) {
            super(new ParentEntity(id));
        }

        @Override
        public ParentEntity toEntity() {
            return new ParentEntity(getId());
        }

        @Override
        public ParentEntity toEntityOnlyId() {
            return new ParentEntity(getId());
        }

        @Override
        public ParentModel fromEntity(ParentEntity entity) {
            return new ParentModel(entity.getId());
        }
    }

    private static final class SubModel extends BasicIdModel<SubEntity, SubModel> {
        private SubModel(Long id) {
            super(new SubEntity(id));
        }

        @Override
        public SubEntity toEntity() {
            return new SubEntity(getId());
        }

        @Override
        public SubEntity toEntityOnlyId() {
            return new SubEntity(getId());
        }

        @Override
        public SubModel fromEntity(SubEntity entity) {
            return new SubModel(entity.getId());
        }
    }
}

