package com.project.minesweeper.service;

import com.project.minesweeper.dto.*;
import com.project.minesweeper.exception.GameNotFoundException;
import com.project.minesweeper.model.Board;
import com.project.minesweeper.model.Cell;
import com.project.minesweeper.model.GameStatus;
import com.project.minesweeper.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public BoardResponse joinGame(JoinGameRequest request, String userId) {
        Board board;

        if (request.gameId() == null || request.gameId().isBlank()) {
            board = new Board(request.width(), request.height());
            board.addPlayer(userId);
            gameRepository.save(board);
            log.debug("Game {} created by {}", board.getId(), userId);
        } else {
            board = gameRepository.findById(request.gameId())
                    .orElseThrow(() -> new GameNotFoundException(request.gameId()));

            board.addPlayer(userId);
            gameRepository.save(board);
            log.debug("Player {} joined game {}", userId, board.getId());
        }

        return toBoardResponse(board, userId);
    }

    public BoardResponse revealCell(RevealCellRequest request, String userId) {
        Board board = gameRepository.findById(request.gameId())
                .orElseThrow(() -> new GameNotFoundException(request.gameId()));

        board.processMove(request.x(), request.y(), userId);
        gameRepository.save(board);

        return toBoardResponse(board, userId);
    }

    private BoardResponse toBoardResponse(Board board, String recipientId) {
        boolean isGameOver = board.getStatus() == GameStatus.WON ||
                board.getStatus() == GameStatus.LOST;

        List<CellResponse> cells = new ArrayList<>();
        for (Cell[] row : board.getCells()) {
            for (Cell c : row) {
                boolean showMine = (c.isRevealed() || isGameOver) && c.isMine();
                int showAdjacent = c.isRevealed() ? c.getAdjacentMines() : 0;

                cells.add(new CellResponse(
                        c.getX(),
                        c.getY(),
                        c.isRevealed(),
                        showMine,
                        showAdjacent,
                        c.isExploded()
                ));
            }
        }

        return new BoardResponse(
                board.getId(),
                board.getWidth(),
                board.getHeight(),
                board.getStatus(),
                cells,
                board.getCurrentTurnId(),
                recipientId,
                board.getWinnerId()
        );
    }
}