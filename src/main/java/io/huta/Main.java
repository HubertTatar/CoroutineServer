package io.huta;

import com.sun.net.httpserver.HttpServer;
import io.huta.infra.Metrics;
import io.huta.infra.Server;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        LOG.info("Starting");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LOG.error("Uncaught Exception", e);
            System.exit(1);
        });

        // load config from file
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        HttpServer server = Server.createHttpServer(8080, executorService);
        server.createContext("/health", exchange -> {
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        });
        server.start();

        PrometheusMeterRegistry registry = Metrics.createRegistry();
        HttpServer prometheus = Server.createHttpServer(8081, executorService);
        prometheus.createContext("/metrics", exchange -> {
            var scraped = registry.scrape();
            exchange.sendResponseHeaders(200, scraped.length());
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(scraped.getBytes(StandardCharsets.UTF_8));
            responseBody.flush();
            responseBody.close();
        });
        prometheus.start();

        // hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Shutdown hook - closing");
            prometheus.stop(10);
            server.stop(10);
        }));
    }
}