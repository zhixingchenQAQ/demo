package com.example.common.security.dubbo.provider;

import com.example.common.security.dubbo.api.GreetingService;
import com.example.common.security.ServicePrincipal;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;

/**
 * 简单的 Dubbo 服务实现，可用于验证服务间调用时的 Token 注入是否成功。
 */
@DubboService(interfaceClass = GreetingService.class)
public class GreetingServiceImpl implements GreetingService {

    private static final Logger log = LoggerFactory.getLogger(GreetingServiceImpl.class);

    @Override
    public String greet(String name) {
        String principal = resolvePrincipalDisplayName();
        log.info("Received dubbo greet request from '{}' as '{}'", name, principal);
        return String.format("Hello %s, I'm %s at %s", name, principal, Instant.now());
    }

    private String resolvePrincipalDisplayName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "anonymous";
        }
        return Optional.ofNullable(authentication.getPrincipal())
                .filter(ServicePrincipal.class::isInstance)
                .map(ServicePrincipal.class::cast)
                .map(ServicePrincipal::getServiceName)
                .orElse(String.valueOf(authentication.getPrincipal()));
    }
}
