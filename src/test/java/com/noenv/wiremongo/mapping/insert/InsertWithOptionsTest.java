package com.noenv.wiremongo.mapping.insert;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

import static com.noenv.wiremongo.matching.JsonMatcher.equalToJson;

@RunWith(VertxUnitRunner.class)
public class InsertWithOptionsTest extends TestBase {

  @Test
  public void testInsertWithOptions(TestContext ctx) {
    mock.insertWithOptions()
      .inCollection("insertWithOptions")
      .withDocument(equalToJson(new JsonObject().put("test", "testInsertWithOptions"), true))
      .withOptions(WriteOption.JOURNALED)
      .returns("5c45f450c29de454289c5705");

    db.insertWithOptions("insertWithOptions", new JsonObject()
      .put("test", "testInsertWithOptions")
      .put("createdAt", Instant.now()), WriteOption.JOURNALED)
      .onSuccess(r -> ctx.assertEquals("5c45f450c29de454289c5705", r))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testInsertWithOptionsFile(TestContext ctx) {
    db.insertWithOptions("insertWithOptions", new JsonObject()
      .put("test", "testInsertWithOptionsFile")
      .put("createdAt", Instant.now()), WriteOption.ACKNOWLEDGED)
      .onSuccess(r -> ctx.assertEquals("5c45f450c29de454289c5706", r))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testInsertWithOptionsFileError(TestContext ctx) {
    db.insertWithOptions("insertWithOptions", new JsonObject()
      .put("test", "testInsertWithOptionsFileError")
      .put("createdAt", Instant.now()), WriteOption.FSYNCED)
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
