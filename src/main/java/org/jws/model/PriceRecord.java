package org.jws.model;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public abstract class PriceRecord {
    @BsonProperty(PriceRecordFields.ITEM_ID)
    public abstract int itemId();
    @BsonProperty(PriceRecordFields.TIMESTAMP)
    public abstract int timestamp();
    public abstract float avgHighPrice();
    public abstract float avgLowPrice();
    public abstract float highPriceVolume();
    public abstract float lowPriceVolume();
}
