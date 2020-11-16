package com.noenv.wiremongo.mapping.find;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RunWith(VertxUnitRunner.class)
public class FindTest extends TestBase {

  @Test
  public void testFind(TestContext ctx) {
    mock.find()
      .inCollection("find")
      .withQuery(new JsonObject().put("test", "testFind"))
      .returns(Arrays.asList(new JsonObject().put("field1", "value1")));

    db.find("find", new JsonObject().put("test", "testFind"))
      .onSuccess(r -> ctx.assertEquals("value1", r.get(0).getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindFile(TestContext ctx) {
    db.find("find", new JsonObject().put("test", "testFindFile"))
      .onSuccess(r -> ctx.assertEquals("value1", r.get(0).getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindFileError(TestContext ctx) {
    db.find("find", new JsonObject().put("test", "testFindFileError"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
