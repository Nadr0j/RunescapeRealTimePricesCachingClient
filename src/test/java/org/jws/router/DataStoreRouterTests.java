/* (C)2024 */
package org.jws.router;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.conversions.Bson;
import org.jws.model.GetPricesRequest;
import org.jws.model.PriceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataStoreRouterTests {

    @Mock
    MongoCollection<PriceRecord> collection;

    @InjectMocks
    DataStoreRouter dataStoreRouter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldCallApi_returnsFalse_whenDataInDb() {
        final PriceRecord mockRecord = mock(PriceRecord.class);
        final GetPricesRequest request = mock(GetPricesRequest.class);
        final FindIterable<PriceRecord> findIterable = mock(FindIterable.class);
        when(collection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.sort(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(mockRecord);
        when(mockRecord.getTimestamp()).thenReturn(100);
        when(request.endTime()).thenReturn(100);
        when(request.startTime()).thenReturn(100);

        assertDoesNotThrow(() -> dataStoreRouter.shouldCallApi(request));
    }

    @Test
    public void shouldCallApi_returnsTrue_whenFirstRecordIsOutOfBounds() {
        final PriceRecord mockRecord1 = mock(PriceRecord.class); // earliest record
        final PriceRecord mockRecord2 = mock(PriceRecord.class); // latest record
        final GetPricesRequest request = mock(GetPricesRequest.class);
        final FindIterable<PriceRecord> findIterable = mock(FindIterable.class);
        when(collection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.sort(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(mockRecord1).thenReturn(mockRecord2);
        when(mockRecord1.getTimestamp()).thenReturn(100);
        when(mockRecord2.getTimestamp()).thenReturn(50);
        when(request.endTime()).thenReturn(100);
        when(request.startTime()).thenReturn(100);

        // test case is latest record is before end time
        assertTrue(dataStoreRouter.shouldCallApi(request));
    }

    @Test
    public void shouldCallApi_returnsTrue_whenSecondRecordIsOutOfBounds() {
        final PriceRecord mockRecord1 = mock(PriceRecord.class); // earliest record
        final PriceRecord mockRecord2 = mock(PriceRecord.class); // latest record
        final GetPricesRequest request = mock(GetPricesRequest.class);
        final FindIterable<PriceRecord> findIterable = mock(FindIterable.class);
        when(collection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.sort(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(mockRecord1).thenReturn(mockRecord2);
        when(mockRecord1.getTimestamp()).thenReturn(150);
        when(mockRecord2.getTimestamp()).thenReturn(100);
        when(request.endTime()).thenReturn(100);
        when(request.startTime()).thenReturn(100);

        // test case is earliest record is after end time
        assertTrue(dataStoreRouter.shouldCallApi(request));
    }
}
