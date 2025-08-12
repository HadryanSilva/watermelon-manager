package br.com.hadryan.api.auth.response;

public record AuthResponse(
        String session,
        String message
) {
}
