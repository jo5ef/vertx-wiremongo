package com.noenv.wiremongo;

import com.noenv.wiremongo.mapping.Count;
import com.noenv.wiremongo.mapping.MatchAll;
import com.noenv.wiremongo.mapping.RunCommand;
import com.noenv.wiremongo.mapping.aggregate.Aggregate;
import com.noenv.wiremongo.mapping.aggregate.AggregateWithOptions;
import com.noenv.wiremongo.mapping.bulkwrite.BulkWrite;
import com.noenv.wiremongo.mapping.bulkwrite.BulkWriteWithOptions;
import com.noenv.wiremongo.mapping.collection.CreateCollection;
import com.noenv.wiremongo.mapping.collection.DropCollection;
import com.noenv.wiremongo.mapping.collection.GetCollections;
import com.noenv.wiremongo.mapping.distinct.Distinct;
import com.noenv.wiremongo.mapping.distinct.DistinctBatch;
import com.noenv.wiremongo.mapping.distinct.DistinctBatchWithQuery;
import com.noenv.wiremongo.mapping.distinct.DistinctWithQuery;
import com.noenv.wiremongo.mapping.find.*;
import com.noenv.wiremongo.mapping.index.CreateIndex;
import com.noenv.wiremongo.mapping.index.CreateIndexWithOptions;
import com.noenv.wiremongo.mapping.index.DropIndex;
import com.noenv.wiremongo.mapping.index.ListIndexes;
import com.noenv.wiremongo.mapping.insert.Insert;
import com.noenv.wiremongo.mapping.insert.InsertWithOptions;
import com.noenv.wiremongo.mapping.remove.RemoveDocument;
import com.noenv.wiremongo.mapping.remove.RemoveDocumentWithOptions;
import com.noenv.wiremongo.mapping.remove.RemoveDocuments;
import com.noenv.wiremongo.mapping.remove.RemoveDocumentsWithOptions;
import com.noenv.wiremongo.mapping.replace.ReplaceDocuments;
import com.noenv.wiremongo.mapping.replace.ReplaceDocumentsWithOptions;
import com.noenv.wiremongo.mapping.save.Save;
import com.noenv.wiremongo.mapping.save.SaveWithOptions;
import com.noenv.wiremongo.mapping.update.UpdateCollection;
import com.noenv.wiremongo.mapping.update.UpdateCollectionWithOptions;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;

@VertxGen
public interface WireMongo {

  static WireMongo create(Vertx vertx) {
    return new WireMongoImpl(vertx);
  }

  default void readFileMappings(Handler<AsyncResult<Void>> handler) {
    readFileMappings().onComplete(handler);
  }

  default void readFileMappings(String path, Handler<AsyncResult<Void>> handler) {
    readFileMappings().onComplete(handler);
  }

  default Future<Void> readFileMappings() {
    return readFileMappings("wiremongo");
  }

  Future<Void> readFileMappings(String path);

  MongoClient getClient();

  @Fluent
  WireMongo clear();

  MatchAll matchAll();

  Insert insert();

  InsertWithOptions insertWithOptions();

  Save save();

  SaveWithOptions saveWithOptions();

  UpdateCollection updateCollection();

  UpdateCollectionWithOptions updateCollectionWithOptions();

  Find find();

  FindWithOptions findWithOptions();

  FindOne findOne();

  FindOneAndUpdate findOneAndUpdate();

  FindOneAndReplace findOneAndReplace();

  FindOneAndReplaceWithOptions findOneAndReplaceWithOptions();

  FindOneAndUpdateWithOptions findOneAndUpdateWithOptions();

  FindOneAndDelete findOneAndDelete();

  FindOneAndDeleteWithOptions findOneAndDeleteWithOptions();

  FindBatch findBatch();

  FindBatchWithOptions findBatchWithOptions();

  CreateCollection createCollection();

  DropCollection dropCollection();

  CreateIndex createIndex();

  BulkWrite bulkWrite();

  BulkWriteWithOptions bulkWriteWithOptions();

  Count count();

  ListIndexes listIndexes();

  GetCollections getCollections();

  CreateIndexWithOptions createIndexWithOptions();

  RemoveDocument removeDocument();

  RemoveDocumentWithOptions removeDocumentWithOptions();

  RemoveDocuments removeDocuments();

  RemoveDocumentsWithOptions removeDocumentsWithOptions();

  ReplaceDocuments replaceDocuments();

  ReplaceDocumentsWithOptions replaceDocumentsWithOptions();

  RunCommand runCommand();

  Aggregate aggregate();

  AggregateWithOptions aggregateWithOptions();

  DropIndex dropIndex();

  Distinct distinct();

  DistinctBatch distinctBatch();

  DistinctWithQuery distinctWithQuery();

  DistinctBatchWithQuery distinctBatchWithQuery();
}
