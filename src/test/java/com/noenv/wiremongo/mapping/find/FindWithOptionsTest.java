package com.noenv.wiremongo.mapping.find;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(VertxUnitRunner.class)
public class FindWithOptionsTest extends TestBase {

  @Test
  public void testFindWithOptions(TestContext ctx) {
    mock.findWithOptions()
      .inCollection("findwithoptions")
      .withQuery(new JsonObject().put("test", "testFindWithOptions"))
      .withOptions(new FindOptions().setLimit(42))
      .returns(Arrays.asList(new JsonObject().put("field1", "value1")));

    db.findWithOptions("findwithoptions", new JsonObject().put("test", "testFindWithOptions"), new FindOptions().setLimit(42))
      .onSuccess(r -> ctx.assertEquals("value1", r.get(0).getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindWithOptionsFile(TestContext ctx) {
    db.findWithOptions("findwithoptions", new JsonObject().put("test", "testFindWithOptionsFile"), new FindOptions().setSkip(42))
      .onSuccess(r -> ctx.assertEquals("value1", r.get(0).getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindWithOptionsFileError(TestContext ctx) {
    db.findWithOptions("findwithoptions", new JsonObject().put("test", "testFindWithOptionsFileError"), new FindOptions().setSkip(42))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
