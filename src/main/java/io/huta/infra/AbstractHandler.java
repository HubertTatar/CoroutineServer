package io.huta.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.huta.test.TestRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class AbstractHandler implements HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractHandler.class);
    private final ObjectMapper mapper;

    protected AbstractHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "HEAD":
                    handleHead(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                default:
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
            }
        } catch (Exception e) {
            LOG.error("s", e);
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
        exchange.close();
    }
    protected void handlePost(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
        exchange.close();
    }
    protected void handleGet(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
        exchange.close();
    }
    protected void handleHead(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
        exchange.close();
    }
    protected void handlePut(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
        exchange.close();
    }

    protected <T> T deserialize(HttpExchange exchange, Class<T> testRequestDtoClass) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String str = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        return mapper.readValue(str, testRequestDtoClass);
    }

    protected String serialize(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    protected void writeResp(String resp, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, resp.length());
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(resp.getBytes(StandardCharsets.UTF_8));
        responseBody.close();
        exchange.close();
    }
}
