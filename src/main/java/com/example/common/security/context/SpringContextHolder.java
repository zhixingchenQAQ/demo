package com.example.common.security.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext CTX;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.CTX = applicationContext;
    }

    @Nullable
    public static <T> T getBean(Class<T> type) {
        return CTX != null ? CTX.getBean(type) : null;
    }
}
