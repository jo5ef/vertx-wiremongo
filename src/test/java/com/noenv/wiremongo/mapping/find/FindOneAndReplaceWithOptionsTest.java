package com.noenv.wiremongo.mapping.find;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.CompletableHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FindOneAndReplaceWithOptionsTest extends TestBase {

  @Test
  public void testFindOneAndReplaceWithOptions(TestContext ctx) {
    mock.findOneAndReplaceWithOptions()
      .inCollection("findOneAndReplaceWithOptions")
      .withQuery(new JsonObject().put("test", "testFindOneAndReplaceWithOptions"))
      .withReplace(new JsonObject().put("foo", "bar"))
      .withFindOptions(new FindOptions().setLimit(4))
      .withUpdateOptions(new UpdateOptions().setWriteOption(WriteOption.FSYNCED))
      .returns(new JsonObject().put("field1", "value1"));

    db.findOneAndReplaceWithOptions("findOneAndReplaceWithOptions",
      new JsonObject().put("test", "testFindOneAndReplaceWithOptions"),
      new JsonObject().put("foo", "bar"),
      new FindOptions().setLimit(4),
      new UpdateOptions().setWriteOption(WriteOption.FSYNCED))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
    .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndReplaceWithOptionsFile(TestContext ctx) {
    db.findOneAndReplaceWithOptions("findOneAndReplaceWithOptions",
      new JsonObject().put("test", "testFindOneAndReplaceWithOptionsFile"),
      new JsonObject().put("foo", "bar"),
      new FindOptions().setFields(new JsonObject().put("field1", -1)),
      new UpdateOptions().setReturningNewDocument(false))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
    .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndReplaceWithOptionsFileError(TestContext ctx) {
    db.findOneAndReplaceWithOptions("findOneAndReplaceWithOptions",
      new JsonObject().put("test", "testFindOneAndReplaceWithOptionsFileError"),
      new JsonObject().put("foo", "bar"),
      new FindOptions().setLimit(42),
      new UpdateOptions().setUpsert(true))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
    .onComplete(ctx.asyncAssertFailure());
  }
}
