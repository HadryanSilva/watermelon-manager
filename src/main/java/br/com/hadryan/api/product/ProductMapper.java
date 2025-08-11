package br.com.hadryan.api.product;

import br.com.hadryan.api.product.request.ProductPostRequest;
import br.com.hadryan.api.product.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id",  ignore = true)
    Product postToProduct(ProductPostRequest request);

    ProductResponse productToResponse(Product product);

}
