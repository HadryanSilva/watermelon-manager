package br.com.hadryan.api.order;

import br.com.hadryan.api.order.request.OrderPostRequest;
import br.com.hadryan.api.order.request.OrderPutRequest;
import br.com.hadryan.api.order.response.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService,
                           OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/list")
    public ResponseEntity<Page<OrderResponse>> findAllByAccount(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        var orders = orderService.findAllByAccountId(PageRequest.of(page, size))
                .map(orderMapper::orderToResponse);

        return ResponseEntity.ok(orders);
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

    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody OrderPutRequest request) {
        orderService.update(orderMapper.putToOrder(request));

        return ResponseEntity.noContent().build();
    }

}
