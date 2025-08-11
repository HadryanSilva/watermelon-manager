package br.com.hadryan.api.user;

import br.com.hadryan.api.account.Account;
import br.com.hadryan.api.account.AccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;

    public UserService(UserRepository userRepository,  AccountService accountService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public User create(Long accountId, User user) {
        var account = handleAccountCreation(accountId);
        user.setAccount(account);
        var userSaved = userRepository.save(user);
        account.getUsers().add(userSaved);
        accountService.save(account);
        return userSaved;
    }

    public User update(User user) {
        var userFound = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        userFound.setFirstName(user.getFirstName());
        userFound.setLastName(user.getLastName());
        userFound.setPhone(user.getPhone());
        userFound.setEmail(user.getEmail());
        userFound.setPassword(user.getPassword());
        return userRepository.save(userFound);
    }

    private Account handleAccountCreation(Long accountId) {
        if (accountId != null) {
            return accountService.findById(accountId);
        }
        Account account = new Account();
        account.setBalance(BigDecimal.ZERO);
        account.setIncomes(BigDecimal.ZERO);
        account.setExpenses(BigDecimal.ZERO);
        return account;
    }

}
