package com.noenv.wiremongo.mapping;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MatchAllTest extends TestBase {

  @Test
  public void testMatchAll(TestContext ctx) {
    Mapping m = mock.matchAll()
      .stub(c -> {
        ctx.assertEquals("count", c.method());
        return 41L;
      });

    db.count("count", new JsonObject().put("test", "testCount"))
      .onSuccess(s -> {
        ctx.assertEquals(41L, s);
        mock.removeMapping(m);
      })
      .onComplete(ctx.asyncAssertSuccess());
  }
}
