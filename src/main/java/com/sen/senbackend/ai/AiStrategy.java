package com.sen.senbackend.ai;

import com.sen.senbackend.gamelogic.model.GameSession;

public interface AiStrategy {
    String makeMove(GameSession session);
}
