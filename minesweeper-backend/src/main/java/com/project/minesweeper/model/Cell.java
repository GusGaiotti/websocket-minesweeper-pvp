package com.project.minesweeper.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cell {
    private final int x;
    private final int y;
    private boolean mine;
    private boolean revealed;
    private int adjacentMines;
    private boolean exploded;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
}