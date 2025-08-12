package br.com.hadryan.api.user;

import br.com.hadryan.api.account.Account;
import br.com.hadryan.api.account.AccountService;
import br.com.hadryan.api.auth.Role;
import br.com.hadryan.api.auth.RoleRepository;
import br.com.hadryan.api.auth.service.SecurityService;
import br.com.hadryan.api.exception.BusinessException;
import br.com.hadryan.api.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final RoleRepository roleRepository;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       AccountService accountService,
                       RoleRepository roleRepository,
                       SecurityService securityService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.roleRepository = roleRepository;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }

    /**
     * Public Register - Creates ADMIN user with Account
     * Only used to create an initial user
     */
    @Transactional
    public User registerNewAdmin(User user) {
        log.info("Registering new admin user with email: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessException("Email already registered: " + user.getEmail());
        }

        Account newAccount = createNewAccount();
        user.setAccount(newAccount);

        Role adminRole = findOrCreateRole("ROLE_ADMIN");
        user.getRoles().clear();
        user.getRoles().add(adminRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        newAccount.getUsers().add(savedUser);
        accountService.save(newAccount);

        log.info("New admin user registered successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Internal Creation - Add user to existing account
     * Authentication and Admin permission required
     */
    @Transactional
    public User createInternalUser(User user, List<String> roleNames) {
        log.info("Creating internal user with email: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessException("Email already registered: " + user.getEmail());
        }

        if (!securityService.isAdmin()) {
            throw new BusinessException("Only administrators can create internal users");
        }

        Account currentAccount = securityService.getCurrentAccount();
        user.setAccount(currentAccount);

        assignRoles(user, roleNames);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        currentAccount.getUsers().add(savedUser);
        accountService.save(currentAccount);

        log.info("Internal user created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Update existent User
     * Required permissions validation
     */
    @Transactional
    public User update(User userUpdate) {
        log.info("Updating user with id: {}", userUpdate.getId());

        User existingUser = findById(userUpdate.getId());

        UUID currentUserId = securityService.getCurrentUser()
                .map(User::getId)
                .orElseThrow(() -> new BusinessException("User not authenticated"));

        boolean isSelfUpdate = currentUserId.equals(existingUser.getId());
        boolean isAdminOfSameAccount = securityService.isAdmin() &&
                securityService.hasAccessToAccount(existingUser.getAccount().getId());

        if (!isSelfUpdate && !isAdminOfSameAccount) {
            throw new BusinessException("You don't have permission to update this user");
        }

        if (!existingUser.getEmail().equals(userUpdate.getEmail())) {
            if (userRepository.findByEmail(userUpdate.getEmail()).isPresent()) {
                throw new BusinessException("Email already in use: " + userUpdate.getEmail());
            }
        }

        existingUser.setFirstName(userUpdate.getFirstName());
        existingUser.setLastName(userUpdate.getLastName());
        existingUser.setPhone(userUpdate.getPhone());
        existingUser.setEmail(userUpdate.getEmail());

        if (userUpdate.getPassword() != null && !userUpdate.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        }

        User updated = userRepository.save(existingUser);
        log.info("User updated successfully");

        return updated;
    }

    /**
     * Update user roles (ONLY ADMIN)
     */
    @Transactional
    public User updateUserRoles(UUID userId, List<String> roleNames) {
        log.info("Updating roles for user: {}", userId);

        if (!securityService.isAdmin()) {
            throw new BusinessException("Only administrators can update user roles");
        }

        User user = findById(userId);

        // Verificar se o admin tem acesso à conta do usuário
        if (!securityService.hasAccessToAccount(user.getAccount().getId())) {
            throw new BusinessException("You can only manage users from your own account");
        }

        // Limpar roles antigas e atribuir novas
        user.getRoles().clear();
        assignRoles(user, roleNames);

        User updated = userRepository.save(user);
        log.info("User roles updated successfully");

        return updated;
    }

    private Account handleAccountCreation(UUID accountId) {
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

    private Account createNewAccount() {
        Account account = new Account();
        account.setBalance(BigDecimal.ZERO);
        account.setIncomes(BigDecimal.ZERO);
        account.setExpenses(BigDecimal.ZERO);
        return accountService.save(account);
    }

    private void assignRoles(User user, List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new BusinessException("At least one role must be specified for internal users");
        }

        user.getRoles().clear();
        for (String roleName : roleNames) {
            Role role = findOrCreateRole(roleName);
            user.getRoles().add(role);
        }
    }

    private Role findOrCreateRole(String roleName) {
        // Aqui você poderia implementar uma busca por nome primeiro
        // Por enquanto, criando nova role sempre (ajustar conforme necessário)
        Role role = new Role();
        role.setRole(roleName);
        return roleRepository.save(role);
    }

}
