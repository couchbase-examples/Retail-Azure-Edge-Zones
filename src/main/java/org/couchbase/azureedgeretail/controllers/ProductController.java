package org.couchbase.azureedgeretail.controllers;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.query.QueryScanConsistency;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import static org.couchbase.azureedgeretail.configs.CollectionNames.PRODUCT;

import org.couchbase.azureedgeretail.configs.DBProperties;
import org.couchbase.azureedgeretail.models.Product;
import org.couchbase.azureedgeretail.models.ProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.couchbase.client.java.query.QueryOptions.queryOptions;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

  private Cluster cluster;
  private Collection productCol;
  private DBProperties dbProperties;

  public ProductController(Cluster cluster, Bucket bucket, DBProperties dbProperties) {
    this.cluster = cluster;
    this.productCol = bucket.collection(PRODUCT);
    this.dbProperties = dbProperties;
  }

  @CrossOrigin(value = "*")
  @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create a new product from the request")
  @ApiResponses({ @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error") })
  public ResponseEntity<Product> save(@RequestBody final ProductRequest newProduct) {
    // generates an id and save the product
    Product product = newProduct.getProduct();
    productCol.insert(product.getId(), product);
    return ResponseEntity.status(HttpStatus.CREATED).body(product);
  }

  @CrossOrigin(value = "*")
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get a product by Id")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "500", description = "Error occurred in getting products") })
  public ResponseEntity<Product> getProduct(@RequestParam String id) {
    Product product = productCol.get(id).contentAs(Product.class);
    return ResponseEntity.status(HttpStatus.OK).body(product);
  }

  @CrossOrigin(value = "*")
  @PutMapping(path = "/{id}")
  @Operation(summary = "Update a product")
  @ApiResponses({ @ApiResponse(responseCode = "200", description = "Updated the product"),
      @ApiResponse(responseCode = "404", description = "product not found"),
      @ApiResponse(responseCode = "500", description = "returns internal server error") })
  public ResponseEntity<Product> update(@PathVariable("id") String id, @RequestBody Product product) {

    try {
      productCol.upsert(id, product);
      return ResponseEntity.status(HttpStatus.CREATED).body(product);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
    }
  }

  @CrossOrigin(value = "*")
  @DeleteMapping(path = "/{id}")
  @Operation(summary = "Delete a Product")
  @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error") })
  public ResponseEntity delete(@PathVariable String id) {

    try {
      productCol.remove(id.toString());
      return ResponseEntity.status(HttpStatus.OK).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
    }
  }

  @CrossOrigin(value = "*")
  @GetMapping(path = "/products/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Search for products")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Returns the list of products"),
      @ApiResponse(responseCode = "500", description = "Error occurred in getting products") })
  public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false, defaultValue = "5") int limit,
      @RequestParam(required = false, defaultValue = "0") int skip, @RequestParam String search) {

    String qryString = "SELECT p.* FROM `" + dbProperties.getBucketName() + "`.`_default`.`" + PRODUCT + "` p "
        + "WHERE p.name LIKE '%" + search + "%'" + "LIMIT " + limit + " OFFSET " + skip;
    System.out.println("Query=" + qryString);

    final List<Product> products = cluster
        .query(qryString, queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS)).rowsAs(Product.class);
    return ResponseEntity.status(HttpStatus.OK).body(products);
  }
}
