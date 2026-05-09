package com.thanlinardos.resource_server.aspect;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.aspect.AuthorizationAspectHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "thanlinardos.resource-server.authorization-mode", havingValue = "service")
public class ServiceAuthorizationAspect {

    @Around("com.thanlinardos.resource_server.aspect.PointCutDefinitions.forServicePackageAndNotEntityReturned()")
    private Object authorizeServiceMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return AuthorizationAspectHelper.authorizeServiceMethod(proceedingJoinPoint);
    }
}
