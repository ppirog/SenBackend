package com.sen.senbackend.ai.ml.repository;

import com.sen.senbackend.ai.ml.model.PlayerMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerMoveRepository extends JpaRepository<PlayerMove, Long> {
}
