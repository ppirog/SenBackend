package com.sen.senbackend.mapper;

import com.sen.senbackend.dto.responses.GameStateDTO;
import com.sen.senbackend.model.GameSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameMapper {

    public GameStateDTO toDto(GameSession session) {
        return GameStateDTO.builder()
                .playerCards(session.getPlayerCards())
                .aiCards(session.getAiCards())
                .deckSize(session.getDeck().size())
                .deck(session.getDeck()) // 🐞 debug
                .deckTop(getDeckTop(session.getDeck())) // 🆕
                .discardTop(getTopDiscard(session.getDiscardPile()))
                .discardPile(session.getDiscardPile()) // 🐞 debug
                .gameOver(session.isGameOver())
                .roundNumber(session.getRoundNumber())
                .build();
    }

    private Integer getTopDiscard(List<Integer> discardPile) {
        if (discardPile == null || discardPile.isEmpty()) {
            return null;
        }
        return discardPile.get(discardPile.size() - 1);
    }

    private Integer getDeckTop(List<Integer> deck) {
        if (deck == null || deck.isEmpty()) {
            return null;
        }
        return deck.get(0);
    }

}

