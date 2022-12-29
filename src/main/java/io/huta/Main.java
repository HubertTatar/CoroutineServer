package io.huta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.huta.infra.Config;
import io.huta.infra.Metrics;
import io.huta.infra.Server;
import io.huta.test.TestHandler;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        LOG.info("Starting");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LOG.error("Uncaught Exception", e);
            System.exit(1);
        });

        Config config = Config.load("app.properties");

        ThreadFactory threadFactory = Thread.ofVirtual().uncaughtExceptionHandler((t, e) ->
                LOG.error("Virtual Uncaught Exception", e)
        ).factory();
        ExecutorService executorService = Executors.newFixedThreadPool(10000, threadFactory);

        HttpServer server = Server.createHttpServer(config.serverCfg().port(), executorService);
        server.createContext("/health", exchange -> {
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        });

        ObjectMapper mapper = new ObjectMapper();

        HttpContext context = server.createContext("/test", new TestHandler(mapper));
        context.getFilters().add(new Filter() {
            @Override
            public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
                LOG.info("filter");
                chain.doFilter(exchange);
            }

            @Override
            public String description() {
                return "test";
            }
        });

        server.start();
        LOG.info("Http listening on port 8080");

        PrometheusMeterRegistry registry = Metrics.createRegistry();
        HttpServer prometheus = Server.createHttpServer(config.prometheusCfg().port(), executorService);
        prometheus.createContext("/metrics", exchange -> {
            var scraped = registry.scrape();
            exchange.sendResponseHeaders(200, scraped.length());
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(scraped.getBytes(StandardCharsets.UTF_8));
            responseBody.flush();
            responseBody.close();
        });
        prometheus.start();
        LOG.info("Prometheus metrics on port 8081");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Shutdown hook - closing");
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOG.error("Error during shutdown");
            }
            prometheus.stop(10);
            server.stop(10);
        }));
    }
}