package br.com.hadryan.api.purchase.response;

import br.com.hadryan.api.purchase.enums.Status;

import java.math.BigDecimal;

public record PurchaseResponse(
        Long id,
        String name,
        String description,
        BigDecimal total,
        Status status
) {
}
