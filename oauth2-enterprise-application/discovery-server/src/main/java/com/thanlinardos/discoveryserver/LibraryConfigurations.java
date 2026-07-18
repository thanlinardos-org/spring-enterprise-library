package com.thanlinardos.discoveryserver;

import com.thanlinardos.spring_enterprise_library.time.SpringEnterpriseLibraryTimePackage;
import com.thanlinardos.spring_enterprise_library.time.properties.TimeProviderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ComponentScan(basePackageClasses = {SpringEnterpriseLibraryTimePackage.class})
@EnableConfigurationProperties({TimeProviderProperties.class})
@DependsOn("timeFactory")
public class LibraryConfigurations {
}
