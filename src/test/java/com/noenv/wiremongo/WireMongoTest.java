package com.noenv.wiremongo;

import com.mongodb.MongoSocketOpenException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteException;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class WireMongoTest {

  private JsonObject config = new JsonObject().put("class", WireMongoClient.class.getName());
  private MongoClient db;
  private WireMongo mock;

  @Before
  public void setUp() {
    Vertx vertx = Vertx.vertx();
    db = WireMongoClient.createShared(vertx, config, null);
    mock = new WireMongo(vertx);
  }

  @After
  public void tearDown() {
    mock.clear();
  }

  @Test
  public void testDuplicate(TestContext ctx) {
    mock.insert()
      .inCollection("foobar")
      .returnsDuplicateKeyError();

    db.insert("foobar", new JsonObject())
      .onFailure(ex -> {
        ctx.assertTrue(ex instanceof MongoWriteException);
        ctx.assertEquals(11000, ((MongoWriteException) ex).getCode());
      })
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testConnectionError(TestContext ctx) {
    mock.find()
      .inCollection("foobar")
      .returnsConnectionException();

    db.find("foobar", new JsonObject())
      .onFailure(ex -> ctx.assertTrue(ex instanceof MongoSocketOpenException))
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testTimeout(TestContext ctx) {
    mock.find()
      .inCollection("foobar")
      .returnsTimeoutException();

    db.find("foobar", new JsonObject())
      .onFailure(ex -> ctx.assertTrue(ex instanceof MongoTimeoutException))
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testMatchError(TestContext ctx) {
    mock.find()
      .withQuery(j -> {
        throw new RuntimeException("intentional");
      })
      .returnsConnectionException();

    db.find("foobar", new JsonObject())
      .onFailure(ex -> ctx.assertTrue(ex.getMessage().startsWith("no mapping found")))
      .onComplete(ctx.asyncAssertFailure());
  }
}
