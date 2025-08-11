package br.com.hadryan.api.field;

import br.com.hadryan.api.field.request.FieldPostRequest;
import br.com.hadryan.api.field.response.FieldResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/fields")
public class FieldController {

    private final FieldService fieldService;
    private final FieldMapper fieldMapper;

    public FieldController(FieldService fieldService, FieldMapper fieldMapper) {
        this.fieldService = fieldService;
        this.fieldMapper = fieldMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldResponse> findById(@PathVariable Long id) {
        var field = fieldService.getById(id);
        return ResponseEntity.ok(fieldMapper.fieldToResponse(field));
    }

    @PostMapping
    public ResponseEntity<FieldResponse> save(@RequestBody FieldPostRequest request) {
        var fieldToSave = fieldMapper.postToField(request);
        var fieldSaved = fieldService.save(request.accountId(),  fieldToSave);
        return ResponseEntity
                .created(URI.create("/api/v1/fields/" + fieldSaved.getId()))
                .body(fieldMapper.fieldToResponse(fieldSaved));
    }

}
