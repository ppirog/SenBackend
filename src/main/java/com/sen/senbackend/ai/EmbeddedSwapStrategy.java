package com.sen.senbackend.ai;

import com.sen.senbackend.gamelogic.exception.GameLogicException;
import com.sen.senbackend.gamelogic.model.GameSession;
import com.sen.senbackend.gamelogic.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("embeddedStrategy")
@RequiredArgsConstructor
public class EmbeddedSwapStrategy implements AiStrategy {

    private final GameSessionRepository gameSessionRepository;

    @Override
    public String makeMove(GameSession session) {
        if (session.isGameOver()) return null;

        List<Integer> aiCards = session.getAiCards();
        List<Integer> discardPile = session.getDiscardPile();
        List<Integer> deck = session.getDeck();

        boolean canDrawFromDiscard = !discardPile.isEmpty();
        boolean canDrawFromDeck = !deck.isEmpty();

        if (!canDrawFromDeck && !canDrawFromDiscard) {
            String msg = "Wbudowany automatyczny przeciwnik nie wykonał ruchu — brak kart do dobrania.";
            session.setLastActionMessage(msg);
            gameSessionRepository.save(session);
            return msg;
        }

        boolean drawFromDiscard = false;
        Integer discardTop = canDrawFromDiscard ? discardPile.get(discardPile.size() - 1) : null;
        Integer drawnCard;
        String source;

        if (canDrawFromDiscard && discardTop != null) {
            int weakestCard = findWeakestCard(aiCards);
            if (discardTop <= weakestCard - 3) {
                drawFromDiscard = true;
            }
        }

        if (drawFromDiscard) {
            drawnCard = discardPile.remove(discardPile.size() - 1);
            source = "odkrytej";

            int indexToReplace = findIndexOfWeakestCard(aiCards);
            Integer oldCard = aiCards.get(indexToReplace);
            aiCards.set(indexToReplace, drawnCard);
            discardPile.add(oldCard);

            String message = String.format(
                    "Wbudowany automatyczny przeciwnik zamienił kartę z talii %s z kartą na indeksie %d",
                    source,
                    indexToReplace
            );

            session.setLastActionMessage(message);
            gameSessionRepository.save(session);
            return message;

        } else {
            drawnCard = deck.remove(0);
            source = "zakrytej";

            int weakestCard = findWeakestCard(aiCards);

            if (drawnCard < weakestCard) {
                // Nowa karta jest lepsza wiec wymieniam
                int indexToReplace = findIndexOfWeakestCard(aiCards);
                Integer oldCard = aiCards.get(indexToReplace);
                aiCards.set(indexToReplace, drawnCard);
                discardPile.add(oldCard);

                String message = String.format(
                        "Wbudowany automatyczny przeciwnik zamienił kartę z talii %s z kartą na indeksie %d",
                        source,
                        indexToReplace
                );

                session.setLastActionMessage(message);
                gameSessionRepository.save(session);
                return message;

            } else {
                // Nowa karta jest gorsza lub równa wiec odkładam na discard bez zmiany
                discardPile.add(drawnCard);

                String message = String.format(
                        "Wbudowany automatyczny przeciwnik dobrał kartę z talii %s, ale nie wymienił, odłożył na stos odrzuceń",
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
                .orElseThrow(() -> new GameLogicException("AI hand is empty – cannot find the weakest card."));
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
}
