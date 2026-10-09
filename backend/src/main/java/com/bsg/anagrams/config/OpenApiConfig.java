package com.bsg.anagrams.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BSG Anagrams API")
                        .description("REST API for the BSG Anagrams assignment — word management and anagram analysis against a dictionary.")
                        .version("1.0.0")
                        .contact(new Contact().name("BSG Selectathon")));
    }
}
