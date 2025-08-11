package br.com.hadryan.api.vendor.request;

public record VendorPutRequest(
        Long id,
        String name,
        String location,
        String phone,
        String email,
        String cnpj
) {
}
