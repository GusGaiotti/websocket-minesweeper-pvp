package com.project.minesweeper.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record JoinGameRequest(
        String gameId,
        @Min(10) @Max(50) int width,
        @Min(10) @Max(50) int height
) {
}