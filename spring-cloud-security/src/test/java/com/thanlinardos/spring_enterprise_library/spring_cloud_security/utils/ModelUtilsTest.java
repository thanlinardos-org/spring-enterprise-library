package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ModelUtilsTest {

    private static final long PARENT_ID = 3L;
    private static final long OTHER_ID = 2L;
    private static final long MODEL_ID = 1L;

    @SuperBuilder
    @NoArgsConstructor
    @Getter
    static class TestIdModel extends BasicIdModel<TestEntity, TestIdModel> {

        private String uuid;

        public TestIdModel(TestEntity entity) {
            this.setId(entity.getId());
            this.uuid = entity.getUuid();
        }

        @Override
        public TestEntity toEntity() {
            return TestEntity.builder()
                    .id(getId())
                    .uuid(getUuid())
                    .build();
        }

        @Override
        public TestEntity toEntityOnlyId() {
            return TestEntity.builder()
                    .id(getId())
                    .build();
        }

        @Override
        public TestIdModel fromEntity(TestEntity entity) {
            return new TestIdModel(entity);
        }
    }

    @SuperBuilder
    @NoArgsConstructor
    @Getter
    static class OtherTestIdModel extends BasicIdModel<OtherTestEntity, OtherTestIdModel> {

        private String uuid;

        public OtherTestIdModel(OtherTestEntity entity) {
            super(entity.getId());
            this.uuid = entity.getUuid();
        }

        @Override
        public OtherTestEntity toEntity() {
            return OtherTestEntity.builder()
                    .id(getId())
                    .uuid(getUuid())
                    .build();
        }

        @Override
        public OtherTestEntity toEntityOnlyId() {
            return OtherTestEntity.builder()
                    .id(getId())
                    .build();
        }

        @Override
        public OtherTestIdModel fromEntity(OtherTestEntity entity) {
            return new OtherTestIdModel(entity);
        }
    }

    @SuperBuilder
    @NoArgsConstructor
    @Getter
    static class TestIdParentModel extends TestIdModel {

        private TestIdModel nestedModel;
        private OtherTestIdModel otherNestedModel;
    }

    @SuperBuilder
    @Getter
    static class TestEntity extends BasicIdJpa {

        private String uuid;
    }

    @SuperBuilder
    @Getter
    static class OtherTestEntity extends BasicIdJpa {

        private String uuid;
    }

    @Test
    void getIdFromModel() {
        Long id = MODEL_ID;
        TestIdModel model = TestIdModel.builder().id(id).build();
        assertEquals(id, ModelUtils.getIdFromModel(model));
    }

    @Test
    void getIdFromNestedModel() {
        Long id = MODEL_ID;
        TestIdModel nestedModel = TestIdModel.builder().id(id).build();
        TestIdParentModel parentModel = TestIdParentModel.builder()
                .id(OTHER_ID)
                .nestedModel(nestedModel)
                .build();
        assertEquals(id, ModelUtils.getIdFromNestedModel(parentModel, TestIdParentModel::getNestedModel));
    }

    private static Stream<Arguments> getIdFromNestedModelOrParams() {
        Long id = MODEL_ID;
        return Stream.of(
                Arguments.argumentSet("Both nested models in parent", buildParentModelWithBoth(id, PARENT_ID, OTHER_ID), id),
                Arguments.argumentSet("Both nested models in parent, parent null id", buildParentModelWithBoth(id, null, OTHER_ID), id),
                Arguments.argumentSet("Both nested models in parent, nested both null id", buildParentModelWithBoth(null, PARENT_ID, null), null),
                Arguments.argumentSet("Both nested models in parent, other nested null id", buildParentModelWithBoth(id, PARENT_ID, null), id),
                Arguments.argumentSet("Both nested models in parent, nested null id", buildParentModelWithBoth(null, PARENT_ID, id), id),
                Arguments.argumentSet("Only other nested model in parent", buildParentModelWithOther(id), id)
        );
    }

    @ParameterizedTest
    @MethodSource("getIdFromNestedModelOrParams")
    void getIdFromNestedModelOr(TestIdParentModel model, Long expected) {
        assertEquals(expected, ModelUtils.getIdFromNestedModelOr(model, TestIdParentModel::getNestedModel, TestIdParentModel::getOtherNestedModel));
    }

    @Test
    void getModelFromIdOrNull() {
        Long id = MODEL_ID;
        TestIdModel model = ModelUtils.getModelFromIdOrNull(id, TestIdModel::new);
        assertNotNull(model);
        assertEquals(id, model.getId());
    }

    @Test
    void getModelFromIdOrNullNull() {
        TestIdModel model = ModelUtils.getModelFromIdOrNull(null, TestIdModel::new);
        assertNull(model);
    }

    @Test
    void getModelFromId() {
        Long id = MODEL_ID;
        TestIdModel model = ModelUtils.getModelFromId(id, TestIdModel::new);
        assertEquals(id, model.getId());
    }

    @Test
    void getTestIdModelFromId() {
        Long id = MODEL_ID;
        TestIdModel model = ModelUtils.getModelFromId(id, TestIdModel::new);
        assertEquals(id, model.getId());
        assertNull(model.getUuid());
    }

    @Test
    void getModelFromEntity() {
        String uuid = "uuid";
        TestIdModel expected = buildTestIdModel(MODEL_ID, uuid);
        TestEntity entity = TestEntity.builder().id(MODEL_ID).uuid(uuid).build();

        TestIdModel modelFromEntity = ModelUtils.getModelFromEntity(entity, TestIdModel::new);
        assertEquals(expected, modelFromEntity);
    }

    @Test
    void getModelsSetFromEntities() {
        String uuid1 = "uuid1";
        TestEntity entity1 = TestEntity.builder().id(MODEL_ID).uuid(uuid1).build();
        String uuid2 = "uuid2";
        TestEntity entity2 = TestEntity.builder().id(OTHER_ID).uuid(uuid2).build();
        Set<TestEntity> entities = Set.of(entity1, entity2);
        Set<TestIdModel> expected = Set.of(buildTestIdModel(MODEL_ID, uuid1), buildTestIdModel(OTHER_ID, uuid2));

        Set<TestIdModel> models = ModelUtils.getModelsSetFromEntities(entities, TestIdModel::new);
        assertEquals(expected, models);
    }

    @Test
    void getModelsFromEntities() {
        String uuid1 = "uuid1";
        TestEntity entity1 = TestEntity.builder().id(MODEL_ID).uuid(uuid1).build();
        String uuid2 = "uuid2";
        TestEntity entity2 = TestEntity.builder().id(OTHER_ID).uuid(uuid2).build();
        List<TestEntity> entities = List.of(entity1, entity2);
        List<TestIdModel> expected = List.of(buildTestIdModel(MODEL_ID, uuid1), buildTestIdModel(OTHER_ID, uuid2));

        Collection<TestIdModel> models = ModelUtils.getModelsFromEntities(entities, TestIdModel::new);
        assertEquals(expected, models);
    }

    private static TestIdModel buildTestIdModel(long id, String uuid) {
        return TestIdModel.builder().id(id).uuid(uuid).build();
    }

    private static TestIdParentModel buildParentModelWithBoth(Long nestedId, Long parentId, Long otherId) {
        TestIdModel nestedModel = TestIdModel.builder().id(nestedId).build();
        OtherTestIdModel otherModel = OtherTestIdModel.builder().id(otherId).build();
        return TestIdParentModel.builder()
                .id(parentId)
                .nestedModel(nestedModel)
                .otherNestedModel(otherModel)
                .build();
    }

    private static TestIdParentModel buildParentModelWithOther(Long otherId) {
        OtherTestIdModel otherModel = OtherTestIdModel.builder().id(otherId).build();
        return TestIdParentModel.builder()
                .id(PARENT_ID)
                .otherNestedModel(otherModel)
                .build();
    }
}