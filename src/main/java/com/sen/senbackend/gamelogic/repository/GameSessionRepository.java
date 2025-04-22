package com.sen.senbackend.gamelogic.repository;

import com.sen.senbackend.gamelogic.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByPlayerId(Long playerId);
}