package org.couchbase.azureedgeretail.product;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import org.couchbase.azureedgeretail.configs.CollectionNames;
import org.couchbase.azureedgeretail.configs.DBProperties;
import org.couchbase.azureedgeretail.models.Product;
import org.couchbase.azureedgeretail.models.ProductRequest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import org.mindrot.jbcrypt.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ProductTest {

  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private Cluster cluster;
  @Autowired
  private Bucket bucket;
  @Autowired
  private DBProperties prop;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Before
  public void cleanDB() {
    cluster.query("DELETE FROM " + prop.getBucketName() + "._default._default");
  }

  @Test
  public void testProductNotFound() {

    this.webTestClient.get().uri("/api/v1/product?limit=5&skip=0&searchname=Chair").accept(MediaType.APPLICATION_JSON)
        .exchange().expectStatus().is4xxClientError().expectHeader().contentType(MediaType.APPLICATION_JSON);
  }

  @Test
  public void testCreateProduct() {
    // test data
    ProductRequest createTestProduct = getCreateTestProduct();
    String json = getCreatedProductJson(createTestProduct);

    // run the post test
    EntityExchangeResult<Product> productResult = this.webTestClient.post().uri("/api/v1/product/").bodyValue(json)
        .accept(MediaType.APPLICATION_JSON).header("Content-Type", "application/json; charset=utf-8").exchange()
        .expectStatus().isCreated().expectBody(Product.class).returnResult();

    Product result = bucket.collection(CollectionNames.PRODUCT).get(productResult.getResponseBody().getId())
        .contentAs(Product.class);

    assertEquals(result.getName(), createTestProduct.getName());
    assertEquals(result.getBrand(), createTestProduct.getBrand());
    assertEquals(result.getCategory(), createTestProduct.getCategory());
    assertEquals(result.getType(), "product");
    assertEquals(result.getModelYear(), createTestProduct.getModelYear());
    assertEquals(result.getListPrice(), createTestProduct.getListPrice());
    assertNotNull(result.getId());
  }

  @Test
  public void testListProductSuccess() {

    // test data
    Product testProduct = getTestProduct();
    bucket.collection(CollectionNames.PRODUCT).insert(testProduct.getId(), testProduct);

    EntityExchangeResult<List<Product>> productListResult = this.webTestClient.get()
        .uri("/api/v1/product/products/?limit=5&skip=0&search=Tre").accept(MediaType.APPLICATION_JSON).exchange()
        .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(Product.class)
        .returnResult();

    MatcherAssert.assertThat(productListResult.getResponseBody(), Matchers.hasSize(1));
    Product result = productListResult.getResponseBody().get(0);
    System.out.println(result);
    assertEquals(result.getName(), testProduct.getName());
    assertEquals(result.getBrand(), testProduct.getBrand());
    assertEquals(result.getCategory(), testProduct.getCategory());
    assertEquals(result.getType(), testProduct.getType());
    assertEquals(result.getModelYear(), testProduct.getModelYear());
    assertEquals(result.getListPrice(), testProduct.getListPrice());
    assertNotNull(result.getId());
  }

  @Test
  public void testListProductsNoResult() {

    // test data
    Product testProduct = getTestProduct();
    bucket.collection(CollectionNames.PRODUCT).insert(testProduct.getId(), testProduct);

    EntityExchangeResult<List<Product>> productListResult = this.webTestClient.get()
        .uri("/api/v1/product/products/?limit=5&skip=0&search=Toaster").accept(MediaType.APPLICATION_JSON).exchange()
        .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(Product.class)
        .returnResult();

    MatcherAssert.assertThat(productListResult.getResponseBody(), Matchers.hasSize(0));
  }

  @Test
  public void testDeleteProduct() {

    exceptionRule.expect(DocumentNotFoundException.class);
    exceptionRule.expectMessage("Document with the given id not found");

    // test data
    Product testProduct = getTestProduct();
    bucket.collection(CollectionNames.PRODUCT).insert(testProduct.getId(), testProduct);

    // delete the product
    this.webTestClient.delete().uri(String.format("/api/v1/product/%s", testProduct.getId()))
        .accept(MediaType.APPLICATION_JSON).header("Content-Type", "application/json; charset=utf-8").exchange()
        .expectStatus().isOk();

    bucket.collection(CollectionNames.PRODUCT).get(testProduct.getId());
  }

  private String getCreatedProductJson(ProductRequest product) {
    // create json to post to integration test
    return JsonObject.create().put("name", product.getName()).put("brand", product.getBrand())
        .put("category", product.getCategory()).put("type", "product").put("modelYear", product.getModelYear())
        .put("listPrice", product.getListPrice()).toString();
  }

  private ProductRequest getCreateTestProduct() {
    return new ProductRequest("Trek Bike", "Trek", "Road Bikes", 2021, 1500.00);
  }

  private Product getTestProduct() {
    return new Product(UUID.randomUUID().toString(), "Trek Bike", "Trek", "Road Bikes", "product", 2021, 1500.00);
  }
}