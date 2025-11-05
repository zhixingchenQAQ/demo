# common-security

一个可复用的 Spring Security 公共模块示例，提供了基础的安全配置、密码编码器以及内存用户。

## 功能特性

- 基于 Spring Boot 3 与 Spring Security 6 的自动配置
- 默认开启 HTTP Basic 与表单登录
- 演示性的内存用户存储（可根据业务替换）
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

3. **自定义**

   - 将 `SecurityConfiguration` 中的 `InMemoryUserDetailsManager` 替换为数据库、LDAP 或自定义的 `UserDetailsService`
   - 根据需要调整 `authorizeHttpRequests` 的权限策略
   - 增加自定义的过滤器或认证逻辑

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
    │   │                   └── SecurityConfiguration.java
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
