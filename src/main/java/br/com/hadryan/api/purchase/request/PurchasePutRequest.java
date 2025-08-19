package br.com.hadryan.api.purchase.request;

import br.com.hadryan.api.purchase.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PurchasePutRequest(
        @NotNull
        Long id,

        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        Status status
) {
}
