package com.storebase.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI storebaseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StoreBase API")
                        .description("API REST do sistema de gerenciamento de loja StoreBase")
                        .version("1.0.0"));
    }
}
