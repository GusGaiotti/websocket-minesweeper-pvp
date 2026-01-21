package com.project.minesweeper.dto;

import com.project.minesweeper.model.User;

public record AuthResponse(
        String token,
        String type,
        Long id,
        String username,
        String email
) {

    public static AuthResponse from(User user, String token) {
        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}