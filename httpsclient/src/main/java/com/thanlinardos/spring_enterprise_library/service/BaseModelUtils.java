package com.thanlinardos.spring_enterprise_library.service;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import com.thanlinardos.spring_enterprise_library.objects.utils.CollectionUtils;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

public class BaseModelUtils {

    private BaseModelUtils() {
    }

    /**
     * Generic method for managing links between a saved model and a set of linked models.
     * Updates the parent entity's links to the given sub-entities in one go.
     *
     * @param model           the already saved model
     * @param existingLinks   the existing set of links
     * @param newLinks        the desired set of links
     * @param unlinkOthers    whether to unlink existing links not present in the new set
     * @param symmetricLinker updates the links to the model
     * @return the model
     */
    public static <T extends BasicIdJpa, S extends BasicIdJpa, M extends BasicIdModel<T, M>, L extends BasicIdModel<S, L>> M createWithLinks(M model, Collection<L> existingLinks, Set<L> newLinks, boolean unlinkOthers, BiConsumer<M, Collection<L>> symmetricLinker) {
        if (unlinkOthers) {
            symmetricLinker.accept(model, newLinks);
        } else {
            symmetricLinker.accept(model, CollectionUtils.combineToSet(existingLinks, newLinks));
        }
        return model;
    }

    /**
     * Generic method for managing links between a saved model and a set of linked models.
     * Links and unlinks the created sub-entity to each given parent entity determined by the existing and new links.
     *
     * @param model         the already saved model
     * @param existingLinks the existing set of links
     * @param newLinks      the desired set of links
     * @param unlinkOthers  whether to unlink existing links not present in the new set
     * @param linker        links a single link to the model
     * @param unlinker      unlinks a single link from the model
     * @return the model
     */
    public static <T extends BasicIdJpa, S extends BasicIdJpa, M extends BasicIdModel<T, M>, L extends BasicIdModel<S, L>> M createSubWithLinks(M model, Collection<L> existingLinks, Set<L> newLinks, boolean unlinkOthers, BiConsumer<M, L> linker, BiConsumer<M, L> unlinker) {
        if (unlinkOthers) {
            Set<L> modelsToUnlink = CollectionUtils.subtractToSet(existingLinks, newLinks);
            modelsToUnlink.forEach(link -> unlinker.accept(model, link));
        }
        Set<L> modelsToLink = CollectionUtils.subtractToSet(newLinks, existingLinks);
        modelsToLink.forEach(link -> linker.accept(model, link));
        return model;
    }
}