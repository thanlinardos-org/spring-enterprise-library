package com.thanlinardos.spring_enterprise_library.annotations;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith({MockitoExtension.class, TimeFactoryExtension.class})
public @interface CoreTest {
}
