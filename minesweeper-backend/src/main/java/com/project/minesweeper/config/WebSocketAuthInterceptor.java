package com.project.minesweeper.config;

import com.project.minesweeper.model.User;
import com.project.minesweeper.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateConnection(accessor);
        }

        return message;
    }

    private void authenticateConnection(StompHeaderAccessor accessor) {
        String authToken = accessor.getFirstNativeHeader("Authorization");

        if (!StringUtils.hasText(authToken) || !authToken.startsWith("Bearer ")) {
            log.warn("WebSocket connection attempt without valid Authorization header");
            throw new IllegalStateException("Authentication failed: Missing or invalid token");
        }

        String token = authToken.substring(7);
        String username = tokenProvider.getUsernameFromToken(token);

        if (username == null) {
            throw new IllegalStateException("Authentication failed: Invalid token payload");
        }

        User user = (User) userDetailsService.loadUserByUsername(username);

        if (!tokenProvider.validateToken(token, user)) {
            throw new IllegalStateException("Authentication failed: Token expired");
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());

        accessor.setUser(auth);
        log.debug("WebSocket user '{}' authenticated successfully", username);
    }
}