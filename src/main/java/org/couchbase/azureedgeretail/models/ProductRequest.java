package org.couchbase.azureedgeretail.models;

import java.util.UUID;

public class ProductRequest {
  private String name, brand, category;
  private int modelYear;
  private Double listPrice;

  public ProductRequest() {
  }

  public ProductRequest(String name, String brand, String category, int modelYear, Double listPrice) {
    this.name = name;
    this.brand = brand;
    this.category = category;
    this.modelYear = modelYear;
    this.listPrice = listPrice;
  }

  public Product getProduct() {
    return new Product("product:" + UUID.randomUUID().toString(), name, brand, category, "product", modelYear,
        listPrice);
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBrand() {
    return this.brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getCategory() {
    return this.category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public int getModelYear() {
    return this.modelYear;
  }

  public void setModelYear(int modelYear) {
    this.modelYear = modelYear;
  }

  public Double getListPrice() {
    return this.listPrice;
  }

  public void setListPrice(Double listPrice) {
    this.listPrice = listPrice;
  }
}
