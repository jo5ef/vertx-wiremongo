package com.noenv.wiremongo.mapping.find;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FindOneAndUpdateTest extends TestBase {

  @Test
  public void testFindOneAndUpdate(TestContext ctx) {
    mock.findOneAndUpdate()
      .inCollection("findoneandupdate")
      .withQuery(new JsonObject().put("test", "testFindOneAndUpdate"))
      .withUpdate(new JsonObject().put("foo", "bar"))
      .returns(new JsonObject().put("field1", "value1"));

    db.findOneAndUpdate("findoneandupdate", new JsonObject().put("test", "testFindOneAndUpdate"), new JsonObject().put("foo", "bar"))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndUpdateFile(TestContext ctx) {
    db.findOneAndUpdate("findoneandupdate", new JsonObject().put("test", "testFindOneAndUpdateFile"), new JsonObject().put("foo", "bar"))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndUpdateFileError(TestContext ctx) {
    db.findOneAndUpdate("findoneandupdate", new JsonObject().put("test", "testFindOneAndUpdateFileError"), new JsonObject().put("foo", "bar"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testFindOneAndUpdateReturnedObjectNotModified(TestContext ctx) {
    final JsonObject given = new JsonObject()
      .put("field1", "value1")
      .put("field2", "value2")
      .put("field3", new JsonObject()
        .put("field4", "value3")
        .put("field5", "value4")
        .put("field6", new JsonArray()
          .add("value5")
          .add("value6")
        )
      );
    final JsonObject expected = given.copy();

    mock.findOneAndUpdate()
      .inCollection("findoneandupdate")
      .withQuery(new JsonObject().put("test", "testFindOneAndUpdate"))
      .withUpdate(new JsonObject().put("foo", "bar"))
      .returns(given);

    db.findOneAndUpdate("findoneandupdate", new JsonObject().put("test", "testFindOneAndUpdate"), new JsonObject().put("foo", "bar"))
      .onSuccess(actual -> ctx.assertEquals(expected, actual))
      .onSuccess(actual -> {
        actual.put("field1", "replace");
        actual.remove("field2");
        actual.put("add", "add");
        actual.getJsonObject("field3").put("field4", "replace");
        actual.getJsonObject("field3").remove("field5");
        actual.getJsonObject("field3").put("add", "add");
        actual.getJsonObject("field3").getJsonArray("field6").remove(0);
        actual.getJsonObject("field3").getJsonArray("field6").add("add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneAndUpdateFileReturnedObjectNotModified(TestContext ctx) {
    final JsonObject expected = new JsonObject().put("field1", "value1");

    db.findOneAndUpdate("findoneandupdate", new JsonObject().put("test", "testFindOneAndUpdateFile"), new JsonObject().put("foo", "bar"))
      .onSuccess(actual -> ctx.assertEquals(expected, actual))
      .onSuccess(actual -> {
        actual.put("field1", "replace");
        actual.put("add", "add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }
}
