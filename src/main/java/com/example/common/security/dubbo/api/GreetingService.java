package com.example.common.security.dubbo.api;

/**
 * 一个用于调试 Dubbo 注册与过滤器链的简单服务接口。
 */
public interface GreetingService {

    /**
     * 生成一个带有调用者信息的问候语。
     *
     * @param name 调用方名称
     * @return 问候语
     */
    String greet(String name);
}
