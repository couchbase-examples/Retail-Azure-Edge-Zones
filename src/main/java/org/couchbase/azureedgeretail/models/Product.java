package org.couchbase.azureedgeretail.models;

public class Product {
  private String id, brand, category, name, type;
  private int modelYear;
  private Double listPrice;

  public Product() {
  }

  public Product(String id, String name, String brand, String category, String type, int modelYear, Double listPrice) {
    this.id = id;
    this.name = name;
    this.brand = brand;
    this.category = category;
    this.type = type;
    this.modelYear = modelYear;
    this.listPrice = listPrice;
  }

  public Product(Product product) {
    this.id = product.getId();
    this.name = product.getName();
    this.brand = product.getBrand();
    this.category = product.getCategory();
    this.type = product.getType();
    this.modelYear = product.getModelYear();
    this.listPrice = product.getListPrice();
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
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

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
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

  public String toString() {
    return "Product: { id=" + this.id + ",name=" + this.name + ",brand=" + this.brand + ",category=" + this.category
        + ",type=" + this.type + ",modelYear=" + this.modelYear + ",listPrice=" + this.listPrice;
  }
}
