package com.noenv.wiremongo.mapping;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CountTest extends TestBase {

  @Test
  public void testCount(TestContext ctx) {
    mock.count()
      .inCollection("count")
      .withQuery(new JsonObject().put("test", "testCount"))
      .returns(41L);

    db.count("count", new JsonObject().put("test", "testCount"))
      .onSuccess(r -> ctx.assertEquals(41L, r))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testCountFile(TestContext ctx) {
    db.count("count", new JsonObject().put("test", "testCountFile"))
      .onSuccess(r -> ctx.assertEquals(26L, r))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testCountError(TestContext ctx) {
    db.count("count", new JsonObject().put("test", "testCountFileError"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testMultipleReturns(TestContext ctx) {
    mock.count()
      .inCollection("double")
      .withQuery(new JsonObject().put("test", "next"))
      .returns(10L)
      .returns(20L);

    db.count("double", new JsonObject().put("test", "next"))
      .flatMap(first -> {
        ctx.assertEquals(10L, first);
        return db.count("double", new JsonObject().put("test", "next"));
      })
      .flatMap(next -> {
        ctx.assertEquals(20L, next);
        return db.count("double", new JsonObject().put("test", "next"));
      })
      .onSuccess(next -> ctx.assertEquals(20L, next))
      .onComplete(ctx.asyncAssertSuccess());
  }
}
