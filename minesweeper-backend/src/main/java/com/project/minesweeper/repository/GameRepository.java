package com.project.minesweeper.repository;

import com.project.minesweeper.model.Board;
import com.project.minesweeper.model.GameStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class GameRepository {
    private final Map<String, Board> games = new ConcurrentHashMap<>();

    public void save(Board board) {
        games.put(board.getId(), board);
    }

    public Optional<Board> findById(String id) {
        return Optional.ofNullable(games.get(id));
    }
}