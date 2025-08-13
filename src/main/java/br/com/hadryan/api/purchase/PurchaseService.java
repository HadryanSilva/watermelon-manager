package br.com.hadryan.api.purchase;

import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import br.com.hadryan.api.product.ProductRepository;
import br.com.hadryan.api.purchase.enums.Status;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    private final PurchaseRepository purchaseRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final SecurityService securityService;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           ItemRepository itemRepository,
                           ProductRepository productRepository,
                           SecurityService securityService) {
        this.purchaseRepository = purchaseRepository;
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
        this.securityService = securityService;
    }

    public Page<Purchase> findAllByAccountId(Pageable page) {
        var accountId = securityService.getCurrentAccountId();

        return purchaseRepository.findAllByAccountWithItems(page, accountId);
    }

    public Purchase findById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", id));
    }

    @Transactional
    public Purchase save(List<Item> items, Purchase purchase) {
        purchase.setStatus(Status.CREATED);
        var purchaseSaved = purchaseRepository.save(purchase);

        var savedItems = saveItems(items, purchaseSaved);
        purchaseSaved.setItems(savedItems);
        return purchaseSaved;
    }

    private List<Item> saveItems(List<Item> items, Purchase purchase) {
        log.info("Saving purchase items...");
        List<Item> savedItems = new ArrayList<>();
        items.forEach(item -> {
            var product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", item.getProduct().getId()));
            item.setProduct(product);
            item.setPurchase(purchase);
            var itemSaved = itemRepository.save(item);
            savedItems.add(itemSaved);
        });
        return savedItems;
    }

}
