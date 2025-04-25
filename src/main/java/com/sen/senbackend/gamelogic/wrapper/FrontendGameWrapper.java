package com.sen.senbackend.gamelogic.wrapper;

import com.sen.senbackend.gamelogic.dto.responses.FrontendGameStateDTO;
import com.sen.senbackend.gamelogic.dto.responses.GameStateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class FrontendGameWrapper {

    public FrontendGameStateDTO build(GameStateDTO gameStateDTO) {
        return FrontendGameStateDTO.builder()
                .id(Long.parseLong(gameStateDTO.getId()))
                .gameOver(gameStateDTO.isGameOver())
                .roundNumber(gameStateDTO.getRoundNumber())
                .gameRound(gameStateDTO.getGameRound())
                .lastActionMessage(gameStateDTO.getLastActionMessage())
                .discardTop(gameStateDTO.getDiscardTop())
                .roundHistory(gameStateDTO.getRoundHistory())
                .totalPlayerPoints(gameStateDTO.getTotalPlayerPoints())
                .totalAiPoints(gameStateDTO.getTotalAiPoints())
                .build();
    }
}
