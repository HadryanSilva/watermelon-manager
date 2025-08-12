package br.com.hadryan.api.auth;

import br.com.hadryan.api.auth.request.LoginRequest;
import br.com.hadryan.api.auth.response.AuthResponse;
import br.com.hadryan.api.auth.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final SessionService sessionService;

    public AuthController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        var authentication = sessionService.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.email(), loginRequest.password()));

        var session = sessionService.createNewSession(authentication, request);
        return ResponseEntity.ok(new AuthResponse("Login Successful", session.getId()));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        sessionService.logout(request.getSession());
        return ResponseEntity.ok(new AuthResponse("Logout Successful", null));
    }

}
