package com.ecomarket.venta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class VentaConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}