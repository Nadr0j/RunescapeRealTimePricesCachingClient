/* (C)2024 */
package org.jws.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import dagger.Module;
import dagger.Provides;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jws.model.ModifiablePriceRecord;
import org.jws.model.PriceRecord;
import org.jws.model.PriceRecordFields;

import javax.inject.Named;
import javax.inject.Singleton;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.jws.dagger.ProviderNames.*;

@Module
public class ServiceModules {
    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    public MongoCollection<PriceRecord> provideTwentyfourHourCollection(
            final MongoClient mongoClient, final CodecRegistry registry) {
        final MongoDatabase database = mongoClient.getDatabase(MONGO_DATABASE_NAME).withCodecRegistry(registry);
        final MongoCollection<PriceRecord> collection = database.getCollection(
                TWENTY_FOUR_HOURS_TIMESERIES_COLLECTION, PriceRecord.class);

        collection.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending(PriceRecordFields.ITEM_ID),
                        Indexes.ascending(PriceRecordFields.TIMESTAMP)
                ),
                new IndexOptions().unique(true)
        );

        return collection;
    }

    @Provides
    @Singleton
    public MongoClient provideMongoClient(@Named(MONGO_CONNECTION_URI) final String connectionUri) {
        return MongoClients.create(connectionUri);
    }

    @Provides
    @Singleton
    public CodecRegistry provideCodecRegistry() {
        return fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder()
                        .register("org.jws.model")
                        .register(ModifiablePriceRecord.class)
                        .automatic(true)
                        .build()));
    }

    @Provides
    @Named(MONGO_CONNECTION_URI)
    public String provideMongoConnectionUri() {
//        return System.getenv(MONGODB_URI_ENV_VAR);
        return "mongodb://10.0.0.102:27017";
    }

    @Provides
    @Named(OSRS_CLIENT_USER_AGENT_ENV_VAR)
    public String provideOsrsClientUserAgent() {
//        return System.getenv(OSRS_CLIENT_USER_AGENT_ENV_VAR);
        return "Exadre@ - testing";
    }
}
