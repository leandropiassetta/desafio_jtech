package br.com.jtech.tasklist.config.infra.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(name = "J-Tech Soluções em Informática", email = "contato@jtech.com.br"),
                title = "JTech Task List API",
                description = "REST API for Task Management with CRUD operations, filtering, and pagination",
                version = "1.0.0"
        )
)
public class OpenAPI30Configuration {
    
    @Bean
    public OpenAPI customizeOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("JTech Task List API")
                        .version("1.0.0")
                        .description("REST API for Task Management with CRUD operations, filtering, and pagination")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("J-Tech Soluções em Informática")
                                .email("contato@jtech.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}