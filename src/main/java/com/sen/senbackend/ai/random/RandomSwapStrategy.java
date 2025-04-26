package com.sen.senbackend.ai.random;

import com.sen.senbackend.ai.AiStrategy;
import com.sen.senbackend.gamelogic.exception.GameLogicException;
import com.sen.senbackend.gamelogic.model.GameSession;
import com.sen.senbackend.gamelogic.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component("randomStrategy")
@RequiredArgsConstructor
public class RandomSwapStrategy implements AiStrategy {

    private final GameSessionRepository gameSessionRepository;
    private final Random random = new Random();

    @Override
    public String makeMove(GameSession session) {
        if (session.isGameOver()) return null;

        List<Integer> aiCards = session.getAiCards();
        List<Integer> discardPile = session.getDiscardPile();
        List<Integer> deck = session.getDeck();

        boolean canDrawFromDiscard = !discardPile.isEmpty();
        boolean canDrawFromDeck = !deck.isEmpty();

        if (!canDrawFromDeck && !canDrawFromDiscard) {
            String msg = "Losowy automatyczny przeciwnik nie wykonał ruchu — brak kart do dobrania.";
            session.setLastActionMessage(msg);
            gameSessionRepository.save(session);
            return msg;
        }

        boolean drawFromDiscard = canDrawFromDiscard && (!canDrawFromDeck || random.nextBoolean());

        Integer drawnCard;
        String source;

        if (drawFromDiscard) {
            drawnCard = discardPile.remove(discardPile.size() - 1);
            source = "odkrytej";
        } else {
            drawnCard = deck.remove(0);
            source = "zakrytej";
        }

        int indexToReplace = findIndexOfWeakestCard(aiCards);

        Integer oldCard = aiCards.get(indexToReplace);
        aiCards.set(indexToReplace, drawnCard);
        discardPile.add(oldCard);

        String message = String.format(
                "Losowy automatyczny przeciwnik zamienił kartę z talii %s z kartą na indeksie %d",
                source,
                indexToReplace
        );

        session.setLastActionMessage(message);
        gameSessionRepository.save(session);

        return message;
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