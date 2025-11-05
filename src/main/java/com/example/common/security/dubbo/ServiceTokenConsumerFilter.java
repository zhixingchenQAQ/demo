package com.example.common.security.dubbo;

import com.example.common.security.ServiceAuthenticationProperties;
import com.example.common.security.ServiceAuthenticationProperties.CurrentService;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Activate(group = CommonConstants.CONSUMER)
public class ServiceTokenConsumerFilter implements Filter {

    private final ServiceAuthenticationProperties properties;

    public ServiceTokenConsumerFilter(ServiceAuthenticationProperties properties) {
        this.properties = properties;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        CurrentService currentService = properties.getCurrent();
        if (currentService != null
            && StringUtils.hasText(currentService.getName())
            && StringUtils.hasText(currentService.getToken())) {
            RpcContext.getClientAttachment().setAttachment("service-name", currentService.getName());
            RpcContext.getClientAttachment().setAttachment("service-token", currentService.getToken());
        }
        return invoker.invoke(invocation);
    }
}
