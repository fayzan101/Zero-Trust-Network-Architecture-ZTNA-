package com.yourname.zerotrust.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ZTNA Simulator API")
                        .description("""
                                Zero Trust Network Access simulator backend.

                                Modules: Authentication (JWT + TOTP MFA), Users & Roles, Device Trust,
                                Risk Engine (weighted scoring with reasons), Policy Engine, Session Monitoring,
                                Attack Simulation, Audit Logs, and Comparison Metrics.

                                Demo credentials (seeded on startup): admin/Admin123! demo/Demo123!
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("ZTNA Simulator")))
                .tags(List.of(
                        new Tag().name("Authentication").description("Register, login, MFA, tokens"),
                        new Tag().name("Users").description("User management (admin)"),
                        new Tag().name("Roles").description("Role management (admin)"),
                        new Tag().name("Devices").description("Device trust and registration"),
                        new Tag().name("Risk").description("Risk scoring and history"),
                        new Tag().name("Policies").description("Policy CRUD and evaluation"),
                        new Tag().name("Monitoring").description("Sessions and anomaly detection (admin)"),
                        new Tag().name("Attack Simulation").description("Attack simulators (admin)"),
                        new Tag().name("Audit Logs").description("Security audit trail (admin)"),
                        new Tag().name("Metrics").description("Traditional vs zero-trust comparison")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components().addSecuritySchemes("Bearer",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token from /api/auth/login or /api/auth/mfa")));
    }
}
