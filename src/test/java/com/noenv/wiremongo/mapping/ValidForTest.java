package com.noenv.wiremongo.mapping;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ValidForTest extends TestBase {

  @Before
  public void before(TestContext ctx) {
    mock.clear();
  }

  @Test
  public void testFindOneMultipleTimesWithoutTimes(TestContext ctx) {
    mock.findOne()
      .inCollection("findone")
      .withQuery(new JsonObject().put("test", "testFindOne"))
      .withFields(new JsonObject().put("field1", 1))
      .returns(new JsonObject().put("field1", "value1"));

    db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1))
      .flatMap(x -> db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1)))
      .flatMap(x -> db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1)))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneTimes(TestContext ctx) {
    mock.findOne()
      .inCollection("findone")
      .withQuery(new JsonObject().put("test", "testFindOne"))
      .withFields(new JsonObject().put("field1", 1))
      .validFor(3)
      .returns(new JsonObject().put("field1", "value1"));

    db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1))
      .flatMap(x -> db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1)))
      .flatMap(x -> db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1)))
      .onSuccess(r -> ctx.assertEquals("value1", r.getString("field1")))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testFindOneTooOften(TestContext ctx) {
    mock.findOne()
      .inCollection("findone")
      .withQuery(new JsonObject().put("test", "testFindOne"))
      .withFields(new JsonObject().put("field1", 1))
      .validFor(2)
      .returns(new JsonObject().put("field1", "value1"));

    db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1))
      .flatMap(x -> db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1)))
      .flatMap(x -> db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1)))
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testFindOneMultipleTimesWithoutValidForRestriction(TestContext ctx) {
    mock.findOne()
      .inCollection("findone")
      .withQuery(new JsonObject().put("test", "testFindOne"))
      .withFields(new JsonObject().put("field1", 1))
      .returns(new JsonObject().put("field1", "value1"));

    db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1))
      .flatMap(x -> db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1)))
      .flatMap(x -> db.findOne("findone", new JsonObject().put("test", "testFindOne"), new JsonObject().put("field1", 1)))
      .onComplete(ctx.asyncAssertSuccess());
  }

}
