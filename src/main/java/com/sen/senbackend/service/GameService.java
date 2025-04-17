package com.sen.senbackend.service;

import com.sen.senbackend.login.loginandregister.UserRepository;
import com.sen.senbackend.model.GameSession;
import com.sen.senbackend.repository.GameSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class GameService {
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;

    public GameSession createNewSession(Long userId) {
        List<Integer> deck = createShuffledDeck();
        List<Integer> playerCards = drawCards(deck, 4);
        List<Integer> aiCards = drawCards(deck, 4);

        List<Integer> discardPile = new ArrayList<>();
        discardPile.add(deck.remove(0));
        discardPile.add(deck.remove(0));
        discardPile.add(deck.remove(0));

        GameSession session = new GameSession();
        session.setPlayerId(userId);
        session.setDeck(deck);
        session.setPlayerCards(playerCards);
        session.setAiCards(aiCards);
        session.setDiscardPile(discardPile);
        session.setGameOver(false);

        return gameSessionRepository.save(session);
    }

    public GameSession getGameSessionByIdAndPlayer(Long sessionId, String login) {
        Long userId = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Game session not found"));

        if (!session.getPlayerId().equals(userId)) {
            throw new RuntimeException("Access denied to this game session");
        }

        return session;
    }

    private List<Integer> createShuffledDeck() {
        List<Integer> deck = new ArrayList<>();

        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j < 4; j++) {
                deck.add(i);
            }
        }

        for (int i = 0; i < 9; i++) {
            deck.add(8);
        }

        for (int i = 0; i < 9; i++) {
            deck.add(9);
        }

        Collections.shuffle(deck);
        return deck;
    }


    private List<Integer> drawCards(List<Integer> deck, int count) {
        List<Integer> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(deck.remove(0));
        }
        return cards;
    }
}

