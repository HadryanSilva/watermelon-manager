package br.com.hadryan.api.customer;

import br.com.hadryan.api.customer.request.CustomerPostRequest;
import br.com.hadryan.api.customer.request.CustomerPutRequest;
import br.com.hadryan.api.customer.response.CustomerResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService,
                              CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @GetMapping("/list")
    public ResponseEntity<Page<CustomerResponse>> findAllByAccount(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        var customers = customerService.findAllByCurrentAccount(PageRequest.of(page, size))
                .map(customerMapper::customerToResponse);

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
        var customer = customerService.findById(id);
        return ResponseEntity.ok(customerMapper.customerToResponse(customer));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerPostRequest request) {
        var customerToSave = customerMapper.postToCustomer(request);
        var customerSaved = customerService.save(customerToSave);
        return ResponseEntity
                .created(URI.create("/api/v1/customers/" + customerSaved.getId()))
                .body(customerMapper.customerToResponse(customerSaved));
    }

    @PutMapping
    public ResponseEntity<CustomerResponse> update(@Valid @RequestBody CustomerPutRequest request) {
        var customerToUpdate = customerMapper.putToCustomer(request);
        var customerUpdated = customerService.update(customerToUpdate);
        return ResponseEntity.ok(customerMapper.customerToResponse(customerUpdated));
    }

}
