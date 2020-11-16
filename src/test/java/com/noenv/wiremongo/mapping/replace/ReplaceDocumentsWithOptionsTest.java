package com.noenv.wiremongo.mapping.replace;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ReplaceDocumentsWithOptionsTest extends TestBase {

  @Test
  public void testReplaceDocumentsWithOptions(TestContext ctx) {
    mock.replaceDocumentsWithOptions()
      .inCollection("replaceDocumentsWithOptions")
      .withQuery(new JsonObject().put("test", "testReplaceDocumentsWithOptions"))
      .withReplace(new JsonObject().put("foo", "bar"))
      .withOptions(new UpdateOptions(true, false))
      .returns(new MongoClientUpdateResult(17, null, 24));

    db.replaceDocumentsWithOptions("replaceDocumentsWithOptions",
      new JsonObject().put("test", "testReplaceDocumentsWithOptions"),
      new JsonObject().put("foo", "bar"), new UpdateOptions(true, false))
      .onSuccess(r -> {
        ctx.assertEquals(17L, r.getDocMatched());
        ctx.assertEquals(24L, r.getDocModified());
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testReplaceDocumentsWithOptionsFile(TestContext ctx) {

    db.replaceDocumentsWithOptions("replaceDocumentsWithOptions",
      new JsonObject().put("test", "testReplaceDocumentsWithOptionsFile"),
      new JsonObject().put("foo", "bar"), new UpdateOptions(false, true))
      .onSuccess(r -> {
        ctx.assertEquals(21L, r.getDocMatched());
        ctx.assertEquals(56L, r.getDocModified());
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testReplaceDocumentsWithOptionsFileError(TestContext ctx) {

    db.replaceDocumentsWithOptions("replaceDocumentsWithOptions",
      new JsonObject().put("test", "testReplaceDocumentsWithOptionsFileError"),
      new JsonObject().put("foo", "bar"), new UpdateOptions().setWriteOption(WriteOption.MAJORITY))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
