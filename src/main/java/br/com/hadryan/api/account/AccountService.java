package br.com.hadryan.api.account;

import br.com.hadryan.api.transaction.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @EventListener
    public void updateAccountBalance(TransactionDTO transaction) {
        log.info("Updating account balance...");
        switch (transaction.type()) {
            case INCOME -> processAccountIncome(transaction);
            case EXPENSE -> processAccountExpense(transaction);
        }
    }

    private void processAccountIncome(TransactionDTO transaction) {
        log.info("Transaction was identified as income, processing...");
        var account = accountRepository.findById(transaction.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        var amount = transaction.amount();
        account.setIncomes(account.getIncomes().add(amount));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    private void processAccountExpense(TransactionDTO transaction) {
        log.info("Transaction was identified as expense, processing...");
        var account = accountRepository.findById(transaction.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        var amount = transaction.amount();
        account.setExpenses(account.getExpenses().add(amount));
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

}
