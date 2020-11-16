package com.noenv.wiremongo.mapping.collection;

import com.noenv.wiremongo.TestBase;
import com.noenv.wiremongo.mapping.Mapping;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(VertxUnitRunner.class)
public class GetCollectionsTest extends TestBase {

  @Test
  public void testGetCollections(TestContext ctx) {
    Mapping<?, ?, ?> m = mock.getCollections()
      .returns(Arrays.asList("collection1", "collection2"));

    db.getCollections()
      .onSuccess(s -> {
        ctx.assertEquals(Arrays.asList("collection1", "collection2"), s);
        mock.removeMapping(m);
      })
      .onComplete(ctx.asyncAssertSuccess());
  }

  @Test
  public void testGetCollectionError(TestContext ctx) {
    Mapping<?, ?, ?> m = mock.getCollections()
      .returnsError(new Exception("intentional"));

    db.getCollections()
      .onFailure(ex -> {
        ctx.assertEquals("intentional", ex.getMessage());
        mock.removeMapping(m);
      })
      .onComplete(ctx.asyncAssertFailure());
  }

  @Test
  public void testGetCollectionReturnedObjectNotModified(TestContext ctx) {
    final List<String> given = Arrays.asList("first", "second");
    final List<String> expected = new ArrayList<>(given);

    mock.getCollections().returns(given);

    db.getCollections()
      .onSuccess(actual -> ctx.assertEquals(expected, actual))
      .onSuccess(actual -> {
        actual.remove(0);
        actual.add("add");
      })
      .onComplete(ctx.asyncAssertSuccess());
  }
}
