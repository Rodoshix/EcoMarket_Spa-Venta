package com.ecomarket.venta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiVentaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Venta - EcoMarket SPA")
                        .description("Microservicio de gestión de ventas del sistema EcoMarket")
                        .version("1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );

    }
}