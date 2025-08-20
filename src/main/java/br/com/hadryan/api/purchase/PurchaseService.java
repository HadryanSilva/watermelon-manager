package br.com.hadryan.api.purchase;

import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import br.com.hadryan.api.product.Product;
import br.com.hadryan.api.product.ProductRepository;
import br.com.hadryan.api.purchase.enums.Status;
import br.com.hadryan.api.transaction.dto.TransactionDTOFactory;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PurchaseRepository purchaseRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final SecurityService securityService;

    public PurchaseService(ApplicationEventPublisher applicationEventPublisher,
                           PurchaseRepository purchaseRepository,
                           ItemRepository itemRepository,
                           ProductRepository productRepository,
                           SecurityService securityService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.purchaseRepository = purchaseRepository;
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
        this.securityService = securityService;
    }

    public Page<Purchase> findAllByAccountId(Pageable page) {
        var accountId = securityService.getCurrentAccountId();
        return purchaseRepository.findAllByAccountWithItems(page, accountId);
    }

    public Page<Purchase> findAllByAccountIdComplete(Pageable page) {
        var accountId = securityService.getCurrentAccountId();
        return purchaseRepository.findAllByAccountWithItemsAndProducts(page, accountId);
    }

    public Purchase findById(Long id) {
        var accountId = securityService.getCurrentAccountId();
        return purchaseRepository.findFullPurchaseById(id, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", id));
    }

    @Transactional
    public Purchase save(List<Item> items, Purchase purchase) {
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

    public void update(Purchase purchase) {
        var existentPurchase = findById(purchase.getId());

        if (!securityService.hasAccessToAccount(existentPurchase.getAccount().getId())) {
            throw new AccessDeniedException("You are not authorized to perform this action.");
        }

        existentPurchase.setName(purchase.getName());
        existentPurchase.setDescription(purchase.getDescription());
        existentPurchase.setStatus(purchase.getStatus());
        if (purchase.getStatus().equals(Status.PAID)) {
            existentPurchase.setStatus(purchase.getStatus());
            sendSaveTransactionEvent(existentPurchase);
        }

        purchaseRepository.save(existentPurchase);
    }

    private List<Item> saveItems(List<Item> items, Purchase purchase) {
        log.info("Saving {} purchase items...", items.size());

        Set<Long> productIds = items.stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toSet());

        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        validateAllProductsExist(productIds, productMap.keySet());

        List<Item> itemsToSave = items.stream()
                .peek(item -> {
                    item.setProduct(productMap.get(item.getProduct().getId()));
                    item.setPurchase(purchase);
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

    private void sendSaveTransactionEvent(Purchase purchase) {
        var dto = TransactionDTOFactory.createFromPurchase(purchase);

        applicationEventPublisher.publishEvent(dto);
    }

}
