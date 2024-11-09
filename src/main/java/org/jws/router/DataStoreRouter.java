/* (C)2024 */
package org.jws.router;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.jws.model.GetPricesRequest;
import org.jws.model.PriceRecord;
import org.jws.model.PriceRecordFields;

import javax.inject.Inject;

public class DataStoreRouter {
    final MongoCollection<PriceRecord> collection;

    @Inject
    public DataStoreRouter(final MongoCollection<PriceRecord> collection) {
        this.collection = collection;
    }

    public boolean shouldCallApi(final GetPricesRequest request) {
        final boolean startTimeInDb = request.startTime()>= getEarliestRecord(request.itemId()).timestamp();
        final boolean endTimeInDb = request.endTime() <= getLatestRecord(request.itemId()).timestamp();
        return !(startTimeInDb && endTimeInDb);
    }

    private PriceRecord getEarliestRecord(final int itemId) {
        return collection
                .find(Filters.eq(PriceRecordFields.ITEM_ID, itemId))
                .sort(Sorts.descending(PriceRecordFields.TIMESTAMP))
                .first();
    }

    private PriceRecord getLatestRecord(final int itemId) {
        return collection
                .find(Filters.eq(PriceRecordFields.ITEM_ID, itemId))
                .sort(Sorts.descending(PriceRecordFields.TIMESTAMP))
                .first();
    }
}
