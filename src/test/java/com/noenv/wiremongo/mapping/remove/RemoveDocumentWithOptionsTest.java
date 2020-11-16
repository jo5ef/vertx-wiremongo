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
public class RemoveDocumentWithOptionsTest extends TestBase {

  @Test
  public void testRemoveDocumentWithOptions(TestContext ctx) {
    mock.removeDocumentWithOptions()
      .inCollection("removeDocumentWithOptions")
      .withQuery(new JsonObject().put("test", "testRemoveDocumentWithOptions"))
      .withOptions(WriteOption.MAJORITY)
      .returns(new MongoClientDeleteResult(1));

    db.removeDocumentWithOptions("removeDocumentWithOptions", new JsonObject()
      .put("test", "testRemoveDocumentWithOptions"), WriteOption.MAJORITY)
      .onSuccess(r -> ctx.assertEquals(1L, r.getRemovedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRemoveDocumentWithOptionsFile(TestContext ctx) {
    db.removeDocumentWithOptions("removeDocumentWithOptions", new JsonObject()
      .put("test", "testRemoveDocumentWithOptionsFile"), WriteOption.REPLICA_ACKNOWLEDGED)
      .onSuccess(r -> ctx.assertEquals(2L, r.getRemovedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRemoveDocumentWithOptionsFileError(TestContext ctx) {
    db.removeDocumentWithOptions("removeDocumentWithOptions", new JsonObject()
      .put("test", "testRemoveDocumentWithOptionsFileError"), WriteOption.UNACKNOWLEDGED)
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
