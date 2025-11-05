# common-security

一个可复用的 Spring Security 公共模块示例，提供了基础的安全配置、密码编码器、Dubbo 集成以及服务间调用认证能力。

## 功能特性

- 基于 JDK 8 可运行的 Spring Boot 2.7 与 Spring Security 5.7 自动配置
- 默认开启 HTTP Basic 与表单登录
- 演示性的内存用户存储（可根据业务替换）
- 内置 Dubbo 启动配置，并提供消费者/提供者过滤器
- 针对 REST 与 Dubbo 调用的服务间 Token 验证过滤器
- 默认提供 Dubbo、Zookeeper、Redis 与 MySQL 的连接示例配置
- 开启方法级别的安全注解支持

## 快速开始

1. **构建项目**

   ```bash
   mvn clean package
   ```

2. **运行示例应用**

   ```bash
   mvn spring-boot:run
   ```

   访问 `http://localhost:8080` 时将提示登录。默认用户：

   | 用户名 | 密码     | 角色  |
   | ------ | -------- | ----- |
   | user   | password | USER  |
   | admin  | admin    | ADMIN |

3. **配置服务间认证**

   在 `src/main/resources/application.yml` 中可以直接修改示例配置，或在外部配置文件中声明本服务的身份以及可信客户端清单：

   ```yaml
   security:
     service:
       current:
         name: order-service
         token: ${ORDER_SERVICE_TOKEN:order-service-secret}
       trusted-clients:
         payment-service: ${PAYMENT_SERVICE_TOKEN:payment-service-secret}
         inventory-service: ${INVENTORY_SERVICE_TOKEN:inventory-service-secret}
   ```

   - REST 请求需携带 `X-Service-Name` 与 `X-Service-Token` 请求头，模块会自动校验并注入 `ROLE_SERVICE` 权限。
   - Dubbo 调用会在消费者侧自动附带当前服务的认证信息，并在提供者侧进行拦截与验证。

4. **自定义**

   - 将 `SecurityConfiguration` 中的 `InMemoryUserDetailsManager` 替换为数据库、LDAP 或自定义的 `UserDetailsService`
   - 根据需要调整 `SecurityConfiguration` 中 `authorizeRequests` 的权限策略或增加角色判断
   - 扩展 `trusted-clients` 清单或接入外部凭证存储

   同时在 `application.yml` 中已经预置：

   - Dubbo 协议端口（默认 `20880`）与注册中心（默认连接本机 Zookeeper `127.0.0.1:2181`）
   - Redis 默认连接（主机 `127.0.0.1`、端口 `6379`、数据库 `0`）
   - MySQL 连接示例（指向 `common_security` 数据库，默认用户名/密码 `root`）

## 目录结构

```
.
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── common
    │   │               └── security
    │   │                   ├── CommonSecurityApplication.java
    │   │                   ├── SecurityConfiguration.java
    │   │                   ├── ServiceAuthenticationProperties.java
    │   │                   ├── ServicePrincipal.java
    │   │                   ├── ServiceTokenAuthenticationFilter.java
    │   │                   └── dubbo
    │   │                       ├── DubboConfiguration.java
    │   │                       ├── ServiceTokenConsumerFilter.java
    │   │                       └── ServiceTokenProviderFilter.java
    │   └── resources
    │       ├── META-INF
    │       │   └── dubbo
    │       │       └── org.apache.dubbo.rpc.Filter
    │       └── application.yml
    └── test
        └── java
            └── com
                └── example
                    └── common
                        └── security
```

## 许可证

本项目以 MIT 许可证开源，欢迎根据需求自由调整。
