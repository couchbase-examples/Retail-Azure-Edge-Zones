package org.couchbase.azureedgeretail.runners;

import com.couchbase.client.core.error.*;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.query.QueryResult;
import org.couchbase.azureedgeretail.configs.CollectionNames;
import org.couchbase.azureedgeretail.configs.DBProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * This class run after the application startup. It automatically setup all
 * indexes needed
 */
@Component
public class DBSetupRunner implements CommandLineRunner {

  @Autowired
  private Bucket bucket;
  @Autowired
  private Cluster cluster;
  @Autowired
  private DBProperties props;

  @Override
  public void run(String... args) {
    try {
      cluster.queryIndexes().createPrimaryIndex(props.getBucketName());
    } catch (Exception e) {
      System.out.println("Primary index already exists on bucket " + props.getBucketName());
    }

    CollectionManager collectionManager = bucket.collections();
    try {
      CollectionSpec spec = CollectionSpec.create(CollectionNames.PRODUCT, bucket.defaultScope().name());
      collectionManager.createCollection(spec);
      Thread.sleep(15000);
    } catch (CollectionExistsException e) {
      System.out.println(String.format("Collection <%s> already exists", CollectionNames.PRODUCT));
    } catch (Exception e) {
      System.out.println(String.format("Generic error <%s>", e.getMessage()));
    }

    try {
      final QueryResult result = cluster.query("CREATE PRIMARY INDEX default_product_index ON " + props.getBucketName()
          + "._default." + CollectionNames.PRODUCT);
      for (JsonObject row : result.rowsAsObject()) {
        System.out.println(String.format("Index Creation Status %s", row.getObject("meta").getString("status")));
      }
      Thread.sleep(10000);
    } catch (IndexExistsException e) {
      System.out.println(String.format("Collection's primary index already exists"));
    } catch (Exception e) {
      System.out.println(String.format("General error <%s> when trying to create index ", e.getMessage()));
    }

  }
}
