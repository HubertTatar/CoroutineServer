package io.huta.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import io.huta.infra.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class TestHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TestHandler.class);

    public TestHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        LOG.info("req");
        TestRequestDto req = deserialize(exchange, TestRequestDto.class);
        LOG.info(req.toString());

        String resp = serialize(
                new TestResponseDto(UUID.randomUUID(), req.field(), req.field2(), req.field3(), req.field4()));

        writeResp(resp, exchange);
    }
}
