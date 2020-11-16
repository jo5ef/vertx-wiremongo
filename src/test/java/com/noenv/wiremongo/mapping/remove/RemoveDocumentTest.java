package com.noenv.wiremongo.mapping.remove;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class RemoveDocumentTest extends TestBase {

  @Test
  public void testRemoveDocument(TestContext ctx) {
    mock.removeDocument()
      .inCollection("removeDocument")
      .withQuery(new JsonObject().put("test", "testRemoveDocument"))
      .returns(new MongoClientDeleteResult(1));

    db.removeDocument("removeDocument", new JsonObject().put("test", "testRemoveDocument"))
      .onSuccess(r -> ctx.assertEquals(1L, r.getRemovedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRemoveDocumentFile(TestContext ctx) {
    db.removeDocument("removeDocument", new JsonObject().put("test", "testRemoveDocumentFile"))
      .onSuccess(r -> ctx.assertEquals(2L, r.getRemovedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRemoveDocumentFileError(TestContext ctx) {
    db.removeDocument("removeDocument", new JsonObject().put("test", "testRemoveDocumentFileError"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
