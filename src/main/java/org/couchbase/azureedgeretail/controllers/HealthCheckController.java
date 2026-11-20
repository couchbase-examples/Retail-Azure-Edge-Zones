package org.couchbase.azureedgeretail.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthCheckController {

  @GetMapping(path = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get Service Health")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "500", description = "Error occurred in checking service health") })
  public String getServiceHealthStatus() {
    return "{ \"serviceStatus\": \"Service is Up\" }";
  }
}
