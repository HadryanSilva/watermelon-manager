package br.com.hadryan.api.product;

import br.com.hadryan.api.product.request.ProductPostRequest;
import br.com.hadryan.api.product.response.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        var productFound = productService.findById(id);
        return ResponseEntity.ok(productMapper.productToResponse(productFound));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> save(@Valid @RequestBody ProductPostRequest request) {
        var productToSave = productMapper.postToProduct(request);
        var productSaved = productService.save(productToSave);
        return ResponseEntity
                .created(URI.create("/api/v1/products/" + productSaved.getId()))
                .body(productMapper.productToResponse(productSaved));
    }

}
