package com.nemo.webHub.Onion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${spring.application.version}")
    private String applicationVersion;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title(applicationName)
            .version(applicationVersion);
        return new OpenAPI()
            .info(info);
    }

}
