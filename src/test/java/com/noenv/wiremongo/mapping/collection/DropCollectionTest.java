package com.noenv.wiremongo.mapping.collection;

import com.noenv.wiremongo.TestBase;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.CompletableHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class DropCollectionTest extends TestBase {

  @Test
  public void testDropCollection(TestContext ctx) {
    mock.dropCollection()
      .inCollection("testDropCollection")
      .returns(null);
    db.dropCollection("testDropCollection")
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testDropCollectionFile(TestContext ctx) {
    db.dropCollection("testDropCollectionFile")
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testDropCollectionFileError(TestContext ctx) {
    db.dropCollection("testDropCollectionFileError")
      .onFailure(e -> ctx.assertEquals("intentional", e.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }
}
