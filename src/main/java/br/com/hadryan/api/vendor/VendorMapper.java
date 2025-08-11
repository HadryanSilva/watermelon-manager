package br.com.hadryan.api.vendor;

import br.com.hadryan.api.vendor.request.VendorPostRequest;
import br.com.hadryan.api.vendor.request.VendorPutRequest;
import br.com.hadryan.api.vendor.response.VendorResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VendorMapper {

    Vendor postToVendor(VendorPostRequest request);

    Vendor putToVendor(VendorPutRequest request);

    VendorResponse vendorToResponse(Vendor vendor);

}
