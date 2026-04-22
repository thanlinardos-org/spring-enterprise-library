package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.account.CardJpa;
import com.thanlinardos.spring_enterprise_library.repository.base.BasicIdJpaRepository;

import java.util.List;

public interface CardRepository extends BasicIdJpaRepository<CardJpa> {

    List<CardJpa> getByAccountOwnerName(String name);
}
