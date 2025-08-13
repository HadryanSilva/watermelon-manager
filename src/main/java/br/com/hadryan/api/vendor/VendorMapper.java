package br.com.hadryan.api.vendor;

import br.com.hadryan.api.vendor.request.VendorPostRequest;
import br.com.hadryan.api.vendor.request.VendorPutRequest;
import br.com.hadryan.api.vendor.response.VendorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VendorMapper {

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "account",  ignore = true)
    Vendor postToVendor(VendorPostRequest request);

    @Mapping(target = "account", ignore = true)
    Vendor putToVendor(VendorPutRequest request);

    VendorResponse vendorToResponse(Vendor vendor);

}
