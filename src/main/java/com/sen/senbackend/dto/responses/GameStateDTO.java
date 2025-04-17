package com.sen.senbackend.dto.responses;

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
    private boolean gameOver;
    private int roundNumber;

    private List<Integer> playerCards;
    private List<Integer> aiCards;

    private Integer discardTop;
    private List<Integer> discardPile;

    private Integer deckTop;
    private List<Integer> deck;
    private int deckSize;
}
