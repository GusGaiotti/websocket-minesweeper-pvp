package com.project.minesweeper.dto;

import com.project.minesweeper.model.GameStatus;

import java.util.List;

public record BoardResponse(
        String gameId,
        int width,
        int height,
        GameStatus status,
        List<CellResponse> cells,
        String currentTurnUserId,
        String yourUserId,
        String winnerUserId
) {}