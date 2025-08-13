package br.com.hadryan.api.transaction;

import br.com.hadryan.api.transaction.request.TransactionPostRequest;
import br.com.hadryan.api.transaction.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService,  TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(@PathVariable Long id) {
        var transaction = transactionService.findById(id);
        return ResponseEntity.ok(transactionMapper.transactionToResponse(transaction));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionPostRequest request) {
        var transactionToCreate = transactionMapper.postToTransaction(request);
        var transactionCreated = transactionService.save(transactionToCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/transactions/" + transactionCreated.getId()))
                .body(transactionMapper.transactionToResponse(transactionCreated));
    }

}
