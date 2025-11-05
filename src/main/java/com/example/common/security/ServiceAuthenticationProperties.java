package com.example.common.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.service")
public class ServiceAuthenticationProperties {

    private final CurrentService current = new CurrentService();

    private Map<String, String> trustedClients = new HashMap<>();

    public CurrentService getCurrent() {
        return current;
    }

    public Map<String, String> getTrustedClients() {
        return trustedClients;
    }

    public void setTrustedClients(Map<String, String> trustedClients) {
        this.trustedClients = trustedClients;
    }

    public static class CurrentService {

        private String name;

        private String token;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
