package com.gatto.rms.controller;

import java.util.Set;

public record CurrentUserResponse(
        String email,
        String displayName,
        boolean authenticated,
        Set<String> roles,
        Set<String> authorities
) {
}
