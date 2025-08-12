package br.com.hadryan.api.auth;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;

public class SimpleRole implements GrantedAuthority, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String authority;

    public SimpleRole(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SimpleRole that = (SimpleRole) obj;
        return authority.equals(that.authority);
    }

    @Override
    public int hashCode() {
        return authority.hashCode();
    }
}
