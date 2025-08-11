package br.com.hadryan.api.purchase;

import br.com.hadryan.api.purchase.request.PurchasePostRequest;
import br.com.hadryan.api.purchase.response.PurchaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseMapper purchaseMapper;
    private final ItemMapper itemMapper;

    public PurchaseController(PurchaseService purchaseService,
                              PurchaseMapper purchaseMapper,
                              ItemMapper itemMapper) {
        this.purchaseService = purchaseService;
        this.purchaseMapper = purchaseMapper;
        this.itemMapper = itemMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> findById(@PathVariable Long id) {
        var purchaseFound = purchaseService.findById(id);
        return ResponseEntity.ok(purchaseMapper.postToResponse(purchaseFound));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> save(@Valid @RequestBody PurchasePostRequest request) {
        var purchaseToSave = purchaseMapper.postToPurchase(request);
        var purchaseItems = itemMapper.itemsToDto(request.items());
        var purchaseSaved = purchaseService.save(purchaseItems, purchaseToSave);

        return ResponseEntity
                .created(URI.create("/api/v1/purchases/" + purchaseSaved.getId()))
                .body(purchaseMapper.postToResponse(purchaseSaved));
    }

}
