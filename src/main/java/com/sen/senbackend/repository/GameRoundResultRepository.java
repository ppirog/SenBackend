package com.sen.senbackend.repository;

import com.sen.senbackend.model.GameRoundResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRoundResultRepository extends JpaRepository<GameRoundResult, Long> {
    List<GameRoundResult> findByGameSessionIdOrderByRoundNumber(Long sessionId);
}

