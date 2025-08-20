package br.com.hadryan.api.order;

import br.com.hadryan.api.order.request.OrderPostRequest;
import br.com.hadryan.api.order.request.OrderPutRequest;
import br.com.hadryan.api.order.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "account",   ignore = true)
    @Mapping(target = "customer.id", source = "customerId")
    @Mapping(target = "field.id", source = "fieldId")
    Order postToOrder(OrderPostRequest request);

    @Mapping(target = "account",   ignore = true)
    @Mapping(target = "customer.id", source = "customerId")
    @Mapping(target = "field.id", source = "fieldId")
    Order putToOrder(OrderPutRequest request);

    OrderResponse orderToResponse(Order order);

}
