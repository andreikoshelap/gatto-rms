package com.gatto.rms.service;

import com.gatto.rms.entity.AppUser;
import com.gatto.rms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {
    private final UserRepository repository;

    @Override
    public String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (!isAuthenticated(authentication)) {
            return null;
        }
        return authentication.getName();
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated(getAuthentication());
    }

    @Override
    public boolean isUserInRole(String username, String role) {
        if (username == null || role == null || role.isBlank()) {
            return false;
        }

        String normalizedRole = normalizeRole(role);
        Authentication authentication = getAuthentication();
        if (isAuthenticated(authentication) && username.equals(authentication.getName())) {
            boolean hasAuthority = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals(normalizedRole) || authority.equals(role));
            if (hasAuthority) {
                return true;
            }
        }

        return repository.findByEmail(username)
                .map(user -> user.getRoles().stream()
                        .anyMatch(userRole -> userRole.getName().name().equals(role)
                                || normalizeRole(userRole.getName().name()).equals(normalizedRole)))
                .orElse(false);
    }

    @Override
    public Optional<AppUser> getCurrentUser(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return repository.findByEmail(username);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private String normalizeRole(String role) {
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
