package br.com.hadryan.api.order;

import br.com.hadryan.api.order.request.OrderPostRequest;
import br.com.hadryan.api.order.response.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order postToOrder(OrderPostRequest request);

    OrderResponse orderToResponse(Order order);

}
