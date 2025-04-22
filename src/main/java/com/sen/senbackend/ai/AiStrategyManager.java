package com.sen.senbackend.ai;

import com.sen.senbackend.gamelogic.exception.GameLogicException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AiStrategyManager {

    private final RandomSwapStrategy randomSwapStrategy;

    public AiStrategy getStrategy(String strategyName) {
        return switch (strategyName.toLowerCase()) {
            case "random" -> randomSwapStrategy;
            default -> throw new GameLogicException("Unknown AI strategy: " + strategyName);
        };
    }
}
