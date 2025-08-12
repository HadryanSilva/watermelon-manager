package br.com.hadryan.api.transaction;

import br.com.hadryan.api.transaction.enums.Category;
import br.com.hadryan.api.transaction.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionDTO(
        Long id,
        UUID accountId,
        String name,
        String description,
        Type type,
        Category category,
        BigDecimal amount,
        LocalDate date
) {
}
