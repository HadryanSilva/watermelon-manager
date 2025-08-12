package br.com.hadryan.api.customer;

import br.com.hadryan.api.account.Account;
import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.exception.BusinessException;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final SecurityService securityService;

    public CustomerService(CustomerRepository customerRepository, SecurityService securityService) {
        this.customerRepository = customerRepository;
        this.securityService = securityService;
    }

    public Customer findById(Long id) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));

        UUID customerAccountId = customer.getAccount().getId();
        if (!securityService.hasAccessToAccount(customerAccountId)) {
            log.warn("User tried to access customer {} from different account", id);
            throw new AccessDeniedException("You don't have access to this customer");
        }

        return customer;
    }

    /**
     * Lista customers apenas do Account do usuário atual
     */
    public Page<Customer> findAllByCurrentAccount(Pageable pageable) {
        Account currentAccount = securityService.getCurrentAccount();
        log.debug("Listing customers for account: {}", currentAccount.getId());

        return customerRepository.findByAccountId(currentAccount.getId(), pageable);
    }

    @Transactional
    public Customer save(Customer customer) {
        log.info("Creating new customer: {}", customer.getName());

        validateUniqueConstraints(customer);

        Account currentAccount = securityService.getCurrentAccount();
        customer.setAccount(currentAccount);

        Customer saved = customerRepository.save(customer);
        log.info("Customer created successfully with id: {}", saved.getId());

        return saved;
    }

    @Transactional
    public Customer update(Customer customerUpdate) {
        log.info("Updating customer with id: {}", customerUpdate.getId());

        Customer existing = findById(customerUpdate.getId());

        if (!existing.getPhone().equals(customerUpdate.getPhone())) {
            validatePhoneUnique(customerUpdate.getPhone(), customerUpdate.getId());
        }
        if (!existing.getEmail().equals(customerUpdate.getEmail())) {
            validateEmailUnique(customerUpdate.getEmail(), customerUpdate.getId());
        }

        existing.setName(customerUpdate.getName());
        existing.setPhone(customerUpdate.getPhone());
        existing.setEmail(customerUpdate.getEmail());
        existing.setLocation(customerUpdate.getLocation());

        Customer updated = customerRepository.save(existing);
        log.info("Customer updated successfully");

        return updated;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting customer with id: {}", id);

        Customer customer = findById(id);

        if (hasActiveOrders(customer)) {
            throw new BusinessException("Cannot delete customer with active orders");
        }

        customerRepository.delete(customer);
        log.info("Customer deleted successfully");
    }

    private boolean hasActiveOrders(Customer customer) {
        return customerRepository.hasActiveOrders(customer.getId());
    }

    private void validateUniqueConstraints(Customer customer) {
        validatePhoneUnique(customer.getPhone(), customer.getId());
        validateEmailUnique(customer.getEmail(), customer.getId());
    }

    private void validatePhoneUnique(String phone, Long excludeId) {
        if (phone != null && customerRepository.existsByPhoneAndIdNot(phone, excludeId)) {
            throw new BusinessException("Phone number already in use: " + phone);
        }
    }

    private void validateEmailUnique(String email, Long excludeId) {
        if (email != null && customerRepository.existsByEmailAndIdNot(email, excludeId)) {
            throw new BusinessException("Email already in use: " + email);
        }
    }

}
