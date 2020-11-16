package com.noenv.wiremongo.mapping.distinct;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class DistinctTest extends TestBase {

  @Test
  public void testDistinct(TestContext ctx) {
    mock.distinct()
      .inCollection("distinct")
      .withFieldName("testDistinct")
      .returns(new JsonArray().add("A").add("B"));

    db.distinct("distinct", "testDistinct", null)
      .onSuccess(r -> {
        ctx.assertEquals(2, r.size());
        ctx.assertEquals("A", r.getString(0));
        ctx.assertEquals("B", r.getString(1));
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testDistinctFile(TestContext ctx) {
    db.distinct("distinct", "testDistinctFile", "java.lang.String")
      .onSuccess(r -> {
        ctx.assertEquals(3, r.size());
        ctx.assertEquals("A", r.getString(0));
        ctx.assertEquals("B", r.getString(1));
        ctx.assertEquals("C", r.getString(2));
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testDistinctFileError(TestContext ctx) {
    db.distinct("distinct", "testDistinctFileError", "java.lang.Integer")
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
