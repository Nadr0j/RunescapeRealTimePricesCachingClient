/* (C)2024 */
package org.jws.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mongodb.lang.Nullable;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableGetBatchPricesRequest.class)
public abstract class GetBatchPricesRequest {
    public abstract Integer startTime();
    public abstract Integer endTime();
    public abstract List<Integer> itemIds();
}
