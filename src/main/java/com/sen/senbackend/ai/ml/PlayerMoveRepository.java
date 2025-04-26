package com.sen.senbackend.ai.ml;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerMoveRepository extends JpaRepository<PlayerMove, Long> {
}
