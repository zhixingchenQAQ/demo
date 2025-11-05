package com.example.common.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ServicePrincipal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String serviceName;

    public ServicePrincipal(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String toString() {
        return serviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServicePrincipal that = (ServicePrincipal) o;
        return Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName);
    }
}
