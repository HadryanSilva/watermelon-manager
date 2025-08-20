package br.com.hadryan.api.order;

import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.customer.CustomerRepository;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import br.com.hadryan.api.field.FieldRepository;
import br.com.hadryan.api.transaction.dto.TransactionDTOFactory;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final FieldRepository fieldRepository;
    private final SecurityService securityService;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        FieldRepository fieldRepository,
                        SecurityService securityService,
                        ApplicationEventPublisher applicationEventPublisher) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.fieldRepository = fieldRepository;
        this.securityService = securityService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Page<Order> findAllByAccountId(Pageable pageable) {
        var accountId = securityService.getCurrentAccountId();
        return orderRepository.findAllByAccountId(pageable, accountId);
    }

    public Order findById(Long id) {
        var order = orderRepository.findWithRelationsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        var accountId = order.getAccount().getId();
        if (!securityService.hasAccessToAccount(accountId)) {
            throw new AccessDeniedException("You don't have access to this order");
        }

        return order;
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
        var account = securityService.getCurrentAccount();
        order.setAccount(account);
        order.setField(field);
        return orderRepository.save(order);
    }

    public void update(Order order) {
        var existingOrder = findById(order.getId());

        existingOrder.setPricePerKg(order.getPricePerKg());
        existingOrder.setTotal(order.getTotal());
        existingOrder.setTransportationPlate(order.getTransportationPlate());
        existingOrder.setOrderStatus(order.getOrderStatus());

        orderRepository.save(existingOrder);

        if (existingOrder.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            sendAccountUpdateEvent(existingOrder);
        }
    }

    private void sendAccountUpdateEvent(Order order) {
        var dto = TransactionDTOFactory.createFromOrder(order);

        applicationEventPublisher.publishEvent(dto);
    }

}
