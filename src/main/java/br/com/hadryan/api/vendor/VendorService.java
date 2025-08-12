package br.com.hadryan.api.vendor;

import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.exception.BusinessException;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VendorService {
    private static final Logger log = LoggerFactory.getLogger(VendorService.class);

    private final VendorRepository vendorRepository;
    private final SecurityService securityService;

    public VendorService(VendorRepository vendorRepository,  SecurityService securityService) {
        this.vendorRepository = vendorRepository;
        this.securityService = securityService;
    }

    public Page<Vendor> findAllByCurrentAccount(Pageable pageable) {
        var accountId = securityService.getCurrentAccountId();

        log.debug("Listing vendors for account: {}", accountId);
        return vendorRepository.findByAccountId(accountId, pageable);
    }

    public Vendor findById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", id));
    }

    @Transactional
    public Vendor save(Vendor vendor) {
        var account = securityService.getCurrentAccount();
        vendor.setAccount(account);
        return vendorRepository.save(vendor);
    }

    public Vendor update(Vendor vendorUpdate) {
        log.info("Updating vendor with id: {}", vendorUpdate.getId());

        var existing = findById(vendorUpdate.getId());

        if (existing.getPhone().equals(vendorUpdate.getPhone())) {
            validatePhoneUnique(vendorUpdate.getPhone(), vendorUpdate.getId());
        }

        existing.setName(vendorUpdate.getName());
        existing.setLocation(vendorUpdate.getLocation());
        existing.setPhone(vendorUpdate.getPhone());
        existing.setEmail(vendorUpdate.getEmail());

        var updated = vendorRepository.save(existing);
        log.info("Vendor with id {} updated successfully", updated.getId());

        return updated;
    }

    private void validatePhoneUnique(String phone, Long excludeId) {
        if (phone != null && vendorRepository.existsByPhoneAndIdNot(phone, excludeId)) {
            throw new BusinessException("Phone number already in use: " + phone);
        }
    }

}
