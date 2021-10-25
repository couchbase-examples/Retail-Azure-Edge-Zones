package org.couchbase.azureedgeretail.models;

import java.util.ArrayList;
import java.util.List;

public class ProductList {

  private List<Product> products;

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }

  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ProductList() {
    products = new ArrayList<Product>();
  }
}
