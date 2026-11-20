package org.couchbase.azureedgeretail.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.couchbase.azureedgeretail.models.Product;
import org.couchbase.azureedgeretail.models.ProductRequest;
import org.couchbase.azureedgeretail.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

  private final ProductRepository products;

  public ProductController(ProductRepository products) {
    this.products = products;
  }

  @CrossOrigin(value = "*")
  @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Create a new product from the request")
  @ApiResponses({ @ApiResponse(code = 201, message = "Created", response = Product.class),
      @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
  public ResponseEntity<Product> save(@RequestBody final ProductRequest newProduct) {
    // generates an id and save the product
    Product product = products.insert(newProduct.getProduct());
    return ResponseEntity.status(HttpStatus.CREATED).body(product);
  }

  @CrossOrigin(value = "*")
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Get a product by Id", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 500, message = "Error occurred in getting products", response = Error.class) })
  public ResponseEntity<Product> getProduct(@RequestParam String id) {
    Product product = products.findById(id);
    return ResponseEntity.status(HttpStatus.OK).body(product);
  }

  @CrossOrigin(value = "*")
  @PutMapping(path = "/{id}")
  @ApiOperation(value = "Update a product", response = Product.class)
  @ApiResponses({ @ApiResponse(code = 200, message = "Updated the product", response = Product.class),
      @ApiResponse(code = 404, message = "product not found", response = Error.class),
      @ApiResponse(code = 500, message = "returns internal server error", response = Error.class) })
  public ResponseEntity<Product> update(@PathVariable("id") String id, @RequestBody Product product) {

    try {
      products.upsert(id, product);
      return ResponseEntity.status(HttpStatus.CREATED).body(product);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
    }
  }

  @CrossOrigin(value = "*")
  @DeleteMapping(path = "/{id}")
  @ApiOperation(value = "Delete a Product")
  @ApiResponses({ @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
      @ApiResponse(code = 404, message = "Not Found", response = Error.class),
      @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
  public ResponseEntity delete(@PathVariable String id) {

    try {
      products.deleteById(id);
      return ResponseEntity.status(HttpStatus.OK).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
    }
  }

  @CrossOrigin(value = "*")
  @GetMapping(path = "/products/", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Search for products", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Returns the list of products"),
      @ApiResponse(code = 500, message = "Error occurred in getting products", response = Error.class) })
  public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false, defaultValue = "5") int limit,
      @RequestParam(required = false, defaultValue = "0") int skip, @RequestParam String search) {

    return ResponseEntity.status(HttpStatus.OK).body(products.searchByName(search, limit, skip));
  }
}
