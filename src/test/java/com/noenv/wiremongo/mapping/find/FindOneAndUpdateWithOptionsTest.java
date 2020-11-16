package com.noenv.wiremongo.mapping.find;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FindOneAndUpdateWithOptionsTest extends TestBase {

  @Test
  public void testFindOneAndUpdateWithOptions(TestContext ctx) {
    mock.findOneAndUpdateWithOptions()
      .inCollection("findoneandupdatewithoptions")
      .withQuery(new JsonObject().put("test", "testFindOneAndUpdateWithOptions"))
      .withFindOptions(new FindOptions().setSkip(42))
      .withUpdateOptions(new UpdateOptions().setReturningNewDocument(true))
      .withUpdate(new JsonObject().put("foo", "bar"))
      .returns(new JsonObject().put("field1", "value1"));

    db.findOneAndUpdateWithOptions("findoneandupdatewithoptions",
      new JsonObject().put("test", "testFindOneAndUpdateWithOptions"),
      new JsonObject().put("foo", "bar"),
      new FindOptions().setSkip(42),
      new UpdateOptions().setReturningNewDocument(true))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndUpdateWithOptionsFile(TestContext ctx) {
    db.findOneAndUpdateWithOptions("findoneandupdatewithoptions",
      new JsonObject().put("test", "testFindOneAndUpdateWithOptionsFile"),
      new JsonObject().put("foo", "bar"),
      new FindOptions().setSkip(42),
      new UpdateOptions().setMulti(true))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndUpdateWithOptionsFileError(TestContext ctx) {
    db.findOneAndUpdateWithOptions("findoneandupdatewithoptions",
      new JsonObject().put("test", "testFindOneAndUpdateWithOptionsError"),
      new JsonObject().put("foo", "bar"),
      new FindOptions().setSkip(42),
      new UpdateOptions().setMulti(true))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
