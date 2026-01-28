package com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.datasource;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.datasource.properties.CustomDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@Slf4j
@ConditionalOnProperty(value = {"spring.cloud.config.enabled"}, havingValue = "true")
@EnableConfigurationProperties(CustomDataSourceProperties.class)
public class RefreshDataSourceConfig {

    @Bean
    @Primary
    @RefreshScope
    public DataSource dataSource(CustomDataSourceProperties properties) {
        log.info("Building DataSource for user {} and url {}", properties.getUsername(), properties.getUrl());
        return DataSourceBuilder.create()
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }
}
