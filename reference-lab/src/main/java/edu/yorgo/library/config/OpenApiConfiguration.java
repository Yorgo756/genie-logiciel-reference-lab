package edu.yorgo.library.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Library Management API",
        version = "1.0",
        description = "Agentic Development Reference Lab REST API"))
public class OpenApiConfiguration {
}
