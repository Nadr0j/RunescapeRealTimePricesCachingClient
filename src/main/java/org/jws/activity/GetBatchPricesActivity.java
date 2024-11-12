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
import org.jws.util.PriceRecordUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.*;

@Log4j2
public class GetBatchPricesActivity {
    private final GetPricesActivity getPricesActivity;
    private final PriceRecordUtils priceRecordUtils;

    @Inject
    public GetBatchPricesActivity(final GetPricesActivity getPricesActivity, final PriceRecordUtils priceRecordUtils) {
        this.getPricesActivity = getPricesActivity;
        this.priceRecordUtils = priceRecordUtils;
    }

    public GetBatchPricesResponse get(final GetBatchPricesRequest getBatchPricesRequest) throws IOException {
        final Map<Integer, List<PriceRecord>> records = new HashMap<>();
        final List<Integer> itemIdsWithNonConsecutiveData = new ArrayList<>();

        for (Integer itemId: getBatchPricesRequest.itemIds()) {
            final GetPricesRequest getPricesRequest = ImmutableGetPricesRequest.builder()
                    .itemId(itemId)
                    .startTime(getBatchPricesRequest.startTime())
                    .endTime(getBatchPricesRequest.endTime())
                    .build();

            final List<PriceRecord> prices = getPricesActivity.get(getPricesRequest).prices();
            if (priceRecordUtils.pricesHaveMissingDays(prices)) {
                itemIdsWithNonConsecutiveData.add(itemId);
            }
            records.put(itemId, prices);
        }

        return ImmutableGetBatchPricesResponse.builder()
                .prices(records)
                .trace(ImmutableTrace.builder()
                        .addAllItemIdsWithMissingDays(itemIdsWithNonConsecutiveData)
                        .build())
                .build();
    }

}
