package com.noenv.wiremongo.mapping.save;

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
public class SaveWithOptionsTest extends TestBase {

  @Test
  public void testSaveWithOptions(TestContext ctx) {
    mock.saveWithOptions()
      .inCollection("saveWithOptions")
      .withDocument(equalToJson(new JsonObject().put("test", "testSaveWithOptions"), true))
      .withOptions(WriteOption.JOURNALED)
      .returns("5c45f450c29de454289c5705");

    db.saveWithOptions("saveWithOptions", new JsonObject()
      .put("test", "testSaveWithOptions")
      .put("createdAt", Instant.now()), WriteOption.JOURNALED)
      .onSuccess(r -> ctx.assertEquals("5c45f450c29de454289c5705", r))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testSaveWithOptionsFile(TestContext ctx) {
    db.saveWithOptions("saveWithOptions", new JsonObject()
      .put("test", "testSaveWithOptionsFile")
      .put("createdAt", Instant.now()), WriteOption.ACKNOWLEDGED)
      .onSuccess(r -> ctx.assertEquals("5c45f450c29de454289c5706", r))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testSaveWithOptionsFileError(TestContext ctx) {
    db.saveWithOptions("saveWithOptions", new JsonObject()
      .put("test", "testSaveWithOptionsFileError")
      .put("createdAt", Instant.now()), WriteOption.FSYNCED)
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
