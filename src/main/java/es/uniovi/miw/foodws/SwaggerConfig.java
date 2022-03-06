package es.uniovi.miw.foodws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors
                        .basePackage("es.uniovi.miw.foodws.controllers"))
                .paths(PathSelectors.any()).build().apiInfo(getApiInfo())
                .useDefaultResponseMessages(false);
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "WS Final Task", "Web Services - MIW - FoodWS", "1.0", "",
                new Contact(
                        "Enrique Fernández Manzano - Ángel Álvarez Rodríguez - Sergio Dominguez Cabrero",
                        "",
                        "uo257742@uniovi.es - uoxxxxxx@uniovi.es - uoxxxxxx@uniovi.es"
                ),
                "LICENSE", "LICENSE URL", Collections.emptyList()
        );
    }
}