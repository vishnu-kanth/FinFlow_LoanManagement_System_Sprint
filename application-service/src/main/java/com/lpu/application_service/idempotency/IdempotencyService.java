package com.lpu.application_service.idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpu.application_service.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository repository;
    private final ObjectMapper objectMapper;

    public <T> T executeIdempotent(String idempotencyKey, Supplier<T> action, Class<T> responseType) {
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            return action.get();
        }

        Optional<IdempotencyKey> existingKeyOpt = repository.findById(idempotencyKey);

        if (existingKeyOpt.isPresent()) {
            IdempotencyKey existingKey = existingKeyOpt.get();
            if ("IN_PROGRESS".equals(existingKey.getStatus())) {
                throw new CustomException("Duplicate request in progress");
            }

            if ("COMPLETED".equals(existingKey.getStatus())) {
                try {
                    return objectMapper.readValue(existingKey.getResponseBody(), responseType);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Failed to parse cached response");
                }
            }
        }

        IdempotencyKey newKey = new IdempotencyKey();
        newKey.setIdempotencyKey(idempotencyKey);
        newKey.setStatus("IN_PROGRESS");
        newKey.setCreatedAt(LocalDateTime.now());
        repository.save(newKey);

        T response;
        try {
            response = action.get();
        } catch (Exception e) {
            repository.delete(newKey);
            throw e;
        }

        try {
            newKey.setResponseBody(objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            newKey.setResponseBody("{}");
        }
        newKey.setStatus("COMPLETED");
        repository.save(newKey);

        return response;
    }
}
