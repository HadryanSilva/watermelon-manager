package br.com.hadryan.api.customer.response;

public record CustomerResponse(
        Long id,
        String name,
        String phone,
        String email,
        String location
) {
}
