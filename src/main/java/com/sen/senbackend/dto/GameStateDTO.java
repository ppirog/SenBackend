package com.sen.senbackend.dto;

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
    private List<Integer> playerCards;
    private List<Integer> aiCards;

    private int deckSize;
    private Integer deckTop;
    private List<Integer> deck;

    private Integer discardTop;
    private List<Integer> discardPile;

    private boolean gameOver;
    private int roundNumber;
}
