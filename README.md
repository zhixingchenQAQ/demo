# common-security

一个可复用的 Spring Security 公共模块示例，提供了基础的安全配置、密码编码器、Dubbo 集成以及服务间调用认证能力。

## 功能特性

- 基于 Spring Boot 3 与 Spring Security 6 的自动配置
- 默认开启 HTTP Basic 与表单登录
- 演示性的内存用户存储（可根据业务替换）
- 内置 Dubbo 启动配置，并提供消费者/提供者过滤器
- 针对 REST 与 Dubbo 调用的服务间 Token 验证过滤器
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

   在应用的配置文件中声明本服务的身份以及可信客户端清单：

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
   - 根据需要调整 `authorizeHttpRequests` 的权限策略或增加角色判断
   - 扩展 `trusted-clients` 清单或接入外部凭证存储

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
    │       └── META-INF
    │           └── dubbo
    │               └── org.apache.dubbo.rpc.Filter
    │   └── resources
    └── test
        └── java
            └── com
                └── example
                    └── common
                        └── security
```

## 许可证

本项目以 MIT 许可证开源，欢迎根据需求自由调整。
