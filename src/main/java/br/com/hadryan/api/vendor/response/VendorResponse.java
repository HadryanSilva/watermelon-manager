package br.com.hadryan.api.vendor.response;

public record VendorResponse(
        Long id,
        String name,
        String location,
        String phone,
        String email
) {
}
