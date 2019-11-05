package com.zemiak.nasphotos;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@ApplicationScoped
public class HealthChecker implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.builder().up().build();
  }
}
