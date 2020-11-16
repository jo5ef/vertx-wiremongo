package com.noenv.wiremongo.mapping.bulkwrite;

import com.mongodb.MongoBulkWriteException;
import com.noenv.wiremongo.TestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.MongoClientBulkWriteResult;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RunWith(VertxUnitRunner.class)
public class BulkWriteTest extends TestBase {

  @Test
  public void testBulkWrite(TestContext ctx) {
    mock.bulkWrite()
      .inCollection("bulkwrite")
      .withOperations(Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWrite"))))
      .returns(new MongoClientBulkWriteResult(1, 0, 0, 0, null));

    db.bulkWrite("bulkwrite", Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWrite"))))
      .onSuccess(r -> ctx.assertEquals(1L, r.getInsertedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testBulkWriteFile(TestContext ctx) {
    db.bulkWrite("bulkwrite", Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWriteFile"))))
      .onSuccess(r -> ctx.assertEquals(28L, r.getDeletedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testBulkWriteFileError(TestContext ctx) {
    db.bulkWrite("bulkwrite", Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWriteFileError"))))
      .onFailure(ex -> ctx.assertEquals("intentional", ex.getMessage()))
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testBulkWriteJsonMatcher(TestContext ctx) {
    db.bulkWrite("bulkwrite", Arrays.asList(BulkOperation.createInsert(new JsonObject()
      .put("test", "testBulkWriteFileJsonMatcher")
      .put("created", Instant.now()))))
      .onSuccess(r -> ctx.assertEquals(48L, r.getDeletedCount()))
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testBulkWriteReturnedObjectNotModified(TestContext ctx) {
    final MongoClientBulkWriteResult given = new MongoClientBulkWriteResult(1, 2, 3, 4, new ArrayList<>(Collections.singletonList(
      new JsonObject()
        .put("field1", "value1")
        .put("field2", "value2")
        .put("field3", new JsonObject()
          .put("field4", "value3")
          .put("field5", "value4")
          .put("field6", new JsonArray()
            .add("value5")
            .add("value6")
          )
        )
    )));
    final MongoClientBulkWriteResult expected = new MongoClientBulkWriteResult(given.toJson().copy());

    mock.bulkWrite()
      .inCollection("bulkwrite")
      .withOperations(Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWrite"))))
      .returns(given);

    db.bulkWrite("bulkwrite", Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWrite"))))
      .onSuccess(actual -> ctx.assertEquals(expected.toJson(), actual.toJson()))
      .onSuccess(actual -> {
        actual.getUpserts().get(0).put("field1", "replace");
        actual.getUpserts().get(0).remove("field2");
        actual.getUpserts().get(0).put("add", "add");
        actual.getUpserts().get(0).getJsonObject("field3").put("field4", "replace");
        actual.getUpserts().get(0).getJsonObject("field3").remove("field5");
        actual.getUpserts().get(0).getJsonObject("field3").put("add", "add");
        actual.getUpserts().get(0).getJsonObject("field3").getJsonArray("field6").remove(0);
        actual.getUpserts().get(0).getJsonObject("field3").getJsonArray("field6").add("add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testBulkWriteFileReturnedObjectNotModified(TestContext ctx) {
    final MongoClientBulkWriteResult expected = new MongoClientBulkWriteResult(0, 0, 28, 0, new ArrayList<>(Collections.singletonList(new JsonObject().put("field1", "value1"))));

    db.bulkWrite("bulkwrite", Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWriteFile"))))
      .onSuccess(actual -> ctx.assertEquals(expected.toJson(), actual.toJson()))
      .onSuccess(actual -> {
        actual.getUpserts().get(0).put("field1", "replace");
        actual.getUpserts().get(0).put("add", "add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testBulkWriteDuplicateKeyError(TestContext ctx) {
    mock.bulkWrite()
      .inCollection("bulkwrite")
      .withOperations(Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWrite"))))
      .returnsDuplicateKeyError();

    db.bulkWrite("bulkwrite", Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWrite"))))
      .onFailure(cause -> {
        ctx.assertTrue(cause instanceof MongoBulkWriteException);
        final MongoBulkWriteException actual = (MongoBulkWriteException) cause;
        ctx.assertEquals(1, actual.getWriteErrors().size());
        ctx.assertEquals(11000, actual.getWriteErrors().get(0).getCode());
      })
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testBulkWriteOtherError(TestContext ctx) {
    mock.bulkWrite()
      .withOperations(Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWrite"))))
      .inCollection("bulkwrite")
      .returnsOtherBulkWriteError();

    db.bulkWrite("bulkwrite", Arrays.asList(BulkOperation.createInsert(new JsonObject().put("test", "testBulkWrite"))))
      .onFailure(cause -> {
        ctx.assertTrue(cause instanceof MongoBulkWriteException);
        final MongoBulkWriteException actual = (MongoBulkWriteException) cause;
        ctx.assertEquals(1, actual.getWriteErrors().size());
        ctx.assertEquals(22000, actual.getWriteErrors().get(0).getCode());
      })
      .onComplete(ctx.asyncAssertFailure());
  }
}
