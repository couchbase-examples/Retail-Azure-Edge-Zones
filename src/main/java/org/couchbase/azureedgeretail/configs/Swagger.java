package org.couchbase.azureedgeretail.configs;

import org.couchbase.azureedgeretail.controllers.ProductController;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger {
  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder().group("default")
        .packagesToScan(ProductController.class.getPackage().getName())
        .pathsToMatch("/**").build();
  }
}
