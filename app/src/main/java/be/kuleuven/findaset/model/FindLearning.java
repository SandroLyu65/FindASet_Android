package be.kuleuven.findaset.model;

import java.util.ArrayList;

public class FindLearning extends AbstractFindASet implements InterfaceFindASet{
    public FindLearning() {
    }

    @Override
    public void startNewGame() {
        generateAllCardsIdsList();

        // init class fields
        this.win = false;
        this.hints = 0;
        this.selectedCardsIndex = new ArrayList<>(3);
        this.foundedSetCardsIds = new ArrayList<>();

        this.isCardSelected = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            this.isCardSelected.add(false);
        }
        this.firstSetOnPage = new ArrayList<>(3);
        for (int i = 0; i < 12; i++) {
            this.firstSetOnPage.add(9999);
        }

        initializeTable(12);
        setCardsTable(12);

        mainActivity.notifyNewGame(12);
        mainActivity.notifyFeatureBoxGrey();
        generateAllCardsIdsList();
    }


    @Override
    public void checkWin() {
        // check if there's still a set after updating
        if (foundedSetCardsIds.size() < 69) {
            updateWholeTable(checkAllSetOnPage());
        }
        else if (foundedSetCardsIds.size() == 69) {
            if (!checkAllSetOnPage()) {
                win = true;
            }
        }
        // the player ha already found 24 sets or more
        // so they win the find All game
        else if (foundedSetCardsIds.size() == 72) {
            // No need to display to display 12 cards
            for (int j = 9; j < 12; j++) {
                mainActivity.notifyUnavailable(j);
            }
            // display last 9 cards
            initializeTable(9);
            setCardsTable(9);
            mainActivity.notifyNewGame(9);
            // If there's no set remaining in the 9 cards the player wins the game
            if (!checkAllSetOnPage()) {
                win = true;
            }
        }
        else if (foundedSetCardsIds.size() == 75) {
            // No need to display to display 12 cards, just 6
            for (int k = 6; k < 9; k++) {
                mainActivity.notifyUnavailable(k);
            }
            initializeTable(6);
            setCardsTable(6);
            mainActivity.notifyNewGame(6);
            // If there's no set remaining in the 6 cards the player wins the game
            if (!checkAllSetOnPage()) {
                win = true;
            }
        }
    }

    @Override
    public boolean checkFeatureMatrix(int[][] featureMatrix, boolean isSet) {
        for (int col = 0; col < 4; col++) {
            // Compares the features of the first of the first two cards
            // if the first two are the same -> check the third one
            if (featureMatrix[0][col] == featureMatrix[1][col]) {
                // if the third is different = no set
                if (featureMatrix[1][col] != featureMatrix[2][col])
                    isSet = false;
                else
                    mainActivity.notifyFeatureSame(col);
            }
            // if the first two are different check if the third one is also different
            // if there's a set it will be the combination of features 1 2 and 3 in different cards
            // the sum in a set should always be 6
            else {
                if (featureMatrix[0][col] + featureMatrix[1][col] + featureMatrix[2][col] != 6)
                    isSet = false;
                else
                    mainActivity.notifyFeatureDifferent(col);
            }
        }
        return isSet;
    }

    @Override
    public void toggle(int index) {
        boolean isSelected = isCardSelected.get(index);
        if (!isSelected) {
            select(index);
            selectedCardsIndex.add(index);
            mainActivity.notifyFeatureBoxGrey();
            if (selectedCardsIndex.size() == 4) {
                unselect(selectedCardsIndex.get(0));
                selectedCardsIndex.remove(selectedCardsIndex.get(0));
                mainActivity.notifyFeatureBoxGrey();
            }
            if (selectedCardsIndex.size() == 3) {
                if (checkSet(selectedCardsIndex)) {
                    mainActivity.notifyDisableHint();
                    mainActivity.notifyLearningModeFindASet();
                }
            }
            else if (selectedCardsIndex.size() == 2) {
                checkFeatureMatrixForTwo(generateFeatureMatrixForTwo());
            }
        } else {
            unselect(index);
            selectedCardsIndex.remove((Integer) index);
            mainActivity.notifyFeatureBoxGrey();
            if (selectedCardsIndex.size() == 2) {
                checkFeatureMatrixForTwo(generateFeatureMatrixForTwo());
            }
        }
    }

    private int[][] generateFeatureMatrixForTwo() {
        int[][] featureMatrix = new int[2][4];
        for (int i = 0; i < 2; i++) {
            int nextCardId = cardsIdTable.get(getSelectedCardsIndex().get(i));
            int size = nextCardId/1000;
            int color = (nextCardId%1000)/100;
            int shading = (nextCardId%100)/10;
            int type = nextCardId%10;
            featureMatrix[i][0] = size;
            featureMatrix[i][1] = color;
            featureMatrix[i][2] = shading;
            featureMatrix[i][3] = type;
        }
        return featureMatrix;
    }

    public void checkFeatureMatrixForTwo(int[][] featureMatrix) {
        for (int col = 0; col < 4; col++) {
            if (featureMatrix[0][col] == featureMatrix[1][col]) {
                mainActivity.notifyFeatureSame(col);
            }
            else {
                mainActivity.notifyFeatureDifferent(col);
            }
        }
    }
}
