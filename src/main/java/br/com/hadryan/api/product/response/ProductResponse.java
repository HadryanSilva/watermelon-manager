package br.com.hadryan.api.product.response;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String brand
) {
}
