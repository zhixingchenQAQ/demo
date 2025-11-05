package com.example.common.security;

import javax.sql.DataSource;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class InfrastructureConnectionLogger implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(InfrastructureConnectionLogger.class);

    private final ObjectProvider<DataSource> dataSourceProvider;
    private final ObjectProvider<RedisConnectionFactory> redisConnectionFactoryProvider;
    private final ObjectProvider<CuratorFramework> curatorFrameworkProvider;
    private final Environment environment;

    public InfrastructureConnectionLogger(
        ObjectProvider<DataSource> dataSourceProvider,
        ObjectProvider<RedisConnectionFactory> redisConnectionFactoryProvider,
        ObjectProvider<CuratorFramework> curatorFrameworkProvider,
        Environment environment
    ) {
        this.dataSourceProvider = dataSourceProvider;
        this.redisConnectionFactoryProvider = redisConnectionFactoryProvider;
        this.curatorFrameworkProvider = curatorFrameworkProvider;
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logDatasource();
        logRedis();
        logZookeeper();
        logDubbo();
    }

    private void logDatasource() {
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        if (dataSource != null) {
            String url = environment.getProperty("spring.datasource.url");
            log.info("Database datasource initialized using URL: {}", StringUtils.hasText(url) ? url : "<unknown>");
        } else {
            log.warn("No DataSource bean available; database connection is not configured");
        }
    }

    private void logRedis() {
        RedisConnectionFactory factory = redisConnectionFactoryProvider.getIfAvailable();
        if (factory != null) {
            String host = environment.getProperty("spring.redis.host", "localhost");
            String port = environment.getProperty("spring.redis.port", "6379");
            log.info("Redis connection factory initialized for {}:{}", host, port);
        } else {
            log.warn("No RedisConnectionFactory bean available; Redis connection is not configured");
        }
    }

    private void logZookeeper() {
        CuratorFramework curatorFramework = curatorFrameworkProvider.getIfAvailable();
        if (curatorFramework != null) {
            String address = environment.getProperty("dubbo.registry.address");
            log.info(
                "CuratorFramework state '{}' for Zookeeper registry {}",
                curatorFramework.getState(),
                StringUtils.hasText(address) ? address : "<unknown>"
            );
        } else {
            log.warn("No CuratorFramework bean available; Zookeeper connection is not configured");
        }
    }

    private void logDubbo() {
        String protocol = environment.getProperty("dubbo.protocol.name", "dubbo");
        String port = environment.getProperty("dubbo.protocol.port", "<unspecified>");
        String registry = environment.getProperty("dubbo.registry.address", "<unspecified>");
        String application = environment.getProperty("dubbo.application.name", environment.getProperty("spring.application.name", "<unknown>"));
        log.info(
            "Dubbo application '{}' configured with protocol '{}' on port {} using registry {}",
            application,
            protocol,
            port,
            registry
        );
    }
}

