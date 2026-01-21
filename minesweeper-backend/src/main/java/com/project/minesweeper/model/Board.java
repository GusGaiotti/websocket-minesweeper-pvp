package com.project.minesweeper.model;

import com.project.minesweeper.exception.InvalidMoveException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class Board {
    private static final double MINE_DENSITY = 0.15;
    private static final int[] DX = {-1, 0, 1, -1, 1, -1, 0, 1};
    private static final int[] DY = {-1, -1, -1, 0, 0, 1, 1, 1};

    private final String id;
    private final int width;
    private final int height;
    private final int totalMines;
    private final Cell[][] cells;

    private GameStatus status;
    private int unrevealedSafeCells;

    private String player1Id;
    private String player2Id;
    private String currentTurnId;
    private String winnerId;
    private LocalDateTime lastActivity;

    public Board(int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.width = width;
        this.height = height;
        this.totalMines = Math.max(1, (int) (width * height * MINE_DENSITY));
        this.cells = new Cell[height][width];
        this.status = GameStatus.WAITING_FOR_PLAYERS;
        this.unrevealedSafeCells = (width * height) - totalMines;
        this.lastActivity = LocalDateTime.now();

        initializeBoard();
    }

    private void initializeBoard() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(x, y);
            }
        }

        Random random = new Random();
        int placed = 0;
        while (placed < totalMines) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if (!cells[y][x].isMine()) {
                cells[y][x].setMine(true);
                placed++;
            }
        }

        calculateAdjacencies();
    }

    public synchronized void addPlayer(String userId) {
        if (player1Id != null && player2Id != null) {
            throw new InvalidMoveException("Game is full");
        }

        if (userId.equals(player1Id) || userId.equals(player2Id)) {
            throw new InvalidMoveException("Player already joined");
        }

        if (player1Id == null) {
            player1Id = userId;
            currentTurnId = userId;
        } else {
            player2Id = userId;
            status = GameStatus.IN_PROGRESS;
        }

        this.lastActivity = LocalDateTime.now();
    }

    public synchronized void processMove(int x, int y, String playerId) {
        this.lastActivity = LocalDateTime.now();

        if (status != GameStatus.IN_PROGRESS) {
            throw new InvalidMoveException("Game not active");
        }
        if (!playerId.equals(currentTurnId)) {
            throw new InvalidMoveException("Not your turn");
        }
        if (!isValidCoordinate(x, y)) {
            throw new InvalidMoveException("Invalid coordinates");
        }

        Cell cell = cells[y][x];
        if (cell.isRevealed()) {
            throw new InvalidMoveException("Cell already revealed");
        }

        if (cell.isMine()) {
            cell.setRevealed(true);
            cell.setExploded(true);
            status = GameStatus.LOST;
            winnerId = playerId.equals(player1Id) ? player2Id : player1Id;
            revealAllMines();
        } else {
            revealSafeCells(x, y);
            if (status == GameStatus.IN_PROGRESS) {
                currentTurnId = currentTurnId.equals(player1Id) ? player2Id : player1Id;
            }
        }
    }

    private void revealSafeCells(int startX, int startY) {
        Queue<Cell> queue = new ArrayDeque<>();
        queue.add(cells[startY][startX]);

        while (!queue.isEmpty()) {
            Cell c = queue.poll();
            if (c.isRevealed() || c.isMine()) continue;

            c.setRevealed(true);
            unrevealedSafeCells--;

            if (c.getAdjacentMines() == 0) {
                for (int i = 0; i < 8; i++) {
                    int nx = c.getX() + DX[i];
                    int ny = c.getY() + DY[i];
                    if (isValidCoordinate(nx, ny)) {
                        queue.add(cells[ny][nx]);
                    }
                }
            }
        }

        if (unrevealedSafeCells == 0) {
            status = GameStatus.WON;
            winnerId = currentTurnId;
            revealAllMines();
        }
    }

    private void revealAllMines() {
        for (Cell[] row : cells) {
            for (Cell c : row) {
                if (c.isMine()) {
                    c.setRevealed(true);
                }
            }
        }
    }

    private void calculateAdjacencies() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (cells[y][x].isMine()) continue;
                int count = 0;
                for (int i = 0; i < 8; i++) {
                    int nx = x + DX[i];
                    int ny = y + DY[i];
                    if (isValidCoordinate(nx, ny) && cells[ny][nx].isMine()) {
                        count++;
                    }
                }
                cells[y][x].setAdjacentMines(count);
            }
        }
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}