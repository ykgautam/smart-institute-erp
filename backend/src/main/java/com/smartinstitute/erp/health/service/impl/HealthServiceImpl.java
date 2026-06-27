package com.smartinstitute.erp.health.service.impl;

import com.smartinstitute.erp.health.dto.HealthResponse;
import com.smartinstitute.erp.health.service.HealthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HealthServiceImpl implements HealthService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${application.version}")
    private String version;

    @Value("${spring.profiles.active:default}")
    private String environment;

    @Override
    public HealthResponse getHealth() {

        return HealthResponse.builder()
                .application(applicationName)
                .version(version)
                .environment(environment)
                .status("UP")
                .serverTime(LocalDateTime.now())
                .build();
    }

}