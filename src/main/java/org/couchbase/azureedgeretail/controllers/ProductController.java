package org.couchbase.azureedgeretail.controllers;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.query.QueryScanConsistency;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
  @ApiOperation(value = "Create a new product from the request")
  @ApiResponses({ @ApiResponse(code = 201, message = "Created", response = Product.class),
      @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
  public ResponseEntity<Product> save(@RequestBody final ProductRequest newProduct) {
    // generates an id and save the product
    Product product = newProduct.getProduct();
    productCol.insert(product.getId(), product);
    return ResponseEntity.status(HttpStatus.CREATED).body(product);
  }

  @CrossOrigin(value = "*")
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Get a product by Id", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 500, message = "Error occurred in getting products", response = Error.class) })
  public ResponseEntity<Product> getProduct(@RequestParam String id) {
    Product product = productCol.get(id).contentAs(Product.class);
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
      productCol.upsert(id, product);
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
      productCol.remove(id.toString());
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

    String qryString = "SELECT p.* FROM `" + dbProperties.getBucketName() + "`.`_default`.`" + PRODUCT + "` p "
        + "WHERE p.name LIKE '%" + search + "%'" + "LIMIT " + limit + " OFFSET " + skip;
    System.out.println("Query=" + qryString);

    final List<Product> products = cluster
        .query(qryString, queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS)).rowsAs(Product.class);
    return ResponseEntity.status(HttpStatus.OK).body(products);
  }
}
