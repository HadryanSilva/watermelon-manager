package br.com.hadryan.api.auth;

import br.com.hadryan.api.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public record AuthUser(String email, String password,
                       Collection<? extends GrantedAuthority> authorities) implements UserDetails {

    public static AuthUser create(User user) {
        List<SimpleRole> simpleRoles = user.getRoles().stream()
                .map(role -> new SimpleRole(role.getAuthority()))
                .collect(Collectors.toList());
        return new AuthUser(user.getEmail(), user.getPassword(), simpleRoles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
