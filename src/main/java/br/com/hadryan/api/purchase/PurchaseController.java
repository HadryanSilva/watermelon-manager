package br.com.hadryan.api.purchase;

import br.com.hadryan.api.purchase.request.PurchasePostRequest;
import br.com.hadryan.api.purchase.response.PurchaseResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseMapper purchaseMapper;

    public PurchaseController(PurchaseService purchaseService,
                              PurchaseMapper purchaseMapper) {
        this.purchaseService = purchaseService;
        this.purchaseMapper = purchaseMapper;
    }

    @GetMapping("/list")
    public ResponseEntity<Page<PurchaseResponse>> findAllByAccount(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        var purchases = purchaseService.findAllByAccountId(PageRequest.of(page, size))
                .map(purchaseMapper::purchaseToResponse);

        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> findById(@PathVariable Long id) {
        var purchaseFound = purchaseService.findById(id);
        return ResponseEntity.ok(purchaseMapper.purchaseToResponse(purchaseFound));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> save(@Valid @RequestBody PurchasePostRequest request) {
        var purchaseToSave = purchaseMapper.postToPurchase(request);

        var purchaseSaved = purchaseService.save(request.items(), purchaseToSave);

        return ResponseEntity
                .created(URI.create("/api/v1/purchases/" + purchaseSaved.getId()))
                .body(purchaseMapper.purchaseToResponse(purchaseSaved));
    }

}
