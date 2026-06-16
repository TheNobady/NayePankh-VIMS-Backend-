package com.nayepankh.vims.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vimsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NayePankh VIMS API")
                        .description("""
                                Volunteer Information Management System for NayePankh Foundation.
                                
                                This REST API serves as the single source of truth for managing volunteers,
                                campaigns, and enrollments across food drives, clothing distribution,
                                health-awareness campaigns, and education initiatives.
                                
                                Consumed by an Android app (volunteer-facing) and a Java Swing desktop app (staff/admin-facing).
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("NayePankh Foundation")
                                .url("https://nayepankh.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
