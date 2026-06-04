package com.gatto.rms.service;

import com.gatto.rms.entity.AppUser;

import java.util.Optional;

public interface CurrentUserService {
    String getCurrentUsername();
    boolean isAuthenticated();
    boolean isUserInRole(String username, String role);
    Optional<AppUser> getCurrentUser(String username);

    default boolean isCurrentUserInRole(String role) {
        return isUserInRole(getCurrentUsername(), role);
    }
}
