package com.sen.senbackend.gamelogic.service;

import com.sen.senbackend.ai.ml.model.PlayerMove;
import com.sen.senbackend.ai.ml.repository.PlayerMoveRepository;
import com.sen.senbackend.ai.AiStrategyManager;
import com.sen.senbackend.gamelogic.dto.responses.RoundHistoryDto;
import com.sen.senbackend.gamelogic.dto.responses.WakeUpResponseDto;
import com.sen.senbackend.gamelogic.exception.GameLogicException;
import com.sen.senbackend.gamelogic.model.GameRoundResult;
import com.sen.senbackend.gamelogic.model.GameSession;
import com.sen.senbackend.gamelogic.repository.GameRoundResultRepository;
import com.sen.senbackend.gamelogic.repository.GameSessionRepository;
import com.sen.senbackend.login.loginandregister.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
public class GameService {
    private final GameSessionRepository gameSessionRepository;
    private final GameRoundResultRepository roundResultRepository;
    private final UserRepository userRepository;
    private final AiStrategyManager aiStrategyManager;
    private final PlayerMoveRepository playerMoveRepository;

    public GameSession createNewSession(Long userId, String strategyName) {
        List<Integer> deck = createShuffledDeck();
        List<Integer> playerCards = drawCards(deck, 4);
        List<Integer> aiCards = drawCards(deck, 4);

        List<Integer> discardPile = new ArrayList<>();
        discardPile.add(deck.remove(0));

        GameSession session = new GameSession();
        session.setPlayerId(userId);
        session.setDeck(deck);
        session.setPlayerCards(playerCards);
        session.setAiCards(aiCards);
        session.setDiscardPile(discardPile);
        session.setGameOver(false);
        session.setAiStrategyName(strategyName);

        String message = "First card value: " + playerCards.get(0) + " and second card value: " + playerCards.get(1);
        session.setLastActionMessage(message);

        return gameSessionRepository.save(session);
    }

    public GameSession getGameSessionByIdAndPlayer(Long sessionId, String login) {
        Long userId = getUserIdByLogin(login);
        return getSessionByIdAndPlayer(sessionId, userId);
    }

    public GameSession swapCardWithDiscard(Long sessionId, String login, int cardIndex) {
        Long userId = getUserIdByLogin(login);
        GameSession session = getSessionByIdAndPlayer(sessionId, userId);

        if (session.isGameOver()) {
            throw new GameLogicException("The game is over. You can't swap cards.");
        }

        if (session.getLastActionRound() == session.getRoundNumber()) {
            throw new GameLogicException("You have already ended your turn this round.");
        }

        if (cardIndex < 0 || cardIndex >= session.getPlayerCards().size()) {
            throw new GameLogicException("Invalid card index.");
        }

        List<Integer> playerCards = session.getPlayerCards();
        List<Integer> discardPile = session.getDiscardPile();

        if (discardPile.isEmpty()) {
            throw new GameLogicException("Discard pile is empty, cannot swap.");
        }

        Integer cardFromHand = playerCards.get(cardIndex);
        Integer cardFromDiscard = discardPile.remove(discardPile.size() - 1);

        playerCards.set(cardIndex, cardFromDiscard);
        discardPile.add(cardFromHand);

        session.setLastActionRound(session.getRoundNumber());
        session.setRoundNumber(session.getRoundNumber() + 1);

        aiStrategyManager.getStrategy(session.getAiStrategyName()).makeMove(session);

        checkAndWakeUpIfDeckEmpty(session);

        return gameSessionRepository.save(session);
    }

    public GameSession skipSwap(Long sessionId, String login) {
        Long userId = getUserIdByLogin(login);
        GameSession session = getSessionByIdAndPlayer(sessionId, userId);

        if (session.isGameOver()) {
            throw new GameLogicException("The game is over. You can't skip anymore.");
        }

        if (session.getLastActionRound() == session.getRoundNumber()) {
            throw new GameLogicException("You have already ended your turn this round.");
        }

        session.setLastActionRound(session.getRoundNumber());
        session.setRoundNumber(session.getRoundNumber() + 1);

        aiStrategyManager.getStrategy(session.getAiStrategyName()).makeMove(session);

        checkAndWakeUpIfDeckEmpty(session);

        return gameSessionRepository.save(session);
    }

    public GameSession drawCardFromDeck(Long sessionId, String login) {
        Long userId = getUserIdByLogin(login);
        GameSession session = getSessionByIdAndPlayer(sessionId, userId);

        if (session.isGameOver()) {
            throw new GameLogicException("The game is over. You can't draw cards.");
        }

        if (session.getLastDrawRound() == session.getRoundNumber()) {
            throw new GameLogicException("You have already drawn a card from the deck this round. Now you must either swap the drawn card with one of your cards or skip the swap!");
        }

        if (session.getDeck().isEmpty()) {
            checkAndWakeUpIfDeckEmpty(session);
            return session;
        }

        Integer drawnCard = session.getDeck().remove(0);

        // 👉 Oceniamy ruch i zapisujemy w player_moves
        evaluateAndSavePlayerMove(session, drawnCard);

        session.getDiscardPile().add(drawnCard);
        session.setLastDrawRound(session.getRoundNumber());

        checkAndWakeUpIfDeckEmpty(session);

        return gameSessionRepository.save(session);
    }

    public WakeUpResponseDto wakeUp(Long sessionId, String login) {
        Long userId = getUserIdByLogin(login);
        GameSession session = getSessionByIdAndPlayer(sessionId, userId);

        if (session.isGameOver()) {
            throw new GameLogicException("The game is already over.");
        }

        int playerPoints = sumPoints(session.getPlayerCards());
        int aiPoints = sumPoints(session.getAiCards());
        int currentGameRound = session.getGameRound();
        int moves = session.getRoundNumber();

        GameRoundResult result = new GameRoundResult();
        result.setGameSessionId(sessionId);
        result.setRoundNumber(currentGameRound);
        result.setPlayerPoints(playerPoints);
        result.setAiPoints(aiPoints);
        roundResultRepository.save(result);

        List<GameRoundResult> allRounds = roundResultRepository.findByGameSessionIdOrderByRoundNumber(sessionId);

        int totalPlayerPoints = allRounds.stream()
                .mapToInt(GameRoundResult::getPlayerPoints)
                .sum();

        int totalAiPoints = allRounds.stream()
                .mapToInt(GameRoundResult::getAiPoints)
                .sum();

        if (totalPlayerPoints > 100 || totalAiPoints > 100) {
            session.setGameOver(true);
            gameSessionRepository.save(session);

            return WakeUpResponseDto.builder()
                    .gameRound(currentGameRound)
                    .movesInRound(moves)
                    .playerPoints(playerPoints)
                    .aiPoints(aiPoints)
                    .build();
        }

        List<Integer> newDeck = createShuffledDeck();
        List<Integer> newPlayer = drawCards(newDeck, 4);
        List<Integer> newAi = drawCards(newDeck, 4);
        List<Integer> newDiscard = List.of(newDeck.remove(0));

        session.setDeck(newDeck);
        session.setPlayerCards(newPlayer);
        session.setAiCards(newAi);
        session.setDiscardPile(new ArrayList<>(newDiscard));

        session.setGameRound(currentGameRound + 1);
        session.setRoundNumber(1);
        session.setLastDrawRound(0);
        session.setLastActionRound(0);

        String message = "First card value: " + newPlayer.get(0) + " and second card value: " + newPlayer.get(1);
        session.setLastActionMessage(message);

        gameSessionRepository.save(session);

        return WakeUpResponseDto.builder()
                .gameRound(currentGameRound)
                .movesInRound(moves)
                .playerPoints(playerPoints)
                .aiPoints(aiPoints)
                .build();
    }

    public List<RoundHistoryDto> getRoundHistory(Long sessionId, String login) {
        Long userId = getUserIdByLogin(login);
        GameSession session = getSessionByIdAndPlayer(sessionId, userId);

        List<GameRoundResult> results = roundResultRepository.findByGameSessionIdOrderByRoundNumber(session.getId());

        return results.stream()
                .map(result -> RoundHistoryDto.builder()
                        .roundNumber(result.getRoundNumber())
                        .playerPoints(result.getPlayerPoints())
                        .aiPoints(result.getAiPoints())
                        .build())
                .toList();
    }

    private void evaluateAndSavePlayerMove(GameSession session, Integer drawnCard) {
        List<Integer> playerCards = session.getPlayerCards();
        Integer weakestCard = playerCards.stream()
                .max(Integer::compareTo)
                .orElseThrow(() -> new GameLogicException("Empty hand."));

        Integer discardTop = session.getDiscardPile() != null && !session.getDiscardPile().isEmpty()
                ? session.getDiscardPile().get(session.getDiscardPile().size() - 1)
                : null;

        double averageCardValue = playerCards.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        Integer differenceWeakestToDiscardTop = (discardTop != null) ? (weakestCard - discardTop) : null;

        boolean goodDecision = drawnCard < weakestCard
                && (discardTop == null || drawnCard <= discardTop);

        PlayerMove move = new PlayerMove();
        move.setSessionId(session.getId());
        move.setRoundNumber(session.getRoundNumber());
        move.setWeakestCard(weakestCard);
        move.setDiscardTop(discardTop);
        move.setAverageCardValue(averageCardValue);
        move.setDifferenceWeakestToDiscardTop(differenceWeakestToDiscardTop);
        move.setGoodDecision(goodDecision);
        move.setCreatedAt(LocalDateTime.now());

        playerMoveRepository.save(move);
    }


    private void checkAndWakeUpIfDeckEmpty(GameSession session) {
        if (session.getDeck().isEmpty()) {
            wakeUp(session.getId(), userRepository.findById(session.getPlayerId())
                    .orElseThrow(() -> new GameLogicException("User not found."))
                    .getLogin());
        }
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

    private Long getUserIdByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new GameLogicException("User not found."))
                .getId();
    }

    private GameSession getSessionByIdAndPlayer(Long sessionId, Long userId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GameLogicException("Game session not found."));

        if (!session.getPlayerId().equals(userId)) {
            throw new GameLogicException("Access denied to this game session.");
        }

        return session;
    }

    private int sumPoints(List<Integer> cards) {
        return cards.stream().mapToInt(Integer::intValue).sum();
    }
}
