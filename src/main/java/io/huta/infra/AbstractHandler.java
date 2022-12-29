package io.huta.infra;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractHandler implements HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractHandler.class);

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
}
