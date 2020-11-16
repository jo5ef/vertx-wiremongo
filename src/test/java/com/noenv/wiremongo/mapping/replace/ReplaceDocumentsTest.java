package com.noenv.wiremongo.mapping.replace;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ReplaceDocumentsTest extends TestBase {

  @Test
  public void testReplaceDocuments(TestContext ctx) {
    mock.replaceDocuments()
      .inCollection("replaceDocuments")
      .withQuery(new JsonObject().put("test", "testReplaceDocuments"))
      .withReplace(new JsonObject().put("foo", "bar"))
      .returns(new MongoClientUpdateResult(17, null, 24));

    db.replaceDocuments("replaceDocuments", new JsonObject().put("test", "testReplaceDocuments"),
      new JsonObject().put("foo", "bar"))
      .onSuccess(r -> {
        ctx.assertEquals(17L, r.getDocMatched());
        ctx.assertEquals(24L, r.getDocModified());
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testReplaceDocumentsFile(TestContext ctx) {
    db.replaceDocuments("replaceDocuments", new JsonObject().put("test", "testReplaceDocumentsFile"),
      new JsonObject().put("foo", "bar"))
      .onSuccess(r -> {
        ctx.assertEquals(21L, r.getDocMatched());
        ctx.assertEquals(56L, r.getDocModified());
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testReplaceDocumentsFileError(TestContext ctx) {
    db.replaceDocuments("replaceDocuments", new JsonObject().put("test", "testReplaceDocumentsFileError"),
      new JsonObject().put("foo", "bar"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
