package com.collabera.kurt.product.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApi {
    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public io.swagger.v3.oas.models.OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new io.swagger.v3.oas.models.OpenAPI()
                .info(new Info()
                        .title(appName)
                        .version(appVersion)
                        .description("This is a swagger document.")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
