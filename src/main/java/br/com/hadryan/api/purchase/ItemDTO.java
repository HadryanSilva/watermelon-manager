package br.com.hadryan.api.purchase;

import java.math.BigDecimal;

public record ItemDTO(
        Long productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
