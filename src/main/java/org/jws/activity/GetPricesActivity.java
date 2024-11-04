package org.jws.activity;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.conversions.Bson;
import org.jws.client.OsrsApiClient;
import org.jws.model.*;
import org.jws.router.DataStoreRouter;

import javax.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class GetPricesActivity {
    private final OsrsApiClient osrsApiClient;
    private final MongoCollection<PriceRecord> collection;
    private final DataStoreRouter router;
    @Inject
    public GetPricesActivity(
            final OsrsApiClient osrsApiClient,
            final MongoCollection<PriceRecord> collection,
            final DataStoreRouter router) {
        this.collection = collection;
        this.osrsApiClient = osrsApiClient;
        this.router = router;
    }

    public GetPricesResponse get(final GetPricesRequest getPricesRequest) throws IOException {
        final List<PriceRecord> records = new ArrayList<>();
        final boolean shouldCallApi = router.shouldCallApi(getPricesRequest);

        if (shouldCallApi) {
            records.addAll(osrsApiClient.getTimeSeries(getPricesRequest.itemId(), Timestep.TWENTY_FOUR_HOUR));
            
        } else {
            final Bson queryFilter = getFilter(getPricesRequest);
             records.addAll(queryCollection(queryFilter));
        }

        return ImmutableGetPricesResponse.builder()
                .prices(records)
                .build();
    }

    private List<PriceRecord> queryCollection(final Bson queryFilter) {
        final List<PriceRecord> records = new ArrayList<>();
        final FindIterable<PriceRecord> findIterable = collection.find(queryFilter);
        findIterable.forEach(records::add);
        return records;
    }

    private Bson getFilter(final GetPricesRequest request) {
        final List<Bson> filters = new ArrayList<>();
        filters.add(eq(PriceRecordFields.ITEM_ID, request.itemId()));

        if (request.startTime().isPresent()) {
            filters.add(gte(PriceRecordFields.TIMESTAMP, request.startTime()));
        }

        if (request.endTime().isPresent()) {
            filters.add(lte(PriceRecordFields.TIMESTAMP, request.endTime()));
        }

        return and(filters);
    }
}
