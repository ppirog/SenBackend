package com.sen.senbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long playerId;

    private boolean vsComputer = true;

    @ElementCollection
    private List<Integer> playerCards;

    @ElementCollection
    private List<Integer> aiCards;

    @ElementCollection
    private List<Integer> deck;

    @ElementCollection
    private List<Integer> discardPile;

    private boolean gameOver;

    private int roundNumber = 1;
}

