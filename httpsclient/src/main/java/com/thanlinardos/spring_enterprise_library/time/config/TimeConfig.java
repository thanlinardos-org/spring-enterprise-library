package com.thanlinardos.spring_enterprise_library.time.config;

import com.thanlinardos.spring_enterprise_library.time.SpringEnterpriseLibraryTimePackage;
import com.thanlinardos.spring_enterprise_library.time.TimeProviderImpl;
import com.thanlinardos.spring_enterprise_library.time.api.TimeProvider;
import com.thanlinardos.spring_enterprise_library.time.properties.TimeProviderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Configuration class for time-related beans and components.
 * <p>
 * This class scans the specified package for components related to time management,
 * excluding any classes annotated with @Configuration to avoid conflicts.
 * It also enables configuration properties for TimeProviderProperties.
 * </p>
 */
@Configuration
@ComponentScan(
        basePackageClasses = SpringEnterpriseLibraryTimePackage.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)}
)
@EnableConfigurationProperties(TimeProviderProperties.class)
public class TimeConfig {

    /**
     * Default constructor for TimeConfig.
     */
    public TimeConfig() {
        // Default constructor
    }

    /**
     * Creates a TimeProvider bean configured with properties from TimeProviderProperties.
     *
     * @param properties the TimeProviderProperties containing configuration settings
     * @return a configured TimeProvider instance
     */
    @Bean
    public TimeProvider timeProvider(TimeProviderProperties properties) {
        return new TimeProviderImpl(
                properties.getTimeZoneId(),
                properties.getAccuracy(),
                properties.getMaxDate(),
                properties.getMinDate(),
                properties.getMaxDateTime(),
                properties.getMinDateTime()
        );
    }
}
