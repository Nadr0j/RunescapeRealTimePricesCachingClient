package org.jws.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableGetPricesResponse.class)
public abstract class GetPricesResponse extends ReturnableModel {
    public abstract List<PriceRecord> prices();
}
