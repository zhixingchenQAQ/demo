package com.example.common.security;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class ServiceTokenAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_SERVICE_NAME = "X-Service-Name";
    public static final String HEADER_SERVICE_TOKEN = "X-Service-Token";

    private final ServiceAuthenticationProperties properties;

    public ServiceTokenAuthenticationFilter(ServiceAuthenticationProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String serviceName = request.getHeader(HEADER_SERVICE_NAME);
            String token = request.getHeader(HEADER_SERVICE_TOKEN);
            if (StringUtils.hasText(serviceName) || StringUtils.hasText(token)) {
                validateAndAuthenticate(request, response, serviceName, token);
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void validateAndAuthenticate(
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        String token
    ) throws IOException {
        if (!StringUtils.hasText(serviceName) || !StringUtils.hasText(token)) {
            reject(response, "Missing service authentication headers");
            return;
        }
        Map<String, String> trustedClients = properties.getTrustedClients();
        String expectedToken = trustedClients.get(serviceName);
        if (!StringUtils.hasText(expectedToken) || !expectedToken.equals(token)) {
            reject(response, "Invalid service credentials");
            return;
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            new ServicePrincipal(serviceName),
            token,
            AuthorityUtils.createAuthorityList("ROLE_SERVICE")
        );
        authentication.setDetails(request.getHeader(HttpHeaders.USER_AGENT));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}
