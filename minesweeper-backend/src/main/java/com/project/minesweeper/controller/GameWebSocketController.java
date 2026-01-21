package com.project.minesweeper.controller;

import com.project.minesweeper.dto.BoardResponse;
import com.project.minesweeper.dto.JoinGameRequest;
import com.project.minesweeper.dto.RevealCellRequest;
import com.project.minesweeper.model.User;
import com.project.minesweeper.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game.join")
    public void join(@Valid @Payload JoinGameRequest request, SimpMessageHeaderAccessor headerAccessor) {

        try {
            User user = getUser(headerAccessor);
            String userId = user.getId().toString();
            String username = user.getUsername();


            BoardResponse board = gameService.joinGame(request, userId);

            messagingTemplate.convertAndSendToUser(username, "/queue/game-init", board);

            messagingTemplate.convertAndSend("/topic/game." + board.gameId(), board);

        } catch (Exception e) {
            log.error("=== ERROR IN JOIN ===", e);
            throw e;
        }
    }

    @MessageMapping("/game.reveal")
    public void reveal(@Payload RevealCellRequest request, SimpMessageHeaderAccessor headerAccessor) {


        try {
            User user = getUser(headerAccessor);
            String userId = user.getId().toString();


            BoardResponse board = gameService.revealCell(request, userId);

            messagingTemplate.convertAndSend("/topic/game." + board.gameId(), board);
        } catch (Exception e) {
            log.error("ERROR IN REVEAL:", e);
            throw e;
        }
    }

    private User getUser(SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth == null) {
            log.error("Authentication is null!");
            throw new IllegalStateException("User not authenticated - auth is null");
        }

        if (!(auth.getPrincipal() instanceof User)) {
            log.error("Principal is not User instance: {}", auth.getPrincipal().getClass());
            throw new IllegalStateException("User not authenticated - wrong principal type");
        }

        return (User) auth.getPrincipal();
    }
}