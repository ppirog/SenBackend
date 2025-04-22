package com.sen.senbackend.gamelogic.repository;

import com.sen.senbackend.gamelogic.model.GameRoundResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRoundResultRepository extends JpaRepository<GameRoundResult, Long> {
    List<GameRoundResult> findByGameSessionIdOrderByRoundNumber(Long sessionId);
}

