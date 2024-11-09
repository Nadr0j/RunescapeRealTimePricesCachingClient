/* (C)2024 */
package org.jws.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Modifiable
@JsonDeserialize(as = ModifiablePriceRecord.class)
public class PriceRecord {

    private int itemId;
    private int timestamp;
    private float avgHighPrice;
    private float avgLowPrice;
    private float highPriceVolume;
    private float lowPriceVolume;

    @BsonCreator
    public PriceRecord(
            @BsonProperty(PriceRecordFields.ITEM_ID) final int itemId,
            @BsonProperty(PriceRecordFields.TIMESTAMP) final int timestamp,
            @BsonProperty("avgHighPrice") final float avgHighPrice,
            @BsonProperty("avgLowPrice") final float avgLowPrice,
            @BsonProperty("highPriceVolume") final float highPriceVolume,
            @BsonProperty("lowPriceVolume") final float lowPriceVolume) {
        this.itemId = itemId;
        this.timestamp = timestamp;
        this.avgHighPrice = avgHighPrice;
        this.avgLowPrice = avgLowPrice;
        this.highPriceVolume = highPriceVolume;
        this.lowPriceVolume = lowPriceVolume;
    }

    // Getters
    public int getItemId() {
        return itemId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public float getAvgHighPrice() {
        return avgHighPrice;
    }

    public float getAvgLowPrice() {
        return avgLowPrice;
    }

    public float getHighPriceVolume() {
        return highPriceVolume;
    }

    public float getLowPriceVolume() {
        return lowPriceVolume;
    }

    // Setters
    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    public void setAvgHighPrice(final float avgHighPrice) {
        this.avgHighPrice = avgHighPrice;
    }

    public void setAvgLowPrice(final float avgLowPrice) {
        this.avgLowPrice = avgLowPrice;
    }

    public void setHighPriceVolume(final float highPriceVolume) {
        this.highPriceVolume = highPriceVolume;
    }

    public void setLowPriceVolume(final float lowPriceVolume) {
        this.lowPriceVolume = lowPriceVolume;
    }

    // No-arg constructor for MongoDB compatibility
    public PriceRecord() {
    }

    // toString method
    @Override
    public String toString() {
        return "PriceRecord{" +
                "itemId=" + itemId +
                ", timestamp=" + timestamp +
                ", avgHighPrice=" + avgHighPrice +
                ", avgLowPrice=" + avgLowPrice +
                ", highPriceVolume=" + highPriceVolume +
                ", lowPriceVolume=" + lowPriceVolume +
                '}';
    }
}
