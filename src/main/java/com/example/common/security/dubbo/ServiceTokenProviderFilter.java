package com.example.common.security.dubbo;

import com.example.common.security.ServiceAuthenticationProperties;
import com.example.common.security.ServicePrincipal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import com.example.common.security.context.SpringContextHolder;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

/**
 * 重要点：
 * 1) 不能用 @Component（由 Dubbo SPI 实例化，不是 Spring）
 * 2) 必须有 public 无参构造
 * 3) 在 invoke() 里通过 SpringContextHolder 获取 Spring Bean
 */
@Activate(group = CommonConstants.PROVIDER)
public class ServiceTokenProviderFilter implements Filter {

    // 不用构造注入，改为懒加载（第一次用时从 Spring 取）
    private static final AtomicReference<ServiceAuthenticationProperties> CACHED = new AtomicReference<>();

    public ServiceTokenProviderFilter() {
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ServiceAuthenticationProperties properties = getProps();

        Map<String, String> trustedClients = properties.getTrustedClients();
        String serviceName = RpcContext.getServerAttachment().getAttachment("service-name");
        String token = RpcContext.getServerAttachment().getAttachment("service-token");

        if (!StringUtils.hasText(serviceName) || !StringUtils.hasText(token)) {
            throw new RpcException(RpcException.FORBIDDEN_EXCEPTION, "Missing service authentication metadata");
        }
        String expectedToken = trustedClients.get(serviceName);
        if (!StringUtils.hasText(expectedToken) || !Objects.equals(expectedToken, token)) {
            throw new RpcException(RpcException.FORBIDDEN_EXCEPTION, "Invalid service credentials for " + serviceName);
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new ServicePrincipal(serviceName),
                token,
                AuthorityUtils.createAuthorityList("ROLE_SERVICE")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            return invoker.invoke(invocation);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private ServiceAuthenticationProperties getProps() {
        ServiceAuthenticationProperties p = CACHED.get();
        if (p == null) {
            p = SpringContextHolder.getBean(ServiceAuthenticationProperties.class);
            if (p == null) {
                throw new RpcException("ServiceAuthenticationProperties not found in Spring context");
            }
            CACHED.compareAndSet(null, p);
        }
        return p;
    }
}
