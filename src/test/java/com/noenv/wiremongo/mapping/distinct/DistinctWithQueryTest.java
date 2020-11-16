package com.noenv.wiremongo.mapping.distinct;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class DistinctWithQueryTest extends TestBase {

  @Test
  public void testDistinctWithQuery(TestContext ctx) {
    mock.distinctWithQuery()
      .inCollection("distinctWithQuery")
      .withFieldName("testDistinctWithQuery")
      .withQuery(new JsonObject().put("foo", "bar"))
      .returns(new JsonArray().add("A").add("B"));

    db.distinctWithQuery("distinctWithQuery", "testDistinctWithQuery",
      null, new JsonObject().put("foo", "bar"))
      .onSuccess(r -> {
        ctx.assertEquals(2, r.size());
        ctx.assertEquals("A", r.getString(0));
        ctx.assertEquals("B", r.getString(1));
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testDistinctWithQueryFile(TestContext ctx) {
    db.distinctWithQuery("distinctWithQuery", "testDistinctWithQueryFile",
      "java.lang.String", new JsonObject().put("foo", "bar"))
      .onSuccess(r -> {
        ctx.assertEquals(3, r.size());
        ctx.assertEquals("A", r.getString(0));
        ctx.assertEquals("B", r.getString(1));
        ctx.assertEquals("C", r.getString(2));
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testDistinctWithQueryFileError(TestContext ctx) {
    db.distinctWithQuery("distinctWithQuery", "testDistinctWithQueryFileError",
      "java.lang.Integer", new JsonObject().put("foo", "bar"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testDistinctWithQueryReturnedObjectNotModified(TestContext ctx) {
    final JsonArray given = new JsonArray().add("value1").add("value2");
    final JsonArray expected = given.copy();

    mock.distinctWithQuery()
      .inCollection("distinctWithQuery")
      .withFieldName("testDistinctWithQuery")
      .withQuery(new JsonObject().put("foo", "bar"))
      .returns(given);

    db.distinctWithQuery("distinctWithQuery", "testDistinctWithQuery", null, new JsonObject().put("foo", "bar"))
      .onSuccess(actual -> ctx.assertEquals(expected, actual))
      .onSuccess(actual -> {
        actual.remove(0);
        actual.add("add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testDistinctWithQueryFileReturnedObjectNotModified(TestContext ctx) {
    final JsonArray expected = new JsonArray().add("A").add("B").add("C");

    db.distinctWithQuery("distinctWithQuery", "testDistinctWithQueryFile", "java.lang.String", new JsonObject().put("foo", "bar"))
      .onSuccess(actual -> ctx.assertEquals(expected, actual))
      .onSuccess(actual -> {
        actual.remove(0);
        actual.add("add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }
}
