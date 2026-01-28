package com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.refresh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(value = {"scheduling.enabled", "thanlinardos.springenterpriselibrary.secrets.refresh.enabled"}, havingValue = "true")
public class CloudClientConfigRefreshComponent {

    private final CustomConfigDataContextRefresher contextRefresher;

    CloudClientConfigRefreshComponent(ConfigDataContextRefresher configDataContextRefresher) {
        this.contextRefresher = (CustomConfigDataContextRefresher) configDataContextRefresher;
    }

    @Scheduled(initialDelayString = "${thanlinardos.springenterpriselibrary.secrets.refresh-interval-ms}", fixedDelayString = "${thanlinardos.springenterpriselibrary.secrets.refresh-interval-ms}")
    void refresher() {
        Object props = contextRefresher.customRefresh();
        log.info("Refreshed the following props: {}", props);
    }
}
