/* (C)2024 */
package org.jws.router;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.extern.log4j.Log4j2;
import org.jws.model.GetPricesRequest;
import org.jws.model.ModifiablePriceRecord;
import org.jws.model.PriceRecord;
import org.jws.model.PriceRecordFields;

import javax.inject.Inject;

@Log4j2
public class DataStoreRouter {
    final MongoCollection<PriceRecord> collection;

    @Inject
    public DataStoreRouter(final MongoCollection<PriceRecord> collection) {
        this.collection = collection;
    }

    public boolean shouldCallApi(final GetPricesRequest request) {
        final PriceRecord earliestRecord = getEarliestRecord(request.itemId());
        final PriceRecord latestRecord = getLatestRecord(request.itemId());
        final boolean startTimeInDb = earliestRecord != null && request.startTime() >= earliestRecord.getTimestamp();
        log.info("Found earliest record [{}]", earliestRecord);
        log.info("StartTimeInDb is [{}]", startTimeInDb);
        final boolean endTimeInDb = latestRecord != null && request.endTime() <= latestRecord.getTimestamp();
        log.info("Found latest record [{}]", latestRecord);
        log.info("EndTimeInDb is [{}]", endTimeInDb);
        return !(startTimeInDb && endTimeInDb);
    }

    private PriceRecord getEarliestRecord(final int itemId) {
        log.info("Getting earliest record for item with id [{}]", itemId);
        return collection
                .find(Filters.eq(PriceRecordFields.ITEM_ID, itemId))
                .sort(Sorts.ascending(PriceRecordFields.TIMESTAMP))
                .first();
    }

    private PriceRecord getLatestRecord(final int itemId) {
        return collection
                .find(Filters.eq(PriceRecordFields.ITEM_ID, itemId))
                .sort(Sorts.descending(PriceRecordFields.TIMESTAMP))
                .first();
    }
}
