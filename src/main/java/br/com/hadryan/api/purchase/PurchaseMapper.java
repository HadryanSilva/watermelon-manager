package br.com.hadryan.api.purchase;

import br.com.hadryan.api.purchase.request.PurchasePostRequest;
import br.com.hadryan.api.purchase.response.PurchaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "account",  ignore = true)
    @Mapping(target = "items", ignore = true)
    Purchase postToPurchase(PurchasePostRequest request);

    PurchaseResponse purchaseToResponse(Purchase purchase);

}
