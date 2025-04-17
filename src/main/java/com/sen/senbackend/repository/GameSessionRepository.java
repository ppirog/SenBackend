package com.sen.senbackend.repository;

import com.sen.senbackend.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByPlayerId(Long playerId);
}