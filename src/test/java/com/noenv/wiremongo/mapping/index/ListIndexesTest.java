package com.noenv.wiremongo.mapping.index;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ListIndexesTest extends TestBase {

  @Test
  public void testListIndexes(TestContext ctx) {
    mock.listIndexes()
      .inCollection("testListIndexes")
      .returns(new JsonArray().add(18).add(27));
    db.listIndexes("testListIndexes")
      .onSuccess(r -> {
        ctx.assertEquals(2, r.size());
        ctx.assertEquals(18, r.getInteger(0));
        ctx.assertEquals(27, r.getInteger(1));
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testListIndexesFile(TestContext ctx) {
    db.listIndexes("testListIndexesFile")
      .onSuccess(r -> {
        ctx.assertEquals(2, r.size());
        ctx.assertEquals(87, r.getInteger(0));
        ctx.assertEquals(92, r.getInteger(1));
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testListIndexesFileError(TestContext ctx) {
    db.listIndexes("testListIndexesFileError")
      .onFailure(e -> ctx.assertEquals("intentional", e.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
