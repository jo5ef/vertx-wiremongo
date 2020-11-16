package com.noenv.wiremongo.mapping.index;

import com.noenv.wiremongo.TestBase;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.CompletableHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class DropIndexTest extends TestBase {

  @Test
  public void testDropIndex(TestContext ctx) {
    mock.dropIndex()
      .inCollection("dropIndex")
      .withName("testDropIndex")
      .returns(null);
    db.dropIndex("dropIndex", "testDropIndex")
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testDropIndexFile(TestContext ctx) {
    db.dropIndex("dropIndex", "testDropIndexFile")
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testDropIndexFileError(TestContext ctx) {
    db.dropIndex("dropIndex", "testDropIndexFileError")
      .onComplete(ctx.asyncAssertFailure());
  }
}
