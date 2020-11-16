package com.noenv.wiremongo.mapping;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class RunCommandTest extends TestBase {

  @Test
  public void testRunCommand(TestContext ctx) {
    mock.runCommand()
      .withCommand("ping", new JsonObject().put("ping", 1))
      .returns(new JsonObject().put("ok", 1));

    db.runCommand("ping", new JsonObject().put("ping", 1))
      .onSuccess(r -> ctx.assertEquals(1, r.getInteger("ok")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRunCommandFile(TestContext ctx) {
    db.runCommand("ping", new JsonObject().put("pingFile", 1))
      .onSuccess(r -> ctx.assertEquals(1, r.getInteger("ok")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testRunCommandFileError(TestContext ctx) {
    db.runCommand("ping", new JsonObject().put("pingFile", 1).put("fail", true))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testRunCommandReturnedObjectNotModified(TestContext ctx) {
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

    mock.runCommand()
      .withCommand("ping", new JsonObject().put("ping", 1))
      .returns(given);

    db.runCommand("ping", new JsonObject().put("ping", 1))
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
  public void testRunCommandFileReturnedObjectNotModified(TestContext ctx) {
    final JsonObject expected = new JsonObject().put("ok", 1);

    db.runCommand("ping", new JsonObject().put("pingFile", 1))
      .onSuccess(actual -> ctx.assertEquals(expected, actual))
      .onSuccess(actual -> {
        actual.put("ok", "replace");
        actual.put("add", "add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }
}
