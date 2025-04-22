package com.sen.senbackend.gamelogic.wrapper;

import com.sen.senbackend.gamelogic.dto.responses.GameStateDTO;
import com.sen.senbackend.gamelogic.dto.responses.RoundHistoryDto;
import com.sen.senbackend.gamelogic.model.GameRoundResult;
import com.sen.senbackend.gamelogic.model.GameSession;
import com.sen.senbackend.gamelogic.repository.GameRoundResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GameWrapper {

    private final GameRoundResultRepository roundResultRepository;

    public GameStateDTO build(GameSession session) {
        List<GameRoundResult> roundResults = roundResultRepository
                .findByGameSessionIdOrderByRoundNumber(session.getId());

        List<RoundHistoryDto> roundHistory = roundResults.stream()
                .map(result -> RoundHistoryDto.builder()
                        .roundNumber(result.getRoundNumber())
                        .playerPoints(result.getPlayerPoints())
                        .aiPoints(result.getAiPoints())
                        .build())
                .toList();

        int totalPlayerPoints = roundResults.stream()
                .mapToInt(GameRoundResult::getPlayerPoints)
                .sum();

        int totalAiPoints = roundResults.stream()
                .mapToInt(GameRoundResult::getAiPoints)
                .sum();

        return GameStateDTO.builder()
                .playerCards(session.getPlayerCards())
                .aiCards(session.getAiCards())
                .deckSize(session.getDeck().size())
                .deck(session.getDeck())
                .deckTop(getDeckTop(session.getDeck()))
                .discardTop(getTopDiscard(session.getDiscardPile()))
                .discardPile(session.getDiscardPile())
                .gameOver(session.isGameOver())
                .roundNumber(session.getRoundNumber())
                .gameRound(session.getGameRound())
                .roundHistory(roundHistory)
                .totalPlayerPoints(totalPlayerPoints)
                .totalAiPoints(totalAiPoints)
                .lastActionMessage(session.getLastActionMessage())
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

