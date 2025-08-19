package br.com.hadryan.api.transaction;

import br.com.hadryan.api.transaction.dto.TransactionDTO;
import br.com.hadryan.api.transaction.request.TransactionPostRequest;
import br.com.hadryan.api.transaction.response.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    Transaction postToTransaction(TransactionPostRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    Transaction dtoToTransaction(TransactionDTO dto);

    TransactionResponse transactionToResponse(Transaction transaction);

}
