package org.jws.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonDeserialize(as = ImmutableGetPricesRequest.class)
public abstract class GetPricesRequest {
    public abstract int itemId();
    public abstract Optional<Integer> startTime();
    public abstract Optional<Integer> endTime();
}
