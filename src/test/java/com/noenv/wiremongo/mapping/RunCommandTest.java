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
}
