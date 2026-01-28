package com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.datasource.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties(prefix = "datasource")
@Getter
@AllArgsConstructor
@RefreshScope
public class CustomDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
