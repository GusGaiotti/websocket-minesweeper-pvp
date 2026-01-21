package com.project.minesweeper.dto;

public record CellResponse(
        int x,
        int y,
        boolean revealed,
        boolean mine,
        int adjacentMines,
        boolean exploded
) {
}