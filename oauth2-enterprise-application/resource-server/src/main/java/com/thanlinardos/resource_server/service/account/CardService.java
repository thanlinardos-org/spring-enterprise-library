package com.thanlinardos.resource_server.service.account;

import com.thanlinardos.resource_server.model.mapped.CardModel;
import com.thanlinardos.resource_server.repository.api.CardRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public List<CardModel> getCardsDetails() {
        return getCardsForPrincipalName(getAuthenticationOrThrow().getName());
    }

    @Nonnull
    private Authentication getAuthenticationOrThrow() {
        return Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private List<CardModel> getCardsForPrincipalName(String name) {
        return cardRepository.getByAccountOwnerName(name).stream()
                .map(CardModel::new)
                .toList();
    }
}