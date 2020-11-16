package com.noenv.wiremongo.mapping.index;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.MaybeHelper;
import io.vertx.reactivex.SingleHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;

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

  @Test
  public void testListIndexesReturnedObjectNotModified(TestContext ctx) {
    final JsonArray given = new JsonArray()
      .add("value1")
      .add("value2")
      .add(new JsonObject()
        .put("field1", "value3")
        .put("field2", "value4")
        .put("field3", new JsonArray()
          .add("value5")
          .add("value6")
        )
      );
    final JsonArray expected = given.copy();

    mock.listIndexes()
      .inCollection("testListIndexes")
      .returns(given);

    db.listIndexes("testListIndexes")
      .onSuccess(actual -> ctx.assertEquals(expected, actual))
      .onSuccess(actual -> {
        actual.remove(0);
        actual.add("add");
        actual.getJsonObject(1).put("add", "add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testListIndexesFileReturnedObjectNotModified(TestContext ctx) {
    final JsonArray expected = new JsonArray().add(87).add(92);

    db.listIndexes("testListIndexesFile")
      .onSuccess(actual -> ctx.assertEquals(expected, actual))
      .onSuccess(actual -> {
        actual.remove(0);
        actual.add("add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }
}
