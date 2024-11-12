/* (C)2024 */
package org.jws.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrace.class)
public abstract class Trace {
    public abstract List<Integer> itemIdsWithMissingDays();
}
