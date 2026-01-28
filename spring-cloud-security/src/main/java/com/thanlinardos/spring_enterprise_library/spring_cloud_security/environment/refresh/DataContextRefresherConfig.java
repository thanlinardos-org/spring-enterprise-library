package com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.refresh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.cloud.util.ConditionalOnBootstrapDisabled;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataContextRefresherConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnBootstrapDisabled
    public ConfigDataContextRefresher configDataContextRefresher(ConfigurableApplicationContext context,
                                                                 org.springframework.cloud.context.scope.refresh.RefreshScope scope,
                                                                 RefreshAutoConfiguration.RefreshProperties properties,
                                                                 @Value("${thanlinardos.springenterpriselibrary.secrets.refresh.displayValues}") boolean displayValues) {
        return new CustomConfigDataContextRefresher(context, scope, properties, displayValues);
    }
}
