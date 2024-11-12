/* (C)2024 */
package org.jws.util;

import org.jws.model.PriceRecord;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PriceRecordUtilsTests {

    @Test
    public void testPricesHaveMissingDays_NoMissingDays() {
        List<PriceRecord> prices = new ArrayList<>();
        prices.add(new PriceRecord(1, 1622505600, 100.0f, 90.0f, 500.0f, 400.0f)); // 2021-06-01 00:00:00
        prices.add(new PriceRecord(1, 1622592000, 105.0f, 95.0f, 600.0f, 450.0f)); // 2021-06-02 00:00:00
        prices.add(new PriceRecord(1, 1622678400, 110.0f, 100.0f, 700.0f, 500.0f)); // 2021-06-03 00:00:00

        PriceRecordUtils utils = new PriceRecordUtils();
        assertFalse(utils.pricesHaveMissingDays(prices));
    }

    @Test
    public void testPricesHaveMissingDays_WithMissingDays() {
        List<PriceRecord> prices = new ArrayList<>();
        prices.add(new PriceRecord(1, 1622505600, 100.0f, 90.0f, 500.0f, 400.0f)); // 2021-06-01 00:00:00
        prices.add(new PriceRecord(1, 1622764800, 105.0f, 95.0f, 600.0f, 450.0f)); // 2021-06-04 00:00:00 (missing 2 days)

        PriceRecordUtils utils = new PriceRecordUtils();
        assertTrue(utils.pricesHaveMissingDays(prices));
    }

    @Test
    public void testPricesHaveMissingDays_EmptyList() {
        List<PriceRecord> prices = new ArrayList<>();

        PriceRecordUtils utils = new PriceRecordUtils();
        assertFalse(utils.pricesHaveMissingDays(prices));
    }

    @Test
    public void testPricesHaveMissingDays_SingleEntry() {
        List<PriceRecord> prices = new ArrayList<>();
        prices.add(new PriceRecord(1, 1622505600, 100.0f, 90.0f, 500.0f, 400.0f)); // 2021-06-01 00:00:00

        PriceRecordUtils utils = new PriceRecordUtils();
        assertFalse(utils.pricesHaveMissingDays(prices));
    }

    @Test
    public void testSortPriceRecords() {
        List<PriceRecord> prices = new ArrayList<>();
        prices.add(new PriceRecord(1, 1622678400, 110.0f, 100.0f, 700.0f, 500.0f)); // 2021-06-03 00:00:00
        prices.add(new PriceRecord(1, 1622505600, 100.0f, 90.0f, 500.0f, 400.0f)); // 2021-06-01 00:00:00
        prices.add(new PriceRecord(1, 1622592000, 105.0f, 95.0f, 600.0f, 450.0f)); // 2021-06-02 00:00:00

        PriceRecordUtils utils = new PriceRecordUtils();
        List<PriceRecord> sortedPrices = utils.sortPriceRecords(prices);

        assertEquals(1622505600, sortedPrices.get(0).getTimestamp());
        assertEquals(1622592000, sortedPrices.get(1).getTimestamp());
        assertEquals(1622678400, sortedPrices.get(2).getTimestamp());
    }
}