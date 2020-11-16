package com.noenv.wiremongo.mapping.update;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.CompletableHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.noenv.wiremongo.matching.JsonMatcher.equalToJson;

@RunWith(VertxUnitRunner.class)
public class UpdateCollectionWithOptionsTest extends TestBase {

  @Test
  public void testUpdateCollectionWithOptions(TestContext ctx) {
    mock.updateCollectionWithOptions()
      .inCollection("updatecollectionwithoptions")
      .withQuery(new JsonObject().put("test", "testUpdateCollectionWithOptions"))
      .withUpdate(equalToJson(new JsonObject().put("foo", "bar")))
      .withOptions(equalToJson(new JsonObject().put("upsert", true), UpdateOptions::toJson))
      .returns(new MongoClientUpdateResult(22, null, 24));

    db.updateCollectionWithOptions("updatecollectionwithoptions",
      new JsonObject().put("test", "testUpdateCollectionWithOptions"),
      new JsonObject().put("foo", "bar"),
      new UpdateOptions().setUpsert(true))
      .onSuccess(r -> {
        ctx.assertEquals(22L, r.getDocMatched());
        ctx.assertEquals(24L, r.getDocModified());
      })
    .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testUpdateCollectionWithOptionsFile(TestContext ctx) {
    db.updateCollectionWithOptions("updatecollectionwithoptions",
      new JsonObject().put("test", "testUpdateCollectionWithOptionsFile"),
      new JsonObject().put("foo", "bar"),
      new UpdateOptions().setMulti(true))
      .onSuccess(r -> {
        ctx.assertEquals(46L, r.getDocMatched());
        ctx.assertEquals(17L, r.getDocModified());
      })
    .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testUpdateCollectionWithOptionsFileError(TestContext ctx) {
    db.updateCollectionWithOptions("updatecollectionwithoptions",
      new JsonObject().put("test", "testUpdateCollectionWithOptionsFileError"),
      new JsonObject().put("foo", "bar"),
      new UpdateOptions().setMulti(true))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
    .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testUpdateCollectionWithOptionsReturnedObjectNotModified(TestContext ctx) {
    final MongoClientUpdateResult given = new MongoClientUpdateResult(17, new JsonObject()
      .put("field1", "value1")
      .put("field2", "value2")
      .put("field3", new JsonObject()
        .put("field4", "value3")
        .put("field5", "value4")
        .put("field6", new JsonArray()
          .add("value5")
          .add("value6")
        )
      ), 24);
    final MongoClientUpdateResult expected = new MongoClientUpdateResult(given.toJson().copy());

    mock.updateCollectionWithOptions()
      .inCollection("updatecollectionwithoptions")
      .withQuery(new JsonObject().put("test", "testUpdateCollectionWithOptions"))
      .withUpdate(equalToJson(new JsonObject().put("foo", "bar")))
      .withOptions(equalToJson(new JsonObject().put("upsert", true), UpdateOptions::toJson))
      .returns(given);

    db.updateCollectionWithOptions("updatecollectionwithoptions",
      new JsonObject().put("test", "testUpdateCollectionWithOptions"),
      new JsonObject().put("foo", "bar"),
      new UpdateOptions().setUpsert(true))
      .onSuccess(actual -> ctx.assertEquals(expected.toJson(), actual.toJson()))
      .onSuccess(actual -> {
        actual.getDocUpsertedId().put("field1", "replace");
        actual.getDocUpsertedId().remove("field2");
        actual.getDocUpsertedId().put("add", "add");
        actual.getDocUpsertedId().getJsonObject("field3").put("field4", "replace");
        actual.getDocUpsertedId().getJsonObject("field3").remove("field5");
        actual.getDocUpsertedId().getJsonObject("field3").put("add", "add");
        actual.getDocUpsertedId().getJsonObject("field3").getJsonArray("field6").remove(0);
        actual.getDocUpsertedId().getJsonObject("field3").getJsonArray("field6").add("add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testUpdateCollectionWithOptionsFileReturnedObjectNotModified(TestContext ctx) {
    final MongoClientUpdateResult expected = new MongoClientUpdateResult(46, new JsonObject().put("field1", "value1"), 17);

    db.updateCollectionWithOptions("updatecollectionwithoptions",
      new JsonObject().put("test", "testUpdateCollectionWithOptionsFile"),
      new JsonObject().put("foo", "bar"),
      new UpdateOptions().setMulti(true))
      .onSuccess(actual -> ctx.assertEquals(expected.toJson(), actual.toJson()))
      .onSuccess(actual -> {
        actual.getDocUpsertedId().put("field1", "replace");
        actual.getDocUpsertedId().put("add", "add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }
}
