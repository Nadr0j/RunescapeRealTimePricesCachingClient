/* (C)2024 */
package org.jws.util;

import org.jws.model.PriceRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PriceRecordUtils {
    public PriceRecordUtils() {}

    public boolean pricesHaveMissingDays(final List<PriceRecord> prices) {
        final List<PriceRecord> sortedPrices = sortPriceRecords(prices);

        for (int i = 1; i < sortedPrices.size(); i++) {
            final PriceRecord previous = sortedPrices.get(i - 1);
            final PriceRecord current = sortedPrices.get(i);

            final long differenceInSeconds = current.getTimestamp() - previous.getTimestamp();

            // Check if the difference is greater than a day (86400 seconds)
            if (differenceInSeconds > 86400) {
                return true; // Missing day(s) found
            }
        }

        return false;
    }

    public List<PriceRecord> sortPriceRecords(final List<PriceRecord> priceRecords) {
        final List<PriceRecord> copiedList = new ArrayList<>(priceRecords);

        copiedList.sort(new Comparator<PriceRecord>() {
            @Override
            public int compare(PriceRecord o1, PriceRecord o2) {
                return Integer.compare(o1.getTimestamp(), o2.getTimestamp());
            }
        });

        return copiedList;
    }
}
