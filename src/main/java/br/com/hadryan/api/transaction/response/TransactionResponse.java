package br.com.hadryan.api.transaction.response;

import br.com.hadryan.api.transaction.enums.Category;
import br.com.hadryan.api.transaction.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        String name,
        String description,
        Type type,
        Category category,
        BigDecimal amount,
        LocalDate date
) {
}
