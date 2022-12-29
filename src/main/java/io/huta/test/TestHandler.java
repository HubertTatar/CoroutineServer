package io.huta.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.huta.infra.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TestHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TestHandler.class);

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        LOG.info("req");
        InputStream requestBody = exchange.getRequestBody();
        String str = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
            TestRequestDto req = mapper.readValue(str, TestRequestDto.class);
            LOG.info(req.toString());


            String serialize = mapper.writeValueAsString(
                    new TestResponseDto(
                            UUID.randomUUID(),
                            req.field(),
                            req.field2(),
                            req.field3(),
                            req.field4()
                    )

            );
            exchange.sendResponseHeaders(200, serialize.length());
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(serialize.getBytes(StandardCharsets.UTF_8));
            responseBody.close();
            exchange.close();
    }
}
