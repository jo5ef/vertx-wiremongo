package com.noenv.wiremongo.mapping.find;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FindOneAndReplaceTest extends TestBase {

  @Test
  public void testFindOneAndReplace(TestContext ctx) {
    mock.findOneAndReplace()
      .inCollection("findoneandreplace")
      .withQuery(new JsonObject().put("test", "testFindOneAndReplace"))
      .withReplace(new JsonObject().put("foo", "bar"))
      .returns(new JsonObject().put("field1", "value1"));

    db.findOneAndReplace("findoneandreplace", new JsonObject().put("test", "testFindOneAndReplace"), new JsonObject().put("foo", "bar"))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndReplaceFile(TestContext ctx) {
    db.findOneAndReplace("findoneandreplace", new JsonObject().put("test", "testFindOneAndReplaceFile"), new JsonObject().put("foo", "bar"))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndReplaceFileError(TestContext ctx) {
    db.findOneAndReplace("findoneandreplace", new JsonObject().put("test", "testFindOneAndReplaceFileError"), new JsonObject().put("foo", "bar"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
