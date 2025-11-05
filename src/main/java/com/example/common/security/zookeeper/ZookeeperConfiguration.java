package com.example.common.security.zookeeper;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
public class ZookeeperConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperConfiguration.class);

    @Bean(initMethod = "start", destroyMethod = "close")
    @ConditionalOnMissingBean(CuratorFramework.class)
    public CuratorFramework curatorFramework(Environment environment) {
        String registryAddress = environment.getProperty("dubbo.registry.address");
        if (!StringUtils.hasText(registryAddress)) {
            throw new IllegalStateException("Property 'dubbo.registry.address' must be configured for CuratorFramework");
        }
        String connectString = resolveConnectString(registryAddress.trim());
        log.info("Initializing CuratorFramework for Zookeeper at {}", connectString);
        return CuratorFrameworkFactory.builder()
            .connectString(connectString)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    }

    private String resolveConnectString(String registryAddress) {
        try {
            URI uri = new URI(registryAddress);
            if (!StringUtils.hasText(uri.getHost())) {
                return registryAddress;
            }
            int port = uri.getPort();
            if (port > 0) {
                return uri.getHost() + ":" + port;
            }
            return uri.getHost();
        } catch (URISyntaxException ex) {
            log.warn("Unable to parse registry address '{}', using raw value", registryAddress, ex);
            return registryAddress;
        }
    }
}

