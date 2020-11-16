package com.noenv.wiremongo.mapping.collection;

import com.noenv.wiremongo.TestBase;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CreateCollectionTest extends TestBase {

  @Test
  public void testCreateCollection(TestContext ctx) {
    mock.createCollection()
      .inCollection("createcollection")
      .returns(null);

    db.createCollection("createcollection")
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testCreateCollectionFile(TestContext ctx) {
    db.createCollection("createcollectionfile")
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testCreateCollectionFileError(TestContext ctx) {
    db.createCollection("createcollectionfileerror")
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
