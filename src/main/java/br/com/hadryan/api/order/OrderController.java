package br.com.hadryan.api.order;

import br.com.hadryan.api.order.request.OrderPostRequest;
import br.com.hadryan.api.order.response.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService,  OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        var orderFound = orderService.findById(id);
        return ResponseEntity.ok(orderMapper.orderToResponse(orderFound));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> save(@Valid @RequestBody OrderPostRequest request) {
        var orderToSave = orderMapper.postToOrder(request);
        var orderSaved = orderService.save(request.customerId(), request.fieldId(), orderToSave);
        return ResponseEntity
                .created(URI.create("/api/v1/orders/" + orderSaved.getId()))
                .body(orderMapper.orderToResponse(orderSaved));
    }

}
