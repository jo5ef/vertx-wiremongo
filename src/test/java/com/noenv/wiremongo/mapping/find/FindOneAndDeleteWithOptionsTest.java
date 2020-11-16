package com.noenv.wiremongo.mapping.find;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FindOneAndDeleteWithOptionsTest extends TestBase {

  @Test
  public void testFindOneAndDeleteWithOptions(TestContext ctx) {
    mock.findOneAndDeleteWithOptions()
      .inCollection("findOneAndDeleteWithOptions")
      .withQuery(new JsonObject().put("test", "testFindOneAndDeleteWithOptions"))
      .withOptions(new FindOptions().setSkip(5).setLimit(21))
      .returns(new JsonObject().put("field1", "value1"));

    db.findOneAndDeleteWithOptions("findOneAndDeleteWithOptions",
      new JsonObject().put("test", "testFindOneAndDeleteWithOptions"),
      new FindOptions().setSkip(5).setLimit(21))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndDeleteWithOptionsFile(TestContext ctx) {
    db.findOneAndDeleteWithOptions("findOneAndDeleteWithOptions",
      new JsonObject().put("test", "testFindOneAndDeleteWithOptionsFile"),
      new FindOptions().setBatchSize(13).setSort(new JsonObject().put("field1", -1)))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndDeleteWithOptionsFileError(TestContext ctx) {
    db.findOneAndDeleteWithOptions("findOneAndDeleteWithOptions",
      new JsonObject().put("test", "testFindOneAndDeleteWithOptionsFileError"),
      new FindOptions().setFields(new JsonObject().put("fieldx", 1).put("fieldy", 1)).setLimit(42))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
