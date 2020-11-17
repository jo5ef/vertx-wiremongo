package com.noenv.wiremongo;

import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public abstract class TestBase {

  protected static WireMongoImpl mock;
  protected MongoClient db;

  @BeforeClass
  public static void setUp(TestContext ctx) {
    mock = new WireMongoImpl(Vertx.vertx());
    mock.readFileMappings("wiremongo-mocks")
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Before
  public void setUp() {
    db = mock.getClient();
  }
}
