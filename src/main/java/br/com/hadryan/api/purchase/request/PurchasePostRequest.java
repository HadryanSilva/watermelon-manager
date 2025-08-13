package br.com.hadryan.api.purchase.request;

import br.com.hadryan.api.purchase.ItemDTO;
import br.com.hadryan.api.purchase.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PurchasePostRequest(
        @NotBlank
        String name,

        String description,

        @NotNull
        Status status,

        @NotEmpty
        List<ItemDTO> items
) {
}
