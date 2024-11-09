/* (C)2024 */
package org.jws.activity;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import lombok.extern.log4j.Log4j2;
import org.bson.conversions.Bson;
import org.jws.client.OsrsApiClient;
import org.jws.model.*;
import org.jws.router.DataStoreRouter;

import javax.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

@Log4j2
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
        log.info("Starting activity");
        final List<PriceRecord> records = new ArrayList<>();
        log.info("Checking if should call api");
        final boolean shouldCallApi = router.shouldCallApi(getPricesRequest);
        log.info("Should call API is [{}]", shouldCallApi);
        if (shouldCallApi) {
            records.addAll(osrsApiClient.getTimeSeries(getPricesRequest.itemId(), Timestep.TWENTY_FOUR_HOUR));
            log.info("API called");
            updateAllRecords(records);
            log.info("Records written to db");
        } else {
            final Bson queryFilter = getFilter(getPricesRequest);
            records.addAll(queryCollection(queryFilter));
            log.info("Collection queries");
        }

        return ImmutableGetPricesResponse.builder()
                .prices(records)
                .build();
    }

    private void updateAllRecords(final List<PriceRecord> priceRecords) {
        log.info("Upserting [{}] records", priceRecords.size());
        final UpdateOptions options = new UpdateOptions().upsert(true);
        for (PriceRecord priceRecord: priceRecords) {
            final Bson filter = and(
                    eq(PriceRecordFields.ITEM_ID, priceRecord.getItemId()),
                    eq(PriceRecordFields.TIMESTAMP, priceRecord.getTimestamp())
            );
            final Bson updateValues = Updates.combine(
                    Updates.inc(PriceRecordFields.AVG_HIGH_PRICE, priceRecord.getAvgHighPrice()),
                    Updates.inc(PriceRecordFields.AVG_LOW_PRICE, priceRecord.getAvgLowPrice()),
                    Updates.inc(PriceRecordFields.HIGH_PRICE_VOLUME, priceRecord.getHighPriceVolume()),
                    Updates.inc(PriceRecordFields.LOW_PRICE_VOLUME, priceRecord.getLowPriceVolume())
            );
            collection.updateOne(filter, updateValues, options);
        }
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
        filters.add(gte(PriceRecordFields.TIMESTAMP, request.startTime()));
        filters.add(lte(PriceRecordFields.TIMESTAMP, request.endTime()));

        return and(filters);
    }
}
