package com.thanlinardos.resource_server.aspect;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
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
@ConditionalOnProperty(name = "thanlinardos.resource-server.authorization-mode", havingValue = "controller")
public class ControllerAuthorizationAspect {

    @Around("com.thanlinardos.resource_server.aspect.PointCutDefinitions.forControllerPackage()")
    private Object authorizeControllerOperation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            return AuthorizationAspectHelper.authorizeControllerOperation(proceedingJoinPoint);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.UNEXPECTED_ERROR, "Error authorizing controller operation", e);
        }
    }
}
