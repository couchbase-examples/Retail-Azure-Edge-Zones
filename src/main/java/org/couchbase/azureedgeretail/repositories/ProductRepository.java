package org.couchbase.azureedgeretail.repositories;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static org.couchbase.azureedgeretail.configs.CollectionNames.PRODUCT;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryScanConsistency;
import java.util.List;
import org.couchbase.azureedgeretail.configs.DBProperties;
import org.couchbase.azureedgeretail.models.Product;
import org.springframework.stereotype.Repository;

/**
 * Owns every Couchbase read and write for products: which collection they live
 * in, how the search query is written, and how its inputs are bound.
 *
 * <p>Callers pass plain values. User input reaches N1QL only as named
 * parameters, never as part of the statement text.
 */
@Repository
public class ProductRepository {

  private final Cluster cluster;
  private final Collection collection;
  private final String searchStatement;

  public ProductRepository(Cluster cluster, Bucket bucket, DBProperties dbProperties) {
    this.cluster = cluster;
    this.collection = bucket.collection(PRODUCT);
    this.searchStatement = "SELECT p.* FROM `" + dbProperties.getBucketName() + "`.`_default`.`" + PRODUCT + "` p "
        + "WHERE p.name LIKE $pattern LIMIT $limit OFFSET $skip";
  }

  public Product insert(Product product) {
    collection.insert(product.getId(), product);
    return product;
  }

  public Product findById(String id) {
    return collection.get(id).contentAs(Product.class);
  }

  public Product upsert(String id, Product product) {
    collection.upsert(id, product);
    return product;
  }

  public void deleteById(String id) {
    collection.remove(id);
  }

  /** Products whose name contains {@code search}, paged by {@code limit}/{@code skip}. */
  public List<Product> searchByName(String search, int limit, int skip) {
    JsonObject params = JsonObject.create().put("pattern", "%" + search + "%").put("limit", limit).put("skip", skip);
    return cluster.query(searchStatement,
        queryOptions().parameters(params).scanConsistency(QueryScanConsistency.REQUEST_PLUS)).rowsAs(Product.class);
  }
}
