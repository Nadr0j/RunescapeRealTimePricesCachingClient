/* (C)2024 */
package org.jws.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableGetBatchPricesResponse.class)
public abstract class GetBatchPricesResponse extends ReturnableModel {
    public abstract Map<Integer, List<PriceRecord>> prices();
    public abstract Trace trace();
}
