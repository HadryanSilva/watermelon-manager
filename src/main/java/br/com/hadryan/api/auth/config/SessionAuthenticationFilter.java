package br.com.hadryan.api.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNullApi;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(SessionAuthenticationFilter.class);

    private final UserDetailsService userDetailsService;

    public SessionAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                filterChain.doFilter(request, response);
                return;
            }

            authenticateFromSession(request);
        } catch (Exception ex) {
            logger.error("Error during authentication: ", ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            logger.warn("No session found when attempting to authenticate from session");
            return;
        }

        SecurityContext securityContext = (SecurityContext) session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        );

        if (securityContext != null && securityContext.getAuthentication() != null) {
            Authentication auth = securityContext.getAuthentication();

            if (auth.isAuthenticated()) {
                if (isValidAuthentication(auth)) {
                    SecurityContextHolder.setContext(securityContext);
                    logger.debug("Usuario autenticado via sessão: {}", auth.getName());
                } else {
                    session.invalidate();
                    SecurityContextHolder.clearContext();
                    logger.debug("Session invalidated - user is not valid anymore");
                }
            }
        }
    }

    private boolean isValidAuthentication(Authentication auth) {
        try {
            // Recarregar detalhes do usuário para verificar se ainda está ativo
            UserDetails userDetails = userDetailsService.loadUserByUsername(auth.getName());
            return userDetails != null
                    && userDetails.isEnabled()
                    && userDetails.isAccountNonExpired()
                    && userDetails.isAccountNonLocked()
                    && userDetails.isCredentialsNonExpired();
        } catch (UsernameNotFoundException e) {
            logger.debug("Usuário não encontrado durante revalidação: {}", auth.getName());
            return false;
        } catch (Exception e) {
            logger.error("Erro ao revalidar usuário: ", e);
            return false;
        }
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        String[] publicEndpoints = {
                "/api/v1/auth/**",
                "/api/v1/users/register",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/health"
        };

        for (String endpoint : publicEndpoints) {
            if (requestURI.startsWith(endpoint)) {
                return true;
            }
        }

        return "OPTIONS".equalsIgnoreCase(method);
    }
}
