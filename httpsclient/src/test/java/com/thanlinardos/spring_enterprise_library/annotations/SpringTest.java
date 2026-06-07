package com.thanlinardos.spring_enterprise_library.annotations;

import com.thanlinardos.spring_enterprise_library.time.config.TimeConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application-test.properties")
@Import(TimeConfig.class)
public @interface SpringTest { // TODO: Failed to introspect meta-annotation @Import on interface com.thanlinardos.spring_enterprise_library.annotations.SpringTest: java.lang.TypeNotPresentException: Type com.thanlinardos.spring_enterprise_library.time.config.TimeConfig not present
}
