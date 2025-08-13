package br.com.hadryan.api.field;

import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class FieldService {

    private final FieldRepository fieldRepository;
    private final SecurityService securityService;

    public FieldService(FieldRepository fieldRepository,
                        SecurityService securityService) {
        this.fieldRepository = fieldRepository;
        this.securityService = securityService;
    }

    public Page<Field> findAllByAccountId(Pageable pageable) {
        var accountId = securityService.getCurrentAccountId();
        return fieldRepository.findByAccountId(accountId, pageable);
    }

    public Field getById(Long id) {
        var field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field", id));

        var accountId = field.getAccount().getId();
        if (!securityService.hasAccessToAccount(accountId)) {
            throw new AccessDeniedException("You don't have access to this field");
        }

        return field;
    }

    @Transactional
    public Field save(Field field) {
        var account = securityService.getCurrentAccount();
        field.setAccount(account);
        return fieldRepository.save(field);
    }

}
