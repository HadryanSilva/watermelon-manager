package br.com.hadryan.api.purchase;

import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import br.com.hadryan.api.product.Product;
import br.com.hadryan.api.product.ProductRepository;
import br.com.hadryan.api.purchase.enums.Status;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    private final PurchaseRepository purchaseRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final SecurityService securityService;
    private final ItemMapper itemMapper;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           ItemRepository itemRepository,
                           ProductRepository productRepository,
                           SecurityService securityService,
                           ItemMapper itemMapper) {
        this.purchaseRepository = purchaseRepository;
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
        this.securityService = securityService;
        this.itemMapper = itemMapper;
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
    public Purchase save(List<ItemDTO> items, Purchase purchase) {
        var account = securityService.getCurrentAccount();
        purchase.setAccount(account);

        purchase.setStatus(Status.CREATED);
        purchase.setTotal(BigDecimal.ZERO);
        var purchaseSaved = purchaseRepository.save(purchase);

        var savedItems = saveItems(items, purchaseSaved);
        purchaseSaved.setItems(savedItems);
        purchaseSaved.setTotal(calculateTotal(savedItems));

        return purchaseRepository.save(purchaseSaved);
    }

    private List<Item> saveItems(List<ItemDTO> items, Purchase purchase) {
        log.info("Saving {} purchase items...", items.size());

        Set<Long> productIds = items.stream()
                .map(ItemDTO::productId)
                .collect(Collectors.toSet());

        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        validateAllProductsExist(productIds, productMap.keySet());

        List<Item> itemsToSave = items.stream()
                .map(itemDTO -> {
                    Item item = itemMapper.dtoToItem(itemDTO);
                    item.setProduct(productMap.get(itemDTO.productId()));
                    item.setPurchase(purchase);
                    return item;
                })
                .collect(Collectors.toList());

        return itemRepository.saveAll(itemsToSave);
    }

    private void validateAllProductsExist(Set<Long> requestedIds, Set<Long> foundIds) {
        var missingIds = requestedIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());

        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException("Products not found: " + missingIds);
        }
    }

    private BigDecimal calculateTotal(List<Item> items) {
        return items.stream()
                .map(Item::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
