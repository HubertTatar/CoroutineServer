package io.huta.test;

import java.util.UUID;

public record TestResponseDto(UUID uuid, Integer field, String field2, Boolean field3, Double field4) {
}
