/* (C)2024 */
package org.jws.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jws.model.PriceRecord;
import org.jws.model.Timestep;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.jws.dagger.ProviderNames.OSRS_CLIENT_USER_AGENT_ENV_VAR;

public class OsrsApiClient {
    private static final String BASE_URL = "https://prices.runescape.wiki/api/v1/osrs";
    private final String userAgent;
    private final ObjectMapper objectMapper;

    @Inject
    public OsrsApiClient(@Named(OSRS_CLIENT_USER_AGENT_ENV_VAR) final String userAgent, final ObjectMapper objectMapper) {
        Objects.requireNonNull(userAgent);
        Objects.requireNonNull(objectMapper);
        this.userAgent = userAgent;
        this.objectMapper = objectMapper;
    }

    public List<PriceRecord> getTimeSeries(final int itemId, final Timestep timestep) throws IOException {
        final String url = BASE_URL + "/timeseries?id=" + itemId + "&timestep=" + timestep.getValue();
        final String responseString = sendGetRequest(url);
        return parseTimeSeriesResponse(itemId, responseString);
    }

    public List<PriceRecord> getOneHourPrices() throws IOException {
        final String url = BASE_URL + "/1h";
        final String responseString = sendGetRequest(url);
        final int currentTime = (int) System.currentTimeMillis() / 1000;
        return parseOneHourPricesResponse(currentTime, responseString);
    }

    private String sendGetRequest(final String urlString) throws IOException {
        final URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent", userAgent);

        final int responseCode = urlConnection.getResponseCode();
        if (responseCode == HTTP_OK) {
            final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            final StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            throw new RuntimeException("Encountered status code " + responseCode);
        }
    }

    private List<PriceRecord> parseTimeSeriesResponse(final int itemId, final String stringResponse) throws JsonProcessingException {
        final JsonNode dataArray = objectMapper.readTree(stringResponse).get("data");
        final List<PriceRecord> prices = new ArrayList<>();

        for (JsonNode node : dataArray) {
            final PriceRecord record = new PriceRecord();
            record.setItemId(itemId);
            record.setTimestamp(node.get("timestamp").asInt());
            record.setAvgHighPrice(node.get("avgHighPrice").floatValue());
            record.setAvgLowPrice(node.get("avgLowPrice").floatValue());
            record.setHighPriceVolume(node.get("highPriceVolume").floatValue());
            record.setLowPriceVolume(node.get("lowPriceVolume").floatValue());
            prices.add(record);
        }

        return prices;
    }

    private List<PriceRecord> parseOneHourPricesResponse(
            final int timestamp, final String stringResponse) throws JsonProcessingException {
        final JsonNode dataObject = objectMapper.readTree(stringResponse).get("data");
        final List<PriceRecord> prices = new ArrayList<>();

        // Iterate over field names in the "data" object
        for (Iterator<String> it = dataObject.fieldNames(); it.hasNext(); ) {
            String itemId = it.next();
            JsonNode node = dataObject.get(itemId);

            final PriceRecord record = new PriceRecord();
            record.setItemId(Integer.parseInt(itemId));
            record.setTimestamp(timestamp);
            record.setAvgHighPrice(node.get("avgHighPrice").isNull() ? null : node.get("avgHighPrice").floatValue());
            record.setAvgLowPrice(node.get("avgLowPrice").isNull() ? null : node.get("avgLowPrice").floatValue());
            record.setHighPriceVolume(node.get("highPriceVolume").isNull() ? null : node.get("highPriceVolume").floatValue());
            record.setLowPriceVolume(node.get("lowPriceVolume").isNull() ? null : node.get("lowPriceVolume").floatValue());
            prices.add(record);
        }

        return prices;
    }

}
