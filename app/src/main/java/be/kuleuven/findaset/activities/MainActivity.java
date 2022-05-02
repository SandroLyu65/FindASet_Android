package be.kuleuven.findaset.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import be.kuleuven.findaset.R;
import be.kuleuven.findaset.model.FindASet;
import be.kuleuven.findaset.model.TestableFindASet;
import be.kuleuven.findaset.model.card.AbstractCard;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TestableFindASet gameModel;
    private ImageView[] cardImages;
    private TextView[] cardTexts;
    private TextView testTxt;
    private final Integer[] cardPicturesIds = {
            R.drawable.ovaal1groen, R.drawable.ovaal2groen, R.drawable.ovaal3groen,
            R.drawable.ovaal1rood, R.drawable.ovaal2rood, R.drawable.ovaal3rood,
            R.drawable.ovaal1paars, R.drawable.ovaal2paars, R.drawable.ovaal3paars,
            R.drawable.ruit1groen, R.drawable.ruit2groen, R.drawable.ruit3groen,
            R.drawable.ruit1rood, R.drawable.ruit2rood, R.drawable.ruit3rood,
            R.drawable.ruit1paars, R.drawable.ruit2paars, R.drawable.ruit3paars,
            R.drawable.tilde1groen, R.drawable.tilde2groen, R.drawable.tilde3groen,
            R.drawable.tilde1rood, R.drawable.tilde2rood, R.drawable.tilde3rood,
            R.drawable.tilde1paars, R.drawable.tilde2paars, R.drawable.tilde3paars};
    private ArrayList<Integer> selectedCardsIndex;

    /**
     * Firstly bound all fields with UI components.
     *
     * Then bound gameModel with new FindASet()
     * to use the method in FindASet.
     *
     * @param savedInstanceState saved content
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testTxt = findViewById(R.id.testTxt);
        selectedCardsIndex = new ArrayList<>(3);

        cardImages = new ImageView[12];
        cardImages[0] = findViewById(R.id.card1);
        cardImages[1] = findViewById(R.id.card2);
        cardImages[2] = findViewById(R.id.card3);
        cardImages[3] = findViewById(R.id.card4);
        cardImages[4] = findViewById(R.id.card5);
        cardImages[5] = findViewById(R.id.card6);
        cardImages[6] = findViewById(R.id.card7);
        cardImages[7] = findViewById(R.id.card8);
        cardImages[8] = findViewById(R.id.card9);
        cardImages[9] = findViewById(R.id.card10);
        cardImages[10] = findViewById(R.id.card11);
        cardImages[11] = findViewById(R.id.card12);

        cardTexts = new TextView[12];
        cardTexts[0] = findViewById(R.id.card1Text);
        cardTexts[1] = findViewById(R.id.card2Text);
        cardTexts[2] = findViewById(R.id.card3Text);
        cardTexts[3] = findViewById(R.id.card4Text);
        cardTexts[4] = findViewById(R.id.card5Text);
        cardTexts[5] = findViewById(R.id.card6Text);
        cardTexts[6] = findViewById(R.id.card7Text);
        cardTexts[7] = findViewById(R.id.card8Text);
        cardTexts[8] = findViewById(R.id.card9Text);
        cardTexts[9] = findViewById(R.id.card10Text);
        cardTexts[10] = findViewById(R.id.card11Text);
        cardTexts[11] = findViewById(R.id.card12Text);

        for(int i = 0; i < 12; i++) {
            cardImages[i].setOnClickListener(this);
        }

        TestableFindASet findASet = new FindASet();
        setGameModel(findASet);
        gameModel.setTable(null);
    }

    /**
     * First get the index of which call the method.
     *
     * If there are already 3 selected cards, remove the first.
     *
     * After that, call checkSet() to check if there is set.
     *
     * If there is, call updateTable().
     */
    @Override
    public void onClick(View view) {
        int index = Arrays.asList(cardImages).indexOf(view);
        if (gameModel.getCard(index).isSelected()) {
            selectedCardsIndex.remove(selectedCardsIndex.indexOf(index));
            gameModel.toggle(index);
        }
        else {
            if (selectedCardsIndex.size() == 3) {
                gameModel.toggle(selectedCardsIndex.get(0));
                selectedCardsIndex.remove(0);
            }
            selectedCardsIndex.add(index);
            gameModel.toggle(index);
            if (selectedCardsIndex.size() == 3) {
                if(checkSet()) {
                    testTxt.setText("set Found");
                    gameModel.updateTable(selectedCardsIndex);
                    selectedCardsIndex.clear();
                }
            }
        }
    }

    public void refreshBtn_Clicked(View caller){
        for (int i = 0; i < selectedCardsIndex.size(); i++) {
            gameModel.toggle(selectedCardsIndex.get(i));
        }
        selectedCardsIndex.clear();
        gameModel.emptyTable();
        gameModel.setTable(null);
    }

    public void setGameModel(TestableFindASet newGameModel) {
        this.gameModel = newGameModel;
        this.gameModel.setUI(this);
    }

    /**
     * Display images of all cards.
     */
    public void notifyNewGame() {
        for(int i = 0; i < cardImages.length; i++){
            notifyCard(i);
        }

        //JUST for TEST
        String str = "SET cards position: "
                + (gameModel.getJustForTest()[0]+1) + " "
                + (gameModel.getJustForTest()[1]+1) + " "
                + (gameModel.getJustForTest()[2]+1) + " ";
        testTxt.setText(str);
    }

    /**
     * Display images of one card accroding to index.
     */
    public void notifyCard(int index) {
        AbstractCard nextCard = gameModel.getCard(index);
        cardImages[index].setImageBitmap(combineImageIntoOne(setBitmaps(nextCard)));
        cardTexts[index].setText(nextCard.toString());
    }

    /**
     * If there is no green border, add one.
     * If there is already one green border, delete it.
     */
    public void notifyToggle(int index) {
        if(gameModel.getCard(index).isSelected())
            cardImages[index].setBackground(getDrawable(R.drawable.selected_card));
        else
            cardImages[index].setBackground(null);
    }

    /**
     * Store features of all cards into 4*3 featureMatrix.
     * For every column, check if is all same numbers or the sum equals to 6.
     *
     * @return If there is set, true will be returned.
     */
    private boolean checkSet() {
        int[][] featureMatrix = new int[3][4];
        for (int i = 0; i < 3; i++) {
            AbstractCard next = gameModel.getCard(selectedCardsIndex.get(i));
            featureMatrix[i][0] = next.getShapeCountInt();
            featureMatrix[i][1] = next.getShadingInt();
            featureMatrix[i][2] = next.getColorInt();
            featureMatrix[i][3] = next.getTypeInt();
        }
        boolean isSet = true;
        for (int col = 0; col < 4; col++) {
            if (featureMatrix[0][col] == featureMatrix[1][col]) {
                if (featureMatrix[1][col] != featureMatrix[2][col]) {
                    isSet = false;
                    break;
                }
            } else if (featureMatrix[0][col] + featureMatrix[1][col] + featureMatrix[2][col] != 6) {
                isSet = false;
                break;
            }
        }
        return isSet;
    }

    /**
     * cardPicturesIds[] is an array that stores all Ids of basic components drawables.
     *
     * According to the feature IDs, the index of basic component in the array can be
     * derived.
     *
     * @param card Object in AbstractCard class.
     *
     * @return An arraylist with the size equals to ShapeCountInt of card,
     *          which stores basic components.
     */
    private ArrayList<Bitmap> setBitmaps(AbstractCard card) {
        int color = card.getColorInt() - 1;
        int shading = card.getShadingInt() - 1;
        int shape = card.getTypeInt() - 1;
        int index = shape * 9 + color * 3 + shading;
        Bitmap test = BitmapFactory.decodeResource(getResources(), cardPicturesIds[index]);
        ArrayList<Bitmap> newBitMap = new ArrayList<>();
        for (int i = 0; i < card.getShapeCountInt(); i++) {
            newBitMap.add(test);
        }
        return newBitMap;
    }

    /**
     * Combine basic components to final image of each card according to the ShapeCountInt.
     *
     * Set the gap between components to make sure the width of new Bitmap equals to
     * 4.5 times of width of basic components, which is fixed no matter how many components.
     *
     * @param bitmap the basic component of
     *
     * @return a new Bitmap contained several components
     */
    private Bitmap combineImageIntoOne(ArrayList<Bitmap> bitmap) {
        int width = bitmap.get(0).getWidth();
        int height = bitmap.get(0).getHeight();
        int size = bitmap.size();

        Bitmap temp = Bitmap.createBitmap((int) (4.5 * width), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        float left = 0;
        if (size == 3) {
            for (int i = 0; i < size; i++) {
                left = (i == 0 ? 0.25f * width : left + 1.5f * width);
                canvas.drawBitmap(bitmap.get(i), left, 0f, null);
            }
        } else if (size == 2) {
            for (int i = 0; i < size; i++) {
                left = (i == 0 ? 0.5f * width : left + 2.5f * width);
                canvas.drawBitmap(bitmap.get(i), left, 0f, null);
            }
        } else {
            left = 1.75f * width;
            canvas.drawBitmap(bitmap.get(0), left, 0f, null);
        }

        return temp;
    }
}