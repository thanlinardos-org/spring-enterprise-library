package com.thanlinardos.spring_enterprise_library.service;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@CoreTest
class BaseModelUtilsTest {

    @Test
    void createWithLinks_shouldReplaceOrMergeBasedOnUnlinkFlag() {
        TestModel model = new TestModel(1L);
        TestModel existing1 = new TestModel(2L);
        TestModel existing2 = new TestModel(3L);
        TestModel newLink = new TestModel(4L);

        List<TestModel> linked = new ArrayList<>();

        TestModel resultReplace = BaseModelUtils.createWithLinks(
                model,
                List.of(existing1, existing2),
                Set.of(newLink),
                true,
                (m, links) -> {
                    linked.clear();
                    linked.addAll(links);
                });

        assertSame(model, resultReplace);
        assertEquals(List.of(newLink), linked);

        TestModel resultMerge = BaseModelUtils.createWithLinks(
                model,
                List.of(existing1),
                Set.of(newLink),
                false,
                (m, links) -> {
                    linked.clear();
                    linked.addAll(links);
                });

        assertSame(model, resultMerge);
        assertEquals(2, new HashSet<>(linked).size());
    }

    @Test
    void createSubWithLinks_shouldUnlinkMissingAndLinkNewOnes() {
        TestModel model = new TestModel(1L);
        TestModel existing1 = new TestModel(2L);
        TestModel existing2 = new TestModel(3L);
        TestModel keepAndLink = new TestModel(3L);
        TestModel add = new TestModel(4L);

        List<TestModel> unlinked = new ArrayList<>();
        List<TestModel> linked = new ArrayList<>();

        TestModel result = BaseModelUtils.createSubWithLinks(
                model,
                Set.of(existing1, existing2),
                Set.of(keepAndLink, add),
                true,
                (m, link) -> linked.add(link),
                (m, link) -> unlinked.add(link));

        assertSame(model, result);
        assertEquals(Set.of(existing1), new HashSet<>(unlinked));
        assertEquals(Set.of(add), new HashSet<>(linked));
    }

    private static final class TestEntity extends BasicIdJpa {
        private TestEntity(Long id) {
            super(id);
        }
    }

    private static final class TestModel extends BasicIdModel<TestEntity, TestModel> {

        private TestModel(Long id) {
            super(new TestEntity(id));
        }

        @Override
        public TestEntity toEntity() {
            return new TestEntity(getId());
        }

        @Override
        public TestEntity toEntityOnlyId() {
            return new TestEntity(getId());
        }

        @Override
        public TestModel fromEntity(TestEntity entity) {
            return new TestModel(entity.getId());
        }
    }
}

