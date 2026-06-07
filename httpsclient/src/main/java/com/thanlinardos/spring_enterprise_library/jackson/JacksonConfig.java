package com.thanlinardos.spring_enterprise_library.jackson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(
        basePackageClasses = SpringEnterpriseLibraryJacksonPackage.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)}
)
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
