package io.huta.infra;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

public class Server {
    public static HttpServer createHttpServer(int port, ExecutorService executorService) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.setExecutor(executorService);
        return server;
    }
}
