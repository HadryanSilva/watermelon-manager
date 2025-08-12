package br.com.hadryan.api.field;

import br.com.hadryan.api.account.AccountService;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FieldService {

    private final FieldRepository fieldRepository;
    private final AccountService accountService;

    public FieldService(FieldRepository fieldRepository,  AccountService accountService) {
        this.fieldRepository = fieldRepository;
        this.accountService = accountService;
    }

    public Field getById(Long id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field", id));
    }

    @Transactional
    public Field save(UUID accountId, Field field) {
        var account = accountService.findById(accountId);
        field.setAccount(account);
        return fieldRepository.save(field);
    }

}
