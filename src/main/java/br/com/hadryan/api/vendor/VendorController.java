package br.com.hadryan.api.vendor;

import br.com.hadryan.api.vendor.request.VendorPostRequest;
import br.com.hadryan.api.vendor.request.VendorPutRequest;
import br.com.hadryan.api.vendor.response.VendorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendors")
public class VendorController {

    private final VendorService vendorService;
    private final VendorMapper vendorMapper;

    public VendorController(VendorService vendorService,  VendorMapper vendorMapper) {
        this.vendorService = vendorService;
        this.vendorMapper = vendorMapper;
    }

    @GetMapping("/list")
    public ResponseEntity<List<VendorResponse>> findVendorsByAccount(@RequestParam int page,
                                                                     @RequestParam int size) {
        var vendors = vendorService.findAllByCurrentAccount(PageRequest.of(page, size)).stream().toList();

        return ResponseEntity.ok(vendorMapper.vendorsToResponse(vendors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorResponse> findById(@PathVariable Long id) {
        var vendorFound = vendorService.findById(id);
        return ResponseEntity.ok(vendorMapper.vendorToResponse(vendorFound));
    }

    @PostMapping
    public ResponseEntity<VendorResponse> save(@RequestBody VendorPostRequest request) {
        var vendorToSave = vendorMapper.postToVendor(request);
        var vendorSaved = vendorService.save(vendorToSave);
        return ResponseEntity.ok(vendorMapper.vendorToResponse(vendorSaved));
    }

    @PutMapping
    public ResponseEntity<VendorResponse> update(VendorPutRequest request) {
        var vendorToUpdate = vendorMapper.putToVendor(request);
        var vendorUpdated = vendorService.update(vendorToUpdate);
        return ResponseEntity.ok(vendorMapper.vendorToResponse(vendorUpdated));
    }

}
