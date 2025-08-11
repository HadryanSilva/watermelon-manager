package br.com.hadryan.api.customer.request;

import jakarta.validation.constraints.NotNull;

public record CustomerPutRequest(
        @NotNull
        Long id,
        String name,
        String phone,
        String email,
        String location
) {
}
