package com.gatto.rms.controller;

import com.gatto.rms.entity.AppUser;
import com.gatto.rms.entity.Role;
import com.gatto.rms.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class CurrentUserController {

    private final CurrentUserService currentUserService;

    @GetMapping
    public CurrentUserResponse currentUser(Authentication authentication) {
        if (!currentUserService.isAuthenticated() || authentication == null) {
            return anonymousUser();
        }

        String name = currentUserService.getCurrentUsername();
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toUnmodifiableSet());

        return currentUserService.getCurrentUser(name)
                .map(user -> authenticatedUser(user, authorities))
                .orElseGet(() -> new CurrentUserResponse(name, name, true, Set.of(), authorities));
    }

    private CurrentUserResponse authenticatedUser(AppUser user, Set<String> authorities) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toUnmodifiableSet());

        return new CurrentUserResponse(
                user.getEmail(),
                user.getDisplayName(),
                user.isEnabled(),
                roles,
                authorities
        );
    }

    private CurrentUserResponse anonymousUser() {
        return new CurrentUserResponse(
                null,
                "Anonymous",
                false,
                Set.of(),
                Set.of()
        );
    }
}
