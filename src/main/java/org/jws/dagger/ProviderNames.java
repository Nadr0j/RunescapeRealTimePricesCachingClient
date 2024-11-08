/* (C)2024 */
package org.jws.dagger;

public class ProviderNames {
    private ProviderNames() {}

    public static final String MONGO_CONNECTION_URI = "MONGO_CONNECTION_URI";
    public static final String MONGO_DATABASE_NAME = "OSRS_TIMESERIES_DB";
    public static final String TWENTY_FOUR_HOURS_TIMESERIES_COLLECTION = "TWENTY_FOUR_HOURS_TIMESERIES_COLLECTION";
    public static final String OSRS_CLIENT_USER_AGENT_ENV_VAR = "osrs.client.useragent";
    public static final String MONGODB_URI_ENV_VAR = "mongodb.uri";

}
