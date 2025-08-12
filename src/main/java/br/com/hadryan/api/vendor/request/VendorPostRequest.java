package br.com.hadryan.api.vendor.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record VendorPostRequest(
        @NotNull
        UUID accountId,

        @NotBlank
        String name,

        String location,

        @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?(?:9\\d{4}|\\d{4})-?\\d{4}$\n")
        String phone,

        @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\n")
        String email,

        @Pattern(regexp = "^\\d{14}$\n")
        String cnpj
) {
}
