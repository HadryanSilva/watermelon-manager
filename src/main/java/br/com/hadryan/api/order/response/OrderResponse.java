package br.com.hadryan.api.order.response;

import br.com.hadryan.api.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderResponse(
        Long id,
        LocalDate orderDate,
        OrderStatus orderStatus,
        BigDecimal pricePerKg,
        BigDecimal total,
        String transportationPlate
) {
}
