/* (C)2024 */
package org.jws.validation;

import org.jws.exception.ValidationException;
import org.jws.model.GetPricesRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class RequestValidatorTests {

    @Test
    void validateGetPricesRequest_doesNotThrow_validRequest() {
        GetPricesRequest request = mock(GetPricesRequest.class);
        when(request.startTime()).thenReturn(1620000000); // Valid epoch timestamp
        when(request.endTime()).thenReturn(1620003600);   // Valid epoch timestamp
        when(request.itemId()).thenReturn(100);              // Valid item ID

        Assertions.assertDoesNotThrow(() -> RequestValidator.validateGetPricesRequest(request));
    }

    @Test
    void validateGetPricesRequest_throwsException_invalidStartTime() {
        GetPricesRequest request = mock(GetPricesRequest.class);
        when(request.startTime()).thenReturn(-100);          // Invalid epoch timestamp
        when(request.endTime()).thenReturn(1620003600);   // Valid epoch timestamp
        when(request.itemId()).thenReturn(100);              // Valid item ID

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () ->
                RequestValidator.validateGetPricesRequest(request));
        Assertions.assertEquals("Invalid start time", thrown.getMessage());
    }

    @Test
    void validateGetPricesRequest_throwsException_invalidEndTime() {
        GetPricesRequest request = mock(GetPricesRequest.class);
        when(request.startTime()).thenReturn(1620000000); // Valid epoch timestamp
        when(request.endTime()).thenReturn(-200);            // Invalid epoch timestamp
        when(request.itemId()).thenReturn(100);              // Valid item ID

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () ->
                RequestValidator.validateGetPricesRequest(request));
        Assertions.assertEquals("Invalid end time", thrown.getMessage());
    }

    @Test
    void validateGetPricesRequest_throwsException_invalidItemId() {
        GetPricesRequest request = mock(GetPricesRequest.class);
        when(request.startTime()).thenReturn(1620000000); // Valid epoch timestamp
        when(request.endTime()).thenReturn(1620003600);   // Valid epoch timestamp
        when(request.itemId()).thenReturn(1);                // Invalid item ID

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () ->
                RequestValidator.validateGetPricesRequest(request));
        Assertions.assertEquals("Invalid item id", thrown.getMessage());
    }

    @Test
    void validateGetPricesRequest_throwsException_timestampInFuture() {
        GetPricesRequest request = mock(GetPricesRequest.class);
        when(request.startTime()).thenReturn(1620003600);
        when(request.endTime()).thenReturn(2147483647);
        when(request.itemId()).thenReturn(100);

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () ->
                RequestValidator.validateGetPricesRequest(request));
        Assertions.assertEquals("Invalid end time", thrown.getMessage());
    }
}
