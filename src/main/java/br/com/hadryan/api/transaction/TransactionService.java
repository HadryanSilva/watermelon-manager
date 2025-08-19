package br.com.hadryan.api.transaction;

import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import br.com.hadryan.api.transaction.dto.TransactionDTO;
import br.com.hadryan.api.transaction.dto.UpdateAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TransactionRepository transactionRepository;
    private final SecurityService securityService;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              SecurityService securityService,
                              ApplicationEventPublisher applicationEventPublisher,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.securityService = securityService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.transactionMapper = transactionMapper;
    }

    public Page<Transaction> findAllByAccount(Pageable pageable) {
        var accountId = securityService.getCurrentAccountId();
        return transactionRepository.findAllByAccountId(pageable, accountId);
    }

    public Transaction findById(Long id) {
        var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        var accountId = transaction.getAccount().getId();
        if (!securityService.hasAccessToAccount(accountId)) {
            throw new AccessDeniedException("You don't have access to this transaction");
        }

        return transaction;
    }

    public Transaction save(Transaction transaction) {
        var account = securityService.getCurrentAccount();
        transaction.setAccount(account);
        var transactionSaved = transactionRepository.save(transaction);
        sendUpdateAccountEvent(transactionSaved);
        return transactionSaved;
    }

    @EventListener
    public void saveTransactionAsync(TransactionDTO transaction) {
        log.info("Saving transaction {}", transaction);
        var account = securityService.getCurrentAccount();
        var transactionToSave = transactionMapper.dtoToTransaction(transaction);
        transactionToSave.setAccount(account);
        var transactionSaved = transactionRepository.save(transactionToSave);
        sendUpdateAccountEvent(transactionSaved);
    }

    private void sendUpdateAccountEvent(Transaction transaction) {
        log.debug("Transaction saved, sending update account event...");
        var dto = new UpdateAccountDTO(transaction.getType(), transaction.getAmount());
        applicationEventPublisher.publishEvent(dto);
    }

}
