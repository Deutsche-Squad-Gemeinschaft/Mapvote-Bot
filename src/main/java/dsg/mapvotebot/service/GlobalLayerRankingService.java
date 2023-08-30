package dsg.mapvotebot.service;

import dsg.mapvotebot.db.entities.GlobalLayerRanking;
import dsg.mapvotebot.db.repositories.GlobalLayerRankingRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contains all methods which interfere with the elo ranking system.
 */
@Setter
@Getter
@Service
@RequiredArgsConstructor
public class GlobalLayerRankingService {
    private final GlobalLayerRankingRepository globalLayerRankingRepository;

    /**
     * Updates layer elo and details in database based on the amount of votes the layers got in a mapvote.
     * Sequence of evaluation: Layer1 vs Layer2, layer1 vs Layer3, Layer2 vs Layer3
     * The elo system used resembles the chess elo system.
     *
     * @param firstLayer Name of first layer that was votable.
     * @param firstLayerVotes Amount of votes the first layer got.
     * @param secondLayer Name of second layer that was votable.
     * @param secondLayerVotes Amount of votes the second layer got.
     * @param thirdLayer Name of third layer that was votable.
     * @param thirdLayerVotes Amount of votes the third layer got.
     */
    public void evaluateMapvoteRanking(String firstLayer, int firstLayerVotes, String secondLayer, int secondLayerVotes, String thirdLayer, int thirdLayerVotes) {

        GlobalLayerRanking layer1 = globalLayerRankingRepository.findByLayer(firstLayer);
        GlobalLayerRanking layer2 = globalLayerRankingRepository.findByLayer(secondLayer);
        GlobalLayerRanking layer3 = globalLayerRankingRepository.findByLayer(thirdLayer);
        layer1.setAppearance(layer1.getAppearance() + 1);
        layer2.setAppearance(layer2.getAppearance() + 1);
        layer3.setAppearance(layer3.getAppearance() + 1);

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

    /**
     * Calculates the new elo for the provided first layer based on expected competition outcome and real outcome.
     *
     * @param layer1 First layer that competes against the second layer.
     * @param layer2 Second layer that competes against the first layer.
     * @param layerOneVotes Amount of votes the first layer got in the mapvote.
     * @param layerTwoVotes Amount of votes the second layer got in the mapvote.
     * @return New amount of elo of first layer.
     */
    private double calculateNewElo(GlobalLayerRanking layer1, GlobalLayerRanking layer2, int layerOneVotes, int layerTwoVotes) {

        double realValue;

        if (layerOneVotes > layerTwoVotes) {
            realValue = 1;
        } else if (layerOneVotes < layerTwoVotes) {
            realValue = 0;
        } else {
            realValue = 0.5;
        }

        double expectedValue = calculateExpectedValue(layer1, layer2);
        System.out.println("Erwartungswert: " + expectedValue);

        if (expectedValue > 0.5 && realValue == 1) {
            layer1.setNumberOfCorrectPredictions(layer1.getNumberOfCorrectPredictions() + 1);
        } else if (expectedValue < 0.5 && realValue == 0) {
            layer1.setNumberOfCorrectPredictions(layer1.getNumberOfCorrectPredictions() + 1);
        } else if (expectedValue == 0.5 && realValue == 0.5) {
            layer1.setNumberOfCorrectPredictions(layer1.getNumberOfCorrectPredictions() + 1);
        } else {
            layer1.setNumberOfFalsePredictions(layer1.getNumberOfFalsePredictions() + 1);
        }

        layer1.setLevelOfDevelopment(selectLevelOfDevelopment(layer1.getElo(), layer1.getAppearance(), layer1.getLevelOfDevelopment()));
        layer1.setReliability(calculateReliability(layer1.getNumberOfCorrectPredictions(), layer1.getNumberOfFalsePredictions()));

        return layer1.getElo() + layer1.getLevelOfDevelopment() * (realValue - expectedValue);
    }

    /**
     * Calculates the probability that based on current elo the first layer gets more votes than the second layer and therefore wins the competition.
     *
     * @param layer1 First layer that competes against the second layer.
     * @param layer2 Second layer that competes against the first layer.
     * @return Expected probability in percent that the first layer wins the competition against the second layer.
     */
    private double calculateExpectedValue(GlobalLayerRanking layer1, GlobalLayerRanking layer2) {
        double exponent = (layer2.getElo() - (layer1.getElo())) / 400;
        double denominator = Math.pow(10, exponent);
        double denominatorWithOne = 1 + denominator;
        double finalResult = 1 / denominatorWithOne;
        return finalResult;
    }

    /**
     * Calculates the reliability that the layers rank is correct based on the number of correct and false predictions the skill system made.
     *
     * @param truePredictions Number of true predictions
     * @param falsePredictions Number of false predictions
     * @return Probability in percent
     */
    private double calculateReliability(int truePredictions, int falsePredictions) {
        return Math.round(((double) truePredictions) / ((double) (truePredictions + falsePredictions)) * 100 * 100.00) / 100.00;
    }

    /**
     * Selects the fitting level of development based on the elo a layer has and the number of appearances from the past.
     *
     * @param elo Current elo a layer has.
     * @param appearance Number of appearances a layer had in the past.
     * @param currentLOD Current "K-Factor" the layer has. In the following named as level of development.
     * @return new level of development
     */
    private int selectLevelOfDevelopment(double elo, int appearance, int currentLOD) {

        if (currentLOD == 10) {
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

    /**
     * Provides three layers which will be votable in the mapvote for the first live map.
     * Layers with an appearance lower than 7 will be selected first, so new layers after an update will get the chance to be played more often.
     * The same layer and layers with the same maps will not be added in the list for more variety.
     *
     * @return A list with three layers which will be votable in the mapvote
     */
    public List<GlobalLayerRanking> selectFirstLiveMaps() {
        List<GlobalLayerRanking> firstLiveMaps = new ArrayList<>();
        List<GlobalLayerRanking> possibleLayer = globalLayerRankingRepository.findAllByFirstLiveMapIsTrueAndPlayableIsTrue();
        List<GlobalLayerRanking> lowAppearanceLayer = extractLayerWithLowAppearance(possibleLayer);

        int min = 1;
        boolean mapsSelected = false;
        int run = 1;
        GlobalLayerRanking randomMap;
        List<GlobalLayerRanking> decimatedPossibleLayer = decimatePossibleLayerByAverageElo(possibleLayer);

        while (!mapsSelected) {
            boolean alreadyInList = false;

            //make sure that layer with low appearance get picked. Otherwise, pick layer based on elo. (This ensures that new layer will get the opportunity to be played more after update)
            if (run >= lowAppearanceLayer.size()) {
                int max = decimatedPossibleLayer.size();
                int range = max - min + 1;
                int rand = (int) (Math.random() * range) + min;
                randomMap = decimatedPossibleLayer.get(rand - 1);
            } else {
                int max = lowAppearanceLayer.size();
                int range = max - min + 1;
                int rand = (int) (Math.random() * range) + min;
                randomMap = lowAppearanceLayer.get(rand - 1);
            }

            //check if map of layer is already in list or layer is already is in list to prevent duplication and provide more variety
            if (!firstLiveMaps.contains(randomMap)) {
                for (GlobalLayerRanking firstLiveMap : firstLiveMaps) {
                    if (firstLiveMap.getMap().equals(randomMap.getMap())) {
                        alreadyInList = true;
                        break;
                    }
                }
                if (!alreadyInList) {
                    firstLiveMaps.add(randomMap);
                }
            }

            //new possibility to pick from layer above or below average elo, so it cant get stuck  (e.g. all layers above average elo cant be picked because of same map)
            switch (firstLiveMaps.size()) {
                case 1, 2 -> decimatedPossibleLayer = decimatePossibleLayerByAverageElo(possibleLayer);
                case 3 -> mapsSelected = true;
            }
            run++;
        }
        return firstLiveMaps;
    }



    /**
     * Decimates a list of layers based on the average elo the layers have and the probability that all layers below or higher than average get eliminated from list.
     * It is possible to call the method multiple times to decimate layers even more.
     *
     * @param possibleLayer List with all layers that fit the requirements.
     * @return Decimated list with all remaining layers.
     */
    private List<GlobalLayerRanking> decimatePossibleLayerByAverageElo(List<GlobalLayerRanking> possibleLayer){
        double averageElo = calculateAverageElo(possibleLayer);
        boolean pickLayerAboveAverageElo = calculateProbability(80);

        List<GlobalLayerRanking> layerList = new ArrayList<>();
        for (GlobalLayerRanking layer : possibleLayer) {
            if (pickLayerAboveAverageElo){
                if (layer.getElo() >= averageElo){
                    layerList.add(layer);
                }
            } else {
                if (layer.getElo() <= averageElo){
                    layerList.add(layer);
                }
            }
        }
        return layerList;
    }

    /**
     * Draws lots for a specific cases occurrence.
     *
     * @param probabilityForCaseOne Probability in percent that a specific case should occur.
     * @return True if the case occurred.
     */
    private boolean calculateProbability(int probabilityForCaseOne) {
        int max = 100;
        int min = 1;
        int range = max - min + 1;
        int rand = (int) (Math.random() * range) + min;

        if (rand <= probabilityForCaseOne) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculates the average elo of multiple layer from a list.
     *
     * @param possibleLayer List with all layers that fit the requirements.
     * @return Average elo the layer have.
     */
    private double calculateAverageElo(List<GlobalLayerRanking> possibleLayer) {
        int possibleLayerAmount = possibleLayer.size();
        double totalElo = 0;
        for (GlobalLayerRanking layer : possibleLayer) {
            totalElo = totalElo + layer.getElo();
        }

        return totalElo / possibleLayerAmount;
    }

    /**
     * Extracts all layers from a list which have an appearance lower or equal 7.
     *
     * @param possibleLayer List with all layers that fit the requirements.
     * @return List with all layers that have an appearance lower or equal than 7.
     */
    private List<GlobalLayerRanking> extractLayerWithLowAppearance(List<GlobalLayerRanking> possibleLayer) {
        Set<GlobalLayerRanking> layerSet = new HashSet<>(possibleLayer);
        List<GlobalLayerRanking> layerList = new ArrayList<>();
        for (GlobalLayerRanking layer : layerSet) {
            //TODO Property: Appearance of Layer which will be picked for Mapvote with a much higher chance
            if (layer.getAppearance() <= 7) {
                layerList.add(layer);
            }
        }
        return layerList;
    }
}
