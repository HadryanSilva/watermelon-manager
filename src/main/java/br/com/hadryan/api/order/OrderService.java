package br.com.hadryan.api.order;

import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.customer.CustomerRepository;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import br.com.hadryan.api.field.FieldRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final FieldRepository fieldRepository;
    private final SecurityService securityService;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        FieldRepository fieldRepository,
                        SecurityService securityService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.fieldRepository = fieldRepository;
        this.securityService = securityService;
    }

    public Page<Order> findAllByAccountId(Pageable pageable) {
        var accountId = securityService.getCurrentAccountId();
        return orderRepository.findAllByAccountId(pageable, accountId);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Transactional
    public Order save(Long customerId, Long fieldId, Order order) {
        if (order.getOrderStatus() == null) {
            order.setOrderStatus(OrderStatus.RECEIVED);
        }
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        order.setCustomer(customer);

        var  field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Field", fieldId));

        order.setField(field);
        return orderRepository.save(order);
    }

}
