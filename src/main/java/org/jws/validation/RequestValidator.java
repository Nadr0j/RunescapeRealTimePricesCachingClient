/* (C)2024 */
package org.jws.validation;

import lombok.extern.log4j.Log4j2;
import org.jws.exception.ValidationException;
import org.jws.model.GetPricesRequest;

import java.time.Instant;
import java.util.Date;

import static org.jws.exception.Messages.*;

@Log4j2
public final class RequestValidator {
    private RequestValidator() {}

    public static void validateGetPricesRequest(final GetPricesRequest request) {
        try {
            assertValidEpoch(request.startTime());
        } catch (final Exception e) {
            throw new ValidationException(INVALID_START_TIMESTAMP);
        }

        try {
            assertValidEpoch(request.endTime());
        } catch (final Exception e) {
            throw new ValidationException(INVALID_END_TIMESTAMP);
        }

        assertValidItemId(request.itemId());
    }

    private static void assertValidEpoch(final Integer timestamp) {
        final int currentTimestamp = (int) (System.currentTimeMillis() / 1_000);

        if (timestamp > currentTimestamp) {
            throw new ValidationException(INVALID_END_TIMESTAMP);
        }

        if (timestamp < 0) {
            throw new ValidationException(INVALID_END_TIMESTAMP);
        }
    }

    private static void assertValidItemId(final int itemId) {
        if (itemId < 2 || itemId > 30146) {
            throw new ValidationException(INVALID_ITEM_ID);
        }
    }
}
