package br.com.hadryan.api.product.request;

import jakarta.validation.constraints.NotBlank;

public record ProductPostRequest(
        @NotBlank
        String name,

        String description,

        @NotBlank
        String brand
) {
}
