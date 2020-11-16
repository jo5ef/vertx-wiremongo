package com.noenv.wiremongo.mapping.remove;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class RemoveDocumentsWithOptionsTest extends TestBase {

  @Test
  public void testRemoveDocumentsWithOptions(TestContext ctx) {
    mock.removeDocumentsWithOptions()
      .inCollection("removeDocumentsWithOptions")
      .withQuery(new JsonObject().put("test", "testRemoveDocumentsWithOptions"))
      .withOptions(WriteOption.FSYNCED)
      .returns(new MongoClientDeleteResult(1));

    db.removeDocumentsWithOptions("removeDocumentsWithOptions", new JsonObject()
      .put("test", "testRemoveDocumentsWithOptions"), WriteOption.FSYNCED)
      .onSuccess(r -> ctx.assertEquals(1L, r.getRemovedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRemoveDocumentsWithOptionsFile(TestContext ctx) {
    db.removeDocumentsWithOptions("removeDocumentsWithOptions", new JsonObject()
      .put("test", "testRemoveDocumentsWithOptionsFile"), WriteOption.ACKNOWLEDGED)
      .onSuccess(r -> ctx.assertEquals(2L, r.getRemovedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRemoveDocumentsWithOptionsFileError(TestContext ctx) {
    db.removeDocumentsWithOptions("removeDocumentsWithOptions", new JsonObject()
      .put("test", "testRemoveDocumentsWithOptionsFileError"), WriteOption.MAJORITY)
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
