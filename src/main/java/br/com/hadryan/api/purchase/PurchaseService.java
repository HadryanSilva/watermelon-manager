package br.com.hadryan.api.purchase;

import br.com.hadryan.api.product.ProductRepository;
import br.com.hadryan.api.purchase.enums.Status;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    private final PurchaseRepository purchaseRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           ItemRepository itemRepository,
                           ProductRepository productRepository) {
        this.purchaseRepository = purchaseRepository;
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
    }

    public Purchase findById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase with id " + id + " not found"));
    }

    @Transactional
    public Purchase save(List<Item> items, Purchase purchase) {
        var savedItems = saveItems(items);
        purchase.setStatus(Status.CREATED);
        var purchaseSaved = purchaseRepository.save(purchase);
        purchaseSaved.setItems(savedItems);
        return purchaseSaved;
    }

    private List<Item> saveItems(List<Item> items) {
        log.info("Saving purchase items...");
        List<Item> savedItems = new ArrayList<>();
        items.forEach(item -> {
            var product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(
                            () -> new RuntimeException("Product with id " + item.getProduct().getId() + " not found")
                    );
            item.setProduct(product);
            var itemSaved = itemRepository.save(item);
            savedItems.add(itemSaved);
        });
        return savedItems;
    }

}
