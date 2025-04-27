package com.sen.senbackend.ai.ml;

import com.sen.senbackend.ai.AiStrategy;
import com.sen.senbackend.ai.ml.service.PlayerMoveTrainingService;
import com.sen.senbackend.gamelogic.exception.GameLogicException;
import com.sen.senbackend.gamelogic.model.GameSession;
import com.sen.senbackend.gamelogic.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("mlStrategy")
@RequiredArgsConstructor
public class MachineLearningSwapStrategy implements AiStrategy {

    private final GameSessionRepository gameSessionRepository;
    private final PlayerMoveTrainingService playerMoveTrainingService;

    @Override
    public String makeMove(GameSession session) {
        if (session.isGameOver()) return null;

        List<Integer> aiCards = session.getAiCards();
        List<Integer> discardPile = session.getDiscardPile();
        List<Integer> deck = session.getDeck();

        boolean canDrawFromDiscard = !discardPile.isEmpty();
        boolean canDrawFromDeck = !deck.isEmpty();

        if (!canDrawFromDeck && !canDrawFromDiscard) {
            String msg = "ML AI opponent could not make a move — no cards to draw.";
            session.setLastActionMessage(msg);
            gameSessionRepository.save(session);
            return msg;
        }

        Integer discardTop = canDrawFromDiscard ? discardPile.get(discardPile.size() - 1) : null;

        int weakestCard = findWeakestCard(aiCards);
        double avgCardValue = calculateAverage(aiCards);
        int difference = discardTop != null ? (weakestCard - discardTop) : 0;

        boolean shouldTakeFromDeck = playerMoveTrainingService.predictMove(
                session.getRoundNumber(),
                weakestCard,
                discardTop,
                avgCardValue,
                difference
        );

        Integer drawnCard;
        String source;

        if (shouldTakeFromDeck && canDrawFromDeck) {
            // ML - dobieramy z zakrytej
            drawnCard = deck.remove(0);
            source = "deck";

            if (drawnCard < weakestCard) {
                int indexToReplace = findIndexOfWeakestCard(aiCards);
                Integer oldCard = aiCards.get(indexToReplace);
                aiCards.set(indexToReplace, drawnCard);
                discardPile.add(oldCard);

                String message = String.format(
                        "ML AI opponent swapped a card from the %s with the card at index %d.",
                        source,
                        indexToReplace
                );

                session.setLastActionMessage(message);
                gameSessionRepository.save(session);
                return message;
            } else {
                discardPile.add(drawnCard);

                String message = String.format(
                        "ML AI opponent drew a card from the %s but did not swap, placed it on the discard pile.",
                        source
                );

                session.setLastActionMessage(message);
                gameSessionRepository.save(session);
                return message;
            }
        } else if (canDrawFromDiscard) {
            // ML - dobieramy z odkrytej
            drawnCard = discardPile.remove(discardPile.size() - 1);
            source = "discard pile";

            int indexToReplace = findIndexOfWeakestCard(aiCards);
            Integer oldCard = aiCards.get(indexToReplace);
            aiCards.set(indexToReplace, drawnCard);
            discardPile.add(oldCard);

            String message = String.format(
                    "ML AI opponent swapped a card from the %s with the card at index %d.",
                    source,
                    indexToReplace
            );

            session.setLastActionMessage(message);
            gameSessionRepository.save(session);
            return message;
        } else {
            // brak discard, dobieramy z zakrytej
            drawnCard = deck.remove(0);
            source = "deck";

            if (drawnCard < weakestCard) {
                int indexToReplace = findIndexOfWeakestCard(aiCards);
                Integer oldCard = aiCards.get(indexToReplace);
                aiCards.set(indexToReplace, drawnCard);
                discardPile.add(oldCard);

                String message = String.format(
                        "ML AI opponent swapped a card from the %s with the card at index %d.",
                        source,
                        indexToReplace
                );

                session.setLastActionMessage(message);
                gameSessionRepository.save(session);
                return message;
            } else {
                discardPile.add(drawnCard);

                String message = String.format(
                        "ML AI opponent drew a card from the %s but did not swap, placed it on the discard pile.",
                        source
                );

                session.setLastActionMessage(message);
                gameSessionRepository.save(session);
                return message;
            }
        }
    }

    private int findWeakestCard(List<Integer> cards) {
        return cards.stream()
                .max(Integer::compareTo)
                .orElseThrow(() -> new GameLogicException("AI hand is empty — cannot find the weakest card."));
    }

    private int findIndexOfWeakestCard(List<Integer> cards) {
        int maxIndex = 0;
        int maxValue = cards.get(0);
        for (int i = 1; i < cards.size(); i++) {
            if (cards.get(i) > maxValue) {
                maxValue = cards.get(i);
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private double calculateAverage(List<Integer> cards) {
        return cards.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}
