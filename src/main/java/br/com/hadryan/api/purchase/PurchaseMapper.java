package br.com.hadryan.api.purchase;

import br.com.hadryan.api.purchase.request.PurchasePostRequest;
import br.com.hadryan.api.purchase.request.PurchasePutRequest;
import br.com.hadryan.api.purchase.response.PurchaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "account",  ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "total",  ignore = true)
    Purchase postToPurchase(PurchasePostRequest request);

    @Mapping(target = "account",  ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "total",  ignore = true)
    Purchase putToPurchase(PurchasePutRequest request);

    PurchaseResponse purchaseToResponse(Purchase purchase);

}
