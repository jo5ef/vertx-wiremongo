package com.noenv.wiremongo.mapping.index;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.IndexOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CreateIndexWithOptionsTest extends TestBase {

  @Test
  public void testCreateIndex(TestContext ctx) {


    mock.createIndexWithOptions()
      .inCollection("createindexwithoptions")
      .withOptions(new IndexOptions().background(false).unique(true).sparse(false))
      .withKey(new JsonObject().put("test", "testCreateIndexWithOptions"))
      .returns(null);

    db.createIndexWithOptions("createindexwithoptions",
      new JsonObject().put("test", "testCreateIndexWithOptions"),
      new IndexOptions().background(false).unique(true).sparse(false))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testCreateIndexFile(TestContext ctx) {

    db.createIndexWithOptions("createindexwithoptions", new JsonObject().put("test", "testCreateIndexWithOptionsFile"),
      new IndexOptions().name("testIndex"))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testCreateIndexFileError(TestContext ctx) {

    db.createIndexWithOptions("createindexwithoptions", new JsonObject().put("test", "testCreateIndexWithOptionsFileError"),
      new IndexOptions().background(false).unique(false).sparse(false).name("testIndexError"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
