package br.com.hadryan.api.user;

import br.com.hadryan.api.account.Account;
import br.com.hadryan.api.account.AccountService;
import br.com.hadryan.api.auth.Role;
import br.com.hadryan.api.auth.RoleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                       AccountService accountService,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.roleRepository = roleRepository;
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public User create(Long accountId, User user) {
        var account = handleAccountCreation(accountId);
        user.setAccount(account);
        handleUserRoleCreation(user);
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

    private void handleUserRoleCreation(User user) {
        if (user.getRoles().isEmpty()) {
            Role role = new Role();
            role.setRole("ROLE_ADMIN");
            var roleCreated = roleRepository.save(role);
            user.getRoles().add(roleCreated);
        }
    }

}
