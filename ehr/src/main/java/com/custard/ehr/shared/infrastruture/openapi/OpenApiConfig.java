package com.custard.ehr.shared.infrastruture.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ehrOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Custard EHR API")
                        .version("1.0.0")
                        .description("""
                                Modular EHR backend API for patient registration, encounters,
                                vitals, consultations, prescriptions, pharmacy, laboratory,
                                payment, audit, and user management.
                                """)
                        .contact(new Contact()
                                .name("Custard EHR Team")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development")
                ));
    }
}