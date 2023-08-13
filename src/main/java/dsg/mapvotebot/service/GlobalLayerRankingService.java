package dsg.mapvotebot.service;

import dsg.mapvotebot.db.entities.GlobalLayerRanking;
import dsg.mapvotebot.db.repositories.GlobalLayerRankingRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Getter
@Service
@RequiredArgsConstructor
public class GlobalLayerRankingService {
    private final GlobalLayerRankingRepository globalLayerRankingRepository;

    public void evaluateMapvoteRanking(String firstLayer, int firstLayerVotes, String secondLayer, int secondLayerVotes, String thirdLayer, int thirdLayerVotes){

        GlobalLayerRanking layer1 = globalLayerRankingRepository.findByLayer(firstLayer);
        GlobalLayerRanking layer2 = globalLayerRankingRepository.findByLayer(secondLayer);
        GlobalLayerRanking layer3 = globalLayerRankingRepository.findByLayer(thirdLayer);
        layer1.setAppearance(layer1.getAppearance() +1);
        layer2.setAppearance(layer2.getAppearance() +1);
        layer3.setAppearance(layer3.getAppearance() +1);

        //firstLayer vs secondLayer
        double layer1Elo = calculateNewElo(layer1, layer2, firstLayerVotes, secondLayerVotes);
        double layer2Elo = calculateNewElo(layer2, layer1, secondLayerVotes, firstLayerVotes);
        layer1.setElo(layer1Elo);
        layer2.setElo(layer2Elo);
        System.out.println("Layer1 Elo: " + layer1.getElo());
        System.out.println("Layer2 Elo: " + layer2.getElo());

        //firstLayer vs thirdLayer
        double layer1Elo2 = calculateNewElo(layer1, layer3, firstLayerVotes, thirdLayerVotes);
        double layer3Elo = calculateNewElo(layer3, layer1, thirdLayerVotes, firstLayerVotes);
        layer1.setElo(layer1Elo2);
        layer3.setElo(layer3Elo);
        System.out.println("Layer1 Elo: " + layer1.getElo());
        System.out.println("Layer3 Elo: " + layer3.getElo());

        //secondLayer vs thirdLayer
        double layer2Elo2 = calculateNewElo(layer2, layer3, secondLayerVotes, thirdLayerVotes);
        double layer3Elo2 = calculateNewElo(layer3, layer2, thirdLayerVotes, secondLayerVotes);
        layer2.setElo(layer2Elo2);
        layer3.setElo(layer3Elo2);
        System.out.println("Layer2 Elo: " + layer2.getElo());
        System.out.println("Layer3 Elo: " + layer3.getElo());

        globalLayerRankingRepository.save(layer1);
        globalLayerRankingRepository.save(layer2);
        globalLayerRankingRepository.save(layer3);
    }

    private double calculateNewElo(GlobalLayerRanking layer1, GlobalLayerRanking layer2, int layerOneVotes, int layerTwoVotes){

        double realValue;

        if(layerOneVotes > layerTwoVotes){
            realValue = 1;
        } else if (layerOneVotes < layerTwoVotes) {
            realValue = 0;
        } else {
            realValue = 0.5;
        }

        double expectedValue = calculateExpectedValue(layer1, layer2);
        System.out.println("Erwartungswert: " + expectedValue);

        if (expectedValue > 0.5 && realValue == 1){
            layer1.setNumberOfCorrectPredictions(layer1.getNumberOfCorrectPredictions() +1);
        } else if (expectedValue < 0.5 && realValue == 0) {
            layer1.setNumberOfCorrectPredictions(layer1.getNumberOfCorrectPredictions() +1);
        } else if (expectedValue == 0.5 && realValue == 0.5) {
            layer1.setNumberOfCorrectPredictions(layer1.getNumberOfCorrectPredictions() +1);
        } else {
            layer1.setNumberOfFalsePredictions(layer1.getNumberOfFalsePredictions() +1);
        }

        layer1.setLevelOfDevelopment(selectLevelOfDevelopment(layer1.getElo(), layer1.getAppearance(), layer1.getLevelOfDevelopment()));
        layer1.setReliability(calculateReliability(layer1.getNumberOfCorrectPredictions(), layer1.getNumberOfFalsePredictions()));

        return layer1.getElo() + layer1.getLevelOfDevelopment() * (realValue - expectedValue);
    }

    private double calculateExpectedValue(GlobalLayerRanking layer1, GlobalLayerRanking layer2){
        double exponent = (layer2.getElo() - (layer1.getElo())) /400;
        double denominator = Math.pow(10, exponent);
        double denominatorWithOne = 1 + denominator;
        double finalResult = 1 / denominatorWithOne;
        return finalResult;
    }

    private double calculateReliability(int truePredictions, int falsePredictions){
        return Math.round(((double) truePredictions) / ((double) (truePredictions + falsePredictions)) * 100 * 100.00) / 100.00;
    }

    private int selectLevelOfDevelopment(double elo, int appearance, int currentLOD){

        if (currentLOD == 10){
            return 10;
        } else if (appearance < 30) {
            return 40;
        } else if (elo < 2400) {
            return 20;
        } else if (elo > 2400) {
            return 10;
        }
        return 20;
    }
}
