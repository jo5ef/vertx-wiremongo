package com.noenv.wiremongo.mapping.remove;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class RemoveDocumentsTest extends TestBase {

  @Test
  public void testRemoveDocuments(TestContext ctx) {
    mock.removeDocuments()
      .inCollection("removeDocuments")
      .withQuery(new JsonObject().put("test", "testRemoveDocuments"))
      .returns(new MongoClientDeleteResult(1));

    db.removeDocuments("removeDocuments", new JsonObject().put("test", "testRemoveDocuments"))
      .onSuccess(r -> ctx.assertEquals(1L, r.getRemovedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRemoveDocumentsFile(TestContext ctx) {
    db.removeDocuments("removeDocuments", new JsonObject().put("test", "testRemoveDocumentsFile"))
      .onSuccess(r -> ctx.assertEquals(2L, r.getRemovedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRemoveDocumentsFileError(TestContext ctx) {
    db.removeDocuments("removeDocuments", new JsonObject().put("test", "testRemoveDocumentsFileError"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
