package com.ballistic.fserver.config;

import com.google.common.base.Predicates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.ballistic.fserver.util.SwaggerMessage.*;


// done test 100%
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final Logger logger = LogManager.getLogger(SwaggerConfig.class);

    @Bean
    public Docket productApi() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(TITLE)
                .description(DESCRIPTION)
                .termsOfServiceUrl(TERMS_OF_SERVICEURL)
                .contact(new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL))
                .license(LICENSE)
                .licenseUrl(LICENSE_URL)
                .version(VERSION)
                .build();

    }

}
