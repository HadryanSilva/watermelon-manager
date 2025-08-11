package br.com.hadryan.api.order;

import br.com.hadryan.api.customer.CustomerRepository;
import br.com.hadryan.api.field.FieldRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final FieldRepository fieldRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        FieldRepository fieldRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.fieldRepository = fieldRepository;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order Not Found"));
    }

    public Order save(Long customerId, Long fieldId, Order order) {
        if (order.getOrderStatus() == null) {
            order.setOrderStatus(OrderStatus.RECEIVED);
        }
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));
        order.setCustomer(customer);

        var  field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Field Not Found"));

        order.setField(field);
        return orderRepository.save(order);
    }

}
