/* (C)2024 */
package org.jws.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableError.class)
public abstract class Error extends ReturnableModel {
    public abstract String error();
}
