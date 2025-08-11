package br.com.hadryan.api.transaction;

import br.com.hadryan.api.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService,
                              ApplicationEventPublisher applicationEventPublisher, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.transactionMapper = transactionMapper;
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction save(Long accountId, Transaction transaction) {
        var account = accountService.findById(accountId);
        transaction.setAccount(account);
        var transactionSaved = transactionRepository.save(transaction);
        sendUpdateAccountEvent(transactionSaved);
        return transactionSaved;
    }

    private void sendUpdateAccountEvent(Transaction transaction) {
        log.debug("Transaction saved, sending update account event...");
        var dto = transactionMapper.transactionToDTO(transaction);
        applicationEventPublisher.publishEvent(dto);
    }

}
