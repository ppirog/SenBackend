package com.sen.senbackend.ai;

import com.sen.senbackend.ai.embedded.EmbeddedSwapStrategy;
import com.sen.senbackend.ai.ml.MachineLearningSwapStrategy;
import com.sen.senbackend.ai.random.RandomSwapStrategy;
import com.sen.senbackend.gamelogic.exception.GameLogicException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AiStrategyManager {

    private final RandomSwapStrategy randomSwapStrategy;
    private final EmbeddedSwapStrategy embeddedSwapStrategy;
    private final MachineLearningSwapStrategy machineLearningSwapStrategy;

    public AiStrategy getStrategy(String strategyName) {
        return switch (strategyName.toLowerCase()) {
            case "random" -> randomSwapStrategy;
            case "embedded" -> embeddedSwapStrategy;
            case "ml" -> machineLearningSwapStrategy;
            default -> throw new GameLogicException("Unknown AI strategy: " + strategyName);
        };
    }
}
