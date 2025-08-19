package br.com.hadryan.api.transaction.dto;

import br.com.hadryan.api.transaction.enums.Type;

import java.math.BigDecimal;

public record UpdateAccountDTO(
        Type transactionType,
        BigDecimal amount
) {
}
