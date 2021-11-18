package org.couchbase.azureedgeretail.configs;

import org.couchbase.azureedgeretail.controllers.ProductController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;

@Configuration
public class Swagger {
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.basePackage(ProductController.class.getPackage().getName()))
        .paths(PathSelectors.any()).build();
  }
}
