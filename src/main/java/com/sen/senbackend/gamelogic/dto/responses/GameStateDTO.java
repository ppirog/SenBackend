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
public class GameStateDTO {
    private String id;
    private boolean gameOver;
    private int roundNumber;
    private int gameRound;

    private List<Integer> playerCards;
    private List<Integer> aiCards;
    private String lastActionMessage;

    private Integer discardTop;
    private List<Integer> discardPile;

    private Integer deckTop;
    private List<Integer> deck;
    private int deckSize;

    private List<RoundHistoryDto> roundHistory;

    private int totalPlayerPoints;
    private int totalAiPoints;
}
