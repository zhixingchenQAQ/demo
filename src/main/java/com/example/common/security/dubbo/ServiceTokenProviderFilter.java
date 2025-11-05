package com.example.common.security.dubbo;

import com.example.common.security.ServiceAuthenticationProperties;
import com.example.common.security.ServicePrincipal;
import java.util.Map;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Activate(group = CommonConstants.PROVIDER)
public class ServiceTokenProviderFilter implements Filter {

    private final ServiceAuthenticationProperties properties;

    public ServiceTokenProviderFilter(ServiceAuthenticationProperties properties) {
        this.properties = properties;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Map<String, String> trustedClients = properties.getTrustedClients();
        String serviceName = RpcContext.getServerAttachment().getAttachment("service-name");
        String token = RpcContext.getServerAttachment().getAttachment("service-token");
        if (!StringUtils.hasText(serviceName) || !StringUtils.hasText(token)) {
            throw new RpcException(RpcException.FORBIDDEN_EXCEPTION, "Missing service authentication metadata");
        }
        String expectedToken = trustedClients.get(serviceName);
        if (!StringUtils.hasText(expectedToken) || !expectedToken.equals(token)) {
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
}
