package com.noenv.wiremongo.mapping.replace;

import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ReplaceDocumentsTest extends TestBase {

  @Test
  public void testReplaceDocuments(TestContext ctx) {
    mock.replaceDocuments()
      .inCollection("replaceDocuments")
      .withQuery(new JsonObject().put("test", "testReplaceDocuments"))
      .withReplace(new JsonObject().put("foo", "bar"))
      .returns(new MongoClientUpdateResult(17, null, 24));

    db.replaceDocuments("replaceDocuments", new JsonObject().put("test", "testReplaceDocuments"),
      new JsonObject().put("foo", "bar"))
      .onSuccess(r -> {
        ctx.assertEquals(17L, r.getDocMatched());
        ctx.assertEquals(24L, r.getDocModified());
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testReplaceDocumentsFile(TestContext ctx) {
    db.replaceDocuments("replaceDocuments", new JsonObject().put("test", "testReplaceDocumentsFile"),
      new JsonObject().put("foo", "bar"))
      .onSuccess(r -> {
        ctx.assertEquals(21L, r.getDocMatched());
        ctx.assertEquals(56L, r.getDocModified());
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testReplaceDocumentsFileError(TestContext ctx) {
    db.replaceDocuments("replaceDocuments", new JsonObject().put("test", "testReplaceDocumentsFileError"),
      new JsonObject().put("foo", "bar"))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testReplaceDocumentsReturnedObjectNotModified(TestContext ctx) {
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

    mock.replaceDocuments()
      .inCollection("replaceDocuments")
      .withQuery(new JsonObject().put("test", "testReplaceDocuments"))
      .withReplace(new JsonObject().put("foo", "bar"))
      .returns(given);

    db.replaceDocuments("replaceDocuments", new JsonObject().put("test", "testReplaceDocuments"),
      new JsonObject().put("foo", "bar"))
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
  public void testReplaceDocumentsFileReturnedObjectNotModified(TestContext ctx) {
    final MongoClientUpdateResult expected = new MongoClientUpdateResult(21, new JsonObject().put("field1", "value1"), 56);

    db.replaceDocuments("replaceDocuments", new JsonObject().put("test", "testReplaceDocumentsFile"), new JsonObject().put("foo", "bar"))
      .onSuccess(actual -> ctx.assertEquals(expected.toJson(), actual.toJson()))
      .onSuccess(actual -> {
        actual.getDocUpsertedId().put("field1", "replace");
        actual.getDocUpsertedId().put("add", "add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }
}
