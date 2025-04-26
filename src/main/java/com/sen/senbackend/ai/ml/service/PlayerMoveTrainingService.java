package com.sen.senbackend.ai.ml.service;

import com.sen.senbackend.ai.ml.model.PlayerMove;
import com.sen.senbackend.ai.ml.repository.PlayerMoveRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PlayerMoveTrainingService {

    private final PlayerMoveRepository playerMoveRepository;

    private J48 model;
    private Instances dataSetStructure;

    @PostConstruct
    public void initializeModel() {
        log.info("🌟 Training model on startup...");
        trainModel();
    }

    @Scheduled(fixedDelay = 300_000)
    public void retrainModel() {
        log.info("🔄 Retraining model...");
        trainModel();
    }

    public void trainModel() {
        List<PlayerMove> moves = playerMoveRepository.findAll();

        if (moves.isEmpty()) {
            log.warn("⚠️ No data to train.");
            return;
        }

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("roundNumber"));
        attributes.add(new Attribute("weakestCard"));
        attributes.add(new Attribute("discardTop"));
        attributes.add(new Attribute("averageCardValue"));
        attributes.add(new Attribute("differenceWeakestToDiscardTop"));

        List<String> classValues = List.of("false", "true");
        attributes.add(new Attribute("goodDecision", classValues));

        Instances dataset = new Instances("PlayerMoves", attributes, moves.size());
        dataset.setClassIndex(dataset.numAttributes() - 1);

        for (PlayerMove move : moves) {
            double[] vals = new double[dataset.numAttributes()];
            vals[0] = move.getRoundNumber() != null ? move.getRoundNumber() : 0;
            vals[1] = move.getWeakestCard() != null ? move.getWeakestCard() : 0;
            vals[2] = move.getDiscardTop() != null ? move.getDiscardTop() : 0;
            vals[3] = move.getAverageCardValue() != null ? move.getAverageCardValue() : 0.0;
            vals[4] = move.getDifferenceWeakestToDiscardTop() != null ? move.getDifferenceWeakestToDiscardTop() : 0;
            vals[5] = move.getGoodDecision() != null && move.getGoodDecision() ? classValues.indexOf("true") : classValues.indexOf("false");

            dataset.add(new DenseInstance(1.0, vals));
        }

        this.model = new J48();
        try {
            model.buildClassifier(dataset);
            this.dataSetStructure = dataset;
            log.info("✅ Model trained successfully on {} samples.", moves.size());
        } catch (Exception e) {
            log.error("❌ Error training model: ", e);
        }
    }

    public boolean predictMove(Integer roundNumber, Integer weakestCard, Integer discardTop, Double averageCardValue, Integer differenceWeakestToDiscardTop) {
        if (model == null || dataSetStructure == null) {
            throw new IllegalStateException("Model is not trained yet!");
        }

        try {
            Instance instance = new DenseInstance(dataSetStructure.numAttributes());
            instance.setDataset(dataSetStructure);

            instance.setValue(0, roundNumber != null ? roundNumber : 0);
            instance.setValue(1, weakestCard != null ? weakestCard : 0);
            instance.setValue(2, discardTop != null ? discardTop : 0);
            instance.setValue(3, averageCardValue != null ? averageCardValue : 0.0);
            instance.setValue(4, differenceWeakestToDiscardTop != null ? differenceWeakestToDiscardTop : 0);

            double prediction = model.classifyInstance(instance);
            String predictedClass = dataSetStructure.classAttribute().value((int) prediction);

            return "true".equals(predictedClass);
        } catch (Exception e) {
            log.error("❌ Error predicting move: ", e);
            throw new RuntimeException(e);
        }
    }
}
