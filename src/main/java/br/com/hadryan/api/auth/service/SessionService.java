package br.com.hadryan.api.auth.service;

import br.com.hadryan.api.auth.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final AuthenticationManager authenticationManager;
    private final SessionRegistry sessionRegistry;

    public SessionService(AuthenticationManager authenticationManager,
                          SessionRegistry sessionRegistry) {
        this.authenticationManager = authenticationManager;
        this.sessionRegistry = sessionRegistry;
    }

    public Authentication authenticate(UsernamePasswordAuthenticationToken credentials) throws AuthenticationException {
        return  authenticationManager.authenticate(credentials);
    }

    public HttpSession createNewSession(Authentication authentication, HttpServletRequest request) {
        AuthUser principal = (AuthUser) authentication.getPrincipal();

        HttpSession session = request.getSession();
        session.setAttribute("principal", principal);
        session.setMaxInactiveInterval(3600);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        sessionRegistry.registerNewSession(session.getId(), principal);
        return session;
    }

    public void logout(HttpSession session) {
        if (session != null) {
            sessionRegistry.removeSessionInformation(session.getId());
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

}
