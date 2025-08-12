package br.com.hadryan.api.vendor;

import br.com.hadryan.api.account.AccountService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    private final AccountService accountService;

    public VendorService(VendorRepository vendorRepository,  AccountService accountService) {
        this.vendorRepository = vendorRepository;
        this.accountService = accountService;
    }

    public Vendor findById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor Not Found"));
    }

    public Vendor save(UUID accountId, Vendor vendor) {
        var account = accountService.findById(accountId);
        vendor.setAccount(account);
        return vendorRepository.save(vendor);
    }

    public Vendor update(Vendor vendor) {
        var vendorToUpdate = findById(vendor.getId());
        vendorToUpdate.setName(vendor.getName());
        vendorToUpdate.setLocation(vendor.getLocation());
        vendorToUpdate.setPhone(vendor.getPhone());
        vendorToUpdate.setEmail(vendor.getEmail());
        return vendorRepository.save(vendorToUpdate);
    }

}
