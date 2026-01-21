package com.project.minesweeper.exception;

import com.project.minesweeper.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GlobalWebSocketExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler({GameNotFoundException.class, InvalidMoveException.class,
            IllegalArgumentException.class, RuntimeException.class})
    public void handleGameExceptions(Exception ex, SimpMessageHeaderAccessor headerAccessor) {
        String username = getUsername(headerAccessor);

        log.warn("WebSocket error for user {}: {}", username, ex.getMessage());

        if (!"unknown".equals(username)) {
            messagingTemplate.convertAndSendToUser(username, "/queue/errors", ex.getMessage());
        } else {
            log.error("Cannot send error to user - authentication failed: {}", ex.getMessage());
        }
    }

    private String getUsername(SimpMessageHeaderAccessor headerAccessor) {
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            if (auth != null && auth.getPrincipal() instanceof User user) {
                return user.getUsername();
            }
        } catch (Exception e) {
            log.error("Failed to extract username: {}", e.getMessage());
        }
        return "unknown";
    }
}