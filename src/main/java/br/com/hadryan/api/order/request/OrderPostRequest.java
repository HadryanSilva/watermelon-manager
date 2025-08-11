package br.com.hadryan.api.order.request;

import br.com.hadryan.api.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderPostRequest(
        @NotNull
        Long customerId,

        @NotNull
        Long fieldId,

        @NotNull
        LocalDate orderDate,

        @NotNull
        OrderStatus orderStatus,

        @NotNull
        BigDecimal pricePerKg,

        @NotNull
        BigDecimal total,
        String transportationPlate
) {
}
