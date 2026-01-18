package com.bazario.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bazarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bazario API")
                        .description("E-commerce platform API documentation")
                        .version("v1.0"));
    }
}

