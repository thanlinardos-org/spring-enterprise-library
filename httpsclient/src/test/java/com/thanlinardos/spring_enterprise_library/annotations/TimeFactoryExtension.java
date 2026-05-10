package com.thanlinardos.spring_enterprise_library.annotations;

import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.TimeProviderImpl;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class TimeFactoryExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        new TimeFactory(new TimeProviderImpl(
                ZoneId.of("UTC"),
                TimeUnit.MILLISECONDS,
                LocalDate.of(9999, 12, 31),
                LocalDate.of(1, 1, 1),
                LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999_999_999),
                LocalDateTime.of(1, 1, 1, 0, 0, 0)
        ));
    }
}



