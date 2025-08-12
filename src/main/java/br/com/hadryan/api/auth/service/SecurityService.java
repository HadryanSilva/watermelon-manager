package br.com.hadryan.api.auth.service;

import br.com.hadryan.api.account.Account;
import br.com.hadryan.api.auth.AuthUser;
import br.com.hadryan.api.exception.UnauthorizedException;
import br.com.hadryan.api.user.User;
import br.com.hadryan.api.user.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SecurityService {

    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof AuthUser authUser) {
            return userRepository.findByEmail(authUser.getUsername());
        }

        return Optional.empty();
    }

    public Account getCurrentAccount() {
        return getCurrentUser()
                .map(User::getAccount)
                .orElseThrow(() -> new UnauthorizedException("User not authenticated or account not found"));
    }

    public UUID getCurrentAccountId() {
        return getCurrentAccount().getId();
    }

    public boolean hasAccessToAccount(UUID accountId) {
        if (accountId == null) {
            return false;
        }

        try {
            UUID currentAccountId = getCurrentAccountId();
            return currentAccountId.equals(accountId);
        } catch (Exception e) {
            return false;
        }
    }

    public void validateAccountAccess(UUID accountId) {
        if (!hasAccessToAccount(accountId)) {
            throw new AccessDeniedException("Access denied to this account's resources");
        }
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

}
