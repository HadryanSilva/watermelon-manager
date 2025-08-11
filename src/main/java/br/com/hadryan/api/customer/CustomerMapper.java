package br.com.hadryan.api.customer;

import br.com.hadryan.api.customer.request.CustomerPostRequest;
import br.com.hadryan.api.customer.request.CustomerPutRequest;
import br.com.hadryan.api.customer.response.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    Customer postToCustomer(CustomerPostRequest request);

    Customer putToCustomer(CustomerPutRequest request);

    CustomerResponse customerToResponse(Customer customer);

}
