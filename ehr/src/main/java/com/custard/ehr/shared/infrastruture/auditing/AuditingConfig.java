package com.custard.ehr.shared.infrastruture.auditing;

import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return new SpringSecurityAuditorAware();
    }
}