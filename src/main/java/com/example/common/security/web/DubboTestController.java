package com.example.common.security.web;

import com.example.common.security.dubbo.api.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简单的 REST 控制器，提供一个入口触发 Dubbo 调用以便人工测试。
 */
@RestController
@RequestMapping("/api/dubbo")
public class DubboTestController {

    private static final Logger log = LoggerFactory.getLogger(DubboTestController.class);

    @DubboReference(check = false, timeout = 3000)
    private GreetingService greetingService;

    @GetMapping("/greet")
    public ResponseEntity<String> greet(@RequestParam(defaultValue = "tester") String name) {
        log.info("Invoking dubbo greeting for '{}'", name);
        return ResponseEntity.ok(greetingService.greet(name));
    }
}
