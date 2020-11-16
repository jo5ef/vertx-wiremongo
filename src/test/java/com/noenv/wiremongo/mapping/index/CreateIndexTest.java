package com.noenv.wiremongo.mapping.index;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CreateIndexTest extends TestBase {

  @Test
  public void testCreateIndex(TestContext ctx) {
    mock.createIndex()
      .inCollection("createindex")
      .withKey(new JsonObject().put("test", "testCreateIndex"))
      .returns(null);

    db.createIndex("createindex", new JsonObject().put("test", "testCreateIndex"))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testCreateIndexFile(TestContext ctx) {
    db.createIndex("createindex", new JsonObject().put("test", "testCreateIndexFile"))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testCreateIndexFileError(TestContext ctx) {
    db.createIndex("createindex", new JsonObject().put("test", "testCreateIndexFileError"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
