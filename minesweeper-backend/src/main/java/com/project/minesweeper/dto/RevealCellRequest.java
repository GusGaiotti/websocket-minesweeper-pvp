package com.project.minesweeper.dto;

import jakarta.validation.constraints.Min;

public record RevealCellRequest(
        String gameId,
        @Min(0) int x,
        @Min(0) int y
) {
}