package com.sen.senbackend.gamelogic.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrontendGameStateDTO {
    private Long id;
    private boolean gameOver;
    private int roundNumber;
    private int gameRound;
    private String lastActionMessage;
    private int discardTop;
    private List<RoundHistoryDto> roundHistory;
    private int totalPlayerPoints;
    private int totalAiPoints;
}
