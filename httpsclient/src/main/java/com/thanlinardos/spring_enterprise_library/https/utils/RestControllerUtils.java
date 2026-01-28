package com.thanlinardos.spring_enterprise_library.https.utils;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestControllerUtils {

    public static <T> ResponseEntity<T> getOkResponseWithBodyOrNotFound(@Nullable T bodyOrNull) {
        return Optional.ofNullable(bodyOrNull)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
