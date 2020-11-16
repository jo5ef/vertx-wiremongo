package com.noenv.wiremongo.mapping.save;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

import static com.noenv.wiremongo.matching.JsonMatcher.equalToJson;

@RunWith(VertxUnitRunner.class)
public class SaveTest extends TestBase {

  @Test
  public void testSave(TestContext ctx) {
    mock.save()
      .inCollection("save")
      .withDocument(equalToJson(new JsonObject().put("test", "testInsert"), true))
      .returns("5c45f450c29de454289c5705");

    db.save("save", new JsonObject()
      .put("test", "testInsert")
      .put("createdAt", Instant.now()))
      .onSuccess(r -> ctx.assertEquals("5c45f450c29de454289c5705", r))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testSaveWithId(TestContext ctx) {
    mock.save()
      .inCollection("save")
      .withDocument(equalToJson(new JsonObject()
        .put("_id", "testId")
        .put("test", "testInsert"), true))
      .returns("5c45f450c29de454289c5721");

    db.save("save", new JsonObject()
      .put("_id", "testId")
      .put("test", "testInsert")
      .put("createdAt", Instant.now()))
      .onSuccess(r -> ctx.assertEquals("5c45f450c29de454289c5721", r))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testSaveFile(TestContext ctx) {
    db.save("save", new JsonObject()
      .put("test", "testSaveFile")
      .put("createdAt", Instant.now()))
      .onSuccess(r -> ctx.assertEquals("5c45f450c29de454289c5706", r))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testSaveFileError(TestContext ctx) {
    db.save("save", new JsonObject()
      .put("test", "testSaveFileError")
      .put("createdAt", Instant.now()))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
