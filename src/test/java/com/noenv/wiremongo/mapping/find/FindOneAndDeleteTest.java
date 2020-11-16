package com.noenv.wiremongo.mapping.find;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FindOneAndDeleteTest extends TestBase {

  @Test
  public void testFindOneAndDelete(TestContext ctx) {
    mock.findOneAndDelete()
      .inCollection("findOneAndDelete")
      .withQuery(new JsonObject().put("test", "testFindOneAndDelete"))
      .returns(new JsonObject().put("field1", "value1"));

    db.findOneAndDelete("findOneAndDelete", new JsonObject().put("test", "testFindOneAndDelete"))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndDeleteFile(TestContext ctx) {
    db.findOneAndDelete("findOneAndDelete", new JsonObject().put("test", "testFindOneAndDeleteFile"))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndDeleteFileError(TestContext ctx) {
    db.findOneAndDelete("findOneAndDelete", new JsonObject().put("test", "testFindOneAndDeleteFileError"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
