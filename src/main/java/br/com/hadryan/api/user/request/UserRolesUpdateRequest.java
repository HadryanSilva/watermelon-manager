package br.com.hadryan.api.user.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UserRolesUpdateRequest {

    @NotEmpty(message = "At least one role must be specified")
    private List<String> roles;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
