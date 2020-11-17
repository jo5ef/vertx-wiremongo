package com.noenv.wiremongo;

import com.noenv.wiremongo.command.Command;
import com.noenv.wiremongo.mapping.Count;
import com.noenv.wiremongo.mapping.Mapping;
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
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WireMongoImpl implements WireMongo {

  private static final Logger logger = LoggerFactory.getLogger(WireMongoImpl.class);

  private final Vertx vertx;
  private final List<Mapping<?, ? extends Command, ?>> mappings = Collections.synchronizedList(new ArrayList<>());
  private final WireMongoClient client;
  private int priorityCounter = 1;

  public WireMongoImpl() {
    this(null);
  }

  public WireMongoImpl(Vertx vertx) {
    this.vertx = vertx;
    this.client = WireMongoClient.createShared(vertx, null, "").setWireMongo(this);
  }

  @Override
  public MongoClient getClient() {
    return client;
  }

  @Override
  public Future<Void> readFileMappings(String path) {
    Promise<List<String>> promise = Promise.promise();
    if (vertx == null) {
      return Future.failedFuture("to read file mappings, initialize WireMongo with a vertx instance");
    }
    vertx.fileSystem().readDir(path, promise);
    return promise.future()
      .compose(l -> l.stream()
        .map(p -> p.toLowerCase().endsWith(".json") ? readMappingFromFile(p) : readFileMappings(p))
        .reduce(Future.succeededFuture(), (a, b) -> a.compose(x -> b)))
      .mapEmpty();
  }

  private Future<Void> readMappingFromFile(String file) {
    Promise<Buffer> promise = Promise.promise();
    vertx.fileSystem().readFile(file, promise);
    return promise.future()
      .map(Buffer::toJsonObject)
      .map(Mapping::create)
      .map(this::addMapping)
      .mapEmpty();
  }

  public <T extends Mapping<?, ?, ?>> T addMapping(T mapping) {
    if (mapping.priority() == 0) {
      mapping.priority(priorityCounter++);
    }
    mappings.add(mapping);
    return mapping;
  }

  public <T extends Mapping<?, ?, ?>> boolean removeMapping(T mapping) {
    return mappings.remove(mapping);
  }

  <T, U extends Command> Future<T> call(U request) {
    logger.debug("wiremongo received request: " + request.toString());
    Mapping<T, U, ?> mapping = this.findMapping(request);
    if (mapping == null) {
      return Future.failedFuture("no mapping found: " + request);
    }
    try {
      return Future.succeededFuture(mapping.invoke(request));
    } catch (Throwable ex) {
      return Future.failedFuture(ex);
    }
  }

  @Override
  public WireMongoImpl clear() {
    mappings.clear();
    return this;
  }

  <U extends Command> ReadStream<JsonObject> callStream(U request) {
    Mapping<ReadStream<JsonObject>, U, ?> mapping = this.findMapping(request);
    if (mapping == null) {
      return MemoryStream.error(new IllegalArgumentException("no mapping found: " + request));
    }
    try {
      return mapping.invoke(request);
    } catch (Throwable ex) {
      return MemoryStream.error(ex);
    }
  }

  private <T, U extends Command> Mapping<T, U, ?> findMapping(U request) {
    synchronized (mappings) {
      //noinspection unchecked
      return (Mapping<T, U, ?>) mappings.stream()
        .filter(m -> {
          try {
            return m.matches(request);
          } catch (Throwable ex) {
            logger.error("error evaluating mapping", ex);
            return false;
          }
        })
        .max(Comparator.comparingInt(Mapping::priority))
        .orElseGet(() -> {
          logger.info("no mapping found (" + request.toString() + ")");
          return null;
        });
    }
  }

  /// ----

  @Override public MatchAll matchAll() {
    return addMapping(new MatchAll());
  }

  @Override public Insert insert() {
    return addMapping(new Insert());
  }

  @Override public InsertWithOptions insertWithOptions() {
    return addMapping(new InsertWithOptions());
  }

  @Override public Save save() {
    return addMapping(new Save());
  }

  @Override public SaveWithOptions saveWithOptions() {
    return addMapping(new SaveWithOptions());
  }

  @Override public UpdateCollection updateCollection() {
    return addMapping(new UpdateCollection());
  }

  @Override public UpdateCollectionWithOptions updateCollectionWithOptions() {
    return addMapping(new UpdateCollectionWithOptions());
  }

  @Override public Find find() {
    return addMapping(new Find());
  }

  @Override public FindWithOptions findWithOptions() {
    return addMapping(new FindWithOptions());
  }

  @Override public FindOne findOne() {
    return addMapping(new FindOne());
  }

  @Override public FindOneAndUpdate findOneAndUpdate() {
    return addMapping(new FindOneAndUpdate());
  }

  @Override public FindOneAndReplace findOneAndReplace() {
    return addMapping(new FindOneAndReplace());
  }

  @Override public FindOneAndReplaceWithOptions findOneAndReplaceWithOptions() {
    return addMapping(new FindOneAndReplaceWithOptions());
  }

  @Override public FindOneAndUpdateWithOptions findOneAndUpdateWithOptions() {
    return addMapping(new FindOneAndUpdateWithOptions());
  }

  @Override public FindOneAndDelete findOneAndDelete() {
    return addMapping(new FindOneAndDelete());
  }

  @Override public FindOneAndDeleteWithOptions findOneAndDeleteWithOptions() {
    return addMapping(new FindOneAndDeleteWithOptions());
  }

  @Override public FindBatch findBatch() {
    return addMapping(new FindBatch());
  }

  @Override public FindBatchWithOptions findBatchWithOptions() {
    return addMapping(new FindBatchWithOptions());
  }

  @Override public CreateCollection createCollection() {
    return addMapping(new CreateCollection());
  }

  @Override public DropCollection dropCollection() {
    return addMapping(new DropCollection());
  }

  @Override public CreateIndex createIndex() {
    return addMapping(new CreateIndex());
  }

  @Override public BulkWrite bulkWrite() {
    return addMapping(new BulkWrite());
  }

  @Override public BulkWriteWithOptions bulkWriteWithOptions() {
    return addMapping(new BulkWriteWithOptions());
  }

  @Override public Count count() {
    return addMapping(new Count());
  }

  @Override public ListIndexes listIndexes() {
    return addMapping(new ListIndexes());
  }

  @Override public GetCollections getCollections() {
    return addMapping(new GetCollections());
  }

  @Override public CreateIndexWithOptions createIndexWithOptions() {
    return addMapping(new CreateIndexWithOptions());
  }

  @Override public RemoveDocument removeDocument() {
    return addMapping(new RemoveDocument());
  }

  @Override public RemoveDocumentWithOptions removeDocumentWithOptions() {
    return addMapping(new RemoveDocumentWithOptions());
  }

  @Override public RemoveDocuments removeDocuments() {
    return addMapping(new RemoveDocuments());
  }

  @Override public RemoveDocumentsWithOptions removeDocumentsWithOptions() {
    return addMapping(new RemoveDocumentsWithOptions());
  }

  @Override public ReplaceDocuments replaceDocuments() {
    return addMapping(new ReplaceDocuments());
  }

  @Override public ReplaceDocumentsWithOptions replaceDocumentsWithOptions() {
    return addMapping(new ReplaceDocumentsWithOptions());
  }

  @Override public RunCommand runCommand() {
    return addMapping(new RunCommand());
  }

  @Override public Aggregate aggregate() {
    return addMapping(new Aggregate());
  }

  @Override public AggregateWithOptions aggregateWithOptions() {
    return addMapping(new AggregateWithOptions());
  }

  @Override public DropIndex dropIndex() {
    return addMapping(new DropIndex());
  }

  @Override public Distinct distinct() {
    return addMapping(new Distinct());
  }

  @Override public DistinctBatch distinctBatch() {
    return addMapping(new DistinctBatch());
  }

  @Override public DistinctWithQuery distinctWithQuery() {
    return addMapping(new DistinctWithQuery());
  }

  @Override public DistinctBatchWithQuery distinctBatchWithQuery() {
    return addMapping(new DistinctBatchWithQuery());
  }
}
