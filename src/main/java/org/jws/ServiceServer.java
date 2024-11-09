/* (C)2024 */
package org.jws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.jws.activity.GetPricesActivity;
import org.jws.model.*;
import org.jws.model.Error;
import org.jws.model.GetPricesResponse;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Executors;


@Log4j2
public class ServiceServer {
    private final ObjectMapper objectMapper;
    private final GetPricesActivity getPricesActivity;

    private static final int PORT = 10_100;
    private static final int BACKLOG = 2;

    @Inject
    public ServiceServer(final GetPricesActivity getPricesActivity, final ObjectMapper objectMapper) {
        this.getPricesActivity = getPricesActivity;
        this.objectMapper = objectMapper;
    }

    public void start() throws IOException {
        log.info("Starting server");
        final HttpServer server = HttpServer.create(new InetSocketAddress(PORT), BACKLOG);
        server.createContext("/some/api", this::handleSomeApi);
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        log.info("Pages server has started");
    }

    private void handleSomeApi(final HttpExchange httpExchange) {
        try {
            setRequestIdInThreadContext();
            if ("POST".equals(httpExchange.getRequestMethod())) {
                final InputStream inputStream = httpExchange.getRequestBody();
                final GetPricesRequest request = objectMapper.readValue(inputStream, GetPricesRequest.class);
                log.info("Received request with request object [{}]", request);
                final GetPricesResponse response = getPricesActivity.get(request);
                sendResponse(httpExchange, HttpURLConnection.HTTP_OK, response);
                log.info("Finished activity with success.");
            } else {
                log.info("WritePage request sent but is not POST. Returning 404.");
                sendError(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, "Request must be POST");
            }
        } catch (final Exception e) {
            log.error("Failed with exception", e);
            sendError(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Encountered error. " +
                    "Please try your request again.");
        } finally {
            clearRequestId();
        }
    }

    private void sendError(final HttpExchange httpExchange, final int statusCode, final String errorMessage) {
        final Error error = ImmutableError.builder().error(errorMessage).build();
        sendResponse(httpExchange, statusCode, error);
    }

    private void sendResponse(final HttpExchange httpExchange, final int statusCode, final ReturnableModel model) {
        try {
            final byte[] responseBytes = objectMapper.writeValueAsBytes(model);
            httpExchange.sendResponseHeaders(statusCode, responseBytes.length);
            final OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(responseBytes);
            responseBody.close();
        } catch (Exception e) {
            log.error("Failed to send response.", e);
        }
    }

    private void setRequestIdInThreadContext() {
        final String requestId = UUID.randomUUID().toString();
        ThreadContext.put("requestId", requestId);
    }

    private void clearRequestId() {
        ThreadContext.clearAll();
    }
}
